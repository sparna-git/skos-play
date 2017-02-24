package fr.sparna.rdf.sesame.toolkit.bd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerBase;

public class LabeledConciseBoundedDescriptionGenerator extends ConciseBoundedDescriptionGenerator implements BoundedDescriptionGeneratorIfc {

	private static final List<java.net.URI> DEFAULT_LABEL_PREDICATES = Arrays.asList(
			new java.net.URI[] { 
					java.net.URI.create(RDFS.LABEL.toString()),
					java.net.URI.create(RDFS.COMMENT.toString()),
					java.net.URI.create(RDFS.SEEALSO.toString())
			}
	);
	
	/**
	 * The list of predicates that this generator will fetch on resources that are referred
	 * by the initial resource for which we generate the bounded description.
	 */
	protected List<URI> labelPredicates;
	
	/**
	 * Explicitly fetch the given predicates for resources referenced by the initial resource
	 * in any statement
	 * 
	 * @param repository
	 * @param withInverse
	 * @param labelPredicates
	 */
	public LabeledConciseBoundedDescriptionGenerator(Repository repository, boolean withInverse, List<java.net.URI> labelPredicates) {
		super(repository, withInverse);
		this.labelPredicates = new ArrayList<URI>();
		for (java.net.URI aURI : labelPredicates) {
			this.labelPredicates.add(toSesameURI(aURI));
		}
	}
	
	/**
	 * Explicitly fetch the given predicates for resources referenced by the initial resource
	 * in any statement
	 * 
	 * @param repository
	 * @param labelPredicates
	 */
	public LabeledConciseBoundedDescriptionGenerator(Repository repository, List<java.net.URI> labelPredicates) {
		this(repository, false, labelPredicates);
	}
	
	/**
	 * Use the default predicate list to fetch : RDFS.LABEL, RDFS.COMMENT, RDFS.SEEALSO
	 * 
	 * @param repository
	 */
	public LabeledConciseBoundedDescriptionGenerator(Repository repository) {
		this(repository, false, DEFAULT_LABEL_PREDICATES);
	}
	
	public LabeledConciseBoundedDescriptionGenerator(Repository repository, boolean withInverse) {
		this(repository, withInverse, DEFAULT_LABEL_PREDICATES);
	}
	
	/**
	 * Explicitly fetch a single predicate for resources referenced by the initial resource
	 * in any statement.
	 * 
	 * @param repository
	 * @param labelPredicate The predicate to fetch
	 */
	public LabeledConciseBoundedDescriptionGenerator(Repository repository, java.net.URI labelPredicate) {
		this(repository, false, Arrays.asList(new java.net.URI[] { labelPredicate }));
	}
	
	public LabeledConciseBoundedDescriptionGenerator(Repository repository, boolean withInverse, java.net.URI labelPredicate) {
		this(repository, withInverse, Arrays.asList(new java.net.URI[] { labelPredicate }));
	}
	
	private static IRI toSesameURI(java.net.URI aURI) {
		return SimpleValueFactory.getInstance().createIRI(aURI.toString());
	}

	@Override
	public void exportBoundedDescription(URI aNode, BoundedDescriptionHandlerIfc handler)
	throws BoundedDescriptionGenerationException {
		final Graph result = getConciseBoundedDescriptionGraph(aNode);
		
		if(this.labelPredicates != null && this.labelPredicates.size() > 0) {
			// gather all objects in a unique Set
			Set<Resource> objects = new HashSet<Resource>();
			for (Statement aStatement : result) {
				if(
						aStatement.getObject() instanceof Resource
						&&
						!objects.contains(aStatement.getObject())
				) {
					objects.add((Resource)aStatement.getObject());
				}
			}
			
			// if we do also inverse bounded description, gather also subjects
			if(this.withInverse) {
				for (Statement aStatement : result) {
					if(
							!objects.contains(aStatement.getSubject())
							&&
							!aStatement.getSubject().equals(aNode)
					) {
						objects.add((Resource)aStatement.getSubject());
					}
				}
			}			
			
			// for each object, gather its predicates belonging to our list
			for (Resource aResource : objects) {
				try {
					for (URI aPredicate : this.labelPredicates) {
						this.repository.getConnection().exportStatements(
								// statements for this subject
								aResource,
								// with this predicate
								SimpleValueFactory.getInstance().createIRI(aPredicate.stringValue()),
								// no matter the value
								null,
								// yes, include inferred
								true,
								// handler
								new RDFHandlerBase() {
									// copy the statement to our bounded description
									@Override
									public void handleStatement(Statement st) throws RDFHandlerException {
										result.add(st);
									}									
								}
						);
					}
				} catch (Exception e) {
					throw new BoundedDescriptionGenerationException(e);
				}
			}
		}
		
		// export to handler
		this.exportGraphToHandler(aNode, result, handler);
	}
	
}
