package fr.sparna.rdf.skos.printer.cli.normalize;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.DisplayPrinter;

public class ArgumentsNormalizeLabels {

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
			names = { "-cs", "--scheme" },
			description = "URI of ConceptScheme to normalize"
	)
	private String conceptScheme;
	
	@Parameter(
			names = { "-l", "--lang" },
			description = "Language code to use.",
			required = true
	)
	private String lang;

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

	public String getConceptScheme() {
		return conceptScheme;
	}

	public void setConceptScheme(String conceptScheme) {
		this.conceptScheme = conceptScheme;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
}
