package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
import fr.sparna.rdf.skos.printer.schema.Table;
import fr.sparna.rdf.skos.toolkit.GetTranslationTableInSchemeHelper;

public class TranslationTableBodyReader extends AbstractBodyReader {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected String targetLanguage;
	
	protected ConceptBlockReader cbReader;
	
	public TranslationTableBodyReader(Repository r, ConceptBlockReader cbReader, String targetLanguage) {
		super(r);
		this.targetLanguage = targetLanguage;
		this.cbReader = cbReader;
	}

	@Override
	public DisplayBody doRead(final String lang, final URI conceptScheme) 
	throws SPARQLPerformException {

		// init ConceptBlockReader
		this.cbReader.initInternal(tagsBundle, lang, conceptScheme);
		
		// prepare body
		DisplayBody body = new DisplayBody();
		
		// query for all the prefLabels correspondance
		final List<QueryResultRow> queryResultRows = new ArrayList<QueryResultRow>();
		GetTranslationTableInSchemeHelper helper = new GetTranslationTableInSchemeHelper(
				lang,
				targetLanguage,
				conceptScheme
		) {

			@Override
			protected void handleTranslation(
					Resource concept,
					Literal label1,
					Literal label2
			) throws TupleQueryResultHandlerException {
				QueryResultRow qrr = new QueryResultRow();
				qrr.conceptURI = concept.stringValue();
				// if no label in the main language, set the URI
				qrr.label1 = (label1 != null)?label1.stringValue():concept.stringValue();
				qrr.label2 = (label2 != null)?label2.stringValue():null;
				queryResultRows.add(qrr);
			}
			
		};
		
		// execute fetch translations
		Perform.on(repository).select(helper);		

		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(lang));
		collator.setStrength(Collator.SECONDARY);
		// sort rows on first cell
		Collections.sort(queryResultRows, new Comparator<QueryResultRow>() {
			@Override
			public int compare(QueryResultRow o1, QueryResultRow o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null || o1.label1 == null) return -1;
				if(o2 == null || o2.label1 == null) return 1;
				return collator.compare(
						o1.label1,
						o2.label1
				);
			}			
		});
		
		boolean addSections = queryResultRows.size() > 200;
		log.debug("Processing "+queryResultRows.size()+" entries.");
		if(addSections) {
			log.debug("Will add sections to the output");
			Section currentSection = null;
			for (QueryResultRow aRow : queryResultRows) {
				ConceptBlock cb1 = cbReader.readConceptBlock(aRow.conceptURI, aRow.label1, false);
				ConceptBlock cb2 = cbReader.readConceptBlock(aRow.conceptURI, aRow.label2, false);
				
				String sectionTitle = StringUtil.withoutAccents(aRow.label1).toUpperCase().substring(0, 1);
				if(currentSection == null || !sectionTitle.equals(currentSection.getTitle())) {
					// on est passé à une nouvelle section
					
					// on ajoute la section courante maintenant remplie
					body.getSection().add(currentSection);
					
					// et on créé une nouvelle section
					currentSection = new Section();
					Table newTable = new Table();
					newTable.setTableHeader(SchemaFactory.createRow(SchemaFactory.createTypedString(lang), SchemaFactory.createTypedString(this.targetLanguage)));
					currentSection.setTable(newTable);
					currentSection.setTitle(sectionTitle);
				}
				
				// add current item
				currentSection.getTable().getRow().add(SchemaFactory.createRow(cb1, cb2));
			}
			// ajouter la dernière section
			body.getSection().add(currentSection);
		} else {
			log.debug("Single section added to output");
			Section s = new Section();
			Table newTable = new Table();
			newTable.setTableHeader(SchemaFactory.createRow(SchemaFactory.createTypedString(lang), SchemaFactory.createTypedString(this.targetLanguage)));
			s.setTable(newTable);
			for (QueryResultRow aRow : queryResultRows) {
				ConceptBlock cb1 = cbReader.readConceptBlock(aRow.conceptURI, aRow.label1, true);
				ConceptBlock cb2 = cbReader.readConceptBlock(aRow.conceptURI, aRow.label2, true);
				newTable.getRow().add(SchemaFactory.createRow(cb1, cb2));
			}
			body.getSection().add(s);
		}
		
		return body;
	}
	
	class QueryResultRow {
		String conceptURI;
		String label1;
		String label2;
	}
	
	public static void main(String... args) throws Exception {
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build display result
		Display display = new Display();
		
		// build and set header
		HeaderReader headerReader = new HeaderReader(r);
		DisplayHeader header = headerReader.read("fr", (args.length > 1)?URI.create(args[1]):null);
		display.setHeader(header);
		
		TranslationTableBodyReader reader = new TranslationTableBodyReader(r, new ConceptBlockReader(r), "en");
		display.setBody(reader.readBody("fr", (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(display, new File("src/main/resources/translation-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.setDebug(true);
		printer.printToHtml(display, new File("display-test.html"));
		printer.printToPdf(display, new File("display-test.pdf"));
	}
	
}
