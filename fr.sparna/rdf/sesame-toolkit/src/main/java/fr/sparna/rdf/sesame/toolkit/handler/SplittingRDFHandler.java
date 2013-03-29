package fr.sparna.rdf.sesame.toolkit.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

/**
 * Output the triples in file chunks, constituted of chunksize triples. The
 * "endRDF" method of the delegating handler is called when chunksize triples is reached 
 * 
 * @author Thomas Francart
 *
 */
public class SplittingRDFHandler implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private String baseOutputFileName;
	private int chunkSize;
	
	private CountStatementHandler countHandler;
	private int currentChunkStartOffset = 0;
	
	private File currentChunkFile;
	private RDFWriter currentWriter;
	
	/**
	 * Constructs a SplittingRDFHandler with a base file name and a specified chunk size
	 * 
	 * @param baseOutputFileName	The file name pattern to use
	 * @param chunkSize				The number of triples to include in each chunk
	 */
	public SplittingRDFHandler(String baseOutputFileName, int chunkSize) {
		super();
		this.baseOutputFileName = baseOutputFileName;
		countHandler = new CountStatementHandler();
		this.chunkSize = chunkSize;
	}
	
	/**
	 * Constructs a SplittingRDFHandler with a base file name and a default chunk size of 100000
	 * 
	 * @param baseOutputFileName	The file name pattern to use
	 */
	public SplittingRDFHandler(String baseOutputFileName) {
		this(baseOutputFileName, 100000);
	}

	/**
	 * Calls <code>startRDF()</code> on the delegate handler
	 */
	@Override
	public void startRDF() throws RDFHandlerException {
		// notify our count handler of start
		this.countHandler.startRDF();
		
		// start this chunk
		this.startChunk();
	}

	/**
	 * 
	 */
	@Override
	public void endRDF() throws RDFHandlerException {		
		// notify our count handler of stop
		this.countHandler.endRDF();
		
		// end this chunk
		this.endChunk();		
	}

	/**
	 * Calls <code>handleComment</code> on the delegate handler
	 */
	@Override
	public void handleComment(String c) throws RDFHandlerException {
		this.currentWriter.handleComment(c);
	}

	/**
	 * Calls <code>handleNamespace</code> on the delegate handler
	 */
	@Override
	public void handleNamespace(String key, String value)
	throws RDFHandlerException {
		this.currentWriter.handleNamespace(key, value);
	}

	/**
	 * Stores the statement internally for later sorting
	 */
	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		// ecrire le statement
		this.currentWriter.handleStatement(s);
		// compter le statement
		this.countHandler.handleStatement(s);
		// si on est a la fin d'un chunk...
		// TODO : handle triples so that resources, if sorted, are not separated in 2 files
		if((countHandler.getStatementCount() % this.chunkSize) == 0) {
			// on termine le handler existant
			this.endChunk();
			// on demarre un nouveau handler
			this.startChunk();
		}
	}
	
	private void startChunk() throws RDFHandlerException {	
		// keep track of current chunk start offset
		this.currentChunkStartOffset = this.countHandler.getStatementCount();
		log.debug("SplittingRDFHandler starting chunk "+this.currentChunkStartOffset);
		
		// inits a new writer
		this.initWriter();
		
		// notify it of start
		this.currentWriter.startRDF();
		// TODO : notify it of namespaces
	}
	
	private void endChunk() throws RDFHandlerException {
		log.debug("SplittingRDFHandler ending chunk "+this.currentChunkStartOffset);
		
		// notifier le writer de la fin
		this.currentWriter.endRDF();
		
		// construire le nom du fichier final
		String finalFileName = 
				this.baseOutputFileName.substring(0, this.baseOutputFileName.lastIndexOf('.'))
				+
				"-"
				+
				this.currentChunkStartOffset
				+
				"-"
				+
				this.countHandler.getStatementCount()
				+
				this.baseOutputFileName.substring(this.baseOutputFileName.lastIndexOf('.'), this.baseOutputFileName.length())
		;
		
		// renommer le fichier temporaire avec son nom final
		this.currentChunkFile.renameTo(new File(finalFileName));
	}	
	
	private void initWriter() throws RDFHandlerException {
		// create output file
		this.currentChunkFile = new File(this.baseOutputFileName);
		if(!currentChunkFile.exists()) {
			try {
				currentChunkFile.createNewFile();
			} catch (IOException e) {
				throw new RDFHandlerException(e);
			}
		}
		// create output stream
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(currentChunkFile);
		} catch (FileNotFoundException e) {
			// should never happen
			throw new RDFHandlerException(e);
		}
		// recreate RDF writer
		this.currentWriter = RDFWriterRegistry.getInstance().get(
				RDFFormat.forFileName(this.baseOutputFileName, RDFFormat.RDFXML)).getWriter(outputStream)
		;
	}
	
	/**
	 * Simple test harness
	 * args[0] : an RDF file or directory to split
	 * args[1] : output file pattern
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromString(args[0]);
		r.getConnection().export(new SplittingRDFHandler(args[1]));
	}
}
