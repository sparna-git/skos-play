package fr.sparna.rdf.extractor;

import java.util.List;

import javax.annotation.Resource;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.rio.RDFHandler;

import fr.sparna.rdf.handler.ContextHandler;
import fr.sparna.rdf.handler.FilterDataVocabularyHandler;
import fr.sparna.rdf.handler.FilterEmptyHandler;
import fr.sparna.rdf.handler.FilterRdfaHandler;
import fr.sparna.rdf.handler.FilterXHTMLHandler;
import fr.sparna.rdf.handler.FilteringHandler;
import fr.sparna.rdf.handler.SchemaOrgHttpNormalizerHandler;
import fr.sparna.rdf.handler.TrimHandler;

/**
 * Creates RDFHandler by wrapping a base RDFHandler and adding various filtering around it.
 * @author Thomas Francart
 *
 */
public class DataExtractorHandlerFactory {

	@Resource(name="excludedProperties")
	protected List<String> excludedProperties;
	
	protected boolean autoTrim = true;
	protected boolean filterEmpty = true;
	protected boolean filterXhtml = true;
	protected boolean filterRdfa = true;
	protected boolean filterDataVocabulary = true;
	protected boolean normalizeHttpSchemaOrg = true;
	protected IRI targetGraph;
	
	/**
	 * Returns a new handler that writes to the given repository in a graph corresponding to the given document IRI
	 * @param repository
	 * @param documentIri
	 * @return
	 */
	public RDFHandler newHandler(RepositoryConnection connection, IRI documentIri) {
		if(targetGraph != null) {
			return new RDFInserter(connection);
		} else {
			return newHandler(new ContextHandler(
					new RDFInserter(connection),
					getTargetGraphIri(documentIri))
			);
		}
		
	}
	
	/**
	 * Returns the graph IRI corresponding to the input documentIRI
	 * @param documentIri
	 * @return
	 */
	public IRI getTargetGraphIri(IRI documentIri) {
		return SimpleValueFactory.getInstance().createIRI(documentIri.stringValue()+"#graph");
	}
	
	/**
	 * Returns a new handler that wraps the input RDFHandler
	 * @param targetHandler
	 * @return
	 */
	public RDFHandler newHandler(RDFHandler targetHandler) {
		
		RDFHandler result = new FilteringHandler(targetHandler, null, excludedProperties);
		
		if(this.targetGraph != null) {
			result = new ContextHandler(result, this.targetGraph);
		}
		
		if(this.filterXhtml) {
			result = new FilterXHTMLHandler(result);
		}
		if(this.autoTrim) {
			result = new TrimHandler(result);
		}
		if(this.filterEmpty) {
			result = new FilterEmptyHandler(result);
		}
		if(this.filterRdfa) {
			result = new FilterRdfaHandler(result);
		}
		if(this.filterDataVocabulary) {
			result = new FilterDataVocabularyHandler(result);
		}
		if(this.normalizeHttpSchemaOrg) {
			result = new SchemaOrgHttpNormalizerHandler(result);
		}
		
		return result;
	}

	public List<String> getExcludedProperties() {
		return excludedProperties;
	}

	public void setExcludedProperties(List<String> excludedProperties) {
		this.excludedProperties = excludedProperties;
	}

	public boolean isAutoTrim() {
		return autoTrim;
	}

	public void setAutoTrim(boolean autoTrim) {
		this.autoTrim = autoTrim;
	}

	public boolean isFilterEmpty() {
		return filterEmpty;
	}

	public void setFilterEmpty(boolean filterEmpty) {
		this.filterEmpty = filterEmpty;
	}

	public boolean isFilterXhtml() {
		return filterXhtml;
	}

	public void setFilterXhtml(boolean filterXhtml) {
		this.filterXhtml = filterXhtml;
	}

	public boolean isFilterRdfa() {
		return filterRdfa;
	}

	public void setFilterRdfa(boolean filterRdfa) {
		this.filterRdfa = filterRdfa;
	}

	public boolean isNormalizeHttpSchemaOrg() {
		return normalizeHttpSchemaOrg;
	}

	public void setNormalizeHttpSchemaOrg(boolean normalizeHttpSchemaOrg) {
		this.normalizeHttpSchemaOrg = normalizeHttpSchemaOrg;
	}

	public boolean isFilterDataVocabulary() {
		return filterDataVocabulary;
	}

	public void setFilterDataVocabulary(boolean filterDataVocabulary) {
		this.filterDataVocabulary = filterDataVocabulary;
	}

}
