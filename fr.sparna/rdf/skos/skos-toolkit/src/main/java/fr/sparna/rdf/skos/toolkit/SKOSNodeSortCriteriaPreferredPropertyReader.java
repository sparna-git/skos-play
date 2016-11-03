package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.List;

import org.eclipse.rdf4j.model.Value;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;

public class SKOSNodeSortCriteriaPreferredPropertyReader implements SKOSNodeSortCriteriaReader {
	
	protected PreferredPropertyReader propertyReader;

	public SKOSNodeSortCriteriaPreferredPropertyReader(PreferredPropertyReader propertyReader) {
		super();
		this.propertyReader = propertyReader;
	}

	@Override
	public String getLang() {
		return propertyReader.getPreferredLanguage();
	}

	@Override
	public String readSortCriteria(URI node) throws SparqlPerformException {
		List<Value> sortCriterias = propertyReader.getValues(node);
		// usually there would be only one
		String sortCriteria = (sortCriterias != null && sortCriterias.size() > 0)?sortCriterias.get(0).stringValue():null;
		return sortCriteria;
	}

	
}
