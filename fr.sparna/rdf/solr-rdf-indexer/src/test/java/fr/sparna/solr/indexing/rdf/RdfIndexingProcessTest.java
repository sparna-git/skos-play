package fr.sparna.solr.indexing.rdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperFactory;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperIfc;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.solr.indexing.IndexingConsumerIfc;
import fr.sparna.solr.indexing.IndexingProcess;
import fr.sparna.solr.indexing.IndexingProcessorIfc;
import fr.sparna.solr.indexing.base.DebugIndexingConsumer;

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
		// IndexingSourceIfc<String> source = new MockIndexingSourceIfc(Arrays.asList(new String[] {"http://www.ex.fr/1", "http://www.ex.fr/2"}), "uri");
		SparqlRdfIndexingSource source = new SparqlRdfIndexingSource(repository, new SparqlQuery("SELECT ?x WHERE { ?x a <"+SKOS.CONCEPT+"> }"));
		List<IndexingProcessorIfc<Resource>> processors = new ArrayList<IndexingProcessorIfc<Resource>>();
		KeyValueHelperIfc<URI, Literal> helper = KeyValueHelperFactory.createUriToLiteralHelper(SKOS.NOTATION.stringValue());
		processors.add(new KeyValueRdfIndexingProcessor(repository, "notation", helper));
		List<IndexingConsumerIfc> consumers = Arrays.asList(new IndexingConsumerIfc[] {new DebugIndexingConsumer()});
		
		IndexingProcess<Resource> p = new IndexingProcess<Resource>(source, processors, consumers);
		p.start();
	}
	
}
