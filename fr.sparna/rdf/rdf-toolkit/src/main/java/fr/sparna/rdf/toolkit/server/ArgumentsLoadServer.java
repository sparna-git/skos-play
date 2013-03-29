package fr.sparna.rdf.toolkit.server;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Loads Sesame server with RDF data from file(s)")
public class ArgumentsLoadServer {

	@Parameter(names = "-i", description = "Input RDF file or directory", required = true)
	private String input;
	
	@Parameter(names = "-s", description = "Server URL e.g. http://localhost:8080/repositories/test", required = true)
	private String server;
	
	@Parameter(names = "-c", description = "Clears repository before loading")
	private boolean clearBeforeLoading = false;

	@Parameter(names = "-ng", description = "Loads every file in its own auto-computed named graph")
	private boolean namedGraphAware = false;
	
	@Parameter(names = "-g", description = "Graph URI to load the data into")
	private String graph = null;

	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public boolean isClearBeforeLoading() {
		return clearBeforeLoading;
	}

	public void setClearBeforeLoading(boolean clearBeforeLoading) {
		this.clearBeforeLoading = clearBeforeLoading;
	}

	public boolean isNamedGraphAware() {
		return namedGraphAware;
	}

	public void setNamedGraphAware(boolean namedGraphAware) {
		this.namedGraphAware = namedGraphAware;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

}
