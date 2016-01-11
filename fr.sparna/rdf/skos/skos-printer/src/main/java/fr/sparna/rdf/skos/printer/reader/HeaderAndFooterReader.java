package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.skos.printer.schema.KosDocumentFooter;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.toolkit.GetConceptSchemesHelper;

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
		
		
		URI conceptSchemeToUse = conceptScheme;
		if (conceptSchemeToUse == null) {
			// try to find if their is a single concept scheme in the data, then take this one.
			List<Resource> conceptSchemes = findConceptSchemes();
			if(conceptSchemes.size() > 1) {
				log.debug("Found "+conceptSchemes.size()+" ConceptSchemes ("+conceptSchemes+"), can't determine which one to use");
			} else if (conceptSchemes.size() == 0) {
				log.debug("Found 0 ConceptSchemes, can't generate header.");
			} else {
				conceptSchemeToUse = URI.create(conceptSchemes.get(0).stringValue());
				log.debug("Determined ConceptScheme automatically : "+conceptSchemeToUse);
			}
		}
		
		if (conceptSchemeToUse != null) {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:sourceConceptLabel
			LabelReader labelReader = new LabelReader(this.repository, "", lang);
			// add dcterms title and dc title
			labelReader.getProperties().add(URI.create(DCTERMS.TITLE.toString()));
			labelReader.getProperties().add(URI.create(DC.TITLE.toString()));
			String label = LabelReader.display(labelReader.getValues(conceptSchemeToUse));
			if(label != null) {
				h.setTitle(label);
			}
			
			// read a description in the given language
			String value = readProperties(
					conceptSchemeToUse,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.DESCRIPTION.stringValue()), java.net.URI.create(DC.DESCRIPTION.stringValue())}),
					lang
			);
			if(!value.equals("")) {
				h.setDescription(value);
			}			
			
			// read a date
			value = readProperties(
					conceptSchemeToUse,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.CREATED.stringValue()), java.net.URI.create(DC.DATE.stringValue())}),
					lang
			);
			if(!value.equals("")) {
				h.setDate(formatDate(value, lang));
			}		
			
			// read an author/creator
			value = readProperties(
					conceptSchemeToUse,
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
		
		URI conceptSchemeToUse = conceptScheme;
		if (conceptSchemeToUse == null) {
			// try to find if their is a single concept scheme in the data, then take this one.
			List<Resource> conceptSchemes = findConceptSchemes();
			if(conceptSchemes.size() > 1) {
				log.debug("Found "+conceptSchemes.size()+" ConceptSchemes ("+conceptSchemes+"), can't determine which one to use");
			} else if (conceptSchemes.size() == 0) {
				log.debug("Found 0 ConceptSchemes, can't generate header.");
			} else {
				conceptSchemeToUse = URI.create(conceptSchemes.get(0).stringValue());
				log.debug("Determined ConceptScheme automatically : "+conceptSchemeToUse);
			}
		}
		
		if(conceptSchemeToUse != null) {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:sourceConceptLabel
			LabelReader labelReader = new LabelReader(this.repository, "", lang);
			// add dcterms title and dc title
			labelReader.getProperties().add(URI.create(DCTERMS.TITLE.toString()));
			labelReader.getProperties().add(URI.create(DC.TITLE.toString()));
			String title = LabelReader.display(labelReader.getValues(conceptSchemeToUse));
			
			// try to read a dcterms:issued too
			String issued = readProperties(
					conceptSchemeToUse,
					Arrays.asList(new URI[]{java.net.URI.create(DCTERMS.ISSUED.stringValue()), java.net.URI.create(DC.DATE.stringValue()) }),
					""
			);
			
			String footer = "";
			if(!title.equals("")) {
				footer = title+((issued != null && !issued.equals(""))?" - "+formatDate(issued, lang):"");
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
	
	protected List<Resource> findConceptSchemes() throws SparqlPerformException {
		final List<Resource> conceptSchemeList = new ArrayList<Resource>();
		
		Perform.on(repository).select(new GetConceptSchemesHelper(null) {		
			@Override
			protected void handleConceptScheme(Resource conceptScheme)
			throws TupleQueryResultHandlerException {
				conceptSchemeList.add(conceptScheme);
			}
		});
		
		return conceptSchemeList;
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
	
	protected String formatDate(String date, String lang) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd"); 
			Date d = sdf.parse(date);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(lang));
			String dateString = df.format(d);
			
			return dateString;
		} catch (ParseException e) {
			// return the original date by default
			log.error(e.getMessage());
			return date;
		}
	}

	public String getApplicationString() {
		return applicationString;
	}

	public void setApplicationString(String applicationString) {
		this.applicationString = applicationString;
	}

}
