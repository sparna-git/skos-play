package fr.sparna.rdf.toolkit.infer;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription = "Infer on input RDF using OWLIM")
public class ArgumentsInferBase {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true
	)
	// TODO : pouvoir passer une List<String> en utilisant variableArity = true  
	private String input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output RDF file to send result to",
			converter = FileConverter.class,
			required = true
	)
	private File output; 

	/**
	 * Default constructor
	 */
	public ArgumentsInferBase() {
		super();
	}
	
	/**
	 * Copy contructor
	 * 
	 * @param other
	 */
	public ArgumentsInferBase(ArgumentsInferBase other) {
		this.input = other.input;
		this.output = other.output;
	}

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
