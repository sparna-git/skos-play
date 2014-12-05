package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Namespace;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromUrl;

/**
 * Manages the Map between full URI and their prefixes. This will attempt to load the dump of prefix.cc
 * to know the most common prefixes, but if it fails it will load a pre-bundled file (see LoadURL).
 * 
 * @author Thomas Francart
 *
 */
public class Namespaces {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Singleton instance
	 */
	private static Namespaces instance;
	
	/**
	 * key : the full namespace URI
	 * value : the prefix
	 */
	private Map<String, String> namespaceMap;
	
	/**
	 * Singleton protected constructor
	 */
	protected Namespaces(boolean live) {
		this.namespaceMap = new HashMap<String, String>();
		this.initNamespaceMap(live);
	}
	
	/**
	 * Set the "live" parameter to true to attempt to dynamically load the prefix data
	 * from prefix.cc.
	 */
	public static Namespaces getInstance(boolean live) {
		if(instance == null) {
			instance = new Namespaces(live);
		}
		return instance;
	}
	
	/**
	 * Singleton static accessor
	 */
	public static Namespaces getInstance() {
		if(instance == null) {
			instance = new Namespaces(false);
		}
		return instance;
	}
	
	public Namespaces withRepository(Repository r) {
		log.debug("Registering repository namespaces...");
		RepositoryConnection c = null;
		try {
			// registers RepositoryConnection namespaces
			c = r.getConnection();
			List<Namespace> repoNamespaces = c.getNamespaces().asList();
			for (Namespace namespace : repoNamespaces) {
				if(!this.namespaceMap.containsKey(namespace.getPrefix())) {
					log.debug("Reading unknown namespace from repository '"+namespace.getPrefix()+"' : <"+namespace.getName()+">");
					this.namespaceMap.put(namespace.getName(), namespace.getPrefix());
				}
			}
		} catch (RepositoryException e) {
			// don't do anything
			e.printStackTrace();
		} finally {
			RepositoryConnectionDoorman.closeQuietly(c);
		}
		
		return this;
	}
	
	public String shorten(String fullURI) {
		String prefix = getPrefix(split(fullURI)[0]);
		if(prefix == null) {
			return fullURI;
		} else {
			return prefix+":"+split(fullURI)[1]; 
		}		
	}
	
	/**
	 * Returns an array with the namespace part in the first position, and local part in the second position
	 * @param fullURI
	 * @return
	 */
	public String[] split(String fullURI) {
		// String[0] : namespace uri
		// String[1] : local part
		if(fullURI.lastIndexOf('#') > 0) {
			return new String[] { fullURI.substring(0, fullURI.lastIndexOf('#')+1), fullURI.substring(fullURI.lastIndexOf('#')+1) };
		} else if (fullURI.lastIndexOf('/') > 0) {
			return new String[] { fullURI.substring(0, fullURI.lastIndexOf('/')+1), fullURI.substring(fullURI.lastIndexOf('/')+1) };
		} else {
			return new String[] { fullURI, "" };
		}
	}
	
	public String getPrefix(String namespace) {
		// TODO : creates and return auto-generated prefix if not found
		return this.namespaceMap.get(namespace);
	}
	
	public String getURI(String prefix) {
		// TODO : throws exception if prefix not found ?
		for (Map.Entry<String, String> anEntry : this.namespaceMap.entrySet()) {
			if(anEntry.getValue().equals(prefix)) {
				return anEntry.getKey();
			}
		}
		
		return null;
	}
	
	public Map<String, String> getNamespaceMap() {
		return namespaceMap;
	}
	
	private void initNamespaceMap(boolean live) {
		try {
			

			RepositoryBuilder builder = new RepositoryBuilder(new LocalMemoryRepositoryFactory());
			// DO NOT REGISTERS NAMESPACES AUTOMATICALLY OTHERWISE : INFINITE LOOP
			builder.setAutoRegisterNamespaces(false);
			if(live) {
				// load URL - will load the bundled file in the jar if the URL loading fails
				builder.addOperation(new LoadFromUrl(new URL("http://prefix.cc/popular/all.file.vann")));
			} else {
				// LoadFromFileOrDirectory has a test to load from classpath
				builder.addOperation(new LoadFromFileOrDirectory("popular/all.file.vann"));
			}
			
			Repository r = builder.createNewRepository();
			
			// make a query on the loaded RDF and populate the namespaceMap with the result
			Perform.on(r).select(
					new SelectSparqlHelper(
							"PREFIX vann:<http://purl.org/vocab/vann/> SELECT ?prefix ?uri WHERE { ?x vann:preferredNamespacePrefix ?prefix . ?x vann:preferredNamespaceUri ?uri }",
							new TupleQueryResultHandlerBase() {
								@Override
								public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
									namespaceMap.put(bindingSet.getValue("uri").stringValue(), bindingSet.getValue("prefix").stringValue());
								}
							}
					)
			);
		} catch (Exception e) {
			e.printStackTrace();
			this.namespaceMap = new HashMap<String, String>();
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(Namespaces.getInstance().getPrefix("http://data.bnf.fr/"));
		System.out.println(Namespaces.getInstance().getPrefix("http://purl.org/dc/terms/"));
		System.out.println(Namespaces.getInstance().getPrefix("http://creativecommons.org/ns#"));
	}
}
