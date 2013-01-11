package fr.sparna.rdf.toolkit.solr;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class ArgumentsGenerateBroaderSynonyms {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true
	)  
	private String input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output synonyms file to generate",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-l", "--languages" },
			description = "2-letter ISO codes of the languages to use (defaults to all languages)",
			variableArity = true
	)
	private List<String> languages;

	public String getInput() {
		return input;
	}

	public File getOutput() {
		return output;
	}

	public List<String> getLanguages() {
		return languages;
	}

}
