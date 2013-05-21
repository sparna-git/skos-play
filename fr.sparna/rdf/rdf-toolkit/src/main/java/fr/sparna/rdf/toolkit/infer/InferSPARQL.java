package fr.sparna.rdf.toolkit.infer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.repository.Repository;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.sesame.toolkit.util.SimpleSPARQLInferenceEngine;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class InferSPARQL implements ToolkitCommandIfc {

	@Override
	public void execute(Object o) throws Exception {
		// TODO : configure logging
		ArgumentsInferSPARQL args = (ArgumentsInferSPARQL)o;
		
		// initialiser la connection aux données
		Repository inputRepository = new AutoDetectRepositoryFactory(args.getInput()).createNewRepository();

		// init rules
		List<SPARQLQueryIfc> helpers = new ArrayList<SPARQLQueryIfc>();
		List<File> sparqls = FileUtil.listFilesRecursive(args.getQueryDirectory());
		for (final File file : sparqls) {
			helpers.add(new SPARQLQuery(new SPARQLQueryBuilder(file)));
		}
		
		// init inference engine
		SimpleSPARQLInferenceEngine engine = new SimpleSPARQLInferenceEngine(
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
