package fr.sparna.rdf.solr.index;

import org.apache.solr.common.SolrInputDocument;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.AssemblyException;
import fr.sparna.assembly.AssemblyStation;
import fr.sparna.assembly.base.BaseAssemblyLineComponent;

public class SolrMockAssemblyStation extends BaseAssemblyLineComponent<SolrInputDocument> implements AssemblyStation<SolrInputDocument> {

	protected String fieldName;
	
	public SolrMockAssemblyStation(String fieldName) {
		super();
		this.fieldName = fieldName;
	}

	@Override
	public void process(Assembly<SolrInputDocument> indexable) throws AssemblyException {
		indexable.getDocument().setField(this.fieldName, "This is a value coming from "+this.getClass().getName());
	}
	
}
