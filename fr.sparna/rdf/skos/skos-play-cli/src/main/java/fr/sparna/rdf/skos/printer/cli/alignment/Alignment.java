package fr.sparna.rdf.skos.printer.cli.alignment;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.cli.SkosPlayCliCommandIfc;
import fr.sparna.rdf.skos.printer.reader.AlignmentDataHarvesterCachedLoader;
import fr.sparna.rdf.skos.printer.reader.AlignmentDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.AlphaIndexDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.BodyReader;
import fr.sparna.rdf.skos.printer.reader.ConceptBlockReader;
import fr.sparna.rdf.skos.printer.reader.HeaderAndFooterReader;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.toolkit.GetLanguagesHelper;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class Alignment implements SkosPlayCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsAlignment args = (ArgumentsAlignment)o;

		// TODO configure logging

		// lire le RDF d'input
		
		RepositoryFactoryIfc factory = new StringRepositoryFactory(args.getInput());
		Repository inputRepository = factory.createNewRepository();

		// SKOS-XL
		ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getSKOSXLRuleset()));
		au.execute(inputRepository);
		
		// build result document
		KosDocument document = new KosDocument();
		
		ConceptBlockReader cbr = new ConceptBlockReader(inputRepository);
		cbr.setLinkDestinationIdPrefix("alignId");
		AlignmentDisplayGenerator reader = new AlignmentDisplayGenerator(
				inputRepository,
				cbr,
				"alignId",
				new AlignmentDataHarvesterCachedLoader(args.getCacheDir().getAbsolutePath(), RDFFormat.RDFXML));
		
		
		reader.setSeparateByTargetScheme(true);
		if(args.isBySourceConcept()) {
			reader.setSeparateByTargetScheme(false);
		}
		
		BodyReader bodyReader = new BodyReader(reader);
		document.setBody(bodyReader.readBody(args.getLang(), (args.getConceptScheme() != null)?URI.create(args.getConceptScheme()):null));

		// if debug needed
		// Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		// m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		// m.marshal(document, new File("src/main/resources/alpha-index-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		switch(args.getFormat()) {
		case HTML : {
			printer.printToHtml(document, args.getOutput(), args.getLang());
			break;
		}
		case PDF : {
			printer.printToPdf(document, args.getOutput(), args.getLang());
			break;
		}
		}
		
		// shutdown repos
		inputRepository.shutDown();

	}

}
