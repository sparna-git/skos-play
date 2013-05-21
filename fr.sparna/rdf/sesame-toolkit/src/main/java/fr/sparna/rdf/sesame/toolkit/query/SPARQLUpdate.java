package fr.sparna.rdf.sesame.toolkit.query;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderList;

/**
 * Concrete implementation of SPARQLUpdateIfc that implements getters and setters
 * for defaultRemoveGraphs and defaultInsertGrap.
 * 
 * @author Thomas Francart
 *
 */
public class SPARQLUpdate extends SPARQLQuery implements SPARQLUpdateIfc {

	protected Set<URI> defaultRemoveGraphs = null;
	
	protected URI defaultInsertGraph = null;

	public SPARQLUpdate(SPARQLQueryBuilderIfc builder) {
		super(builder);
	}

	public SPARQLUpdate(String sparql) {
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
	
	public static List<SPARQLUpdate> fromUpdateDirectory(File directory) {
		return fromUpdateList(SPARQLQueryBuilderList.fromDirectory(directory));
	}
	
	public static List<SPARQLUpdate> fromUpdateList(List<? extends SPARQLQueryBuilderIfc> builders) {
		if(builders == null) {
			return null;
		}
		
		ArrayList<SPARQLUpdate> result = new ArrayList<SPARQLUpdate>();
		for (SPARQLQueryBuilderIfc aBuilder : builders) {
			result.add(new SPARQLUpdate(aBuilder));
		}
		return result;
	}
	
}
