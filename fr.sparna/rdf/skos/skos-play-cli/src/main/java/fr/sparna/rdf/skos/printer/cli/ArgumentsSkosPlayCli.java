package fr.sparna.rdf.skos.printer.cli;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class ArgumentsSkosPlayCli {

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
			names = { "-l", "--lang" },
			description = "Language code to use.",
			required = true
	)
	private String lang;
	
	public enum FORMAT {
		HTML,
		PDF
	}
	
	@Parameter(
			names = { "-f", "--format" },
			description = "Output format. Values can be either 'html' or 'pdf'. Defaults to html."
	)
	private  FORMAT format = FORMAT.HTML;
	
	@Parameter(
			names = { "-cs", "--scheme" },
			description = "URI of ConceptScheme to print"
	)
	private String conceptScheme;

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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public FORMAT getFormat() {
		return format;
	}

	public void setFormat(FORMAT format) {
		this.format = format;
	}

	public String getConceptScheme() {
		return conceptScheme;
	}

	public void setConceptScheme(String conceptScheme) {
		this.conceptScheme = conceptScheme;
	}
	
	
	
}
