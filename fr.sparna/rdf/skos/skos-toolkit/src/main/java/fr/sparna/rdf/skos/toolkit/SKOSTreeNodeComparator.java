package fr.sparna.rdf.skos.toolkit;

import java.text.Collator;
import java.util.Comparator;

import fr.sparna.commons.tree.GenericTreeNode;

public class SKOSTreeNodeComparator implements Comparator<GenericTreeNode<SKOSTreeNode>> {

	private Collator collator;
	
	public SKOSTreeNodeComparator(Collator collator) {
		super();
		this.collator = collator;
	}

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
	
}
