package fr.sparna.rdf.skos.toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;

import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;


/**
 * Queries for the skos:prefLabel, skos:altLabels and skos:hiddenLabels of a concept. Concept URI can be null
 * to return the labels of all concepts.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetLabelsHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {
	
	public GetLabelsHelper(
			final IRI concept,
			boolean includePrefLabels, 
			boolean includeAltLabels,
			boolean includeHiddenLabels,
			List<String> langs) {
		
		super(
				new SimpleSparqlOperation(new QuerySupplier(
					includePrefLabels,
					includeAltLabels,
					includeHiddenLabels,
					langs
				)).withBinding(
						(concept != null)
						?new SimpleBinding("concept", concept)
						:null
				)
		);
	}
	
	/**
	 * Use null as the concept URI to return the prefLabels of all concepts.
	 * @param concept
	 */
	public GetLabelsHelper(IRI concept) {
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
		IRI labelType = (IRI)binding.getValue("labelType");
		Literal label = (Literal)binding.getValue("label");
		this.handleLabel(concept, labelType, label.stringValue(), label.getLanguage().get());
	}
	
	protected abstract void handleLabel(Resource concept, IRI labelType, String label, String lang)
	throws TupleQueryResultHandlerException;
	
	public static class QuerySupplier implements Supplier<String> {

		// includes pref labels by default
		private boolean includePrefLabels = true;
		// do NOT include alt labels by default
		private boolean includeAltLabels = false;
		// do NOT include hidden labels by default
		private boolean includeHiddenLabels = false;
		private List<String> langs = null;
		private List<java.net.URI> conceptSchemesToExclude;

		
		public QuerySupplier(
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
		public String get() {
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
					sparql += "langMatches(lang(?label), '"+aLang+"')";
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
