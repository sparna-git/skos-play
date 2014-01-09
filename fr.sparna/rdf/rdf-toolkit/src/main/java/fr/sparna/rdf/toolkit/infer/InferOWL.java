package fr.sparna.rdf.toolkit.infer;

import fr.sparna.rdf.sesame.toolkit.repository.OwlimConfigProvider;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

/**
 * A utility main class to apply OWL-reduced inference using OWLIM on an RDF input.
 * 
 * @author Thomas Francart
 */
public class InferOWL implements ToolkitCommandIfc {
	
	@Override
	public void execute(Object o) throws Exception {
		new Infer().execute(
				new ArgumentsInfer(
						(ArgumentsInferBase)o,
						OwlimConfigProvider.OWL_REDUCED_CONFIG_PROVIDER.getRuleset()
				)
		);
	}
	
}
