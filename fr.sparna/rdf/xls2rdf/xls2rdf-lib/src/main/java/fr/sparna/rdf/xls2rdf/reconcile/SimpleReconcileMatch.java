package fr.sparna.rdf.xls2rdf.reconcile;

import java.util.Collections;
import java.util.List;

public class SimpleReconcileMatch implements ReconcileMatchIfc {

	private String id;
	private String name;
	private List<String> types;
	private double score;
	private boolean match;
	
	public SimpleReconcileMatch(String id, String name, String type) {
		this(id, name, Collections.singletonList(type), 1.0d, true);
	}
	
	public SimpleReconcileMatch(String id, String name, List<String> types, double score, boolean match) {
		super();
		this.id = id;
		this.name = name;
		this.types = types;
		this.score = score;
		this.match = match;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setMatch(boolean match) {
		this.match = match;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getTypes() {
		return types;
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public boolean isMatch() {
		return match;
	}

}
