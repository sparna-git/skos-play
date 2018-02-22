package fr.sparna.assembly;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An assembly line, to assemble anything. This line needs to be constructed with :
 * <ul>
 *   <li>an <code>AssemblySource</code>, capable of iterating over the <code>Assembly</code> to send into the line</li>
 *   <li>a list of <code>AssemblyStation</code>, that will be applied in sequence to build up the <code>Assembly</code></li>
 *   <li>a list of <code>AssemblyConsumer</code>, that will consume the generated <code>Assembly</code>s</li>
 * </ul>
 * 
 * @author Thomas Francart
 *
 * @param <X>	The class of the objects to be processed.
 */
public class AssemblyLine<X> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected AssemblySource<X> source;
	protected List<AssemblyStation<X>> processors;
	protected List<AssemblyConsumer<X>> consumers;
	
	protected transient int processedCount = 0;

	public AssemblyLine(
			AssemblySource<X> source,
			List<AssemblyStation<X>> processors,
			List<AssemblyConsumer<X>> consumers
	) {
		super();
		this.source = source;
		this.processors = processors;
		this.consumers = consumers;
	}
	
	/**
	 * Convenience constructor with a single consumer.
	 * 
	 * @param source
	 * @param processors
	 * @param consumer
	 */
	public AssemblyLine(
			AssemblySource<X> source,
			List<AssemblyStation<X>> processors,
			AssemblyConsumer<X> consumer
			) {
		this(source, processors, Collections.singletonList(consumer));
	}

	/**
	 * Start indexing.
	 * 
	 * @throws LifecycleException
	 * @throws ConsumeException
	 */
	public void start() throws LifecycleException, ConsumeException {
		log.info("Indexing process started...");
	
		// reset counter
		this.processedCount = 0;
		
		// notify everyone of start
		this.init();

		try {
			try {
				while(this.source.hasNext()) {
					// get next id
					Assembly<X> iterable = this.source.next();
					log.debug("Processing "+iterable.getId()+"...");

					// iterate on processors to process the document
					process(iterable);

					// iterate on consumers to consume SolRDocument
					this.consume(iterable);

					log.debug("Done processing "+iterable.getId());
				}
				
				// commit consumers
				this.commit();
				
				// increment counter
				this.processedCount++;
				
			} catch (AssemblyException e) {
				// exception happened during processing, rollback consumers
				this.rollback();
				log.error("Indexing process failed and rollbacked, due to : "+e.getMessage());
				e.printStackTrace();
			}
		} finally {
			// notify everyone of stop
			this.destroy();
		}
		log.info("Indexing process stop");
	}



	public AssemblySource<X> getSource() {
		return source;
	}

	public void setSource(AssemblySource<X> source) {
		this.source = source;
	}

	public List<AssemblyStation<X>> getProcessors() {
		return processors;
	}

	public void setProcessors(List<AssemblyStation<X>> processors) {
		this.processors = processors;
	}

	public List<AssemblyConsumer<X>> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<AssemblyConsumer<X>> consumers) {
		this.consumers = consumers;
	}

	private void init() throws LifecycleException {
		log.debug("Initializing source "+this.source.getClass().getName());
		source.init(this);
		for (AssemblyStation<X> aProcessor : this.processors) {
			log.debug("Initializing processor "+aProcessor.getClass().getName());
			aProcessor.init(this);
		}
		for (AssemblyConsumer<X> aConsumer : this.consumers) {
			log.debug("Initializing consumer "+aConsumer.getClass().getName());
			aConsumer.init(this);
		}
	}

	private void destroy() throws LifecycleException {
		log.debug("Destroying source "+this.source.getClass().getName());
		source.destroy();
		for (AssemblyStation<X> aProcessor : this.processors) {
			log.debug("Destroying processor "+aProcessor.getClass().getName());
			aProcessor.destroy();
		}
		for (AssemblyConsumer<X> aConsumer : this.consumers) {
			log.debug("Destroying consumer "+aConsumer.getClass().getName());
			aConsumer.destroy();
		}
	}

	private void commit() throws ConsumeException {
		if(this.consumers != null) {
			for (AssemblyConsumer<X> aConsumer : this.consumers) {
				log.debug("Committing "+aConsumer.getClass().getName());
				aConsumer.commit();
			}
		}
	}

	private void rollback() throws ConsumeException {
		if(this.consumers != null) {
			for (AssemblyConsumer<X> aConsumer : this.consumers) {
				log.debug("Rolling back "+aConsumer.getClass().getName());
				aConsumer.rollback();
			}
		}
	}

	private void consume(Assembly<X> indexable) throws ConsumeException {
		if(this.consumers != null) {
			for (AssemblyConsumer<X> aConsumer : this.consumers) {
				aConsumer.consume(indexable);
			}
		}
	}

	private void process(Assembly<X> indexable) throws AssemblyException {
		if(this.processors != null) {
			for (AssemblyStation<X> aProcessor : this.processors) {
				aProcessor.process(indexable);
			}
		}
	}

	public int getProcessedCount() {
		return processedCount;
	}

}
