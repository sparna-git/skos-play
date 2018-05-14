package fr.sparna.rdf.skos.toolkit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.TypeReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode.NodeType;

/**
 * Determines if an entry in the tree corresponds to a Concept, a Collection or a ConceptScheme.
 * 
 * @author Thomas Francart
 */
public class SKOSNodeTypeReader {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected TypeReader typeReader;
	protected RepositoryConnection connection;
	
	public SKOSNodeTypeReader(TypeReader typeReader, RepositoryConnection connection) {
		super();
		this.typeReader = typeReader;
		this.connection = connection;
	}

	public NodeType readNodeType(IRI node) {
		List<IRI> types = typeReader.read(node, this.connection);

		for (IRI value : types) {
			if(value.stringValue().equals(SKOS.CONCEPT)) {
				return NodeType.CONCEPT;
			} else if(value.stringValue().equals(SKOS.COLLECTION)) {
				
				// determine if the Collection corresponds to a ThesaurusArray or a MT
				final List<String> broaders = new ArrayList<String>();
				Perform.on(connection).select(new GetBroadersOfMembersOfCollection(node) {
					@Override
					protected void handleBroaderOfMemberOfCollection(Resource concept) throws TupleQueryResultHandlerException {
						broaders.add(concept.stringValue());
					}					
				});
				
				// either they have a single parent...
				if(
						broaders.size() == 1
				) {
					return NodeType.COLLECTION_AS_ARRAY;
				} else {
					// or no parent at all... which can mean 2 things...
					if(broaders.size() == 0) {
						// if no broaders were found, test if the collection actually has only concepts as members
						// and not collections, like Domains in the UNESCO thesaurus
						if(Perform.on(connection).ask(new HasOnlyConceptMembersQuery(node.toString()).get())) {
							// then we consider it a top-level ThesaurusArray
							return NodeType.COLLECTION_AS_ARRAY;
						} else {
							// otherwise, it is a simple collection
							return NodeType.COLLECTION;
						}
					}
					return NodeType.COLLECTION;
				}				
				
			} else if(value.stringValue().equals(SKOS.CONCEPT_SCHEME)) {
				return NodeType.CONCEPT_SCHEME;
			}
		}
		
		log.warn("Unable to determine NodeType for node "+node.toString()+". Node has types : "+types);
		return NodeType.UNKNOWN;
	}
	
}
