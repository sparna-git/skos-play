package fr.sparna.rdf.sesame.toolkit.list;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;


/**
 * Ramene dans l'ordre le contenu d'une liste RDF.
 * 
 * @author Thomas Francart
 */
public class RDFListReader {
	
	protected Repository repository;
	
	public RDFListReader(Repository repository) {
		super();
		this.repository = repository;
	}
	
	/**
	 * Indique si l'objet du statement passé en paramètre est une liste, c'est-à-dire un blank-node portant un triplet rdf:first.
	 * 
	 * @param statement
	 * @return true if the object of the statement is an RDF list
	 * @throws RepositoryException
	 */
	public boolean hasListObject(Statement statement) throws RepositoryException {
		return(
				statement.getObject() instanceof BNode
				&&
				(this.repository.getConnection().getStatements((BNode)statement.getObject(), RDF.FIRST, null, false).hasNext())
		);
	}

	/**
	 * Ramène sous forme d'une liste le contenu de la liste RDF dont le noeud est passé en paramètre. C'est le noeud de la liste
	 * qui doit être passé à la méthode.
	 * 
	 * @param list	the RDF list node
	 * @return 		a List containing the resources contained in the RDF list
	 * @throws RepositoryException
	 */
	public List<Resource> getListContent(Resource list) throws RepositoryException {
		List<Resource> result = new ArrayList<Resource>();
		RepositoryResult<Statement> rrFirst = this.repository.getConnection().getStatements(list, RDF.FIRST, null, false);
		if(rrFirst.hasNext()) {
			Resource first = (Resource)((Statement)rrFirst.next()).getObject();
			result.add(first);
			
			RepositoryResult<Statement> rrRest = this.repository.getConnection().getStatements(list, RDF.REST, null, false);
			if(rrRest.hasNext()) {
				Resource rest = (Resource)((Statement)rrRest.next()).getObject();
				if(rest.stringValue() != RDF.NIL.stringValue()) {
					result.addAll(getListContent(rest));
				}
			}
		}		
		
		return result;
	}
	
	/**
	 * Si le statement initial pointe sur une liste avec 
	 * 	?s ?p ?o .
	 *  ?o rdf:first ?first .
	 *  ?o rdf:rest ?o2 .
	 *  ?o2 rdf:first ?second .
	 *  ?o2 rdf:rest rdf:nil
	 *  
	 * Ramène une liste de statements :
	 *  ?s ?p ?first .
	 *  ?s ?p ?second .
	 * 
	 * @param 	statement
	 * @return 	a List containing the translated statements
	 * @throws RepositoryException
	 */
	public List<Statement> getEquivalentStatements(Statement statement) throws RepositoryException {
		List<Resource> listContent = getListContent((Resource)statement.getObject());
		List<Statement> result = new ArrayList<Statement>();
		for (Resource aResource : listContent) {
			result.add(this.repository.getValueFactory().createStatement(
					statement.getSubject(),
					statement.getPredicate(),
					aResource
			)					
			);
		}
		return result;
	}
	
}
