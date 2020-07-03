package fr.sparna.rdf.skos.printer.cli.reconcile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.algebra.evaluation.function.string.Substring;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.skos.printer.cli.SkosPlayCliCommandIfc;
import fr.sparna.rdf.skos.toolkit.GetConceptsWithLabelHelper;

public class BatchReconcile implements SkosPlayCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsBatchReconcile args = (ArgumentsBatchReconcile)o;

		log.debug("Batch reconciliation against "+args.getInput()+"...");
		
		// lire le RDF d'input		
		Repository inputRepository = RepositoryBuilderFactory.fromStringList(args.getInput()).get();

		if(!args.getOutput().exists()) {
			args.getOutput().createNewFile();
		}
		
		FileOutputStream out = new FileOutputStream(args.getOutput());
		
		try(CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT)) {
			try(RepositoryConnection connection = inputRepository.getConnection()) {
				
				try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args.getLabels()), args.getCharset()))) {
				    for(String line; (line = br.readLine()) != null; ) {
				    	
				    	if(!line.equals("")) {
					    	// remove " from begin and end
					    	String value = (line.endsWith("\"") && line.startsWith("\""))?line.substring(1, line.length()-1):line;
					    	
					    	csvPrinter.print(value);
							Perform.on(connection).select(new GetConceptsWithLabelHelper(value, args.getLang(), null) {						
								@Override
								protected void handleConcept(Resource concept, Literal prefLabel) throws TupleQueryResultHandlerException {
									try {
										csvPrinter.print(concept.stringValue());
										csvPrinter.print(prefLabel.stringValue());
									} catch (IOException e) {
										throw new TupleQueryResultHandlerException(e);
									}
								}
							});							
				    	}
				    	csvPrinter.println();
				    }
				    // line is not visible here.
				}
	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// shutdown repos
		inputRepository.shutDown();
		
		log.debug("Done batch reconciliation");
	}

}
