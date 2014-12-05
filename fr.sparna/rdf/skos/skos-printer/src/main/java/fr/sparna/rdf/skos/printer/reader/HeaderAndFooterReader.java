package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.skos.printer.schema.KosDocumentFooter;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;

public class HeaderAndFooterReader {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Repository repository;
	protected String applicationString;
	
	public HeaderAndFooterReader(Repository repository) {
		super();
		this.repository = repository;
	}
	
	public KosDocumentHeader readHeader(final String lang, final URI conceptScheme)
	throws SparqlPerformException {
		KosDocumentHeader h = new KosDocumentHeader();
		
		if(conceptScheme == null) {
			// TODO
		} else {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:sourceConceptLabel
			LabelReader labelReader = new LabelReader(this.repository, "", lang);
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
	
	public KosDocumentFooter readFooter(final String lang, final URI conceptScheme)
	throws SparqlPerformException {
		KosDocumentFooter f = new KosDocumentFooter();
		
		if(conceptScheme == null) {
			// TODO
		} else {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:sourceConceptLabel
			LabelReader labelReader = new LabelReader(this.repository, "", lang);
			// add dcterms title and dc title
			labelReader.getProperties().add(URI.create(DCTERMS.TITLE.toString()));
			labelReader.getProperties().add(URI.create(DC.TITLE.toString()));
			String title = LabelReader.display(labelReader.getValues(conceptScheme));
			
			// try to read a dcterms:issued too
			String issued = readProperties(
					conceptScheme,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.ISSUED.stringValue()), java.net.URI.create(DC.DATE.stringValue()) }),
					""
			);
			String footer = "";
			if(!title.equals("")) {
				footer = title+((issued != null && !issued.equals(""))?" - "+issued:"");
			}
			
			log.debug("Header/Footer reader reading footer title in "+lang+" : '"+footer+"'");
			if(!footer.equals("")) {
				f.setTitle(footer);
			}
		}
		
		if(this.applicationString != null) {
			f.setApplication(this.applicationString);
		}
		
		return f;
	}
	
	protected String readProperties(URI subject, List<URI> uris, String lang) 
	throws SparqlPerformException {
		PreferredPropertyReader reader = new PreferredPropertyReader(
				this.repository,
				uris,
				(List<String>)null,
				lang
		);
		List<Value> v = reader.getValues(subject);
		log.debug(v.toString());
		return LabelReader.display(reader.getValues(subject));
	}

	public String getApplicationString() {
		return applicationString;
	}

	public void setApplicationString(String applicationString) {
		this.applicationString = applicationString;
	}

}
