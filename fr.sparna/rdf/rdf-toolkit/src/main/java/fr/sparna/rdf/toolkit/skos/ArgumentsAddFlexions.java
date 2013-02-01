package fr.sparna.rdf.toolkit.skos;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription = "Adds flexions for SKOS labels in the RDF")
public class ArgumentsAddFlexions {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true
	) 
	private String input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output rdf file",
			converter = FileConverter.class,
			required = true
	) 
	private File output;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}
	
}
