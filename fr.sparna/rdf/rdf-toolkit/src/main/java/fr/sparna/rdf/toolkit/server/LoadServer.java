package fr.sparna.rdf.toolkit.server;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
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
		
		if (args.isNamedGraphAware() && args.getGraph() == null) {
			log.info("Loading *will* be named-graph aware...");
		} else if(args.getGraph() != null) {
			log.warn("graph URI given, named-graph parameter not used");
		} else {
			log.info("Loading will NOT be named-graph aware...");
		}

		RepositoryBuilder factory = new RepositoryBuilder(new EndpointRepositoryFactory(args.getServer(), true));
		if(args.isClearBeforeLoading()) {
			factory.addOperation(new ClearRepository());
		}
		LoadFromFileOrDirectory operation = new LoadFromFileOrDirectory(args.getInput());
		if(args.getGraph() != null) {
			operation.setTargetGraph(URI.create(args.getGraph()));
		} else {
			operation.setAutoNamedGraphs(args.isNamedGraphAware());
		}
		
		factory.addOperation(operation);
		
		log.info("Running loading in server...");
		factory.createNewRepository();
		
		// mettre un petit message
		log.info("Done loading "+args.getInput()+" in server : "+args.getServer());

	}

}
