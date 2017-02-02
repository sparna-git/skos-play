package fr.sparna.rdf.sesame.toolkit.functions;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

/**
 * A SPARQL function to split a CamelCaseString
 * Usage in a SPARQL query :
 * <code>
 *  PREFIX sparna: <http://www.sparna.fr/rdf/sesame/toolkit/functions#>
 *  SELECT ...
 *  WHERE {
 *    ...
 *    FILTER(sparna:splitCamelCase(?x))
 *  }
 * </code>
 * 
 * @author Thomas Francart
 *
 */
public class SplitCamelCaseFunction implements Function {

	// define a constant for the namespace of our custom function
	public static final String NAMESPACE = "http://www.sparna.fr/rdf/sesame/toolkit/functions#";

	/**
	 * return the URI 'http://www.sparna.fr/rdf/sesame/toolkit/functions#splitCamelCase' as a String
	 */
	@Override
	public String getURI() {
		return NAMESPACE + "splitCamelCase";
	} 
	
	public static final String SPLIT_CAMEL_CASE_REGEX = "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])";

	@Override
	public Value evaluate(ValueFactory valueFactory, Value... args)
	throws ValueExprEvaluationException {
		if (args.length != 1) {
			throw new ValueExprEvaluationException("splitCamelCase function requires exactly 1 argument, got " + args.length);
		} 

		Value arg1 = args[0];

		if (! (arg1 instanceof Literal)) {
			throw new ValueExprEvaluationException("Invalid argument (literal expected): " + arg1);
		}
		
		String splitted = String.join(" ", arg1.stringValue().split(SPLIT_CAMEL_CASE_REGEX));
		return valueFactory.createLiteral(splitted);
	}

}
