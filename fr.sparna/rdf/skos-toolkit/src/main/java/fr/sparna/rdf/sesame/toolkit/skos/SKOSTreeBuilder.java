package fr.sparna.rdf.sesame.toolkit.skos;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;

/**
 * 
 * @author Thomas Francart
 *
 */
public class SKOSTreeBuilder {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Repository repository;
	protected PropertyReader sortCriteriaReader;

	private Collator collator;

	/**
	 * Builds a SKOSTreeBuilder that will use the given PropertyReader to read the property on which
	 * to sort the elements of the tree.
	 *  
	 * @param repository			The repository to read data from
	 * @param sortCriteriaReader	A PropertyReader to read the property that will be used to sort the elements of the tree
	 */
	public SKOSTreeBuilder(Repository repository, PropertyReader sortCriteriaReader) {
		super();
		this.repository = repository;
		this.sortCriteriaReader = sortCriteriaReader;
		
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
		this(repository, new PropertyReader(repository, java.net.URI.create(SKOS.PREF_LABEL), lang));
	}

	/**
	 * Build all the trees found in the data. Roots are the ConceptSchemes if at least one ConceptScheme is found,
	 * or all the Concept with no broaders if no ConceptScheme can be found in the data. If none of these can be found,
	 * result will be an empty list.
	 * 
	 * @return		A List of trees starting at the ConceptSchemes or the Concepts with no broaders.
	 * @throws SPARQLExecutionException
	 */
	public List<GenericTree<SKOSTreeNode>> buildTrees() 
	throws SPARQLExecutionException {

		final List<GenericTree<SKOSTreeNode>> result = new ArrayList<GenericTree<SKOSTreeNode>>();
		
		final List<Resource> conceptSchemeList = new ArrayList<Resource>();
		new SesameSPARQLExecuter(this.repository).executeSelect(new GetConceptsSchemesHelper(null) {		
			@Override
			protected void handleConceptScheme(Resource conceptScheme)
			throws TupleQueryResultHandlerException {
				conceptSchemeList.add(conceptScheme);
			}
		});
		
		if(conceptSchemeList.size() > 0) {
			// no root given, and multiple concept schemes available
			log.debug("No root URI was given and multiple concept schemes exist, will create a dummy root and set concept scheme as first level nodes");
			
			// set all the concept schemes as children of the root
			for (Resource aConceptScheme : conceptSchemeList) {
				result.add(new GenericTree<SKOSTreeNode>(buildTreeRec((URI)aConceptScheme, true)));
			}		
		} else {
			log.debug("No concept schemes exist, will set all the concepts without broaders as roots.");
			
			// fetch all concepts with no broaders
			new SesameSPARQLExecuter(this.repository).executeSelect(new GetConceptsWithNoBroaderHelper(null) {
				@Override
				protected void handleConceptWithNoBroader(Resource noBroader)
				throws TupleQueryResultHandlerException {
					try {
						result.add(new GenericTree<SKOSTreeNode>(buildTreeRec((URI)noBroader, false)));
					} catch (SPARQLExecutionException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
			});			
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
	 * @throws SPARQLExecutionException
	 */
	public GenericTree<SKOSTreeNode> buildTree(java.net.URI root) 
	throws SPARQLExecutionException {

		log.debug("Building SKOS Tree from root "+root);
		
		final List<Resource> conceptSchemeList = new ArrayList<Resource>();
		new SesameSPARQLExecuter(this.repository).executeSelect(new GetConceptsSchemesHelper(null) {		
			@Override
			protected void handleConceptScheme(Resource conceptScheme)
			throws TupleQueryResultHandlerException {
				conceptSchemeList.add(conceptScheme);
			}
		});
		
		log.debug("Found "+conceptSchemeList.size()+" concept schemes in the data");

		// a root was given
		log.debug("Will test if root is a concept scheme");
		
		boolean isConceptScheme = false;
		for (Resource aConceptScheme : conceptSchemeList) {
			if(aConceptScheme.stringValue().equals(root.toString())) {
				isConceptScheme = true;
				break;
			}
		}
		
		final GenericTreeNode<SKOSTreeNode> treeRoot;
		if(isConceptScheme) {
			// if given root is a concept scheme...
			log.debug("Root URI is a concept scheme, will set it as the root");
			treeRoot = buildTreeRec(this.repository.getValueFactory().createURI(root.toString()), true);
		} else {
			// if it is NOT a concept scheme, we assume it is a simple concept
			log.debug("Root URI is not a concept scheme, will set it as the root");
			treeRoot = buildTreeRec(this.repository.getValueFactory().createURI(root.toString()), false);
		}

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
	
	private GenericTreeNode<SKOSTreeNode> buildTreeRec(URI conceptOrConceptScheme, final boolean isConceptScheme)
	throws SPARQLExecutionException {

		// fetch sort criteria - usually prefLabel in a given language
		List<Value> sortCriterias = this.sortCriteriaReader.read(java.net.URI.create(conceptOrConceptScheme.stringValue()));
		// usually there would be only one
		String sortCriteria = (sortCriterias != null && sortCriterias.size() > 0)?sortCriterias.get(0).stringValue():null;				
		
		// build node
		final SKOSTreeNode payload = new SKOSTreeNode(java.net.URI.create(conceptOrConceptScheme.stringValue()), sortCriteria);
		final GenericTreeNode<SKOSTreeNode> node = new GenericTreeNode<SKOSTreeNode>(payload);
		
		// get subtree
		if(isConceptScheme) {

			new SesameSPARQLExecuter(this.repository).executeSelect(new GetTopConceptOfConceptSchemeHelper(java.net.URI.create(conceptOrConceptScheme.stringValue()), null) {
				
				@Override
				protected void handleTopConcept(Resource top)
				throws TupleQueryResultHandlerException {
					try {
						node.addChild(buildTreeRec((URI)top, false));
					} catch (SPARQLExecutionException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
				
			});
			
			if(node.getChildren() == null || node.getChildren().size() == 0) {
				// if no explicit hasTopConcept or istopConceptOf was found, get the concepts of that scheme with no broader info
				new SesameSPARQLExecuter(this.repository).executeSelect(new GetConceptsWithNoBroaderHelper(null, java.net.URI.create(conceptOrConceptScheme.stringValue())) {
					@Override
					protected void handleConceptWithNoBroader(Resource noBroader)
					throws TupleQueryResultHandlerException {
						try {
							node.addChild(buildTreeRec((URI)noBroader, false));
						} catch (SPARQLExecutionException e) {
							throw new TupleQueryResultHandlerException(e);
						}
					}
				});
			}
		} else {
			new SesameSPARQLExecuter(this.repository).executeSelect(new GetNarrowersHelper(java.net.URI.create(conceptOrConceptScheme.stringValue()), null) {
				
				@Override
				protected void handleNarrowerConcept(Resource parent, Resource narrower)
				throws TupleQueryResultHandlerException {
					try {
						node.addChild(buildTreeRec((URI)narrower, false));
					} catch (SPARQLExecutionException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
				
			});	
		}
		
		return node;
	}

}
