package fr.sparna.rdf.toolkit.infer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.repository.Repository;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQueryIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.sesame.toolkit.util.SimpleSparqlInferenceEngine;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class InferSPARQL implements ToolkitCommandIfc {

	@Override
	public void execute(Object o) throws Exception {
		// TODO : configure logging
		ArgumentsInferSPARQL args = (ArgumentsInferSPARQL)o;
		
		// initialiser la connection aux données
		Repository inputRepository = new AutoDetectRepositoryFactory(args.getInput()).createNewRepository();

		// init rules
		List<SparqlQueryIfc> helpers = new ArrayList<SparqlQueryIfc>();
		List<File> sparqls = FileUtil.listFilesRecursive(args.getQueryDirectory());
		for (final File file : sparqls) {
			helpers.add(new SparqlQuery(new SparqlQueryBuilder(file)));
		}
		
		// init inference engine
		SimpleSparqlInferenceEngine engine = new SimpleSparqlInferenceEngine(
				inputRepository,
				helpers
		);
		if(args.getMaxIterations() > 0) {
			engine.setMaxIterationCount(args.getMaxIterations());
		}
		
		// run engine
		engine.run();
		
		// output saturated repository
		RepositoryWriter.writeToFile(args.getOutput(), inputRepository);
	}

}
