package fr.sparna.solr.indexing.rdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;

import fr.sparna.assembly.AssemblyConsumer;
import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.AssemblyStation;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperFactory;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperIfc;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.solr.index.SolrIndexableFactory;
import fr.sparna.rdf.solr.index.source.SparqlRdfAssemblySource;
import fr.sparna.rdf.solr.index.step.KeyValueRdfIndexingStation;

public class RdfIndexingProcessTest {

	protected static final String RDF_DATA = "test-skos.rdf";
	
	protected Repository repository;
	
	@Before
	public void init() throws Exception {
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna").setLevel(org.apache.log4j.Level.TRACE);
		
		repository = RepositoryBuilder.fromString(RDF_DATA);
	}
	
	@Test
	public void testSparqlRdfIndexingSource() throws Exception {
		// AssemblySource<String> source = new MockIndexingSourceIfc(Arrays.asList(new String[] {"http://www.ex.fr/1", "http://www.ex.fr/2"}), "uri");
		SparqlRdfAssemblySource<SolrInputDocument> source = new SparqlRdfAssemblySource<SolrInputDocument>(
				repository,
				new SparqlQuery("SELECT ?x WHERE { ?x a <"+SKOS.CONCEPT+"> }"),
				new SolrIndexableFactory("uri")
		);
		List<AssemblyStation<SolrInputDocument>> processors = new ArrayList<AssemblyStation<SolrInputDocument>>();
		KeyValueHelperIfc<URI, Literal> helper = KeyValueHelperFactory.createUriToLiteralHelper(SKOS.NOTATION.stringValue());
		processors.add(new KeyValueRdfIndexingStation(repository, "notation", helper));
		List<AssemblyConsumer<SolrInputDocument>> consumers = new ArrayList<AssemblyConsumer<SolrInputDocument>>();
		// consumers.add(new DebugAssemblyConsumer());
		
		AssemblyLine<SolrInputDocument> p = new AssemblyLine<SolrInputDocument>(source, processors, consumers);
		p.start();
	}
	
}
