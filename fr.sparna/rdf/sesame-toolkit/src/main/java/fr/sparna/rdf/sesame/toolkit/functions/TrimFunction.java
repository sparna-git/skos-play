package fr.sparna.rdf.sesame.toolkit.functions;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.query.algebra.evaluation.function.Function;

/**
 * A SPARQL function to trim a String literal. Leading and trailing whitespaces
 * are removed.
 * 
 * @author Thomas Francart
 *
 */
public class TrimFunction implements Function {

	// define a constant for the namespace of our custom function
	public static final String NAMESPACE = "http://www.sparna.fr/rdf/sesame/toolkit/functions#";

	/**
	 * return the URI 'http://www.sparna.fr/rdf/sesame/toolkit/functions#trim' as a String
	 */
	@Override
	public String getURI() {
		return NAMESPACE + "trim";
	} 

	@Override
	public Value evaluate(ValueFactory valueFactory, Value... args)
	throws ValueExprEvaluationException {
		
		if (args.length != 1) {
			throw new ValueExprEvaluationException("trim function requires exactly 1 argument, got " + args.length);
		} 

		if (! (args[0] instanceof Literal)) {
			throw new ValueExprEvaluationException("Invalid argument (literal expected): " + args[0]);
		}
		
		Literal value = (Literal)args[0];
		
		if(value.getDatatype() != null) {
			return valueFactory.createLiteral(value.stringValue().trim(), value.getDatatype());
		} else if(value.getLanguage() != null) {
			return valueFactory.createLiteral(value.stringValue().trim(), value.getLanguage());
		} else {
			return valueFactory.createLiteral(value.stringValue().trim());
		}
	}
	
}
