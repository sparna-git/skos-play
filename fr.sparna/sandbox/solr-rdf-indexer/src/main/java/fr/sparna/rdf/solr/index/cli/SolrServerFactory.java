package fr.sparna.rdf.solr.index.cli;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;

public class SolrServerFactory {

	protected String parameter;
	
	public SolrServerFactory(String parameter) {
		super();
		this.parameter = parameter;
	}

	public SolrServer createSolrServer() {
		if(parameter.startsWith("http://")) {
			return new HttpSolrServer(parameter);
		} else {
			System.setProperty("solr.solr.home", parameter);
			CoreContainer container = new CoreContainer(parameter);
			container.load();
			return new EmbeddedSolrServer(container, "skos");
		}
	}


}
