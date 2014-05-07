package fr.sparna.rdf.sesame.jena.repository;

import junit.framework.Assert;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaRepositoryTest {

	@Test
	public void testRepository() throws Exception {
		Model model = ModelFactory.createDefaultModel();
		JenaRepository repository = new JenaRepository(model);
		repository.initialize();
		Assert.assertTrue(repository.getValueFactory() != null);
		Assert.assertTrue(repository.getConnection() != null);
	}
	
	@Test
	public void testShutdown() throws Exception {
		Model model = ModelFactory.createDefaultModel();
		JenaRepository repository = new JenaRepository(model);
		repository.initialize();
		Assert.assertFalse(model.isClosed());
		repository.shutDown();
		Assert.assertTrue(model.isClosed());
	}
	
	@Test
	public void jenaHelloWorld() throws Exception {
//		// create a new model
//		Model model = ModelFactory.createDefaultModel();
//		// create a Resource and a Literal
//		Resource subject = model.createResource("http://www.sparna.fr/thomas");
//		Literal object = model.createLiteral("Thomas Francart");
//		// create a Statement, using a constant
//		Statement statement = model.createStatement(subject, RDFS.label, object);
//		// add the statement to the model
//		model.add(statement);
//		// write the model to the console, in Turtle
//		model.write(System.out, RDFFormat.TURTLE.getName());
//		
//		
//		StmtIterator it = model.listStatements();
//		while(it.hasNext()) {
//			Statement s = it.next();
//			System.out.println(s.getSubject()+"/"+s.getPredicate()+"/"+s.getObject());
//		}
//		
//		// determine syntax automatically from extension
//		model.read("my_rdf.ttl");
//		// specify explicitely the syntax to use
//		model.read("my_rdf.xml", RDFFormat.RDFXML.getName());
//		
//		// Create a model and read into it from file 
//		// "data.ttl" assumed to be Turtle.
//		Model anotherModel = RDFDataMgr.loadModel("data.ttl") ;
//
//		// Create a dataset and read into it from file 
//		// "data.trig" assumed to be TriG.
//		Dataset dataset = RDFDataMgr.loadDataset("data.trig") ;
//
//		// Read into an existing Model
//		RDFDataMgr.read(model, "data2.ttl") ;
//		
//		// writes in RDF/XML
//		model.write(new FileOutputStream("my_rdf.rdf"));
//		// writes using another syntax
//		model.write(new FileOutputStream("my_rdf.ttl"), RDFFormat.TURTLE.getName());
//		
//		// writes a model
//		RDFDataMgr.write(
//				new FileOutputStream("my_rdf.rdf"),
//				model,
//				RDFFormat.TURTLE) ;
//		// writes a Dataset
//		RDFDataMgr.write(
//				new FileOutputStream("my_rdf.trig"),
//				dataset,
//				RDFFormat.TRIG) ;
//		
//		String queryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 10" ;
//		Query query = QueryFactory.create(queryString) ;
//		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
//		try {
//			ResultSet results = qexec.execSelect() ;
//			while(results.hasNext()) {
//				QuerySolution soln = results.nextSolution() ;
//				// Get a result variable - must be a resource
//				Resource s = soln.getResource("s") ;
//				Resource p = soln.getResource("p") ; 
//				// Get a result variable by name.
//				RDFNode o = soln.get("o") ;   
//				if(o.isLiteral()) {
//					// do something
//				}
//				// Get a result variable - must be a literal
//				// Literal o = soln.getLiteral("o") ;				
//			}
//		} finally {
//			qexec.close() ;
//		}
//		
//		String myQueryString = "SELECT ?s ?p ?o WHERE { ?s ?p ?o } LIMIT 10" ;
//		Query myQuery = QueryFactory.create(queryString) ;
//		// query a remote endpoint
//		QueryExecution myQueryExec = QueryExecutionFactory.sparqlService("http://fr.dbpedia.org/sparql", myQuery) ;
//		// instead of local data
//		// QueryExecution qexec = QueryExecutionFactory.create(myQuery, model) ;
//		try {
//			ResultSet results = qexec.execSelect() ;
//			while(results.hasNext()) {
//				// etc...			
//			}
//		} finally {
//			qexec.close() ;
//		}
//		
//		String myConstruct = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o } LIMIT 10" ;
//		Query myConstructQuery = QueryFactory.create(myQueryString) ;
//		QueryExecution myConstructExec = QueryExecutionFactory.create(myConstructQuery, model) ;
//		try {
//			// use execConstruct() instead of execSelect()
//			// the execution returns an RDF graphe : an instance of Model
//			Model resultModel = qexec.execConstruct() ;
//			// do something with the model
//			StmtIterator iterator = resultModel.listStatements();
//			// etc.
//		} finally { myConstructExec.close(); }
//		
//		Model spec = FileManager.get().loadModel( "examples.ttl" );
//		Resource root = spec.createResource( spec.expandPrefix( "eg:opening-example" ) );
//		// pour créer un model
//		Model m = Assembler.general.openModel( root );
//		// pour créer un Dataset
//		Dataset d = DatasetFactory.assemble(root);
//		ModelFactory.a
		
	}
	
	
}
