package fr.sparna.rdf.extractor;

import java.util.Date;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * A DataExtractorListener that handles additional operation on the target Repository :
 * <ul>
 *   <li>Adds a DCterms.modified on the target Graph</<li>
 *   <li>Adds a DCterms.isPartOf to link the target graph the domain name that is computed automatically</<li>
 * </ul>
 * @author Thomas Francart
 *
 */
public class RepositoryManagementListener implements DataExtractorListener {

	private Repository repository;
	
	public RepositoryManagementListener(Repository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public void begin(DataExtractionSource source) {
		// clean target graph
     	repository.getConnection().clear(source.getDocumentIri());
	}

	@Override
	public void end(DataExtractionSource source, boolean success) {
		
		IRI graphIri = new DataExtractorHandlerFactory().getTargetGraphIri(source.getDocumentIri());
		
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
		IRI domainIri = extractDomainIri(source.getDocumentIri());
		
		c.add(
				graphIri,
				DCTERMS.IS_PART_OF,
				domainIri,
				(Resource)null
		);
		
		c.close();
	}
	
	protected IRI extractDomainIri(IRI documentIri) {
		String sourceString = documentIri.stringValue();
		
		// normalize https into http
		if(sourceString.startsWith("https")) {
			sourceString = "http"+sourceString.substring("https".length());
		}
		
		IRI domainIri;
		if(sourceString.substring(9).indexOf('/') > 0) {
			// http://toto.fr/page#me
			domainIri = SimpleValueFactory.getInstance().createIRI(
					sourceString.substring(0, 9),
					sourceString.substring(9, sourceString.substring(9).indexOf('/')+9)
			);
			
		} else if(sourceString.substring(9).indexOf('#') > 0) {
			// http://toto.fr#me
			// but should never happen as long as we normalize the page URL before processing
			domainIri = SimpleValueFactory.getInstance().createIRI(
					sourceString.substring(0, 9),
					sourceString.substring(9, sourceString.substring(9).indexOf('#')+9)
			);
		} else {
			// http://toto.fr
			domainIri = SimpleValueFactory.getInstance().createIRI(sourceString);
		}
		
		return domainIri;
	}
	
	public static void main(String...strings) throws Exception {
		RepositoryManagementListener me = new RepositoryManagementListener(null);
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("http://sparna.fr")));
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("http://www.sparna.fr/")));
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("http://sparna.fr/toto")));
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("http://blog.sparna.fr")));
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("http://sparna.fr#formation")));
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("http://blog.sparna.fr#formation")));
		System.out.println(me.extractDomainIri(SimpleValueFactory.getInstance().createIRI("https://sparna.fr")));
	}
	
}
