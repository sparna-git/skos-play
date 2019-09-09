package fr.sparna.rdf.skos.xls2skos;

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelHelper {

	private ExcelHelper() {
	}

	public static String getCellValue(Cell cell) {
		if(cell == null) return null;
		return getCellValue(cell.getCellTypeEnum(), cell);
	}
	
	private static String getCellValue(CellType type, Cell cell) {
		// blank or error cells give an empty value
		if (type == CellType.BLANK || type == CellType.ERROR) {
			return "";
		} else if (type == CellType.STRING) {
			return cell.getStringCellValue();
        } else if (type == CellType.NUMERIC) {
        	double d = cell.getNumericCellValue();
			if((d % 1) == 0) {
				// return it as an int without the dot to avoid values like "1.0"
				return "" + new Double(d).intValue();
			} else {
				return "" + d;
			} 
        } else if (type == CellType.BOOLEAN) {
        	return Boolean.toString(cell.getBooleanCellValue());
        } else if (type == CellType.FORMULA) {
            // Re-run based on the formula type
            return getCellValue(cell.getCachedFormulaResultTypeEnum(), cell);
        } else {
        	throw new Xls2SkosException("Cell type unknown or unsupported ({}) at Sheet '{}', row {}, column {}", type.name(), cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex());
        }
	}
	
	
	public static Calendar asCalendar(String value) {
		Calendar calendar = DateUtil.getJavaCalendar(Double.valueOf(value));
		calendar.setTimeZone(TimeZone.getTimeZone("CEST"));
		return calendar;
	}
	
	public static Row columnLookup(String value, Sheet sheet, int columnIndex) {
		for(Row r : sheet) {
		   Cell c = r.getCell(columnIndex);
		   if(c != null) {
		      String cellValue = getCellValue(c);
		      if(cellValue.trim().equals(value.trim())) {
		    	  return r;
		      }
		   }
		}
		
		return null;
	}


}
