package fr.sparna.rdf.toolkit.solr;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class ArgumentsGenerateAutocompleteDictionary {

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
	
	@Parameter(
			names = { "-plw", "--prefLabelsWeight" },
			description = "Weight to assign to the pref labels in the generated word dictionary"
	)
	private float prefLabelsWeight = 2.0f;
	
	@Parameter(
			names = { "-np", "--noPrefLabels" },
			description = "Exclude skos:prefLabel predicates for synonym file generation"
	)
	private boolean noPrefs = false;
	
	@Parameter(
			names = { "-na", "--noAltLabels" },
			description = "Exclude skos:altLabel predicates for synonym file generation"
	)
	private boolean noAlts = false;
	
	@Parameter(
			names = { "-nh", "--noHiddenLabels" },
			description = "Exclude skos:hiddenLabel predicates for synonym file generation"
	)
	private boolean noHiddens = false;

	public String getInput() {
		return input;
	}

	public File getOutput() {
		return output;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public float getPrefLabelsWeight() {
		return prefLabelsWeight;
	}	

	public boolean isNoPrefs() {
		return noPrefs;
	}

	public boolean isNoAlts() {
		return noAlts;
	}

	public boolean isNoHiddens() {
		return noHiddens;
	}
	
}
