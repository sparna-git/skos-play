package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.skos.printer.schema.Alphabetical;
import fr.sparna.rdf.skos.printer.schema.TranslationTable;

public class TranslationTableSkosReader {

	protected Repository repository;
	
	public TranslationTableSkosReader(Repository r) {
		super();
		this.repository = r;
	}
	
	public TranslationTable read(final URI conceptScheme, String lang1, String lang2) 
	throws SPARQLPerformException {
		
		return null;
	}
	
}
