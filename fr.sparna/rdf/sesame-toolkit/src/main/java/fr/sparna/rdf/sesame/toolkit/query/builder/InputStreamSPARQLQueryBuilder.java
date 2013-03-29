package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.io.InputStream;

import fr.sparna.commons.io.InputStreamUtil;

/**
 * Reads a SPARQL query from an input stream.
 * 
 * @author Thomas Francart
 *
 */
public class InputStreamSPARQLQueryBuilder implements SPARQLQueryBuilderIfc {

	protected String sparql;
	
	/**
	 * Construct an InputStreamSPARQLQueryBuilder by reading from the stream.
	 * 
	 * @param stream the stream to read from
	 */
	public InputStreamSPARQLQueryBuilder(InputStream stream) {
		super();
		// read from the stream
		// TODO : specify encoding ?
		this.sparql = InputStreamUtil.readToString(stream);
	}

	@Override
	public String getSPARQL() {
		return sparql;
	}

}
