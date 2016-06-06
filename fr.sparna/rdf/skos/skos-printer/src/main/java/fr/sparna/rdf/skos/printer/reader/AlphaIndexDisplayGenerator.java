package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
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
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.DisplayPrinter.Style;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class AlphaIndexDisplayGenerator extends AbstractKosDisplayGenerator {
	
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
	
	public static final List<String> EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS = Arrays.asList(new String[] {
			SKOS.ALT_LABEL,
			SKOSPLAY.TOP_TERM,
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
	
	public static final List<String> EXPANDED_SKOS_PROPERTIES_WITH_MT = Arrays.asList(new String[] {
			SKOS.ALT_LABEL,
			SKOSPLAY.MEMBER_OF,
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
	
	
	public AlphaIndexDisplayGenerator(Repository r, ConceptBlockReader cbReader, String displayId) {
		super(r, displayId);
		this.cbReader = cbReader;
	}
	
	
	public AlphaIndexDisplayGenerator(Repository r, ConceptBlockReader cbReader) {
		super(r);
		this.cbReader = cbReader;
	}

	@Override
	public KosDisplay doGenerate(String lang, final URI conceptScheme) 
	throws SparqlPerformException {
				
		// init ConceptBlockReader
		this.cbReader.initInternal(lang, conceptScheme, this.displayId);
		
		// init display
		KosDisplay d = new KosDisplay();
		
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

				String entrySectionTitle = StringUtil.withoutAccents(anEntry.label).toUpperCase().substring(0, Math.min(1, anEntry.label.length()));
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
			log.debug("Single section added to output");
			Section s = new Section();
			fr.sparna.rdf.skos.printer.schema.List list = new fr.sparna.rdf.skos.printer.schema.List();
			s.setList(list);
			for (QueryResultRow aRow : queryResultRows) {
				ConceptBlock cb = buildConceptBlock(aRow);
				list.getListItem().add(SchemaFactory.createListItem(cb));
			}
			d.getSection().add(s);
		}
		
		// ask for two columns
		d.setColumnCount(BigInteger.valueOf(2));
		
		return d;
	}

	private ConceptBlock buildConceptBlock(QueryResultRow aRow)
	throws SparqlPerformException {
		ConceptBlock cb;
		// s'il y a un prefLabel, c'est que la valeur de "sourceConceptLabel" est un altLabel
		if(aRow.prefLabel != null) {
			cb = this.cbReader.readConceptBlockForSynonym(aRow.conceptURI, aRow.label, aRow.prefLabel);
		// sinon, la valeur de "sourceConceptLabel" est un prefLabel
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

	public static void main(String... args) throws Exception {	
//		Repository r = RepositoryBuilder.fromRdf(
//				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
//				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
//				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
//				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
//				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3-pref\"@fr ."
//		);
		
		final String LANG = "fr";
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		// Repository r = RepositoryBuilder.fromString(args[0]);
		RepositoryBuilder localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));
		localRepositoryBuilder.addOperation(new LoadFromFileOrDirectory(args[0]));
		Repository r = localRepositoryBuilder.createNewRepository();
		
		// SKOS-XL
		ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getSkosXl2SkosRuleset()));
		au.execute(r);
		
		// build result document
		KosDocument document = new KosDocument();
		
		// build and set header
		HeaderAndFooterReader headerReader = new HeaderAndFooterReader(r);
		KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?URI.create(args[1]):null);
		document.setHeader(header);
		
		// build and set metadata
		DocumentMetadataReader metaReader = new DocumentMetadataReader(r);
		document.setKosDocumentMetadata(
				metaReader.readKosDocumentMetadata(LANG, (args.length > 1)?URI.create(args[1]):null)
		);
		
		ConceptBlockReader cbReader = new ConceptBlockReader(r);
		cbReader.setSkosPropertiesToRead(EXPANDED_SKOS_PROPERTIES_WITH_MT);
		cbReader.setAdditionalLabelLanguagesToInclude(Arrays.asList(new String[] { "en", "es", "ru" }));
		
		AlphaIndexDisplayGenerator reader = new AlphaIndexDisplayGenerator(r, cbReader);
		BodyReader bodyReader = new BodyReader(reader);		
		document.setBody(bodyReader.readBody(LANG, (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(document, new File("src/main/resources/alpha-index-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.setStyle(Style.UNESCO);
		printer.printToHtml(document, new File("display-test.html"), LANG);
		printer.printToPdf(document, new File("display-test.pdf"), LANG);

	}
	
}
