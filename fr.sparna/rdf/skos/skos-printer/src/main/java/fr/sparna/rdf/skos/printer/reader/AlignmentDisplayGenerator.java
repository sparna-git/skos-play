package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.Function;
import fr.sparna.commons.lang.Lists;
import fr.sparna.commons.lang.StringUtil;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.Namespaces;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.printer.schema.StyledString;
import fr.sparna.rdf.skos.printer.schema.Table;
import fr.sparna.rdf.skos.toolkit.GetAlignmentsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class AlignmentDisplayGenerator extends AbstractKosDisplayGenerator {

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected ConceptBlockReader cbReader;
	protected AlignmentDataHarvesterIfc alignmentHarvester;
	
	// set whether we want to have a single table, sorted by source concept
	// or multiple tables, one for each target scheme
	protected boolean separateByTargetScheme = true;

	public AlignmentDisplayGenerator(
			Repository r,
			ConceptBlockReader cbReader,
			String displayId,
			AlignmentDataHarvesterIfc alignmentHarvester
	) {
		super(r, displayId);
		this.cbReader = cbReader;
		this.alignmentHarvester = alignmentHarvester;
	}
	
	public AlignmentDisplayGenerator(
			Repository r,
			ConceptBlockReader cbReader,
			AlignmentDataHarvesterIfc alignmentHarvester
	) {
		super(r);
		this.cbReader = cbReader;
		this.alignmentHarvester = alignmentHarvester;
	}

	@Override
	public KosDisplay doGenerate(final String lang, final URI conceptScheme) 
	throws SparqlPerformException {

		// init ConceptBlockReader
		this.cbReader.initInternal(lang, conceptScheme, this.displayId);

		// prepare body
		KosDisplay d = new KosDisplay();
		
		// harvest necessary data for alignments
		try {
			this.alignmentHarvester.harvestData(repository, conceptScheme);
		} catch (RepositoryOperationException e) {
			e.printStackTrace();
		}
		
		// query for all the correspondance
		final List<AlignmentRow> queryResultRows = new ArrayList<AlignmentRow>();
		GetAlignmentsInSchemeHelper helper = new GetAlignmentsInSchemeHelper(
				conceptScheme
		) {
			
			@Override
			protected void handleAlignment(
					Resource concept,
					Resource alignementType,
					Resource targetConcept
			) throws TupleQueryResultHandlerException {
				AlignmentRow qrr = new AlignmentRow();
				qrr.sourceConcept = concept.toString();
				qrr.alignmentType = alignementType.toString();
				qrr.targetConcept = targetConcept.toString();
				queryResultRows.add(qrr);
			}
		};
		
		// execute fetch alignments
		Perform.on(repository).select(helper);	

		log.debug("Found "+queryResultRows.size()+" alignment rows");
		
		// prepare a skos:prefLabel reader for concepts of our thesaurus
		LabelReader conceptLabelReader = new LabelReader(
				repository,
				Arrays.asList(new URI[] {
						URI.create(SKOS.PREF_LABEL)
				}),
				"",
				lang
		);
		
		// prepare a skos:prefLabel reader for concepts of our thesaurus
		List<String> fallbackLanguages = new ArrayList<String>();
		// if language variant, check it first
		if(lang.indexOf("-") > 0) {
			fallbackLanguages.add(lang.substring(0, lang.indexOf("-")));
		}
		// then check for no language
		fallbackLanguages.add("");
		// then check for english
		fallbackLanguages.add("en");
		
		LabelReader alignedConceptLabelReader = new LabelReader(
				repository,
				Arrays.asList(new URI[] {
						URI.create(SKOS.PREF_LABEL),
						// pour DBPedia
						URI.create(RDFS.LABEL.toString()),
						// pour INSPIRE
						URI.create(DCTERMS.TITLE.toString())
				}),
				fallbackLanguages,
				lang
		);
		
		// prepare a skos:prefLabel reader for concept schemes
		LabelReader schemeLabelReader = new LabelReader(
				repository,
				Arrays.asList(new URI[] {
						URI.create(SKOS.PREF_LABEL),
						// pour DBPedia
						URI.create(RDFS.LABEL.toString()),
						// pour les concept schemes
						URI.create(DCTERMS.TITLE.toString()),
						URI.create(DC.TITLE.toString())}),
				fallbackLanguages,
				lang
		);
		
		// read source concept labels
		Map<URI, List<Value>> sourceLabels = conceptLabelReader.getValues(
				// on évite les doublons !
				new HashSet<URI>(
					// convertir la liste en liste des URI de concepts source
					Lists.transform(queryResultRows, new Function<AlignmentRow, URI>() {
						public URI apply(AlignmentRow r) { return URI.create(r.sourceConcept); }
					}
				)
		));
		
		// read target concepts labels
		HashSet<URI> targetConcepts = new HashSet<URI>();
		for (AlignmentRow aRow : queryResultRows) {
			targetConcepts.add(URI.create(aRow.targetConcept));
		}
		Map<URI, List<Value>> targetLabels = alignedConceptLabelReader.getValues(targetConcepts);
		
		// read skos:inScheme values
		PreferredPropertyReader inSchemeReader = new PreferredPropertyReader(repository, URI.create(SKOS.IN_SCHEME));
		Map<URI, List<Value>> inSchemes = inSchemeReader.getValues(targetConcepts);
		
		// store the labels in data-structure
		List<AlignmentRow> newAlignmentRows = new ArrayList<AlignmentRow>();
		for (AlignmentRow row : queryResultRows) {
			
			// on récupère les schemes
			Set<String> targetSchemes = new HashSet<String>();
			if(inSchemes.get(URI.create(row.targetConcept)) != null) {
				for (Value aScheme : inSchemes.get(URI.create(row.targetConcept))) {
					targetSchemes.add(aScheme.stringValue());
				}
			}
			
			// si aucun scheme trouvé, on met cette ligne dans la section correspondant à sa racine d'URI
			if(targetSchemes == null || targetSchemes.size() == 0) {
				targetSchemes = new HashSet<String>(Arrays.asList(new String[] { Namespaces.getInstance().split(row.targetConcept)[0] }));
			}
			
			// pour chaque scheme cible, on génère une ligne de tableau
			for (String aScheme : targetSchemes) {
				AlignmentRow newRow = new AlignmentRow();
				newRow.sourceConcept = row.sourceConcept;
				newRow.targetConcept = row.targetConcept;
				newRow.alignmentType = row.alignmentType;
				newRow.sourceConceptLabel = LabelReader.display(sourceLabels.get(URI.create(row.sourceConcept)));
				newRow.targetConceptLabel = LabelReader.display(targetLabels.get(URI.create(row.targetConcept)));
				newRow.targetScheme = aScheme;
				newRow.targetSchemeLabel = LabelReader.display(schemeLabelReader.getValues(URI.create(aScheme))).intern();
				newAlignmentRows.add(newRow);
			}
		}
		
		AlignmentTablesGenerator generator;
		if(this.isSeparateByTargetScheme()) {
			generator = new TargetSchemeAlignmentTablesGenerator();
		} else {
			generator = new SourceConceptAlignmentTablesGenerator();
		}
		
		generator.addAlignmentTables(newAlignmentRows, d, lang);
		
		// return display
		return d;
	}
	
	public boolean isSeparateByTargetScheme() {
		return separateByTargetScheme;
	}

	public void setSeparateByTargetScheme(boolean separateByTargetScheme) {
		this.separateByTargetScheme = separateByTargetScheme;
	}
	
	class AlignmentRow {
		String sourceConcept;
		String sourceConceptLabel;
		String alignmentType;
		String targetConcept;
		String targetConceptLabel;
		String targetScheme;
		String targetSchemeLabel;
	}
	
	class SectionData {
		String label;
		String sectionUri;
		
		public SectionData(String label, String sectionUri) {
			super();
			this.label = label;
			this.sectionUri = sectionUri;
		}
	}
	
	
	
	interface AlignmentTablesGenerator {		
		public void addAlignmentTables(List<AlignmentRow> data, KosDisplay d, String lang) throws SparqlPerformException;		
	}
	
	
	class TargetSchemeAlignmentTablesGenerator implements AlignmentTablesGenerator {
		
		public void addAlignmentTables(List<AlignmentRow> data, KosDisplay d, String lang) throws SparqlPerformException {
			// setup Collator
			final Collator collator = Collator.getInstance(new Locale(lang));
			collator.setStrength(Collator.SECONDARY);
			// sort rows on target scheme first, and then on first row label
			Collections.sort(data, new Comparator<AlignmentRow>() {
				@Override
				public int compare(AlignmentRow o1, AlignmentRow o2) {
					// si les schemes sont différents, on tri d'abord sur le scheme
					if(o1.targetScheme == null && o2.targetScheme != null) {
						return -1;
					} else if(o2.targetScheme == null && o1.targetScheme != null) {
						return 1;
					} else if(o1.targetScheme != null && o2.targetScheme != null && !o1.targetScheme.equals(o2.targetScheme)) {
						return o1.targetScheme.compareTo(o2.targetScheme);
					} else {
						// les schemes sont égaux, on tri sur le libellé de la première colonne
						if(o1 == null && o2 == null) return 0;
						if(o1 == null || o1.sourceConceptLabel == null) return -1;
						if(o2 == null || o2.sourceConceptLabel == null) return 1;
						return collator.compare(
								o1.sourceConceptLabel,
								o2.sourceConceptLabel
						);
					}
				}
			});
			
			// if we have some data...
			if(data.size() > 0) {
				AlignmentRow previousRow = null;
				Section currentSection = null;
				Table currentTable = null;
				for (AlignmentRow aRow : data) {
					
					// 1. either this is the first row (no existing section), or we are moving to a row in a new section
					if(previousRow == null || !aRow.targetScheme.equals(previousRow.targetScheme)) {
						previousRow = aRow;
						
						// if this is not the first row, we add the current section to the output
						if(currentSection != null) {
							d.getSection().add(currentSection);
						}
						
						// 2. initialize a new section
						currentSection = new Section();
						String prefix = "Alignment with ";
						if(lang.startsWith("fr")) {
							prefix = "Alignement avec ";
						}
						currentSection.setTitle(prefix+aRow.targetSchemeLabel);
						currentTable = SchemaFactory.createTable(40, 20, 40);
						currentTable.setTableHeader(SchemaFactory.createRow(
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentSource")),
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentType")),
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentTarget"))
						));
						currentSection.setTable(currentTable);
					}
					
					// 3. add our row to the current table
					// siouxerie pour éviter les ID dupliquées dans le cas où un libellé serait le même dans les 2 langues
					ConceptBlock cb1 = cbReader.readConceptBlock(
							aRow.sourceConcept,
							aRow.sourceConceptLabel,
							cbReader.computeConceptBlockId(aRow.sourceConcept, aRow.alignmentType+"-"+aRow.targetConcept),
							false
					);
					// set a random UUID since we won't have that concept in overall output
					// ConceptBlock cb2 = cbReader.readConceptBlock(aRow.targetConcept, aRow.targetConceptLabel, UUID.randomUUID().toString(), false);
					ConceptBlock cb2 = SchemaFactory.createConceptBlock(
							UUID.randomUUID().toString(),
							aRow.targetConcept,
							SchemaFactory.createLabelLinkExternal(aRow.targetConcept, aRow.targetConceptLabel, null)
							);
					JAXBElement<StyledString> type = SchemaFactory.createStr(
								SchemaFactory.createStyledKey(aRow.alignmentType.substring(SKOS.NAMESPACE.length()), "smaller")
					);
					
					currentTable.getRow().add(SchemaFactory.createRow(cb1, type, cb2));
				}
				
				// add last section to the document
				d.getSection().add(currentSection);
			}
		}
	}
	
	class SourceConceptAlignmentTablesGenerator implements AlignmentTablesGenerator {		
		
		public void addAlignmentTables(List<AlignmentRow> data, KosDisplay d, String lang) throws SparqlPerformException {
			
			// setup Collator
			final Collator collator = Collator.getInstance(new Locale(lang));
			collator.setStrength(Collator.SECONDARY);
			// sort rows on target scheme first, and then on first row label
			Collections.sort(data, new Comparator<AlignmentRow>() {
				@Override
				public int compare(AlignmentRow o1, AlignmentRow o2) {
					// on tri sur le libellé de la première colonne
					if(o1 == null && o2 == null) return 0;
					if(o1 == null || o1.sourceConceptLabel == null) return -1;
					if(o2 == null || o2.sourceConceptLabel == null) return 1;
					int labelCompare = collator.compare(
							o1.sourceConceptLabel,
							o2.sourceConceptLabel
					);
					if(labelCompare == 0) {
						// sort on target scheme label
						if(o1.targetSchemeLabel == null) return -1;
						if(o2.targetSchemeLabel == null) return 1;
						return collator.compare(
								o1.targetSchemeLabel,
								o2.targetSchemeLabel
						);
					} else {
						return labelCompare;
					}
				}
			});
			
			// if we have some data...
			if(data.size() > 0) {
				AlignmentRow previousRow = null;
				Section currentSection = null;
				Table currentTable = null;
				for (AlignmentRow aRow : data) {
					
					// 1. either this is the first row (no existing section), or we are moving to a row in a new section
					String currentFirstLetter = StringUtil.withoutAccents(aRow.sourceConceptLabel).toUpperCase().substring(0, 1);
					if(
							previousRow == null
							||
							!currentFirstLetter.equals(StringUtil.withoutAccents(previousRow.sourceConceptLabel).toUpperCase().substring(0, 1))
					) {					
						// if this is not the first row, we add the current section to the output
						if(currentSection != null) {
							d.getSection().add(currentSection);
						}
						
						// 2. initialize a new section
						currentSection = new Section();
						String title = null;
						if(lang.startsWith("fr")) {
							title = "Alignements - ";
						} else {
							title = "Mappings - ";
						}
						currentSection.setTitle(title+currentFirstLetter);
						currentTable = SchemaFactory.createTable(32, 24, 12, 32);
						currentTable.setTableHeader(SchemaFactory.createRow(
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentSource")),
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentScheme")),
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentType")),
								SchemaFactory.createStr(SchemaFactory.createStyledKey("alignmentTarget"))
						));	
						currentSection.setTable(currentTable);
					}
					
					// 3. add our row to the current table
					// siouxerie pour éviter les ID dupliquées dans le cas où un libellé serait le même dans les 2 langues
					Object cell1;
					if(previousRow == null || !(aRow.sourceConcept.equals(previousRow.sourceConcept))) {
						cell1 = cbReader.readConceptBlock(
								aRow.sourceConcept,
								aRow.sourceConceptLabel,
								cbReader.computeConceptBlockId(aRow.sourceConcept, aRow.alignmentType+"-"+aRow.targetConcept),
								false
						);
					} else {
						cell1 = SchemaFactory.createStr(
								SchemaFactory.createStyledString(" ")
						);
					}
					// set a random UUID since we won't have that concept in overall output
					// ConceptBlock cb2 = cbReader.readConceptBlock(aRow.targetConcept, aRow.targetConceptLabel, UUID.randomUUID().toString(), false);
					ConceptBlock cb2 = SchemaFactory.createConceptBlock(
							UUID.randomUUID().toString(),
							aRow.targetConcept,
							SchemaFactory.createLabelLinkExternal(aRow.targetConcept, aRow.targetConceptLabel, null)
							);
					JAXBElement<StyledString> type = SchemaFactory.createStr(
								SchemaFactory.createStyledKey(aRow.alignmentType.substring(SKOS.NAMESPACE.length()))
					);
					JAXBElement<StyledString> targetScheme = SchemaFactory.createStr(
							SchemaFactory.createStyledString(aRow.targetSchemeLabel)
					);
					
					currentTable.getRow().add(SchemaFactory.createRow(cell1, targetScheme, type, cb2));
					
					// keep track of previous row
					previousRow = aRow;
				}
				
				// add last section to the document
				d.getSection().add(currentSection);
			}
		}		
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
		// KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?URI.create(args[1]):null);
		// document.setHeader(header);
		
		ConceptBlockReader cbr = new ConceptBlockReader(r);
		cbr.setLinkDestinationIdPrefix("alignId");
		AlignmentDisplayGenerator reader = new AlignmentDisplayGenerator(
				r,
				cbr,
				"alignId",
				new AlignmentDataHarvesterCachedLoader("/home/thomas/workspace/skosplay/alignCache", RDFFormat.RDFXML));
		reader.setSeparateByTargetScheme(false);
		BodyReader bodyReader = new BodyReader(reader);
		document.setBody(bodyReader.readBody(LANG, (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(document, new File("src/main/resources/translation-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.setDebug(true);
		printer.printToHtml(document, new File("display-test-alignment.html"), LANG);
		printer.printToPdf(document, new File("display-test-alignment.pdf"), LANG);
	}
	
}
