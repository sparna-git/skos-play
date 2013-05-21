package fr.sparna.rdf.toolkit.select;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ClasspathUnzip;
import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.handler.CSVHandler;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Select implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsSelect args = (ArgumentsSelect)o;

		// TODO configure logging

		// lire le RDF d'input
		Repository inputRepository = new AutoDetectRepositoryFactory(args.getInput()).createNewRepository();

		// init potential bindings
		HashMap<String, Object> bindings = new HashMap<String, Object>();
		if(args.getBindings() != null) {
			for (Map.Entry<String, String> anEntry : args.getBindings().entrySet()) {
					Object value = null;
					try {
						value = new URI(anEntry.getValue());
					} catch (Exception e) {
						value = anEntry.getValue();
					}

					if(!((URI)value).isAbsolute()) {
						value = anEntry.getValue();
					}
					
					bindings.put(anEntry.getKey(), value);
			}
		}

		switch(args.getMode()) {
		case HTML : {
			// init writer
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args.getOutput())));

			// print header
			SimpleHTMLReportHandler.printHeader(writer);

			// executer les SPARQL
			List<File> sparqls = FileUtil.listFilesRecursive(args.getQueryDirectoryOrFile());
			Collections.sort(sparqls, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
				}
			});
			for (final File file : sparqls) {
				log.debug("Executing query in "+file.getAbsolutePath()+"...");
				SPARQLQueryBuilder builder = new SPARQLQueryBuilder(file);
				log.debug("Query is "+builder.getSPARQL());
				
				SPARQLQuery query = new SPARQLQuery(builder);
				if(!bindings.isEmpty()) {
					query.setBindings(bindings);
				}
		
				try {
					Perform.on(inputRepository).select(
							new SelectSPARQLHelper(
									query, 
									new SimpleHTMLReportHandler(
											writer,
											builder.getSPARQL(),
											file.getName(),
											file.getName().replaceAll("-", " ").replaceAll("_", " ").replaceAll(".sparql", "")
											)
									)
					);
				} catch (Exception e) {
					log.error("Error in query in "+file.getAbsolutePath()+"...");
					e.printStackTrace();
					// passer a la suivante
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
			List<File> sparqls = FileUtil.listFilesRecursive(args.getQueryDirectoryOrFile());
			for (final File file : sparqls) {
				log.debug("Executing query in "+file.getAbsolutePath()+"...");
				SPARQLQueryBuilder builder = new SPARQLQueryBuilder(file);
				log.debug("Query is :\n"+builder.getSPARQL());
				
				SPARQLQuery query = new SPARQLQuery(builder);
				if(!bindings.isEmpty()) {
					query.setBindings(bindings);
				}
				
				// init writer
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDir, file.getName()+".csv"))));
				
				try {
					Perform.on(inputRepository).select(
							new SelectSPARQLHelper(
									query, 
									// new CSVHandler(writer)
									new CSVHandler(writer, false, false)
							)
					);
				} catch (Exception e) {
					log.error("Error in query in "+file.getAbsolutePath()+"...");
					e.printStackTrace();
					// passer a la suivante
				}
				
				// finalize writer
				writer.close();
			}
			
			break;
		}
		}
		
		// shutdown repos
		inputRepository.shutDown();

	}

}
