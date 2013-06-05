package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.StringUtil;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.skos.GetLabelsInSchemeHelper;
import fr.sparna.rdf.sesame.toolkit.skos.SKOS;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.Alphabetical;
import fr.sparna.rdf.skos.printer.schema.Display;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;
import fr.sparna.rdf.skos.printer.schema.Entry;
import fr.sparna.rdf.skos.printer.schema.Section;

public class AlphabeticalSkosReader {
	
	public static final List<String> EXPANDED_SKOS_PROPERTIES = Arrays.asList(new String[] {
			SKOS.ALT_LABEL,
			SKOS.BROADER,
			SKOS.NARROWER,
			SKOS.RELATED,
			SKOS.NOTATION,
			SKOS.DEFINITION,
			SKOS.SCOPE_NOTE,
			SKOS.EXAMPLE,			
			SKOS.CHANGE_NOTE,			
			SKOS.HISTORY_NOTE,
			SKOS.EDITORIAL_NOTE,
	});
	

	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected Repository repository;

	protected ResourceBundle tagsBundle;
	
	protected List<String> skosPropertiesToRead;

	public AlphabeticalSkosReader(Repository r) {
		super();
		this.repository = r;
	}

	public Alphabetical read(final String lang, final URI conceptScheme) 
	throws SPARQLExecutionException {
		
		// init tag resource bundle if not set
		if(this.tagsBundle == null) {
			tagsBundle = ResourceBundle.getBundle(
					"fr.sparna.rdf.skos.display.Tags",
					new Locale(lang),
					new fr.sparna.i18n.StrictResourceBundleControl()
			);
		}
				
		// build our alphabetical display
		final Alphabetical alphabetical = new Alphabetical();
		
		final List<EntrySpec> entries = new ArrayList<EntrySpec>();
		
		GetLabelsInSchemeHelper helper = new GetLabelsInSchemeHelper(
				lang,
				conceptScheme
		) {
			@Override
			protected void handleLabel(
					Literal label,
					Literal prefLabel,
					Resource concept
			) throws TupleQueryResultHandlerException {
				EntrySpec es = new EntrySpec();
				es.conceptURI = concept.stringValue();
				es.label = label.stringValue();
				es.prefLabel = (prefLabel != null)?prefLabel.stringValue():null;	
				entries.add(es);
			}
		};
		
		Perform.on(repository).select(helper);		

		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(lang));
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(entries, new Comparator<EntrySpec>() {

			@Override
			public int compare(EntrySpec o1, EntrySpec o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null) return -1;
				if(o2 == null) return 1;
				return collator.compare(o1.label, o2.label);
			}
			
		});
		
		
		// no concept scheme filtering - we want to be able to read prefLabel independently from the conceptScheme
		final PropertyReader prefLabelReader = new PropertyReader(this.repository, URI.create(SKOS.PREF_LABEL), lang, null, null);

		// setup additional readers
		final List<PropertyReader> additionalReaders = new ArrayList<PropertyReader>();
		
		if(this.skosPropertiesToRead != null) {
			for (String aProperty : this.skosPropertiesToRead) {
				additionalReaders.add(
						(conceptScheme != null)
						?new PropertyReader(this.repository, URI.create(aProperty), (SKOS.isDatatypeProperty(aProperty))?lang:null, URI.create(SKOS.IN_SCHEME), URI.create(conceptScheme.toString()))
						:new PropertyReader(this.repository, URI.create(aProperty), (SKOS.isDatatypeProperty(aProperty))?lang:null, null, null)
				);
			}
		}
		
		boolean addSections = entries.size() > 100;
		log.debug("Processing "+entries.size()+" entries.");
		if(addSections) {
			log.debug("Will add sections to the output");
			Section currentSection = null;
			for (EntrySpec anEntry : entries) {
				Entry e = buildEntry(anEntry, additionalReaders, prefLabelReader);

				String entrySectionTitle = StringUtil.withoutAccents(anEntry.label).toUpperCase().substring(0, 1);
				if(currentSection == null || !entrySectionTitle.equals(currentSection.getTitle())) {
					// on est passé à une nouvelle section
					
					// on ajoute la section courante maintenant remplie
					alphabetical.getEntryOrSection().add(currentSection);
					
					// et on créé une nouvelle section
					currentSection = new Section();
					currentSection.setTitle(entrySectionTitle);
				}
				currentSection.getEntry().add(e);

			}
			// ajouter la dernière section
			alphabetical.getEntryOrSection().add(currentSection);
		} else {
			log.debug("No sections added to output");
			for (EntrySpec anEntry : entries) {
				Entry e = buildEntry(anEntry, additionalReaders, prefLabelReader);
				alphabetical.getEntryOrSection().add(e);
			}
		}
		
		return alphabetical;
	}

	private Entry buildEntry(EntrySpec anEntry, final List<PropertyReader> additionalReaders, final PropertyReader prefLabelReader)
	throws SPARQLExecutionException {
		Entry e;
		// s'il y a un prefLabel, c'est que la valeur de "label" est un altLabel
		if(anEntry.prefLabel != null) {
			String entryId = Integer.toString((anEntry.conceptURI+anEntry.label).hashCode());
			e = SchemaFactory.createEntry(entryId, anEntry.conceptURI, anEntry.label, "alt");
			String entryRef = Integer.toString((anEntry.conceptURI+anEntry.prefLabel).hashCode());
			e.getAttOrRef().add(SchemaFactory.createRef(entryRef, anEntry.conceptURI, anEntry.prefLabel, this.tagsBundle.getString(SKOS.PREF_LABEL.substring(SKOS.NAMESPACE.length())), "pref"));

		// sinon, la valeur de "label" est un prefLabel
		} else {
			String entryId = Integer.toString((anEntry.conceptURI+anEntry.label).hashCode());
			e = SchemaFactory.createEntry(entryId, anEntry.conceptURI, anEntry.label);

			for (PropertyReader predicateReader : additionalReaders) {
				List<Value> values = predicateReader.read(URI.create(anEntry.conceptURI));
				for (Value value : values) {

					if(value instanceof Literal) {
						e.getAttOrRef().add(
								SchemaFactory.createAtt(
										((Literal)value).stringValue(),
										this.tagsBundle.getString(predicateReader.getPropertyURI().toString().substring(SKOS.NAMESPACE.length())),
										(predicateReader.getPropertyURI().toString().equals(SKOS.ALT_LABEL))?"alt":null
										)
								);
					} else {
						org.openrdf.model.URI aRef = (org.openrdf.model.URI)value;
						List<Value> prefs = prefLabelReader.read(URI.create(aRef.stringValue()));
						String prefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue();
						String entryRef = Integer.toString((aRef.stringValue()+prefLabel).hashCode());
						e.getAttOrRef().add(
								SchemaFactory.createRef(
										entryRef,
										aRef.stringValue(),
										prefLabel,
										this.tagsBundle.getString(predicateReader.getPropertyURI().toString().substring(SKOS.NAMESPACE.length())),
										"pref"
										)
								);
					}
				}
			}
		}
		
		return e;
	}
	
	class EntrySpec {
		String label;
		String prefLabel;
		String conceptURI;
	}
	
	
	
	public List<String> getSkosPropertiesToRead() {
		return skosPropertiesToRead;
	}

	public void setSkosPropertiesToRead(List<String> skosPropertiesToRead) {
		this.skosPropertiesToRead = skosPropertiesToRead;
	}

	public void setTagsBundle(ResourceBundle tagsBundle) {
		this.tagsBundle = tagsBundle;
	}

	public static void main(String... args) throws Exception {	
//		Repository r = RepositoryBuilder.fromRdf(
//				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
//				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
//				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
//				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
//				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3-pref\"@fr ."
//		);
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build display result
		Display display = new Display();
		
		// build and set header
		DisplayHeaderSkosReader headerReader = new DisplayHeaderSkosReader(r);
		DisplayHeader header = headerReader.read("en", (args.length > 1)?URI.create(args[1]):null);
		display.setHeader(header);
		
		AlphabeticalSkosReader reader = new AlphabeticalSkosReader(r);
		reader.setSkosPropertiesToRead(EXPANDED_SKOS_PROPERTIES);
		display.getAlphabeticalOrHierarchical().add(reader.read("en", (args.length > 1)?URI.create(args[1]):null));

		DisplayPrinter printer = new DisplayPrinter();
		printer.printToHtml(display, new File("display-test.html"));
//		printer.printToPdf(display, new File("display-test.pdf"));
		
//		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
//		m.setProperty("jaxb.formatted.output", true);
//		m.marshal(display, System.out);
		
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		dbf.setNamespaceAware(true);
//		DocumentBuilder domFactory = dbf.newDocumentBuilder();
//		Document displayDom = domFactory.newDocument();
//		m.marshal(display, displayDom);
//		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		XSLProcessor.createDefaultProcessor().transform("stylesheets/display-to-html.xsl", displayDom, baos);
//		System.out.println(baos.toString());
//		ReadWriteXML.write(displayDom, new File("/home/thomas/display-test.xml"));
	}
	
}
