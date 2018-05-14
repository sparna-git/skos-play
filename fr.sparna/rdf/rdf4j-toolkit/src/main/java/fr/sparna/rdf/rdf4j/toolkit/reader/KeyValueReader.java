package fr.sparna.rdf.rdf4j.toolkit.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;

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
	
	// the cache of values
	private Map<Key, List<Value>> cache;

	private String query;
	private Function<Key, BindingSet> bindingSetGenerator;
	private BindingSetParserIfc<Key, Value> bindingSetParser;
	
	public KeyValueReader(
			String query,
			Function<Key, BindingSet> bindingSetGenerator,
			BindingSetParserIfc<Key, Value> bindingSetParser
	) {
		this.query = query;
		this.bindingSetGenerator = bindingSetGenerator;
		this.bindingSetParser = bindingSetParser;
	}
			
	/**
	 * Reads the (potentially multiple) values associated to the given key. To read a single value, use readUnique.
	 * 
	 * @param key	The key to read the values for.
	 * @return
	 * @throws SparqlPerformException
	 */
	public List<Value> read(Key key, RepositoryConnection connection) {
		// initialize at first call
		if(cache == null) {
			this.init(connection);
		}
		
		if(preLoad) {
			List<Value> result = cache.get(key);
			if(result == null) {
				ArrayList<Value> value = new ArrayList<Value>();
				cache.put(key, value);
				return value;
			} else {
				return result;
			}
		} else {
			if(cache.containsKey(key)) {
				return cache.get(key);
			} else {
				readFromRepository(key, connection);
				if(!cache.containsKey(key)) {
					cache.put(key, new ArrayList<Value>());
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
	public Value readUnique(Key key, RepositoryConnection connection) {
		List<Value> values = this.read(key, connection);
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
	private void init(RepositoryConnection connection) {
		this.cache = new HashMap<Key, List<Value>>();
		if(preLoad) {
			this.readFromRepository(null, connection);
		}
	}
	
	private void readFromRepository(Key key, RepositoryConnection connection) {		
		Perform.on(connection).select(
				new SimpleSparqlOperation(this.query, this.bindingSetGenerator.apply(key)),
				new AbstractTupleQueryResultHandler() {

					@Override
					public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
						Key k = bindingSetParser.parseKey(bindingSet);
						Value v = bindingSetParser.parseValue(bindingSet);
						if(!cache.containsKey(k)) {
							cache.put(k, new ArrayList<Value>());
						}
						cache.get(k).add(v);
					}
					
				}
		);
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
	
}
