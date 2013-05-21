package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import fr.sparna.commons.io.InputStreamUtil;
import fr.sparna.commons.io.ReadWriteTextFile;

public class SPARQLQueryBuilder implements SPARQLQueryBuilderIfc {

	protected String sparql;
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a String holding the query
	 * 
	 * @param sparql The String holding the query.
	 */
	public SPARQLQueryBuilder(String sparql) {
		super();
		this.sparql = sparql;
	}
	
	/**
	 * Construct an SPARQLQueryBuilderBase by reading from the stream with the default encoding.
	 * 
	 * @param stream the stream to read from
	 */
	public SPARQLQueryBuilder(InputStream stream) {
		this(stream, Charset.defaultCharset().name());
	}
	
	/**
	 * Construct an SPARQLQueryBuilderBase by reading from the stream with the specified charset
	 * 
	 * @param stream the stream to read from
	 */
	public SPARQLQueryBuilder(InputStream stream, String charset) {
		super();
		// read from the stream
		this.sparql = InputStreamUtil.readToString(stream, charset);
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a reference to a File to read from and a charset
	 * 
	 * @param file 		The file to read from
	 * @param charset	Charset to use to read the file
	 */
	public SPARQLQueryBuilder(File file, String charset) {
		super();
		
		try {
			this.sparql = ReadWriteTextFile.getContents(file, charset);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("SPARQL file not found : "+file.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new IllegalArgumentException("File cannot be read from : "+file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a reference to a File to read from in the default charset
	 * 
	 * @param file 		The file to read from
	 */
	public SPARQLQueryBuilder(File file) {
		this(file, Charset.defaultCharset().name());
	}
	
	/**
	 * Construct a SPARQLQueryBuilderBase with a resource from the classpath
	 * 
	 * @param owner 	The class used to load the resource
	 * @param resource	The resource reference on the classpath relative to the owner
	 */
    public SPARQLQueryBuilder(Class<?> owner, String resource) {
    	if (owner == null) {
            throw new IllegalArgumentException("owner");
        }
    	
    	// Load SPARQL query definition
        InputStream src = owner.getResourceAsStream(resource);
        if (src == null) {
            throw new RuntimeException(new FileNotFoundException(resource));
        }
        
		// read from the stream
		// TODO : specify encoding ?
		this.sparql = InputStreamUtil.readToString(src);
    }
	
    /**
	 * Construct a SPARQLQueryBuilderBase with a resource from the classpath
	 * 
	 * @param owner 	The resource owner used to load the resource
	 * @param resource	The resource reference on the classpath relative to the owner
	 */
    public SPARQLQueryBuilder(Object owner, String resource) {
        this((Class<?>)owner.getClass(), resource);
    }
	
	@Override
	public String getSPARQL() {
		return sparql;
	}

}
