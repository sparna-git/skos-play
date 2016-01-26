package fr.sparna.rdf.skos.toolkit;

import java.net.URI;
import java.util.List;

import org.openrdf.model.Value;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;

public class SKOSNodeSortCriteriaPropertyReader implements SKOSNodeSortCriteriaReader {
	
	protected PropertyReader propertyReader;

	public SKOSNodeSortCriteriaPropertyReader(PropertyReader propertyReader) {
		super();
		this.propertyReader = propertyReader;
	}

	@Override
	public String getLang() {
		return propertyReader.getLang();
	}

	@Override
	public String readSortCriteria(URI node) throws SparqlPerformException {
		List<Value> sortCriterias = propertyReader.read(node);
		// usually there would be only one
		String sortCriteria = (sortCriterias != null && sortCriterias.size() > 0)?sortCriterias.get(0).stringValue():null;
		return sortCriteria;
	}

	
}
