package fr.sparna.rdf.toolkit.select;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.impl.SimpleBinding;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.query.SPARQLQueryBindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ClasspathUnzip;
import fr.sparna.rdf.rdf4j.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.rdf4j.toolkit.handler.CsvHandler;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReader;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.toolkit.ListFilesRecursive;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Select implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsSelect args = (ArgumentsSelect)o;

		// TODO configure logging

		// lire le RDF d'input
		Repository inputRepository = new AutoDetectRepositoryFactory(args.getInput()).get();

		// init potential bindings
		SPARQLQueryBindingSet bindings = new SPARQLQueryBindingSet();
		if(args.getBindings() != null) {
			for (Map.Entry<String, String> anEntry : args.getBindings().entrySet()) {
					Value value = null;
					try {
						URI uriValue = new URI(anEntry.getValue());
						if(!uriValue.isAbsolute()) {
							value = SimpleValueFactory.getInstance().createLiteral(anEntry.getValue());
						}
						value = SimpleValueFactory.getInstance().createIRI(anEntry.getValue());
					} catch (Exception e) {
						value = SimpleValueFactory.getInstance().createLiteral(anEntry.getValue());
					}
					bindings.addBinding(new SimpleBinding(anEntry.getKey(), value));
			}
		}
		
		switch(args.getMode()) {
		case HTML : {
			// init writer
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args.getOutput())));

			// print header
			SimpleHTMLReportHandler.printHeader(writer);

			// executer les SPARQL
			List<File> sparqls = ListFilesRecursive.listFilesRecursive(args.getQueryDirectoryOrFile());
			Collections.sort(sparqls, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
				}
			});

			try(RepositoryConnection connection = inputRepository.getConnection()) {
				for (final File file : sparqls) {
					log.debug("Executing query in "+file.getAbsolutePath()+"...");
					SimpleQueryReader reader = new SimpleQueryReader(file);
					log.debug("Query is "+reader.get());

					SimpleSparqlOperation query = new SimpleSparqlOperation(reader);
					if(bindings.size() > 0) {
						query.setBindingSet(bindings);
					}

					try {
						Perform.on(connection).select(
								query,
								new SimpleHTMLReportHandler(
										writer,
										reader.get(),
										file.getName(),
										file.getName().replaceAll("-", " ").replaceAll("_", " ").replaceAll(".sparql", "")
										)									
								);
					} catch (Exception e) {
						log.error("Error in query in "+file.getAbsolutePath()+"...");
						e.printStackTrace();
						// passer a la suivante
					}
				}	
			}

			// print footer
			SimpleHTMLReportHandler.printFooter(writer);

			// finalize writer
			writer.close();

			// extract css
			ClasspathUnzip.unzipFileFromClassPath("bootstrap.min.css", args.getOutput().getAbsoluteFile().getParent(), false);

			break;
		} case CSV : {
			// create output dir
			File outputDir = args.getOutput();
			if(!outputDir.exists()) {
				outputDir.mkdirs();
			}

			// executer les SPARQL
			List<File> sparqls = ListFilesRecursive.listFilesRecursive(args.getQueryDirectoryOrFile());

			try(RepositoryConnection connection = inputRepository.getConnection()) {
				for (final File file : sparqls) {
					log.debug("Executing query in "+file.getAbsolutePath()+"...");
					SimpleQueryReader reader = new SimpleQueryReader(file);
					log.debug("Query is :\n"+reader.get());

					SimpleSparqlOperation query = new SimpleSparqlOperation(reader);
					if(bindings.size() > 0) {
						query.setBindingSet(bindings);
					}

					// init writer
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDir, file.getName()+".csv"))));

					try {
						Perform.on(connection).select(
								query,
								new CsvHandler(writer, true, true)
								);
					} catch (Exception e) {
						log.error("Error in query in "+file.getAbsolutePath()+"...");
						e.printStackTrace();
						// passer a la suivante
					}

					// finalize writer
					writer.close();
				}
			}

			break;
		}
		}			
		

		
		// shutdown repos
		inputRepository.shutDown();

	}

}
