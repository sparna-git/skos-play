package fr.sparna.rdf.skos.printer.cli;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;

import fr.sparna.rdf.skos.printer.cli.alignment.Alignment;
import fr.sparna.rdf.skos.printer.cli.alignment.ArgumentsAlignment;
import fr.sparna.rdf.skos.printer.cli.alphabetical.Alphabetical;
import fr.sparna.rdf.skos.printer.cli.alphabetical.ArgumentsAlphabetical;
import fr.sparna.rdf.skos.printer.cli.complete.ArgumentsComplete;
import fr.sparna.rdf.skos.printer.cli.complete.Complete;
import fr.sparna.rdf.skos.printer.cli.normalize.ArgumentsNormalizeLabels;
import fr.sparna.rdf.skos.printer.cli.normalize.NormalizeLabels;
import fr.sparna.rdf.skos.printer.cli.translation.ArgumentsTranslation;
import fr.sparna.rdf.skos.printer.cli.translation.Translation;


public class Main {

	enum COMMAND {		
		
		ALIGNMENT(new ArgumentsAlignment(), new Alignment()),
		ALPHABETICAL(new ArgumentsAlphabetical(), new Alphabetical()),
		COMPLETE(new ArgumentsComplete(), new Complete()),
		TRANSLATION(new ArgumentsTranslation(), new Translation()),
		NORMALIZE(new ArgumentsNormalizeLabels(), new NormalizeLabels()),
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
		// explicitely quiet FOP
		org.apache.log4j.Logger.getLogger("org.apache.fop").setLevel(org.apache.log4j.Level.INFO);
		
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
