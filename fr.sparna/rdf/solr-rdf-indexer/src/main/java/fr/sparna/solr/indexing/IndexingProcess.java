package fr.sparna.solr.indexing;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexingProcess<X> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected IndexingSourceIfc<X> source;
	protected List<IndexingProcessorIfc<X>> processors;
	protected List<IndexingConsumerIfc> consumers;

	public IndexingProcess(
			IndexingSourceIfc<X> source,
			List<IndexingProcessorIfc<X>> processors,
			List<IndexingConsumerIfc> consumers
			) {
		super();
		this.source = source;
		this.processors = processors;
		this.consumers = consumers;
	}

	public void start() throws LifecycleException, ConsumingException {
		log.info("Indexing process started...");
		
		// notify everyone of start
		this.init();

		try {
			try {
				while(this.source.hasNext()) {
					// get next id
					X x = this.source.next();
					log.debug("Processing "+x+"...");

					// init a SolR document with it
					SolrInputDocument d = this.source.createSolrDocument(x);

					// iterate on processors to process the document
					process(x, d);

					// iterate on consumers to consume SolRDocument
					this.consume(d);

					log.debug("Done processing "+x);
				}
				
				// commit consumers
				this.commit();
				
			} catch (ProcessingException e) {
				// exception happened during processing, rollback consumers
				this.rollback();
			}
		} finally {
			// notify everyone of stop
			this.destroy();
		}
		log.info("Indexing process stop");
	}



	public IndexingSourceIfc<X> getSource() {
		return source;
	}

	public void setSource(IndexingSourceIfc<X> source) {
		this.source = source;
	}

	public List<IndexingProcessorIfc<X>> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IndexingProcessorIfc<X>> processors) {
		this.processors = processors;
	}

	public List<IndexingConsumerIfc> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<IndexingConsumerIfc> consumers) {
		this.consumers = consumers;
	}

	private void init() throws LifecycleException {
		log.debug("Initializing source "+this.source.getClass().getName());
		source.init();
		for (IndexingProcessorIfc<X> aProcessor : this.processors) {
			log.debug("Initializing processor "+aProcessor.getClass().getName());
			aProcessor.init();
		}
		for (IndexingConsumerIfc aConsumer : this.consumers) {
			log.debug("Initializing consumer "+aConsumer.getClass().getName());
			aConsumer.init();
		}
	}

	private void destroy() throws LifecycleException {
		log.debug("Destroying source "+this.source.getClass().getName());
		source.destroy();
		for (IndexingProcessorIfc<X> aProcessor : this.processors) {
			log.debug("Destroying processor "+aProcessor.getClass().getName());
			aProcessor.destroy();
		}
		for (IndexingConsumerIfc aConsumer : this.consumers) {
			log.debug("Destroying consumer "+aConsumer.getClass().getName());
			aConsumer.destroy();
		}
	}

	private void commit() throws ConsumingException {
		if(this.consumers != null) {
			for (IndexingConsumerIfc aConsumer : this.consumers) {
				log.debug("Committing "+aConsumer.getClass().getName());
				aConsumer.commit();
			}
		}
	}

	private void rollback() throws ConsumingException {
		if(this.consumers != null) {
			for (IndexingConsumerIfc aConsumer : this.consumers) {
				log.debug("Rolling back "+aConsumer.getClass().getName());
				aConsumer.rollback();
			}
		}
	}

	private void consume(SolrInputDocument d) throws ConsumingException {
		if(this.consumers != null) {
			for (IndexingConsumerIfc aConsumer : this.consumers) {
				aConsumer.consume(d);
			}
		}
	}

	private void process(X x, SolrInputDocument d) throws ProcessingException {
		if(this.processors != null) {
			for (IndexingProcessorIfc<X> aProcessor : this.processors) {
				aProcessor.process(x, d);
			}
		}
	}

}
