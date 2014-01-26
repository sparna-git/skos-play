package fr.sparna.solr.indexing;

import org.apache.solr.common.SolrInputDocument;

public interface IndexingSourceIfc<X> extends IndexingComponentIfc {

	public boolean hasNext();
	
	public X next();
	
	public SolrInputDocument createSolrDocument(X x);
	
}
