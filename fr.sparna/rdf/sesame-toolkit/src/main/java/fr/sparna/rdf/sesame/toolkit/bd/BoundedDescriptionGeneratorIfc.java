package fr.sparna.rdf.sesame.toolkit.bd;

import org.openrdf.model.URI;


/**
 * Generates and exports bounded description of a given URI.
 * <p />See <a href="http://docs.api.talis.com/getting-started/bounded-descriptions-in-rdf">http://docs.api.talis.com/getting-started/bounded-descriptions-in-rdf</a>
 * for description of bounded descriptions.
 * 
 * @author Thomas Francart
 *
 */
public interface BoundedDescriptionGeneratorIfc {

	/**
	 * Exports the bounded description of the given URI
	 * 
	 * @param aNode		The URI for which we want to generate the bounded description
	 * @param handler	The handler that will gather the bounded description statements
	 * @throws BoundedDescriptionGenerationException
	 */
	public void exportBoundedDescription(URI aNode, BoundedDescriptionHandlerIfc handler)
	throws BoundedDescriptionGenerationException;
	
}
