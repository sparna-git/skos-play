package fr.sparna.rdf.toolkit.construct;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.LocalMemoryRepositorySupplier;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ListFilesRecursive;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Construct implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsConstruct args = (ArgumentsConstruct)o;
		
		// TODO configure logging
		
		// lire le RDF d'input
		Repository inputRepository = new AutoDetectRepositoryFactory(args.getInput()).get();
		
		// preparer le RDF d'output
		Repository outputRepository = new LocalMemoryRepositorySupplier().get();
		
		// executer les SPARQL
		List<File> sparqls = ListFilesRecursive.listFilesRecursive(args.getQueryDirectoryOrFile());
		
		// open output connection
		try(RepositoryConnection outputConnection = outputRepository.getConnection()) {
			// open input connection
			try(RepositoryConnection inputConnection = inputRepository.getConnection()) {
				for (File file : sparqls) {
					log.debug("Applying rule "+file.getAbsolutePath()+"...");
					Perform.on(inputConnection).graph(
							FileUtils.readFileToString(file, Charset.defaultCharset()),
							new RDFInserter(outputConnection)
					);
				}
			}
			
			// write output
			RepositoryWriter.writeToFile(args.getOutput(), outputConnection);
		}

		// shutdown repos
		inputRepository.shutDown();
		outputRepository.shutDown();
	}

}
