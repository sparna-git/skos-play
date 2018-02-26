package fr.sparna.rdf.toolkit.serialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.sparna.cli.SpaceSplitter;

@Parameters(commandDescription = "Pretty prints, merge, or translate (e.g. RDF/XML to N3) RDF")
public class ArgumentsSerialize {

	@Parameter(
			names = { "-i", "--input" },
			description = "RDF files, directory, endpoint URL, or Spring config",
			required = true,
			variableArity = true
	) 
	private List<String> input;

	@Parameter(names = "-o", description = "Output RDF file", required = true)
	private String output;
	
	@Parameter(
			names = "-ns",
			description = "Namespace prefixes, in the form <key1>,<ns1> <key2>,<ns2> e.g. skos,http://www.w3.org/2004/02/skos/core# dct,http://purl.org/dc/terms/",
			variableArity = true,
			splitter = SpaceSplitter.class
	)
	private List<String> namespaceMappingsStrings;
	
	@Parameter(
			names = { "-no", "--no-order" },
			description = "Don't sort triples when outputting result file",
			required = false
	) 
	private boolean noOrder = false;
	
	public Map<String, String> getNamespaceMappings() {
		if(this.namespaceMappingsStrings == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String aMappingString : this.namespaceMappingsStrings) {
			result.put(aMappingString.split(",")[0],aMappingString.split(",")[1]);
		}
		return result;
	}
	
	public List<String> getInput() {
		return input;
	}

	public void setInput(List<String> input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public List<String> getNamespaceMappingsStrings() {
		return namespaceMappingsStrings;
	}

	public void setNamespaceMappingsStrings(List<String> namespaceMappingsStrings) {
		this.namespaceMappingsStrings = namespaceMappingsStrings;
	}

	public boolean isNoOrder() {
		return noOrder;
	}

	public void setNoOrder(boolean noOrder) {
		this.noOrder = noOrder;
	}

}
