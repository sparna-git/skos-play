package fr.sparna.rdf.skos.printer.cli.skosxl2skos;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.cli.ArgumentsSkosPlayCli;

@Parameters(commandDescription = "Converts a SKOS XL file to plain SKOS")
public class ArgumentsSkosXl2Skos {
	
	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory or endpoint URL",
			required = true,
			variableArity = true
	)
	private List<String> input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output file",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-c", "--clean" },
			description = "Also cleans the SKOS-XL data from the file",
			required = false
	)
	private boolean cleanXl = false;

	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public boolean isCleanXl() {
		return cleanXl;
	}

	public void setCleanXl(boolean cleanXl) {
		this.cleanXl = cleanXl;
	}	
	
}
