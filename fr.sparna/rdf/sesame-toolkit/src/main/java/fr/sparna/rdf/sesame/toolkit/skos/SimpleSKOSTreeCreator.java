package fr.sparna.rdf.sesame.toolkit.skos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;

/**
 * Prints a SKOS tree as a String
 * 
 * @author Thomas Francart
 *
 */
public class SimpleSKOSTreeCreator {

	private Repository repository;
	private String displayLanguage = null;

	public SimpleSKOSTreeCreator(Repository repository) {
		super();
		this.repository = repository;
	}

	public SimpleSKOSTreeCreator(Repository repository, String displayLanguage) {
		super();
		this.repository = repository;
		this.displayLanguage = displayLanguage;
	}

	public String getTree() 
	throws SPARQLExecutionException {
		final StringBuffer buffer = new StringBuffer();

		// s'il y a des concepts schemes, les mettre en premier
		final List<Resource> firstLevelNodes = new ArrayList<Resource>();
		final List<Resource> conceptSchemeList = new ArrayList<Resource>();
		new SesameSPARQLExecuter(this.repository).executeSelect(new GetConceptsSchemesHelper(this.displayLanguage) {		
			@Override
			protected void handleConceptScheme(Resource conceptScheme)
			throws TupleQueryResultHandlerException {
				firstLevelNodes.add(conceptScheme);
				conceptSchemeList.add(conceptScheme);
			}
		});

		if(firstLevelNodes.size() == 0) {
			// aucun concept scheme, on prend tous les noeuds sans top
			// et on les descend
			new SesameSPARQLExecuter(this.repository).executeSelect(new GetConceptsWithNoBroaderHelper(this.displayLanguage) {
				@Override
				protected void handleConceptWithNoBroader(Resource noBroader)
				throws TupleQueryResultHandlerException {
					firstLevelNodes.add(noBroader);
				}
			});
		}

		for (Resource aFirstLevelNode : firstLevelNodes) {
			getTreeOfConceptRec((URI)aFirstLevelNode, buffer, 0, conceptSchemeList);
		}

		return buffer.toString();
	}

	private void getTreeOfConceptRec(URI concept, final StringBuffer buffer, final int depth, final Collection<Resource> conceptSchemeList)
	throws SPARQLExecutionException {
		final StringBuffer conceptLabel = new StringBuffer();
		new SesameSPARQLExecuter(this.repository).executeSelect(new GetLabelsHelper(concept) {
			@Override
			protected void handleLabel(Resource concept, URI labelType, String prefLabel, String lang)
			throws TupleQueryResultHandlerException {
				// on n'affiche que si le label correspond à la langue d'affichage voulue
				if(displayLanguage == null || displayLanguage.equals(lang)) {
					conceptLabel.append(prefLabel+((lang != null)?"@"+lang:"")+", ");
				}
			}
		});
		// remove trailing ", "
		conceptLabel.delete(conceptLabel.length() - 2, conceptLabel.length());

		// add URI
		conceptLabel.append(" ("+concept.stringValue()+")");

		// print tabs
		for(int i=0;i<depth;i++) {
			buffer.append("  ");
		}			
		buffer.append((depth > 0)?"\\-":"");

		// print concept label
		buffer.append(conceptLabel+"\n");

		// print subtree
		if(conceptSchemeList.contains(concept)) {
			new SesameSPARQLExecuter(this.repository).executeSelect(new GetTopConceptOfConceptSchemeHelper(concept, displayLanguage) {
				@Override
				protected void handleTopConcept(Resource top)
				throws TupleQueryResultHandlerException {
					try {
						getTreeOfConceptRec((URI)top, buffer, depth+1, conceptSchemeList);
					} catch (SPARQLExecutionException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
			});
		} else {
			new SesameSPARQLExecuter(this.repository).executeSelect(new GetNarrowersHelper(concept, displayLanguage) {
				@Override
				protected void handleNarrowerConcept(Resource parent, Resource narrower)
				throws TupleQueryResultHandlerException {
					try {
						getTreeOfConceptRec((URI)narrower, buffer, depth+1, conceptSchemeList);
					} catch (SPARQLExecutionException e) {
						throw new TupleQueryResultHandlerException(e);
					}
				}
			});	
		}	
	}

}
