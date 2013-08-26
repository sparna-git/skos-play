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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.StringUtil;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.Display;
import fr.sparna.rdf.skos.printer.schema.DisplayBody;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class AlphabeticalIndexBodyReader extends AbstractBodyReader {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
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

	protected ConceptBlockReader cbReader;
	
	public AlphabeticalIndexBodyReader(Repository r, ConceptBlockReader cbReader) {
		super(r);
		this.cbReader = cbReader;
	}

	@Override
	public DisplayBody doRead(String lang, final URI conceptScheme) 
	throws SPARQLPerformException {
				
		// init ConceptBlockReader
		this.cbReader.initInternal(this.tagsBundle, lang, conceptScheme);
		
		// build our display body
		DisplayBody body = new DisplayBody();
		
		final List<QueryResultRow> queryResultRows = new ArrayList<QueryResultRow>();		
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
				QueryResultRow es = new QueryResultRow();
				es.conceptURI = concept.stringValue();
				es.label = label.stringValue();
				es.prefLabel = (prefLabel != null)?prefLabel.stringValue():null;	
				queryResultRows.add(es);
			}
		};
		
		Perform.on(repository).select(helper);		

		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(lang));
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(queryResultRows, new Comparator<QueryResultRow>() {

			@Override
			public int compare(QueryResultRow o1, QueryResultRow o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null) return -1;
				if(o2 == null) return 1;
				return collator.compare(o1.label, o2.label);
			}
			
		});
		
		boolean addSections = queryResultRows.size() > 200;
		log.debug("Processing "+queryResultRows.size()+" entries.");
		if(addSections) {
			log.debug("Will add sections to the output");
			Section currentSection = null;
			for (QueryResultRow anEntry : queryResultRows) {
				ConceptBlock cb = buildConceptBlock(anEntry);

				String entrySectionTitle = StringUtil.withoutAccents(anEntry.label).toUpperCase().substring(0, 1);
				if(currentSection == null || !entrySectionTitle.equals(currentSection.getTitle())) {
					// on est passé à une nouvelle section
					
					// on ajoute la section courante maintenant remplie
					body.getSection().add(currentSection);
					
					// et on créé une nouvelle section
					currentSection = new Section();
					fr.sparna.rdf.skos.printer.schema.List newList = new fr.sparna.rdf.skos.printer.schema.List();
					currentSection.setList(newList);
					currentSection.setTitle(entrySectionTitle);
				}
				currentSection.getList().getListItem().add(SchemaFactory.createListItem(cb));
			}
			// ajouter la dernière section
			body.getSection().add(currentSection);
		} else {
			log.debug("Single section added to output");
			Section s = new Section();
			fr.sparna.rdf.skos.printer.schema.List list = new fr.sparna.rdf.skos.printer.schema.List();
			s.setList(list);
			for (QueryResultRow aRow : queryResultRows) {
				ConceptBlock cb = buildConceptBlock(aRow);
				list.getListItem().add(SchemaFactory.createListItem(cb));
			}
			body.getSection().add(s);
		}
		
		return body;
	}

	private ConceptBlock buildConceptBlock(QueryResultRow aRow)
	throws SPARQLPerformException {
		ConceptBlock cb;
		// s'il y a un prefLabel, c'est que la valeur de "label" est un altLabel
		if(aRow.prefLabel != null) {
			// compute an ID for the entry based on conceptURI + label
			String conceptBlockId = Integer.toString((aRow.conceptURI+aRow.label).hashCode());
			cb = SchemaFactory.createConceptBlock(conceptBlockId, aRow.conceptURI, aRow.label, "alt");
			String refId = Integer.toString((aRow.conceptURI+aRow.prefLabel).hashCode());
			cb.getAttOrRef().add(SchemaFactory.createRef(refId, aRow.conceptURI, aRow.prefLabel, this.tagsBundle.getString(SKOS.PREF_LABEL.substring(SKOS.NAMESPACE.length())), "pref"));
		// sinon, la valeur de "label" est un prefLabel
		} else {
			cb = this.cbReader.readConceptBlock(aRow.conceptURI, aRow.label, true);
		}
		
		return cb;
	}
	
	class QueryResultRow {
		String label;
		String prefLabel;
		String conceptURI;
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
		HeaderReader headerReader = new HeaderReader(r);
		DisplayHeader header = headerReader.read("en", (args.length > 1)?URI.create(args[1]):null);
		display.setHeader(header);
		
		AlphabeticalIndexBodyReader reader = new AlphabeticalIndexBodyReader(r, new ConceptBlockReader(r, EXPANDED_SKOS_PROPERTIES));
		display.setBody(reader.readBody("en", (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(display, new File("src/main/resources/alpha-index-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		// ask for 2-columns !
		printer.getTransformerParams().put("column-count", 2);
		printer.printToHtml(display, new File("display-test.html"));
		printer.printToPdf(display, new File("display-test.pdf"));

	}
	
}
