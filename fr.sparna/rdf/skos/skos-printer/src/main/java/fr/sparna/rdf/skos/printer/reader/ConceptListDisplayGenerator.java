package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.math.BigInteger;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.StringUtil;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.toolkit.GetConceptsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class ConceptListDisplayGenerator extends AbstractKosDisplayGenerator {
	
	public static final List<String> EXPANDED_SKOS_PROPERTIES = Arrays.asList(new String[] {
			SKOS.NOTATION,
			SKOS.ALT_LABEL,
			SKOS.BROADER,
			SKOS.NARROWER,
			SKOS.RELATED,			
			SKOS.DEFINITION,
			SKOS.SCOPE_NOTE,
			SKOS.EXAMPLE,
			SKOS.CHANGE_NOTE,			
			SKOS.HISTORY_NOTE,
			SKOS.EDITORIAL_NOTE,
	});
	
	public static final List<String> EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS = Arrays.asList(new String[] {
			SKOS.NOTATION,
			SKOS.ALT_LABEL,
			SKOSPLAY.TOP_TERM,
			SKOS.BROADER,
			SKOS.NARROWER,
			SKOS.RELATED,			
			SKOS.DEFINITION,
			SKOS.SCOPE_NOTE,
			SKOS.EXAMPLE,
			SKOS.CHANGE_NOTE,			
			SKOS.HISTORY_NOTE,
			SKOS.EDITORIAL_NOTE,
	});
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected ConceptBlockReader cbReader;

	
	public ConceptListDisplayGenerator(RepositoryConnection connection, ConceptBlockReader cbReader, String displayId) {
		super(connection, displayId);
		this.cbReader = cbReader;
	}
	
	public ConceptListDisplayGenerator(RepositoryConnection connection, ConceptBlockReader cbReader) {
		super(connection);
		this.cbReader = cbReader;
	}

	@Override
	public KosDisplay doGenerate(final String lang, final IRI conceptScheme) {

		// init ConceptBlockReader
		this.cbReader.initInternal(lang, conceptScheme, this.displayId);
		
		// build our display
		KosDisplay d = new KosDisplay();

		final List<QueryResultRow> queryResultRows = new ArrayList<QueryResultRow>();
		
		GetConceptsInSchemeHelper helper = new GetConceptsInSchemeHelper(
				lang,
				conceptScheme
		) {
			@Override
			protected void handleConcept(
					Resource concept,
					Literal label
			) throws TupleQueryResultHandlerException {
				QueryResultRow es = new QueryResultRow();
				es.conceptURI = concept.stringValue();
				es.prefLabel = (label != null)?label.stringValue():null;	
				queryResultRows.add(es);
			}
		};
		

		Perform.on(connection).select(helper);		

		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(lang));
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(queryResultRows, new Comparator<QueryResultRow>() {

			@Override
			public int compare(QueryResultRow o1, QueryResultRow o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null) return -1;
				if(o2 == null) return 1;
				return collator.compare(o1.prefLabel, o2.prefLabel);
			}
			
		});
		
		boolean addSections = queryResultRows.size() > 200;
		log.debug("Processing "+queryResultRows.size()+" entries.");
		if(addSections) {
			log.debug("Will add sections to the output");
			Section currentSection = null;
			for (QueryResultRow aRow : queryResultRows) {
				ConceptBlock cb = this.cbReader.readConceptBlock(connection, aRow.conceptURI, aRow.prefLabel, true);

				String entrySectionTitle = StringUtil.withoutAccents(aRow.prefLabel).toUpperCase().substring(0, 1);
				if(currentSection == null || !entrySectionTitle.equals(currentSection.getTitle())) {
					// on est passé à une nouvelle section
					
					// on ajoute la section courante maintenant remplie
					d.getSection().add(currentSection);
					
					// et on créé une nouvelle section
					currentSection = new Section();
					fr.sparna.rdf.skos.printer.schema.List newList = new fr.sparna.rdf.skos.printer.schema.List();
					currentSection.setList(newList);
					currentSection.setTitle(entrySectionTitle);
				}
				currentSection.getList().getListItem().add(SchemaFactory.createListItem(cb));
			}
			// ajouter la dernière section
			d.getSection().add(currentSection);
		} else {
			log.debug("No sections added to output");
			Section s = new Section();
			fr.sparna.rdf.skos.printer.schema.List list = new fr.sparna.rdf.skos.printer.schema.List();
			s.setList(list);
			for (QueryResultRow aRow : queryResultRows) {
				ConceptBlock cb = this.cbReader.readConceptBlock(connection, aRow.conceptURI, aRow.prefLabel, true);
				list.getListItem().add(SchemaFactory.createListItem(cb));
			}
			d.getSection().add(s);
		}
		
		// ask for 2 columns !
		d.setColumnCount(BigInteger.valueOf(2));
		
		return d;
	}
	
	class QueryResultRow {
		String prefLabel;
		String conceptURI;
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
		
		final String LANG = "fr";
		
		Repository r = RepositoryBuilderFactory.fromString(args[0]).get();
		
		try(RepositoryConnection connection = r.getConnection()) {
			// build display result
			KosDocument document = new KosDocument();
			
			// build and set header
			HeaderAndFooterReader headerReader = new HeaderAndFooterReader(connection);
			KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null);
			document.setHeader(header);
			
			ConceptBlockReader cbReader = new ConceptBlockReader();
			cbReader.setSkosPropertiesToRead(EXPANDED_SKOS_PROPERTIES);
			ConceptListDisplayGenerator reader = new ConceptListDisplayGenerator(connection, cbReader);
			BodyReader bodyReader = new BodyReader(reader);
			document.setBody(bodyReader.readBody(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null));
	
			DisplayPrinter printer = new DisplayPrinter();
			printer.setDebug(true);
			printer.printToHtml(document, new File("display-test.html"), LANG);
			printer.printToPdf(document, new File("display-test.pdf"), LANG);
			
	//		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
	//		m.setProperty("jaxb.formatted.output", true);
	//		m.marshal(display, System.out);
		}
	}
	
}
