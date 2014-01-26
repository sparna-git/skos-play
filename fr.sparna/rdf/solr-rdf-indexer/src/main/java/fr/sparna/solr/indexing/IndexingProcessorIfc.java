package fr.sparna.solr.indexing;

import org.apache.solr.common.SolrInputDocument;

public interface IndexingProcessorIfc<X> extends IndexingComponentIfc {

	public void process(X documentId, SolrInputDocument doc) throws ProcessingException;
	
}
