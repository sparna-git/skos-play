package fr.sparna.rdf.solr.index;

import org.apache.solr.common.SolrInputDocument;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.base.AssemblyFactory;

public class SolrIndexableFactory implements AssemblyFactory<SolrInputDocument> {

	public static final String DEFAULT_URI_FIELD_NAME = "uri";
	
	protected String uriFieldName = DEFAULT_URI_FIELD_NAME;
	
	public SolrIndexableFactory(String uriFieldName) {
		super();
		this.uriFieldName = uriFieldName;
	}

	@Override
	public Assembly<SolrInputDocument> buildIndexable(String id) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField(this.uriFieldName, id);
		return new SolrInputDocumentAssembly(id, doc);
	}

	public String getUriFieldName() {
		return uriFieldName;
	}

	public void setUriFieldName(String uriFieldName) {
		this.uriFieldName = uriFieldName;
	}
	
}
