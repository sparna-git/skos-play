package fr.sparna.rdf.rdf4j.toolkit.handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromUrl;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;


public class LoadURIHandler extends ReadValueListHandler implements TupleQueryResultHandler {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected RepositoryConnection connection;
	protected List<URL> errors = new ArrayList<URL>();
	protected int currentLoadCount = 0;
	protected LoadFromUrl loader;
	
	public LoadURIHandler(RepositoryConnection connection, LoadFromUrl loader) {
		super();
		this.connection = connection;
		this.loader = loader;
	}
	
	public LoadURIHandler(RepositoryConnection connection) {
		this(connection, new LoadFromUrl((List<URL>)null, true));
	}

	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
		// convert List<Value> to List<URL>
		List<URL> urlsToLoad = new ArrayList<URL>();
		for (Value aValue : this.result) {
			if(aValue instanceof IRI) {
				try {
					java.net.URI javaURI = java.net.URI.create(aValue.stringValue());
					urlsToLoad.add(javaURI.toURL());
				} catch (MalformedURLException e) {
					log.warn("Cannot build a valid URL from '"+aValue.stringValue()+"'");
					// throw new TupleQueryResultHandlerException(e);
				}
			}
		}
		
		currentLoadCount = 0;
		for (URL url : urlsToLoad) {
			// initiate a LoadFromUrl operation
			loader.setUrls(Collections.singletonList(url));
			try {
				// run it
				loader.accept(connection);
				log.debug("Sucessfully loaded : "+url);
			} catch (Exception e) {
				// that's a failure, keep track of it
				log.error("Unable to load URL : "+url);
				this.errors.add(url);
			}
			// keep track of current load count
			currentLoadCount++;
		}
		
	}

	public int getCurrentLoadCount() {
		return currentLoadCount;
	}

	// Pour activer le debug des headers des URLs :
	//   1. ajouter -Djava.util.logging.config.file=/home/thomas/logging.properties a la config de lancement
	//   2. mettre dans ce fichier sun.net.www.protocol.http.HttpURLConnection.level = ALL
	//   3. bien mettre java.util.logging.ConsoleHandler.level = ALL
	public static void main(String... args) throws Exception {
//		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
//		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
//		System.setProperty("org.slf4j.simpleLogger.log.fr.sparna.rdf","trace");
//		
		Repository r = RepositoryBuilderFactory.fromString("/media/Library/Sparna/Thesaurus/Gemet").get();
		try(RepositoryConnection connection = r.getConnection()) {
			LoadURIHandler h = new LoadURIHandler(connection);
			Perform.on(connection).select("SELECT ?x WHERE { ?c <"+SKOS.EXACT_MATCH.stringValue()+"> ?x }", h);
			RepositoryWriter.writeToFile("output.ttl", connection);
		}
		
	}
	
}
