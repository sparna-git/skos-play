package fr.sparna.rdf.sesame.jena.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openjena.jenasesame.util.Convert;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.impl.BindingImpl;

import com.hp.hpl.jena.query.QuerySolution;

/**
 * A BindingSet wrapping a Jena QuerySolution
 * 
 * @author Thomas Francart
 * 
 */
public class JenaBindingSet implements BindingSet {

	protected QuerySolution solution;
	protected ValueFactory factory;
	
	public JenaBindingSet(QuerySolution solution, ValueFactory factory) {
		super();
		this.solution = solution;
		this.factory = factory;
	}

	@Override
	public Set<String> getBindingNames() {
		HashSet<String> result = new HashSet<String>();
		for (Iterator<String> varNamesIterator = solution.varNames(); varNamesIterator.hasNext();) {
			String aVarName = varNamesIterator.next();
			result.add(aVarName);
		}
		return result;
	}
	
	@Override
	public Binding getBinding(String key) {
		if(!solution.contains(key)) {
			return null;
		}
		return new BindingImpl(key, getValue(key));
	}

	@Override
	public Value getValue(String key) {
		if(!solution.contains(key)) {
			return null;
		}

		return Convert.nodeToValue(factory, solution.get(key).asNode());
	}

	@Override
	public boolean hasBinding(String arg0) {
		return solution.contains(arg0);
	}

	@Override
	public Iterator<Binding> iterator() {
		// TODO : optimize
		List<Binding> allBindings = new ArrayList<Binding>();
		Set<String> bindingNames = this.getBindingNames();
		for (Iterator<String> iterator = bindingNames.iterator(); iterator.hasNext();) {
			String aBindingName = iterator.next();
			allBindings.add(this.getBinding(aBindingName));
		}
		
		return allBindings.iterator();
	}

	@Override
	public int size() {
		return getBindingNames().size();
	}

}
