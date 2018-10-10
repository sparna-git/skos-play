package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
import fr.sparna.rdf.rdf4j.toolkit.languages.Languages;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.printer.schema.Table;
import fr.sparna.rdf.skos.toolkit.GetTranslationTableInSchemeHelper;

public class TranslationTableDisplayGenerator extends AbstractKosDisplayGenerator {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected String targetLanguage;
	
	protected ConceptBlockReader cbReader;
	
	
	public TranslationTableDisplayGenerator(RepositoryConnection connection, ConceptBlockReader cbReader, String targetLanguage, String displayId) {
		super(connection, displayId);
		this.targetLanguage = targetLanguage;
		this.cbReader = cbReader;
	}
	
	public TranslationTableDisplayGenerator(RepositoryConnection connection, ConceptBlockReader cbReader, String targetLanguage) {
		super(connection);
		this.targetLanguage = targetLanguage;
		this.cbReader = cbReader;
	}

	@Override
	public KosDisplay doGenerate(final String lang, final IRI conceptScheme) 
	{

		// init ConceptBlockReader
		this.cbReader.initInternal(lang, conceptScheme, this.displayId);
		
		// prepare body
		KosDisplay d = new KosDisplay();
		
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
				// if no sourceConceptLabel in the main language, set the URI
				qrr.label1 = (label1 != null)?label1.stringValue():concept.stringValue();
				qrr.label2 = (label2 != null)?label2.stringValue():null;
				queryResultRows.add(qrr);
			}
			
		};
		
		// execute fetch translations
		Perform.on(connection).select(helper);				

		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(lang));
		collator.setStrength(Collator.SECONDARY);
		// sort rows on first cell
		Collections.sort(queryResultRows, new Comparator<QueryResultRow>() {
			@Override
			public int compare(QueryResultRow o1, QueryResultRow o2) {
				if((o1 == null || o1.label1 == null) && (o2 == null || o2.label1 == null)) return 0;
				if(o1 == null || o1.label1 == null) return 1;
				if(o2 == null || o2.label1 == null) return -1;
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
				// siouxerie pour éviter les ID dupliquées dans le cas où un libellé serait le même dans les 2 langues
				ConceptBlock cb1 = cbReader.readConceptBlock(connection, aRow.conceptURI, aRow.label1, cbReader.computeConceptBlockId(aRow.conceptURI, aRow.label1+"-"+lang), false);
				ConceptBlock cb2 = cbReader.readConceptBlock(connection, aRow.conceptURI, aRow.label2, cbReader.computeConceptBlockId(aRow.conceptURI, aRow.label2+"-"+this.targetLanguage), false);
				
				String sectionTitle = StringUtil.withoutAccents(aRow.label1).toUpperCase().substring(0, 1);
				if(currentSection == null || !sectionTitle.equals(currentSection.getTitle())) {
					// on est passé à une nouvelle section
					
					// on ajoute la section courante maintenant remplie
					d.getSection().add(currentSection);
					
					// et on créé une nouvelle section
					currentSection = new Section();
					Table newTable = SchemaFactory.createTable(50, 50);
					newTable.setTableHeader(SchemaFactory.createRow(
							SchemaFactory.createStr(SchemaFactory.createStyledString(displayLanguage(lang, lang))),
							SchemaFactory.createStr(SchemaFactory.createStyledString(displayLanguage(this.targetLanguage, lang)))
					));
					currentSection.setTable(newTable);
					currentSection.setTitle(sectionTitle);
				}
				
				// add current item
				currentSection.getTable().getRow().add(SchemaFactory.createRow(cb1, cb2));
			}
			// ajouter la dernière section
			d.getSection().add(currentSection);
		} else {
			log.debug("Single section added to output");
			Section s = new Section();
			Table newTable = new Table();
			newTable.setTableHeader(SchemaFactory.createRow(
					SchemaFactory.createStr(SchemaFactory.createStyledString(displayLanguage(lang, lang))),
					SchemaFactory.createStr(SchemaFactory.createStyledString(displayLanguage(this.targetLanguage, lang)))
			));
			s.setTable(newTable);
			for (QueryResultRow aRow : queryResultRows) {
				// siouxerie pour éviter les ID dupliquées dans le cas où un libellé serait le même dans les 2 langues
				ConceptBlock cb1 = cbReader.readConceptBlock(
						connection,
						aRow.conceptURI,
						aRow.label1,
						cbReader.computeConceptBlockId(aRow.conceptURI, aRow.label1+"-"+lang),
						true);
				ConceptBlock cb2 = cbReader.readConceptBlock(
						connection,
						aRow.conceptURI,
						aRow.label2,
						cbReader.computeConceptBlockId(aRow.conceptURI, aRow.label2+"-"+this.targetLanguage),
						true);
				newTable.getRow().add(SchemaFactory.createRow(cb1, cb2));
			}
			d.getSection().add(s);
		}
		
		// return display
		return d;
	}
	
	class QueryResultRow {
		String conceptURI;
		String label1;
		String label2;
	}
	
	public static String displayLanguage(String languageCode, String displayLanguage) {
		Languages.Language l = Languages.getInstance().withIso639P1(languageCode);
		if(l != null) {
			return l.displayIn(displayLanguage)+" ("+languageCode+")";
		} else {
			return languageCode;
		}
	}
	
	public static void main(String... args) throws Exception {
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		final String LANG = "fr";
		
		Repository r = RepositoryBuilderFactory.fromString(args[0]).get();
		
		// build result document
		KosDocument document = new KosDocument();
		
		try(RepositoryConnection connection = r.getConnection()) {
			// build and set header
			HeaderAndFooterReader headerReader = new HeaderAndFooterReader(connection);
			KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null);
			document.setHeader(header);
			
			TranslationTableDisplayGenerator reader = new TranslationTableDisplayGenerator(connection, new ConceptBlockReader(), "en");
			BodyReader bodyReader = new BodyReader(reader);
			document.setBody(bodyReader.readBody(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null));
	
			Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
			m.setProperty("jaxb.formatted.output", true);
			// m.marshal(display, System.out);
			m.marshal(document, new File("src/main/resources/translation-output-test.xml"));
			
			DisplayPrinter printer = new DisplayPrinter();
			printer.setDebug(true);
			printer.printToHtml(document, new File("display-test.html"), LANG);
			printer.printToPdf(document, new File("display-test.pdf"), LANG);
		}
	}
	
}
