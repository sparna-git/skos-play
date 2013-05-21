package fr.sparna.rdf.toolkit.update;

import java.net.URISyntaxException;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLUpdate;
import fr.sparna.rdf.sesame.toolkit.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class Update implements ToolkitCommandIfc {

	@Override
	public void execute(Object o) throws Exception {
		// TODO : configure logging
		ArgumentsUpdate args = (ArgumentsUpdate)o;
		
		// initialiser la connection aux données
		Repository r = new AutoDetectRepositoryFactory(args.getInput()).createNewRepository();

		// init updates
		ApplyUpdates u = new ApplyUpdates(SPARQLUpdate.fromUpdateDirectory(args.getUpdateDirectory()));
		
		// execute updates
		u.execute(r);
		
		// output updated repository
		if(args.getOutputGraphs() != null && args.getOutputGraphs().size() > 0) {
			try {
				RepositoryWriter.writeToFile(args.getOutput().getAbsolutePath(), r, args.getOutputGraphsURIs());
			} catch (URISyntaxException e) {
				throw e;
			}
		} else {
			RepositoryWriter.writeToFile(args.getOutput(), r);
		}
		
	}

}
