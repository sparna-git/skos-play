package fr.sparna.rdf.toolkit.select;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.cli.SpaceSplitter;

@Parameters(commandDescription = "Applies a set of SELECT queries and generates an HTML page with the results")
public class ArgumentsSelect {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true,
			variableArity = true
	)
	private List<String> input;
	
	@Parameter(
			names = { "-q", "--queries" },
			description = "SPARQL query file, or directory containing SPARQL queries",
			converter = FileConverter.class,
			required = true
	)
	private File queryDirectoryOrFile; 
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output file or directory",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	public enum MODE {
		HTML,
		CSV
	}
	
	@Parameter(
			names = { "-m", "--mode" },
			description = "Output mode. Values can be either 'html' or 'csv'. Defaults to html"
	)
	private  MODE mode = MODE.HTML;
	
	@Parameter(
			names = { "-b", "--bindings" },
			description = "Bindings, in the form <key>,<value> e.g. uri,http://www.example.com/onto#Concept1",
			variableArity = true,
			splitter = SpaceSplitter.class
	)
	private List<String> bindingStrings;

	public Map<String, String> getBindings() {
		if(this.bindingStrings == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String aBindingString : this.bindingStrings) {
			if(!aBindingString.contains(",")) {
				throw new InvalidParameterException("Bindings should be in the form <key>,<value>, here received : '"+aBindingString+"'");
			}
			result.put(aBindingString.trim().split(",")[0],aBindingString.trim().split(",")[1]);
		}
		return result;
	}
	
	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

	public File getQueryDirectoryOrFile() {
		return queryDirectoryOrFile;
	}

	public void setQueryDirectoryOrFile(File queryDirectoryOrFile) {
		this.queryDirectoryOrFile = queryDirectoryOrFile;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public List<String> getBindingStrings() {
		return bindingStrings;
	}

	public void setBindingStrings(List<String> bindingStrings) {
		this.bindingStrings = bindingStrings;
	}

	public MODE getMode() {
		return mode;
	}

	public void setMode(MODE mode) {
		this.mode = mode;
	}	
	
}
