package fr.sparna.rdf.sesame.toolkit.reader;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;

/**
 * Reads a Value for a given Key, and handles a cache of the Values. The Values can be preloaded
 * if the appropriate flag is set prior to the first call (true by default).
 * 
 * @author Thomas Francart
 *
 */
public class KeyValueReader<Key, Value> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// a flag indicating if we need to preload all the values
	protected boolean preLoad = true;
	
	// the repository we are querying
	private Repository repository;
	
	// the cache of values
	private ListMap<Key, Value> cache;

	private KeyValueHelperIfc<Key, Value> helper;
	

	public KeyValueReader(
			Repository repository,
			KeyValueHelperIfc<Key, Value> helper
	) {
		this.repository = repository;
		this.helper = helper;
	}
			
	/**
	 * Reads the (potentially multiple) values associated to the given key. To read a single value, use readUnique.
	 * 
	 * @param key	The key to read the values for.
	 * @return
	 * @throws SparqlPerformException
	 */
	public List<Value> read(Key key) 
	throws SparqlPerformException {
		// initialize at first call
		if(cache == null) {
			this.init();
		}
		
		if(preLoad) {
			List<Value> result = cache.get(key);
			if(result == null) {
				cache.clear(key);
				return new ArrayList<Value>();
			} else {
				return result;
			}
		} else {
			if(cache.containsKey(key)) {
				return cache.get(key);
			} else {
				readFromRepository(key);
				if(!cache.containsKey(key)) {
					cache.clear(key);
				}
				return cache.get(key);
			}
		}
	}
	
	/**
	 * Reads a single value for the given key. If multiple values exist for the given key,
	 * returns the first one in the list.
	 * 
	 * @param key	The key to read the single value for.
	 * @return
	 * @throws SparqlPerformException
	 */
	public Value readUnique(Key key) 
	throws SparqlPerformException {
		List<Value> values = this.read(key);
		if(values.size() > 0) {
			return values.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates the cache and pre-loads it if needed
	 * @throws SparqlPerformException
	 */
	private void init() throws SparqlPerformException {
		this.cache = new ListMap<Key, Value>();
		if(preLoad) {
			this.readFromRepository(null);
		}
	}
	
	private void readFromRepository(Key key) 
	throws SparqlPerformException {		
		Perform.on(this.repository).select(new SelectSparqlHelper(
				new SparqlQuery(this.helper.getSPARQLQueryBuilder(), this.helper.getKeyMappingGenerator().generate(key)),
				new TupleQueryResultHandlerBase() {

					@Override
					public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
						Key k = helper.getKeyValueBindingSetReader().readKey(bindingSet);
						Value v = helper.getKeyValueBindingSetReader().readValue(bindingSet);
						// log.trace("Storing in cache key="+k+", value="+v);
						cache.add(k, v);
					}
					
				}
		));
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
	
}
