package fr.sparna.rdf.microdata.parser;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;

public class RDFUtils {

	private static final ValueFactory valueFactory = SimpleValueFactory.getInstance();
	
	public static final URI uri(String uri) {
		return valueFactory.createURI(uri);
	}

	public static Literal literal(String value, URI datatype) {
		return valueFactory.createLiteral(value, datatype);
	}

	public static Literal literal(String value, String lang) {
		if(lang != null) {
			return valueFactory.createLiteral(value, lang);
		} else {
			return literal(value);
		}
	}

	public static Literal literal(String value) {
		return valueFactory.createLiteral(value);
	}
	
    public static BNode getBNode(String id) {
        return valueFactory.createBNode(
        		"node" + id
        		// "node" + MathUtils.md5(id)
        );
    }
    
    public static Statement statement(Resource s, URI p, Value o) {
    	return valueFactory.createStatement(s, p, o);
    }
	
}
