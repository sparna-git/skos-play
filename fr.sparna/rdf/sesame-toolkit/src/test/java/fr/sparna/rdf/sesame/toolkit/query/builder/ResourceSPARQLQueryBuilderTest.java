package fr.sparna.rdf.sesame.toolkit.query.builder;

import junit.framework.Assert;

import org.junit.Test;

import fr.sparna.rdf.sesame.toolkit.query.builder.ResourceSPARQLQueryBuilder;

public class ResourceSPARQLQueryBuilderTest {

	@Test
	public void test1() throws Exception {
		// String s = new ResourceSPARQLQueryBuilder("fr/sparna/rdf/sesame/toolkit/query/builder/test.rq").getSPARQL();
		String s = new ResourceSPARQLQueryBuilder(this, "test.rq").getSPARQL();
		Assert.assertTrue(s != null);
	}
	
}
