package fr.sparna.rdf.skos.toolkit;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;

/**
 * Prints a SKOS tree as a String
 * 
 * @author Thomas Francart
 *
 */
public class SimpleSKOSTreePrinter {

	private RepositoryConnection connection;
	private String displayLanguage = null;

	public SimpleSKOSTreePrinter(RepositoryConnection connection) {
		super();
		this.connection = connection;
	}

	public SimpleSKOSTreePrinter(RepositoryConnection connection, String displayLanguage) {
		super();
		this.connection = connection;
		this.displayLanguage = displayLanguage;
	}

	public String printTree() {
		SKOSTreeBuilder builder = new SKOSTreeBuilder(this.connection, this.displayLanguage);
		return printTree(builder);
	}
	
	public String printTree(SKOSTreeBuilder builder) {
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

	private String printConceptRec(GenericTreeNode<SKOSTreeNode> aNode, int depth) {
		final StringBuffer buffer = new StringBuffer();
		
		// print tabs
		for(int i=0;i<depth;i++) {
			buffer.append("  ");
		}			
		buffer.append((depth > 0)?"\\-":"");
		
		Perform.on(connection).select(new GetLabelsHelper(aNode.getData().getIri()) {
			@Override
			protected void handleLabel(Resource concept, IRI labelType, String prefLabel, String lang)
			throws TupleQueryResultHandlerException {
				// on n'affiche que si le label correspond à la langue d'affichage voulue
				if(displayLanguage == null || displayLanguage.equals(lang)) {
					buffer.append(prefLabel+((lang != null)?"@"+lang:"")+", ");
				}
			}
		});
		// remove trailing ", "
		if(buffer.length() > 2) {
			buffer.delete(buffer.length() - 2, buffer.length());
		}

		// add URI
		buffer.append(" ("+aNode.getData().getIri().toString()+")"+"\n");

		if(aNode.getChildren() != null) {
			for (GenericTreeNode<SKOSTreeNode> aChild : aNode.getChildren()) {
				buffer.append(printConceptRec(aChild, depth+1));
			}
		}
		
		return buffer.toString();
	}

}
