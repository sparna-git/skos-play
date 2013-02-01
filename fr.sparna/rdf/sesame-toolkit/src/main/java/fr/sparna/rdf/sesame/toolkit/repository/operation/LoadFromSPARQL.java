package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.repository.Repository;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.query.builder.FileSPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder;

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
	protected List<? extends SPARQLQueryBuilderIfc> sparqlQueries;	

	/**
	 * Will execute and load the given list of SPARQL queries on the given repository
	 * 
	 * @param repository	The repository to execute SPARQL queries on
	 * @param sparqlQueries	The list of SPARQL queries to execute
	 */
	public LoadFromSPARQL(Repository sourceRepository, List<String> sparqlQueries) {
		super();
		this.sourceRepository = sourceRepository;
		this.sparqlQueries = StringSPARQLQueryBuilder.fromStringList(sparqlQueries);
	}

	/**
	 * Shortcut to execute and load a single query on the given repository
	 * 
	 * @param repository	The repository to execute SPARQL queries on
	 * @param query
	 */
	public LoadFromSPARQL(Repository sourceRepository, String query) {
		this(sourceRepository, Collections.singletonList(query));
	}
	
	public LoadFromSPARQL(Repository sourceRepository) {
		this(sourceRepository, (String)null);
	}
	
	public LoadFromSPARQL(Repository sourceRepository, File sparqlDir) {
		this.sourceRepository = sourceRepository; 
		
		if(sparqlDir.exists()) {
			List<File> files = FileUtil.listFilesRecursive(sparqlDir);
			ArrayList<FileSPARQLQueryBuilder> builders = new ArrayList<FileSPARQLQueryBuilder>();
			for (File aFile : files) {
				builders.add(new FileSPARQLQueryBuilder(aFile));
			}
			
			this.setSparqlQueries(builders);
		}
	}


	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {		
		if(this.sparqlQueries != null) {
			for (final SPARQLQueryBuilderIfc aBuilder : this.sparqlQueries) {
				try {
					new SesameSPARQLExecuter(this.sourceRepository).executeConstruct(
							new ConstructSPARQLHelper(
									aBuilder,
									new CopyStatementRDFHandler(repository, this.targetGraph)
							)
					);
				} catch (SPARQLExecutionException e) {
					throw new RepositoryOperationException(e);
				}
			}
		}
	}

	public void setSparqlQueries(List<? extends SPARQLQueryBuilderIfc> sparqlQueries) {
		this.sparqlQueries = sparqlQueries;
	}

}
