package fr.sparna.rdf.sesame.jena.repository;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.query.impl.AbstractUpdate;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * An update statement on a Jena Model.
 * 
 * @author Thomas Francart
 */
public class JenaUpdate extends AbstractUpdate implements Update {

	protected ValueFactory factory;
	protected Model model;
	protected String sparql;
	protected String baseURI;

	public JenaUpdate(Model model, ValueFactory factory, String sparql, String baseURI) {
		super();
		this.model = model;
		this.factory = factory;
		this.sparql = sparql;
		this.baseURI = baseURI;
	}

	@Override
	public void execute() throws UpdateExecutionException {
		// TODO
	}
	


}
