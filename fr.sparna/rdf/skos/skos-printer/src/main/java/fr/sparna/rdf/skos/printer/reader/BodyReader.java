package fr.sparna.rdf.skos.printer.reader;

import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;

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
	
	public KosDocumentBody readBody(String mainLang, IRI conceptScheme) {
		KosDocumentBody body = new KosDocumentBody();
		
		for (AbstractKosDisplayGenerator aGenerator : this.generators) {
			KosDisplay display = aGenerator.generateDisplay(mainLang, conceptScheme);
			if(display.getSection() != null && display.getSection().size() != 0) {
				body.getKosDisplay().add(display);
			}
		}
		
		return body;
	}
	
}
