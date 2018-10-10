package fr.sparna.rdf.skos.printer.cli.translation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.xml.fop.FopProvider;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.ApplyUpdates;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.cli.SkosPlayCliCommandIfc;
import fr.sparna.rdf.skos.printer.reader.AbstractKosDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.BodyReader;
import fr.sparna.rdf.skos.printer.reader.ConceptBlockReader;
import fr.sparna.rdf.skos.printer.reader.TranslationTableReverseDisplayGenerator;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.toolkit.GetLanguagesHelper;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class Translation implements SkosPlayCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsTranslation args = (ArgumentsTranslation)o;

		// TODO configure logging

		// lire le RDF d'input
		Repository inputRepository = RepositoryBuilderFactory.fromStringList(args.getInput()).get();

		try(RepositoryConnection connection = inputRepository.getConnection()) {
			// SKOS-XL
			ApplyUpdates.fromQueryReaders(SKOSRules.getSkosXl2SkosRuleset()).accept(connection);
			
			// build result document
			KosDocument document = new KosDocument();
			
			// prepare a list of generators
			List<AbstractKosDisplayGenerator> generators = new ArrayList<AbstractKosDisplayGenerator>();
			
			// read all potential languages and exclude the main one
			final String mainLang = args.getLang();
			final List<String> additionalLangs = new ArrayList<String>();
			Perform.on(connection).select(new GetLanguagesHelper() {			
				@Override
				protected void handleLang(Literal lang) throws TupleQueryResultHandlerException {
					if(!lang.stringValue().equals(mainLang) && !lang.stringValue().equals("")) {
						additionalLangs.add(lang.stringValue());
					}
				}
			});
			
			// add translation tables for each additional languages
			for (int i=0;i<additionalLangs.size(); i++) {			
				String anAdditionalLang = additionalLangs.get(i);
				ConceptBlockReader aCbReader = new ConceptBlockReader();
				// aCbReader.setLinkDestinationIdPrefix("alpha");
				TranslationTableReverseDisplayGenerator ttGen = new TranslationTableReverseDisplayGenerator(
						connection,
						aCbReader,
						anAdditionalLang,
						"trans"+i);
				generators.add(ttGen);
			}
			
			BodyReader bodyReader = new BodyReader(generators);
			document.setBody(bodyReader.readBody(args.getLang(), (args.getConceptScheme() != null)?SimpleValueFactory.getInstance().createIRI(args.getConceptScheme()):null));
	
			// if debug needed
			// Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
			// m.setProperty("jaxb.formatted.output", true);
			// m.marshal(display, System.out);
			// m.marshal(document, new File("src/main/resources/alpha-index-output-test.xml"));
			
			if(args.getFopConfigPath() != null) {
				log.info("Will use FOP config file path : "+args.getFopConfigPath());
			}
			DisplayPrinter printer = new DisplayPrinter(new FopProvider(args.getFopConfigPath()));
			printer.setStyle(args.getStyle());
			printer.print(document, args.getOutput(), args.getLang(), args.getFormat());
		}
		
		// shutdown repos
		inputRepository.shutDown();
	}

}
