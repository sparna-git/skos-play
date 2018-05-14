package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Reads RDF from an inline String. The RDF format to use to parse the string can be supplied
 * along with the string; if not supplied, this operation will try in order all the known
 * RDF formats in order : TURTLE, RDF/XML, N3, NTRIPLES, TRIG, TRIX.
 * If all fail, it will throw an exception. 
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromString extends AbstractLoadOperation implements Consumer<RepositoryConnection> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String data;
	protected String rdfFormat;

	public LoadFromString(String data, String rdfFormat) {
		super();
		this.data = data;
		this.rdfFormat = rdfFormat;
	}

	public LoadFromString(String data) {
		this(data, null);
	}

	@Override
	public void accept(RepositoryConnection connection) {

		List<RDFFormat> formats = new ArrayList<RDFFormat>(Arrays.asList(new RDFFormat[] {
				RDFFormat.TURTLE,
				RDFFormat.RDFXML,
				RDFFormat.N3,
				RDFFormat.NTRIPLES,
				RDFFormat.TRIG,
				RDFFormat.TRIX
		}));
		if(this.rdfFormat != null) {
			log.debug("Will use this RDF format : "+this.rdfFormat);
			formats.retainAll(Collections.singletonList(Rio.getParserFormatForFileName(this.rdfFormat).orElse(RDFFormat.RDFXML)));
		} else {
			log.debug("No RDF format specified. Will use all formats : "+formats);
		}
		
		boolean success = false;
		for (RDFFormat aFormat : formats) {
			try {
				log.debug("Trying to parse String with format : "+aFormat);
				if(this.targetGraph == null) {
					connection.add(
							new ByteArrayInputStream(this.data.getBytes()),
							(this.defaultNamespace != null)?this.defaultNamespace:RDF.NAMESPACE,
							aFormat
					);
				} else {
					connection.add(
							new ByteArrayInputStream(this.data.getBytes()),
							(this.defaultNamespace != null)?this.defaultNamespace:RDF.NAMESPACE,
							aFormat,
							connection.getValueFactory().createIRI(this.targetGraph.toString())
					);
				}
				log.debug("Parsing with format "+aFormat+" suceeded.");
				success = true;
				break;
			} catch (Exception e) {
				log.debug("Parsing with format "+aFormat+" failed (reason : "+e.getMessage()+")");
			}
		}
		
		if(!success) {
			throw new RuntimeException("Unable to parse input RDF in one of the formats "+formats);
		}

	}	

}
