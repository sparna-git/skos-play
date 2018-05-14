package fr.sparna.rdf.rdf4j.toolkit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.repository.RepositoryConnection;


/**
 * Returns a label for given resource or list of resources, in a given language.
 * 
 * @author Thomas Francart
 */
public class LabelReader extends PreferredPropertyReader {

	public static final List<IRI> DEFAULT_LABEL_PROPERTIES = Arrays.asList(new IRI[] {
			// skos:prefLabel first
			SKOS.PREF_LABEL,
			// rdfs:label
			RDFS.LABEL,
			// dcterms title
			// URI.create(DCTERMS.TITLE.toString())
			// could also look for old dc property ?
			// ,URI.create("http://purl.org/dc/elements/1.1/title"),
	});
	
	public LabelReader(
			RepositoryConnection connection,
			List<IRI> labelProperties,
			List<String> fallbackLanguages,
			String preferredLanguage) {
		super(connection, labelProperties, fallbackLanguages, preferredLanguage);
	}
	
	public LabelReader(RepositoryConnection connection, List<String> fallbackLanguages, String preferredLanguage) {
		this(
				connection,
				// make a copy of it to make editable
				new ArrayList<IRI>(DEFAULT_LABEL_PROPERTIES),
				fallbackLanguages,
				preferredLanguage
		);
	}
	
	public LabelReader(
			RepositoryConnection connection,
			List<IRI> labelProperties,
			String fallbackLanguage,
			String preferredLanguage) {
		this(connection, labelProperties, Collections.singletonList(fallbackLanguage), preferredLanguage);
	}
	
	public LabelReader(RepositoryConnection connection, String fallbackLanguage, String preferredLanguage) {
		this(
				connection,
				// make a copy of it to make editable
				new ArrayList<IRI>(DEFAULT_LABEL_PROPERTIES),
				fallbackLanguage,
				preferredLanguage
		);
	}
	
	public LabelReader(RepositoryConnection connection, String preferredLanguage) {
		this(
				connection,
				// make a copy of it to make editable
				new ArrayList<IRI>(DEFAULT_LABEL_PROPERTIES),
				(List<String>)null,
				preferredLanguage
		);
	}
	
	public static String display(List<? extends Value> values) {
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
	public List<Value> getValues(final IRI resource) {
		List<Value> values = super.getValues(resource);
		
		// if nothing was found add the URI itself in the list of values
		if(values.size() == 0) {
			values.add(this.connection.getValueFactory().createLiteral(
					Namespaces.getInstance().withRepository(this.connection.getRepository()).shorten(resource.toString())
			));
		}

		return values;
	}	
	
	@Override
	public Map<IRI, List<Value>> getValues(Collection<IRI> resources) {
		Map<IRI, List<Value>> values = (Map<IRI, List<Value>>)super.getValues(resources);
				
		// for each resources for which a value wasn't found, create a default value
		Namespaces namespaces = Namespaces.getInstance().withRepository(this.connection.getRepository());
		for (IRI uri : resources) {
			if(!values.containsKey(uri)) {
				values.put(uri, Collections.singletonList(this.connection.getValueFactory().createLiteral(namespaces.shorten(uri.toString()))));
			}
			
			if(values.get(uri).size() == 0) {
				values.get(uri).add(
						this.connection.getValueFactory().createLiteral(namespaces.shorten(uri.toString())
				));
			}
		}
		
		return values;
	}

}
