package fr.sparna.rdf.solr.index.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class Parameters {

	@Parameter
	private List<String> parameters = new ArrayList<String>();

	@Parameter(
			names = { "--logLevel", "-l" },
			description = "Level of verbosity"
	)
	private String logLevel;

	@Parameter(
			names = { "--rdf", "-r" },
			description = "RDF data-source",
			required = true
	)
	private String rdf;

	@Parameter(
			names = { "--solr", "-s" },
			description = "Solr server",
			required = true
	)
	private String solr;
	
	@Parameter(
			names = { "--config", "-c" },
			description = "Indexing configuration",
			converter = FileConverter.class,
			required = true
	)
	private File config;

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getRdf() {
		return rdf;
	}

	public void setRdf(String rdf) {
		this.rdf = rdf;
	}

	public String getSolr() {
		return solr;
	}

	public void setSolr(String solr) {
		this.solr = solr;
	}

	public File getConfig() {
		return config;
	}

	public void setConfig(File config) {
		this.config = config;
	}
	
}
