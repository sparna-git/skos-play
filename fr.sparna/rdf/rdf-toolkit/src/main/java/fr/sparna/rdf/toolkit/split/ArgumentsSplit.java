package fr.sparna.rdf.toolkit.split;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Prints a SKOS file in an HTML page")
public class ArgumentsSplit {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true,
			variableArity = true
	) 
	private List<String> input;

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

}
