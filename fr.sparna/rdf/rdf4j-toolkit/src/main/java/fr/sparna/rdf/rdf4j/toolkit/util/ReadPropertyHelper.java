package fr.sparna.rdf.rdf4j.toolkit.util;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;

import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;


/**
 * Returns the values for a given predicate of a given subject.
 * <p>The subjectURI can be null, in which case this helper will return all the pair [?subject;?object] for the given predicate.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class ReadPropertyHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @deprecated Don't use the additionnalCriteria parameters
	 * 
	 * @param predicateURI 					URI of the predicate type to read. This must not be null.
	 * @param lang							language to filter the values on. Can be null.
	 * @param subjectURI 					URI of the subject for which we want to read the predicate. Can be null to read the predicate for any subject.
	 * @param additionalCriteriaPredicate	URI of a predicate to add an additional selection criteria on subjects to read the predicate for.
	 * @param additionalCriteriaObject		URI to use as value of the additional selection criteria.
	 */
	public ReadPropertyHelper(
			final IRI predicateURI,
			final String additionalPath,
			final String lang,
			final IRI subjectURI,
			final IRI additionalCriteriaPredicate,
			final IRI additionalCriteriaObject
	) {
		super(
				new SimpleSparqlOperation(
						(additionalCriteriaPredicate != null || additionalCriteriaObject != null)
						?new QuerySupplier(((additionalPath != null)?"<"+predicateURI.toString()+">|"+additionalPath:null),true, lang)
						:new QuerySupplier(((additionalPath != null)?"<"+predicateURI.toString()+">|"+additionalPath:null),false, lang)	
				).withBindings(
						SimpleSparqlOperation.toBindginSet(new ArrayList<Binding>() {{
							// bind the property
							add(new SimpleBinding("predicate", predicateURI));
							
							// si concept est null la variable ne sera pas bindee et la query
							// remontera TOUTES les valeurs de tous les concepts
							if(subjectURI != null) {
								add(new SimpleBinding("subject", subjectURI));
							}
							
							if(additionalCriteriaPredicate != null) {
								add(new SimpleBinding("additionalCriteriaPredicate", additionalCriteriaPredicate));
							}
							
							if(additionalCriteriaObject != null) {
								add(new SimpleBinding("additionalCriteriaObject", additionalCriteriaObject));
							}
						}})
				)
		);
	}
	
	/**
	 * Will read the predicate value for every subjects.
	 * 
	 * @param predicateURI URI of the predicate to read
	 */
	public ReadPropertyHelper(final IRI predicateURI) {
		this(predicateURI, null, null, null, null, null);
	}
	
	/**
	 * Will read the predicate value for a given subject;
	 * 
	 * @param predicateURI URI of the property type to read on the concepts
	 * @param subjectURI 	URI of the subject for which we want to read the predicate.
	 */
	public ReadPropertyHelper(final IRI predicateURI, final IRI subjectURI) {
		this(predicateURI, null, null, subjectURI, null, null);
	}
	
	/**
	 * Process the bindings and calls <code>handleValue</code> with each tuple [concept;value]
	 */
	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Resource subject = (Resource)binding.getValue("subject");
		Value value = binding.getValue("object");
		this.handleValue(subject, value);
	}
	
	/**
	 * Called for each tuple [subject;object].
	 * 
	 * @param concept	URI of the subject
	 * @param value		value of the predicate read
	 * 
	 * @throws TupleQueryResultHandlerException
	 */
	protected abstract void handleValue(Resource concept, Value value)
	throws TupleQueryResultHandlerException;
	
	/**
	 * Builds a SPARQL Query that fetch the <code>?object</code> of a <code>?predicate</code> on a given <code>?subject</code> variable.
	 * If the <code>?subject</code> variable is bound to a URI, this will fetch the <code>?object</code>s of this <code>?subject</code> only.
	 * If it is not bound, this will fetch all the tuples [subject;object] in the graph for this predicate.
	 * 
	 * @author Thomas Francart
	 */
	public static class QuerySupplier implements Supplier<String> {
		
		protected String predicatePath = null;
		protected boolean additionalCriteria = false;
		protected String lang;

		public QuerySupplier(String predicatePath, boolean additionalCriteria, String lang) {
			super();
			this.predicatePath = predicatePath;
			this.additionalCriteria = additionalCriteria;
			this.lang = lang;
		}
		
		public QuerySupplier(boolean additionalCriteria, String lang) {
			this(null, additionalCriteria, lang);
		}

		/**
		 * No predicatePath, no additionnal criteria, no language filtering
		 */
		public QuerySupplier() {	
			this(false, null);
		}
		
		@Override
		public String get() {
			StringBuffer sparql = new StringBuffer();
			if(this.predicatePath != null) {
				// add a distinct to avoid duplicated matches
				sparql.append("SELECT DISTINCT ?subject ?object"+"\n");
			} else {
				sparql.append("SELECT ?subject ?object"+"\n");
			}			
			sparql.append("WHERE {"+"\n");
			// sparql.append("	?subject ?predicate ?object ."+"\n");
			sparql.append("	?subject");
			if(this.predicatePath != null) {
				sparql.append(" "+this.predicatePath);
			} else {
				sparql.append("	?predicate");
			}
			sparql.append("	?object ."+"\n");
			if(this.additionalCriteria) {
				sparql.append("   ?subject ?additionalCriteriaPredicate ?additionalCriteriaObject ."+"\n");
			}
			if(this.lang != null) {
				sparql.append("   FILTER(langMatches(lang(?object), '"+this.lang+"'))"+"\n");
			}
			sparql.append("} ");
			
			return sparql.toString();
		}		
	}
	
}
