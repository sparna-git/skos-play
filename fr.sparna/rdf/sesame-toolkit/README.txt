
Sesame-Toolkit documentation

Sesame-toolkit is a set of utility java classes that ease the programming with OpenRDF Sesame RDF library. Its main features include :
	- Easy initialisation of Sesame repository from an RDF file or directory containing RDF files
	- Easy loading of RDF from various data-sources : file, directory, URL, XML+XSL, etc.
	- SPARQL Helper mechanism that allows easy SPARQL query execution and handling of results
	- SesameSPARQLExecuter that wraps redundant SPARQL execution code
	- A set of utility query handlers to process query results : generate CSV, sort, split, count or copy results.
	- Bounded description generation classes
	- RepositoryWriter to easily write repository contents to an RDF file, including proper sorting of RDF/XML triples to garantee the serialisation

I - Easy repository creation / initialization

	// creates an in-memory Repository without inference capabilities loaded with the RDF at the given path
	// the path can the URL of an RDF file, a file, a directory, or a classpath resource.
	// RDF serialization does not matter as long as Sesame can understand it
	Repository r = RepositoryBuilder.fromString(String rdfDataFilePath);

	// creates an in-memory Repository without inference capabilities loaded with the data
	// in the given String
	// serialization used for the RDF data does not matter as long as Sesame can understand it
	Repository r = RepositoryBuilder.fromRdf(String rdfData);

	// example of an advanced repository creation :
	// creates an in-memory Repository without inference capabilities loaded with the data in the given
	// file or directory, the data from the given URL, and on which a SPARQL inference rule is applied
	RepositoryBuilder builder = new RepositoryBuilder();
	builder.addOperation(new LoadFromFileOrDirectory(String fileOrDirectoryPath));
	builder.addOperation(new LoadFromURL(URL aURL));
	builder.addOperation(new InferFromSPARQL(SPARQLQueryBuilderIfc query));
	Repository r = builder.createNewRepository();

II - Read and load SPARQL queries from difference sources

	// reads a SPARQL query from a File
	SPARQLQueryBuilderIfc sparqlBuilder = new FileSPARQLQueryBuilder(File aFile);
	// reads a SPARQL query from a Resource in the classpath
	SPARQLQueryBuilderIfc sparqlBuilder = new ResourceSPARQLQueryBuilder(this, String sparqlResource);

III - Wraps redundant query execution code using a Helper / Executer pattern

	SelectSPARQLHelper helper = new SelectSPARQLHelper(
		SPARQLQueryBuilderIfc aSPARQLQuery,
		TupleQueryResultHandler aHandlerTo
	);
	SesameSPARQLExecuter.newExecuter(Repository r).executeSelect(

	); 