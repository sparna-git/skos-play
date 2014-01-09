package fr.sparna.rdf.sesame.toolkit.query;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderList;

/**
 * Concrete implementation of SparqlUpdateIfc that implements getters and setters
 * for defaultRemoveGraphs and defaultInsertGrap.
 * 
 * @author Thomas Francart
 *
 */
public class SparqlUpdate extends SparqlQuery implements SparqlUpdateIfc {

	protected Set<URI> defaultRemoveGraphs = null;
	
	protected URI defaultInsertGraph = null;

	public SparqlUpdate(SparqlQueryBuilderIfc builder) {
		super(builder);
	}

	public SparqlUpdate(String sparql) {
		super(sparql);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<URI> getDefaultRemoveGraphs() {
		return defaultRemoveGraphs;
	}

	public void setDefaultRemoveGraphs(Set<URI> defaultRemoveGraphs) {
		this.defaultRemoveGraphs = defaultRemoveGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getDefaultInsertGraph() {
		return defaultInsertGraph;
	}

	public void setDefaultInsertGraph(URI defaultInsertGraph) {
		this.defaultInsertGraph = defaultInsertGraph;
	}
	
	public static List<SparqlUpdate> fromUpdateDirectory(File directory) {
		return fromUpdateList(SparqlQueryBuilderList.fromDirectory(directory));
	}
	
	public static List<SparqlUpdate> fromUpdateList(List<? extends SparqlQueryBuilderIfc> builders) {
		if(builders == null) {
			return null;
		}
		
		ArrayList<SparqlUpdate> result = new ArrayList<SparqlUpdate>();
		for (SparqlQueryBuilderIfc aBuilder : builders) {
			result.add(new SparqlUpdate(aBuilder));
		}
		return result;
	}
	
}
