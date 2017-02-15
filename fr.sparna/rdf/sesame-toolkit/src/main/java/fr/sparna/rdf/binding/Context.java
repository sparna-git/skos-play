package fr.sparna.rdf.binding;

import org.eclipse.rdf4j.model.Model;

public class Context {

	protected Model model;

	public Context(Model model) {
		super();
		this.model = model;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	
	
}
