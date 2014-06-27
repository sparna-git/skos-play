package fr.sparna.rdf.solr.index.step;

import java.util.List;

import org.apache.solr.common.SolrInputField;
import org.openrdf.model.Literal;

public class SolrInputDocumentUtil {

	public static SolrInputField toField(String fieldId, List<Literal> values) {
		SolrInputField field = new SolrInputField(fieldId);
		for (Literal aLiteral : values) {
			field.addValue(aLiteral.stringValue(), 1.0f);
		}
		return field;
	}
	
}
