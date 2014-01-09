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

	// ID of the main display in the body
	protected String mainDisplayId;
	
	public BodyReader(List<AbstractKosDisplayGenerator> generators, String mainDisplayId) {
		super();
		this.generators = generators;
		this.mainDisplayId = mainDisplayId;
	}
	
	public BodyReader(AbstractKosDisplayGenerator generator) {
		// the main display ID is automatically the ID of the single generator
		this(Collections.singletonList(generator), generator.getDisplayId());
	}
	
	public KosDocumentBody readBody(String mainLang, URI conceptScheme) 
	throws SparqlPerformException {
		KosDocumentBody body = new KosDocumentBody();
		
		for (AbstractKosDisplayGenerator aGenerator : this.generators) {
			KosDisplay display = aGenerator.generateDisplay(mainLang, conceptScheme, this);
			// if this is the main display in the body, mark it as such
			if(display.getDisplayId().equals(this.mainDisplayId)) {
				display.setMain(true);
			}
			body.getKosDisplay().add(display);
		}
		
		return body;
	}

	public String getMainDisplayId() {
		return mainDisplayId;
	}

	public void setMainDisplayId(String mainDisplayId) {
		this.mainDisplayId = mainDisplayId;
	}
	
}
