package fr.sparna.rdf.rdf4j.skos;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.impl.SimpleLogger;

import fr.sparna.rdf.rdf4j.toolkit.query.Queries;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;

public class InferIsoThesThesaurusArrayTest {

	@Test
	public void test() {
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
		Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());
		RepositoryBuilder rb = new RepositoryBuilderFactory("/fr/sparna/rdf/rdf4j/skos/InferIsoThesThesaurusArray.ttl").get();
		Repository r = rb.get();
		try(RepositoryConnection c = r.getConnection()) {
			Model m = Queries.examineUpdateResult(c, "/fr/sparna/rdf/skos/helper/InferIsoThesThesaurusArray.rq");
			m.stream().forEach(s -> {
				log.debug(s.toString());
			});
			Assert.assertEquals(1, m.subjects().size());
		}
	}
	
}
