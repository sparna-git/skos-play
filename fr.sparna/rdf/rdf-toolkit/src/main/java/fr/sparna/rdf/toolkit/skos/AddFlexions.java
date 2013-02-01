package fr.sparna.rdf.toolkit.skos;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.skos.FlexionsAdder;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class AddFlexions implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// TODO : configure logging
		
		ArgumentsAddFlexions args = (ArgumentsAddFlexions)o;
		
		// lire le RDF d'input
		StringRepositoryFactory factory = new StringRepositoryFactory(args.getInput());
		Repository r = factory.createNewRepository();

		FlexionsAdder adder = new FlexionsAdder();
		adder.addFlexions(r);
		
		// output new RDF
		RepositoryWriter.writeToFile(args.getOutput(), r);
	}
}
