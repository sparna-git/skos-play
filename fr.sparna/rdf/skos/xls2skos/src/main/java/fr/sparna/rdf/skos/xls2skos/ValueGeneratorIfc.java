package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

public interface ValueGeneratorIfc {
	public Resource addValue(Model model, Resource subject, String value, String language, IRI datatype);
}