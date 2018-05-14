package fr.sparna.rdf.rdf4j.toolkit.handler;

import java.io.PrintWriter;
import java.util.List;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

/**
 * Generates a Comma Separated Value file from the result of a SPARQL SELECT query.
 * 
 * @author Thomas Francart
 */
public class CsvHandler extends AbstractTupleQueryResultHandler implements TupleQueryResultHandler {

	protected PrintWriter writer;
	protected List<String> bindingNames;
	protected boolean addQuotes	= true;
	protected boolean addHeader 	= true;

	
	public CsvHandler(PrintWriter writer, boolean addQuotes, boolean addHeader) {
		super();
		this.writer = writer;
		this.addQuotes = addQuotes;
		this.addHeader = addHeader;
	}
	
	public CsvHandler(PrintWriter writer) {
		this(writer, true, true);
	}
	
	@Override
	public void startQueryResult(List<String> bindingNames)
	throws TupleQueryResultHandlerException {	
		// keep binding names - because we need to make sure we process
		// them in the same order when processing query results
		this.bindingNames = bindingNames;

		if(this.addHeader) {
			// print header
			for(int i=0;i<bindingNames.size();i++) {
				writer.print(bindingNames.get(i));
				if(i < bindingNames.size()-1) {
					writer.print(",");
				}
			}
			writer.println("");
		}
	}

	@Override
	public void handleSolution(BindingSet bs)
	throws TupleQueryResultHandlerException {
		for(int i=0;i<bindingNames.size();i++) {
			if(bs.getValue(bindingNames.get(i)) != null) {
				if(addQuotes) {
					// surround value between quotes and use double quotes for quotes in the value
					writer.print("\""+bs.getValue(bindingNames.get(i)).stringValue().replaceAll("\"", "\"\"")+"\"");
				} else {
					writer.print(bs.getValue(bindingNames.get(i)).stringValue());
				}
			}
			
			if(i < bindingNames.size()-1) {
				writer.print(",");
			}
		}
		writer.println("");
	}
	
	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
		this.writer.flush();
	}

	public boolean isAddQuotes() {
		return addQuotes;
	}

	/**
	 * Sets whether values should be surrounded by quotes (and quotes in value are doubled to be escaped).
	 * Defaults to true.
	 * 
	 * @param addQuotes
	 */
	public void setAddQuotes(boolean addQuotes) {
		this.addQuotes = addQuotes;
	}

	public boolean isAddHeader() {
		return addHeader;
	}

	/**
	 * Sets whether the first line should contain the variable names as a header.
	 * Defaults to true.
	 * 
	 * @param addHeader
	 */
	public void setAddHeader(boolean addHeader) {
		this.addHeader = addHeader;
	}	

}
