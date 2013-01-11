package fr.sparna.rdf.toolkit.xml;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

@Parameters(commandDescription = "Apply a XSL on a set of XML files to produce RDF")
public class ArgumentsLoadXML {

	@Parameter(
			names = "-i",
			description = "Input XML file or directory",
			required = true
	)
	private String input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Output RDF file",
			converter = FileConverter.class,
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-x", "--xsl" },
			description = "XSL Stylesheet to use",
			converter = FileConverter.class,
			required = true
	)
	private File xsl;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public File getXsl() {
		return xsl;
	}

	public void setXsl(File xsl) {
		this.xsl = xsl;
	}
	
}
