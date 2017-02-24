package fr.sparna.rdf.sesame.toolkit.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

/**
 * Wraps a List of <code>RDFHandler</code>, and delegates the calls to startRDF, endRDF, handleStatement, handleNamespace, and handleComment
 * to all of them.
 * <p/>This is useful to process the result of a single query in multiple ways, without executing the query multiple times.
 * 
 * @deprecated use org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper instead
 * @author Thomas Francart
 *
 */
public class MultipleRDFHandler implements RDFHandler {

	protected List<RDFHandler> handlers;

	public MultipleRDFHandler() {
		super();
	}
	
	/**
	 * On ne type volontairement pas la Collection pour pouvoir passer des sous-classes
	 * @param handlers
	 */
	@SuppressWarnings("all")
	public MultipleRDFHandler(List handlers) {
		super();
		this.handlers = handlers;
	}

	public void addHandler(RDFHandler handler) {
		if(this.handlers == null) {
			handlers = new ArrayList<RDFHandler>();
		}
		handlers.add(handler);
	}

	public List<RDFHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<RDFHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		if(this.handlers != null) {
			for (RDFHandler aHandler : this.handlers) {
				aHandler.endRDF();
			}
		}
	}

	@Override
	public void handleComment(String aComment) throws RDFHandlerException {
		if(this.handlers != null) {
			for (RDFHandler aHandler : this.handlers) {
				aHandler.handleComment(aComment);
			}
		}
	}

	@Override
	public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
		if(this.handlers != null) {
			for (RDFHandler aHandler : this.handlers) {
				aHandler.handleNamespace(arg0, arg1);
			}
		}
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		if(this.handlers != null) {
			for (RDFHandler aHandler : this.handlers) {
				aHandler.handleStatement(s);
			}
		}
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		if(this.handlers != null) {
			for (RDFHandler aHandler : this.handlers) {
				aHandler.startRDF();
			}
		}
	}

}
