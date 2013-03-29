package fr.sparna.rdf.toolkit.skos;

import java.util.logging.Level;

import org.apache.log4j.BasicConfigurator;
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
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter").setLevel(org.apache.log4j.Level.ALL);
		
		ArgumentsAddFlexions args = (ArgumentsAddFlexions)o;
		
		// lire le RDF d'input
		StringRepositoryFactory factory = new StringRepositoryFactory(args.getInput());
		Repository r = factory.createNewRepository();

		log.info("Excluding concept schemes from flexions : "+args.getConceptSchemesToExclude());
		
		FlexionsAdder adder = new FlexionsAdder();
		adder.addFlexions(r, args.getConceptSchemesToExclude());
		
		// output new RDF
		RepositoryWriter.writeToFile(args.getOutput(), r);
	}
}
