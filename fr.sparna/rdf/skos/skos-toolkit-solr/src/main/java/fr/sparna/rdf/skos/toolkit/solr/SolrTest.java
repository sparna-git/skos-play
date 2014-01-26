package fr.sparna.rdf.skos.toolkit.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;

public class SolrTest {

	public static void main(String... args) throws Exception {
		System.setProperty("solr.solr.home", "/home/thomas/workspace/sources/sparna/fr.sparna/rdf/solr-rdf-indexer/src/main/resources/solr-skos");
		CoreContainer.Initializer initializer1 = new CoreContainer.Initializer();
		CoreContainer coreContainer1 = initializer1.initialize();
		EmbeddedSolrServer server1 = new EmbeddedSolrServer(coreContainer1, "skos");
		CoreContainer.Initializer initializer2 = new CoreContainer.Initializer();
		CoreContainer coreContainer2 = initializer2.initialize();
		EmbeddedSolrServer server2 = new EmbeddedSolrServer(coreContainer2, "skos");
		
		SolrInputDocument doc1 = new SolrInputDocument();
		doc1.addField( "uri", "http://A.fr", 1.0f );
		doc1.addField( "prefLabel_fr", "doc1", 1.0f );
		
		server1.add(doc1);
		server1.commit();
		server1.optimize();
		
//		SolrQuery query = new SolrQuery();
//	    query.setQuery( "*:*" );
//	    
//	    QueryResponse rsp1 = server1.query( query );
//	    System.out.println("server1 : "+rsp1.getResults().getNumFound());
//	    QueryResponse rsp2 = server2.query( query );
//	    System.out.println("server2 : "+rsp2.getResults().getNumFound());
	    
	    server1.shutdown();
	    server2.shutdown();
	    coreContainer1.shutdown();
	    coreContainer2.shutdown();
	}

}
