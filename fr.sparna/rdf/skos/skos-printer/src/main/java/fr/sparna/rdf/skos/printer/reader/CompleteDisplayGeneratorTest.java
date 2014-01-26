package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.openrdf.model.Literal;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.toolkit.GetLanguagesHelper;

public class CompleteDisplayGeneratorTest {
	
	
	public static void main(String... args) throws Exception {			
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build result document
		KosDocument document = new KosDocument();
		
		// build and set header
		HeaderReader headerReader = new HeaderReader(r);
		KosDocumentHeader header = headerReader.read("en", (args.length > 1)?URI.create(args[1]):null);
		document.setHeader(header);
		
		// prepare a list of generators
		List<AbstractKosDisplayGenerator> generators = new ArrayList<AbstractKosDisplayGenerator>();
		
		// read all potential languages and exclude the main one
		final List<String> additionalLangs = new ArrayList<String>();
		Perform.on(r).select(new GetLanguagesHelper() {			
			@Override
			protected void handleLang(Literal lang) throws TupleQueryResultHandlerException {
				if(!lang.stringValue().equals("en") && !lang.stringValue().equals("")) {
					additionalLangs.add(lang.stringValue());
				}
			}
		});
			
		// alphabetical display
		ConceptBlockReader alphaCbReader = new ConceptBlockReader(r);
		alphaCbReader.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES);
		alphaCbReader.setAdditionalLabelLanguagesToInclude(additionalLangs);
		alphaCbReader.setLinkDestinationIdPrefix("hier");
		AlphaIndexDisplayGenerator alphaGen = new AlphaIndexDisplayGenerator(
				r,
				alphaCbReader,
				"alpha"
		);
		generators.add(alphaGen);
		
		// hierarchical display
		ConceptBlockReader hierCbReader = new ConceptBlockReader(r);
		hierCbReader.setLinkDestinationIdPrefix("alpha");
		HierarchicalDisplayGenerator hierarchyGen = new HierarchicalDisplayGenerator(
				r,
				hierCbReader,
				"hier"
		);
		generators.add(hierarchyGen);
		
		// add translation tables for each additional languages
		for (int i=0;i<additionalLangs.size(); i++) {
			String anAdditionalLang = additionalLangs.get(i);
			ConceptBlockReader aCbReader = new ConceptBlockReader(r);
			aCbReader.setLinkDestinationIdPrefix("alpha");
			TranslationTableReverseDisplayGenerator ttGen = new TranslationTableReverseDisplayGenerator(
					r,
					aCbReader,
					anAdditionalLang,
					"trans"+i);
			generators.add(ttGen);
		}
		
		BodyReader bodyReader = new BodyReader(generators);
		document.setBody(bodyReader.readBody("en", (args.length > 1)?URI.create(args[1]):null));
		
		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(document, new File("src/main/resources/complete-display-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.printToHtml(document, new File("display-test.html"));
		printer.printToPdf(document, new File("display-test.pdf"));

	}

}
