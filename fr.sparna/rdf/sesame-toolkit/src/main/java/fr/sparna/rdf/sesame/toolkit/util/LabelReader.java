package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;

import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;

/**
 * Returns a label for given resource or list of resources, in a given language.
 * 
 * @author Thomas Francart
 */
public class LabelReader extends PreferredPropertyReader {

	public static final List<java.net.URI> DEFAULT_LABEL_PROPERTIES = Arrays.asList(new java.net.URI[] {
			// skos:prefLabel first
			URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"),
			// rdfs:label
			URI.create(RDFS.LABEL.toString()),
			// dcterms title
			// URI.create(DCTERMS.TITLE.toString())
			// could also look for old dc property ?
			// ,URI.create("http://purl.org/dc/elements/1.1/title"),
	});
	
	public LabelReader(
			Repository repository,
			List<java.net.URI> labelProperties,
			String fallbackLanguage,
			String preferredLanguage) {
		super(repository, labelProperties, fallbackLanguage, preferredLanguage);
	}
	
	public LabelReader(Repository repository, String fallbackLanguage, String preferredLanguage) {
		this(
				repository,
				// make a copy of it to make editable
				new ArrayList<URI>(DEFAULT_LABEL_PROPERTIES),
				fallbackLanguage,
				preferredLanguage
		);
	}
	
	public LabelReader(Repository repository, String preferredLanguage) {
		this(
				repository,
				// make a copy of it to make editable
				new ArrayList<URI>(DEFAULT_LABEL_PROPERTIES),
				null,
				preferredLanguage
		);
	}
	
	public static String display(List<Value> values) {
		StringBuffer buffer = new StringBuffer();
		if(values != null) {
			for (Value value : values) {
				buffer.append(value.stringValue());
				buffer.append(", ");
			}
			// remove last garbage if needed
			if(buffer.length() > 2)
				buffer.delete(buffer.length() - ", ".length(), buffer.length());
		}
		return buffer.toString();
	}
	
	@Override
	public List<Value> getValues(final java.net.URI resource) 
	throws SPARQLPerformException {
		List<Value> values = super.getValues(resource);
		
		// if nothing was found add the URI itself in the list of values
		if(values.size() == 0) {
			values.add(this.repository.getValueFactory().createLiteral(
					Namespaces.getInstance().withRepository(this.repository).shorten(resource.toString())
			));
		}

		return values;
	}	
	
	@Override
	public List<Value> getValues(final org.openrdf.model.URI resource) 
	throws SPARQLPerformException {
		return getValues(URI.create(resource.stringValue()));
	}
	
	@Override
	public Map<java.net.URI, List<Value>> getValues(Collection<java.net.URI> resources)
	throws SPARQLPerformException {
		ListMap<java.net.URI, Value> values = (ListMap<java.net.URI, Value>)super.getValues(resources);
				
		// for each resources for which a value wasn't found, create a default value
		for (URI uri : resources) {
			if(!values.containsKey(uri)) {
				values.add(
						uri, 
						this.repository.getValueFactory().createLiteral(Namespaces.getInstance().withRepository(this.repository).shorten(uri.toString())
				));
			}
		}
		
		return values;
	}

}
