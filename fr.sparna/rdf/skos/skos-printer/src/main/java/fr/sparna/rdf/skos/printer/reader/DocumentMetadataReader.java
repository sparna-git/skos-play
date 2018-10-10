package fr.sparna.rdf.skos.printer.reader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBElement;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
import fr.sparna.rdf.skos.printer.schema.ElementContainer;
import fr.sparna.rdf.skos.printer.schema.ObjectFactory;
import fr.sparna.rdf.skos.printer.schema.SimpleLiteral;
import fr.sparna.rdf.skos.toolkit.GetConceptSchemesHelper;

public class DocumentMetadataReader {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Repository repository;
	
	public DocumentMetadataReader(Repository repository) {
		super();
		this.repository = repository;
	}
	
	public ElementContainer readKosDocumentMetadata(final String lang, final IRI conceptScheme) {
		ElementContainer m = new ElementContainer();
		final ObjectFactory objectFactory = new ObjectFactory();
		
		try(RepositoryConnection connection = this.repository.getConnection()) {
			IRI conceptSchemeToUse = conceptScheme;
			if (conceptSchemeToUse == null) {
				// try to find if their is a single concept scheme in the data, then take this one.
				List<Resource> conceptSchemes = findConceptSchemes(connection);
				if(conceptSchemes.size() > 1) {
					log.debug("Found "+conceptSchemes.size()+" ConceptSchemes ("+conceptSchemes+"), can't determine which one to use");
				} else if (conceptSchemes.size() == 0) {
					log.debug("Found 0 ConceptSchemes, can't generate header.");
				} else {
					conceptSchemeToUse = SimpleValueFactory.getInstance().createIRI(conceptSchemes.get(0).stringValue());
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
					m.getAny().add(createSimpleLiteralValue(label, lang, new DCCreator() {
						public JAXBElement<SimpleLiteral> createDc(SimpleLiteral value) {
							return objectFactory.createTitle(value);
						}
					}));
				}
				
				MetadataReader reader = new MetadataReader(conceptSchemeToUse, lang);
				
				// read a description in the given language
				reader.process(m,
						connection,
						Arrays.asList(new IRI[]{DCTERMS.DESCRIPTION, DC.DESCRIPTION}),
						new DCCreator() {
							public JAXBElement<SimpleLiteral> createDc(SimpleLiteral value) {
								return objectFactory.createDescription(value);
							}
						}
				);		
				
				// read a date
				reader.process(m,
						connection,
						Arrays.asList(new IRI[]{DCTERMS.ISSUED, DCTERMS.MODIFIED, DCTERMS.CREATED, DC.DATE}),
						new DCCreator() {
							public JAXBElement<SimpleLiteral> createDc(SimpleLiteral value) {
								return objectFactory.createDate(value);
							}
						}
				);		
				
				// read an author/creator
				reader.process(m,
						connection,
						Arrays.asList(new IRI[]{DCTERMS.CREATOR, DC.CREATOR}),
						new DCCreator() {
							public JAXBElement<SimpleLiteral> createDc(SimpleLiteral value) {
								return objectFactory.createCreator(value);
							}
						}
				);
				
				// read subject
				reader.process(m,
						connection,
						Arrays.asList(new IRI[]{DCTERMS.SUBJECT, DC.SUBJECT}),
						new DCCreator() {
							public JAXBElement<SimpleLiteral> createDc(SimpleLiteral value) {
								return objectFactory.createSubject(value);
							}
						}
				);	
			}
		} // end try(connection)
		
		return m;
	}
	
	
	protected List<Resource> findConceptSchemes(RepositoryConnection connection) {
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
	
	protected List<Value> readProperties(RepositoryConnection connection, IRI subject, List<IRI> uris, String lang) {
		PreferredPropertyReader reader = new PreferredPropertyReader(
				connection,
				uris,
				Arrays.asList(new String[] { "" }),
				lang
		);
		List<Value> v = reader.getValues(subject);
		return v;
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
	
	interface DCCreator {
		public JAXBElement<SimpleLiteral> createDc(SimpleLiteral value);
	}
	
	protected JAXBElement<SimpleLiteral> createSimpleLiteralValue(String value, String lang, DCCreator dcCreator) {		
		SimpleLiteral literal = new SimpleLiteral();
		literal.getContent().add(value);
		literal.setLang(lang);
		return dcCreator.createDc(literal);
	}
	
	class MetadataReader {
		private IRI conceptSchemeToUse;
		private String lang;
		
		public MetadataReader(IRI conceptSchemeToUse, String lang) {
			super();
			this.conceptSchemeToUse = conceptSchemeToUse;
			this.lang = lang;
		}
		
		public void process(ElementContainer container, RepositoryConnection connection, List<IRI> properties, DCCreator dcCreator) {
			List<Value> values = readProperties(
					connection,
					conceptSchemeToUse,
					properties,
					lang
			);
			if(values != null) {
				container.getAny().add(createSimpleLiteralValue(LabelReader.display(values), null, dcCreator));
//				for (Value aValue : values) {
//					String theLang = (aValue instanceof Literal)?((Literal)aValue).getLanguage():lang;
//					String theValue = (aValue instanceof Literal)?((Literal)aValue).getLabel():aValue.stringValue();
//					container.getAny().add(createSimpleLiteralValue(theValue, theLang, dcCreator));
//				}
			}
		}
	}

}
