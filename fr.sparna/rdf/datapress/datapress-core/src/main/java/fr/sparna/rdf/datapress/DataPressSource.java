package fr.sparna.rdf.datapress;

import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;

public interface DataPressSource {

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
}
