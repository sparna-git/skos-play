package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.skos.SKOS;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;

public class DisplayHeaderSkosReader {

	protected Repository repository;

	public DisplayHeaderSkosReader(Repository repository) {
		super();
		this.repository = repository;
	}
	
	public DisplayHeader read(final String lang, final URI conceptScheme)
	throws SPARQLExecutionException {
		DisplayHeader h = new DisplayHeader();
		
		if(conceptScheme == null) {
			// TODO
		} else {
			// read skos:prefLabel for title
			PropertyReader reader = new PropertyReader(
					this.repository,
					URI.create(SKOS.PREF_LABEL),
					lang,
					null,
					null
			);
			
			List<Value> values = reader.read(conceptScheme);
			h.setTitle(valueListToString(values));
		}
		
		return h;
	}
	
	private String valueListToString(List<Value> values) {
		StringBuffer sb = new StringBuffer();
		if(values != null && values.size() > 0) {
			for (Value aValue : values) {
				sb.append(((Literal)aValue).getLabel()+", ");
			}
			// remove last ", "
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}
}
