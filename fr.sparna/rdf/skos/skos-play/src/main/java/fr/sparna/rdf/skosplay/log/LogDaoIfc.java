package fr.sparna.rdf.skosplay.log;

import java.util.List;

public interface LogDaoIfc {
	
	/**
	 * Insertion data
	 * @param entry
	 */
	public void writeLog(LogEntry entry);
	
	/**
	 * List all data in the base
	 * @return
	 */
	
	public List<LogEntry> ListAllLog();
	
	/**
	 * This Method return the number of conversion
	 * @return
	 */
	public int getConvertNumber();
	
	/**
	 * This Method returns the number of print
	 * @return
	 */
	public int getPrintNumber();
	
	/**
	 * return the last date of conversion
	 * @return
	 */
	
	public String getLastConversionDate();
	
	/**
	 * return the last date of printing
	 * @return
	 */
	public String getLastPrintDate();
	
}
