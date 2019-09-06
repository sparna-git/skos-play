package fr.sparna.rdf.skos.xls2skos.reconcile;

import java.util.Collections;
import java.util.List;

public class SimpleReconcileResult implements ReconcileResultIfc {

	private List<ReconcileMatchIfc> matches;
	
	
	public SimpleReconcileResult(String id, String name, String type) {
		this(Collections.singletonList(new SimpleReconcileMatch(id, name, type)));
	}
	
	public SimpleReconcileResult(List<ReconcileMatchIfc> matches) {
		super();
		this.matches = matches;
	}

	public void setMatches(List<ReconcileMatchIfc> matches) {
		this.matches = matches;
	}

	@Override
	public List<ReconcileMatchIfc> getMatches() {
		return matches;
	}
	
}
