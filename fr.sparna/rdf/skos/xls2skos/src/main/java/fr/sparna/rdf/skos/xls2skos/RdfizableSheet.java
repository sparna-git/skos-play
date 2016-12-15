package fr.sparna.rdf.skos.xls2skos;

import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getCellValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Sheet in a Workbook that can be turned into RDF.
 * @author Thomas Francart
 *
 */
public class RdfizableSheet {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Sheet sheet;
	protected Xls2SkosConverter converter;

	public RdfizableSheet(Sheet sheet, Xls2SkosConverter converter) {
		super();
		this.sheet = sheet;
		this.converter = converter;
	}
	
	/**
	 * A Sheet can be converted to RDF if :
	 * <ol>
	 *   <li>The first row is not empty</li>
	 *   <li>Cell B1 contains a value</li>
	 *   <li>The value is a URI starting with http:// or using one of the declared prefixes in the file</li>
	 * </ol>
	 * @return true if this sheet can be converted in RDF by the converter, false otherwise.
	 */
	public boolean canRDFize() {
		if(sheet.getRow(0) == null) {
			log.debug(sheet.getSheetName()+" : First row is empty.");
			return false;
		}
		
		String uri = getCellValue(sheet.getRow(0).getCell(1));
		
		if(StringUtils.isBlank(uri)) {
			log.debug(sheet.getSheetName()+" : B1 is empty.");
			return false;
		} else {
			String fixedUri = converter.prefixManager.uri(uri, true);
			try {
				new URI(fixedUri);
			} catch (URISyntaxException e) {
				log.debug(sheet.getSheetName()+" : B1 is not a valid URI ('"+uri+"').");
				return false;
			} catch (NullPointerException e) {
				log.debug("Cannot build a valid URI from '"+uri+"'.");
				return false;
			}
		}
		
		return true;
	}
	
	public String getSchemeOrGraph() {
		return getCellValue(sheet.getRow(0).getCell(1));
	}
	
	/**
	 * Determines the index of the row containing the column headers. This is determined by checking if column B and C both contain a URI (full, starting with http://, or abbreviated
	 * using one of the declared prefix).
	 * @return
	 */
	public int getTitleRowIndex() {
		int headerRowIndex = 1;
		for (int rowIndex = headerRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			// test if we find a header in columns 2 and 3, this indicates the header line
			if(sheet.getRow(rowIndex) != null) {
				ColumnHeader headerB = ColumnHeader.parse(getCellValue(sheet.getRow(rowIndex).getCell(1)), converter.prefixManager);
				ColumnHeader headerC = ColumnHeader.parse(getCellValue(sheet.getRow(rowIndex).getCell(2)), converter.prefixManager);
				if(headerB != null && headerC != null) {
					if(
								(
										converter.valueGenerators.containsKey(headerB.getProperty())
										||
										converter.prefixManager.expand(headerB.getProperty()) != null
								)
							&&
								(
										converter.valueGenerators.containsKey(headerC.getProperty())
										||
										converter.prefixManager.expand(headerC.getProperty()) != null
								)
					) {
						headerRowIndex = rowIndex;
						break;
					}
				}
			}
		}
		return headerRowIndex;
	}
	
	/**
	 * Parses the ColumnHeader from the title row index.
	 * @param rowNumber the index of the row containing the column headers
	 * @return
	 */
	public List<ColumnHeader> getColumnHeaders(int rowNumber) {
		List<ColumnHeader> columnNames = new ArrayList<>();
		Row row = this.sheet.getRow(rowNumber);
		if(row != null) {
			for (int i = 0; true; i++) {
				Cell cell = row.getCell(i);
				if (null == cell) break;
				String columnName = cell.getStringCellValue();
				if (StringUtils.isBlank(columnName)) {
					break;
				}
				columnNames.add(ColumnHeader.parse(columnName, converter.prefixManager));
			}
		}
		return columnNames;
	}
	
	/**
	 * Reads the prefixes declared in the sheet. The prefixes are read in the top 20 rows, when column A contains "PREFIX" or "@prefix" (ignoring case).
	 * @return the map of prefices
	 */
	public Map<String, String> readPrefixes() {
		Map<String, String> prefixes = new HashMap<String, String>();
		
		// read the prefixes in the top 20 rows		
		for (int rowIndex = 1; rowIndex <= 20; rowIndex++) {
			if(sheet.getRow(rowIndex) != null) {
				String prefixKeyword = getCellValue(sheet.getRow(rowIndex).getCell(0));
				// if we have the "prefix" keyword...
				// note : we add a null check here because there are problems with some sheets
				if(prefixKeyword != null && (prefixKeyword.equalsIgnoreCase("PREFIX") || prefixKeyword.equalsIgnoreCase("@prefix"))) {
					// and we have the prefix and namespaces defined...
					String prefix = getCellValue(sheet.getRow(rowIndex).getCell(1));
					if(StringUtils.isNotBlank(prefix)) {
						if(prefix.charAt(prefix.length()-1) == ':') {
							prefix = prefix.substring(0, prefix.length()-1);
						}
						String namespace = getCellValue(sheet.getRow(rowIndex).getCell(2));
						if(StringUtils.isNotBlank(namespace)) {
							prefixes.put(prefix, namespace);
						}
					}
				}
			}
		}
		
		return prefixes;
	}
}
