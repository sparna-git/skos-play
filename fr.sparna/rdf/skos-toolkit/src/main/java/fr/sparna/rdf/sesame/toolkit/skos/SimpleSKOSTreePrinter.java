package fr.sparna.rdf.sesame.toolkit.skos;

import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

/**
 * Prints a SKOS tree as a String
 * 
 * @author Thomas Francart
 *
 */
public class SimpleSKOSTreePrinter {

	private Repository repository;
	private String displayLanguage = null;

	public SimpleSKOSTreePrinter(Repository repository) {
		super();
		this.repository = repository;
	}

	public SimpleSKOSTreePrinter(Repository repository, String displayLanguage) {
		super();
		this.repository = repository;
		this.displayLanguage = displayLanguage;
	}

	public String printTree() 
	throws SPARQLExecutionException {
		SKOSTreeBuilder builder = new SKOSTreeBuilder(this.repository, this.displayLanguage);
		List<GenericTree<SKOSTreeNode>> trees = builder.buildTrees();
		
		final StringBuffer buffer = new StringBuffer();
		for (GenericTree<SKOSTreeNode> aTree : trees) {
			for (GenericTreeNode<SKOSTreeNode> aChild : aTree.getRoot().getChildren()) {
				buffer.append(printConceptRec(aChild, 0));
			}
			buffer.append("\n\n");
		}
		
		return buffer.toString();
	}

	private String printConceptRec(GenericTreeNode<SKOSTreeNode> aNode, int depth) 
	throws SPARQLExecutionException {
		final StringBuffer buffer = new StringBuffer();
		
		// print tabs
		for(int i=0;i<depth;i++) {
			buffer.append("  ");
		}			
		buffer.append((depth > 0)?"\\-":"");
		
		new SesameSPARQLExecuter(this.repository).executeSelect(new GetLabelsHelper(aNode.getData().getUri()) {
			@Override
			protected void handleLabel(Resource concept, URI labelType, String prefLabel, String lang)
			throws TupleQueryResultHandlerException {
				// on n'affiche que si le label correspond à la langue d'affichage voulue
				if(displayLanguage == null || displayLanguage.equals(lang)) {
					buffer.append(prefLabel+((lang != null)?"@"+lang:"")+", ");
				}
			}
		});
		// remove trailing ", "
		buffer.delete(buffer.length() - 2, buffer.length());

		// add URI
		buffer.append(" ("+aNode.getData().getUri().toString()+")"+"\n");

		if(aNode.getChildren() != null) {
			for (GenericTreeNode<SKOSTreeNode> aChild : aNode.getChildren()) {
				buffer.append(printConceptRec(aChild, depth+1));
			}
		}
		
		return buffer.toString();
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"1\"@fr ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"a\"@fr; skos:broader test:_1 ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"B\"@fr; skos:broader test:_1 ."
		);
		
		SimpleSKOSTreePrinter printer = new SimpleSKOSTreePrinter(r, "fr");
		System.out.println(printer.printTree());
	}

}
