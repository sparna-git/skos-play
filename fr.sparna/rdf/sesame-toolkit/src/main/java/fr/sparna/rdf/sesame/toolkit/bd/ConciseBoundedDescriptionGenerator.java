package fr.sparna.rdf.sesame.toolkit.bd;

import java.util.HashMap;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.GraphImpl;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import fr.sparna.rdf.sesame.toolkit.query.ConstructSparqlHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.util.GraphExport;


public class ConciseBoundedDescriptionGenerator implements BoundedDescriptionGeneratorIfc {

	protected Repository repository;
	protected boolean withInverse;
	
	public ConciseBoundedDescriptionGenerator(Repository repository, boolean withInverse) {
		this.repository = repository;
		this.withInverse = withInverse;
	}
	
	public ConciseBoundedDescriptionGenerator(Repository repository) {
		this(repository, false);
	}
	
	@Override
	public void exportBoundedDescription(URI aNode, BoundedDescriptionHandlerIfc handler)
	throws BoundedDescriptionGenerationException {
		Graph result = getConciseBoundedDescriptionGraph(aNode);
		exportGraphToHandler(aNode, result, handler);
	}
	
	protected Graph getConciseBoundedDescriptionGraph(Resource aNode) 
	throws BoundedDescriptionGenerationException {
		GetStatementsWithSubjectHelper helperSubject;
		try {
			helperSubject = new GetStatementsWithSubjectHelper(aNode, this.repository);
			Perform.on(this.repository).construct(helperSubject);
		} catch (SparqlPerformException e) {
			throw new BoundedDescriptionGenerationException(e);
		}
		
		Graph result = helperSubject.getBoundedDescription();
		
		if(this.withInverse) {
			GetStatementsWithObjectHelper helperObject;
			try {
				helperObject = new GetStatementsWithObjectHelper(aNode, this.repository);
				Perform.on(this.repository).construct(helperObject);
			} catch (SparqlPerformException e) {
				throw new BoundedDescriptionGenerationException(e);
			}
			
			// merge 2 graphs
			result.addAll(helperObject.getBoundedDescription());
		}
		
		return result;
	}
	
	protected void exportGraphToHandler(Resource aNode, Graph result, BoundedDescriptionHandlerIfc handler) 
	throws BoundedDescriptionGenerationException {
		try {
			// notify handler of resource
			handler.handleResource(this.repository.getValueFactory().createURI(aNode.toString()));
			
			// notify it of the result
			GraphExport.export(result, handler);
		} catch (Exception e) {
			throw new BoundedDescriptionGenerationException(e);
		}
	}
	
	protected class GetStatementsWithSubjectHelper extends ConstructSparqlHelperBase {

		protected Resource r;
		protected Repository repository;
		protected Graph boundedDescription = new GraphImpl();
		
		@SuppressWarnings("serial")
		public GetStatementsWithSubjectHelper(final Resource r, Repository repository) {
			super(
					"CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }",
					new HashMap<String, Object>() {{
						put("s",r);
					}}
			);
			this.r = r;
			this.repository = repository;
		}

		@Override
		public void handleStatement(Statement s) throws RDFHandlerException {
			// on descends récursivement dans les blanks nodes
			if(s.getObject() instanceof BNode) {
				try {
					this.repository.getConnection().exportStatements(
							(BNode)s.getObject(),
							null,
							null,
							// include inferred
							true,
							// handler
							this
					);
				} catch (RepositoryException e) {
					throw new RDFHandlerException(e);
				}
			}
			
			// ajouter le statement a la liste
			if(!this.boundedDescription.contains(s)) {
				this.boundedDescription.add(s);
			}
		}

		public Graph getBoundedDescription() {
			return boundedDescription;
		}
	}

	
	protected class GetStatementsWithObjectHelper extends ConstructSparqlHelperBase {

		protected Resource r;
		protected Repository repository;
		protected Graph boundedDescription = new GraphImpl();
		
		@SuppressWarnings("serial")
		public GetStatementsWithObjectHelper(final Resource r, Repository repository) {
			super(
					"CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }",
					new HashMap<String, Object>() {{
						put("o",r);
					}}
			);
			this.r = r;
			this.repository = repository;
		}

		@Override
		public void handleStatement(Statement s) throws RDFHandlerException {
			// on descends récursivement dans les blanks nodes
			if(s.getSubject() instanceof BNode) {
				try {
					this.repository.getConnection().exportStatements(
							null,
							null,
							(BNode)s.getSubject(),
							// include inferred
							true,
							// handler
							this
					);
				} catch (RepositoryException e) {
					throw new RDFHandlerException(e);
				}
			}
			
			// ajouter le statement a la liste
			if(!this.boundedDescription.contains(s)) {
				this.boundedDescription.add(s);
			}
		}

		public Graph getBoundedDescription() {
			return boundedDescription;
		}
	}
	
}
