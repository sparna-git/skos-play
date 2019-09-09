package fr.sparna.rdf.skos.xls2skos;

import java.io.StringReader;

import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public class TurtleParserTest {

	public static void main(String... strings) {
		// try to parse it like a blank node
		StringBuffer turtle = new StringBuffer();
		turtle.append("@prefix sh: <http://www.w3.org/ns/shacl#> ."+"\n");
		turtle.append("@prefix eli: <http://data.europa.eu/eli/ontology#> ."+"\n");
		turtle.append("<http://toto.fr> <http://is.a> [ sh:inversePath eli:is_realized_by] .");
		System.out.println(turtle.toString());
		StatementCollector collector = new StatementCollector();
		RDFParser parser = RDFParserRegistry.getInstance().get(RDFFormat.TURTLE).get().getParser();
		parser.setRDFHandler(collector);
		try {
			parser.parse(new StringReader(turtle.toString()), RDF.NS.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
