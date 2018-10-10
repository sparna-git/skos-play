package fr.sparna.rdf.skos.printer.cli.normalize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.skos.printer.cli.SkosPlayCliCommandIfc;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class NormalizeLabels implements SkosPlayCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsNormalizeLabels args = (ArgumentsNormalizeLabels)o;

		// TODO configure logging

		log.debug("Normalizing labels for concept scheme : "+args.getConceptScheme()+"...");
		
		// lire le RDF d'input		
		Repository inputRepository = RepositoryBuilderFactory.fromStringList(args.getInput()).get();

		try(RepositoryConnection connection = inputRepository.getConnection()) {
			
			final ListMap<Resource, Literal> labels = new ListMap<Resource, Literal>();
			Perform.on(connection).select(new GetLabelsInSchemeHelper(args.getLang(), (args.getConceptScheme() != null)?SimpleValueFactory.getInstance().createIRI(args.getConceptScheme()):null) {	
				@Override
				protected void handleLabel(Literal label, Literal prefLabel, Resource concept)
				throws TupleQueryResultHandlerException {
						labels.add(
								concept,
								label
						);			
				}
			});		
			
			// output skos:hiddenLabels
			Repository outputRepository = new SailRepository(new MemoryStore());
			outputRepository.initialize();
			
			List<Statement> statements = new ArrayList<Statement>();
			for (Map.Entry<Resource, List<Literal>> anEntry : labels.entrySet()) {
				for (Literal aLabel : anEntry.getValue()) {
					String withoutAccents = fr.sparna.commons.lang.StringUtil.withoutAccents(aLabel.getLabel());
					// only add the unaccented variant if different from the original label
					if(!withoutAccents.equals(aLabel.getLabel())) {
						statements.add(outputRepository.getValueFactory().createStatement(
								anEntry.getKey(),
								outputRepository.getValueFactory().createURI(SKOS.HIDDEN_LABEL),
								outputRepository.getValueFactory().createLiteral(
										fr.sparna.commons.lang.StringUtil.withoutAccents(withoutAccents), 
										aLabel.getLanguage().get()
								)
						));
					}
				}
			}
			
			RepositoryConnection c = outputRepository.getConnection();
			c.add(statements);
			c.commit();
			c.close();
			
			// write to output file
			try(RepositoryConnection outputConnection = outputRepository.getConnection()) {
				log.debug("Writing normalized labels to "+args.getOutput()+"...");
				RepositoryWriter.writeToFile(args.getOutput(), outputConnection);
			}
		
		}
		
		// shutdown repos
		inputRepository.shutDown();
		
		log.debug("Done normalizing labels for concept scheme : "+args.getConceptScheme()+"...");
	}

}
