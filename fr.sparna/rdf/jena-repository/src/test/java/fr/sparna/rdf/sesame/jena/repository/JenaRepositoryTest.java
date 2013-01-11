package fr.sparna.rdf.sesame.jena.repository;

import junit.framework.Assert;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaRepositoryTest {

	@Test
	public void testRepository() throws Exception {
		Model model = ModelFactory.createDefaultModel();
		JenaRepository repository = new JenaRepository(model);
		repository.initialize();
		Assert.assertTrue(repository.getValueFactory() != null);
		Assert.assertTrue(repository.getConnection() != null);
	}
	
	@Test
	public void testShutdown() throws Exception {
		Model model = ModelFactory.createDefaultModel();
		JenaRepository repository = new JenaRepository(model);
		repository.initialize();
		Assert.assertFalse(model.isClosed());
		repository.shutDown();
		Assert.assertTrue(model.isClosed());
	}
	
	
}
