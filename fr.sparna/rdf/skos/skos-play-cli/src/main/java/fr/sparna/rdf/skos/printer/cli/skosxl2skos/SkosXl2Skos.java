package fr.sparna.rdf.skos.printer.cli.skosxl2skos;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.skos.printer.cli.SkosPlayCliCommandIfc;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class SkosXl2Skos implements SkosPlayCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsSkosXl2Skos args = (ArgumentsSkosXl2Skos)o;

		// TODO configure logging

		// lire le RDF d'input		
		RepositoryFactoryIfc factory = new StringRepositoryFactory(args.getInput());
		Repository inputRepository = factory.createNewRepository();

		// Apply transformation
		ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getSkosXl2SkosRuleset(args.isCleanXl())));	
		au.execute(inputRepository);
		
		// output in an output file
		RepositoryWriter.writeToFile(args.getOutput(), inputRepository);
		
		// shutdown repos
		inputRepository.shutDown();

	}

}
