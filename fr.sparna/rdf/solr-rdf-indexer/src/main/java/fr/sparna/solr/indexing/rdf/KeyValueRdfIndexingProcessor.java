package fr.sparna.solr.indexing.rdf;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueReader;
import fr.sparna.solr.indexing.IndexingProcessorIfc;
import fr.sparna.solr.indexing.LifecycleException;
import fr.sparna.solr.indexing.ProcessingException;
import fr.sparna.solr.indexing.base.BaseIndexingComponent;

public class KeyValueRdfIndexingProcessor extends BaseIndexingComponent implements IndexingProcessorIfc<Resource> {

	protected Repository repository;
	protected String field;
	
	protected KeyValueHelperIfc<URI, Literal> helper;
	
	// reader is built from helper in the init method
	protected transient KeyValueReader<URI, Literal> reader;
	
	public KeyValueRdfIndexingProcessor(
			Repository repository,
			String field,
			KeyValueHelperIfc<URI, Literal> helper
	) {
		super();
		this.repository = repository;
		this.field = field;
		this.helper = helper;
	}

	@Override
	public void init() throws LifecycleException {
		this.reader = new KeyValueReader<URI, Literal>(repository, helper);
	}

	@Override
	public void process(Resource documentId, SolrInputDocument doc) throws ProcessingException {	
		try {
			List<Literal> l = reader.read((URI)documentId);
			doc.put(field, SolrInputDocumentUtil.toField(field, l));
		} catch (SparqlPerformException e) {
			throw new ProcessingException(e);
		}
	}
	
}
