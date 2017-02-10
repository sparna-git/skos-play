package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

public interface ValueGeneratorIfc {
	/**
	 * Generates one (or more) properties on the given subject, based on the given value, and insert them in the input model.
	 * The language is passed as a parameter to be able to overwrite a global language parameter with column-specific language declaration.
	 * 
	 * @param model
	 * @param subject
	 * @param value
	 * @param language
	 * @return
	 */
	public Resource addValue(Model model, Resource subject, String value, String language);
}