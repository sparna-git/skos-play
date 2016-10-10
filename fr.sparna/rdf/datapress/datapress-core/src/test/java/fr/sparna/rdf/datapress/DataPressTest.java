package fr.sparna.rdf.datapress;

import java.util.Arrays;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.helpers.BufferedGroupingRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Before;
import org.junit.Test;

import fr.sparna.rdf.datapress.jsonld.JsonLDPress;
import fr.sparna.rdf.datapress.microdata.MicrodataPress;
import fr.sparna.rdf.datapress.rdfa.RdfaPress;

public class DataPressTest {

	// private static final String TEST_URL = "http://sparna.fr#head";
	private static final String TEST_URL = "http://paloaltours.org/membres/annuaire/off-editions";
	private static final String REPOSITORY_URL = "http://localhost:8080/rdf4j-server/repositories/cuve";
	
	private CompositePress thePress;
	private DataPressHandlerFactory handlerFactory;
	
	@Before
	public void init() {
		thePress = new CompositePress();
		thePress.setPresses(Arrays.asList(new DataPress[] {
				new RdfaPress(),
				new JsonLDPress(),
				new MicrodataPress()
		}));
		
		handlerFactory = new DataPressHandlerFactory();
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
        DataPressSource source = new DataPressSourceFactory().buildSource(cuve.getValueFactory().createIRI(uri));
        
        // create a notifying DataPress that will handle cleaning and administrive metadata of the repository
        NotifyingDataPress notifyingDataPress = new NotifyingDataPressWrapper(thePress, new RepositoryManagementListener(cuve));
        
        // create the target handler
        RDFHandler handler = this.handlerFactory.newHandler(cuve, source.getDocumentIri());
        
        // extract
        notifyingDataPress.press(source, handler);      
        
        // determine output format and write response
        RDFHandler writer = RDFWriterRegistry.getInstance().get(RDFFormat.NQUADS).get().getWriter(System.out);
        cuve.getConnection().export(new BufferedGroupingRDFHandler(1024*24, writer));
	}
	
}
