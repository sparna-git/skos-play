package fr.sparna.rdf.datapress;

import org.eclipse.rdf4j.model.IRI;
import org.w3c.dom.Document;

public class SimpleDataPressSource implements DataPressSource {

	protected IRI iri;
	protected byte[] content;
	protected Document contentDom;
	protected IRI documentIri;
	
	public SimpleDataPressSource(IRI iri) {
		super();
		this.iri = iri;
	}


	@Override
	public IRI getIri() {
		return iri;
	}

	@Override
	public byte[] getContent() {
		return content;
	}

	@Override
	public Document getContentDom() {
		return contentDom;
	}
	
	@Override
	public IRI getDocumentIri() {
		return documentIri;
	}

	public void setIri(IRI iri) {
		this.iri = iri;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public void setContentDom(Document contentDom) {
		this.contentDom = contentDom;
	}

	public void setDocumentIri(IRI documentIri) {
		this.documentIri = documentIri;
	}
	
	
}
