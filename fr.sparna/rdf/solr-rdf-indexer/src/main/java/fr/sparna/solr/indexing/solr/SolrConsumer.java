package fr.sparna.solr.indexing.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.solr.indexing.ConsumingException;
import fr.sparna.solr.indexing.IndexingConsumerIfc;
import fr.sparna.solr.indexing.LifecycleException;
import fr.sparna.solr.indexing.base.BaseIndexingComponent;

public class SolrConsumer extends BaseIndexingComponent implements IndexingConsumerIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected final int BATCH_SIZE = 1000;

	protected SolrServer server;
	private List<SolrInputDocument> currentBatch = new ArrayList<SolrInputDocument>();

	public SolrConsumer(SolrServer server) {
		super();
		this.server = server;
	}

	@Override
	public void consume(SolrInputDocument doc) throws ConsumingException {
		// we're at the end of a batch, send to server
		if(this.currentBatch.size() >= BATCH_SIZE) {
			addToServer();
			this.currentBatch = new ArrayList<SolrInputDocument>();
		}

		currentBatch.add(doc);
	}

	@Override
	public void commit() throws ConsumingException {
		// end of processing notified, add remaining items if necessary
		if(this.currentBatch.size() > 0) {
			addToServer();
			this.currentBatch = new ArrayList<SolrInputDocument>();
		}

		try {
			log.debug("Committing SolR server");
			// at the end, commit
			server.commit();
		} catch (SolrServerException e) {
			throw new ConsumingException(e);
		} catch (IOException e) {
			throw new ConsumingException(e);
		}
	}

	@Override
	public void rollback() throws ConsumingException {
		try {
			log.debug("Rolling back SolR server");
			// problem, rollback
			server.rollback();
		} catch (SolrServerException e) {
			throw new ConsumingException(e);
		} catch (IOException e) {
			throw new ConsumingException(e);
		}
	}

	@Override
	public void destroy() throws LifecycleException {
		log.debug("Shutting down SolR server");
		// release allocated resources
		server.shutdown();
	}	

	private void addToServer() throws ConsumingException {
		log.debug("Adding "+this.currentBatch.size()+" documents to SolR");

		try {
			server.add(this.currentBatch);
		} catch (SolrServerException e) {
			throw new ConsumingException(e);
		} catch (IOException e) {
			throw new ConsumingException(e);
		}
	}

}
