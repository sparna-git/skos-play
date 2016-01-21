package fr.sparna.rdf.skos.printer.cli.normalize;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
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
		RepositoryFactoryIfc factory = new StringRepositoryFactory(args.getInput());
		Repository inputRepository = factory.createNewRepository();

				
		final ListMap<Resource, Literal> labels = new ListMap<Resource, Literal>();
		Perform.on(inputRepository).select(new GetLabelsInSchemeHelper(args.getLang(), (args.getConceptScheme() != null)?URI.create(args.getConceptScheme()):null) {	
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
									aLabel.getLanguage()
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
		log.debug("Writing normalized labels to "+args.getOutput()+"...");
		RepositoryWriter.writeToFile(args.getOutput(), outputRepository);
		
		// shutdown repos
		inputRepository.shutDown();
		
		log.debug("Done normalizing labels for concept scheme : "+args.getConceptScheme()+"...");
	}

}
