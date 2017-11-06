package fr.sparna.rdf.skos.xls2skos;

import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getCellValue;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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



public class Xls2SkosConverter {
	
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
	 * Internal storage of Models for each ConceptScheme URI
	 */
	private final Map<String, Model> csModels = new HashMap<>();
	
	/**
	 * Internal list of value generators
	 */
	protected final Map<String, ValueGeneratorIfc> valueGenerators = new HashMap<>();
	
	/**
	 * The prefixes declared in the file along with utility classes and default prefixes
	 */
	protected PrefixManager prefixManager = new PrefixManager();
	
	/**
	 * The workbook currently being processed, to ge references to fonts
	 */
	private transient Workbook workbook;
	
	public Xls2SkosConverter(ModelWriterIfc modelWriter, String lang) {
		
		this.modelWriter = modelWriter;
		this.lang = lang;
		
		// TODO : handle language declared on a per-colum basis
		
		// inScheme for additionnal inScheme information, if needed
		valueGenerators.put("skos:inScheme", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.IN_SCHEME, prefixManager), ","));
		// labels
		valueGenerators.put("skos:prefLabel", 		ValueGeneratorFactory.langLiteral(SKOS.PREF_LABEL));
		valueGenerators.put("skos:altLabel", 		ValueGeneratorFactory.langLiteral(SKOS.ALT_LABEL));
		valueGenerators.put("skos:hiddenLabel", 	ValueGeneratorFactory.langLiteral(SKOS.HIDDEN_LABEL));
		// notes
		valueGenerators.put("skos:definition", 		ValueGeneratorFactory.langLiteral(SKOS.DEFINITION));		
		valueGenerators.put("skos:editorialNote", 	ValueGeneratorFactory.langLiteral(SKOS.EDITORIAL_NOTE));
		valueGenerators.put("skos:historyNote", 	ValueGeneratorFactory.langLiteral(SKOS.HISTORY_NOTE));
		valueGenerators.put("skos:scopeNote", 		ValueGeneratorFactory.langLiteral(SKOS.SCOPE_NOTE));
		valueGenerators.put("skos:changeNote", 		ValueGeneratorFactory.langLiteral(SKOS.CHANGE_NOTE));
		valueGenerators.put("skos:example", 		ValueGeneratorFactory.langLiteral(SKOS.EXAMPLE));
		// notation
		valueGenerators.put("skos:notation", 		ValueGeneratorFactory.plainLiteral(SKOS.NOTATION));
		// semantic relations
		valueGenerators.put("skos:broader", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.BROADER, prefixManager), ","));
		valueGenerators.put("skos:narrower", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.NARROWER, prefixManager), ","));
		valueGenerators.put("skos:related", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.RELATED, prefixManager), ","));
		// mapping relations		
		valueGenerators.put("skos:exactMatch", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.EXACT_MATCH, prefixManager), ","));
		valueGenerators.put("skos:closeMatch", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.CLOSE_MATCH, prefixManager), ","));
		valueGenerators.put("skos:relatedMatch", 	ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.RELATED_MATCH, prefixManager), ","));
		valueGenerators.put("skos:broadMatch", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.BROAD_MATCH, prefixManager), ","));
		valueGenerators.put("skos:narrowMatch", 	ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.RELATED_MATCH, prefixManager), ","));
		// XL labels
		valueGenerators.put("skosxl:prefLabel", 	ValueGeneratorFactory.skosXlLabel(SKOSXL.PREF_LABEL, prefixManager));
		valueGenerators.put("skosxl:altLabel", 		ValueGeneratorFactory.skosXlLabel(SKOSXL.ALT_LABEL, prefixManager));
		valueGenerators.put("skosxl:hiddenLabel",	ValueGeneratorFactory.skosXlLabel(SKOSXL.HIDDEN_LABEL, prefixManager));
		valueGenerators.put("skosxl:literalForm", 	ValueGeneratorFactory.langLiteral(SKOSXL.LITERAL_FORM));
		// other concepts metadata
		valueGenerators.put("euvoc:status", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SimpleValueFactory.getInstance().createIRI("http://publications.europa.eu/ontology/euvoc#status"), prefixManager), ","));		
		valueGenerators.put("euvoc:startDate", 		ValueGeneratorFactory.dateLiteral(SimpleValueFactory.getInstance().createIRI("http://publications.europa.eu/ontology/euvoc#startDate")));
		valueGenerators.put("euvoc:endDate", 		ValueGeneratorFactory.dateLiteral(SimpleValueFactory.getInstance().createIRI("http://publications.europa.eu/ontology/euvoc#endDate")));
		valueGenerators.put("dct:created", 			ValueGeneratorFactory.dateLiteral(DCTERMS.CREATED));
		valueGenerators.put("dct:modified", 		ValueGeneratorFactory.dateLiteral(DCTERMS.MODIFIED));
		// a source can be a literal or a URI
		valueGenerators.put("dct:source", 			ValueGeneratorFactory.split(ValueGeneratorFactory.resourceOrLiteral(new ColumnHeaderParser(prefixManager).parse("dct:source"), prefixManager), ","));
		// dct metadata for the ConceptScheme
		valueGenerators.put("dct:title", 			ValueGeneratorFactory.langLiteral(DCTERMS.TITLE));
		valueGenerators.put("dct:description", 		ValueGeneratorFactory.langLiteral(DCTERMS.DESCRIPTION));
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
			
			// store the workbook reference
			this.workbook = workbook;
			
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
		
		RdfizableSheet rdfizableSheet = new RdfizableSheet(sheet, this);
		
		if(!rdfizableSheet.canRDFize()) {
			log.debug(sheet.getSheetName()+" : Ignoring sheet.");
			return null;
		} else {
			log.debug("Processing sheet: " + sheet.getSheetName());
		}
		
		// read the prefixes
		this.prefixManager.register(rdfizableSheet.readPrefixes());
		
		// initialize target Model
		Model model = new LinkedHashModelFactory().createEmptyModel();
		SimpleValueFactory svf = SimpleValueFactory.getInstance();

		// read the concept scheme or graph URI
		String csUri = prefixManager.uri(rdfizableSheet.getSchemeOrGraph(), true);
		
		// if the URI was already processed, this is an exception
		if(this.csModels.containsKey(csUri)) {
			log.debug("Duplicate graph declaration found: " + csUri + " (declared in more than one sheet)");
		}
		
		Resource csResource = svf.createIRI(csUri);	
		
		// read the title row index
		int headerRowIndex = rdfizableSheet.getTitleRowIndex();		
		// si la ligne d'entete n'a pas été trouvée, on ne génère que le ConceptScheme
		if(headerRowIndex == 1) {
			log.info("Could not find header row index in sheet "+sheet.getSheetName());
			// we are assuming a header row index of 10 to be able to read at least the ConceptScheme header, even with no concept in it.
			headerRowIndex = 10;
		}
		
		// read the properties on the concept scheme by reading the top rows
		ColumnHeaderParser headerParser = new ColumnHeaderParser(prefixManager);
		for (int rowIndex = 1; rowIndex <= headerRowIndex; rowIndex++) {
			if(sheet.getRow(rowIndex) != null) {
				String key = getCellValue(sheet.getRow(rowIndex).getCell(0));
				String value = getCellValue(sheet.getRow(rowIndex).getCell(1));
				
				ColumnHeader header = headerParser.parse(key);
				if(
						header != null
						&&
						StringUtils.isNotBlank(value)
				) {
					ValueGeneratorIfc valueGenerator = null;
					if(valueGenerators.containsKey(header.getDeclaredProperty())) {
						valueGenerator = valueGenerators.get(header.getDeclaredProperty());
					} else if(header.getProperty() != null) {
						valueGenerator = ValueGeneratorFactory.resourceOrLiteral(
								header,
								prefixManager
						);
					}
					
					if(valueGenerator != null) {
						System.out.println("Adding value on "+csResource);
						valueGenerator.addValue(
								model,
								csResource,
								value,
								header.getLanguage().orElse(this.lang)
						);
					}
				}
			}
		}		

		// read the column names from the header row
		List<ColumnHeader> columnNames = rdfizableSheet.getColumnHeaders(headerRowIndex);
		
		log.debug("Processing column headers: ");
		for (ColumnHeader columnHeader : columnNames) {
			log.debug(columnHeader.toString());
		}
		
		// read the rows after the header and process each row
		for (int rowIndex = (headerRowIndex + 1); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row r = sheet.getRow(rowIndex);
			if(r != null) {
				handleRow(model, columnNames, prefixManager, r);
			}
		}
		
		// post-process the model to add inverses, hasTopConcepts, etc.
		postProcess(model, csResource);		
		
		// Turn the model to SKOS-XL
		if(this.generateXl || this.generateXlDefinitions) {
			xlify(model);
		}
		
		// writes the resulting Model
		modelWriter.saveGraphModel(csUri, model, prefixManager.getPrefixes());
		// stores the Model
		csModels.put(csUri, model);
		return model;
	}
	
	private void postProcess(Model model, Resource csResource) {
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
		
		if(!model.filter(csResource, RDF.TYPE, SKOS.COLLECTION).isEmpty()) {
			// if the header object was explicitely typed as skos:Collectio, then add skos:members to every included skos:Concept
			model.filter(null, RDF.TYPE, SKOS.CONCEPT).forEach(
					s -> { model.add(csResource, SKOS.MEMBER, ((Resource)s.getSubject())); }
			);
		} else {
			// no explicit type given in header : either this is a ConceptScheme (default)
			// or a class, in which case no post-processing is done 
			
			// add a skos:inScheme to every skos:Concept or skos:Collection or skos:OrderedCollection that was created
			model.filter(null, RDF.TYPE, SKOS.CONCEPT).forEach(
					s -> { model.add(((Resource)s.getSubject()), SKOS.IN_SCHEME, csResource); }
			);
			model.filter(null, RDF.TYPE, SKOS.COLLECTION).forEach(
					s -> { model.add(((Resource)s.getSubject()), SKOS.IN_SCHEME, csResource); }
			);
			model.filter(null, RDF.TYPE, SKOS.ORDERED_COLLECTION).forEach(
					s -> { model.add(((Resource)s.getSubject()), SKOS.IN_SCHEME, csResource); }
			);	
			
			// if at least one skos:Concept was generated, 
			// or if no entry was generated at all, declare the URI in B1 as a ConceptScheme
			if(
					!model.filter(null, RDF.TYPE, SKOS.CONCEPT).isEmpty()
					||
					model.filter(null, RDF.TYPE, null).isEmpty()
			) {
				model.add(csResource, RDF.TYPE, SKOS.CONCEPT_SCHEME);
			}
			
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
		}
		
	}
	
	private Model xlify(Model m) {
		log.debug("Xlifying Model...");
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		
		try(RepositoryConnection c = r.getConnection()) {
			c.add(m);
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
			
			// re-export to a new Model
			m.clear();
			c.export(new AbstractRDFHandler() {
				public void handleStatement(Statement st) throws RDFHandlerException {
					m.add(st);
				}			
			});
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}
		
		return m;
		
	}

	private Resource handleRow(Model model, List<ColumnHeader> columnHeaders, PrefixManager prefixManager, Row row) {
		RowBuilder rowBuilder = null;
		for (int colIndex = 0; colIndex < columnHeaders.size(); colIndex++) {
			ColumnHeader header = columnHeaders.get(colIndex);
			
			Cell c = row.getCell(colIndex);			
			String value = getCellValue(c);
			// if it is the first column...
			if (null == rowBuilder) {
				// if the value of the first column is empty, or is striked through, skip the whole row
				if (StringUtils.isBlank(value) || this.workbook.getFontAt(c.getCellStyle().getFontIndex()).getStrikeout()) {
					return null;
				}
				// create the RowBuilder with the URI in the first column
				rowBuilder = new RowBuilder(model, prefixManager.uri(value, true));
				continue;
			}
			
			// process the cell for each subsequent columns after the first one
			if (StringUtils.isNotBlank(value)) {
				if(this.workbook.getFontAt(c.getCellStyle().getFontIndex()).getStrikeout()) {
					// skip the cell if it is striked out
					continue;
				}
				
				ValueGeneratorIfc valueGenerator = valueGenerators.get(header.getDeclaredProperty());
				
				// if this is not one of the known processor, but the property is known, then defaults to a generic processor
				// also defaults to a generic processor if a custom datatype is declared on the property
				if(
						(
								valueGenerator == null
								||
								header.getDatatype().isPresent()
						)
						&&
						header.getProperty() != null
				) {
					valueGenerator = ValueGeneratorFactory.resourceOrLiteral(
							header,
							prefixManager
					);
				}
				
				if(header.getParameters().get(ColumnHeader.PARAMETER_SEPARATOR) != null) {
					valueGenerator = ValueGeneratorFactory.split(valueGenerator, header.getParameters().get(ColumnHeader.PARAMETER_SEPARATOR));
					// use a default comma separator for cells that contain URI references
				} else if(
					!header.getDatatype().isPresent()
					&&
					!header.getLanguage().isPresent()
					&&
					(value.startsWith("http") || prefixManager.usesKnownPrefix(value.trim()))
				) {
					valueGenerator = ValueGeneratorFactory.split(valueGenerator, ",");
				}
				
				// if a value generator was successfully generated, then process the value
				if(valueGenerator != null) {
					try {
						rowBuilder.processCell(
								valueGenerator,
								value,
								header.getLanguage().orElse(this.lang)
						);
					} catch (Exception e) {
						throw new Xls2SkosException(e, "Convert exception while processing value '"+value+"', row "+(row.getRowNum()+1)+" in sheet "+row.getSheet().getSheetName()+". Message is : "+e.getMessage());
					}
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
			// set the current subject to the conceptResource by default
			subject = conceptResource;
		}

		public void processCell(ValueGeneratorIfc valueGenerator, String value, String language) {
			// if the column is unknown, ignore it
			if(valueGenerator != null) {				
				Resource newResource = valueGenerator.addValue(model, subject, value, language);
				if (null != newResource) {
					// change the focus to the new resource in the case of xl labels
					// so that subsequent columns are added on that resource
					subject = newResource;
				}
			}
		}
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
	
	public Map<String, Model> getCsModels() {
		return csModels;
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
		
		Xls2SkosConverter me = new Xls2SkosConverter(writer, "fr");
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
		Xls2SkosConverter converter = new Xls2SkosConverter(modelWriter, lang);
		converter.setGenerateXl(false);
		converter.setGenerateXlDefinitions(false);
		converter.processInputStream(input);
	}
	
	

}
