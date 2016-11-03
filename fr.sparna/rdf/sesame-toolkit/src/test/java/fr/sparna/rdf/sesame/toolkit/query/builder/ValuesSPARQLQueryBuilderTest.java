package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.util.Arrays;

import org.junit.Test;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;

public class ValuesSPARQLQueryBuilderTest {

	@Test
	public void test1() throws Exception {
		String sparql = "SELECT DISTINCT ?type WHERE { ?x a ?type }";
		
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		
		ValuesSparqlQueryBuilder builder = new ValuesSparqlQueryBuilder(
				new SparqlQueryBuilder(sparql),
				"x",
				Arrays.asList(new Value[] { r.getValueFactory().createURI("http://www.exemple.fr")})
		);
		System.out.println(builder.getSPARQL());
		Perform.on(r).select(new SelectSparqlHelper(builder, new DebugHandler()));
	}
	
}
