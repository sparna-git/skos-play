package fr.sparna.rdf.skos.printer.cli.alignment;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.xml.fop.FopProvider;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.ApplyUpdates;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.cli.SkosPlayCliCommandIfc;
import fr.sparna.rdf.skos.printer.reader.AlignmentDataHarvesterCachedLoader;
import fr.sparna.rdf.skos.printer.reader.AlignmentDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.BodyReader;
import fr.sparna.rdf.skos.printer.reader.ConceptBlockReader;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class Alignment implements SkosPlayCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsAlignment args = (ArgumentsAlignment)o;

		// TODO configure logging

		// lire le RDF d'input
		
		Repository inputRepository = RepositoryBuilderFactory.fromStringList(args.getInput()).get();

		try(RepositoryConnection connection = inputRepository.getConnection()) {
			// SKOS-XL
			ApplyUpdates.fromQueryReaders(SKOSRules.getSkosXl2SkosRuleset()).accept(connection);
			
			// build result document
			KosDocument document = new KosDocument();
			
			ConceptBlockReader cbr = new ConceptBlockReader();
			cbr.setLinkDestinationIdPrefix("alignId");
			AlignmentDisplayGenerator reader = new AlignmentDisplayGenerator(
					connection,
					cbr,
					"alignId",
					new AlignmentDataHarvesterCachedLoader(args.getCacheDir().getAbsolutePath(), RDFFormat.RDFXML));
			
			
			reader.setSeparateByTargetScheme(true);
			if(args.isBySourceConcept()) {
				reader.setSeparateByTargetScheme(false);
			}
			
			BodyReader bodyReader = new BodyReader(reader);
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
