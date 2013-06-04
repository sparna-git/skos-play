package fr.sparna.commons.tree;


public class GenericTree<T> {

	private GenericTreeNode<T> root;

	public GenericTree() {
		super();
	}
	
	public GenericTree(GenericTreeNode<T> root) {
		this();
		this.setRoot(root);
	}

	public GenericTreeNode<T> getRoot() {
		return this.root;
	}

	public void setRoot(GenericTreeNode<T> root) {
		this.root = root;
	}

	public int getNumberOfNodes() {
		int numberOfNodes = 0;

		if(root != null) {
			numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; //1 for the root!
		}

		return numberOfNodes;
	}

	public boolean exists(T dataToFind) {
		return (find(dataToFind) != null);
	}

	public GenericTreeNode<T> find(T dataToFind) {
		GenericTreeNode<T> returnNode = null;

		if(root != null) {
			returnNode = auxiliaryFind(root, dataToFind);
		}

		return returnNode;
	}

	public boolean isEmpty() {
		return (root == null);
	}
	
	public void visit(GenericTreeVisitorIfc<T> visitor) throws GenericTreeVisitorException {
		visit(this.root, visitor);
	}
	
	private void visit(GenericTreeNode<T> node, GenericTreeVisitorIfc<T> visitor)
	throws GenericTreeVisitorException {
		if(visitor.visit(node)) {
			if(node.hasChildren()) {
				for (GenericTreeNode<T> aChild : node.getChildren()) {
					visit(aChild, visitor);
				}
			}
		}
	}

	private int auxiliaryGetNumberOfNodes(GenericTreeNode<T> node) {
		int numberOfNodes = node.getNumberOfChildren();

		for(GenericTreeNode<T> child : node.getChildren()) {
			numberOfNodes += auxiliaryGetNumberOfNodes(child);
		}

		return numberOfNodes;
	}
	
	private GenericTreeNode<T> auxiliaryFind(GenericTreeNode<T> currentNode, T dataToFind) {
		GenericTreeNode<T> returnNode = null;
		int i = 0;

		if (currentNode.getData().equals(dataToFind)) {
			returnNode = currentNode;
		}

		else if(currentNode.hasChildren()) {
			i = 0;
			while(returnNode == null && i < currentNode.getNumberOfChildren()) {
				returnNode = auxiliaryFind(currentNode.getChildAt(i), dataToFind);
				i++;
			}
		}

		return returnNode;
	}

}
