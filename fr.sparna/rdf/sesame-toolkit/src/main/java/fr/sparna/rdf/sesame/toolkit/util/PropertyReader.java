package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.query.Perform;

/**
 * Reads a given property in a repository, and handles caching of values.
 * 
 * @author Thomas Francart
 *
 */
public class PropertyReader {

	protected java.net.URI propertyURI;
	protected String lang;
	protected java.net.URI additionalCriteriaProperty;
	protected java.net.URI additionalCriteriaObject;
	protected String additionalPath;
	
	protected boolean preLoad = true;
	
	private Repository repository;
	private ListMap<java.net.URI, Value> cache;

	/**
	 * Will read the given property with the given language in the repository, with a additional criteria on the set of subjects for which
	 * we want to read the property.
	 * 
	 * @param repository					The repository to read from
	 * @param propertyURI					The property URI to read
	 * @param lang							The language in which we want to read the property (optional, may be null)
	 * @param additionalCriteriaProperty	An additional property constraint on the resources on which we will be reading the property (optional, may be null)
	 * @param additionalCriteriaObject		A value for the additional property constraint (optional, may be null)
	 */
	public PropertyReader(
			Repository repository,
			java.net.URI propertyURI,
			String additionalPath,
			String lang,
			java.net.URI additionalCriteriaProperty,
			java.net.URI additionalCriteriaObject
	) {
		super();
		this.repository = repository;
		this.propertyURI = propertyURI;
		this.additionalPath = additionalPath;
		this.lang = lang;
		this.additionalCriteriaProperty = additionalCriteriaProperty;
		this.additionalCriteriaObject = additionalCriteriaObject;
	}
	
	/**
	 * Will read the given property with the given language in the repository
	 * 
	 * @param repository	The repository to read from
	 * @param propertyURI	The property URI to read
	 * @param lang			The language in which to read the property
	 */
	public PropertyReader(Repository repository, java.net.URI propertyURI, String lang) {
		this(repository, propertyURI, null, lang, null, null);
	}
	
	/**
	 * Will read the given property in the repository
	 * 
	 * @param repository	The repository to read from
	 * @param propertyURI	The property URI to read
	 */
	public PropertyReader(Repository repository, java.net.URI propertyURI) {
		this(repository, propertyURI, null, null, null, null);
	}

	public List<Value> read(java.net.URI subjectURI) 
	throws SPARQLPerformException {
		// initialize at first call
		if(cache == null) {
			this.init();
		}
		
		if(preLoad) {
			List<Value> result = cache.get(subjectURI);
			if(result == null) {
				cache.clear(subjectURI);
				return new ArrayList<Value>();
			} else {
				return result;
			}
		} else {
			if(cache.containsKey(subjectURI)) {
				return cache.get(subjectURI);
			} else {
				readFromRepository(subjectURI);
				if(!cache.containsKey(subjectURI)) {
					cache.clear(subjectURI);
				}
				return cache.get(subjectURI);
			}
		}
	}
	
	/**
	 * Creates the cache and pre-loads it if needed
	 * @throws SPARQLPerformException
	 */
	private void init() throws SPARQLPerformException {
		this.cache = new ListMap<URI, Value>();
		if(preLoad) {
			this.readFromRepository(null);
		}
	}
	
	private void readFromRepository(URI subjectURI) 
	throws SPARQLPerformException {
		ReadPropertyHelper helper = new ReadPropertyHelper(
				this.propertyURI,
				this.additionalPath,
				this.lang,
				subjectURI,
				this.additionalCriteriaProperty,
				this.additionalCriteriaObject
		) {			
			@Override
			protected void handleValue(Resource concept, Value value)
			throws TupleQueryResultHandlerException {
				// TODO : handle blank nodes
				cache.add(URI.create(concept.stringValue()), value);
			}
		};
		
		Perform.on(this.repository).select(helper);
	}

	public boolean isPreLoad() {
		return preLoad;
	}

	/**
	 * Set to true before reading the first value to tell the reader to pre-load the values for every resources in a single query.
	 * preLoad is true by default.
	 * 
	 * @param preLoad
	 */
	public void setPreLoad(boolean preLoad) {
		this.preLoad = preLoad;
	}

	public Repository getRepository() {
		return repository;
	}

	public URI getPropertyURI() {
		return propertyURI;
	}

	public String getLang() {
		return lang;
	}

	public URI getAdditionalCriteriaProperty() {
		return additionalCriteriaProperty;
	}

	public URI getAdditionalCriteriaObject() {
		return additionalCriteriaObject;
	}

}
