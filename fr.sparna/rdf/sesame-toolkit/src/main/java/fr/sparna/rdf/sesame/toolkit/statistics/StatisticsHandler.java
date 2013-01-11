package fr.sparna.rdf.sesame.toolkit.statistics;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * Gather statistics on the result of a CONSTRUCT query or an entire repository if used in the
 * Repository.export() method.
 * <p/>The statistics gathered are :
 * <ul>
 *   <li/>Total number of triples;
 *   <li/>Number of URI that are subject of triples;
 *   <li/>Number of blank nodes that are subject of triples;
 *   <li/>Number of datatypeTriples and objectTriples;
 *   <li/>Number of triples by predicates;
 *   <li/>Number of triples by namespace;
 *   <li/>Number of instances per class;
 * </ul>
 * 
 * Statistics results can then be handled by a {@link fr.sparna.rdf.sesame.toolkit.statistics.StatisticsRenderer StatisticsRenderer}</code>
 * 
 * @author Thomas Francart
 *
 */
public class StatisticsHandler implements RDFHandler {

	// Total number of triples
	protected int numberOfTriples = 0;
	// Number of datatypeTriples
	protected int numberOfDatatypeTriples = 0;
	// Number of objectTriples
	protected int numberOfObjectTriples = 0;
	// Number of triples by predicates
	protected Map<URI, Integer> numberOfTripleByPredicate = new HashMap<URI, Integer>();
	// Number of triples by namespace
	protected Map<String, Integer> numberOfTripleByNamespace = new HashMap<String, Integer>();
	// Number of instances per class
	protected Map<URI, Integer> numberOfInstancesByClass = new HashMap<URI, Integer>();
	
	// Number of URI that are subject of triples
	protected int numberOfSubjectURI = 0;
	// Number of blank nodes that are subject of triples
	protected int numberOfSubjectBlankNodes = 0;
	
	// just temporary variables to check for unicity
	protected Set<String> setOfSubjectURI = new HashSet<String>();
	protected Set<String> setOfSubjectBlankNodes = new HashSet<String>();
	
	protected void reset() {
		this.numberOfTriples = 0;
		this.numberOfDatatypeTriples = 0;
		this.numberOfObjectTriples = 0;
		this.numberOfTripleByPredicate = new HashMap<URI, Integer>();
		this.numberOfTripleByNamespace = new HashMap<String, Integer>();
		this.numberOfInstancesByClass = new HashMap<URI, Integer>();
		this.numberOfSubjectURI = 0;
		this.numberOfSubjectBlankNodes = 0;
		this.setOfSubjectURI = new HashSet<String>();
		this.setOfSubjectBlankNodes = new HashSet<String>();
	}
	
	@Override
	public void startRDF() throws RDFHandlerException {
		this.reset();
	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		this.numberOfSubjectURI = this.setOfSubjectURI.size();
		this.numberOfSubjectBlankNodes = this.setOfSubjectBlankNodes.size();
	}

	@Override
	public void handleComment(String arg0) throws RDFHandlerException {
		// nothing
	}

	@Override
	public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
		// nothing
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		// on compte le nombre total de triplets
		this.numberOfTriples++;
		// on compte les literaux et les objectProperties
		if(s.getObject() instanceof Literal) {
			this.numberOfDatatypeTriples++;
		} else {
			this.numberOfObjectTriples++;
		}
		// on compte le breakdown par predicat
		URI predicate = URI.create(s.getPredicate().stringValue());
		if(this.numberOfTripleByPredicate.containsKey(predicate)) {
			this.numberOfTripleByPredicate.put(predicate, (this.numberOfTripleByPredicate.get(predicate) + 1));
		} else {
			this.numberOfTripleByPredicate.put(predicate, 1);
		}
		// on compte le breakdown par namespace
		String predicateNamespace = getURINamespace(predicate);
		if(this.numberOfTripleByNamespace.containsKey(predicateNamespace)) {
			this.numberOfTripleByNamespace.put(predicateNamespace, (this.numberOfTripleByNamespace.get(predicateNamespace) + 1));
		} else {
			this.numberOfTripleByNamespace.put(predicateNamespace, 1);
		}
		// on compte le nombre d'instances
		if(s.getPredicate().stringValue().equals(RDF.TYPE.stringValue())) {
			URI classURI = URI.create(s.getObject().stringValue());
			if(this.numberOfInstancesByClass.containsKey(classURI)) {
				this.numberOfInstancesByClass.put(classURI, (this.numberOfInstancesByClass.get(classURI) + 1));
			} else {
				this.numberOfInstancesByClass.put(classURI, 1);
			}
		}
		// on compte les sujets et les blankNodes
		if(s.getSubject() instanceof org.openrdf.model.URI) {
			this.setOfSubjectURI.add(s.getSubject().stringValue());
		} else if(s.getSubject() instanceof org.openrdf.model.BNode) {
			this.setOfSubjectBlankNodes.add(s.getSubject().stringValue());
		} else {
			System.out.println("What is this type of subject !?"+s.getSubject().getClass().getCanonicalName());
		}
	}
	
	protected String getURINamespace(URI aUri) {
		String uri = aUri.toString();
		String namespace = null;
		if(uri.toString().lastIndexOf("#") > 0) {
			namespace = uri.substring(0, uri.lastIndexOf("#")+1);
		} else if (uri.lastIndexOf("/") > 0) {
			namespace = uri.substring(0, uri.lastIndexOf("/")+1);
		} else {
			// default
			namespace = uri;
		}
		return namespace;
	}

	public int getNumberOfTriples() {
		return numberOfTriples;
	}

	public int getNumberOfDatatypeTriples() {
		return numberOfDatatypeTriples;
	}

	public int getNumberOfObjectTriples() {
		return numberOfObjectTriples;
	}

	public Map<URI, Integer> getNumberOfTripleByPredicate() {
		return numberOfTripleByPredicate;
	}

	public int getNumberOfSubjectURI() {
		return numberOfSubjectURI;
	}

	public int getNumberOfSubjectBlankNodes() {
		return numberOfSubjectBlankNodes;
	}

	public Map<String, Integer> getNumberOfTripleByNamespace() {
		return numberOfTripleByNamespace;
	}

	public Map<URI, Integer> getNumberOfInstancesByClass() {
		return numberOfInstancesByClass;
	}

}
