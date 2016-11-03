package fr.sparna.rdf.sesame.toolkit.functions;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.algebra.evaluation.ValueExprEvaluationException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.Function;

/**
 * A SPARQL function to compute Levenshtein distance between 2 strings.
 * Usage in a SPARQL query :
 * <code>
 *  PREFIX sparna: <http://www.sparna.fr/rdf/sesame/toolkit/functions#>
 *  SELECT ...
 *  WHERE {
 *    ...
 *    FILTER(sparna:levenshtein(?x,?y) < 10)
 *  }
 * </code>
 * Note that the distance computation is case-insensitive.
 * 
 * @author Thomas Francart
 *
 */
public class LevenshteinDistanceFunction implements Function {

	// define a constant for the namespace of our custom function
	public static final String NAMESPACE = "http://www.sparna.fr/rdf/sesame/toolkit/functions#";

	/**
	 * return the URI 'http://www.sparna.fr/rdf/sesame/toolkit/functions#levenshtein' as a String
	 */
	@Override
	public String getURI() {
		return NAMESPACE + "levenshtein";
	} 

	@Override
	public Value evaluate(ValueFactory valueFactory, Value... args)
			throws ValueExprEvaluationException {
		if (args.length != 2) {
			throw new ValueExprEvaluationException("levenstein function requires exactly 2 arguments, got " + args.length);
		} 

		Value arg1 = args[0];
		Value arg2 = args[1];

		if (! (arg1 instanceof Literal)) {
			throw new ValueExprEvaluationException("Invalid argument (literal expected): " + arg1);
		}
		if (! (arg2 instanceof Literal)) {
			throw new ValueExprEvaluationException("Invalid argument (literal expected): " + arg2);
		}
		
		int distance = LevenshteinDistance.computeLevenshteinDistance(arg1.stringValue(), arg2.stringValue(), false);
		return valueFactory.createLiteral(distance);
	}

}
