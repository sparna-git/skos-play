package fr.sparna.rdf.skos.printer.reader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.rdf4j.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.skos.printer.schema.KosDocumentFooter;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.toolkit.GetConceptSchemesHelper;

public class HeaderAndFooterReader {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected RepositoryConnection connection;
	protected String applicationString;
	
	public HeaderAndFooterReader(RepositoryConnection connection) {
		super();
		this.connection = connection;
	}
	
	public KosDocumentHeader readHeader(final String lang, final IRI conceptScheme) {
		KosDocumentHeader h = new KosDocumentHeader();
		
		IRI conceptSchemeToUse = conceptScheme;
		if (conceptSchemeToUse == null) {
			// try to find if their is a single concept scheme in the data, then take this one.
			List<Resource> conceptSchemes = findConceptSchemes();
			if(conceptSchemes.size() > 1) {
				log.debug("Found "+conceptSchemes.size()+" ConceptSchemes ("+conceptSchemes+"), can't determine which one to use");
			} else if (conceptSchemes.size() == 0) {
				log.debug("Found 0 ConceptSchemes, can't generate header.");
			} else {
				conceptSchemeToUse = (IRI)conceptSchemes.get(0);
				log.debug("Determined ConceptScheme automatically : "+conceptSchemeToUse);
			}
		}
		
		if (conceptSchemeToUse != null) {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:sourceConceptLabel
			LabelReader labelReader = new LabelReader(connection, "", lang);
			// add dcterms title and dc title
			labelReader.getProperties().add(DCTERMS.TITLE);
			labelReader.getProperties().add(DC.TITLE);
			String label = LabelReader.display(labelReader.getValues(conceptSchemeToUse));
			if(label != null) {
				h.setTitle(label);
			}
			
			// read a description in the given language
			String value = readProperties(
					connection,
					conceptSchemeToUse,
					Arrays.asList(new IRI[]{DCTERMS.DESCRIPTION, DC.DESCRIPTION}),
					lang
			);
			if(!value.equals("")) {
				h.setDescription(value);
			}			
			
			// read a date
			value = readProperties(
					connection,
					conceptSchemeToUse,
					Arrays.asList(new IRI[]{DCTERMS.CREATED, DC.DATE}),
					lang
			);
			if(!value.equals("")) {
				h.setDate(formatDate(value, lang));
			}		
			
			// read an author/creator
			value = readProperties(
					connection,
					conceptSchemeToUse,
					Arrays.asList(new IRI[]{DCTERMS.CREATOR, DC.CREATOR}),
					lang
			);
			if(!value.equals("")) {
				h.setCreator(value);
			}		
			
			// read a version ?
		}
		
		return h;
	}
	
	public KosDocumentFooter readFooter(final String lang, final IRI conceptScheme) {
		KosDocumentFooter f = new KosDocumentFooter();
		
		IRI conceptSchemeToUse = conceptScheme;
		if (conceptSchemeToUse == null) {
			// try to find if their is a single concept scheme in the data, then take this one.
			List<Resource> conceptSchemes = findConceptSchemes();
			if(conceptSchemes.size() > 1) {
				log.debug("Found "+conceptSchemes.size()+" ConceptSchemes ("+conceptSchemes+"), can't determine which one to use");
			} else if (conceptSchemes.size() == 0) {
				log.debug("Found 0 ConceptSchemes, can't generate header.");
			} else {
				conceptSchemeToUse = (IRI)conceptSchemes.get(0);
				log.debug("Determined ConceptScheme automatically : "+conceptSchemeToUse);
			}
		}
		
		if(conceptSchemeToUse != null) {
			// this will try to read in turn all the properties defined in a LabelReader
			// skos:prefLabel, rdfs:sourceConceptLabel
			LabelReader labelReader = new LabelReader(connection, "", lang);
			// add dcterms title and dc title
			labelReader.getProperties().add(DCTERMS.TITLE);
			labelReader.getProperties().add(DC.TITLE);
			String title = LabelReader.display(labelReader.getValues(conceptSchemeToUse));
			
			// try to read a dcterms:issued too
			String issued = readProperties(
					connection,
					conceptSchemeToUse,
					Arrays.asList(new IRI[]{DCTERMS.ISSUED, DC.DATE }),
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
	
	protected List<Resource> findConceptSchemes() {
		final List<Resource> conceptSchemeList = new ArrayList<Resource>();
		
		Perform.on(connection).select(new GetConceptSchemesHelper(null) {		
			@Override
			protected void handleConceptScheme(Resource conceptScheme)
			throws TupleQueryResultHandlerException {
				conceptSchemeList.add(conceptScheme);
			}
		});
		
		return conceptSchemeList;
	}
	
	protected String readProperties(RepositoryConnection connection, IRI subject, List<IRI> uris, String lang) {
		PreferredPropertyReader reader = new PreferredPropertyReader(
				connection,
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
