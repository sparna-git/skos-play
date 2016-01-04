package fr.sparna.rdf.toolkit.translate;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.util.Namespaces;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Translate implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsTranslate args = (ArgumentsTranslate)o;
		
		// TODO : configure logging
		
		// lire le RDF d'input
		StringRepositoryFactory factory = new StringRepositoryFactory(args.getInput());
		Repository r = factory.createNewRepository();
		
		// preparer le dumper
		RepositoryWriter writer = new RepositoryWriter(r);
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
		
		// mettre un petit message
		log.info("Translated "+args.getInput()+" in output file : "+args.getOutput());
	}

}
