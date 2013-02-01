package fr.sparna.rdf.sesame.toolkit.skos;

import java.net.URI;

public class SKOSTreeNode {

	protected java.net.URI uri;
	protected String sortCriteria;

	
	public SKOSTreeNode(URI uri, String sortCriteria) {
		super();
		this.uri = uri;
		this.sortCriteria = sortCriteria;
	}

	public java.net.URI getUri() {
		return uri;
	}
	
	public void setUri(java.net.URI uri) {
		this.uri = uri;
	}

	public String getSortCriteria() {
		return sortCriteria;
	}

	public void setSortCriteria(String sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
	
}
