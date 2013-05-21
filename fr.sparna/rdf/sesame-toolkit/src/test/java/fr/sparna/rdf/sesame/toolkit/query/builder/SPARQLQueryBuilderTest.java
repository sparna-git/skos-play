package fr.sparna.rdf.sesame.toolkit.query.builder;

import junit.framework.Assert;

import org.junit.Test;

public class SPARQLQueryBuilderTest {

	@Test
	public void test1() throws Exception {
		// String s = new ResourceSPARQLQueryBuilder("fr/sparna/rdf/sesame/toolkit/query/builder/test.rq").getSPARQL();
		String s = new SPARQLQueryBuilder(this, "test.rq").getSPARQL();
		Assert.assertTrue(s != null);
	}
	
}
