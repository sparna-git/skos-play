package fr.sparna.rdf.toolkit;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class ArgumentsMain {
	
	@Parameter(
			names = { "-h", "--help" },
			description = "Prints the help message",
			help = true
	)
	private boolean help = false;
	
	@Parameter(
			names = { "-l", "--log" },
			description = "Reference to a log4j configuration file",
			converter = FileConverter.class
	)
	private File log;

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public File getLog() {
		return log;
	}

	public void setLog(File log) {
		this.log = log;
	}
	
}
