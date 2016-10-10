package fr.sparna.rdf.datapress;

import java.util.List;

import javax.annotation.Resource;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFHandler;

import fr.sparna.rdf.FilterEmptyRDFHandler;
import fr.sparna.rdf.FilterRdfa;
import fr.sparna.rdf.FilterXHTMLRDFHandler;
import fr.sparna.rdf.FilteringRDFHandler;
import fr.sparna.rdf.RepositoryRDFHandler;
import fr.sparna.rdf.SchemaOrgHttpNormalizer;
import fr.sparna.rdf.TrimRDFHandler;

public class DataPressHandlerFactory {

	@Resource(name="excludedProperties")
	protected List<String> excludedProperties;
	
	protected boolean autoTrim = true;
	protected boolean filterEmpty = true;
	protected boolean filterXhtml = true;
	protected boolean filterRdfa = true;
	protected boolean normalizeHttpSchemaOrg = true;
	
	public RDFHandler newHandler(Repository repository, IRI documentIri) {
		RepositoryRDFHandler base = new RepositoryRDFHandler(repository, getTargetGraphIri(documentIri));
        return this.newHandler(base);
	}
	
	public IRI getTargetGraphIri(IRI documentIri) {
		return SimpleValueFactory.getInstance().createIRI(documentIri.stringValue()+"#graph");
	}
	
	public RDFHandler newHandler(RDFHandler targetHandler) {
		
		RDFHandler result = new FilteringRDFHandler(targetHandler, null, excludedProperties);
		
		if(this.filterXhtml) {
			result = new FilterXHTMLRDFHandler(result);
		}
		if(this.autoTrim) {
			result = new TrimRDFHandler(result);
		}
		if(this.filterEmpty) {
			result = new FilterEmptyRDFHandler(result);
		}
		if(this.filterRdfa) {
			result = new FilterRdfa(result);
		}
		if(this.normalizeHttpSchemaOrg) {
			result = new SchemaOrgHttpNormalizer(result);
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
	
	

}
