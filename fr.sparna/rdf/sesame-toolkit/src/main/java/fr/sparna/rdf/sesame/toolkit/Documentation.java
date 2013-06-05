package fr.sparna.rdf.sesame.toolkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.Rio;

import fr.sparna.rdf.sesame.toolkit.bd.BoundedDescriptionGeneratorIfc;
import fr.sparna.rdf.sesame.toolkit.bd.BoundedDescriptionHandlerAdapter;
import fr.sparna.rdf.sesame.toolkit.bd.ConciseBoundedDescriptionGenerator;
import fr.sparna.rdf.sesame.toolkit.bd.LabeledConciseBoundedDescriptionGenerator;
import fr.sparna.rdf.sesame.toolkit.handler.CSVHandler;
import fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler;
import fr.sparna.rdf.sesame.toolkit.handler.CountStatementHandler;
import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.handler.MultipleRDFHandler;
import fr.sparna.rdf.sesame.toolkit.handler.SplittingRDFHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLUpdate;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase;
import fr.sparna.rdf.sesame.toolkit.query.builder.OrderBy;
import fr.sparna.rdf.sesame.toolkit.query.builder.PagingSPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;
import fr.sparna.rdf.sesame.toolkit.repository.ConfigRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.OWLIMConfigProvider;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.repository.operation.InferFromSPARQL;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromSPARQL;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromString;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromURL;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromXML;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ThreadedRepositoryOperation;
import fr.sparna.rdf.sesame.toolkit.util.Namespaces;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;

