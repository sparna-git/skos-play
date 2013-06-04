package fr.sparna.commons.tree;

public interface GenericTreeVisitorIfc<T> {

	public boolean visit(GenericTreeNode<T> node) throws GenericTreeVisitorException;
	
}
