package fr.sparna.rdf.toolkit.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.DefaultRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ClearRepository;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class LoadServer implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsLoadServer args = (ArgumentsLoadServer)o;
		
		// TODO : configure logging
		
		// get connection to remote server
		log.info("Loading "+args.getInput()+" in server : "+args.getServer()+"...");
		
		if (args.isClearBeforeLoading()) {
			log.info("Repository *will* be cleared at load...");
		} else {
			log.info("Repository will NOT be cleared at load...");
		}
		
		if (args.isNamedGraphAware()) {
			log.info("Loading *will* be named-graph aware...");
		} else {
			log.info("Loading will NOT be named-graph aware...");
		}

		DefaultRepositoryFactory factory = new DefaultRepositoryFactory(new EndpointRepositoryFactory(args.getServer()));
		if(args.isClearBeforeLoading()) {
			factory.addOperation(new ClearRepository());
		}
		LoadFromFileOrDirectory operation = new LoadFromFileOrDirectory(args.getInput());
		operation.setAutoNamedGraphs(args.isNamedGraphAware());
		factory.addOperation(operation);
		
		log.info("Running loading in server...");
		factory.createNewRepository();
		
		// mettre un petit message
		log.info("Done loading "+args.getInput()+" in server : "+args.getServer());

	}

}
