package fr.sparna.rdf.skos.printer.cli;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.DisplayPrinter;

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
	
	@Parameter(
			names = { "-f", "--format" },
			description = "Output format. Values can be either 'html' or 'pdf'. Defaults to pdf."
	)
	private DisplayPrinter.Format format = DisplayPrinter.Format.PDF;
	
	@Parameter(
			names = { "-s", "--style" },
			description = "Output style. Values can be either 'default' or 'unesco'. Defaults to 'default'."
	)
	private  DisplayPrinter.Style style = DisplayPrinter.Style.DEFAULT;
	
	@Parameter(
			names = { "-cs", "--scheme" },
			description = "URI of ConceptScheme to print"
	)
	private String conceptScheme;
	
	@Parameter(
			names = { "-fop", "--fopConfig" },
			description = "FOP config file",
			converter = FileConverter.class
	)
	private File fopConfig;

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

	public DisplayPrinter.Format getFormat() {
		return format;
	}

	public void setFormat(DisplayPrinter.Format format) {
		this.format = format;
	}

	public String getConceptScheme() {
		return conceptScheme;
	}

	public void setConceptScheme(String conceptScheme) {
		this.conceptScheme = conceptScheme;
	}

	public File getFopConfig() {
		return fopConfig;
	}

	public void setFopConfig(File fopConfig) {
		this.fopConfig = fopConfig;
	}
	
	public String getFopConfigPath() {
		return (getFopConfig() != null)?getFopConfig().getAbsolutePath():null;
	}

	public DisplayPrinter.Style getStyle() {
		return style;
	}

	public void setStyle(DisplayPrinter.Style style) {
		this.style = style;
	}
	
}
