package fr.sparna.rdf.skos.printer.cli.reconcile;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.DisplayPrinter;

public class ArgumentsBatchReconcile {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory or endpoint URL",
			required = true,
			variableArity = true
	)
	private List<String> input;
	
	@Parameter(
			names = { "-lb", "--labels" },
			description = "Labels file, one label per line",
			converter = FileConverter.class,
			required = true
	)
	private File labels;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output file",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-l", "--lang" },
			description = "Language to use to search",
			required = false
	)
	private String lang;

	@Parameter(
			names = { "-c", "--charset" },
			description = "Charset to use to read label file. Defaults to UTF-8",
			required = false
	)
	private String charset="UTF-8";
	
	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}
	
	public File getLabels() {
		return labels;
	}

	public void setLabels(File labels) {
		this.labels = labels;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public String getCharset() {
		return charset;
	}
	
}
