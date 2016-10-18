package fr.sparna.rdf.skos.xls2skos;

import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getCellValue;
import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getColumnNames;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConceptSchemeFromExcel {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private List<String> sheetsToIgnore = new ArrayList<String>();

	private final Map<String, Model> csModels = new HashMap<>();
	private final Map<String, ValueAdder> valueAdders = new HashMap<>();

	private ModelWriterIfc modelWriter;
	private String lang;

	public ConceptSchemeFromExcel(ModelWriterIfc modelWriter, String lang) {
		
		this.modelWriter = modelWriter;;
		this.lang = lang;
		
		// labels
		valueAdders.put("skos:prefLabel", 		langLiteral(SKOS.PREF_LABEL, this.lang));
		valueAdders.put("skos:altLabel", 		langLiteral(SKOS.ALT_LABEL, this.lang));
		valueAdders.put("skos:hiddenLabel", 	langLiteral(SKOS.HIDDEN_LABEL, this.lang));
		// notes
		valueAdders.put("skos:definition", 		langLiteral(SKOS.DEFINITION, this.lang));		
		valueAdders.put("skos:editorialNote", 	langLiteral(SKOS.EDITORIAL_NOTE, this.lang));
		valueAdders.put("skos:historyNote", 	langLiteral(SKOS.HISTORY_NOTE, this.lang));
		valueAdders.put("skos:scopeNote", 		langLiteral(SKOS.SCOPE_NOTE, this.lang));
		valueAdders.put("skos:changeNote", 		langLiteral(SKOS.CHANGE_NOTE, this.lang));
		valueAdders.put("skos:example", 		langLiteral(SKOS.EXAMPLE, this.lang));
		// notation
		valueAdders.put("skos:notation", 		plainLiteral(SKOS.NOTATION));
		// semantic relations
		valueAdders.put("skos:broader", 		resources(SKOS.BROADER, ','));
		valueAdders.put("skos:narrower", 		resources(SKOS.NARROWER, ','));
		valueAdders.put("skos:related", 		resources(SKOS.RELATED, ','));
		// mapping relations		
		valueAdders.put("skos:exactMatch", 		resources(SKOS.EXACT_MATCH, ','));
		valueAdders.put("skos:closeMatch", 		resources(SKOS.CLOSE_MATCH, ','));
		valueAdders.put("skos:relatedMatch", 	resources(SKOS.RELATED_MATCH, ','));
		valueAdders.put("skos:broadMatch", 		resources(SKOS.BROAD_MATCH, ','));
		valueAdders.put("skos:narrowMatch", 	resources(SKOS.RELATED_MATCH, ','));
		// XL labels
		valueAdders.put("skosxl:prefLabel", 	skosXlLabel(SKOSXL.PREF_LABEL));
		valueAdders.put("skosxl:altLabel", 		skosXlLabel(SKOSXL.ALT_LABEL));
		valueAdders.put("skosxl:hiddenLabel",	skosXlLabel(SKOSXL.HIDDEN_LABEL));
		valueAdders.put("skosxl:literalForm", 	langLiteral(SKOSXL.LITERAL_FORM, this.lang));
		// other concepts metadata
		valueAdders.put("euvoc:status", 		resources(SimpleValueFactory.getInstance().createIRI("http://publications.europa.eu/ontology/euvoc#status"), ','));		
		valueAdders.put("euvoc:startDate", 		dateLiteral(SimpleValueFactory.getInstance().createIRI("http://eurovoc.europa.eu/schema#startDate")));
		valueAdders.put("euvoc:endDate", 		dateLiteral(SimpleValueFactory.getInstance().createIRI("http://eurovoc.europa.eu/schema#endDate")));
		valueAdders.put("dct:created", 			dateLiteral(DCTERMS.CREATED));
		valueAdders.put("dct:modified", 		dateLiteral(DCTERMS.MODIFIED));
		valueAdders.put("dct:source", 			resources(DCTERMS.SOURCE, ','));
		// dct metadata for the ConceptScheme
		valueAdders.put("dct:title", 			langLiteral(DCTERMS.TITLE, this.lang));
		valueAdders.put("dct:description", 		langLiteral(DCTERMS.DESCRIPTION, this.lang));
		// all the rest are ignored
	}

	public List<Model> loadAllToFile(File input) {

		List<Model> models = new ArrayList<>();

		try (InputStream inputStream = new FileInputStream(input)) {
			// Workbook workbook = WorkbookFactory.create(inputStream);
			Workbook workbook = WorkbookFactory.create(input);
			// Workbook workbook = new XSSFWorkbook(input);
			loadAllToFile(workbook);			
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}

		return models;
	}
	
	public List<Model> loadAllToFile(Workbook workbook) {

		List<Model> models = new ArrayList<>();

		try {
			
			// notify begin
			modelWriter.beginWorkbook();
			
			for (Sheet sheet : workbook) {

				if (Arrays.stream(sheetsToIgnore.toArray(new String[] {})).filter(
						name -> sheet.getSheetName().equalsIgnoreCase(name)
					).findAny().isPresent()
				) {
					log.debug("Skipping sheet: " + sheet.getSheetName());
					continue;
				}

				// if load(sheet) returns null, the sheet was ignored
				Model model = load(sheet);
				models.add(model);
			}
			
			// notify end
			modelWriter.endWorkbook();
			
		} catch (Exception e) {
			throw Xls2SkosException.rethrow(e);
		}

		return models;
	}

	private Model load(Sheet sheet) {
		
		if(sheet.getRow(0) == null) {
			log.debug(sheet.getSheetName()+" : First row is empty, ignoring sheet.");
			return null;
		}
		
		String uri = getCellValue(sheet.getRow(0).getCell(1));
		
		if(StringUtils.isBlank(uri)) {
			log.debug(sheet.getSheetName()+" : B1 is empty, ignoring sheet.");
			return null;
		} else {
			try {
				new URI(fixUri(uri));
			} catch (URISyntaxException e) {
				log.debug(sheet.getSheetName()+" : B1 is not a valid URI ('"+uri+"'), ignoring sheet");
				return null;
			}
		}		
		
		log.debug("Processing sheet: " + sheet.getSheetName());
		
		Model model = new LinkedHashModelFactory().createEmptyModel();
		SimpleValueFactory svf = SimpleValueFactory.getInstance();

		String csUri = fixUri(uri);		
		Resource csResource = svf.createIRI(csUri);
		model.add(csResource, RDF.TYPE, SKOS.CONCEPT_SCHEME);

		int topRows = 1;
		for (int rowIndex = topRows; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			// test if we find a header in columns 2 and 3, this indicates the header line
			if(
					sheet.getRow(rowIndex) != null
					&&
					valueAdders.containsKey(getCellValue(sheet.getRow(rowIndex).getCell(1)))
					&&
					valueAdders.containsKey(getCellValue(sheet.getRow(rowIndex).getCell(2)))
			) {
				topRows = rowIndex;
				break;
			}
		}
		
		for (int rowIndex = 1; rowIndex <= topRows; rowIndex++) {
			if(sheet.getRow(rowIndex) != null) {
				String key = getCellValue(sheet.getRow(rowIndex).getCell(0));
				String value = getCellValue(sheet.getRow(rowIndex).getCell(1));
				if(valueAdders.containsKey(key) && StringUtils.isNotBlank(value)) {
					valueAdders.get(key).addValue(model, csResource, value);
				}
			}
		}		

		List<String> columnNames = getColumnNames(sheet, topRows);
		for (int rowIndex = (topRows + 1); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row r = sheet.getRow(rowIndex);
			if(r != null) {
				Resource conceptResource = handleRow(model, columnNames, r);
				
				if (null != conceptResource) {
					model.add(conceptResource, SKOS.IN_SCHEME, csResource);
				}
			}
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
		
		// add skos:topConceptOf and skos:hasTopConcept
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
		
		modelWriter.saveGraphModel(csUri, model);
		csModels.put(csUri, model);
		return model;
	}

	private Resource handleRow(Model model, List<String> columnNames, Row row) {
		RowBuilder rowBuilder = null;
		for (int colIndex = 0; colIndex < columnNames.size(); colIndex++) {
			String value = getCellValue(row.getCell(colIndex));
			if (null == rowBuilder) {
				if (StringUtils.isBlank(value)) {
					return null;
				}
				// create the RowBuilder with the URI in the first column
				rowBuilder = new RowBuilder(model, fixUri(value));
				continue;
			}
			
			// process the cell for each subsequent columns after the first one
			if (StringUtils.isNotBlank(value)) {
				rowBuilder.addRow(columnNames.get(colIndex), value);
			}
		}
		
		return null == rowBuilder ? null : rowBuilder.conceptResource;
	}

	private static String fixUri(String uri) {
		Xls2SkosException.when(StringUtils.isBlank(uri), "Empty URI");
		return uri.startsWith("http://") ? uri : "http://" + uri;
	}


	private class RowBuilder {
		private final Model model;
		private final Resource conceptResource;
		private Resource subject;

		public RowBuilder(Model model, String uri) {
			this.model = model;
			conceptResource = SimpleValueFactory.getInstance().createIRI(uri);
			model.add(SimpleValueFactory.getInstance().createIRI(uri), RDF.TYPE, SKOS.CONCEPT);
			subject = conceptResource;
		}

		public void addRow(String type, String value) {
			// fetch proper value adder
			ValueAdder valueAdder = valueAdders.get(type);
			// if the column is unknown, ignore it
			if(valueAdder != null) {
				Resource newResource = valueAdder.addValue(model, subject, value);
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


	private interface ValueAdder {
		Resource addValue(Model model, Resource subject, String value);
	}

	private static ValueAdder resources(IRI property, char separator) {
		return (model, subject, value) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}

			Arrays.stream(
					StringUtils.split(value, separator)
					).forEach(
							uri -> model.add(subject, property, SimpleValueFactory.getInstance().createIRI(uri.trim()))
			);
			return null;
		};
	}

	private static ValueAdder dateLiteral(IRI property) {
		return (model, subject, value) -> {

			if (StringUtils.isBlank(value)) return null;

			try {
				Calendar calendar = DateUtil.getJavaCalendar(Double.valueOf(value));
				calendar.setTimeZone(TimeZone.getTimeZone("CEST"));
				model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)calendar)));
			}
			catch (NumberFormatException ignore) {
			}
			catch (DatatypeConfigurationException ignore) {
				ignore.printStackTrace();
			}
			return null;
		};
	}

	private static ValueAdder langLiteral(IRI property, String lang) {
		return (model, subject, value) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value, lang));
			return null;
		};
	}

	private static ValueAdder plainLiteral(IRI property) {
		return (model, subject, value) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value));
			return null;
		};
	}

	private static ValueAdder skosXlLabel(IRI xlLabelProperty) {
		return (model, subject, value) -> {
			String labelUri = fixUri(value);
			IRI labelResource = SimpleValueFactory.getInstance().createIRI(labelUri);
			model.add(labelResource, RDF.TYPE, SKOSXL.LABEL);
			model.add(subject, xlLabelProperty, labelResource);
			return labelResource;
		};
	}

	private ValueAdder failIfFilledIn(String property) {
		return (model, subject, value) -> {
			if (StringUtils.isBlank(value)) return null;
			throw new Xls2SkosException("Property not supported {} if filled in- {} - {}", property, subject, value);
		};
	}
	
	public static void main(String[] args) throws Exception {
		
		// quick and dirty Log4J config
		org.apache.log4j.BasicConfigurator.configure();
		org.apache.log4j.Logger.getLogger("org.eclipse.rdf4j").setLevel(Level.INFO);
		
		
		// Method 1 : save each scheme to a separate directory
		// DirectoryModelSaver ms = new DirectoryModelSaver(new File("/home/thomas"));
		// ms.setSaveGraphFile(false);
		
		// Method 2 : save everything to a single SKOS file
		OutputStreamModelWriter ms = new OutputStreamModelWriter(new File("/home/thomas/controlled-vocabularies.ttl"));
		
		// Method 3 : save each scheme to a separate entry in a ZIP file.
		// ZipOutputStreamModelWriter ms = new ZipOutputStreamModelWriter(new File("/home/thomas/controlled-vocabularies.zip"));
		// ms.setFormat(RDFFormat.TURTLE);
		
		ConceptSchemeFromExcel me = new ConceptSchemeFromExcel(ms, "en");
		// me.loadAllToFile(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/fr.sparna/rdf/skos/xls2skos/src/test/resources/test-excel-saved-from-libreoffice.xlsx"));
		// me.loadAllToFile(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/fr.sparna/rdf/skos/xls2skos/src/test/resources/test-libreoffice.ods"));
		me.loadAllToFile(new File("/home/thomas/sparna/00-Clients/Sparna/20-Repositories/sparna/fr.sparna/rdf/skos/xls2skos/src/test/resources/testExcelNative.xlsx"));
	}

}
