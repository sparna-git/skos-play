package fr.sparna.rdf.rdf4j.toolkit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.commons.lang.LRUCache;
import fr.sparna.rdf.rdf4j.toolkit.handler.ReadValueListHandler;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReader;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.SparqlOperationIfc;
import fr.sparna.rdf.rdf4j.toolkit.query.ValuesSparqlQueryBuilder;


/**
 * Returns a list of values for an ordered list of properties, for given resource or list of resources, in a given language.
 * 
 * @author Thomas Francart
 */
public class PreferredPropertyReader {
	
	// data to query on
	protected RepositoryConnection connection;

	// preferred language to retrieve
	protected String preferredLanguage;
	
	// fallback languages to retrieve if initial language was not found
	protected List<String> fallbackLanguages;
	
	// list of properties to check in order for a value
	protected List<IRI> properties;
	
	// cache
	private static final int CACHE_SIZE = 10000;
	private Map<IRI, List<Value>> cache = Collections.synchronizedMap(new LRUCache<IRI, List<Value>>(CACHE_SIZE));
	
	// flag to activate caching or not
	protected boolean caching = true;
	
	public PreferredPropertyReader(
			RepositoryConnection connection,
			List<IRI> properties,
			List<String> fallbackLanguages,
			String preferredLanguage) {
		
		this.connection = connection;
		this.properties = properties;
		this.fallbackLanguages = fallbackLanguages;
		this.preferredLanguage = preferredLanguage;
	}
	
	/**
	 * @deprecated use constructor with List<String> for fallbackLanguages
	 */
	public PreferredPropertyReader(
			RepositoryConnection connection,
			List<IRI> properties,
			String fallbackLanguage,
			String preferredLanguage) {
		this(
				connection,
				properties,
				Collections.singletonList(fallbackLanguage),
				preferredLanguage
		);
	}
	
	public PreferredPropertyReader(
			RepositoryConnection connection,
			List<IRI> properties,
			String preferredLanguage) {	
		this(connection, properties, preferredLanguage, null);
	}
	
	public PreferredPropertyReader(
			RepositoryConnection connection,
			IRI property,
			String preferredLanguage) {	
		this(connection, Arrays.asList(property), preferredLanguage, null);
	}
	
	public PreferredPropertyReader(
			RepositoryConnection connection,
			List<IRI> properties) {	
		this(connection, properties, (List<String>)null, null);
	}
	
	public PreferredPropertyReader(
			RepositoryConnection connection,
			IRI property) {	
		this(connection, Arrays.asList(property), (List<String>)null, null);
	}
	
	public List<Value> getValues(final IRI resource) {
		
		// look into the cache first
		if(isCaching() && cache.containsKey(resource)) {
			return cache.get(resource);
		}
		
		List<SparqlOperationIfc> queries = new ArrayList<SparqlOperationIfc>();
		
		// for each possible property in order ...
		for (final IRI aType : this.properties) {
			// query for the preferredLanguage
			// if preferredLanguage is the empty string, this will query for labels without a language
			if(this.preferredLanguage != null) {
				queries.add(new SimpleSparqlOperation(
						"SELECT ?o WHERE { ?s ?p ?o FILTER(langMatches(lang(?o), '"+this.preferredLanguage+"')) }",
						Arrays.asList(new Binding[] { new SimpleBinding("s", resource) , new SimpleBinding("p", aType) })
				));
			} else {
				queries.add(new SimpleSparqlOperation(
						"SELECT ?o WHERE { ?s ?p ?o }",
						Arrays.asList(new Binding[] { new SimpleBinding("s", resource) , new SimpleBinding("p", aType) })
				));
			}
			
			// then for the fallback languages
			if(this.fallbackLanguages != null) {
				for (String aLanguage : fallbackLanguages) {
					queries.add(new SimpleSparqlOperation(
							"SELECT ?o WHERE { ?s ?p ?o FILTER(langMatches(lang(?o), '"+aLanguage+"')) }",
							Arrays.asList(new Binding[] { new SimpleBinding("s", resource) , new SimpleBinding("p", aType) })
					));
				}				
			}
		}
		
		List<Value> result = findValues(queries);
		
		if(isCaching()) {
			this.cache.put(resource, result);
		}
		return result;
	}	
	
