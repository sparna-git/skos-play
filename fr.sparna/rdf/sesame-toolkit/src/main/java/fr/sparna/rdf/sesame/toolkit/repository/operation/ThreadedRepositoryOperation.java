package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.util.concurrent.CountDownLatch;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.DefaultRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;

/**
 * Runs another RepositoryOperationIfc in a separate thread. This allows for example to load large quantity of data
 * at startup without impacting the startup time.
 * Usage exemple :
 * <code><pre>
 		InitializingRepositoryFactory factory = new InitializingRepositoryFactory(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_WITH_DIRECT_TYPE_AWARE));
		CountDownLatch latch = new CountDownLatch(1);
		ThreadedRepositoryOperation tro = new ThreadedRepositoryOperation(new LoadFromFileOrDirectory(args[0]), latch);
		factory.addOperation(tro);
		factory.createNewRepository();
 *	</pre></code>
 * 
 * @author Thomas Francart
 *
 */
public class ThreadedRepositoryOperation implements RepositoryOperationIfc, Runnable {

	protected Logger log = LoggerFactory.getLogger(ThreadedRepositoryOperation.class);

	protected RepositoryOperationIfc delegate;
	protected Repository repository;
	protected CountDownLatch latch;

	protected boolean finished = false;
	protected boolean sucessful = false;

	public ThreadedRepositoryOperation(RepositoryOperationIfc delegate, CountDownLatch latch) {
		super();
		this.delegate = delegate;
		this.latch = latch;
	}

	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		this.repository = repository;
		new Thread(this).start();
	}

	@Override
	public void run() {
		synchronized ( this ) {
			try {
				delegate.execute(repository);
				sucessful = true;
				log.trace("Threaded listener sucessfully initialized");
			} catch (RepositoryOperationException e) {
				sucessful = false;
				e.printStackTrace();
			} finally {
				finished = true;
				latch.countDown();
			}
		}
	}	

	public boolean isFinished() {
		return finished;
	}

	public boolean isSucessful() {
		return sucessful;
	}

	public static void main(String[] args) throws Exception {
		DefaultRepositoryFactory factory = new DefaultRepositoryFactory(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_WITH_DIRECT_TYPE_AWARE));
		CountDownLatch latch = new CountDownLatch(1);
		ThreadedRepositoryOperation tro = new ThreadedRepositoryOperation(new LoadFromFileOrDirectory(args[0]), latch);
		factory.addOperation(tro);
		factory.createNewRepository();
		
		try {
			// block until the worker has set the latch to 0:
			long start = System.currentTimeMillis();
			System.out.println("Waiting for repository loading...");
			latch.await();
			System.out.println("Loading finished in "+(System.currentTimeMillis() - start));
		} catch (InterruptedException ex){
			System.err.println(ex.toString());
			Thread.currentThread().interrupt();
		}
		
		if(tro.isSucessful()) {
			System.out.println("Loading was sucessful");
		} else {
			System.out.println("Loading was NOT sucessful");
		}	
	}

}
