package fr.sparna.rdf.toolkit.split;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Prints a SKOS file in an HTML page")
public class ArgumentsSplit {

	@Parameter(
			names = "-i",
			description = "SKOS file, directory or config file to print",
			required = true
	)
	private String input;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
}
