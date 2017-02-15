package fr.sparna.rdf.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.w3c.dom.Node;

public class Marshaller {

	protected Context context;
	
	public Marshaller(Context context) {
		super();
		this.context = context;
	}

	public void marshal(Resource input, Node node) {
		List<Resource> types = readTypes(input);
		
		// select a main type
		// select the most specific type ?
	}
	
	protected List<Resource> readTypes(Resource input) {
		Set<Value> rawTypes = context.getModel().filter(input, RDF.TYPE, null).objects();
		List<Resource> types = new ArrayList<Resource>();
		rawTypes.stream().forEach(type -> {
			if(type instanceof Resource) {
				types.add((Resource)type);
			}
		});
		
		return types;
	}
	
}
