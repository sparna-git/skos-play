package fr.sparna.rdf.toolkit.infer;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Infer using a given ruleset on input RDF with OWLIM")
public class ArgumentsInfer extends ArgumentsInferBase {
	
	@Parameter(
			names = { "-r", "--rules" },
			description = "Ruleset definition file",
			required = true
	)
	private String ruleset; 
	
	public ArgumentsInfer() {
		super();
	}
	
	public ArgumentsInfer(ArgumentsInferBase other, String ruleset) {
		super(other);
		this.ruleset = ruleset;
	}

	public String getRuleset() {
		return ruleset;
	}

	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
}
