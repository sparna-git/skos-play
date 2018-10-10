package fr.sparna.rdf.skos.printer.reader;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public interface AlignmentDataHarvesterIfc {

	public void harvestData(RepositoryConnection connection, IRI conceptScheme);
	
}
