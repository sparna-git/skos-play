package fr.sparna.rdf.toolkit.skos;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription = "Prints a SKOS Tree in a text format")
public class ArgumentsPrintSkosTree {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true
	) 
	private String input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output text file. Standard output is used if not set",
			converter = FileConverter.class
	) 
	private File output;
	
	@Parameter(
			names = { "-l", "--language" },
			description = "2-letter ISO code of the language to use to display concept labels"
	) 
	private String language;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}
	
}
