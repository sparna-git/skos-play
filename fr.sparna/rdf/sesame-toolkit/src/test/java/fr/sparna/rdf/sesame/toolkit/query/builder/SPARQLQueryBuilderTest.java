package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;

public class SPARQLQueryBuilderTest {

	@Test
	public void test1() throws Exception {
		// String s = new ResourceSPARQLQueryBuilder("fr/sparna/rdf/sesame/toolkit/query/builder/test.rq").getSPARQL();
		String s = new SparqlQueryBuilder(this, "test.rq").getSPARQL();
		Assert.assertTrue(s != null);
	}
	
	@Test
	public void test2() throws Exception {
		StringBuffer buffer = new StringBuffer("SELECT ?x WHERE {");
		buffer.append(" ?x <http://purl.org/dc/terms/modified> ?modified .");
		buffer.append(" FILTER( ");
		buffer.append(" ?modified >= ?modificationDate");
		buffer.append(" )");
		buffer.append(" }");
		System.out.println(buffer.toString());
		
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		Date tomorrow = new Date();
		tomorrow.setDate(new Date().getDate()+1);
		Date yesterday = new Date();
		yesterday.setDate(new Date().getDate()-1);
		r.getConnection().add(r.getValueFactory().createStatement(
				r.getValueFactory().createURI("http://www.ex.fr/1"),
				r.getValueFactory().createURI("http://purl.org/dc/terms/modified"),
				r.getValueFactory().createLiteral(tomorrow)
		));
		r.getConnection().add(r.getValueFactory().createStatement(
				r.getValueFactory().createURI("http://www.ex.fr/2"),
				r.getValueFactory().createURI("http://purl.org/dc/terms/modified"),
				r.getValueFactory().createLiteral(yesterday)
		));
		
		final Literal literal = r.getValueFactory().createLiteral(new Date());
		
		SparqlQuery q = new SparqlQuery(
				buffer.toString(),
				new HashMap<String, Object>() {{
					put("modificationDate", literal);
				}}
		);
		
		Value v = Perform.on(r).read(q);
		System.out.println(v.stringValue());
	}
	
}
