package fr.sparna.rdf.rdf4j.toolkit.query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

/**
 * Utility class for manipulating queries
 * 
 * @author Thomas Francart
 *
 */
public class Queries {

	public static Model examineUpdateResult(RepositoryConnection connection, String resource) {
		try {
			IRI graph = SimpleValueFactory.getInstance().createIRI("http://sparna.fr/"+Queries.class.getName()+"/"+URLEncoder.encode(resource, "UTF-8"));
			Perform.on(connection).insertIn(graph).update(
					SimpleQueryReader.fromResource(resource)
			);			
			
			StatementCollector sc = new StatementCollector();
			connection.exportStatements(null, null, null, true, sc, graph);
			return new LinkedHashModel(sc.getStatements());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
