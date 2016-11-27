package fr.sparna.rdf.skos.xls2skos;

import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getCellValue;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Xls2Skos {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Language used to generate the literals
	 */
	protected String lang;
	
	/**
	 * Name of sheets to ignore when generating SKOS
	 */
	protected List<String> sheetsToIgnore = new ArrayList<String>();
	
	/**
	 * Whether to automatically generates SKOS-XL labels
	 */
	protected boolean generateXl = true;
	
	/**
	 * Whether to automatically reify definitions (for Vocbench)
	 */
	protected boolean generateXlDefinitions = true;
	
	/**
	 * Object capable of serializing the resulting models
	 */
	protected ModelWriterIfc modelWriter;

	/**
	 * Internal storage of Models for each sheet name
	 */
	private final Map<String, Model> csModels = new HashMap<>();
	
	/**
	 * Internal list of value generators
	 */
	private final Map<String, ValueGeneratorIfc> valueGenerators = new HashMap<>();
	
	private PrefixManager prefixManager = new PrefixManager();
	
	public Xls2Skos(ModelWriterIfc modelWriter, String lang) {
		
		this.modelWriter = modelWriter;;
		this.lang = lang;
		
		// inScheme for additionnal inScheme information, if needed
		valueGenerators.put("skos:inScheme", 		ValueGeneratorFactory.resources(SKOS.IN_SCHEME, ',', prefixManager));
		// labels
		valueGenerators.put("skos:prefLabel", 		ValueGeneratorFactory.langLiteral(SKOS.PREF_LABEL, this.lang));
		valueGenerators.put("skos:altLabel", 		ValueGeneratorFactory.langLiteral(SKOS.ALT_LABEL, this.lang));
		valueGenerators.put("skos:hiddenLabel", 	ValueGeneratorFactory.langLiteral(SKOS.HIDDEN_LABEL, this.lang));
		// notes
		valueGenerators.put("skos:definition", 		ValueGeneratorFactory.langLiteral(SKOS.DEFINITION, this.lang));		
		valueGenerators.put("skos:editorialNote", 	ValueGeneratorFactory.langLiteral(SKOS.EDITORIAL_NOTE, this.lang));
		valueGenerators.put("skos:historyNote", 	ValueGeneratorFactory.langLiteral(SKOS.HISTORY_NOTE, this.lang));
		valueGenerators.put("skos:scopeNote", 		ValueGeneratorFactory.langLiteral(SKOS.SCOPE_NOTE, this.lang));
		valueGenerators.put("skos:changeNote", 		ValueGeneratorFactory.langLiteral(SKOS.CHANGE_NOTE, this.lang));
		valueGenerators.put("skos:example", 		ValueGeneratorFactory.langLiteral(SKOS.EXAMPLE, this.lang));
		// notation
		valueGenerators.put("skos:notation", 		ValueGeneratorFactory.plainLiteral(SKOS.NOTATION));
		// semantic relations
		valueGenerators.put("skos:broader", 		ValueGeneratorFactory.resources(SKOS.BROADER, ',', prefixManager));
		valueGenerators.put("skos:narrower", 		ValueGeneratorFactory.resources(SKOS.NARROWER, ',', prefixManager));
		valueGenerators.put("skos:related", 		ValueGeneratorFactory.resources(SKOS.RELATED, ',', prefixManager));
		// mapping relations		
		valueGenerators.put("skos:exactMatch", 		ValueGeneratorFactory.resources(SKOS.EXACT_MATCH, ',', prefixManager));
		valueGenerators.put("skos:closeMatch", 		ValueGeneratorFactory.resources(SKOS.CLOSE_MATCH, ',', prefixManager));
		valueGenerators.put("skos:relatedMatch", 	ValueGeneratorFactory.resources(SKOS.RELATED_MATCH, ',', prefixManager));
		valueGenerators.put("skos:broadMatch", 		ValueGeneratorFactory.resources(SKOS.BROAD_MATCH, ',', prefixManager));
		valueGenerators.put("skos:narrowMatch", 	ValueGeneratorFactory.resources(SKOS.RELATED_MATCH, ',', prefixManager));
		// XL labels
		valueGenerators.put("skosxl:prefLabel", 	ValueGeneratorFactory.skosXlLabel(SKOSXL.PREF_LABEL, prefixManager));
		valueGenerators.put("skosxl:altLabel", 		ValueGeneratorFactory.skosXlLabel(SKOSXL.ALT_LABEL, prefixManager));
		valueGenerators.put("skosxl:hiddenLabel",	ValueGeneratorFactory.skosXlLabel(SKOSXL.HIDDEN_LABEL, prefixManager));
		valueGenerators.put("skosxl:literalForm", 	ValueGeneratorFactory.langLiteral(SKOSXL.LITERAL_FORM, this.lang));
		// other concepts metadata
		valueGenerators.put("euvoc:status", 		ValueGeneratorFactory.resources(SimpleValueFactory.getInstance().createIRI("http://publications.europa.eu/ontology/euvoc#status"), ',', prefixManager));		
		valueGenerators.put("euvoc:startDate", 		ValueGeneratorFactory.dateLiteral(SimpleValueFactory.getInstance().createIRI("http://eurovoc.europa.eu/schema#startDate")));
		valueGenerators.put("euvoc:endDate", 		ValueGeneratorFactory.dateLiteral(SimpleValueFactory.getInstance().createIRI("http://eurovoc.europa.eu/schema#endDate")));
		valueGenerators.put("dct:created", 			ValueGeneratorFactory.dateLiteral(DCTERMS.CREATED));
		valueGenerators.put("dct:modified", 		ValueGeneratorFactory.dateLiteral(DCTERMS.MODIFIED));
		// a source can be a literal or a URI
		valueGenerators.put("dct:source", 			ValueGeneratorFactory.resourcesOrLiteral(DCTERMS.SOURCE, ',', this.lang, prefixManager, false));
		// dct metadata for the ConceptScheme
		valueGenerators.put("dct:title", 			ValueGeneratorFactory.langLiteral(DCTERMS.TITLE, this.lang));
		valueGenerators.put("dct:description", 		ValueGeneratorFactory.langLiteral(DCTERMS.DESCRIPTION, this.lang));
	}

	/**
	 * Parses a File into a Workbook, and defer processing to processWorkbook(Workbook workbook)
	 * @param input
	 * @return
	 */
	public List<Model> processFile(File input) {
		try {
			Workbook workbook = WorkbookFactory.create(input);
			return processWorkbook(workbook);
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}			
	}
	
	/**
	 * Parses an InputStream into a Workbook, and defer processing to processWorkbook(Workbook workbook)
	 * @param input
	 * @return
	 */
	public List<Model> processInputStream(InputStream input) {
		try {
			Workbook workbook = WorkbookFactory.create(input);
			return processWorkbook(workbook);
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}			
	}
	
	/**
	 * Process an Excel sheet.
	 * 
	 * @param workbook
	 * @return
	 */
	public List<Model> processWorkbook(Workbook workbook) {

		List<Model> models = new ArrayList<>();

		try {
			
			// notify begin
			modelWriter.beginWorkbook();
			
			// for every sheet...
			for (Sheet sheet : workbook) {

				// if the sheet should not be ignored...
				if (Arrays.stream(sheetsToIgnore.toArray(new String[] {})).filter(
						name -> sheet.getSheetName().equalsIgnoreCase(name)
					).findAny().isPresent()
				) {
					log.debug("Skipping sheet: " + sheet.getSheetName());
					continue;
				}

				// process the sheet, possibly returning null
				// if load(sheet) returns null, the sheet was ignored
				Model model = processSheet(sheet);
				models.add(model);
			}
			
			// notify end
			modelWriter.endWorkbook();
			
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}

		return models;
	}

	/**
	 * Process a single sheet and returns corresponding Model
	 * 
	 * @param sheet
	 * @return
	 */
	private Model processSheet(Sheet sheet) {
		
		if(sheet.getRow(0) == null) {
			log.debug(sheet.getSheetName()+" : First row is empty, ignoring sheet.");
			return null;
		}
		
		// read the prefixes in the top 20 rows		
		for (int rowIndex = 1; rowIndex <= 20; rowIndex++) {
			if(sheet.getRow(rowIndex) != null) {
				String prefixKeyword = getCellValue(sheet.getRow(rowIndex).getCell(0));
				// if we have the "prefix" keyword...
				// note : we add a null check here because there are problems with some sheets
				if(prefixKeyword != null && prefixKeyword.toUpperCase().startsWith("PREFIX")) {
					// and we have the prefix and namespaces defined...
					String prefix = getCellValue(sheet.getRow(rowIndex).getCell(1));
					if(StringUtils.isNotBlank(prefix)) {
						if(prefix.charAt(prefix.length()-1) == ':') {
							prefix = prefix.substring(0, prefix.length()-1);
						}
						String namespace = getCellValue(sheet.getRow(rowIndex).getCell(2));
						if(StringUtils.isNotBlank(namespace)) {
							prefixManager.register(prefix, namespace);
						}
					}
				}
			}
		}
		
		String uri = getCellValue(sheet.getRow(0).getCell(1));
		
		if(StringUtils.isBlank(uri)) {
			log.debug(sheet.getSheetName()+" : B1 is empty, ignoring sheet.");
			return null;
		} else {
			String fixedUri = prefixManager.uri(uri, true);
			try {
				new URI(fixedUri);
			} catch (URISyntaxException e) {
				log.debug(sheet.getSheetName()+" : B1 is not a valid URI ('"+uri+"'), ignoring sheet");
				return null;
			} catch (NullPointerException e) {
				log.debug("Cannot build a valid URI from '"+uri+"', ignoring sheet");
				return null;
			}
		}		
		
		log.debug("Processing sheet: " + sheet.getSheetName());
		
		Model model = new LinkedHashModelFactory().createEmptyModel();
		SimpleValueFactory svf = SimpleValueFactory.getInstance();

		String csUri = prefixManager.uri(uri, true);
		
		// if the URI was already processed, this is an exception
		if(this.csModels.containsKey(csUri)) {
			throw new Xls2SkosException("Duplicate ConceptScheme found : "+csUri+" - make sure it is declared only once in the file.");
		}
		
		Resource csResource = svf.createIRI(csUri);	
		
		int headerRowIndex = 1;
		for (int rowIndex = headerRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			// test if we find a header in columns 2 and 3, this indicates the header line
			if(sheet.getRow(rowIndex) != null) {
				String valueColumnB = getCellValue(sheet.getRow(rowIndex).getCell(1));
				String valueColumnC = getCellValue(sheet.getRow(rowIndex).getCell(2));
				if(
							(
									valueGenerators.containsKey(valueColumnB)
									||
									prefixManager.expand(valueColumnB) != null
							)
						&&
							(
									valueGenerators.containsKey(valueColumnC)
									||
									prefixManager.expand(valueColumnC) != null
							)
				) {
					headerRowIndex = rowIndex;
					break;
				}
			}
		}
		
		// si la ligne d'entete n'a pas été trouvée, on ne génère que le ConceptScheme
		if(headerRowIndex == 1) {
			log.info("Could not find header row index in sheet "+sheet.getSheetName());
			// we are assuming a header row index of 10 to be able to read at least the ConceptScheme header, even with no concept in it.
			headerRowIndex = 10;
		}
		
		// read the properties on the concept scheme by reading the top rows
		for (int rowIndex = 1; rowIndex <= headerRowIndex; rowIndex++) {
			if(sheet.getRow(rowIndex) != null) {
				String key = getCellValue(sheet.getRow(rowIndex).getCell(0));
				String value = getCellValue(sheet.getRow(rowIndex).getCell(1));
				
				ColumnHeader header = ColumnHeader.parse(key, prefixManager);
				if(
						header != null
						&&
						StringUtils.isNotBlank(value)
						&&
						valueGenerators.containsKey(header.getProperty())
				) {
					valueGenerators.get(header.getProperty()).addValue(
							model,
							csResource,
							value,
							header.getLanguage().orElse(lang),
							header.getDatatype().orElse(null)
					);
				}
			}
		}		

		// read the column names from the header row
		List<ColumnHeader> columnNames = parseColumnNames(sheet, headerRowIndex);
		
		// read the rows after the header and process each row
		for (int rowIndex = (headerRowIndex + 1); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row r = sheet.getRow(rowIndex);
			if(r != null) {
				handleRow(model, columnNames, prefixManager, r);
			}
		}
		
		// add a skos:inScheme to every skos:Concept or skos:Collection that was created
		model.filter(null, RDF.TYPE, SKOS.CONCEPT).forEach(
				s -> { model.add(((Resource)s.getSubject()), SKOS.IN_SCHEME, csResource); }
		);
		model.filter(null, RDF.TYPE, SKOS.COLLECTION).forEach(
				s -> { model.add(((Resource)s.getSubject()), SKOS.IN_SCHEME, csResource); }
		);
		
		// if at least one skos:inScheme was added, declare the URI in B1 as a ConceptScheme
		if(!model.filter(null, RDF.TYPE, SKOS.CONCEPT).isEmpty()) {
			model.add(csResource, RDF.TYPE, SKOS.CONCEPT_SCHEME);
		}			
		
		// add the inverse broaders and narrowers
		model.filter(null, SKOS.BROADER, null).forEach(
				s -> {
					model.add(((Resource)s.getObject()), SKOS.NARROWER, s.getSubject());
				}
		);
		model.filter(null, SKOS.NARROWER, null).forEach(
				s -> {
					model.add(((Resource)s.getObject()), SKOS.BROADER, s.getSubject());
				}
		);
		
		// add skos:topConceptOf and skos:hasTopConcept for each skos:Concept without broader/narrower
		model.filter(null, RDF.TYPE, SKOS.CONCEPT).subjects().forEach(
				concept -> {
					if(
							model.filter(concept, SKOS.BROADER, null).isEmpty()
							&&
							model.filter(null, SKOS.NARROWER, concept).isEmpty()
					) {
						model.add(csResource, SKOS.HAS_TOP_CONCEPT, concept);
						model.add(concept, SKOS.TOP_CONCEPT_OF, csResource);
					}
				}
		);
		
		if(this.generateXl || this.generateXlDefinitions) {
			xlify(model);
		}
		
		// writes the resulting Model
		modelWriter.saveGraphModel(csUri, model);
		// stores the Model
		csModels.put(csUri, model);
		return model;
	}
	
	private Model xlify(Model m) {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		RepositoryConnection c = r.getConnection();
		
		c.add(m);
		
		try {
			if(this.generateXl) {
				final List<String> SKOS2SKOSXL_URI_RULESET = Arrays.asList(new String[] { 
						"skos2skosxl/S55-S56-S57-URIs.ru"
				});			
				
				for (String aString : SKOS2SKOSXL_URI_RULESET) {
					// Load SPARQL query definition
			        InputStream src = this.getClass().getResourceAsStream(aString);		        
			        String sparql =  IOUtils.toString(src);
					
					Update u = c.prepareUpdate(sparql);
					u.execute();
				}
			}
			
			if(this.generateXlDefinitions) {
				final List<String> SKOS2SKOSXL_NOTES_URI_RULESET = Arrays.asList(new String[] { 
						"skos2skosxl/S16-URIs.ru"
				});
				
				for (String aString : SKOS2SKOSXL_NOTES_URI_RULESET) {
					// Load SPARQL query definition
			        InputStream src = this.getClass().getResourceAsStream(aString);	
			        String sparql =  IOUtils.toString(src);
					Update u = c.prepareUpdate(sparql);
					u.execute();
				}
			}
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}
		
		// re-export to a new Model
		m.clear();
		c.export(new AbstractRDFHandler() {
			public void handleStatement(Statement st) throws RDFHandlerException {
				m.add(st);
			}			
		});
		
		c.close();
		
		return m;
		
	}

	private Resource handleRow(Model model, List<ColumnHeader> columnNames, PrefixManager prefixManager, Row row) {
		RowBuilder rowBuilder = null;
		for (int colIndex = 0; colIndex < columnNames.size(); colIndex++) {
			String value = getCellValue(row.getCell(colIndex));
			if (null == rowBuilder) {
				if (StringUtils.isBlank(value)) {
					return null;
				}
				// create the RowBuilder with the URI in the first column
				rowBuilder = new RowBuilder(model, prefixManager.uri(value, true));
				continue;
			}
			
			// process the cell for each subsequent columns after the first one
			if (StringUtils.isNotBlank(value)) {	
				ValueGeneratorIfc valueAdder = valueGenerators.get(columnNames.get(colIndex).getProperty());
				
				// if this is not one of the known processor, but the prefix is known, then defaults to a generic processor
				if(valueAdder == null && prefixManager.expand(columnNames.get(colIndex).getProperty()) != null) {
					valueAdder = ValueGeneratorFactory.resourcesOrLiteral(
							SimpleValueFactory.getInstance().createIRI(prefixManager.expand(columnNames.get(colIndex).getProperty())),
							',',
							columnNames.get(colIndex).getLanguage().orElse(lang),
							prefixManager,
							columnNames.get(colIndex).isInverse()
					);
				}
				
				if(valueAdder != null) {
					rowBuilder.addCell(
							valueAdder,
							value,
							columnNames.get(colIndex).getLanguage().orElse(lang),
							columnNames.get(colIndex).getDatatype().orElse(null)
					);
				}
			}
		}
		
		// if, after row processing, no rdf:type was generated, then we consider the row to be a skos:Concept
		// this allows to generate something else that skos:Concept
		if(rowBuilder != null && !model.contains(rowBuilder.conceptResource, RDF.TYPE, null)) {
			model.add(rowBuilder.conceptResource, RDF.TYPE, SKOS.CONCEPT);
		}
		
		return null == rowBuilder ? null : rowBuilder.conceptResource;
	}

	private class RowBuilder {
		private final Model model;
		private final Resource conceptResource;
		private Resource subject;

		public RowBuilder(Model model, String uri) {
			this.model = model;
			conceptResource = SimpleValueFactory.getInstance().createIRI(uri);
			// model.add(SimpleValueFactory.getInstance().createURI(uri), RDF.TYPE, SKOS.CONCEPT);
			// set the current subject to the conceptResource by default
			subject = conceptResource;
		}

		public void addCell(ValueGeneratorIfc valueAdder, String value, String language, IRI datatype) {
			// if the column is unknown, ignore it
			if(valueAdder != null) {
				Resource newResource = valueAdder.addValue(model, subject, value, language, datatype);
				if (null != newResource) {
					// change the focus to the new resource in the case of xl labels
					// so that subsequent columns are added on that resource
					subject = newResource;
				}
			}
		}
	}
	
	public List<ColumnHeader> parseColumnNames(Sheet worksheet, int rowNumber) {
		List<ColumnHeader> columnNames = new ArrayList<>();
		Row row = worksheet.getRow(rowNumber);
		if(row != null) {
			for (int i = 0; true; i++) {
				Cell cell = row.getCell(i);
				if (null == cell) break;
				String columnName = cell.getStringCellValue();
				if (StringUtils.isBlank(columnName)) {
					break;
				}
				columnNames.add(ColumnHeader.parse(columnName, prefixManager));
			}
		}
		return columnNames;
	}

	public List<String> getSheetsToIgnore() {
		return sheetsToIgnore;
	}

	public void setSheetsToIgnore(List<String> sheetsToIgnore) {
		this.sheetsToIgnore = sheetsToIgnore;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isGenerateXl() {
		return generateXl;
	}

	public void setGenerateXl(boolean generateXl) {
		this.generateXl = generateXl;
	}

	public boolean isGenerateXlDefinitions() {
		return generateXlDefinitions;
	}

	public void setGenerateXlDefinitions(boolean generateXlDefinitions) {
		this.generateXlDefinitions = generateXlDefinitions;
	}
	
	public static void main(String[] args) throws Exception {
		
		// quick and dirty Log4J config
		org.apache.log4j.BasicConfigurator.configure();
		org.apache.log4j.Logger.getLogger("org.eclipse.rdf4j").setLevel(Level.INFO);
		
		
		// Method 1 : save each scheme to a separate directory
//		DirectoryModelWriter writer = new DirectoryModelWriter(new File("/home/thomas/sparna/00-Clients/Luxembourg/02-Migration/controlled-vocabularies-xls2skos/cv-from-xls2skos"));
//		writer.setSaveGraphFile(true);
//		writer.setGraphSuffix("/graph");
		
		// Method 2 : save everything to a single SKOS file
		// OutputStreamModelWriter ms = new OutputStreamModelWriter(new File("/home/thomas/controlled-vocabularies.ttl"));
		
		// Method 3 : save each scheme to a separate entry in a ZIP file.
		ZipOutputStreamModelWriter writer = new ZipOutputStreamModelWriter(new File("/home/thomas/sparna/00-Clients/Luxembourg/02-Migration/controlled-vocabularies-xls2skos/cv-from-xls2skos.zip"));
		writer.setSaveGraphFile(true);
		writer.setGraphSuffix("/graph");
		
		Xls2Skos me = new Xls2Skos(writer, "fr");
		me.setGenerateXl(false);
		me.setGenerateXlDefinitions(false);
		// me.loadAllToFile(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/fr.sparna/rdf/skos/xls2skos/src/test/resources/test-excel-saved-from-libreoffice.xlsx"));
		// me.loadAllToFile(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/fr.sparna/rdf/skos/xls2skos/src/test/resources/test-libreoffice.ods"));
		me.processFile(new File("/home/thomas/sparna/00-Clients/Luxembourg/02-Migration/controlled-vocabularies-xls2skos/jolux-controlled-voc-travail-20161026-recup.xlsx"));
		// me.processFile(new File("/home/thomas/sparna/00-Clients/Luxembourg/02-Migration/jolux-controlled-voc-travail-20161012.xlsx"));
	}
	
	
	public static void runLikeInSkosPlay(
			InputStream input,
			OutputStream output,
			String lang
	) throws Exception {
		OutputStreamModelWriter modelWriter = new OutputStreamModelWriter(output);
		Xls2Skos converter = new Xls2Skos(modelWriter, lang);
		converter.setGenerateXl(false);
		converter.setGenerateXlDefinitions(false);
		converter.processInputStream(input);
	}

}
