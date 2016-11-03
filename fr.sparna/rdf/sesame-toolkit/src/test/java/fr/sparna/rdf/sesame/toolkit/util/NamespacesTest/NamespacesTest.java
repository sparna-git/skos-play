package fr.sparna.rdf.sesame.toolkit.util.NamespacesTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.util.Namespaces;

public class NamespacesTest {

	protected Namespaces ns;
	
	@Before
	public void init() {
		// a ajouter dans les parametres de la VM :
		// -Dorg.slf4j.simplelogger.defaultlog=debug
		// System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
		// pour activer les logs seulement pour fr.sparne.rdf :
		// -Dorg.slf4j.simplelogger.log.fr.sparna.rdf=debug
		ns = Namespaces.getInstance();
	}
	
	@Test
	public void testURIEndingInHashOrSlash() throws Exception {
		Assert.assertTrue(ns.split("http://www.toto.fr#")[1].equals(""));
		Assert.assertTrue(ns.split("http://www.toto.fr/")[1].equals(""));
	}
	
	@Test
	public void testWithRepository() throws Exception {
		Repository r = RepositoryBuilder.fromString("data.bnf/dump_works_tiny.n3");
		Assert.assertTrue(ns.withRepository(r).getURI("bnf-onto") != null);
	}
	
}
