package fr.sparna.rdf.rdf4j.toolkit.query;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.impl.SimpleDataset;

public class DatasetFactory {

	public static Dataset fromWorkingGraph(IRI graphIri) {
		SimpleDataset d = new SimpleDataset();
		d.setDefaultInsertGraph(graphIri);
		d.addDefaultGraph(graphIri);
		d.addDefaultRemoveGraph(graphIri);
		return d;
	}
	
}
