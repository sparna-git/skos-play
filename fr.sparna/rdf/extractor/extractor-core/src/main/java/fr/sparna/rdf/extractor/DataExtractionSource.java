package fr.sparna.rdf.extractor;

import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;

/**
 * A source for extracting data.
 * @author Thomas Francart
 *
 */
public interface DataExtractionSource {

	/**
	 * @return The original parsed IRI (including fragment identifier, if any)
	 */
	public IRI getIri();
	
	/**
	 * @return The raw content of the document
	 */
	public byte[] getContent();
	
	/**
	 * @return A DOM of the raw content
	 */
	public Document getContentDom();
	
	/**
	 * @return The IRI of the document IRI (without fragment identifier). This is identical to the source IRI if the source IRI had no fragment identifier.
	 */
	public IRI getDocumentIri();
	
	/**
	 * @return
	 */
	public String getContentType();
}
