package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.commons.lang.LRUCache;
import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.handler.LiteralListHandler;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.ValuesSPARQLQueryBuilder;

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
			// URI.create("http://purl.org/dc/terms/title")
			// could also look for old dc property ?
			// ,URI.create("http://purl.org/dc/elements/1.1/title"),
	});
	
	// data to query on
	protected Repository repository;

	// preferred language to retrieve
	protected String preferredLanguage;
	
	// fallback language to retrieve if initial language was not found
	protected String fallbackLanguage;
	
	// list of properties to check in order for a value
	protected List<java.net.URI> labelProperties;
	
	// cache
	private static final int CACHE_SIZE = 10000;
	private Map<java.net.URI, List<Literal>> cache = Collections.synchronizedMap(new LRUCache<java.net.URI, List<Literal>>(CACHE_SIZE));
	
	// flag to activate caching or not
	protected boolean caching = true;
	
	public LabelReader(
			Repository repository,
			List<java.net.URI> labelProperties,
			String fallbackLanguage,
			String preferredLanguage) {
		this.repository = repository;
		this.labelProperties = labelProperties;
		this.fallbackLanguage = fallbackLanguage;
		this.preferredLanguage = preferredLanguage;
	}
	
	public LabelReader(Repository repository, String fallbackLanguage, String preferredLanguage) {
		this(
				repository,
				DEFAULT_LABEL_PROPERTIES,
				fallbackLanguage,
				preferredLanguage
		);
	}
	
	public LabelReader(Repository repository, String preferredLanguage) {
		this(
				repository,
				DEFAULT_LABEL_PROPERTIES,
				null,
				preferredLanguage
		);
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
	
	public List<Literal> getLabels(final java.net.URI resource) 
	throws SPARQLPerformException {
		
		// look into the cache first
		if(isCaching() && cache.containsKey(resource)) {
			return cache.get(resource);
		}
		
		List<SPARQLQuery> queries = new ArrayList<SPARQLQuery>();
		
		// for each possible property in order ...
		for (final java.net.URI aType : this.labelProperties) {
			// query for the preferredLanguage
			queries.add(new SPARQLQuery(
					"SELECT ?label WHERE { ?uri ?labelProp ?label FILTER(lang(?label) = '"+this.preferredLanguage+"') }",
					new HashMap<String, Object>() {{ 
						put("uri", resource);
						put("labelProp", aType);
					}}
			));
			
			// then for the fallback language
			if(this.fallbackLanguage != null) {
				queries.add(new SPARQLQuery(
						"SELECT ?label WHERE { ?uri ?labelProp ?label FILTER(lang(?label) = '"+this.fallbackLanguage+"') }",
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
			result.add(this.repository.getValueFactory().createLiteral(
					Namespaces.getInstance().withRepository(this.repository).shorten(resource.toString())
			));
		}
		
		if(isCaching()) {
			this.cache.put(resource, result);
		}
		return result;
	}	
	
	public List<Literal> getLabels(final org.openrdf.model.URI resource) 
	throws SPARQLPerformException {
		return getLabels(URI.create(resource.stringValue()));
	}
	
	public Map<java.net.URI, List<Literal>> getLabels(Collection<java.net.URI> resources)
	throws SPARQLPerformException {
		// prepare result
		ListMap<java.net.URI, Literal> result = new ListMap<java.net.URI, Literal>();
		
		// the remaining work we have to do
		Set<java.net.URI> resourcesToProcess = new HashSet<java.net.URI>(resources);
		
		// first look in the cache
		if(caching) {
			for (URI uri : resourcesToProcess) {
				if(cache.containsKey(uri)) {
					result.put(uri, cache.get(uri));
				}
			}
		}
		
		// for each property ...
		for (URI aProperty : this.labelProperties) {
			// try to get a value for each of the resources for that property
			Map<java.net.URI, List<Literal>> labels = getLabelsOnProperty(resourcesToProcess, aProperty);
			// add that to the final result
			result.putAll(labels);
			// remove what we have found from the work to be done
			resourcesToProcess.removeAll(labels.keySet());
			// if we have found everything, we can break
			if(resourcesToProcess.size() == 0) {
				break;
			}
		}
		
		// for each resources for which a value wasn't found, create a default value
		for (URI uri : resourcesToProcess) {
			result.add(
					uri, 
					this.repository.getValueFactory().createLiteral(Namespaces.getInstance().withRepository(this.repository).shorten(uri.toString())
			));
		}
		
		// feed the cache
		if(caching) {
			this.cache.putAll(result);
		}
		
		return result;
	}
	
	private Map<java.net.URI, List<Literal>> getLabelsOnProperty(Set<java.net.URI> resources, final java.net.URI property) 
	throws SPARQLPerformException {
		
		final int CHUNK_SIZE = 100;
		ListMap<java.net.URI, Literal> result = new ListMap<java.net.URI, Literal>();
		
		// gather chunks to process
		List<java.net.URI> chunk = new ArrayList<java.net.URI>();
		
		// for each URI...
		for (URI aUri : resources) {
			// add it to the chunk
			chunk.add(aUri);
			
			// if we have reached our chunk size
			if(chunk.size() >= CHUNK_SIZE) {
				// do stuff
				result.putAll(processChunkOnProperty(chunk, property));
				
				// then reset chunk
				chunk = new ArrayList<java.net.URI>();
			}
		}
		
		// process last part of the cunk
		if(chunk.size() > 0) {
			result.putAll(processChunkOnProperty(chunk, property));
		}
		
		return result;
	}
	
	private Map<java.net.URI, List<Literal>> processChunkOnProperty(List<java.net.URI> resources, final java.net.URI property) 
	throws SPARQLPerformException {
		
		String query = "SELECT ?uri ?label WHERE { ?uri ?labelProp ?label FILTER(lang(?label) = '"+this.preferredLanguage+"') }";
		ValuesSPARQLQueryBuilder builder = new ValuesSPARQLQueryBuilder(
				new SPARQLQueryBuilder(query),
				"uri",
				Arrays.asList(URIUtil.toResourceArray(resources, repository.getValueFactory()))
		);
		
		SPARQLQuery q = new SPARQLQuery(
				builder,
				new HashMap<String, Object>() {{ 
					put("labelProp", property);
				}}
		);
		
		System.out.println(q.getSPARQL());
		
		final ListMap<java.net.URI, Literal> result = new ListMap<java.net.URI, Literal>();
		Perform.on(this.repository).select(new SelectSPARQLHelper(
				q,
				new TupleQueryResultHandlerBase() {

					@Override
					public void handleSolution(BindingSet bindingSet)
					throws TupleQueryResultHandlerException {
						Resource uri = (Resource)bindingSet.getValue("uri");
						Literal label = (Literal)bindingSet.getValue("label");
						result.add(URI.create(uri.stringValue()), label);
					}
					
				}
		));
		
		return result;
	}
	
	public boolean isCaching() {
		return caching;
	}

	public void setCaching(boolean caching) {
		this.caching = caching;
	}

	private List<Literal> findALabel(List<SPARQLQuery> queries) 
	throws SPARQLPerformException {
		LiteralListHandler h = new LiteralListHandler();
		
		// for each query
		for (SPARQLQuery aQuery : queries) {
			Perform.on(this.repository).select(new SelectSPARQLHelper(
					aQuery,
					h
			));
			
			// as soon as we find one, exit
			if(h.getResult().size() > 0) {
				break;
			}
		}
		
		return h.getResult();
	}
}
