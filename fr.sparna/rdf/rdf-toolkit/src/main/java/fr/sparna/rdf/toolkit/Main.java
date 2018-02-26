package fr.sparna.rdf.toolkit;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;

import fr.sparna.cli.URIFactory;
import fr.sparna.rdf.toolkit.construct.ArgumentsConstruct;
import fr.sparna.rdf.toolkit.construct.Construct;
import fr.sparna.rdf.toolkit.select.ArgumentsSelect;
import fr.sparna.rdf.toolkit.select.Select;
import fr.sparna.rdf.toolkit.serialize.ArgumentsSerialize;
import fr.sparna.rdf.toolkit.serialize.Serialize;
import fr.sparna.rdf.toolkit.update.ArgumentsUpdate;
import fr.sparna.rdf.toolkit.update.Update;

public class Main {
	
	enum COMMAND {		
		
		CONSTRUCT(new ArgumentsConstruct(), new Construct()),
		SELECT(new ArgumentsSelect(), new Select()),
		SERIALIZE(new ArgumentsSerialize(), new Serialize()),
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
