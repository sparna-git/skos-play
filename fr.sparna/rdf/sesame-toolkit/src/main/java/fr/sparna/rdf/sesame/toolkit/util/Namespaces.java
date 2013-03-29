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
import org.openrdf.repository.RepositoryException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

/**
 * Manages the Map between full URI and their prefixes. This will attempt to load the dump of prefix.cc
 * to know the most common prefixes, but if it fails it will load a pre-bundled file (see LoadURL).
 * 
 * @author Thomas Francart
 *
 */
public class Namespaces {

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
	protected Namespaces() {
		this.namespaceMap = new HashMap<String, String>();
		this.initNamespaceMap();
	}
	
	/**
	 * Singleton static accessor
	 */
	public static Namespaces getInstance() {
		if(instance == null) {
			instance = new Namespaces();
		}
		return instance;
	}
	
	public void withRepository(Repository r) {
		try {
			// registers RepositoryConnection namespaces
			List<Namespace> repoNamespaces = r.getConnection().getNamespaces().asList();
			for (Namespace namespace : repoNamespaces) {
				if(!this.namespaceMap.containsKey(namespace.getPrefix())) {
					this.namespaceMap.put(namespace.getPrefix(), namespace.getName());
				}
			}
		} catch (RepositoryException e) {
			// don't do anything
			e.printStackTrace();
		}
	}
	
	public String shorten(String fullURI) {
		return getPrefix(split(fullURI)[0])+":"+split(fullURI)[1];
	}
	
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
	
	private void initNamespaceMap() {
		try {
			// load URL - will load the bundled file in the jar if the URL loading fails
			Repository r = RepositoryBuilder.fromURL(new URL("http://prefix.cc/popular/all.file.vann"));
			
			// make a query on the loaded RDF and populate the namespaceMap with the result
			SesameSPARQLExecuter.newExecuter(r).executeSelect(
					new SelectSPARQLHelper(
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
