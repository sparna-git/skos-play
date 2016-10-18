package fr.sparna.rdf.datapress;

import java.util.Date;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class RepositoryManagementListener implements DataPressListener {

	private Repository repository;
	
	public RepositoryManagementListener(Repository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public void begin(DataPressSource source) {
		// clean target graph
     	repository.getConnection().clear(source.getDocumentIri());
	}

	@Override
	public void end(DataPressSource source, boolean success) {
		
		IRI graphIri = new DataPressHandlerFactory().getTargetGraphIri(source.getDocumentIri());
		
		// Add a dcterms:modified triple to capture the date of storage
		RepositoryConnection c = repository.getConnection();
		c.remove(
				graphIri,
				DCTERMS.MODIFIED,
				null,
				(Resource)null
		);
		c.add(
				graphIri,
				DCTERMS.MODIFIED,
				SimpleValueFactory.getInstance().createLiteral(new Date(System.currentTimeMillis())),
				(Resource)null
		);
		
		// Add a dcterms:isPartOf to link to the domain name that we compute automatically
		String sourceString = source.getDocumentIri().stringValue();
		IRI domainIri;
		if(sourceString.substring(9).indexOf('/') > 0) {
			// http://toto.fr/page#me
			domainIri = SimpleValueFactory.getInstance().createIRI(
					sourceString.substring(0, 9),
					sourceString.substring(9, sourceString.substring(9).indexOf('/'))
			);
			
		} else if(sourceString.substring(9).indexOf('#') > 0) {
			// http://toto.fr#me
			// but should never happen as long as we normalize the page URL before processing
			domainIri = SimpleValueFactory.getInstance().createIRI(
					sourceString.substring(0, 9),
					sourceString.substring(9, sourceString.substring(9).indexOf('#'))
			);
		} else {
			// http://toto.fr
			domainIri = source.getDocumentIri();
		}
		
		c.add(
				graphIri,
				DCTERMS.IS_PART_OF,
				domainIri,
				(Resource)null
		);
		
		c.close();
	}
	
}
