package fr.sparna.rdf.skos.toolkit;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode.NodeType;

/**
 * 
 * @author Thomas Francart
 *
 */
public class SKOSTreeBuilder {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Repository repository;
	protected PropertyReader sortCriteriaReader;
	protected SKOSNodeTypeReader nodeTypeReader;

	private Collator collator;
	
	private boolean ignoreExplicitTopConcepts = false;

	/**
	 * Builds a SKOSTreeBuilder that will use the given PropertyReader to read the property on which
	 * to sort the elements of the tree.
	 *  
	 * @param repository			The repository to read data from
	 * @param sortCriteriaReader	A PropertyReader to read the property that will be used to sort the elements of the tree
	 */
	public SKOSTreeBuilder(Repository repository, PropertyReader sortCriteriaReader, SKOSNodeTypeReader nodeTypeReader) {
		super();
		this.repository = repository;
		this.sortCriteriaReader = sortCriteriaReader;
		this.nodeTypeReader = nodeTypeReader;
		
		// setup Collator with a Locale corresponding to the lang read by our sort criteria reader
		collator = Collator.getInstance((sortCriteriaReader.getLang() != null)?new Locale(sortCriteriaReader.getLang()):Locale.getDefault());
		collator.setStrength(Collator.SECONDARY);
	}
	
	/**
	 * Builds a SKOSTreeBuilder that will sort its entries based on the skos:prefLabel property in the given language
	 * 
	 * @param repository	The repository to read data from
	 * @param lang			The language with which to read the skos:prefLabel property of the Concept to sort them
	 */
	public SKOSTreeBuilder(Repository repository, String lang) {
		this(
				repository,
				new PropertyReader(repository, java.net.URI.create(SKOS.PREF_LABEL), lang),
				new SKOSNodeTypeReader(new PropertyReader(repository, java.net.URI.create(RDF.TYPE.stringValue())))
		);
	}

	/**
	 * Build all the trees found in the data. Roots are the ConceptSchemes if at least one ConceptScheme is found,
	 * or all the Concept with no broaders if no ConceptScheme can be found in the data. If none of these can be found,
	 * result will be an empty list.
	 * 
	 * @return		A List of trees starting at the ConceptSchemes or the Concepts with no broaders.
	 * @throws SPARQLPerformException
	 */
	public List<GenericTree<SKOSTreeNode>> buildTrees() 
	throws SPARQLPerformException {

		final List<GenericTree<SKOSTreeNode>> result = new ArrayList<GenericTree<SKOSTreeNode>>();
		
		final List<Resource> conceptSchemeList = new ArrayList<Resource>();
		Perform.on(repository).select(new GetConceptSchemesHelper(null) {		
			@Override
			protected void handleConceptScheme(Resource conceptScheme)
			throws TupleQueryResultHandlerException {
				try {
					result.add(new GenericTree<SKOSTreeNode>(buildTreeRec((URI)conceptScheme)));
				} catch (SPARQLPerformException e) {
					throw new TupleQueryResultHandlerException(e);
				}
			}
		});
		
		if(conceptSchemeList.size() > 0) {
			// multiple concept schemes available
			log.debug("Concept schemes exists, will take them as first level nodes");
			
			// set all the concept schemes as roots
			for (Resource aConceptScheme : conceptSchemeList) {
				result.add(new GenericTree<SKOSTreeNode>(buildTreeRec((URI)aConceptScheme)));
			}		
		} else {
			
			// see if there are some top-level collections
			final List<Resource> topCollectionsList = new ArrayList<Resource>();
			Perform.on(repository).select(new GetTopCollectionsHelper(null, null) {				
				@Override
				protected void handleTopCollection(Resource top)
				throws TupleQueryResultHandlerException {
					topCollectionsList.add(top);
				}				
			});
			
			if(topCollectionsList.size() > 0) {
				log.debug("Collections exist, will take them as first level nodes");
				
				// set all the collections as root
				for (Resource aCollection : topCollectionsList) {
					result.add(new GenericTree<SKOSTreeNode>(buildTreeRec((URI)aCollection)));
				}
			} else {
				log.debug("No concept schemes and no collections exists, will fetch all concepts without broaders.");
				
				// fetch all concepts with no broaders
				Perform.on(repository).select(new GetConceptsWithNoBroaderHelper(null) {
					@Override
					protected void handleConceptWithNoBroader(Resource noBroader)
					throws TupleQueryResultHandlerException {
						try {
							result.add(new GenericTree<SKOSTreeNode>(buildTreeRec((URI)noBroader)));
						} catch (SPARQLPerformException e) {
							throw new TupleQueryResultHandlerException(e);
						}
					}
				});
			}			
		}
		
		// sort trees before returning them
		for (GenericTree<SKOSTreeNode> aTree : result) {
			sortTreeRec(aTree.getRoot());
		}
		
		return result;
		
	}
	
	/**
	 * Builds a tree starting from the given root, which can be a Concept or a ConceptScheme.
	 * 
	 * @param root		The URI of the Concept or the ConceptScheme that will be the root of that tree
	 * @return
	 * @throws SPARQLPerformException
	 */
	public GenericTree<SKOSTreeNode> buildTree(java.net.URI root) 
	throws SPARQLPerformException {

		log.debug("Building SKOS Tree from root "+root);
			
		GenericTreeNode<SKOSTreeNode> treeRoot = buildTreeRec(this.repository.getValueFactory().createURI(root.toString()));

		// sort tree before returning it
		sortTreeRec(treeRoot);

		return new GenericTree<SKOSTreeNode>(treeRoot);
	}
	
	