	public Map<IRI, List<Value>> getValues(Collection<IRI> resources) {
		// prepare result
		Map<IRI, List<Value>> result = new HashMap<IRI, List<Value>>();
		
		// the remaining work we have to do
		Set<IRI> resourcesToProcess = new HashSet<IRI>(resources);
		
		// first look in the cache
		if(caching) {
			for (IRI iri : resourcesToProcess) {
				if(cache.containsKey(iri)) {
					result.put(iri, cache.get(iri));
				}
			}
		}
		
		// for each property ...
		for (IRI aProperty : this.properties) {
			// try to get a value for each of the resources for that property
			Map<IRI, List<Value>> values = getValuesOnProperty(resourcesToProcess, aProperty);
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
		for (IRI uri : resourcesToProcess) {
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
	
	private Map<IRI, List<Value>> getValuesOnProperty(Set<IRI> resources, final IRI property) {
		
		final int CHUNK_SIZE = 100;
		Map<IRI, List<Value>> result = new HashMap<IRI, List<Value>>();
		
		// gather chunks to process
		List<IRI> chunk = new ArrayList<IRI>();
		
		// for each URI...
		for (IRI aUri : resources) {
			// add it to the chunk
			chunk.add(aUri);
			
			// if we have reached our chunk size
			if(chunk.size() >= CHUNK_SIZE) {
				// do stuff
				result.putAll(processChunkOnProperty(chunk, property));
				
				// then reset chunk
				chunk = new ArrayList<IRI>();
			}
		}
		
		// process last part of the chunk
		if(chunk.size() > 0) {
			result.putAll(processChunkOnProperty(chunk, property));
		}
		
		return result;
	}
	
	private Map<IRI, List<Value>> processChunkOnProperty(List<IRI> resources, final IRI property) {
		
		Map<IRI, List<Value>> result = processChunkOnPropertyAndLanguage(resources, property, this.preferredLanguage);
		
		if(this.fallbackLanguages != null) {
			for (String aLanguage : this.fallbackLanguages) {
				// now remove what was found from original list
				resources.removeAll(result.keySet());
				
				// stop if we have found everything
				if(resources.size() == 0) {
					break;
				}
				
				// and try with this language
				Map<IRI, List<Value>> fallbacklanguageResult = processChunkOnPropertyAndLanguage(resources, property, aLanguage);
				result.putAll(fallbacklanguageResult);
			}			
		}
		
		return result;
	}
	
	private Map<IRI, List<Value>> processChunkOnPropertyAndLanguage(List<IRI> resources, final IRI property, final String language) {
		
		// if there is no preferred language, don't specify a FILTER to be able to fetch object properties
		String query;
		if(language != null) {
			query = "SELECT ?s ?o WHERE { ?s ?p ?o FILTER(langMatches(lang(?o), '"+language+"')) }";
		} else {
			query = "SELECT ?s ?o WHERE { ?s ?p ?o }";
		}
		// add a VALUES with the list of URIs
		ValuesSparqlQueryBuilder builder = new ValuesSparqlQueryBuilder(
				new SimpleQueryReader(query),
				"s",
				resources
		);
		
		// bind the p variable to the property
		SimpleSparqlOperation operation = new SimpleSparqlOperation(builder).withBinding(
				new SimpleBinding("p", property)
		);
		
		// execute and store results
		final Map<IRI, List<Value>> result = new HashMap<IRI, List<Value>>();
		
		Perform.on(this.connection).select(
				operation, 
				new AbstractTupleQueryResultHandler() {
					@Override
					public void handleSolution(BindingSet bindingSet)
					throws TupleQueryResultHandlerException {
						Resource uri = (Resource)bindingSet.getValue("s");
						Value v = bindingSet.getValue("o");
						if(result.containsKey(uri)) {
							result.get(uri).add(v);
						} else {
							result.put((IRI)uri, new ArrayList<Value>());
						}
					}					
				}
		);
		
		return result;
	}
	
	public boolean isCaching() {
		return caching;
	}

	public void setCaching(boolean caching) {
		this.caching = caching;
	}

	public List<IRI> getProperties() {
		return properties;
	}

	public void setProperties(List<IRI> properties) {
		this.properties = properties;
	}

	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	public List<String> getFallbackLanguages() {
		return fallbackLanguages;
	}

	private List<Value> findValues(List<SparqlOperationIfc> operations) {
		ReadValueListHandler h = new ReadValueListHandler();
		
		// for each query
		for (SparqlOperationIfc anOperation : operations) {
			Perform.on(this.connection).select(
					anOperation,
					h
			);
			
			// as soon as we find one, exit
			if(h.getResult().size() > 0) {
				break;
			}
		}
		
		return h.getResult();
	}
}
