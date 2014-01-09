package fr.sparna.rdf.skos.toolkit;

import java.util.List;

import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.Function;
import fr.sparna.commons.lang.Lists;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode.NodeType;

/**
 * Determines if an entry in the tree corresponds to a Concept, a Collection or a ConceptScheme.
 * 
 * @author Thomas Francart
 */
public class SKOSNodeTypeReader {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected PropertyReader typeReader;
	
	public SKOSNodeTypeReader(PropertyReader typeReader) {
		super();
		this.typeReader = typeReader;
	}

	public NodeType readNodeType(java.net.URI node) 
	throws SparqlPerformException {
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
		
		log.warn("Unable to determine NodeType for node "+node.toString()+". Node has types : "+Lists.transform(types, new Function<Value, String>() {
			public String apply(Value v) { return v.toString(); }
		}));
		return NodeType.UNKNOWN;
	}
	
}
