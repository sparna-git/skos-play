package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.commons.lang.LRUCache;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;

/**
 * Returns a label for given resource or list of resources, in a given language.
 * The algorithm is as follow
 * 
 * 
 * @author Thomas Francart
 *
 */
public class LabelReader {

	public static final List<java.net.URI> DEFAULT_LABEL_PROPERTIES = Arrays.asList(new java.net.URI[] {
			// skos:prefLabel first
			URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"),
			// rdfs:label
			URI.create(RDFS.LABEL.toString()),
			// dcterms title
			URI.create("http://purl.org/dc/terms/title")
			// could also look for old dc property ?
			// ,URI.create("http://purl.org/dc/elements/1.1/title"),
	});
	
	// data to query on
	protected Repository repository;
	
	// fallback language to retrieve if initial language was not found
	protected String fallbackLanguage;
	
	// list of properties to check in order for a value
	protected List<java.net.URI> labelProperties;
	
	// cache
	private static final int CACHE_SIZE = 1000;
	private Map<java.net.URI, List<Literal>> cache = Collections.synchronizedMap(new LRUCache<java.net.URI, List<Literal>>(CACHE_SIZE));
	
	// flag to activate caching or not
	protected boolean caching = true;
	
	public LabelReader(Repository repository, List<java.net.URI> labelProperties, String fallbackLanguage) {
		this.repository = repository;
		this.labelProperties = labelProperties;
		this.fallbackLanguage = fallbackLanguage;
	}
	
	public LabelReader(Repository repository, String fallbackLanguage) {
		this(repository, DEFAULT_LABEL_PROPERTIES, fallbackLanguage);
	}
	
	public LabelReader(Repository repository) {
		this(repository, DEFAULT_LABEL_PROPERTIES, null);
	}
	
	public static String display(List<Literal> literals) {
		StringBuffer buffer = new StringBuffer();
		if(literals != null) {
			for (Literal literal : literals) {
				buffer.append(literal.stringValue());
				buffer.append(", ");
			}
			// remove last garbage if needed
			if(buffer.length() > 2)
				buffer.delete(buffer.length() - ", ".length(), buffer.length());
		}
		return buffer.toString();
	}
	
	public List<Literal> getLabels(org.openrdf.model.URI resource, final String lang) 
	throws SPARQLExecutionException {
		return getLabels(URI.create(resource.stringValue()), lang);
	}
	
	public List<Literal> getLabels(final java.net.URI resource, final String lang) 
	throws SPARQLExecutionException {
		
		// look into the cache first
		if(isCaching() && cache.containsKey(resource)) {
			return cache.get(resource);
		}
		
		List<SPARQLQuery> queries = new ArrayList<SPARQLQuery>();
		
		// for each possible property in order ...
		for (final java.net.URI aType : this.labelProperties) {
			// query for the preferredLanguage
			queries.add(new SPARQLQuery(
					"SELECT ?label WHERE { ?uri ?labelProp ?label FILTER(lang(?label) = '"+lang+"') }",
					new HashMap<String, Object>() {{ 
						put("uri", resource);
						put("labelProp", aType);
					}}
			));
			
			// then for the fallback language
			if(this.fallbackLanguage != null) {
				queries.add(new SPARQLQuery(
						"SELECT ?label WHERE { ?uri ?labelProp ?label FILTER(lang(?label) = '"+fallbackLanguage+"') }",
						new HashMap<String, Object>() {{ 
							put("uri", resource);
							put("labelProp", aType);
						}}
				));
			}
			
			// TODO then for values with no language ?
		}
		
		List<Literal> result = findALabel(queries);
		
		// if nothing was found add the URI itself in the list of values
		if(result.size() == 0) {
			result.add(this.repository.getValueFactory().createLiteral(Namespaces.getInstance().withRepository(this.repository).shorten(resource.toString())));
		}
		
		if(isCaching()) {
			this.cache.put(resource, result);
		}
		return result;
	}
	
	public Map<java.net.URI, List<Literal>> getLabels(Set<java.net.URI> resources, String lang) {
		throw new UnsupportedOperationException("Not implement yet");
	}
	
	public boolean isCaching() {
		return caching;
	}

	public void setCaching(boolean caching) {
		this.caching = caching;
	}

	private List<Literal> findALabel(List<SPARQLQuery> queries) 
	throws SPARQLExecutionException {
		final List<Literal> result = new ArrayList<Literal>();
		
		SesameSPARQLExecuter executer = SesameSPARQLExecuter.newExecuter(this.repository);
		// for each query
		for (SPARQLQuery aQuery : queries) {
			executer.executeSelect(new SelectSPARQLHelper(
					aQuery,
					new TupleQueryResultHandlerBase() {
						@Override
						public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
							result.add((Literal)bindingSet.getValue("label"));
						}						
					}
			));
			
			// as soon as we find one, exit
			if(result.size() > 0) {
				break;
			}
		}
		
		return result;
	}
}
