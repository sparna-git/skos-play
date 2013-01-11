package fr.sparna.rdf.toolkit.skos;

import java.io.PrintStream;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.skos.SimpleSKOSTreeCreator;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class PrintSkosTree implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public void execute(Object o) throws Exception {
		// TODO : configure logging

		ArgumentsPrintSkosTree args = (ArgumentsPrintSkosTree)o;
		
		// lire le RDF d'input
		StringRepositoryFactory factory = new StringRepositoryFactory(args.getInput());
		Repository r = factory.createNewRepository();

		// build creator - language may be null
		SimpleSKOSTreeCreator treeCreator = new SimpleSKOSTreeCreator(r, args.getLanguage());

		// output result
		PrintStream s;
		if(args.getOutput() == null) {
			s = System.out;
		} else {
			if(!args.getOutput().exists()) {
				args.getOutput().createNewFile();
			}
			s = new PrintStream(args.getOutput(), "UTF-8");
		}
		s.println(treeCreator.getTree());
	}
}
