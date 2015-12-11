package fr.sparna.rdf.skos.printer.cli.alphabetical;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.sparna.rdf.skos.printer.cli.ArgumentsSkosPlayCli;

@Parameters(commandDescription = "Generates a complete report.")
public class ArgumentsAlphabetical extends ArgumentsSkosPlayCli {
	
	@Parameter(
			names = { "-m", "--multilingual" },
			description = "Also includes translations in other languages and translation tables. Defaults to false."
	)
	private boolean multilingual = false;

	public boolean isMultilingual() {
		return multilingual;
	}

	public void setMultilingual(boolean multilingual) {
		this.multilingual = multilingual;
	}
	
}
