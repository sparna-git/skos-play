package fr.sparna.rdf.toolkit.update;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ListFilesRecursive;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Update implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		// TODO : configure logging
		ArgumentsUpdate args = (ArgumentsUpdate)o;
		
		// initialiser la connection aux données
		Repository r = new AutoDetectRepositoryFactory(args.getInput()).get();

		List<File> sparqls = ListFilesRecursive.listFilesRecursive(args.getUpdateDirectory());
		try(RepositoryConnection connection = r.getConnection()) {
			for (File file : sparqls) {
				log.debug("Applying update "+file.getAbsolutePath()+"...");
				Perform.on(connection).update(FileUtils.readFileToString(file, Charset.defaultCharset()));
			}
			
			// output updated repository
			if(args.getOutputGraphs() != null && args.getOutputGraphs().size() > 0) {
				try {
					RepositoryWriter.writeToFile(args.getOutput().getAbsolutePath(), connection, args.getOutputGraphsIRIs());
				} catch (URISyntaxException e) {
					throw e;
				}
			} else {
				RepositoryWriter.writeToFile(args.getOutput(), connection);
			}
		}
		

		
	}

}
