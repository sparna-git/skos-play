package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;

import org.eclipse.rdf4j.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;

public interface AlignmentDataHarvesterIfc {

	public void harvestData(Repository r, URI conceptScheme) throws SparqlPerformException, RepositoryOperationException;
	
}
