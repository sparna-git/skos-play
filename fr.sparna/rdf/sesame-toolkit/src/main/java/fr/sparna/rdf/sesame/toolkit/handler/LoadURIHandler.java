package fr.sparna.rdf.sesame.toolkit.handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromUrl;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;

public class LoadURIHandler extends ReadValueListHandler implements TupleQueryResultHandler {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected Repository repository;
	protected List<URL> errors = new ArrayList<URL>();
	
	public LoadURIHandler(Repository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
		// convert List<Value> to List<URL>
		List<URL> urlsToLoad = new ArrayList<URL>();
		for (Value aValue : this.result) {
			if(aValue instanceof URI) {
				try {
					java.net.URI javaURI = java.net.URI.create(aValue.stringValue());
					urlsToLoad.add(javaURI.toURL());
				} catch (MalformedURLException e) {
					throw new TupleQueryResultHandlerException(e);
				}
			}
		}
		
		for (URL url : urlsToLoad) {
			// initiate a LoadFromUrl operation
			LoadFromUrl loadOperation = new LoadFromUrl(url, true);
			try {
				// run it
				loadOperation.execute(this.repository);
				log.error("Sucessfully loaded : "+url);
			} catch (Exception e) {
				// that's a failure, keep track of it
				log.error("Unable to load URL : "+url);
				this.errors.add(url);
			}
		}
		
		
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
		Repository r = RepositoryBuilder.fromString("/media/Library/Sparna/Thesaurus/Gemet");
		LoadURIHandler h = new LoadURIHandler(r);
		Perform.on(r).select(new SelectSparqlHelper("SELECT ?x WHERE { ?c <"+SKOS.EXACT_MATCH.stringValue()+"> ?x }", h));
		RepositoryWriter.writeToFile("output.ttl", r);
	}
	
}
