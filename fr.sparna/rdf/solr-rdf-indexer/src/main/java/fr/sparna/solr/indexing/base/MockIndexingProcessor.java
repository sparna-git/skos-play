package fr.sparna.solr.indexing.base;

import org.apache.solr.common.SolrInputDocument;

import fr.sparna.solr.indexing.IndexingProcessorIfc;
import fr.sparna.solr.indexing.ProcessingException;

public class MockIndexingProcessor<X> extends BaseIndexingComponent implements IndexingProcessorIfc<X> {

	protected String fieldName;
	
	public MockIndexingProcessor(String fieldName) {
		super();
		this.fieldName = fieldName;
	}

	@Override
	public void process(X documentId, SolrInputDocument doc) throws ProcessingException {
		doc.setField(this.fieldName, "This is a value coming from "+this.getClass().getName());
	}
	
}
