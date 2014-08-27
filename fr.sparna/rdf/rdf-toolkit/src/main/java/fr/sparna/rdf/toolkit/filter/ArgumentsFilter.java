package fr.sparna.rdf.toolkit.filter;

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
public class ArgumentsFilter {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files or directory",
			required = true,
			converter = FileConverter.class,
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output file or directory",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-in", "--includes" },
			description = "Predicates URI to include",
			variableArity = true,
			splitter = SpaceSplitter.class
	)
	private List<String> includes;
	
	@Parameter(
			names = { "-ex", "--excludes" },
			description = "Predicates URI to exclude",
			variableArity = true,
			splitter = SpaceSplitter.class
	)
	private List<String> excludes;

	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}
	
	
}
