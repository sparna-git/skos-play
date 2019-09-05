package fr.sparna.rdf.skos.xls2skos;

import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getCellValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.CellReference;
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
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
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

import ch.qos.logback.classic.BasicConfigurator;



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
	 * List of identifiers of all the graphs / concept schemes converted
	 */
	private final List<String> convertedVocabularyIdentifiers = new ArrayList<String>();
	
	/**
	 * Internal list of value generators
	 */
	protected final Map<String, ValueGeneratorIfc> valueGenerators = new HashMap<>();
	
	/**
	 * The prefixes declared in the file along with utility classes and default prefixes
	 */
	protected PrefixManager prefixManager = new PrefixManager();
	
	/**
	 * The workbook currently being processed, to get references to fonts
	 */
	private transient Workbook workbook;
	
	/**
	 * Global Repository containing all the converted data from all sheets, useful for reconciling values
	 */
	private transient Repository globalRepository = new SailRepository(new MemoryStore());
	
	/**
	 * Supporting Repository containing external data on which to reconcile values
	 */
	private transient Repository supportRepository = null;
	
	/**
	 * Whether to apply post processings on the RDF produced from the sheets
	 */
	private boolean applyPostProcessings = true;
	
	
	
	public Xls2SkosConverter(ModelWriterIfc modelWriter, String lang) {
		
		this.globalRepository.initialize();
		this.modelWriter = modelWriter;
		this.lang = lang;
		
//		// inScheme for additionnal inScheme information, if needed
//		valueGenerators.put("skos:inScheme", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.IN_SCHEME, prefixManager), ","));
//		// labels
//		valueGenerators.put("skos:prefLabel", 		ValueGeneratorFactory.langLiteral(SKOS.PREF_LABEL));
//		valueGenerators.put("skos:altLabel", 		ValueGeneratorFactory.langLiteral(SKOS.ALT_LABEL));
//		valueGenerators.put("skos:hiddenLabel", 	ValueGeneratorFactory.langLiteral(SKOS.HIDDEN_LABEL));
//		// notes
//		valueGenerators.put("skos:definition", 		ValueGeneratorFactory.langLiteral(SKOS.DEFINITION));		
//		valueGenerators.put("skos:editorialNote", 	ValueGeneratorFactory.langLiteral(SKOS.EDITORIAL_NOTE));
//		valueGenerators.put("skos:historyNote", 	ValueGeneratorFactory.langLiteral(SKOS.HISTORY_NOTE));
//		valueGenerators.put("skos:scopeNote", 		ValueGeneratorFactory.langLiteral(SKOS.SCOPE_NOTE));
//		valueGenerators.put("skos:changeNote", 		ValueGeneratorFactory.langLiteral(SKOS.CHANGE_NOTE));
//		valueGenerators.put("skos:example", 		ValueGeneratorFactory.langLiteral(SKOS.EXAMPLE));
//		// notation
//		valueGenerators.put("skos:notation", 		ValueGeneratorFactory.plainLiteral(SKOS.NOTATION));
//		// semantic relations
//		valueGenerators.put("skos:broader", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.BROADER, prefixManager), ","));
//		valueGenerators.put("skos:narrower", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.NARROWER, prefixManager), ","));
//		valueGenerators.put("skos:related", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.RELATED, prefixManager), ","));
//		// mapping relations		
//		valueGenerators.put("skos:exactMatch", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.EXACT_MATCH, prefixManager), ","));
//		valueGenerators.put("skos:closeMatch", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.CLOSE_MATCH, prefixManager), ","));
//		valueGenerators.put("skos:relatedMatch", 	ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.RELATED_MATCH, prefixManager), ","));
//		valueGenerators.put("skos:broadMatch", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.BROAD_MATCH, prefixManager), ","));
//		valueGenerators.put("skos:narrowMatch", 	ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SKOS.RELATED_MATCH, prefixManager), ","));
//
//		// other concepts metadata
//		valueGenerators.put("euvoc:status", 		ValueGeneratorFactory.split(ValueGeneratorFactory.resource(SimpleValueFactory.getInstance().createIRI("http://publications.europa.eu/ontology/euvoc#status"), prefixManager), ","));
//		// a source can be a literal or a URI
//		valueGenerators.put("dct:source", 			ValueGeneratorFactory.split(ValueGeneratorFactory.resourceOrLiteral(new ColumnHeaderParser(prefixManager).parse("dct:source", (short)-1), prefixManager), ","));
//		// dct metadata for the ConceptScheme
//		valueGenerators.put("dct:title", 			ValueGeneratorFactory.langLiteral(DCTERMS.TITLE));
//		valueGenerators.put("dct:description", 		ValueGeneratorFactory.langLiteral(DCTERMS.DESCRIPTION));
	}

	/**
	 * Parses a File into a Workbook, and defer processing to processWorkbook(Workbook workbook)
	 * @param input
	 * @return
	 */
	public List<Model> processFile(File input) {
		try {
			log.info("Converting file "+input.getAbsolutePath()+"...");
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
				try(RepositoryConnection connection = this.globalRepository.getConnection()) {
					connection.add(model);
				}
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
		
		// initialize target Model
		Model model = new LinkedHashModelFactory().createEmptyModel();
		SimpleValueFactory svf = SimpleValueFactory.getInstance();
		
		RdfizableSheet rdfizableSheet = new RdfizableSheet(sheet, this);
		
		if(!rdfizableSheet.canRDFize()) {
			log.debug(sheet.getSheetName()+" : Ignoring sheet.");
			return model;
		} else {
			log.debug("Processing sheet: " + sheet.getSheetName());
		}
		
		// read the prefixes
		this.prefixManager.register(rdfizableSheet.readPrefixes());

		// read the concept scheme or graph URI
		String csUri = prefixManager.uri(rdfizableSheet.getSchemeOrGraph(), true);
		
		// if the URI was already processed, output a warning (this is a possible case)
		if(this.convertedVocabularyIdentifiers.contains(csUri)) {
			log.debug("Duplicate graph declaration found: " + csUri + " (declared in more than one sheet)");
		}
		
		Resource csResource = svf.createIRI(csUri);	
		
		// read the title row index
		int headerRowIndex = rdfizableSheet.getTitleRowIndex();
		log.debug("Found title row at index "+headerRowIndex);
		// si la ligne d'entete n'a pas été trouvée, on ne génère que le ConceptScheme
		if(headerRowIndex == 1) {
			log.info("Could not find header row index in sheet "+sheet.getSheetName()+", will parse header object until end of sheet (last rowNum = "+ sheet.getLastRowNum() +")");
			headerRowIndex = sheet.getLastRowNum();
		}
		
		// read the properties on the header by reading the top rows
		ColumnHeaderParser headerParser = new ColumnHeaderParser(prefixManager);
		for (int rowIndex = 1; rowIndex <= headerRowIndex; rowIndex++) {
			if(sheet.getRow(rowIndex) != null) {
				String key = getCellValue(sheet.getRow(rowIndex).getCell(0));
				String value = getCellValue(sheet.getRow(rowIndex).getCell(1));
				
				ColumnHeader header = headerParser.parse(key, (short)-1);
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
						log.debug("Adding value on header object \""+value+"\"@"+header.getLanguage().orElse(this.lang));
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

		if(rdfizableSheet.hasDataSection()) {
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
		} else {
			log.info("Sheet has no title row, skipping data processing.");
		}
		
		
		if(this.applyPostProcessings) {
			log.info("Applying SKOS post-processings on the result");
			// post-process the model to add inverses, hasTopConcepts, etc.
			postProcess(model, csResource);		
			
			// Turn the model to SKOS-XL
			if(this.generateXl || this.generateXlDefinitions) {
				xlify(model);
			}
		} else {
			log.info("Skipping SKOS post-processings");
		}
		
		// writes the resulting Model
		log.debug("Saving graph of "+model.size()+" statements generated from Sheet "+sheet.getSheetName());
		modelWriter.saveGraphModel(csUri, model, prefixManager.getPrefixes());
		
		// stores the idenifier of generated vocabulary
		convertedVocabularyIdentifiers.add(csUri);
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
			// if the header object was explicitely typed as skos:Collection, then add skos:members to every included skos:Concept
			model.filter(null, RDF.TYPE, SKOS.CONCEPT).forEach(
					s -> { model.add(csResource, SKOS.MEMBER, ((Resource)s.getSubject())); }
			);
		} else if(
				!model.filter(csResource, RDF.TYPE, OWL.CLASS).isEmpty()
				||
				!model.filter(csResource, RDF.TYPE, RDFS.CLASS).isEmpty()
		) {
			// for each resource without an explicit rdf:type, declare it of the type specified in the header
			model.subjects().stream().filter(s -> model.filter(s, RDF.TYPE, null).isEmpty()).forEach(s -> {
				model.add(s, RDF.TYPE, csResource);
			});
		}
		else {
			// no explicit type given in header : we suppose this is a ConceptScheme and apply SKOS post processings
			
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
				// the URI could be null
				rowBuilder = new RowBuilder(model, prefixManager.uri(value, false));
				continue;
			}
			
			// process the cell for each subsequent columns after the first one
			if (StringUtils.isNotBlank(value)) {
				if(this.workbook.getFontAt(c.getCellStyle().getFontIndex()).getStrikeout()) {
					// skip the cell if it is striked out
					continue;
				}
				
				ValueGeneratorIfc valueGenerator = valueGenerators.get(header.getDeclaredProperty());
				
				if(header.getParameters().get(ColumnHeader.PARAMETER_LOOKUP_COLUMN) != null) {
					// finds the index of the column corresponding to lookupColumn reference
					String lookupColumnRef = header.getParameters().get(ColumnHeader.PARAMETER_LOOKUP_COLUMN);
					short lookupColumnIndex = ColumnHeader.idRefOrPropertyRefToColumnIndex(columnHeaders, lookupColumnRef);
					if(lookupColumnIndex == -1) {
						throw new Xls2SkosException("Unable to find lookupColumn reference '"+lookupColumnRef+"' (full header "+header.getOriginalValue()+") in sheet "+row.getSheet().getSheetName()+".");
					}
					
					// now find the subject at which the lookupColumn property is attached
					ColumnHeader lookupColumnHeader = ColumnHeader.findByColumnIndex(columnHeaders, lookupColumnIndex);
					short lookupSubjectColumn = 0;
					if(header.getParameters().get(ColumnHeader.PARAMETER_SUBJECT_COLUMN) != null) {
						String subjectColumnRef = lookupColumnHeader.getParameters().get(ColumnHeader.PARAMETER_SUBJECT_COLUMN);
						lookupSubjectColumn = ColumnHeader.idRefToColumnIndex(columnHeaders, subjectColumnRef);
						if(lookupSubjectColumn == -1) {
							throw new Xls2SkosException("Unable to find subjectColumn reference '"+subjectColumnRef+"' (full header "+lookupColumnHeader.getOriginalValue()+") in sheet "+row.getSheet().getSheetName()+", while processing lookupColumn in header "+header.getOriginalValue());
						}
					}
					
					valueGenerator = ValueGeneratorFactory.lookup(
							header,
							row.getSheet(),
							lookupColumnIndex,
							lookupSubjectColumn,
							prefixManager
					);
				}
				
				else if(header.getParameters().get(ColumnHeader.PARAMETER_RECONCILE) != null) {
					String reconcileParameterValue = header.getParameters().get(ColumnHeader.PARAMETER_RECONCILE);
					IRI reconcileProperty = (header.getReconcileProperty() != null)?header.getReconcileProperty():SKOS.PREF_LABEL;
					
					
					if(reconcileParameterValue.equals("local")) {						
						valueGenerator = ValueGeneratorFactory.reconcile(
								header,
								prefixManager,
								reconcileProperty,
								globalRepository
						);
					} else if(reconcileParameterValue.equals("external")) {						
						valueGenerator = ValueGeneratorFactory.reconcile(
								header,
								prefixManager,
								reconcileProperty,
								supportRepository
						);
					}
					
				}
				
				// if this is not one of the known processor, but the property is known, then defaults to a generic processor
				// also defaults to a generic processor if a custom datatype is declared on the property
				else if(
						(
								valueGenerator == null
								||
								header.getDatatype().isPresent()
								||
								(header.getParameters() != null && !header.getParameters().isEmpty())
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
					// if it is a true column wih a declared property...
					header.getProperty() != null
					&&
					!header.getDatatype().isPresent()
					&&
					!header.getLanguage().isPresent()
					&&
					(value.startsWith("http") || prefixManager.usesKnownPrefix(value.trim()))
				) {
					valueGenerator = ValueGeneratorFactory.split(valueGenerator, ",");
				}
				
				// determine the subject of the triple, be default it is the value of the first column but can be overidden
				if(header.getParameters().get(ColumnHeader.PARAMETER_SUBJECT_COLUMN) != null) {
					String subjectColumnRef = header.getParameters().get(ColumnHeader.PARAMETER_SUBJECT_COLUMN);
					int subjectColumnIndex = ColumnHeader.idRefOrPropertyRefToColumnIndex(columnHeaders, subjectColumnRef);
					if(subjectColumnIndex == -1) {
						throw new Xls2SkosException("Unable to find subjectColumn reference '"+subjectColumnRef+"' (full header "+header.getOriginalValue()+") in sheet "+row.getSheet().getSheetName()+".");
					}
					
					String currentSubject = getCellValue(row.getCell(subjectColumnIndex));
					
					if(currentSubject != null) {
						try {
							rowBuilder.setCurrentSubject(SimpleValueFactory.getInstance().createIRI(currentSubject));
						} catch (Exception e) {
							e.printStackTrace();
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							e.printStackTrace(new PrintStream(baos));
							String stacktraceString = new String(baos.toByteArray());
							String stacktraceStringBegin = (stacktraceString.length() > 256)?stacktraceString.substring(0, 256):stacktraceString;
							throw new Xls2SkosException(e, "Cannot set subject URI in cell "+subjectColumnRef+(row.getRowNum()+1)+", value is '"+ currentSubject +"' (header "+header.getOriginalValue()+") in sheet "+row.getSheet().getSheetName()+".\n Message is : "+e.getMessage()+"\n Beginning of stacktrace is "+stacktraceStringBegin);
						}
					} else {
						log.warn("Unable to set a new current subject from cell '"+CellReference.convertNumToColString(colIndex)+(row.getRowNum()+1)+"' (header "+header.getOriginalValue()+") in sheet "+row.getSheet().getSheetName()+".");
					}
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
						e.printStackTrace();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						e.printStackTrace(new PrintStream(baos));
						String stacktraceString = new String(baos.toByteArray());
						String stacktraceStringBegin = (stacktraceString.length() > 256)?stacktraceString.substring(0, 256):stacktraceString;
						throw new Xls2SkosException(e, "Convert exception while processing value '"+value+"', cell "+CellReference.convertNumToColString(colIndex)+(row.getRowNum()+1)+" (header "+header.getOriginalValue()+") in sheet "+row.getSheet().getSheetName()+".\n Message is : "+e.getMessage()+"\n Beginning of stacktrace is "+stacktraceStringBegin);
					}
				}
				
				// reset the current subject after that
				rowBuilder.resetCurrentSubject();
			}
		}
		
		// if, after row processing, no rdf:type was generated, then we consider the row to be a skos:Concept
		// this allows to generate something else that skos:Concept
		if(this.applyPostProcessings && rowBuilder != null && rowBuilder.rowMainResource != null && !model.contains(rowBuilder.rowMainResource, RDF.TYPE, null)) {
			model.add(rowBuilder.rowMainResource, RDF.TYPE, SKOS.CONCEPT);
		}
		
		return null == rowBuilder ? null : rowBuilder.rowMainResource;
	}

	private class RowBuilder {
		private final Model model;
		private Resource rowMainResource;
		private Resource currentSubject;

		public RowBuilder(Model model) {
			this(model, null);
		}
		
		public RowBuilder(Model model, String uri) {
			this.model = model;
			if(uri != null) {
				rowMainResource = SimpleValueFactory.getInstance().createIRI(uri);
				// set the current subject to the main resource by default
				currentSubject = rowMainResource;
			}
		}

		public void processCell(ValueGeneratorIfc valueGenerator, String value, String language) {
			// if the column is unknown, ignore it
			// if no current subject was found, cannot add any value
			if(valueGenerator != null && this.currentSubject != null) {				
				valueGenerator.addValue(model, currentSubject, value, language);
			}
		}

		public void setCurrentSubject(Resource currentSubject) {
			this.currentSubject = currentSubject;
		}
		
		public void resetCurrentSubject() {
			this.currentSubject = this.rowMainResource;
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

	public List<String> getConvertedVocabularyIdentifiers() {
		return convertedVocabularyIdentifiers;
	}

	public boolean isApplyPostProcessings() {
		return applyPostProcessings;
	}

	public void setApplyPostProcessings(boolean applyPostProcessings) {
		this.applyPostProcessings = applyPostProcessings;
	}
	
	public void setSupportRepository(Repository supportRepository) {
		this.supportRepository = supportRepository;
	}

	public static void main(String[] args) throws Exception {
		
		// quick and dirty Log4J config		
		BasicConfigurator.configureDefaultContext();
		((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("org.eclipse.rdf4j")).setLevel(ch.qos.logback.classic.Level.INFO);
		
		
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
