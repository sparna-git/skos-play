package fr.sparna.rdf.datapress;

import java.util.List;

import javax.annotation.Resource;

import org.eclipse.rdf4j.rio.RDFHandler;

import fr.sparna.rdf.FilterEmptyRDFHandler;
import fr.sparna.rdf.FilterXHTMLRDFHandler;
import fr.sparna.rdf.FilteringRDFHandler;
import fr.sparna.rdf.TrimRDFHandler;

public class DataPressHandlerFactory {

	@Resource(name="excludedProperties")
	protected List<String> excludedProperties;
	
	protected boolean autoTrim = true;
	protected boolean filterEmpty = true;
	protected boolean filterXhtml = true;
	
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
	
	

}
