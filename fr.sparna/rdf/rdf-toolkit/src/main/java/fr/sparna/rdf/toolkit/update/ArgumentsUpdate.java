package fr.sparna.rdf.toolkit.update;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription="Apply inference on RDF data using a set of SPARQL queries defined in a directory")
public class ArgumentsUpdate {

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
	
	@Parameter(
			names = { "-og", "--outputGraphs" },
			description = "Named graphs URI to output",
			variableArity = true
	)
	private List<String> outputGraphs;
	
	@Parameter(
			names = { "-u", "--updates" },
			description = "Directory containing SPARQL updates",
			converter = FileConverter.class,
			required = true
	)
	private File updateDirectory;

	public List<URI> getOutputGraphsURIs() throws URISyntaxException {
		if(outputGraphs == null) {
			return null;
		}
		
		List<URI> uris = new ArrayList<URI>();
		for (String aString : this.outputGraphs) {
			uris.add(new URI(aString));
		}
		return uris;
	}

	public File getUpdateDirectory() {
		return updateDirectory;
	}

	public void setUpdateDirectory(File updateDirectory) {
		this.updateDirectory = updateDirectory;
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

	public List<String> getOutputGraphs() {
		return outputGraphs;
	}

	public void setOutputGraphs(List<String> outputGraphs) {
		this.outputGraphs = outputGraphs;
	}

}
