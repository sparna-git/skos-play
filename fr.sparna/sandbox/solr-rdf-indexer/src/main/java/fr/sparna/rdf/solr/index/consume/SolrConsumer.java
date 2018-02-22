package fr.sparna.rdf.solr.index.consume;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.AssemblyConsumer;
import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.ConsumeException;
import fr.sparna.assembly.LifecycleException;
import fr.sparna.assembly.base.BaseAssemblyLineComponent;

public class SolrConsumer extends BaseAssemblyLineComponent<SolrInputDocument> implements AssemblyConsumer<SolrInputDocument> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected final int BATCH_SIZE = 1000;

	protected SolrServer server;
	
	protected boolean clearIndexAtStartup = false;
	
	// current processing batch
	private List<SolrInputDocument> currentBatch = new ArrayList<SolrInputDocument>();

	
	
	public SolrConsumer(SolrServer server) {
		super();
		this.server = server;
	}

	@Override
	public void consume(Assembly<SolrInputDocument> doc) throws ConsumeException {
		// we're at the end of a batch, send to server
		if(this.currentBatch.size() >= BATCH_SIZE) {
			addToServer();
			this.currentBatch = new ArrayList<SolrInputDocument>();
		}

		currentBatch.add(doc.getDocument());
	}

	@Override
	public void commit() throws ConsumeException {
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
			throw new ConsumeException(e);
		} catch (IOException e) {
			throw new ConsumeException(e);
		}
	}

	@Override
	public void rollback() throws ConsumeException {
		try {
			log.debug("Rolling back SolR server");
			// problem, rollback
			server.rollback();
		} catch (SolrServerException e) {
			throw new ConsumeException(e);
		} catch (IOException e) {
			throw new ConsumeException(e);
		}
	}

	@Override
	public void destroy() throws LifecycleException {
		log.debug("Shutting down SolR server");
		// release allocated resources
		server.shutdown();
	}	

	@Override
	public void init(AssemblyLine<SolrInputDocument> assemblyLine) throws LifecycleException {
		super.init(assemblyLine);
		if(this.clearIndexAtStartup) {
			try {
				server.deleteByQuery("*:*");
			} catch (Exception e) {
				throw new LifecycleException(e);
			}
		}
	}

	private void addToServer() throws ConsumeException {
		log.debug("Adding "+this.currentBatch.size()+" documents to SolR");

		try {
			server.add(this.currentBatch);
		} catch (SolrServerException e) {
			throw new ConsumeException(e);
		} catch (IOException e) {
			throw new ConsumeException(e);
		}
	}

}
