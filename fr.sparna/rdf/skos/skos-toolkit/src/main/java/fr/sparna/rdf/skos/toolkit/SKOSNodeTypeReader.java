package fr.sparna.rdf.skos.toolkit;

import java.util.List;

import org.openrdf.model.Value;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode.NodeType;

public class SKOSNodeTypeReader {

	protected PropertyReader typeReader;
	
	public SKOSNodeTypeReader(PropertyReader typeReader) {
		super();
		this.typeReader = typeReader;
	}

	public NodeType readNodeType(java.net.URI node) 
	throws SPARQLPerformException {
		List<Value> types = typeReader.read(node);

		for (Value value : types) {
			if(value.stringValue().equals(SKOS.CONCEPT)) {
				return NodeType.CONCEPT;
			} else if(value.stringValue().equals(SKOS.COLLECTION)) {
				return NodeType.COLLECTION;
			} else if(value.stringValue().equals(SKOS.CONCEPT_SCHEME)) {
				return NodeType.CONCEPT_SCHEME;
			}
		}
		
		return NodeType.UNKNOWN;
	}
	
}
