package fr.sparna.rdf.skos.printer.cli.index;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.sparna.rdf.skos.printer.cli.ArgumentsSkosPlayCli;
import fr.sparna.rdf.skos.printer.reader.IndexGenerator;

@Parameters(commandDescription = "Generates a index report.")
public class ArgumentsIndex extends ArgumentsSkosPlayCli {
	
	@Parameter(
			names = { "-t", "--type" },
			description = "Index style. Values can be either 'kwic' or 'kwoc' or 'kwac'."
	)
	private  IndexGenerator.IndexType indexType = IndexGenerator.IndexType.KWIC;

	public IndexGenerator.IndexType getIndexType() {
		return indexType;
	}
	
	
	
}
