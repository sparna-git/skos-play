package fr.sparna.rdf.toolkit;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;

import fr.sparna.cli.URIFactory;
import fr.sparna.rdf.toolkit.construct.ArgumentsConstruct;
import fr.sparna.rdf.toolkit.construct.Construct;
import fr.sparna.rdf.toolkit.infer.ArgumentsInfer;
import fr.sparna.rdf.toolkit.infer.ArgumentsInferBase;
import fr.sparna.rdf.toolkit.infer.ArgumentsInferSPARQL;
import fr.sparna.rdf.toolkit.infer.Infer;
import fr.sparna.rdf.toolkit.infer.InferOWL;
import fr.sparna.rdf.toolkit.infer.InferRDFS;
import fr.sparna.rdf.toolkit.infer.InferSPARQL;
import fr.sparna.rdf.toolkit.select.ArgumentsSelect;
import fr.sparna.rdf.toolkit.select.Select;
import fr.sparna.rdf.toolkit.server.ArgumentsLoadServer;
import fr.sparna.rdf.toolkit.server.LoadServer;
import fr.sparna.rdf.toolkit.skos.AddFlexions;
import fr.sparna.rdf.toolkit.skos.ArgumentsAddFlexions;
import fr.sparna.rdf.toolkit.skos.ArgumentsPrintSkosTree;
import fr.sparna.rdf.toolkit.skos.PrintSkosTree;
import fr.sparna.rdf.toolkit.solr.ArgumentsGenerateAutocompleteDictionary;
import fr.sparna.rdf.toolkit.solr.ArgumentsGenerateBroaderSynonyms;
import fr.sparna.rdf.toolkit.solr.ArgumentsGenerateLabelSynonyms;
import fr.sparna.rdf.toolkit.solr.GenerateAutocompleteDictionary;
import fr.sparna.rdf.toolkit.solr.GenerateBroaderSynonyms;
import fr.sparna.rdf.toolkit.solr.GenerateLabelSynonyms;
import fr.sparna.rdf.toolkit.split.ArgumentsSplit;
import fr.sparna.rdf.toolkit.split.Split;
import fr.sparna.rdf.toolkit.translate.ArgumentsTranslate;
import fr.sparna.rdf.toolkit.translate.Translate;
import fr.sparna.rdf.toolkit.update.ArgumentsUpdate;
import fr.sparna.rdf.toolkit.update.Update;
import fr.sparna.rdf.toolkit.xml.ArgumentsLoadXML;
import fr.sparna.rdf.toolkit.xml.LoadXML;

public class Main {
	
	enum COMMAND {		
		
		ADDFLEXIONS(new ArgumentsAddFlexions(), new AddFlexions()),
		CONSTRUCT(new ArgumentsConstruct(), new Construct()),
		GENERATEBROADERSYNONYMS(new ArgumentsGenerateBroaderSynonyms(), new GenerateBroaderSynonyms()),
		GENERATELABELSYNONYMS(new ArgumentsGenerateLabelSynonyms(), new GenerateLabelSynonyms()),
		GENERATEAUTOCOMPLETEDICTIONARY(new ArgumentsGenerateAutocompleteDictionary(), new GenerateAutocompleteDictionary()),
		INFER(new ArgumentsInfer(), new Infer()),
		INFEROWL(new ArgumentsInferBase(), new InferOWL()),
		INFERRDFS(new ArgumentsInferBase(), new InferRDFS()),
		INFERSPARQL(new ArgumentsInferSPARQL(), new InferSPARQL()),
		LOADSERVER(new ArgumentsLoadServer(), new LoadServer()),
		LOADXML(new ArgumentsLoadXML(), new LoadXML()),
		PRINTSKOSTREE(new ArgumentsPrintSkosTree(), new PrintSkosTree()),
		SELECT(new ArgumentsSelect(), new Select()),
		SPLIT(new ArgumentsSplit(), new Split()),
		TRANSLATE(new ArgumentsTranslate(), new Translate()),
		UPDATE(new ArgumentsUpdate(), new Update()),	
		;
		
		private ToolkitCommandIfc command;
		private Object arguments;

		private COMMAND(Object arguments, ToolkitCommandIfc command) {
			this.command = command;
			this.arguments = arguments;
		}

		public ToolkitCommandIfc getCommand() {
			return command;
		}

		public Object getArguments() {
			return arguments;
		}		
	}
	
	private void run(String[] args) throws Exception {
		ArgumentsMain main = new ArgumentsMain();
		JCommander jc = new JCommander(main);
		jc.addConverterFactory(new URIFactory());
		
		for (COMMAND aCOMMAND : COMMAND.values()) {
			jc.addCommand(aCOMMAND.name().toLowerCase(), aCOMMAND.getArguments());
		}
		
		try {
			jc.parse(args);
		// a mettre avant ParameterException car c'est une sous-exception
		} catch (MissingCommandException e) {
			// if no command was found, exit with usage message and error code
			System.err.println("Unkwown command.");
			jc.usage();
			System.exit(-1);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			jc.usage(jc.getParsedCommand());
			System.exit(-1);
		} 
		
		// if help was requested, print it and exit with a normal code
		if(main.isHelp()) {
			jc.usage();
			System.exit(0);
		}
		
		// if no command was found (0 parameters passed in command line)
		// exit with usage message and error code
		if(jc.getParsedCommand() == null) {
			System.err.println("No command found.");
			jc.usage();
			System.exit(-1);
		}
		
		// configure logging using log4j
		if(main.getLog() != null) {
			if(main.getLog().getName().endsWith(".xml")) {
				DOMConfigurator.configure(main.getLog().getAbsolutePath());
			} else {
				PropertyConfigurator.configure(main.getLog().getAbsolutePath());
			}
		}
		
		// executes the command with the associated arguments
		COMMAND.valueOf(jc.getParsedCommand().toUpperCase()).getCommand().execute(
				COMMAND.valueOf(jc.getParsedCommand().toUpperCase()).getArguments()
		);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Main me = new Main();
		me.run(args);
	}
}
