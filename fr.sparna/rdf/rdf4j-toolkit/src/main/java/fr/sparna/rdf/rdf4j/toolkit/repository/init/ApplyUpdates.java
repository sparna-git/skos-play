package fr.sparna.rdf.rdf4j.toolkit.repository.init;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.rdf4j.toolkit.query.DatasetFactory;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReader;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.SparqlOperationIfc;


public class ApplyUpdates extends AbstractLoadOperation implements Consumer<RepositoryConnection> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<SparqlOperationIfc> updates = new ArrayList<SparqlOperationIfc>();
	
	public ApplyUpdates(List<SparqlOperationIfc> updates) {
		super();
		this.updates = updates;
	}
	
	public static ApplyUpdates fromStrings(List<String> updates) {
		return new ApplyUpdates(updates.stream().map(s -> new SimpleSparqlOperation(s)).collect(Collectors.toList()));
	}
	
	public static ApplyUpdates fromQueryReaders(List<SimpleQueryReader> readers) {
		return new ApplyUpdates(readers.stream().map(r -> new SimpleSparqlOperation(r.get())).collect(Collectors.toList()));
	}
	
	@Override
	public void accept(RepositoryConnection connection) {
		if(this.updates != null) {
			Perform p = Perform.on(connection);

			// set targetGraph
			if(this.targetGraph != null) {
				p.setDataset(DatasetFactory.fromWorkingGraph(this.targetGraph));
			}
			
			// apply updates
			for (SparqlOperationIfc anUpdate : this.updates) {
				log.debug("Applying update : "+"\n"+anUpdate.toString());
				p.update(anUpdate);
			}
		}
	}

	public List<SparqlOperationIfc> getUpdates() {
		return updates;
	}

	public void setUpdates(List<SparqlOperationIfc> updates) {
		this.updates = updates;
	}

}
