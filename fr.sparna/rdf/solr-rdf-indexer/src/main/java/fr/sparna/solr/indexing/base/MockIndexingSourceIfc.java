package fr.sparna.solr.indexing.base;

import java.util.Iterator;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import fr.sparna.solr.indexing.IndexingSourceIfc;

public class MockIndexingSourceIfc extends BaseIndexingComponent implements IndexingSourceIfc<String> {

	protected List<String> values;
	private Iterator<String> iterator;
	private String idField;
	
	
	public MockIndexingSourceIfc(List<String> values, String idField) {
		super();
		this.idField = idField;
		this.values = values;
		this.iterator = values.iterator();
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public String next() {
		return this.iterator.next();
	}

	@Override
	public SolrInputDocument createSolrDocument(String x) {
		SolrInputDocument d = new SolrInputDocument();
		d.setField(this.idField, x);
		return d;
	}

	
}