/**
 * <b>Table of contents</b><br /><br />
 * <a href="#section1">RepositoryBuilder : easily create Sesame repositories</a><br />
 * <a href="#section2">RepositoryOperationIfc : load data when a repository is created</a><br />
 * <a href="#section3">RepositoryFactoryIfc : create other types of Repositories</a><br />
 * <a href="#section4">SPARQLQueryBuilderIfc : read SPARQL queries from different sources</a><br />
 * <a href="#section5">SPARQLQueryIfc : create SPARQL queries</a><br />
 * <a href="#section6">Perform and helpers : execute SPARQL queries</a><br />
 * <a href="#section7">Handlers : some utility handlers</a><br />
 * <a href="#section8">Utilities</a><br />
 * <br />
 * <br />
 * <ol type="I">
 * <a id="section1" />
 * <br />
 * <li><b>RepositoryBuilder : easily create Sesame repositories</b><br />
 * <p>
 * 	One-line initialization of Repositories loaded with some data.<br />
 *  These shortcuts are using the corresponding {@link fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationIfc}
 * 	underneath; they are useful when you receive a command-line argument parameter as a String for exemple.
 * </p>
 * <ol>
 * <li>
 * 		Creates a repository loaded with RDF data from a single file
 * 		{@.jcite -- fromString1}
 * </li>
 * <li>
 * 		Creates a repository loaded with RDF data from multiple files in a directory
 * 		{@.jcite -- fromString2}
 * </li>
 * <li>
 * 		Creates a repository pointing to a remote SPARQL endpoint or Sesame repository
 * 		{@.jcite -- fromString3}
 * </li>
 * <li>
 * 		Creates a repository loaded with RDF data from a URL
 * 		{@.jcite -- fromURL1}
 * </li>
 * <li>
 * 		Creates a repository loaded with RDF data from a String
 * 		{@.jcite -- fromRdf1}
 * </li>
 * </ol>
 * <a id="section2" />
 * <br />
 * </li><!-- end Repository Creation Shortcuts -->
 * 
 * <li><b>RepositoryOperationIfc : load data when a repository is created</b><br />
 * <p>
 * 	A {@link fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationIfc} is an operation executed by the RepositoryBuilder,
 * 	when a repository is created, usually to load some data into the repository.
 * </p>
 * <ol>
 * <li>
 * 		A single operation can be used to initialize a repository
 * 		{@.jcite -- singleOperation1}
 * </li>
 * <li>
 * 		Multiple operations can be combined
 * 		{@.jcite -- multipleOperation1}
 * </li>
 * <li>
 * 		Basic operations are {@link fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory},
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromString} to load inline RDF data,
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromURL} to load data from a URL, and
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream} that can be used to load RDF data from
 * 		a resource on the classpath.
 * 		{@.jcite -- basicOperations1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromSPARQL} operation executes a serie of
 * 		SPARQL CONSTRUCT queries read from a directory on a given endpoint, and loads the resulting triples in
 * 		the constructed Repository
 * 		{@.jcite -- loadFromSPARQL1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.repository.operation.InferFromSPARQL} operation applies recursively a serie of
 * 		SPARQL CONSTRUCT queries on the source repository and saves the resulting triples in the repository, until no additionnal results are found.
 * 		{@.jcite -- inferFromSPARQL1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates} operation applies a serie of
 * 		SPARQL INSERT or DELETE queries on the source repository.
 * 		{@.jcite -- applyUpdates1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromXML} operation applies an XSL transformation
 * 		onto an XML file or a list of XML files contained in a given directory, to produce RDF/XML and then load it in the repository;
 * 		{@.jcite -- loadFromXML1}
 * </li>
 * <li>
 * 		Any of these operations can be executed asynchronously in a separate thread using a
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.operation.ThreadedRepositoryOperation} that
 * 		encapsulates another operation inside a new thread. This is useful in web application contexts in which, for example, you may
 * 		want to load a large RDF file when the application starts, without impacting on the time the web application takes to start.
 * 		{@.jcite -- threadedRepositoryOperation1}
 * </li>
 * <li>
 * 		Other operations exists like {@link fr.sparna.rdf.sesame.toolkit.repository.operation.ClearRepository}
 * 		that empties a repository, or {@link fr.sparna.rdf.sesame.toolkit.repository.operation.CleanDatatypes} that
 * 		removes all non-standard datatypes from the data.
 * </li>
 * </ol>
 * <br />
 * <a id="section3" />
 * <br />
 * </li><!-- end Operations -->
 * 
 * <li><b>RepositoryFactoryIfc : create other types of Repositories</b><br />
 * <p>
 * By default, the RepositoryBuilder creates an in-memory store with no inferencing. You can configure it to create other
 * types of repository by passing it the proper {@link fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc}
 * </p>
 * <ol>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory} can create RDFS-aware in-memory Repositories.
 * 		{@.jcite -- localMemoryRepositoryFactory1}
 * </li>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory} creates a connection to a remote SPARQL endpoint or Sesame repository.
 * 		(this internally uses either HTTPRepository or SPARQLRepository).
 * 		This also shows that a {@link fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryIfc} can be used independantly from the RepositoryBuilder.
 * 		{@.jcite -- endpointRepositoryFactory1}
 * </li>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.repository.ConfigRepositoryFactory} creates Repositories based on a Sesame configuration file and is
 * 		useful to initialize an OWLIM triplestore entirely programmaticaly.<p />
 * 		<em>
 * 			Note that OWLIM is not packaged with this library and needs to be present in your classpath for this code.
 * 			You need to download it and include it as a dependency separately in order for this to work.
 * 		</em>
 * 		{@.jcite -- configRepositoryFactory1}
 * </li>
 * </ol>
 * <a id="section4" />
 * <br /> 
 * </li><!-- end RepositoryFactoryIfc -->
 * <li><b>SPARQLQueryBuilderIfc : reads SPARQL queries from different sources</b>
 * <p>A {@link fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc} is an abstraction for anything capable or returning
 * a SPARQL query as a String. The query could be read from a file or build programmaticaly depending on the implementation.
 * </p>
 * <ol>
 * <li>
 * 		The base implemention is {@link fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder} that allows
 * 		to construct SPARQL queries from String, File, InputStream, classpath Resources.
 * 		{@.jcite -- queryBuilder1}
 * </li>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.query.builder.PagingSPARQLQueryBuilder} wraps another SparqlQueryBuilderIfc and
 * 		adds LIMIT, OFFSET and ORDER BY clauses to it
 * 		{@.jcite -- pagingSparqlQueryBuilder1}
 * </li>
 * </ol> 
 * <a id="section5" />
 * <br />
 * </li><!-- end SPARQLQueryBuilderIfc -->
 * <li><b>SPARQLQueryIfc : create SPARQL queries</b>
 * <p>
 * 		{@link fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc}s represents a SPARQL query along with all its
 * 		execution parameters :<br />
 *		- The SPARQL query itself as a String;<br />
 *		- The variable bindings of the query;<br />
 *		- The default graph on which the query will be executed;<br />
 *		- The available named graph in the query;<br />
 *		<br/>
 *		{@link fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery} is a concrete implementation of SPARQLQueryIfc that
 *		uses a {@link fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc} to return the SPARQL String.
 *		<br/>
 *		{@link fr.sparna.rdf.sesame.toolkit.query.SPARQLUpdateIfc} and its concrete implementation
 *		{@link fr.sparna.rdf.sesame.toolkit.query.SPARQLUpdate} are subclasses of SPARQLQuery with the additionnal following parameters :
 *		- The default insert graph in which triples will be inserted;<br />
 *		- The default graphs in which triples will be deleted.<br />
 * </p>
 * <ol>
 * <li>
 * 		The variable bindings mechanism is useful in the sense you can write your query once and use it with
 * 		different values in different contexts.
 * 		{@.jcite -- sparqlQuery1}
 * </li>
 * <li>
 * 		The objects you can pass in the Map<String, Object> can be of the following types :<br />
 * 		- a java.net.URI or a java.net.URL will be interpreted as an org.openrdf.model.URI<br />
 * 		- an org.openrdf.model.Value or one of its subclass (URI, Literal, BNode) will be inserted as it is<br />
 *      - any other class will be passed as a String in a Literal, using toString()
 * 		{@.jcite -- sparqlQuery2}
 * </li>
 * </ol>
 * <a id="section6" />
 * <br />
 * </li><!-- end SPARQLQueryIfc -->
 * <li><b>Perform and helpers : execute SPARQL queries</b>
 * <p>
 * 	{@link fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperIfc} and {@link fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelperIfc} represents
 * 	the association of a SPARQL query and of the code capable of processing the result of the query. In other words,
 * 	a SelectSPARQLHelperIfc is the composition of a SPARQLQueryIfc + a TupleQueryResultHandler from Sesame, and a ConstructSPARQLHelperIfc
 * 	is the composition of a SPARQLQueryIfc + a RDFHandler from Sesame.
 * </p>
 * <p>
 * 	Both of these abstractions can be passed to a {@link fr.sparna.rdf.sesame.toolkit.query.Perform} to be executed.
 *  The SPARQL query gets executed and the results are being passed to the helper. The Perform takes care of all the
 *  connection handling, variable bindings, etc.<br />
 * </p>
 * <ol>
 * <li>
 * 		Simple cases with a SPARQL query as a String are easily adressed by constructing a SelectSPARQLHelper with the String, associate the appropriate
 * 		TupleQueryResultHandler, and pass them to the Perform class. The "on(Repository)" static method tells the Perform class to execute the query
 * 		on the given Repository.<br />
 * 		You need to catch {@link fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException} when performing
 * 		a SPARQL query.
 * 		{@.jcite -- perform1}
 * </li>
 * <li>
 * 		Here is another example where the SPARQLQuery is constructed inline.
 * 		{@.jcite -- perform2}
 * </li>
 * <li>
 * 		The Perform object can be configured with the same parameters as a SPARQLQueryIfc : 
 * 		includeInferred, defaultGraph, namedGraphs, defaultInsertGraph and defaultRemoveGraphs. All queries
 * 		processed by the same Perform instance will be applied the same parameters, unless they are overidden
 * 		in the SPARQLQuery itself.
 * 		{@.jcite -- perform3}
 * </li>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperIfc} comes in two implementations : 
 *  	{@link fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper} is a simple composition subclass to use most of the time, and
 *  	{@link fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelperBase} is used when you want to create subclasses. The same goes
 *  	with the implementations of {@link fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelperIfc}.<br />
 * 		Here is an example using SelectSPARQLHelperBase to work with subclasses instead of composition
 * 		{@.jcite -- selectSPARQLHelperBase1}
 * </li>
 * <li>
 * 		Here is another example with exception handling detailed :
 * 		{@.jcite -- perform4}
 * </li>
 * </ol>
 * <a id="section7" />
 * <br />
 * </li><!-- end SelectSPARQLHelperIfc, ConstructSPARQLHelperIfc -->
 * <li><b>TupleQueryResultHandler and RDFHandler</b>
 * <p>
 * 		Some useful Handlers are also provided
 * </p>
 * <ol>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.handler.DebugHandler} prints the result of a SPARQL query on the console
 * 		or on a given OutputStream. See the previous examples.
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.handler.CSVHandler} generates a Comma Separated Value file
 * 		from the result of a SPARQL SELECT query.
 * 		{@.jcite -- csvHandler1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.handler.SortingRDFHandler} retains all the resulting triples of
 * 		a Construct query or a Sesame export and sorts them according to subjects, then predicate (rdf:type first),
 * 		then objects. This allows to pretty print RDF/XML or Turtle files. See also {@link fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.handler.SplittingRDFHandler} splits the result of a construct
 * 		query or a Sesame export in multiple files.
 * 		{@.jcite -- splittingRDFHandler1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler} copies the resulting triples of a construct
 * 		query or a Sesame export into a target Repository. Target repository can be set to be the same
 * 		as the source repository to insert the constructed triples in the same repository. This is outdated by SPARQL 1.1 Update features.
 * 		{@.jcite -- copyStatementRDFHandler1}
 * </li>
 * <li>
 * 		The {@link fr.sparna.rdf.sesame.toolkit.handler.MultipleRDFHandler} wraps a list of other handlers and
 * 		forwards its methods to the list of the wrapped handlers.
 * 		{@.jcite -- multipleRDFHandler1}
 * </li>
 * </ol>
 * <a id="section8" />
 * <br />
 * </li><!-- end Handlers -->
 * <li><b>Utilities</b>
 * <ol>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter} writes the content of a Repository or part of the
 * 		Repository (a named graph or the result of a SPARQL construct qury), to a File, a String, or an OutputStream.
 * 		By default the repository contents gets sorted so that the serialisation order is garanteed.
 * 		{@.jcite -- repositoryWriter1}
 * </li>
 * <li>
 * 		{@link fr.sparna.rdf.sesame.toolkit.util.Namespaces} contains a static mapping between namespaces URIs and
 * 		their associated prefix. Data is loaded from a local copy of the data provided by the http://prefix.cc service.
 * 		It is possible to dynamically load the data from prefix.cc at initialisation. Every Repository
 * 		built trough the RepositoryBuilder has all of its Namespaces mapping loaded into the static Namespaces
 * 		mapping.
 * 		{@.jcite -- namespaces1}
 * </li>
 * </ol>
 * </li><!-- end Utilities -->
 * </ol>
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("unused")
public class Documentation {
		
	private static void fromString1() throws Exception {
		// -- fromString1
		// file extension can be .rdf, .ttl, .n3, .trig, .trix
		// any other extensions (e.g. .owl) will be assumed to be RDF/XML
		Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		// -- fromString1
	}	
	
	private static void fromString2() throws Exception {
		// -- fromString2
		// directory contains any number of RDF files, with subdirectories
		Repository r = RepositoryBuilder.fromString("/directory/containing/rdf/files");
		// -- fromString2
	}
	
	private static void fromString3() throws Exception {
		// -- fromString3
		// the string passed to "fromString" can also be the URL of a SPARQL endpoint or Sesame repository
		Repository r = RepositoryBuilder.fromString("http://dbpedia.org/sparql");
		// -- fromString3
	}
	
	private static void fromURL1() throws Exception {
		// -- fromURL1
		// Attemps to load the given URL in a local memory repository.
		Repository r = RepositoryBuilder.fromURL(new URL("http://dbpedia.org/resource/Berlin"));
		// -- fromURL1
	}
	
	private static void fromRdf1() throws Exception {
		// -- fromRdf1
		// string can contain any valid RDF syntax, they will all be tested until one suceeds.
		String rdfInlineData = "@prefix foo: <http://www.foo.com/> foo:A foo:B foo:C . foo:A foo:D \"A name\"@en";
		Repository r = RepositoryBuilder.fromRdf(rdfInlineData);
		// -- fromRdf1
	}
	
	private static void singleOperation1() throws Exception {
		// -- singleOperation1
		String rdfDataPath = "/directory/containing/rdf/files";
		RepositoryBuilder builder = new RepositoryBuilder(new LoadFromFileOrDirectory(rdfDataPath));
		Repository r = builder.createNewRepository();
		// -- singleOperation1
	}
	
	private static void multipleOperation1() throws Exception {
		// -- multipleOperation1
		String rdfDataPath = "/directory/containing/rdf/files";
		String rdfInlineData = "@prefix foo: <http://www.foo.com/> foo:A foo:B foo:C . foo:A foo:D \"A name\"@en";
		RepositoryBuilder builder = new RepositoryBuilder();
		builder.addOperation(new LoadFromFileOrDirectory(rdfDataPath));
		builder.addOperation(new LoadFromString(rdfInlineData));
		Repository r = builder.createNewRepository();
		// -- multipleOperation1
	}
	
	private static void basicOperations1() throws Exception {
		// -- basicOperations1
		String endpointURL = "http://dbpedia.org/sparql";
		RepositoryBuilder builder = new RepositoryBuilder();
		// to load from a file or directory
		builder.addOperation(new LoadFromFileOrDirectory("/path/to/file/or/directory"));
		// to load from inline RDF data
		builder.addOperation(new LoadFromString("@prefix foo: <http://www.foo.com/> foo:A foo:B foo:C . foo:A foo:D \"A name\"@en"));
		// to load from a URL
		builder.addOperation(new LoadFromURL(new URL("http://dbpedia.org/resource/Berlin")));
		// to load from a classpath resource. RDF Format is deduced with the resource file extension
		Object anyObject = null;
		builder.addOperation(new LoadFromStream(anyObject, "path/to/resource.ttl"));
		// -- basicOperations1
	}
	
	private static void loadFromSPARQL1() throws Exception {
		// -- loadFromSPARQL1
		String endpointURL = "http://dbpedia.org/sparql";
		File directoryContainingSPARQL = new File("/path/to/directory/containing/sparql/construct/queries");
		RepositoryBuilder builder = new RepositoryBuilder(new LoadFromSPARQL(RepositoryBuilder.fromString(endpointURL), directoryContainingSPARQL));
		Repository r = builder.createNewRepository();
		// -- loadFromSPARQL1
	}
	
	private static void inferFromSPARQL1() throws Exception {
		// -- inferFromSPARQL1
		File directoryContainingSPARQL = new File("/path/to/directory/containing/sparql/construct/queries");
		RepositoryBuilder builder = new RepositoryBuilder(new InferFromSPARQL(directoryContainingSPARQL));
		Repository r = builder.createNewRepository();
		// -- inferFromSPARQL1
	}

	private static void applyUpdates1() throws Exception {
		// -- applyUpdates1
		RepositoryBuilder builder = new RepositoryBuilder();
		// load from a file or directory
		builder.addOperation(new LoadFromFileOrDirectory("/path/to/file/or/directory"));
		// apply updates contained in files in a repository
		builder.addOperation(new ApplyUpdates(SPARQLUpdate.fromUpdateDirectory(new File("/path/to/directory/containing/sparql/updates"))));
		// -- applyUpdates1
	}
	
	private static void loadFromXML1() throws Exception {
		// -- loadFromXML1
		File directoryContainingSPARQL = new File("/path/to/directory/containing/sparql/construct/queries");
		LoadFromXML operation = new LoadFromXML(
				new File("/path/to/xml/file/r/directory"),
				new File("/path/to/stylesheet")
		);
		RepositoryBuilder builder = new RepositoryBuilder(operation);
		Repository r = builder.createNewRepository();
		System.out.println("Number of processed documents : "+operation.getProcessedFilesCounter());
		// -- loadFromXML1
	}
	
	private static void threadedRepositoryOperation1() throws Exception {
		// -- threadedRepositoryOperation1
		LoadFromFileOrDirectory operation = new LoadFromFileOrDirectory("/path/to/BIG/rdf/file");
		CountDownLatch latch = new CountDownLatch(1);
		ThreadedRepositoryOperation threadedOperation = new ThreadedRepositoryOperation(operation, latch);
		RepositoryBuilder builder = new RepositoryBuilder(threadedOperation);
		// this will launch the operation thread
		Repository r = builder.createNewRepository();
		
		// now, somewhere else in the code, we can wait for the thread to finish if needed
		// provided we have access to the CountDownLatch object
		try {
			// block until the latch has been set to 0
			long start = System.currentTimeMillis();
			System.out.println("Waiting for repository loading...");
			latch.await();
			System.out.println("Loading finished in "+(System.currentTimeMillis() - start));
		} catch (InterruptedException ex){
			System.err.println(ex.toString());
			Thread.currentThread().interrupt();
		}
		
		// test if loading was sucessfull or failed due to an exception
		if(threadedOperation.isSucessful()) {
			System.out.println("Loading was sucessful");
		} else {
			System.out.println("Loading was NOT sucessful");
		}
		// -- threadedRepositoryOperation1
	}

	private static void localMemoryRepositoryFactory1() throws Exception {
		// -- localMemoryRepositoryFactory1
		RepositoryBuilder builder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));
		builder.addOperation(new LoadFromFileOrDirectory("/directory/containing/rdf/files"));
		Repository r = builder.createNewRepository();
		// -- localMemoryRepositoryFactory1
	}
	
	private static void endpointRepositoryFactory1() throws Exception {
		// -- endpointRepositoryFactory1
		String repositoryURL = "http://dbpedia.org/sparql";
		EndpointRepositoryFactory factory = new EndpointRepositoryFactory(repositoryURL);
		Repository r = factory.createNewRepository();
		// -- endpointRepositoryFactory1
	}
	
	private static void configRepositoryFactory1() throws Exception {
		// -- configRepositoryFactory1
		// to get OWL inference
		ConfigRepositoryFactory crf = new ConfigRepositoryFactory(OWLIMConfigProvider.OWL_REDUCED_CONFIG_PROVIDER);
		// to get RDFS inference, use :
		// ConfigRepositoryFactory crf = new ConfigRepositoryFactory(OWLIMConfigProvider.OWL_REDUCED_CONFIG_PROVIDER);
		// you can write your custom RuleSet and load it with :
		// ConfigRepositoryFactory crf = new ConfigRepositoryFactory("owlim-base.ttl", "/path/to/custom-rulset.pie");
		RepositoryBuilder builder = new RepositoryBuilder(crf);
		builder.addOperation(new LoadFromFileOrDirectory("/directory/containing/rdf/files"));
		Repository r = builder.createNewRepository();
		// -- configRepositoryFactory1
	}
	
	private static void queryBuilder1() throws Exception {
		// -- queryBuilder1
		// simplest constructor with a String
		String sparql = "SELECT DISTINCT ?type WHERE { ?x a ?type }";
		SPARQLQueryBuilder b1 = new SPARQLQueryBuilder(sparql);
		System.out.println(b1.getSPARQL());
		// reads a SPARQL query from a File
		SPARQLQueryBuilder b2 = new SPARQLQueryBuilder(new File("/path/to/sparql/file.rq"));
		System.out.println(b2.getSPARQL());
		// reads a SPARQL query from a classpath resource
		// put the sparql query file in the same java package as the class that is using it
		Object user = null;
		SPARQLQueryBuilder b3 = new SPARQLQueryBuilder(user, "myQuery.rq");
		System.out.println(b2.getSPARQL());
		// -- queryBuilder1
	}
	
	private static void pagingSparqlQueryBuilder1() throws Exception {
		// -- pagingSparqlQueryBuilder1
		// simplest implementation of StringSPARQLQueryBuilder
		String sparql = "SELECT DISTINCT ?type WHERE { ?x a ?type }";
		SPARQLQueryBuilder b1 = new SPARQLQueryBuilder(sparql);
		System.out.println(b1.getSPARQL());
		PagingSPARQLQueryBuilder b2 = new PagingSPARQLQueryBuilder(b1, 10, 10);
		// should print :
		// SELECT DISTINCT ?type WHERE { ?x a ?type } OFFSET 10 LIMIT 10
		System.out.println(b2.getSPARQL());
		b2 = new PagingSPARQLQueryBuilder(b1, 10, 10, new OrderBy("type", false));
		// should print :
		// SELECT DISTINCT ?type WHERE { ?x a ?type } ORDER BY DESC(?type) OFFSET 10 LIMIT 10
		System.out.println(b2.getSPARQL());
		// -- pagingSparqlQueryBuilder1
	}
	
	private static void sparqlQuery1() throws Exception {
		// -- sparqlQuery1
		// Simple String constructor
		SPARQLQuery q1 = new SPARQLQuery("SELECT ?x WHERE { ?x a ?type }");
		// Or use a SPARQLQueryBuilderIfc instead
		SPARQLQuery q2 = new SPARQLQuery(new SPARQLQueryBuilder(new File("/path/to/file.rq")));
		// use the variable bindings to bind the "type" variable to the value "http://xmlns.com/foaf/0.1/Person"
		// this is equivalent to the query SELECT DISTINCT ?x WHERE { ?x a <http://xmlns.com/foaf/0.1/Person> }
		// except the original query can be written once and reused with different values each time
		SPARQLQuery q3 = new SPARQLQuery(
				"SELECT ?x WHERE { ?x a ?type }",
				new HashMap<String, Object>(){{
					put("type", java.net.URI.create("http://xmlns.com/foaf/0.1/Person"));
				}}
		);
		// -- sparqlQuery1
	}

	private static void sparqlQuery2() throws Exception {
		// -- sparqlQuery2
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		SPARQLQueryBuilderIfc theQuery = new SPARQLQueryBuilder("SELECT ?x WHERE { ?x a ?type }");
		// binding with a java.net.URI
		SPARQLQuery q = new SPARQLQuery(
				theQuery,
				new HashMap<String, Object>(){{
					put("type", java.net.URI.create("http://xmlns.com/foaf/0.1/Person"));
				}}
		);
		// binding with a Value from the ValueFactory
		SPARQLQuery q2 = new SPARQLQuery(
				theQuery,
				new HashMap<String, Object>(){{
					put("type", r.getValueFactory().createURI("http://xmlns.com/foaf/0.1/Document"));
				}}
		);
		// -- sparqlQuery2
	}
	
	private static void perform1() throws RepositoryFactoryException {
		// -- perform1
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		try {
			// Performs the query. The DebugHandler prints the result on the console
			Perform.on(r).select(new SelectSPARQLHelper("SELECT DISTINCT ?type WHERE { ?x a ?type }", new DebugHandler()));
		} catch (SPARQLExecutionException e) {
			e.printStackTrace();
		}
		// -- perform1
	}
	
	private static void perform2() throws Exception {
		// -- perform2
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		// Performs the query. The DebugHandler prints the result on the console
		Perform.on(r).select(new SelectSPARQLHelper(
				new SPARQLQuery(
						"SELECT ?x WHERE { ?x a ?type }",
						new HashMap<String, Object>(){{
							put("type", java.net.URI.create("http://xmlns.com/foaf/0.1/Document"));
						}}
				),				
				new DebugHandler()));
		// -- perform2
	}
	
	private static void perform3() throws Exception {
		// -- perform3
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/directory");
		Perform perform = new Perform(r);
		// sets a default graph for all queries processed by this Perform instance, unless
		// overriden in the query itself.
		perform.setDefaultGraphs(Collections.singleton(java.net.URI.create("http://www.exemple.com/graph#foo")));
		// this query will be executed in the default graph "http://www.exemple.com/graph#foo"
		perform.select(new SelectSPARQLHelper("SELECT DISTINCT ?type WHERE { ?x a ?type }", new DebugHandler()));
		SPARQLQuery q = new SPARQLQuery("SELECT DISTINCT ?type WHERE { ?x a ?type }");
		q.setDefaultGraphs(Collections.singleton(java.net.URI.create("http://www.another.com/graph#bar")));
		// this query will be executed in the default graph "http://www.another.com/graph#bar"
		perform.select(new SelectSPARQLHelper(q, new DebugHandler()));
		// -- perform3
	}
	
	// -- selectSPARQLHelperBase1
	private void selectSPARQLHelperBase1() throws Exception {
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/directory");
		Perform.on(r).select(new MyHelper());
	}
	
	class MyHelper extends SelectSPARQLHelperBase {

		public MyHelper() {
			// construct the helper with a SPARQL query coming from a String, a File
			// or a classpath resource
			super(new SPARQLQueryBuilder(new File("/path/to/sparql/query.rq")));
			// could be :
			// super("SELECT ?x ?label WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }");
		}

		/**
		 * Override the handleSolution method to process the query results.
		 */
		@Override
		public void handleSolution(BindingSet bindingSet)
		throws TupleQueryResultHandlerException {
			Resource x = (Resource)bindingSet.getValue("x");
			Literal label = (Literal)bindingSet.getValue("label");
			System.out.println(x+" has label "+label.stringValue()+" with language "+label.getLanguage());
		}		
	}
	// -- selectSPARQLHelperBase1
	
	private static void perform4() {
		// -- perform4
		try {
			final Repository r = RepositoryBuilder.fromString("/path/to/rdf/directory");
			Perform.on(r).select(new SelectSPARQLHelper(
					new SPARQLQuery("SELECT ?x ?label WHERE { ?x <http://www.w3.org/2000/01/rdf-schema#label> ?label }"),
					
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
		} catch (RepositoryFactoryException e) {
			e.printStackTrace();
		} catch (SPARQLExecutionException e) {
			e.printStackTrace();
		}
		// -- perform4
	}
	
	private void csvHandler1() throws Exception {
		// -- csvHandler1
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		// Performs the query, and outputs CSV results in the given file
		Perform.on(r).select(new SelectSPARQLHelper(
				"SELECT DISTINCT ?type WHERE { ?x a ?type }",
				new CSVHandler(
						new PrintWriter(new FileOutputStream("/path/to/output/file.csv")),
						// true to add quotes around the values
						true,
						// true to add the header a the top of the file
						true
				)
		));
		// -- csvHandler1
	}
	
	private void splittingRDFHandler1() throws Exception {
		// -- splittingRDFHandler1
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		RepositoryConnection c = r.getConnection();
		c.export(new SplittingRDFHandler(
				// base name for all the files to generate
				// files will be named /base/file-1.ttl, /base/files-2.ttl, etc.
				"/base/file.ttl",
				// will store 200000 triples per file
				// default is 100000
				200000
				));
		// -- splittingRDFHandler1
	}
	
	private void copyStatementRDFHandler1() throws Exception {
		// -- copyStatementRDFHandler1
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		// Performs the query. and copies resulting triples into a new graph in the repository
		// this is equivalent to an INSERT ... WHERE ... query
		Perform.on(r).construct(new ConstructSPARQLHelper(
				"CONSTRUCT { ?s a <http://www.exemple.com/Adult> } WHERE { ?s <http://www.exemple.com/age> ?age . FILTER(?age > 18) }",
				new CopyStatementRDFHandler(r, java.net.URI.create("http://www.exemple.com/anotherGraphURI"))
		));
		// -- copyStatementRDFHandler1
	}
	
	private void multipleRDFHandler1() throws Exception {
		// -- multipleRDFHandler1
		final Repository r = RepositoryBuilder.fromString("/path/to/rdf/file.ttl");
		// init 2 handlers
		CountStatementHandler csh = new CountStatementHandler();
		// Associate 2 handlers
		MultipleRDFHandler h = new MultipleRDFHandler(
				Arrays.asList(new RDFHandler[] {
						csh,
						new CopyStatementRDFHandler(r, java.net.URI.create("http://www.exemple.com/anotherGraphURI"))
				}));
		// Performs the query. Data gets copied ...
		Perform.on(r).construct(new ConstructSPARQLHelper(
				"CONSTRUCT { ?s a <http://www.exemple.com/Adult> } WHERE { ?s <http://www.exemple.com/age> ?age . FILTER(?age > 18) }",
				h
		));
		// ... and we can get the number of inserted triples
		System.out.println(csh.getStatementCount());
		// -- multipleRDFHandler1
	}	
	
	private void repositoryWriter1() throws Exception {
		// -- repositoryWriter1
		RepositoryBuilder builder = new RepositoryBuilder();
		builder.addOperation(new LoadFromFileOrDirectory("/path/to/rdf/file.ttl"));
		// apply some updates to it
		builder.addOperation(new ApplyUpdates(SPARQLUpdate.fromUpdateDirectory(new File("/path/to/directory/containing/sparql/updates"))));
		Repository r = builder.createNewRepository();
		// automatically picks up the right RDF format
		// sorts the statements autoamtically by subject, predicate, and objet
		RepositoryWriter.writeToFile("/path/to/output/file.n3", r);
		// -- repositoryWriter1
	}
	
	private void namespaces1() throws Exception {
		// -- namespaces1
		// get the usual prefix associated with the SKOS namespace
		System.out.println(Namespaces.getInstance().getPrefix("http://www.w3.org/2004/02/skos/core#"));
		// get the namespace URI associated with a prefix
		System.out.println(Namespaces.getInstance().getURI("skos"));
		// -- namespaces1
	}	

	private void usageExample1() throws Exception {
		// -- usageExample1
		// reads RDF files contained in a directory and load them in a repository
		Repository r = RepositoryBuilder.fromString("/directory/containing/rdf/files");
		// apply some SPARQL updates on it, by reading a list of *.rq files in another directory
		ApplyUpdates upd = new ApplyUpdates(SPARQLUpdate.fromUpdateDirectory(new File("/directory/containing/sparql/updates")));
		upd.execute(r);
		// perform a query on it
		Perform.on(r).select(new SelectSPARQLHelper(
				"SELECT DISTINCT ?type WHERE { [] a ?type }",
				// will print result on the console
				new DebugHandler()
		));
		// perform another query coming from a file
		Perform.on(r).select(new SelectSPARQLHelper(
				new SPARQLQueryBuilder(new File("/path/to/sparql/file.rq")),
				// will output result in a CSV file
				new CSVHandler(new PrintWriter(new File("/path/to/output/file.csv")))
		));
		// output repository content (with updates applied) in another single file
		RepositoryWriter.writeToFile("/path/to/output/file.ttl", r);
		// -- usageExample1
	}
	
	
	private static void testCustomFunction(Repository repository) throws Exception {
		Perform.on(repository).select(
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
	
	
	public static void main(String...strings) throws Exception {
		final String test = "/home/thomas/Téléchargements/NAL_Thesaurus_2013_SKOS.xml";
		// System.out.println(RDFFormat.forFileName(test.replaceAll(".xml", ".rdf"), RDFFormat.RDFXML));
		System.out.println(Rio.getParserFormatForFileName(test, RDFFormat.RDFXML));
	}
	
}
