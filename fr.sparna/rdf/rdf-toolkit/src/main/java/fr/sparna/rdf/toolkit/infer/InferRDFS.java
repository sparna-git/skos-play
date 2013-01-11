package fr.sparna.rdf.toolkit.infer;

import fr.sparna.rdf.sesame.toolkit.repository.OWLIMConfigProvider;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

/**
 * A utility main class to apply RDFS inference using OWLIM on an RDF input.
 * 
 * @author Thomas Francart
 */
public class InferRDFS implements ToolkitCommandIfc {
	
	@Override
	public void execute(Object o) throws Exception {
		new Infer().execute(
				new ArgumentsInfer(
						(ArgumentsInferBase)o,
						OWLIMConfigProvider.RDFS_CONFIG_PROVIDER.getRuleset()
				)
		);
	}
	
}
