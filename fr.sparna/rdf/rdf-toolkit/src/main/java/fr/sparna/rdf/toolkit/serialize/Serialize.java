package fr.sparna.rdf.toolkit.serialize;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Serialize implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsSerialize args = (ArgumentsSerialize)o;
		
		// TODO : configure logging
		
		// lire le RDF d'input
		Repository r = new RepositoryBuilderFactory(args.getInput()).get().get();
		
		try(RepositoryConnection connection = r.getConnection()) {
			// preparer le dumper
			RepositoryWriter writer = new RepositoryWriter(connection);
			
			// on demande de trier explicitement si besoin
			if(args.isNoOrder()) {
				writer.setSorting(false);
			} else {
				writer.setSorting(true);
			}		
			
			// on positionne les namespaces
			writer.setNamespacesMap(args.getNamespaceMappings());

			// dumper le repository trié
			writer.writeToFile(args.getOutput());
			
		}
		
		// mettre un petit message
		log.info("Translated "+args.getInput()+" in output file : "+args.getOutput());	

	}

}
