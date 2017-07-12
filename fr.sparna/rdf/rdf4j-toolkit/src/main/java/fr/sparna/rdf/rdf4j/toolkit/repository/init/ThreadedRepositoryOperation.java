package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.repository.LocalMemoryRepositorySupplier;
import fr.sparna.rdf.rdf4j.toolkit.repository.LocalMemoryRepositorySupplier.FactoryConfiguration;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;

/**
 * Runs another RepositoryOperationIfc in a separate thread. This allows for example to load large quantity of data
 * at startup without impacting the startup time.
 * Usage exemple :
 * <code><pre>
 		RepositoryBuilder builder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_WITH_DIRECT_TYPE_AWARE));
		CountDownLatch latch = new CountDownLatch(1);
		ThreadedRepositoryOperation tro = new ThreadedRepositoryOperation(new LoadFromFileOrDirectory(args[0]), latch);
		factory.addOperation(tro);
		factory.createRepository();
 *	</pre></code>
 * 
 * @author Thomas Francart
 *
 */
public class ThreadedRepositoryOperation implements Consumer<RepositoryConnection>, Runnable {

	protected Logger log = LoggerFactory.getLogger(ThreadedRepositoryOperation.class);

	protected Consumer<RepositoryConnection> delegate;
	protected RepositoryConnection connection;
	protected CountDownLatch latch;

	protected boolean finished = false;
	protected boolean sucessful = false;

	public ThreadedRepositoryOperation(Consumer<RepositoryConnection> delegate, CountDownLatch latch) {
		super();
		this.delegate = delegate;
		this.latch = latch;
	}

	@Override
	public void accept(RepositoryConnection connection) {
		this.connection = connection;
		new Thread(this).start();
	}

	@Override
	public void run() {
		synchronized ( this ) {
			try {
				delegate.accept(this.connection);
				sucessful = true;
				log.trace("Threaded listener sucessfully initialized");
			} catch (Exception e) {
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
		RepositoryBuilder factory = new RepositoryBuilder(new LocalMemoryRepositorySupplier(FactoryConfiguration.RDFS_WITH_DIRECT_TYPE_AWARE));
		CountDownLatch latch = new CountDownLatch(1);
		ThreadedRepositoryOperation tro = new ThreadedRepositoryOperation(new LoadFromFileOrDirectory(args[0]), latch);
		factory.addOperation(tro);
		factory.get();
		
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
