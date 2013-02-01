package fr.sparna.rdf.sesame.toolkit.skos;

/**
 * Declares all the constants of the SKOS vocabulary.
 * See http://www.w3.org/TR/skos-reference/skos.html
 * 
 * @author Thomas Francart
 */
public final class SKOS {

	// SKOS.NAMESPACE
	
	public static final String NAMESPACE = "http://www.w3.org/2004/02/skos/core#";
	
	public static final String PREFIX = "skos";
	
	// CLASSES...
	
	public static final String COLLECTION = SKOS.NAMESPACE+"Collection";
	
	public static final String CONCEPT = SKOS.NAMESPACE+"Concept";
	
	public static final String CONCEPT_SCHEME = SKOS.NAMESPACE+"ConceptScheme";
	
	public static final String ORDERED_COLLECTION = SKOS.NAMESPACE+"OrderedCollection";
	
	// PROPERTIES
	
	public static final String ALT_LABEL = SKOS.NAMESPACE+"altLabel";
	
	public static final String BROAD_MATCH = SKOS.NAMESPACE+"broadMatch";
	
	public static final String BROADER = SKOS.NAMESPACE+"broader";
	
	public static final String BROADER_TRANSITIVE = SKOS.NAMESPACE+"broaderTransitive";
	
	public static final String CHANGE_NOTE = SKOS.NAMESPACE+"changeNote";
	
	public static final String CLOSE_MATCH = SKOS.NAMESPACE+"closeMatch";
	
	public static final String DEFINITION = SKOS.NAMESPACE+"definition";
	
	public static final String EDITORIAL_NOTE = SKOS.NAMESPACE+"editorialNote";
	
	public static final String EXACT_MATCH = SKOS.NAMESPACE+"exactMatch";
	
	public static final String EXAMPLE = SKOS.NAMESPACE+"example";
	
	public static final String HAS_TOP_CONCEPT = SKOS.NAMESPACE+"hasTopConcept";
	
	public static final String HIDDEN_LABEL = SKOS.NAMESPACE+"hiddenLabel";
	
	public static final String HISTORY_NOTE = SKOS.NAMESPACE+"historyNote";
	
	public static final String IN_SCHEME = SKOS.NAMESPACE+"inScheme";
	
	public static final String MAPPING_RELATION = SKOS.NAMESPACE+"mappingRelation";
	
	public static final String MEMBER = SKOS.NAMESPACE+"member";
	
	public static final String MEMBER_LIST = SKOS.NAMESPACE+"memberList";
	
	public static final String NARROW_MATCH = SKOS.NAMESPACE+"narrowMatch";
	
	public static final String NARROWER = SKOS.NAMESPACE+"narrower";
	
	public static final String NARROWER_TRANSITIVE = SKOS.NAMESPACE+"narrowerTransitive";
	
	public static final String NOTATION = SKOS.NAMESPACE+"notation";
	
	public static final String NOTE = SKOS.NAMESPACE+"note";
	
	public static final String PREF_LABEL = SKOS.NAMESPACE+"prefLabel";
	
	public static final String RELATED = SKOS.NAMESPACE+"related";
	
	public static final String RELATED_MATCH = SKOS.NAMESPACE+"relatedMatch";
	
	public static final String SCOPE_NOTE = SKOS.NAMESPACE+"scopeNote";
	
	public static final String SEMANTIC_RELATION = SKOS.NAMESPACE+"semanticRelation";
	
	public static final String TOP_CONCEPT_OF = SKOS.NAMESPACE+"topConceptOf";

	public static boolean isDatatypeProperty(String skosProperty) {
		return
				skosProperty.equals(SKOS.ALT_LABEL)
				||
				skosProperty.equals(SKOS.CHANGE_NOTE)
				||
				skosProperty.equals(SKOS.DEFINITION)
				||
				skosProperty.equals(SKOS.EDITORIAL_NOTE)
				||
				skosProperty.equals(SKOS.EXAMPLE)
				||
				skosProperty.equals(SKOS.HIDDEN_LABEL)
				||
				skosProperty.equals(SKOS.NOTATION)
				||
				skosProperty.equals(SKOS.NOTE)
				||
				skosProperty.equals(SKOS.PREF_LABEL)
				||
				skosProperty.equals(SKOS.SCOPE_NOTE);
	}
}
