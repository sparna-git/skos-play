package fr.sparna.rdf.handler;

import java.util.Collection;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sorts statements before sending them to a delegate RDFHandler. Groups the statements by
 * subject, then predicate, with RDF.TYPE first.
 * You can use a RDFXMLPrettyWriter as a delegate to produce a clean RDF/XML output.
 * Usage :
 * <code>
 * RDFHandler writer = new SortingRDFHandler(new RDFXMLPrettyWriter(new FileOutputStream(myFile)));
 * repository.export(writer);
 * </code>
 * 
 * @author Thomas Francart
 *
 */
public class FilteringHandler implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// delegate
	private RDFHandler handler;

	// list of properties to include
	private Collection<String> includes;
	// list of properties to exclude
	private Collection<String> excludes;

	public FilteringHandler() {
		super();
	}
	
	/**
	 * Constructs a FilteringRDFHandler with a delegate handler
	 * 
	 * @param handler	The handler to delegate the calls to
	 */
	public FilteringHandler(RDFHandler handler) {
		super();
		this.handler = handler;
	}
	
	public FilteringHandler(RDFHandler handler, Collection<String> includes, Collection<String> excludes) {
		super();
		this.handler = handler;
		this.includes = includes;
		this.excludes = excludes;
	}
	
	public FilteringHandler(Collection<String> includes, Collection<String> excludes) {
		super();
		this.includes = includes;
		this.excludes = excludes;
	}

	/**
	 * Calls <code>startRDF()</code> on the delegate handler
	 */
	@Override
	public void startRDF() throws RDFHandlerException {
		handler.startRDF();
	}

	/**
	 * Sorts all the statements gathered in <code>handleStatement</code>
	 * Before calling <code>handleStatement</code> on the delegate with sorted statements, and finally
	 * calls <code>endRDF()</code> on the delegate.
	 */
	@Override
	public void endRDF() throws RDFHandlerException {		
		// call the endRDF method on the delegate RDFHandler
		handler.endRDF();		
	}

	/**
	 * Calls <code>handleComment</code> on the delegate handler
	 */
	@Override
	public void handleComment(String arg0) throws RDFHandlerException {
		handler.handleComment(arg0);
	}

	/**
	 * Calls <code>handleNamespace</code> on the delegate handler
	 */
	@Override
	public void handleNamespace(String arg0, String arg1)
	throws RDFHandlerException {
		handler.handleNamespace(arg0, arg1);
	}

	/**
	 * Stores the statement internally for later sorting
	 */
	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		if(isValid(s)) {
			handler.handleStatement(s);
		}
	}
	
	private boolean isValid(Statement s) {
		return(
			(
				(this.includes == null || this.includes.size() == 0)
				||
				this.includes.contains(s.getPredicate().stringValue())
			)
			&&
			(
				(this.excludes == null || this.excludes.size() == 0)
				||
				!this.excludes.contains(s.getPredicate().stringValue())
			)
		);
	}

	public Collection<String> getIncludes() {
		return includes;
	}

	public void setIncludes(Collection<String> includes) {
		this.includes = includes;
	}

	public Collection<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(Collection<String> excludes) {
		this.excludes = excludes;
	}

	public RDFHandler getHandler() {
		return handler;
	}

	public void setHandler(RDFHandler handler) {
		this.handler = handler;
	}
	
	
}
