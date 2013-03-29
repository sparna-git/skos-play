package fr.sparna.rdf.sesame.toolkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.helpers.RDFHandlerBase;

import fr.sparna.rdf.sesame.toolkit.bd.BoundedDescriptionGeneratorIfc;
import fr.sparna.rdf.sesame.toolkit.bd.BoundedDescriptionHandlerAdapter;
import fr.sparna.rdf.sesame.toolkit.bd.ConciseBoundedDescriptionGenerator;
import fr.sparna.rdf.sesame.toolkit.bd.LabeledConciseBoundedDescriptionGenerator;
import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperIfc;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.query.builder.PagingSPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.StringSPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;

public class Examples {
	
	private static Repository getLocalMemoryRepository(String rdfDataFilePath) throws Exception {
		return RepositoryBuilder.fromString(rdfDataFilePath);
	}	

	private static Repository getLocalMemoryRepositoryWithRDFSInference(String rdfDataFilePath) throws Exception {
		RepositoryBuilder factory = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));
		factory.addOperation(new LoadFromFileOrDirectory(rdfDataFilePath));
		return factory.createNewRepository();
	}
	
	private static Repository getRemoteRepository(String repositoryURL) throws Exception {
		EndpointRepositoryFactory factory = new EndpointRepositoryFactory(repositoryURL);
		return factory.createNewRepository();
	}
	
	private static Repository getLoadedMemoryRepositoryOrEndpointRepository(String repositoryURL) throws Exception {
		StringRepositoryFactory factory = new StringRepositoryFactory(repositoryURL);
		return factory.createNewRepository();
	}
	
	private static void selectAllLabels(Repository repository) throws Exception {
		SesameSPARQLExecuter.newExecuter(repository).executeSelect(new SelectSPARQLHelper(
				new SPARQLQuery("SELECT ?x ?label WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }") {
					/**
					 * By default, the base class SelectSPARQLHelperBase returns null. If you need to make a query without
					 * inferred statements, sets the boolean to false.
					 */
					@Override
					public Boolean isIncludeInferred() {
						return false;
					}
				},
				new TupleQueryResultHandlerBase() {
					@Override
					public void handleSolution(BindingSet binding)
					throws TupleQueryResultHandlerException {
						Resource x = (Resource)binding.getValue("x");
						Literal label = (Literal)binding.getValue("label");
						System.out.println(x+" has label "+label.stringValue()+" with language "+label.getLanguage());
					}
				}
		));
	}
	
	private static void selectAllLabelsWithPaging(Repository repository, final Integer offset, final Integer limit)
	throws Exception {
		SesameSPARQLExecuter.newExecuter(repository).executeSelect(new SelectSPARQLHelper(
				new PagingSPARQLQueryBuilder(
						new StringSPARQLQueryBuilder("SELECT ?x ?label WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }"), offset, limit
				),
				new TupleQueryResultHandlerBase() {
					@Override
					public void startQueryResult(List<String> arg0)
					throws TupleQueryResultHandlerException {
						System.out.println("Paging from "+offset+" to "+(offset + limit)+" : ");
					}
			
					@Override
					public void handleSolution(BindingSet binding)
					throws TupleQueryResultHandlerException {
						Resource x = (Resource)binding.getValue("x");
						Literal label = (Literal)binding.getValue("label");
						System.out.println(x+" has label "+label.stringValue()+" with language "+label.getLanguage());
					}
				}
		));
	}
	
	
	private static void constructAllLabels(Repository repository) throws Exception {
		// Here is another way to control to include inferred statements : directly on the SesameSPARQLExecuter
		// If the includeInferred flag is present on the Helper, it will be taken. If none is provided on the
		// helper, the default behavior set on the SPARQLExecuter will be used.
		new SesameSPARQLExecuter(repository, false).executeConstruct(new ConstructSPARQLHelper(
			/**
			 * The query is a little stupid, it is just to illustrate Construct helper
			 */
			"CONSTRUCT { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label } WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }",
			// process resulting statements
			new RDFHandlerBase() {
				@Override
				public void handleStatement(Statement s) throws RDFHandlerException {
					System.out.println(s.getSubject().stringValue()+" "+s.getPredicate().stringValue()+" "+s.getObject().stringValue());
				}
			}
		));
	}
	
	private static void constructAllLabelsWithBaseHelper(Repository repository) throws Exception {
		// Here is another way to control to include inferred statements : directly on the SesameSPARQLExecuter
		// If the includeInferred flag is present on the Helper, it will be taken. If none is provided on the
		// helper, the default behavior set on the SPARQLExecuter will be used.
		new SesameSPARQLExecuter(repository, false).executeConstruct(new ConstructSPARQLHelperBase(
			/**
			 * The query is a little stupid, it is just to illustrate Construct helper
			 */
			"CONSTRUCT { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label } WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }"
			) {
				// process resulting statements
				@Override
				public void handleStatement(Statement s) throws RDFHandlerException {
					System.out.println(s.getSubject().stringValue()+" "+s.getPredicate().stringValue()+" "+s.getObject().stringValue());
				}	
			}
		);
	}
	
	
	private static void constructAllLabelsWithPaging(Repository repository, final Integer offset, final Integer limit) throws Exception {
		new SesameSPARQLExecuter(repository, false).executeConstruct(new ConstructSPARQLHelper(
				/**
				 * The query is a little stupid, it is just to illustrate Construct helper
				 */
				new PagingSPARQLQueryBuilder(
						new StringSPARQLQueryBuilder("CONSTRUCT { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label } WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }"),
						offset,
						limit
				),
				// process resulting statements
				new RDFHandlerBase() {
					@Override
					public void handleStatement(Statement s) throws RDFHandlerException {
						System.out.println(s.getSubject().stringValue()+" "+s.getPredicate().stringValue()+" "+s.getObject().stringValue());
					}
				}
			));
	}
	
	private static boolean isItmAssociation(final Repository repository) throws Exception {
		return SesameSPARQLExecuter.newExecuter(repository).executeAsk(new SPARQLQuery(	
				"ASK { ?s <http://www.mondeca.com/system/itm#type> \"association\" }"
			) {
			
			@Override
			public Boolean isIncludeInferred() {
				return false;
			}	

			@Override
			public Map<String, Object> getBindings() {
				return new HashMap<String, Object>() {{
					put("s", java.net.URI.create("http://publications.europa.eu/resource/ontology/cdm#article"));
				}};
			}
		});
	}
	
	class ResourceReader implements SelectSPARQLHelperIfc {

		private URI subjectURI;
		
		public ResourceReader(URI subjectURI) {
			super();
			this.subjectURI = subjectURI;
		}

		@Override
		public SPARQLQueryIfc getQuery() {
			return new SPARQLQuery(
					"SELECT * WHERE { ?s ?p ?o }",
					new HashMap<String, Object>() {{
						put("s", subjectURI);
					}}
			);
		}
		
		@Override
		public TupleQueryResultHandler getHandler() {
			return new TupleQueryResultHandlerBase() {
				@Override
				public void startQueryResult(List<String> arg0)
				throws TupleQueryResultHandlerException {
					System.out.println(subjectURI+" is subject of the following triples :");
				}
				
				
				@Override
				public void handleSolution(BindingSet binding)
				throws TupleQueryResultHandlerException {
					Resource predicate = (Resource)binding.getValue("p");
					Value object = binding.getValue("o");
					System.out.println(predicate.stringValue()+" "+object.stringValue());
				}
			};
		}
	}
	
	private static void useSameSPARQLWithTwoBindings(Repository repository) throws Exception {
		SesameSPARQLExecuter.newExecuter(repository).executeSelect(
				new Examples().new ResourceReader(repository.getValueFactory().createURI("http://sws.geonames.org/3532479/"))
		);
		SesameSPARQLExecuter.newExecuter(repository).executeSelect(
				new Examples().new ResourceReader(repository.getValueFactory().createURI("http://sws.geonames.org/3580763/"))
		);
	}
	
	private static void testCustomFunction(Repository repository) throws Exception {
		SesameSPARQLExecuter.newExecuter(repository).executeSelect(
				new SelectSPARQLHelper(
						"PREFIX sparna:<http://www.sparna.fr/rdf/sesame/toolkit/functions#> " +
						"SELECT ?x ?label ?score WHERE {" +
						" ?x <http://www.w3.org/2004/02/skos/core#prefLabel> ?label ." +
						" BIND(sparna:levenshtein(?label,\"tourism\") as ?score)" +
						" FILTER(?score <= 3)" +
						"}" +
						" ORDER BY ?score ",
						new DebugHandler()
				)				
		);
	}
	
	private static void testBoundedDescription(Repository repository) throws Exception {
		BoundedDescriptionGeneratorIfc generator = new ConciseBoundedDescriptionGenerator(repository);
		generator.exportBoundedDescription(
				repository.getValueFactory().createURI("http://thes.world-tourism.org#CIRCUIT_TOURISTIQUE"),
				new BoundedDescriptionHandlerAdapter(RDFWriterRegistry.getInstance().get(RDFFormat.N3).getWriter(System.out))
		);
		System.out.println();
		generator = new LabeledConciseBoundedDescriptionGenerator(repository, java.net.URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
		generator.exportBoundedDescription(
				repository.getValueFactory().createURI("http://thes.world-tourism.org#CIRCUIT_TOURISTIQUE"),
				new BoundedDescriptionHandlerAdapter(RDFWriterRegistry.getInstance().get(RDFFormat.N3).getWriter(System.out))
		);
	}
	
	/**
	 * args[0] : either a path to a RDF file or directory, or a URL is you choose to run the exemples on a remote repository
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... args) throws Exception {
		// local or remote repository
		Repository repository = Examples.getLoadedMemoryRepositoryOrEndpointRepository(args[0]);
		
		Examples.selectAllLabels(repository);
		Examples.selectAllLabelsWithPaging(repository, 0, 10);
		Examples.constructAllLabels(repository);
		Examples.constructAllLabelsWithPaging(repository, 0, 5);		
		System.out.println(Examples.isItmAssociation(repository));
		Examples.useSameSPARQLWithTwoBindings(repository);	
		Examples.testCustomFunction(repository);
		Examples.testBoundedDescription(repository);
	}
}
