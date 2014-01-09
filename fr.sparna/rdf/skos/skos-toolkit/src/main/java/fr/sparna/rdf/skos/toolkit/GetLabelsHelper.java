package fr.sparna.rdf.skos.toolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;


/**
 * Queries for the skos:prefLabel, skos:altLabels and skos:hiddenLabels of a concept. Concept URI can be null
 * to return the labels of all concepts.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetLabelsHelper extends SelectSparqlHelperBase {
	
	public GetLabelsHelper(
			final java.net.URI concept,
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
				new HashMap<String, Object>() {{
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
	public GetLabelsHelper(java.net.URI concept) {
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
	
	public static class QueryBuilder implements SparqlQueryBuilderIfc {

		// includes pref labels by default
		private boolean includePrefLabels = true;
		// do NOT include alt labels by default
		private boolean includeAltLabels = false;
		// do NOT include hidden labels by default
		private boolean includeHiddenLabels = false;
		private List<String> langs = null;
		private List<java.net.URI> conceptSchemesToExclude;

		
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
			// TODO : mieux gérer la négation
			((this.conceptSchemesToExclude != null && this.conceptSchemesToExclude.size() > 0)?" ?concept <"+SKOS.IN_SCHEME+"> ?scheme ."+"\n":"")+
			"	?concept ?labelType ?label."+"\n" +
			"   FILTER((";
			for (String aLabelType : labelTypes) {
				sparql += "?labelType = <"+aLabelType+">\n"+" || "+"\n";
			}			
			// remove last dirt
			sparql = sparql.substring(0, sparql.length() - ("\n"+" || "+"\n").length());
			sparql += ")";
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
			if(this.conceptSchemesToExclude != null && this.conceptSchemesToExclude.size() > 0) {
				sparql += " && (";
				for (java.net.URI conceptSchemeToInclude : conceptSchemesToExclude) {
					sparql += "?scheme != <"+conceptSchemeToInclude.toString()+">";
					sparql += " && ";
				}
				// remove last dirt
				sparql = sparql.substring(0, sparql.length() - " || ".length());
				sparql += ")";
			}
			sparql += "   )"+
			"}";
			
			return sparql;
		}

		public List<java.net.URI> getConceptSchemesToExclude() {
			return conceptSchemesToExclude;
		}

		public void setConceptSchemesToExclude(List<java.net.URI> conceptSchemesToExclude) {
			this.conceptSchemesToExclude = conceptSchemesToExclude;
		}
		
	}
	
}
