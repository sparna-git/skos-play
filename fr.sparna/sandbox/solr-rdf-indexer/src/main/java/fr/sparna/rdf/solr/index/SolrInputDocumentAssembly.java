package fr.sparna.rdf.solr.index;

import org.apache.solr.common.SolrInputDocument;

import fr.sparna.assembly.Assembly;

public class SolrInputDocumentAssembly implements Assembly<SolrInputDocument> {

	protected String id;
	protected SolrInputDocument document;
	
	public SolrInputDocumentAssembly(String id, SolrInputDocument document) {
		super();
		this.id = id;
		this.document = document;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public SolrInputDocument getDocument() {
		return document;
	}	
	
}
