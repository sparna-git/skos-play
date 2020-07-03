package fr.sparna.rdf.skos.printer.cli.skosxl2skos;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.ApplyUpdates;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;
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
		Repository inputRepository = RepositoryBuilderFactory.fromStringList(args.getInput()).get();

		try(RepositoryConnection connection = inputRepository.getConnection()) {
			// Apply transformation
			ApplyUpdates.fromQueryReaders(SKOSRules.getSkosXl2SkosRuleset(args.isCleanXl())).accept(connection);

			// output in an output file
			RepositoryWriter.writeToFile(args.getOutput(), connection);
		}
		
		// shutdown repos
		inputRepository.shutDown();

	}

}
