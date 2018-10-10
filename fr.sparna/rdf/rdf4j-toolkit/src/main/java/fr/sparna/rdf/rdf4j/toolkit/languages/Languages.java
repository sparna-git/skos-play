package fr.sparna.rdf.rdf4j.toolkit.languages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;


public final class Languages {

	// singleton instance
	private static Languages instance;
	
	// language cache
	private Map<String, Language> cache = new HashMap<String, Language>();
	
	// ISO-2 code to URI cache
	private Map<String, String> iso639P1Cache = new HashMap<String, String>();
	
	// repository containing language data
	private Repository repository;
	
	private Languages() {
		// load the language data
		this.repository = RepositoryBuilderFactory.fromString("fr/sparna/rdf/rdf4j/toolkit/languages/lexvo-languages.ttl").get();
	}
	
	public static Languages getInstance() {
		if(instance == null) {
			instance = new Languages();
		}
		return instance;
	}
	
	public Language withUri(String URI) {
		if(this.cache.containsKey(URI)) {
			return this.cache.get(URI);
		} else {
			Language l = new Language(URI, this.repository);
			this.cache.put(URI, l);
			return l;
		}
	}
	
	public Language withIso639P1(String code) {
		String uri;
		if(this.iso639P1Cache.containsKey(code)) {
			uri = this.iso639P1Cache.get(code);
		} else {
			uri = findWithCode(Lexvo.ISO639P1CODE, code);
			this.iso639P1Cache.put(code, uri);
		}
		
		if(uri == null) {
			return null;
		}
		
		return withUri(uri);
	}
	
	private String findWithCode(String codeProperty, String value) {
		try(RepositoryConnection connection = this.repository.getConnection()) {
			Value v = Perform.on(connection).read("SELECT ?l WHERE { ?l <"+codeProperty+"> ?value FILTER langMatches(\""+value+"\", ?value) .}");
			return (v != null)?v.stringValue():null;
		}
	}
	
	public class Language {
		
		private String uri;
		private Map<String, String> labels = new HashMap<String, String>();
		
		// the language repository
		private Repository repository;
		
		public Language(String uri, Repository repository) {
			this.uri = uri;
			this.repository = repository;
		}
		
		public String displayIn(String language) {
			if(labels.containsKey(language)) {
				return labels.get(language);
			} else {
				String display = generateDisplay(language);
				labels.put(language, display);
				return display;
			}
		}
		
		private String generateDisplay(String language) {
			try(RepositoryConnection connection = this.repository.getConnection()) {
				// if language is 'en', look for a skos:prefLabel
				// look for rdfs:label in the given language
				String query = "SELECT ?l WHERE { <"+this.uri+"> <"+RDFS.LABEL+"> ?l FILTER(langMatches('"+language+"', lang(?l))) }";
				List<Value> rdfsLabels = Perform.on(connection).readList(query);
				
				// if no labels can be found in the given language, return the language code (end of the URI)
				if(rdfsLabels.size() == 0) {
					return this.uri.substring(Lexvo.NAMESPACE_INSTANCES.length());
				} else if(rdfsLabels.size() == 1) {
					// if only one, perfect.
					return rdfsLabels.get(0).stringValue();
				} else {
					// if we have more than one, take the shortest one
					List<String> labels = new ArrayList<String>();
					for (Value v : rdfsLabels) {
						labels.add(v.stringValue());
					}
					
					List<String> shortestLabels = new ArrayList<String>();
					for (String aLabel : labels) {
						if(shortestLabels.size() == 0 || aLabel.length() < shortestLabels.get(0).length()) {
							shortestLabels.clear();
							shortestLabels.add(aLabel);
						}
						
						if(aLabel.length() == shortestLabels.get(0).length()) {
							shortestLabels.add(aLabel);
						}
					}
					
					if(shortestLabels.size() == 1) {
						return shortestLabels.get(0);
					} else {
						// if more than one have the same length, use the one starting with a capital letter
						List<String> startingWithCapital = new ArrayList<String>();
						for (String aShortLabel : shortestLabels) {
							if(Character.isUpperCase(aShortLabel.charAt(0))) {
								startingWithCapital.add(aShortLabel);
							}
						}
						
						if(startingWithCapital.size() == 0) {
							return shortestLabels.get(0);
						} else {
							// if more than one with same length and starting with capital letter, take the first one
							return startingWithCapital.get(0);
						}
					}					
				}
			}

		}
		
	}
	
	public static void main(String...strings) throws Exception {
		System.out.println(Languages.getInstance().withIso639P1("fr").displayIn("en"));
		System.out.println(Languages.getInstance().withIso639P1("en").displayIn("en"));
		System.out.println(Languages.getInstance().withIso639P1("de").displayIn("en"));
		System.out.println(Languages.getInstance().withIso639P1("fr").displayIn("fr"));
		System.out.println(Languages.getInstance().withIso639P1("en").displayIn("fr"));
		System.out.println(Languages.getInstance().withIso639P1("de").displayIn("fr"));
	}
	
}
