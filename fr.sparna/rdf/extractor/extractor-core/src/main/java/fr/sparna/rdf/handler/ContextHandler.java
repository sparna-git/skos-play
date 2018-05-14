package fr.sparna.rdf.handler;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;

public class ContextHandler extends RDFHandlerWrapper implements RDFHandler {
	
	// target graphs to copy statements to
	protected Set<IRI> targetGraphs;
	
	public ContextHandler(RDFHandler handler, IRI targetGraph) {
		super(handler);
		this.targetGraphs = new HashSet<IRI>();
		this.targetGraphs.add(targetGraph);
	}
	
	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		if(s.getContext() != null) {
			super.handleStatement(s);
		} else {
			// set the context
			if(this.targetGraphs != null) {
				for (IRI aGraph : this.targetGraphs) {
					Statement result = SimpleValueFactory.getInstance().createStatement(
							s.getSubject(),
							s.getPredicate(),
							s.getObject(),
							aGraph
					);
					super.handleStatement(result);
				}
			}
		}
	}

	public Set<IRI> getTargetGraphs() {
		return targetGraphs;
	}

	/**
	 * Sets the target graphs in which the resulting statements will be inserted. Be default
	 * the statements will be added in the default graph.
	 * 
	 * @param targetGraphs a set of URI containing all the graphs in which the statements will be inserted.
	 */
	public void setTargetGraphs(Set<IRI> targetGraphs) {
		this.targetGraphs = targetGraphs;
	}
	
}
