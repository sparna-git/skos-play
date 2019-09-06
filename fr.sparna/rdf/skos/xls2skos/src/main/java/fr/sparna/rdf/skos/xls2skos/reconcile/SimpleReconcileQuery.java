package fr.sparna.rdf.skos.xls2skos.reconcile;

import java.util.List;

public class SimpleReconcileQuery implements ReconcileQueryIfc {

	private String query;
	private List<String> types;
	
	public SimpleReconcileQuery(String query) {
		super();
		this.query = query;
	}

	public SimpleReconcileQuery(String query, List<String> types) {
		super();
		this.query = query;
		this.types = types;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public List<String> getTypes() {
		return types;
	}

}
