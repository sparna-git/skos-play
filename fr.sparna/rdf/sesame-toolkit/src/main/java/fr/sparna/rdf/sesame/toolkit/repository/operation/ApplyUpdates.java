package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLUpdate;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderList;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

public class ApplyUpdates extends AbstractLoadOperation implements RepositoryOperationIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<SPARQLUpdate> updates = new ArrayList<SPARQLUpdate>();
	
	public ApplyUpdates(List<SPARQLUpdate> updates) {
		super();
		this.updates = updates;
	}
	
	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		if(this.updates != null) {
			Perform p = Perform.on(repository);

			// set targetGraph
			if(this.targetGraph != null) {
				// TODO : not necessarily the workingGraph but only defaultInsertGraph
				// and defaultDeleteGraph
				p.setWorkingGraph(this.targetGraph);
			}
			
			// apply updates
			for (SPARQLUpdate anUpdate : this.updates) {
				try {
					log.debug("Applying update : "+"\n"+anUpdate.toString());
					p.update(anUpdate);
				} catch (SPARQLExecutionException e) {
					throw new RepositoryOperationException(e);
				}
			}
		}
	}

	public List<SPARQLUpdate> getUpdates() {
		return updates;
	}

	public void setUpdates(List<SPARQLUpdate> updates) {
		this.updates = updates;
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromString("/home/thomas/workspace/01-Projets/JobTransport/Referentiel/jt-onto-v2.2.ttl");
		ApplyUpdates o = new ApplyUpdates(SPARQLUpdate.fromUpdateList(SPARQLQueryBuilderList.fromClasspathDirectory("rules/test")));
		o.execute(r);
	}

}
