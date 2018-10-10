package fr.sparna.rdf.rdf4j.toolkit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;

/**
 * Reads a given property in a repository, and handles caching of values.
 * 
 * @deprecated use PropertyValueReader.GenericQueryBuilder
 * @author Thomas Francart
 *
 */
public class PropertyReader {

	protected IRI propertyURI;
	protected String lang;
	protected IRI additionalCriteriaProperty;
	protected IRI additionalCriteriaObject;
	protected String additionalPath;
	
	protected boolean preLoad = true;
	
	private RepositoryConnection connection;
	private Map<IRI, List<Value>> cache;

	/**
	 * @deprecated Don't use the additionnalCriteria parameters
	 * Will read the given property with the given language in the repository, with a additional criteria on the set of subjects for which
	 * we want to read the property.
	 * 
	 * @param repository					The repository to read from
	 * @param propertyURI					The property URI to read
	 * @param additionalPath				An additional path to append to the predicate URI
	 * @param lang							The language in which we want to read the property (optional, may be null)
	 * @param additionalCriteriaProperty	An additional property constraint on the resources on which we will be reading the property (optional, may be null)
	 * @param additionalCriteriaObject		A value for the additional property constraint (optional, may be null)
	 */
	public PropertyReader(
			RepositoryConnection connection,
			IRI propertyURI,
			String additionalPath,
			String lang,
			IRI additionalCriteriaProperty,
			IRI additionalCriteriaObject
	) {
		super();
		this.connection = connection;
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
	public PropertyReader(RepositoryConnection connection, IRI propertyURI, String lang) {
		this(connection, propertyURI, null, lang, null, null);
	}
	
	/**
	 * Will read the given property with the given language in the repository
	 * 
	 * @param repository		The repository to read from
	 * @param propertyURI		The property URI to read
	 * @param additionalPath	An additionnal path to append to the predicate URI
	 * @param lang				The language in which to read the property
	 */
	public PropertyReader(RepositoryConnection connection, IRI propertyURI, String additionalPath, String lang) {
		this(connection, propertyURI, additionalPath, lang, null, null);
	}
	
	/**
	 * Will read the given property in the repository
	 * 
	 * @param repository	The repository to read from
	 * @param propertyURI	The property URI to read
	 */
	public PropertyReader(RepositoryConnection connection, IRI propertyURI) {
		this(connection, propertyURI, null, null, null, null);
	}

	public List<Value> read(IRI subjectURI) {
		// initialize at first call
		if(cache == null) {
			this.init();
		}
		
		if(preLoad) {
			List<Value> result = cache.get(subjectURI);
			if(result == null) {
				cache.remove(subjectURI);
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
					cache.remove(subjectURI);
				}
				return cache.get(subjectURI);
			}
		}
	}
	
	/**
	 * Creates the cache and pre-loads it if needed
	 * @throws SparqlPerformException
	 */
	private void init() {
		this.cache = new HashMap<IRI, List<Value>>();
		if(preLoad) {
			this.readFromRepository(null);
		}
	}
	
	private void readFromRepository(IRI subjectURI) {
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
				if(cache.containsKey((IRI)concept)) {
					cache.get((IRI)concept).add(value);
				}
			}
		};
		
		Perform.on(this.connection).select(helper);
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

	public IRI getPropertyURI() {
		return propertyURI;
	}

	public String getLang() {
		return lang;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public IRI getAdditionalCriteriaProperty() {
		return additionalCriteriaProperty;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public IRI getAdditionalCriteriaObject() {
		return additionalCriteriaObject;
	}

}
