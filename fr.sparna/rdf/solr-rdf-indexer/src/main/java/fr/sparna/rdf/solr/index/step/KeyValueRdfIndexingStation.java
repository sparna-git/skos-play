package fr.sparna.rdf.solr.index.step;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.AssemblyException;
import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.AssemblyStation;
import fr.sparna.assembly.LifecycleException;
import fr.sparna.assembly.base.BaseAssemblyLineComponent;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperIfc;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueReader;

public class KeyValueRdfIndexingStation extends BaseAssemblyLineComponent<SolrInputDocument> implements AssemblyStation<SolrInputDocument> {

	protected Repository repository;
	protected String field;
	
	protected KeyValueHelperIfc<URI, Literal> helper;
	
	// reader is built from helper in the init method
	protected transient KeyValueReader<URI, Literal> reader;
	
	public KeyValueRdfIndexingStation(
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
	public void init(AssemblyLine<SolrInputDocument> assemblyLine) throws LifecycleException {
		super.init(assemblyLine);
		this.reader = new KeyValueReader<URI, Literal>(repository, helper);
	}

	@Override
	public void process(Assembly<SolrInputDocument> idxable) throws AssemblyException {	
		try {
			List<Literal> l = reader.read(repository.getValueFactory().createURI(idxable.getId()));
			idxable.getDocument().put(field, SolrInputDocumentUtil.toField(field, l));
		} catch (SparqlPerformException e) {
			throw new AssemblyException(e);
		}
	}
	
}