	private void sortTreeRec(GenericTreeNode<SKOSTreeNode> aNode) {
		if(aNode.getChildren() != null) {
			Collections.sort(aNode.getChildren(), new Comparator<GenericTreeNode<SKOSTreeNode>>() {

				@Override
				public int compare(GenericTreeNode<SKOSTreeNode> o1, GenericTreeNode<SKOSTreeNode> o2) {
					if(o1.getData().getSortCriteria() == null) {
						if(o2.getData().getSortCriteria() == null) {
							return 0;
						} else {
							return -1;
						}
					} else {
						if(o2.getData().getSortCriteria() == null) {
							return 1;
						} else {
							return collator.compare(o1.getData().getSortCriteria(), o2.getData().getSortCriteria());
						}
					}
				}
			
			});
			
			for (GenericTreeNode<SKOSTreeNode> aChild : aNode.getChildren()) {
				sortTreeRec(aChild);
			}
		}
		
	}
	
	private GenericTreeNode<SKOSTreeNode> buildTreeRec(URI conceptOrConceptSchemeOrCollection)
	throws SPARQLPerformException {

		// fetch sort criteria - usually prefLabel in a given language
		List<Value> sortCriterias = this.sortCriteriaReader.read(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()));
		// usually there would be only one
		String sortCriteria = (sortCriterias != null && sortCriterias.size() > 0)?sortCriterias.get(0).stringValue():null;				
		
		// fetch node type
		final NodeType nodeType = this.nodeTypeReader.readNodeType(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()));
		
		// build node
		final SKOSTreeNode payload = new SKOSTreeNode(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()), sortCriteria, nodeType);
		final GenericTreeNode<SKOSTreeNode> node = new GenericTreeNode<SKOSTreeNode>(payload);
		
		// get subtree
		switch(nodeType) {
		case CONCEPT_SCHEME : {
			log.debug("Found a Concept Scheme URI : "+conceptOrConceptSchemeOrCollection);
			
			// We take Collections if we find some
			Perform.on(repository).select(new GetTopCollectionsHelper(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()), null) {
				
				@Override
				protected void handleTopCollection(Resource top)
				throws TupleQueryResultHandlerException {
					try {
						node.addChild(buildTreeRec((URI)top));
					} catch (SPARQLPerformException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
				
			});
			
			// if no collection was found, we look for topConcepts declared on the scheme
			if(ignoreExplicitTopConcepts) {
				if(node.getChildren() == null || node.getChildren().size() == 0) {
					Perform.on(repository).select(new GetTopConceptsHelper(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()), null) {
						
						@Override
						protected void handleTopConcept(Resource top)
						throws TupleQueryResultHandlerException {
							try {
								node.addChild(buildTreeRec((URI)top));
							} catch (SPARQLPerformException e) {
								throw new TupleQueryResultHandlerException(e);
							}
						}
						
					});
				}
			}
			
			// if no explicit hasTopConcept or istopConceptOf was found, get the concepts of that scheme with no broader info
			if(node.getChildren() == null || node.getChildren().size() == 0) {
				Perform.on(repository).select(new GetConceptsWithNoBroaderHelper(null, java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue())) {
					@Override
					protected void handleConceptWithNoBroader(Resource noBroader)
					throws TupleQueryResultHandlerException {
						try {
							node.addChild(buildTreeRec((URI)noBroader));
						} catch (SPARQLPerformException e) {
							throw new TupleQueryResultHandlerException(e);
						}
					}
				});
			}
			break;
		}
		case COLLECTION : {
			log.debug("Found a Collection URI : "+conceptOrConceptSchemeOrCollection);
			Perform.on(repository).select(new GetTopMembersHelper(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()), null) {
				
				@Override
				protected void handleMember(Resource collection, Resource member)
				throws TupleQueryResultHandlerException {
					try {
						node.addChild(buildTreeRec((URI)member));
					} catch (SPARQLPerformException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
				
			});
			break;
		}
		// in case of an unknown type, attempt to read it like a concept
		case UNKNOWN : {
			log.warn("Unable to determine node type of : "+conceptOrConceptSchemeOrCollection);
		}
		case CONCEPT : {			
			Perform.on(repository).select(new GetNarrowersHelper(java.net.URI.create(conceptOrConceptSchemeOrCollection.stringValue()), null) {
				
				@Override
				protected void handleNarrowerConcept(Resource parent, Resource narrower)
				throws TupleQueryResultHandlerException {
					try {
						node.addChild(buildTreeRec((URI)narrower));
					} catch (SPARQLPerformException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
				
			});
			break;
		}
		default : {
			break;
		}
		}
		
		return node;
	}

	public boolean isIgnoreExplicitTopConcepts() {
		return ignoreExplicitTopConcepts;
	}

	public void setIgnoreExplicitTopConcepts(boolean ignoreExplicitTopConcepts) {
		this.ignoreExplicitTopConcepts = ignoreExplicitTopConcepts;
	}

}
