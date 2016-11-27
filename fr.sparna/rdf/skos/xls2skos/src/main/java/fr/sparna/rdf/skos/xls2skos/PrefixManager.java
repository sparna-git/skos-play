package fr.sparna.rdf.skos.xls2skos;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SKOS;

public class PrefixManager {

	private Map<String, String> prefixes = new HashMap<>();
	
	public PrefixManager() {
		// always add some known namespaces
		prefixes.put("rdf", RDF.NAMESPACE);
		prefixes.put("owl", OWL.NAMESPACE);
		prefixes.put("schema", "http://schema.org/");
		prefixes.put("skos", SKOS.NAMESPACE);
		prefixes.put("skosxl", SKOSXL.NAMESPACE);
		prefixes.put("rdfs", RDFS.NAMESPACE);
		prefixes.put("prov", "http://www.w3.org/ns/prov#");
		prefixes.put("org", "http://www.w3.org/ns/org#");
		prefixes.put("foaf", FOAF.NAMESPACE);
		prefixes.put("dc", DC.NAMESPACE);
		prefixes.put("dcterms", DCTERMS.NAMESPACE);
		prefixes.put("dct", DCTERMS.NAMESPACE);
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
	}
	
	public void register(String prefix, String uri) {
		this.prefixes.put(prefix, uri);
	}
	
	public String expand(String shortForm) {
		if(shortForm == null) {
			return null;
		}
		if(!shortForm.contains(":")) {
			return null;
		}
		
		if(shortForm.startsWith("http")) {
			return shortForm;
		}
		
		String namespace = prefixes.get(shortForm.substring(0, shortForm.indexOf(':')));
		
		// prefix not found
		if(namespace == null) {
			return null;
		}
		
		return namespace+shortForm.substring(shortForm.indexOf(':')+1);
	}
	
	public boolean usesKnownPrefix(String qname) {
		boolean result = false;
		if(qname.contains(":")) {
			String prefix = qname.substring(0, qname.indexOf(':'));
			result = prefixes.containsKey(prefix);
		}
		return result;
	}
	
	public String uri(String value, boolean fixHttp) {
		// if the value starts with http, return it directly
		if(value.startsWith("http")) {
			return value;
		}
		// if the value uses a known prefix, expand it
		if(usesKnownPrefix(value)) {
			return expand(value);
		}
		
		if(fixHttp) {
			// otherwise try to fix the URI by adding "http://" in front of the value (may happen when excel formats the value)
			try {
				new URI("http://"+value);
				return "http://"+value;
			} catch (URISyntaxException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static String property(String valueWithPotentialLanguageAndDatatype) {
		String property = valueWithPotentialLanguageAndDatatype;
		if(valueWithPotentialLanguageAndDatatype.contains("@")) {
			property = valueWithPotentialLanguageAndDatatype.substring(0, valueWithPotentialLanguageAndDatatype.lastIndexOf('@'));
		}
		if(valueWithPotentialLanguageAndDatatype.contains("^^")) {
			property = valueWithPotentialLanguageAndDatatype.substring(0, valueWithPotentialLanguageAndDatatype.lastIndexOf("^^"));
		}
		return property;
	}
	
	public static Optional<String> language(String valueWithPotentialLanguageAndDatatype) {
		if(valueWithPotentialLanguageAndDatatype.contains("@")) {
			return Optional.of(valueWithPotentialLanguageAndDatatype.substring(valueWithPotentialLanguageAndDatatype.lastIndexOf('@')+1));
		}
		return Optional.empty();
	}
	
	public Optional<IRI> datatype(String valueWithPotentialLanguageAndDatatype) {
		if(valueWithPotentialLanguageAndDatatype.contains("^^")) {
			String dt = valueWithPotentialLanguageAndDatatype.substring(valueWithPotentialLanguageAndDatatype.lastIndexOf("^^")+2);
			if(usesKnownPrefix(dt)) {
				return Optional.of(SimpleValueFactory.getInstance().createIRI(this.uri(dt, false)));
			} else if (dt.startsWith("<http")){
				return Optional.of(SimpleValueFactory.getInstance().createIRI(dt.substring(1, dt.length()-2)));
			} else {
				return Optional.of(SimpleValueFactory.getInstance().createIRI(dt));
			}
		}
		return Optional.empty();
	}
	
}
