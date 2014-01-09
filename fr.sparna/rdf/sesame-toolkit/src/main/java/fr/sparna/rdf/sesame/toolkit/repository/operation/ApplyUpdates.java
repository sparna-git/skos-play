package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderList;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

public class ApplyUpdates extends AbstractLoadOperation implements RepositoryOperationIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<SparqlUpdate> updates = new ArrayList<SparqlUpdate>();
	
	public ApplyUpdates(List<SparqlUpdate> updates) {
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
			for (SparqlUpdate anUpdate : this.updates) {
				try {
					log.debug("Applying update : "+"\n"+anUpdate.toString());
					p.update(anUpdate);
				} catch (SparqlPerformException e) {
					throw new RepositoryOperationException(e);
				}
			}
		}
	}

	public List<SparqlUpdate> getUpdates() {
		return updates;
	}

	public void setUpdates(List<SparqlUpdate> updates) {
		this.updates = updates;
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromString("/home/thomas/workspace/01-Projets/JobTransport/Referentiel/jt-onto-v2.2.ttl");
		ApplyUpdates o = new ApplyUpdates(SparqlUpdate.fromUpdateList(SparqlQueryBuilderList.fromClasspathDirectory("rules/test")));
		o.execute(r);
	}

}
