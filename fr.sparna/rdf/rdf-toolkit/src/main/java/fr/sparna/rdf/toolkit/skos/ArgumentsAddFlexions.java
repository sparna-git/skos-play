package fr.sparna.rdf.toolkit.skos;

import java.io.File;
import java.net.URI;
import java.util.List;

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
	
	@Parameter(
			names = { "-e", "--exclude" },
			description = "List of concept schemes to exclude"
	)
	// voir URIConverter
	private List<URI> conceptSchemesToExclude;

	public String getInput() {
		return input;
	}

	public File getOutput() {
		return output;
	}

	public List<URI> getConceptSchemesToExclude() {
		return conceptSchemesToExclude;
	}
	
}
