package fr.sparna.rdf.skos.toolkit;

import java.net.URI;

public class SKOSTreeNode {

	public static enum NodeType {
		CONCEPT,
		CONCEPT_SCHEME,
		COLLECTION,
		UNKNOWN
	}
	
	protected java.net.URI uri;
	protected NodeType nodeType = NodeType.UNKNOWN;
	protected String sortCriteria;

	
	public SKOSTreeNode(URI uri, String sortCriteria, NodeType nodeType) {
		super();
		this.uri = uri;
		this.sortCriteria = sortCriteria;
		this.nodeType = nodeType;
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

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
}
