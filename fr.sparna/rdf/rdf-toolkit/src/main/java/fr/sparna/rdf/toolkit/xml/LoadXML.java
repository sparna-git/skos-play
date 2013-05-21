package fr.sparna.rdf.toolkit.xml;

import java.io.File;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromXML;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class LoadXML implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		ArgumentsLoadXML args = (ArgumentsLoadXML)o;
		
		// essayer d'utiliser saxon
		try {
			Thread.currentThread().getContextClassLoader().loadClass("net.sf.saxon.TransformerFactoryImpl");
			System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		} catch (java.lang.ClassNotFoundException e) {
			System.out.println("Saxon class not found. If you want to use XSLT 2.0 features, include saxon jar in the runtime classpath.");
		}

		// init factory
		RepositoryBuilder factory = new RepositoryBuilder(
				new LocalMemoryRepositoryFactory()
		);
		// add the LoadFromXML operation
		factory.addOperation(new LoadFromXML(new File(args.getInput()), args.getXsl()));
		// obtain repository loaded with data
		Repository r = factory.createNewRepository();

		// write output result
		RepositoryWriter.writeToFile(args.getOutput(), r);
	}

}
