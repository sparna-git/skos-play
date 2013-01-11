package fr.sparna.rdf.toolkit.infer;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.ConfigRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.DefaultRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.OWLIMConfigProvider;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Infer implements ToolkitCommandIfc {

	private static Logger log = LoggerFactory.getLogger(Infer.class.getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsInfer args = (ArgumentsInfer)o;
		
		// TODO : configure logging
		
		log.debug("Using ruleset : "+args.getRuleset());
		ConfigRepositoryFactory delegateFactory = new ConfigRepositoryFactory(
				new OWLIMConfigProvider("owlim-base.ttl", args.getRuleset())
		);
		delegateFactory.setRepositoryName("test");
		delegateFactory.setCleanAtStartup(true);
		
		DefaultRepositoryFactory factory = new DefaultRepositoryFactory(
				delegateFactory,
				new LoadFromFileOrDirectory(args.getInput())
		);		
		log.debug("Init repository...");
		Repository r = factory.createNewRepository();
		
		log.debug("Output inference result into "+args.getOutput());
		RepositoryWriter.writeToFile(args.getOutput(), r);
		
		log.debug("Closing repository...");
		r.shutDown();
		log.debug("Done.");
	}

}
