package fr.sparna.rdf.skos.printer.cli.skos2skosxl;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.cli.ArgumentsSkosPlayCli;

@Parameters(commandDescription = "Converts a SKOS file to SKOS-XL file")
public class ArgumentsSkos2SkosXl {
	
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
			names = { "-b", "--bnodes" },
			description = "Use bnodes for generated xl:Label",
			required = false
	)
	private boolean useBnodes = false;
	
	@Parameter(
			names = { "-n", "--notes" },
			description = "Also reifies the notes, definitions, scopeNotes, etc.",
			required = false
	)
	private boolean includeNotes = false;

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

	public boolean isUseBnodes() {
		return useBnodes;
	}

	public void setUseBnodes(boolean useBnodes) {
		this.useBnodes = useBnodes;
	}

	public boolean isIncludeNotes() {
		return includeNotes;
	}

	public void setIncludeNotes(boolean includeNotes) {
		this.includeNotes = includeNotes;
	}
	
	
}
