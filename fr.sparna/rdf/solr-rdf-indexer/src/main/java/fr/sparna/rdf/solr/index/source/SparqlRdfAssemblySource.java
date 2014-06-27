package fr.sparna.rdf.solr.index.source;

import org.openrdf.repository.Repository;

import fr.sparna.assembly.base.AssemblyFactory;
import fr.sparna.assembly.base.ListAssemblySource;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;

public class SparqlRdfAssemblySource<X> extends ListAssemblySource<X> {

	protected Repository repository;
	protected SparqlQuery query;
	protected AssemblyFactory<X> indexableFactory;	
	
	public SparqlRdfAssemblySource(
			Repository repository,
			SparqlQuery query,
			AssemblyFactory<X> indexableFactory
	) throws SparqlPerformException {
		super(Perform.on(repository).readStringList(query), indexableFactory);
		this.repository = repository;
		this.query = query;
	}
	
}
