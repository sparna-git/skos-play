package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

public interface ValueGeneratorIfc {
	Resource addValue(Model model, Resource subject, String value);
}