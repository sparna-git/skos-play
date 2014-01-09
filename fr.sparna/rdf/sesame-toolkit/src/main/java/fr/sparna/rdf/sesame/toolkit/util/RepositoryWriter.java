package fr.sparna.rdf.sesame.toolkit.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.SortingRDFHandler;

/**
 * Writes the content of a repository. By default, the repository will be sorted before being dumped,
 * so that the final file is neatly serialized; set sorting to "false" if you want to bypass sorting.
 * 
 * 
 * @author Thomas Francart
 *
 */
public class RepositoryWriter {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());	

	// repository to dump
	private Repository repository;

	// optionnal : sparql to execute to dump only the result of this query
	private String sparql;

	// optionnal : namespaces to handle at writing
	// we put some common namespaces by default
	@SuppressWarnings("serial")
	private Map<String, String> namespacesMap = new HashMap<String, String>() {{
		put("owl","http://www.w3.org/2002/07/owl#");
		put("rdfs","http://www.w3.org/2000/01/rdf-schema#");
		put("skos","http://www.w3.org/2004/02/skos/core#");
	}};
	
	// optionnal : sort results when writing
	private boolean sorting = true;

	// optionnal : named graph to output
	private List<URI> namedGraphsToDump = null;
	
	// optionnal : output encoding
	private String encoding;

	/**
	 * Construct a RepositoryWriter that will write the given repository.
	 * @param repository
	 */
	public RepositoryWriter(
			Repository repository
	) {
		super();
		this.repository = repository;
	}
	
	/**
	 * Construct a RepositoryWriter that will write the resul of the given SPARQL query
	 * on the given repository
	 * 
	 * @param repository
	 * @param sparql
	 */
	public RepositoryWriter(
			Repository repository,
			String sparql
	) {
		super();
		this.repository = repository;
		this.sparql = sparql;
	}
	
	/**
	 * Shortcut method that writes the content of a repository to the file denoted by the given path.
	 * Internally calls the <code>writeToFile(targetFile)</code> method.
	 * 
	 * @param targetFile	Path of the file to write to
	 * @param repository	Repository to write
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public static void writeToFile(String targetFile, Repository repository)
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		RepositoryWriter dumper = new RepositoryWriter(repository);
		dumper.writeToFile(targetFile);
	}
	
	/**
	 * Shortcut method that writes the content of a repository to the given file.
	 * Internally calls the <code>writeToFile(targetFile)</code> method.
	 * 
	 * @param targetFile	The file to write to
	 * @param repository	Repository to write
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public static void writeToFile(File targetFile, Repository repository)
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		RepositoryWriter dumper = new RepositoryWriter(repository);
		dumper.writeToFile(targetFile);
	}

	/**
	 * Shortcut method that writes the content of the given namedGraphs of a repository to the file
	 * denoted by the given path. Internally calls the <code>writeToFile</code> method.
	 * 
	 * @param targetFile	Path of the file to write to
	 * @param repository	Repository to write
	 * @param namedGraphs	List of named graphs URI to write
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public static void writeToFile(String targetFile, Repository repository, List<URI> namedGraphs)
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		RepositoryWriter dumper = new RepositoryWriter(repository);
		dumper.setNamedGraphsToDump(namedGraphs);
		dumper.writeToFile(targetFile);
	}	
	
	/**
	 * Shortcut method that writes the content of the given namedGraph of a repository to the file
	 * denoted by the given path. Internally calls the <code>writeToFile</code> method.
	 * 
	 * @param targetFile	Path of the file to write to
	 * @param repository	Repository to write
	 * @param namedGraph	URI of the named graph to write
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public static void writeToFile(String targetFile, Repository repository, URI namedGraph)
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		writeToFile(targetFile, repository, Arrays.asList(new URI[] { namedGraph }));
	}

	/**
	 * Write the repository to the file with the given path. Delegates the call to <code>writeToFile(new File(file))</code>
	 * 
	 * @param file	The path of the file to write to
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public void writeToFile(String file) 
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		writeToFile(new File(file));
	}
	
	/**
	 * Write the repository to the file with the given path. The format used for serializing is deduced from
	 * the file extension. The file is created if it does not exists.
	 * 
	 * @param file	The file to write to
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public void writeToFile(File fout)
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		log.debug("Will dump repository into file "+fout+"...");

		if (!fout.exists()) {
			// create parent dir if needed
			File parentDir = fout.getParentFile();
			if(parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}
			// create output file
			fout.createNewFile();
		}
		
		this.writeToStream(
				new FileOutputStream(fout),
				RDFFormat.forFileName(fout.getName(),RDFFormat.RDFXML)
		);
		
		log.debug("Done dumping.");
	}
	
	/**
	 * Writes the repository into a String, using the given RDFFormat, and returns that String.
	 *  
	 * @param format	The Format to use for serialisation
	 * @return			A String containing the result of the serialization of the repository
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public String writeToString(RDFFormat format)
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.writeToStream(baos, format);
		return baos.toString((this.encoding != null)?this.encoding:Charset.defaultCharset().name());
	}
	
	/**
	 * Writes the Repository into an OutputStream using the given RDFFormat.
	 * Handles sorting, namespaces, and SPARQL query execution.
	 * 
	 * @param stream	Stream to write to
	 * @param format	Format to use for serialisation
	 * 
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws IOException
	 * @throws QueryEvaluationException
	 * @throws RDFHandlerException
	 */
	public void writeToStream(OutputStream stream, RDFFormat format) 
	throws RepositoryException, MalformedQueryException, IOException, QueryEvaluationException, RDFHandlerException {
		
		// use pretty print RDF/XML handler
		try {
			RDFWriterRegistry.getInstance().add((RDFWriterFactory)this.getClass().getClassLoader().loadClass("org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriterFactory").newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// sets charset
		Charset charset = (this.encoding != null)?Charset.forName(this.encoding):Charset.defaultCharset();
		log.debug("Will use charset : "+charset.toString());
		
		// picks up the correct RDF format based on target file extension (.rdf, .n3, .ttl, etc...)
		RDFHandler writer = RDFWriterRegistry.getInstance().get(format).getWriter(new OutputStreamWriter(stream,charset));
		
		if(sorting) {
			writer = new SortingRDFHandler(writer);
		}
		
		if(this.namespacesMap != null) {
			for (Map.Entry<String, String> anEntry : this.namespacesMap.entrySet()) {
				writer.handleNamespace(anEntry.getKey(), anEntry.getValue());
			}
		}

		RepositoryConnection connection = repository.getConnection();
		try {
			if(this.sparql != null) {
				GraphQuery q = connection.prepareGraphQuery(QueryLanguage.SPARQL, this.sparql);
				// on inclut bien les statements inferred !!!
				q.setIncludeInferred(true);

				log.debug("Executing dump query...");
				long startQuery = System.currentTimeMillis();
				// ici on execute et on serialise le resultat dans la meme commande - c'est pas beau, tout ca ?!
				q.evaluate(writer);
				long endQuery = System.currentTimeMillis();
				log.debug("Done Executing dump query and serializing in "+(endQuery-startQuery)+"ms");
			} else {
				// on dump tout le repository si aucune query n'est precisee
				if(this.namedGraphsToDump == null) {
					connection.exportStatements(
							// subject - predicate - object
							null,
							null,
							null,
							// includeInferredStatements
							true,
							// writer
							writer
					);
				} else {
					connection.exportStatements(
							// subject - predicate - object
							null,
							null,
							null,
							// includeInferredStatements
							true,
							// writer
							writer,
							UriUtil.toResourceArray(this.namedGraphsToDump, repository.getValueFactory())
					);
				}
			}
		} finally {
			connection.close();
		}
	}

	public String getSparql() {
		return sparql;
	}

	public Map<String, String> getNamespacesMap() {
		return namespacesMap;
	}

	public void setNamespacesMap(Map<String, String> namespacesMap) {
		this.namespacesMap = namespacesMap;
	}

	public boolean isSorting() {
		return sorting;
	}

	public void setSorting(boolean sorting) {
		this.sorting = sorting;
	}

	public List<URI> getNamedGraphsToDump() {
		return namedGraphsToDump;
	}

	public void setNamedGraphsToDump(List<URI> namedGraphsToDump) {
		this.namedGraphsToDump = namedGraphsToDump;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}
