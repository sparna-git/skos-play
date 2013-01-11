package fr.sparna.rdf.toolkit.translate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Pretty prints, merge, or translate (e.g. RDF/XML to N3) RDF")
public class ArgumentsTranslate {

	@Parameter(names = "-i", description = "Input RDF file or directory", required = true)
	private String input;

	@Parameter(names = "-o", description = "Output RDF file", required = true)
	private String output;
	
	@Parameter(
			names = "-ns",
			description = "Namespace prefixes, in the form <key>,<uri> e.g. skos,http://www.w3.org/2004/02/skos/core#",
			variableArity = true
	)
	private List<String> namespaceMappingsStrings;
	
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
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
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

}
