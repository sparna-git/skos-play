package fr.sparna.rdf.extractor.cli;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BufferedGroupingRDFHandler;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLWriterFactory;
import org.eclipse.rdf4j.rio.rdfxml.util.RDFXMLPrettyWriterFactory;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Test;

public class RDF4JModelTest {

	@Test
	public void testModelSerialize() {
		SimpleValueFactory factory = SimpleValueFactory.getInstance();
		Model m = new LinkedHashModel();
		m.add(factory.createIRI("http://sparna.fr"), RDF.TYPE, SKOS.CONCEPT);
		m.add(factory.createIRI("http://sparna.fr"), SKOS.PREF_LABEL, factory.createLiteral("Sparna", (IRI)null));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RDFHandler writer = new RDFXMLPrettyWriterFactory().getWriter(baos);
		Rio.write(m, writer);
		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		RDFHandler writer = new RDFXMLWriterFactory().getWriter(baos);
//		writer = new BufferedGroupingRDFHandler(writer);
		
//		Repository r = new SailRepository(new MemoryStore());
//		r.initialize();
//		try(RepositoryConnection connection = r.getConnection()) {
//			connection.setNamespace("skos", SKOS.NAMESPACE);
//			connection.add(m);
//			connection.export(writer);
//		}
		
		System.out.println(baos.toString());
	}
	
}
