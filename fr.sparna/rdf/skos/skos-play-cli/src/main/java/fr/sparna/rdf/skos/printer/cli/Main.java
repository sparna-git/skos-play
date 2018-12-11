package fr.sparna.rdf.skos.printer.cli;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;

import ch.qos.logback.classic.util.ContextInitializer;
import fr.sparna.rdf.skos.printer.cli.alignment.Alignment;
import fr.sparna.rdf.skos.printer.cli.alignment.ArgumentsAlignment;
import fr.sparna.rdf.skos.printer.cli.alphabetical.Alphabetical;
import fr.sparna.rdf.skos.printer.cli.alphabetical.ArgumentsAlphabetical;
import fr.sparna.rdf.skos.printer.cli.complete.ArgumentsComplete;
import fr.sparna.rdf.skos.printer.cli.complete.Complete;
import fr.sparna.rdf.skos.printer.cli.hierarchical.ArgumentsHierarchical;
import fr.sparna.rdf.skos.printer.cli.hierarchical.Hierarchical;
import fr.sparna.rdf.skos.printer.cli.index.ArgumentsIndex;
import fr.sparna.rdf.skos.printer.cli.index.Index;
import fr.sparna.rdf.skos.printer.cli.normalize.ArgumentsNormalizeLabels;
import fr.sparna.rdf.skos.printer.cli.normalize.NormalizeLabels;
import fr.sparna.rdf.skos.printer.cli.skos2skosxl.ArgumentsSkos2SkosXl;
import fr.sparna.rdf.skos.printer.cli.skos2skosxl.Skos2SkosXl;
import fr.sparna.rdf.skos.printer.cli.skosxl2skos.ArgumentsSkosXl2Skos;
import fr.sparna.rdf.skos.printer.cli.skosxl2skos.SkosXl2Skos;
import fr.sparna.rdf.skos.printer.cli.translation.ArgumentsTranslation;
import fr.sparna.rdf.skos.printer.cli.translation.Translation;


public class Main {

	enum COMMAND {		
		
		ALIGNMENT(new ArgumentsAlignment(), new Alignment()),
		ALPHABETICAL(new ArgumentsAlphabetical(), new Alphabetical()),
		HIERARCHICAL(new ArgumentsHierarchical(), new Hierarchical()),
		COMPLETE(new ArgumentsComplete(), new Complete()),
		TRANSLATION(new ArgumentsTranslation(), new Translation()),
		NORMALIZE(new ArgumentsNormalizeLabels(), new NormalizeLabels()),
		INDEX(new ArgumentsIndex(), new Index()),
		SKOS2SKOSXL(new ArgumentsSkos2SkosXl(), new Skos2SkosXl()),
		SKOSXL2SKOS(new ArgumentsSkosXl2Skos(), new SkosXl2Skos()),
		;
		
		private SkosPlayCliCommandIfc command;
		private Object arguments;

		private COMMAND(Object arguments, SkosPlayCliCommandIfc command) {
			this.command = command;
			this.arguments = arguments;
		}

		public SkosPlayCliCommandIfc getCommand() {
			return command;
		}

		public Object getArguments() {
			return arguments;
		}		
	}
	
	
	private void run(String[] args) throws Exception {
		ArgumentsMain main = new ArgumentsMain();
		JCommander jc = new JCommander(main);
		
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
		
		// configure logging
		if(main.getLog() != null) {
			System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, main.getLog().getAbsolutePath());

		}
		// explicitely quiet FOP
		((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("org.apache.fop")).setLevel(ch.qos.logback.classic.Level.INFO);		
		
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
		
		// executes the command with the associated arguments
		COMMAND.valueOf(jc.getParsedCommand().toUpperCase()).getCommand().execute(
				COMMAND.valueOf(jc.getParsedCommand().toUpperCase()).getArguments()
		);
	}
	
	public static void main(String[] args) throws Exception {
		Main me = new Main();
		me.run(args);
	}

}
