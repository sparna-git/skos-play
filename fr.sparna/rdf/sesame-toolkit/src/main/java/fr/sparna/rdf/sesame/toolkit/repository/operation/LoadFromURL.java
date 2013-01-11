package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;

public class LoadFromURL extends AbstractLoadOperation implements RepositoryOperationIfc {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected URL url;
	protected String localFallback;

	/**
	 * @param url url to load data from
	 * @param localFallback path to a local resource to load if call to url fails
	 */
	public LoadFromURL(URL url, String localFallback) {
		this.url = url;
		this.localFallback = localFallback;
	}
	
	/**
	 * Constructs a LoadFromURL operation that will have a localFallback equal to the file path in the URL,
	 * minus the leading '/', if useDefaultFallback is set to true
	 * 
	 * @param url
	 */
	public LoadFromURL(URL url, boolean useDefaultFallback) {
		this(url, (useDefaultFallback)?url.getFile().substring(1):null);
	}
	
	/**
	 * Constructs a LoadFromURL operation with no local fallback.
	 * 
	 * @param url
	 */
	public LoadFromURL(URL url) {
		this(url, false);
	}
	
	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		try {
			repository.getConnection().add(
					this.url,
					this.defaultNamespace,
					RDFFormat.forFileName(url.toString()),
					(this.targetGraph != null)?repository.getValueFactory().createURI(this.targetGraph.toString()):null
			);
		} catch (RDFParseException e) {
			throw new RepositoryOperationException("Error when parsing content at URL '"+this.url.toString()+"'", e);
		} catch (RepositoryException e) {
			throw new RepositoryOperationException("Error when adding content of URL '"+this.url.toString()+"'", e);
		} catch (IOException e) {
			log.info("Cannot open stream of URL '"+this.url+"'");
			if(this.localFallback != null) {
				log.info("Will attempt to load local resource fallback : '"+this.localFallback+"'");
				InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(this.localFallback);
				if(resourceAsStream == null) {
					throw new RepositoryOperationException("Cannot find resource "+this.localFallback);
				}
				LoadFromStream lfs = new LoadFromStream(
						resourceAsStream,
						RDFFormat.forFileName(this.localFallback, RDFFormat.RDFXML),
						this.defaultNamespace
				);
				lfs.setTargetGraph(this.targetGraph);
				lfs.execute(repository);
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		LoadFromURL lfu = new LoadFromURL(new URL("http://prefix.cc/popular/all.file.vann"));
		lfu.execute(r);
		
		SesameSPARQLExecuter.newExecuter(r).executeSelect(
				new SelectSPARQLHelper(
						"PREFIX vann:<http://purl.org/vocab/vann/> SELECT ?prefix ?uri WHERE { ?x vann:preferredNamespacePrefix ?prefix . ?x vann:preferredNamespaceUri ?uri }",
						new DebugHandler(System.out)
				)
		);
	}
	
}
