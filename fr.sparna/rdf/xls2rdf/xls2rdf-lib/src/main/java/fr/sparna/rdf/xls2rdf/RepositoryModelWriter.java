package fr.sparna.rdf.xls2rdf;

import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * @author Thomas Francart
 *
 */
public class RepositoryModelWriter implements ModelWriterIfc {
	
	private Repository outputRepository;
	
	public RepositoryModelWriter(Repository outputRepository) {
		super();
		this.outputRepository = outputRepository;
	}

	@Override
	public void saveGraphModel(String graph, Model model, Map<String, String> prefixes) {
		try {
			try(RepositoryConnection c = this.outputRepository.getConnection()) {
				// register the prefixes
				prefixes.entrySet().forEach(e -> c.setNamespace(e.getKey(), e.getValue()));
				c.add(model, SimpleValueFactory.getInstance().createIRI(graph));
			}
		} catch(Exception e) {
			throw Xls2SkosException.rethrow(e);
		}
	}
	
	@Override
	public void beginWorkbook() {
		// nothing
	}

	@Override
	public void endWorkbook() {
		// nothing
	}

}
