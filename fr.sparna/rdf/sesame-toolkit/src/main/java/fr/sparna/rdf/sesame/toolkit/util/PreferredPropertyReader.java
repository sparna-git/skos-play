package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URI;
import java.security.InvalidParameterException;
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
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.commons.lang.LRUCache;
import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.handler.ReadValueListHandler;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.ValuesSparqlQueryBuilder;

/**
 * Returns a list of values for an ordered list of properties, for given resource or list of resources, in a given language.
 * 
 * @author Thomas Francart
 */
public class PreferredPropertyReader {
	
	// data to query on
	protected Repository repository;

	// preferred language to retrieve
	protected String preferredLanguage;
	
	// fallback language to retrieve if initial language was not found
	protected String fallbackLanguage;
	
	// list of properties to check in order for a value
	protected List<java.net.URI> properties;
	
	// cache
	private static final int CACHE_SIZE = 10000;
	private Map<java.net.URI, List<Value>> cache = Collections.synchronizedMap(new LRUCache<java.net.URI, List<Value>>(CACHE_SIZE));
	
	// flag to activate caching or not
	protected boolean caching = true;
	
	public PreferredPropertyReader(
			Repository repository,
			List<java.net.URI> properties,
			String fallbackLanguage,
			String preferredLanguage) {
		
		// prevent null language
		if(preferredLanguage == null) {
			throw new InvalidParameterException("preferredLanguage cannot be null (but can be the empty string");
		}
		
		this.repository = repository;
		this.properties = properties;
		this.fallbackLanguage = fallbackLanguage;
		this.preferredLanguage = preferredLanguage;
	}
	
	public List<Value> getValues(final java.net.URI resource) 
	throws SparqlPerformException {
		
		// look into the cache first
		if(isCaching() && cache.containsKey(resource)) {
			return cache.get(resource);
		}
		
		List<SparqlQuery> queries = new ArrayList<SparqlQuery>();
		
		// for each possible property in order ...
		for (final java.net.URI aType : this.properties) {
			// query for the preferredLanguage
			// if preferredLanguage is the empty string, this will query for labels without a language
			queries.add(new SparqlQuery(
					"SELECT ?o WHERE { ?s ?p ?o FILTER(lang(?o) = '"+this.preferredLanguage+"') }",
					new HashMap<String, Object>() {{ 
						put("s", resource);
						put("p", aType);
					}}
			));
			
			// then for the fallback language
			if(this.fallbackLanguage != null) {
				queries.add(new SparqlQuery(
						"SELECT ?o WHERE { ?s ?p ?o FILTER(lang(?o) = '"+this.fallbackLanguage+"') }",
						new HashMap<String, Object>() {{ 
							put("s", resource);
							put("p", aType);
						}}
				));
			}
			
			// TODO then for values with no language ?
		}
		
		List<Value> result = findValues(queries);
		
		if(isCaching()) {
			this.cache.put(resource, result);
		}
		return result;
	}	
	
	public List<Value> getValues(final org.openrdf.model.URI resource) 
	throws SparqlPerformException {
		return getValues(URI.create(resource.stringValue()));
	}
	
	public Map<java.net.URI, List<Value>> getValues(Collection<java.net.URI> resources)
	throws SparqlPerformException {
		// prepare result
		ListMap<java.net.URI, Value> result = new ListMap<java.net.URI, Value>();
		
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
		for (URI aProperty : this.properties) {
			// try to get a value for each of the resources for that property
			Map<java.net.URI, List<Value>> values = getLabelsOnProperty(resourcesToProcess, aProperty);
			// add that to the final result
			result.putAll(values);
			// remove what we have found from the work to be done
			resourcesToProcess.removeAll(values.keySet());
			// if we have found everything, we can break
			if(resourcesToProcess.size() == 0) {
				break;
			}
		}
		
		// for each resources for which a value wasn't found, create a default value
		for (URI uri : resourcesToProcess) {
			result.put(
					uri, 
					new ArrayList<Value>()
			);
		}
		
		// feed the cache
		if(caching) {
			this.cache.putAll(result);
		}
		
		return result;
	}
	
	private Map<java.net.URI, List<Value>> getLabelsOnProperty(Set<java.net.URI> resources, final java.net.URI property) 
	throws SparqlPerformException {
		
		final int CHUNK_SIZE = 100;
		ListMap<java.net.URI, Value> result = new ListMap<java.net.URI, Value>();
		
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
		
		// process last part of the chunk
		if(chunk.size() > 0) {
			result.putAll(processChunkOnProperty(chunk, property));
		}
		
		return result;
	}
	
	private Map<java.net.URI, List<Value>> processChunkOnProperty(List<java.net.URI> resources, final java.net.URI property) 
	throws SparqlPerformException {
		
		String query = "SELECT ?s ?o WHERE { ?s ?p ?o FILTER(lang(?o) = '"+this.preferredLanguage+"') }";
		ValuesSparqlQueryBuilder builder = new ValuesSparqlQueryBuilder(
				new SparqlQueryBuilder(query),
				"s",
				Arrays.asList(UriUtil.toResourceArray(resources, repository.getValueFactory()))
		);
		
		SparqlQuery q = new SparqlQuery(
				builder,
				new HashMap<String, Object>() {{ 
					put("p", property);
				}}
		);
		
		final ListMap<java.net.URI, Value> result = new ListMap<java.net.URI, Value>();
		Perform.on(this.repository).select(new SelectSparqlHelper(
				q,
				new TupleQueryResultHandlerBase() {

					@Override
					public void handleSolution(BindingSet bindingSet)
					throws TupleQueryResultHandlerException {
						Resource uri = (Resource)bindingSet.getValue("s");
						Value v = bindingSet.getValue("o");
						result.add(URI.create(uri.stringValue()), v);
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

	public List<java.net.URI> getProperties() {
		return properties;
	}

	public void setProperties(List<java.net.URI> properties) {
		this.properties = properties;
	}

	private List<Value> findValues(List<SparqlQuery> queries) 
	throws SparqlPerformException {
		ReadValueListHandler h = new ReadValueListHandler();
		
		// for each query
		for (SparqlQuery aQuery : queries) {
			Perform.on(this.repository).select(new SelectSparqlHelper(
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
