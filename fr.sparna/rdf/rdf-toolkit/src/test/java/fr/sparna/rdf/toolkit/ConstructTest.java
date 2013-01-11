package fr.sparna.rdf.toolkit;

import java.io.File;

import org.junit.Test;

import fr.sparna.rdf.toolkit.construct.ArgumentsConstruct;
import fr.sparna.rdf.toolkit.construct.Construct;


public class ConstructTest {

	@Test
	public void testConstruct() throws Exception {
		ArgumentsConstruct args = new ArgumentsConstruct();
		args.setInput("src/main/resources/examples/construct/data");
		args.setQueryDirectoryOrFile(new File("src/main/resources/examples/construct/queries"));
		args.setOutput(new File("src/main/resources/examples/construct/output.n3"));
		Construct construct = new Construct();
		construct.execute(args);
	}
	
}
