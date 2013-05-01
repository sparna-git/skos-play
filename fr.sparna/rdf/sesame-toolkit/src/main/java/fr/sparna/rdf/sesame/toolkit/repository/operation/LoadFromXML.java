package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.sparna.commons.io.ReadWriteTextFile;
import fr.sparna.commons.xml.ReadWriteXML;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryConnectionDoorman;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;

/**
 * Uses an XSL stylesheet on an XML file (or set of files in a directory) to transform it into RDF, and loads the RDF into a repository.
 * A file or a directory or a list of files can be given as input.
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromXML extends AbstractLoadOperation implements RepositoryOperationIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	// list of files or directory to process
	private List<String> xmlFiles;
	// path to XSL file or XSL classpath ressource
	protected String xslSource;

	// loaded once before start
	private InputStream xslInputStream = null;

	// number of processed documents
	private int inputDocumentCounter = 0;
	
	public LoadFromXML(List<String> xmlFiles, String xslSource) {
		super();
		this.xmlFiles = xmlFiles;
		this.xslSource = xslSource;
	}
	
	public LoadFromXML(List<String> xmlFiles, File xslSource) {
		this(xmlFiles, xslSource.getAbsolutePath());
	}

	public LoadFromXML(List<String> xmlFiles, InputStream xslInputStream){
		super();
		this.xmlFiles = xmlFiles;
		this.xslInputStream = xslInputStream;
	}

	/**
	 * Convenience constructor to construct a LoadFromXML from a single String referring
	 * to the XML file or directory and a String referring to the XSL file or classpath
	 * resource to apply
	 * 
	 * @param xmlFile		XML file or directory to process
	 * @param xslSource		XSL File path or classpath resource to use
	 */
	public LoadFromXML(String xmlFile, String xslSource){
		this(new ArrayList<String>(Arrays.asList(xmlFile)), xslSource);
	}

	/**
	 * Convenience constructor to construct a LoadFromXML from a single String referring
	 * to the XML file or directory and an InputStream referring to the XSL file to apply.
	 * 
	 * @param xmlFile			XML File or directory to process
	 * @param xslInputStream	XSL File to use
	 */
	public LoadFromXML(String xmlFile, InputStream xslInputStream){
		this(new ArrayList<String>(Arrays.asList(xmlFile)), xslInputStream);
	}
	
	/**
	 * Convenience constructor to construct a LoadFromXML from a single String referring
	 * to the XML file or directory and a File referring to the XSL to apply.
	 * 
	 * @param xmlFile	XML file or directory to process
	 * @param xslFile	XSL file to use
	 */
	public LoadFromXML(String xmlFile, File xslFile){
		this(new ArrayList<String>(Arrays.asList(xmlFile)), xslFile);
	}

	@Override
	public void execute(Repository repository) throws RepositoryOperationException {
		try {
			// load XSL file
			// TODO : checks if file exists
			// if xslInputStream hasn't already been initialized
			if (xslInputStream == null) {
				log.debug("Load XSL from " + this.xslSource);
				xslInputStream = null;
				File xslFile = new File(this.xslSource);
				if (xslFile.exists()) {
					xslInputStream = new FileInputStream(this.xslSource);
				} else {
					// try to load it from classpath
					log.debug("File does not exist, try from the classpath");
					xslInputStream = this.getClass().getClassLoader().getResourceAsStream(this.xslSource);
				}

				if (xslInputStream == null) {
					throw new IOException("Cannot find XSL " + this.xslSource + " in a file or in the classpath");
				} else {
					log.debug("Found on the classpath");
				}
			}

			log.debug("Compiling stylesheet...");
			Transformer transformer = TransformerFactory.newInstance()
					// NOTE : on passe bien par un DOMSource et pas une StreamSource pour bien gerer
					// le flag de namespaceAware
					.newTransformer(new DOMSource(ReadWriteXML.read(xslInputStream)));
			log.debug("Done");

			log.debug("Applying transformation ...");
			RepositoryConnection connection = repository.getConnection();
			try {
				connection.setAutoCommit(false);
				for(String file : (List<String>)xmlFiles){
					doApplyXSLOnAFile(transformer, connection, new File(file));
				}
				connection.commit();
			} finally {
				RepositoryConnectionDoorman.closeQuietly(connection);
			}
			log.debug("Done applying transformation");

			if(log.isTraceEnabled()) {
				log.trace("Exporting final result into "+this.xmlFiles+".rdf"+"...");
				RepositoryWriter.writeToFile(this.xmlFiles+".rdf", repository);
				log.trace("Done");
			}

		} catch (Exception e) {
			// TODO : on ramasse toutes les exceptions ici, ce n'est pas terrible...
			throw new RepositoryOperationException("Error when init RDFFromXML repository", e);
		}
	}

	protected void doApplyXSLOnAFile(
			Transformer transformer,
			RepositoryConnection connection,
			File fileOrDirectory
	) throws RepositoryOperationException {
		if (fileOrDirectory.isDirectory()) {
			log.debug("Processing directory " + fileOrDirectory.getAbsolutePath() + "...");
			// we have a directory, iterate on its content
			File[] files = fileOrDirectory.listFiles();
			for (File file : files) {
				doApplyXSLOnAFile(transformer, connection, file);
			}
		} else {
			try {
				// prepare ByteArrayOutputStream
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				// apply transformation
				log.trace("Applying transformation on "+fileOrDirectory.getAbsolutePath()+"...");
				Document inputXML = fr.sparna.commons.xml.ReadWriteXML.read(new FileInputStream(fileOrDirectory));
				transformer.transform(
						new DOMSource(inputXML),
						new StreamResult(baos)
						);

				if(log.isTraceEnabled()) {
					// Output in file to debug
					File debugFile = new File(fileOrDirectory+"xsl-result.xml");
					if(!debugFile.exists()) {
						debugFile.createNewFile();
					}

					// TODO : set charset
					log.trace("Debugging XSL result in "+fileOrDirectory+"xsl-result.xml");
					ReadWriteTextFile.setContents(debugFile, baos.toString("UTF-8"), "UTF-8");
				}

				// load RDF in repository
				// TODO : charset ?
				connection.add(
						new ByteArrayInputStream(baos.toByteArray()),
						this.defaultNamespace,
						RDFFormat.RDFXML,
						(this.targetGraph != null)?connection.getValueFactory().createURI(this.targetGraph.toString()):null
						);

				// increment counter
				this.inputDocumentCounter++;
				if(this.inputDocumentCounter % 1000 == 0) {
					log.debug("Processed "+this.inputDocumentCounter+" input files");
				}
			} catch (FileNotFoundException e) {
				// will never happen
				e.printStackTrace();
			} catch (Exception e) {
				throw new RepositoryOperationException("Error while applying XSL on file "+fileOrDirectory.getAbsolutePath(), e);
			}
		}
	}

	public List<String> getXmlFiles() {
		return xmlFiles;
	}

	public void setXmlFiles(List<String> xmlFiles) {
		this.xmlFiles = xmlFiles;
	}

	public String getXslSource() {
		return xslSource;
	}

	public void setXslSource(String xslSource) {
		this.xslSource = xslSource;
	}

}