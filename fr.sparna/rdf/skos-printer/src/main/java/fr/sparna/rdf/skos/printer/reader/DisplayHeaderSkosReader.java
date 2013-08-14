package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;

public class DisplayHeaderSkosReader {

	protected Repository repository;

	public DisplayHeaderSkosReader(Repository repository) {
		super();
		this.repository = repository;
	}
	
	public DisplayHeader read(final String lang, final URI conceptScheme)
	throws SPARQLPerformException {
		DisplayHeader h = new DisplayHeader();
		
		if(conceptScheme == null) {
			// TODO
		} else {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:label and dcterms:title
			LabelReader labelReader = new LabelReader(this.repository, lang);
			String label = LabelReader.display(labelReader.getLabels(conceptScheme));
			if(label != null) {
				h.setTitle(label);
			}
			
			// read a description in the given language
			PropertyReader descriptionReader = new PropertyReader(repository, java.net.URI.create(DCTERMS.DESCRIPTION.stringValue()), lang);
			List<Value> descriptions = descriptionReader.read(conceptScheme);
			if(descriptions != null && descriptions.size() > 0) {
				// take the first one
				h.setDescription(((Literal)descriptions.get(0)).getLabel());
			} else {
				// try with dc
				PropertyReader dcDescriptionReader = new PropertyReader(repository, java.net.URI.create(DC.DESCRIPTION.stringValue()), lang);
				descriptions = dcDescriptionReader.read(conceptScheme);
				if(descriptions != null && descriptions.size() > 0) {
					// take the first one
					h.setDescription(((Literal)descriptions.get(0)).getLabel());
				} 
			}
		}
		
		return h;
	}
}
