package fr.sparna.solr.indexing;

import org.apache.solr.common.SolrInputDocument;

public interface IndexingConsumerIfc extends IndexingComponentIfc {

	public void consume(SolrInputDocument doc) throws ConsumingException;
	
	public void commit() throws ConsumingException;
	
	public void rollback() throws ConsumingException;
	
}
