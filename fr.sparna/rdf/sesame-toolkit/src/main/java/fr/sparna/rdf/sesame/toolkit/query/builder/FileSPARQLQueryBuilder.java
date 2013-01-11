package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.sparna.commons.io.ReadWriteTextFile;

/**
 * Reads a SPARQL query from a file.
 * 
 * @author Thomas Francart
 *
 */
public class FileSPARQLQueryBuilder implements SPARQLQueryBuilderIfc {

	protected File file;
	protected String sparql;
	
	/**
	 * Construct a FileSPARQLQueryBuilder with a reference to a File to read from.
	 * TODO : For the moment the file in read in UTF-8
	 * 
	 * @param file The file to read from
	 */
	public FileSPARQLQueryBuilder(File file) {
		super();
		this.file = file;
		
		// TODO : specify encoding
		try {
			this.sparql = ReadWriteTextFile.getContents(file, "UTF-8");
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("SPARQL file not found : "+file.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new IllegalArgumentException("File cannot be read from : "+file.getAbsolutePath(), e);
		}
	}

	@Override
	public String getSPARQL() {
		return sparql;
	}

}
