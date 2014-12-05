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
import fr.sparna.rdf.sesame.toolkit.languages.Languages;
import fr.sparna.rdf.sesame.toolkit.languages.Languages.Language;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.printer.schema.Table;
import fr.sparna.rdf.skos.toolkit.GetTranslationTableInSchemeHelper;

public class TranslationTableReverseDisplayGenerator extends AbstractKosDisplayGenerator {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected String targetLanguage;
	
	protected ConceptBlockReader cbReader;
	
	
	public TranslationTableReverseDisplayGenerator(Repository r, ConceptBlockReader cbReader, String targetLanguage, String displayId) {
		super(r, displayId);
		this.targetLanguage = targetLanguage;
		this.cbReader = cbReader;
	}
	
	public TranslationTableReverseDisplayGenerator(Repository r, ConceptBlockReader cbReader, String targetLanguage) {
		super(r);
		this.targetLanguage = targetLanguage;
		this.cbReader = cbReader;
	}

	@Override
	public KosDisplay doGenerate(final String lang, final URI conceptScheme) 
	throws SparqlPerformException {

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
		Perform.on(repository).select(helper);		

		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(this.targetLanguage));
		collator.setStrength(Collator.SECONDARY);
		// sort rows on first cell
		Collections.sort(queryResultRows, new Comparator<QueryResultRow>() {
			@Override
			public int compare(QueryResultRow o1, QueryResultRow o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null || o1.label2 == null) return -1;
				if(o2 == null || o2.label2 == null) return 1;
				return collator.compare(
						o1.label2,
						o2.label2
				);
			}			
		});
		
		
		log.debug("Single section added to output");
		Section s = new Section();
		s.setTitle(TranslationTableDisplayGenerator.displayLanguage(this.targetLanguage, lang));
		Table newTable = SchemaFactory.createTable(50, 50);
		newTable.setTableHeader(SchemaFactory.createRow(
				SchemaFactory.createStr(SchemaFactory.createStyledString(TranslationTableDisplayGenerator.displayLanguage(this.targetLanguage, lang))),
				SchemaFactory.createStr(SchemaFactory.createStyledString(TranslationTableDisplayGenerator.displayLanguage(lang, lang)))
		));
		s.setTable(newTable);
		for (QueryResultRow aRow : queryResultRows) {
			// don't display rows that would start with an empty sourceConceptLabel in the first column
			if(aRow.label2 != null && !aRow.label2.equals("")) {
				// siouxerie pour éviter les ID dupliquées dans le cas où un libellé serait le même dans les 2 langues
				ConceptBlock cb1 = cbReader.readConceptBlock(aRow.conceptURI, aRow.label2, cbReader.computeConceptBlockId(aRow.conceptURI, aRow.label2+"-"+this.targetLanguage), false);
				ConceptBlock cb2 = cbReader.readConceptBlock(aRow.conceptURI, aRow.label1, cbReader.computeConceptBlockId(aRow.conceptURI, aRow.label1+"-"+lang), false);
				newTable.getRow().add(SchemaFactory.createRow(cb1, cb2));
			}
		}
		d.getSection().add(s);
		
		// return display
		return d;
	}
	
	class QueryResultRow {
		String conceptURI;
		String label1;
		String label2;
	}
	
	public static void main(String... args) throws Exception {
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		final String LANG = "fr";
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build result document
		KosDocument document = new KosDocument();
		
		// build and set header
		HeaderAndFooterReader headerReader = new HeaderAndFooterReader(r);
		KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?URI.create(args[1]):null);
		document.setHeader(header);
		
		TranslationTableReverseDisplayGenerator reader = new TranslationTableReverseDisplayGenerator(r, new ConceptBlockReader(r), "en");
		BodyReader bodyReader = new BodyReader(reader);
		document.setBody(bodyReader.readBody(LANG, (args.length > 1)?URI.create(args[1]):null));

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
