package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.Collections;
import java.util.List;


import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocumentBody;

public class BodyReader {

	// generators for display contained in this body
	protected List<AbstractKosDisplayGenerator> generators;
	
	public BodyReader(List<AbstractKosDisplayGenerator> generators) {
		super();
		this.generators = generators;
	}
	
	public BodyReader(AbstractKosDisplayGenerator generator) {
		// the main display ID is automatically the ID of the single generator
		this(Collections.singletonList(generator));
	}
	
	public KosDocumentBody readBody(String mainLang, URI conceptScheme) 
	throws SparqlPerformException {
		KosDocumentBody body = new KosDocumentBody();
		
		for (AbstractKosDisplayGenerator aGenerator : this.generators) {
			KosDisplay display = aGenerator.generateDisplay(mainLang, conceptScheme);
			body.getKosDisplay().add(display);
		}
		
		return body;
	}
	
}
