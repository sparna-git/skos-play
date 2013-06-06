package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderList;

/**
 * Loads data from a repository provider (possibly the same one as the initial repository), by doing some SPARQL queries onto it.
 * This can be used to filter the data that is in a public SPARQL endpoint, and select
 * only part of the data that is interesting, or to add some custom inferred data into the repository.
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromSPARQL extends AbstractLoadOperation implements RepositoryOperationIfc {

	protected Repository sourceRepository;
	protected List<? extends SPARQLQueryIfc> sparqlQueries;	

	/**
	 * Will execute and load the given list of SPARQL queries on the given repository
	 * 
	 * @param repository		The repository to execute SPARQL queries on
	 * @param sparqlQueries		The list of SPARQL queries to execute
	 */
	public LoadFromSPARQL(Repository sourceRepository, List<SPARQLQueryIfc> sparqlQueries) {
		super();
		this.sourceRepository = sourceRepository;
		this.sparqlQueries = sparqlQueries;
	}

	/**
	 * Shortcut to execute and load a single query on the given repository
	 * 
	 * @param repository	The repository to execute SPARQL queries on
	 * @param query
	 */
	public LoadFromSPARQL(Repository sourceRepository, SPARQLQueryIfc query) {
		this(sourceRepository, Collections.singletonList(query));
	}
	
	/**
	 * Executes all the sparql queries contained recursively in the given folder
	 * 
	 * @param sourceRepository	the repository to execute SPARQL queries on 
	 * @param sparqlDir			a folder from which queries will be read
	 */
	public LoadFromSPARQL(Repository sourceRepository, File sparqlDir) {
		this.sourceRepository = sourceRepository; 		
		this.setSparqlQueries(SPARQLQuery.fromQueryList(SPARQLQueryBuilderList.fromDirectory(sparqlDir)));
	}
	
	/**
	 * Specifies only the repository on which to execute the SPARQL queries, but no
	 * SPARQL queries to execute.
	 * 
	 * @param sourceRepository The repository to execute SPARQL queries on
	 */
	public LoadFromSPARQL(Repository sourceRepository) {
		this(sourceRepository, (SPARQLQueryIfc)null);
	}

	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {		
		if(this.sparqlQueries != null) {
			for (final SPARQLQueryIfc aBuilder : this.sparqlQueries) {
				try {
					new Perform(this.sourceRepository).construct(
							new ConstructSPARQLHelper(
									aBuilder,
									new CopyStatementRDFHandler(repository, this.targetGraph)
							)
					);
				} catch (SPARQLPerformException e) {
					throw new RepositoryOperationException(e);
				}
			}
		}
	}

	/**
	 * Sets the SPARQL queries to execute.
	 * @param sparqlQueries
	 */
	public void setSparqlQueries(List<? extends SPARQLQueryIfc> sparqlQueries) {
		this.sparqlQueries = sparqlQueries;
	}

}
