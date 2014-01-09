package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;

public class HeaderReader {

	protected Repository repository;
	
	public HeaderReader(Repository repository) {
		super();
		this.repository = repository;
	}
	
	public KosDocumentHeader read(final String lang, final URI conceptScheme)
	throws SparqlPerformException {
		KosDocumentHeader h = new KosDocumentHeader();
		
		if(conceptScheme == null) {
			// TODO
		} else {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:label and dcterms:title
			LabelReader labelReader = new LabelReader(this.repository, lang);
			// add dcterms title and dc title
			labelReader.getProperties().add(URI.create(DCTERMS.TITLE.toString()));
			labelReader.getProperties().add(URI.create(DC.TITLE.toString()));
			String label = LabelReader.display(labelReader.getValues(conceptScheme));
			if(label != null) {
				h.setTitle(label);
			}
			
			// read a description in the given language
			String value = readProperties(
					conceptScheme,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.DESCRIPTION.stringValue()), java.net.URI.create(DC.DESCRIPTION.stringValue())}),
					lang
			);
			if(!value.equals("")) {
				h.setDescription(value);
			}			
			
			// read a date
			value = readProperties(
					conceptScheme,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.CREATED.stringValue()), java.net.URI.create(DC.DATE.stringValue())}),
					lang
			);
			if(!value.equals("")) {
				h.setDate(value);
			}		
			
			// read an author/creator
			value = readProperties(
					conceptScheme,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.CREATOR.stringValue()), java.net.URI.create(DC.CREATOR.stringValue())}),
					lang
			);
			if(!value.equals("")) {
				h.setCreator(value);
			}		
			
			// read a version ?
		}
		
		return h;
	}
	
	protected String readProperties(URI subject, List<URI> uris, String lang) 
	throws SparqlPerformException {
		PreferredPropertyReader reader = new PreferredPropertyReader(
				this.repository,
				uris,
				null,
				lang
		);
		return LabelReader.display(reader.getValues(subject));
	}
}
