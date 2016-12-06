package fr.sparna.rdf.skos.xls2skos.cli;

import java.io.File;
import java.util.List;

import org.eclipse.rdf4j.rio.RDFFormat;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;


public class ArgumentsConvert {

	@Parameter(
			names = { "-i", "--input" },
			description = "Input Excel file",
			converter = FileConverter.class,
			required = true
	)
	private File input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output RDF or ZIP file name",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-d", "--directory" },
			description = "Consider output like a directory, and generate separate files in it for each graph."
	)
	private boolean outputAsDirectory = false;
	
	@Parameter(
			names = { "-l", "--lang" },
			description = "Default language to use.",
			required = true
	)
	private String lang;
	
	@Parameter(
			names = { "-f", "--format" },
			description = "Output RDF format mime type."
	)
	private String rdfFormat = null;
	
	@Parameter(
			names = { "-xl", "--skosxl" },
			description = "XLify labels"
	)
	private boolean xlify = false;

	@Parameter(
			names = { "-def", "--definitions" },
			description = "XLify definitions"
	)
	private boolean xlifyDefinitions = false;

	@Parameter(
			names = { "-g", "--generateGraphs" },
			description = "Generate Virtuoso graph files"
	)
	private boolean generateGraphFiles = false;

	public File getInput() {
		return input;
	}

	public void setInput(File input) {
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

	public String getRdfFormat() {
		return rdfFormat;
	}

	public void setRdfFormat(String rdfFormat) {
		this.rdfFormat = rdfFormat;
	}

	public boolean isXlify() {
		return xlify;
	}

	public void setXlify(boolean xlify) {
		this.xlify = xlify;
	}

	public boolean isXlifyDefinitions() {
		return xlifyDefinitions;
	}

	public void setXlifyDefinitions(boolean xlifyDefinitions) {
		this.xlifyDefinitions = xlifyDefinitions;
	}

	public boolean isGenerateGraphFiles() {
		return generateGraphFiles;
	}

	public void setGenerateGraphFiles(boolean generateGraphFiles) {
		this.generateGraphFiles = generateGraphFiles;
	}

	public boolean isOutputAsDirectory() {
		return outputAsDirectory;
	}

	public void setOutputAsDirectory(boolean outputAsDirectory) {
		this.outputAsDirectory = outputAsDirectory;
	}
	
}
