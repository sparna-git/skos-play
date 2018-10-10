package fr.sparna.rdf.rdf4j.toolkit.handler;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

/**
 * Generates a simple (debug) output of a SELECT query. This is mostly convenient for debugging
 * purposes.
 * 
 * @author Thomas Francart
 *
 */
public class DebugHandler extends AbstractTupleQueryResultHandler implements TupleQueryResultHandler {

	protected PrintWriter writer;
	
	private List<String> bindings;
	
	public DebugHandler(PrintStream stream) {
		this(new PrintWriter(stream));
	}
	
	public DebugHandler(PrintWriter writer) {
		super();
		this.writer = writer;
	}
	
	/**
	 * Constructs a new DebugHandler using System.out as the output writer.
	 */
	public DebugHandler() {
		this(System.out);
	}

	@Override
	public void startQueryResult(List<String> bindings)
	throws TupleQueryResultHandlerException {
		this.bindings = bindings;
		
		String header = "";
		for (String aBinding : bindings) {
			header += aBinding+"\t";
		}
		
		// remove last tab
		header = header.substring(0, header.length() -1);
		writer.println(header);
	}
	
	@Override
	public void handleSolution(BindingSet bindings)
	throws TupleQueryResultHandlerException {
		String aLine = "";
		
		for (String aBindingName : this.bindings) {
			aLine += bindings.getBinding(aBindingName).getValue()+"\t";
		}
		
		// remove last tab
		aLine = aLine.substring(0, aLine.length() -1);
		writer.println(aLine);
	}

	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
		// flush writer
		this.writer.flush();
	}	

}
