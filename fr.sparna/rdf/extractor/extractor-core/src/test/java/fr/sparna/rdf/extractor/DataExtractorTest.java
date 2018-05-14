package fr.sparna.rdf.extractor;

import java.util.Arrays;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.helpers.BufferedGroupingRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Before;
import org.junit.Test;

import fr.sparna.rdf.extractor.CompositeExtractor;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractionSourceFactory;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.DataExtractorHandlerFactory;
import fr.sparna.rdf.extractor.NotifyingDataExtractor;
import fr.sparna.rdf.extractor.NotifyingDataExtractorWrapper;
import fr.sparna.rdf.extractor.RepositoryManagementListener;
import fr.sparna.rdf.extractor.jsonld.JsonLDExtractor;
import fr.sparna.rdf.extractor.microdata.MicrodataExtractor;
import fr.sparna.rdf.extractor.rdfa.RdfaExtractor;

public class DataExtractorTest {

	// private static final String TEST_URL = "http://sparna.fr#head";
	private static final String TEST_URL = "http://paloaltours.org/membres/annuaire/off-editions";
	private static final String REPOSITORY_URL = "http://localhost:8080/rdf4j-server/repositories/cuve";
	
	private CompositeExtractor thePress;
	private DataExtractorHandlerFactory handlerFactory;
	
	@Before
	public void init() {
		thePress = new CompositeExtractor();
		thePress.setExtractors(Arrays.asList(new DataExtractor[] {
				new RdfaExtractor(),
				new JsonLDExtractor(),
				new MicrodataExtractor()
		}));
		
		handlerFactory = new DataExtractorHandlerFactory();
	}
	
	@Test
	public void simpleTest() throws Exception {
		
		String uri = "http://www.ports-37.com/index.php/8-mairie";
		
        // create inMemory DB
         Repository cuve = new SailRepository(new MemoryStore());
         cuve.initialize();
//		Repository cuve = new HTTPRepository(REPOSITORY_URL);
//		cuve.initialize();
		
        // create source
        DataExtractionSource source = new DataExtractionSourceFactory().buildSource(cuve.getValueFactory().createIRI(uri));
        
        // create a notifying DataPress that will handle cleaning and administrive metadata of the repository
        NotifyingDataExtractor notifyingDataExtractor = new NotifyingDataExtractorWrapper(thePress, new RepositoryManagementListener(cuve));
        
        try(RepositoryConnection connection = cuve.getConnection()) {
	        // create the target handler
	        RDFHandler handler = this.handlerFactory.newHandler(connection, source.getDocumentIri());
	        
	        // extract
	        notifyingDataExtractor.extract(source, handler);      
        }
        
        // determine output format and write response
        RDFHandler writer = RDFWriterRegistry.getInstance().get(RDFFormat.NQUADS).get().getWriter(System.out);
        cuve.getConnection().export(new BufferedGroupingRDFHandler(1024*24, writer));
	}
	
}
