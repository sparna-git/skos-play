package fr.sparna.rdf.skos.helper;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.rdf4j.toolkit.query.TupleResourceQueryHelper;

public abstract class GetAlignmentInScheme extends TupleResourceQueryHelper {
	
	@Override
	public String getResourcePath() {
		return "/fr/sparna/rdf/skos/helper/GetAlignmentsInScheme.rq";
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept1 = (Resource)binding.getValue("concept1");
		Resource alignementType = (Resource)binding.getValue("align");
		Resource concept2 = (Resource)binding.getValue("concept2");
		
		this.handleAlignment(concept1, alignementType, concept2);
	}
	
	
	protected abstract void handleAlignment(Resource concept, Resource alignementType, Resource targetConcept)
	throws TupleQueryResultHandlerException;
	
}
