package fr.sparna.rdf.sesame.toolkit.skos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.Literal;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;


/**
 * Queries for the skos:prefLabel, skos:altLabels and skos:hiddenLabels of a concept. Concept URI can be null
 * to return the labels of all concepts.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetLabelsHelper extends SelectSPARQLHelperBase {
	
	public GetLabelsHelper(
			final URI concept,
			boolean includePrefLabels, 
			boolean includeAltLabels,
			boolean includeHiddenLabels,
			List<String> langs) {
		super(
				new QueryBuilder(
					includePrefLabels,
					includeAltLabels,
					includeHiddenLabels,
					langs
				),
				new HashMap<String, Value>() {{
					// si concept est null la variable ne sera pas bindee et la query
					// remontera TOUS les prefLabels de tous les concepts
					if(concept != null) {
						put("concept", concept);
					}
				}}
				);
	}
	
	/**
	 * Use null as the concept URI to return the prefLabels of all concepts.
	 * @param concept
	 */
	public GetLabelsHelper(URI concept) {
		this(
				concept,
				// fetch pref labels
				true,
				// do NOT fetch alt labels
				false,
				// do NOT fetch hidden labels
				false,
				// do NOT restrict on a language
				null);
	}
	


	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource concept = (Resource)binding.getValue("concept");
		URI labelType = (URI)binding.getValue("labelType");
		Literal label = (Literal)binding.getValue("label");
		this.handleLabel(concept, labelType, label.stringValue(), label.getLanguage());
	}
	
	protected abstract void handleLabel(Resource concept, URI labelType, String label, String lang)
	throws TupleQueryResultHandlerException;
	
	public static class QueryBuilder implements SPARQLQueryBuilderIfc {

		// includes pref labels by default
		private boolean includePrefLabels = true;
		// do NOT include alt labels by default
		private boolean includeAltLabels = false;
		// do NOT include hidden labels by default
		private boolean includeHiddenLabels = false;
		private List<String> langs = null;		

		
		public QueryBuilder(
				boolean includePrefLabels, 
				boolean includeAltLabels,
				boolean includeHiddenLabels,
				List<String> langs
		) {
			this.includePrefLabels = includePrefLabels;
			this.includeAltLabels = includeAltLabels;
			this.includeHiddenLabels = includeHiddenLabels;
			this.langs = langs;
		}

		@Override
		public String getSPARQL() {
			List<String> labelTypes = new ArrayList<String>();
			if(this.includePrefLabels) {
				labelTypes.add(SKOS.PREF_LABEL);
			}
			if(this.includeAltLabels) {
				labelTypes.add(SKOS.ALT_LABEL);
			}
			if(this.includeHiddenLabels) {
				labelTypes.add(SKOS.HIDDEN_LABEL);
			}
			
			String sparql = "" +
			"SELECT ?concept ?labelType ?label"+"\n" +
			"WHERE {"+"\n" +
			"   ?concept a <"+SKOS.CONCEPT+"> ."+
			"	?concept ?labelType ?label."+"\n" +
			"   FILTER(";
			for (String aLabelType : labelTypes) {
				sparql += "?labelType = <"+aLabelType+">\n"+" || "+"\n";
			}
			// remove last dirt
			sparql = sparql.substring(0, sparql.length() - ("\n"+" || "+"\n").length());
			if(this.langs != null) {
				sparql += " && (";
				for (String aLang : this.langs) {
					sparql += "lang(?label) = '"+aLang+"'";
					sparql += " || ";
				}
				// remove last dirt
				sparql = sparql.substring(0, sparql.length() - " || ".length());
				sparql += ")";
			}
			sparql += "   )"+
			"}";
			
			return sparql;
		}		
	}
	
}
