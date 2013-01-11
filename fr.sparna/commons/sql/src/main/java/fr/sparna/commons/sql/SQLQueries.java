package fr.sparna.commons.sql;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * A utility class to load SQL queries from properties file.
 *
 * @author thomas
 */
public class SQLQueries
{
    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------

    /** Default SQL queries definition file name. */
    public final static String QUERIES_DEFAULT_FILE = "sql-queries.properties";

    //-------------------------------------------------------------------------
    // Class member definitions
    //-------------------------------------------------------------------------

    private final static Logger log = LoggerFactory.getLogger(SQLQueries.class);
    
    //-------------------------------------------------------------------------
    // Instance members
    //-------------------------------------------------------------------------

    /** The defined SQL queries. */
    private final Map<String,String> queries;

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Loads SQL queries from the
     * {@link SQLQueries#QUERIES_DEFAULT_FILE default queries file}
     * on behalf of the specified object.
     * 
     * <p>
     * This method is a shortcut for
     * <code>new SQLQueries(owner.getClass())</code>.</p>
     * @param  owner   the object owning the queries, used to resolve
     *                 the properties file path in the classpath.
     *
     * @throws NullPointerException if <code>owner</code> is
     *         <code>null</code>.
     * @throws RuntimeException if any error occurred accessing the
     *         properties file or loading the queries.
     *
     * @see    #SQLQueries(String, Object)
     * @see    #QUERIES_DEFAULT_FILE
     */
    public SQLQueries(Object owner) {
        this(QUERIES_DEFAULT_FILE, owner.getClass());
    }

    /**
     * Loads SQL queries from the
     * {@link SQLQueries#QUERIES_DEFAULT_FILE default queries file}
     * on behalf of the specified class.
     * @param  owner   the class owning the queries, used to resolve
     *                 the properties file path in the classpath.
     *
     * @throws NullPointerException if <code>owner</code> is
     *         <code>null</code>.
     * @throws RuntimeException if any error occurred accessing the
     *         properties file or loading the queries.
     *
     * @see    #SQLQueries(String, Class)
     * @see    #QUERIES_DEFAULT_FILE
     */
    public SQLQueries(Class<?> owner) {
        this(QUERIES_DEFAULT_FILE, owner);
    }

    /**
     * Loads SQL queries on behalf of the specified object.
     * <p>
     * This method is a shortcut for
     * <code>new SQLQueries(path, owner.getClass())</code>.</p>
     * @param  path    the path to the properties file containing the
     *                 SQL queries.
     * @param  owner   the object owning the queries, used to resolve
     *                 the properties file path in the classpath.
     *
     * @throws NullPointerException if either <code>path</code> or
     *         <code>owner</code> is <code>null</code>.
     * @throws RuntimeException if any error occurred accessing the
     *         properties file or loading the queries.
     *
     * @see    #SQLQueries(String, Class)
     */
    public SQLQueries(String path, Object owner) {
        this(path, owner.getClass());
    }

    /**
     * Loads SQL queries on behalf of the specified class.
     * @param  path    the path to the properties file containing the
     *                 SQL queries.
     * @param  owner   the class owning the queries, used to resolve
     *                 the properties file path in the classpath.
     *
     * @throws NullPointerException if either <code>path</code> or
     *         <code>owner</code> is <code>null</code>.
     * @throws RuntimeException if any error occurred accessing the
     *         properties file or loading the queries.
     */
    public SQLQueries(String path, Class<?> owner) {
        if ((path == null) || (path.length() == 0)) {
            throw new IllegalArgumentException("path");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner");
        }
        try {
            // Load SQL query definitions
            InputStream src = owner.getResourceAsStream(path);
            if (src== null) {
                throw new RuntimeException(new FileNotFoundException(path));
            }
            Properties p = new Properties();
            p.load(src);

            // Build queries
            Map<String,String> queries = new TreeMap<String,String>();
            for (Object o : p.keySet()) {
                String key = (String)o;
                if ((key != null) && (key.length() != 0)) {
                    String query = p.getProperty(key);
                    if ((query != null) && (query.length() != 0)) {
                        log.trace("Loaded query: {} -> {}", key, query);
                        queries.put(key, query);
                    }
                    else {
                        log.warn("No SQL query defined for {}", key);
                    }
                }
                // Else: ignore...
            }
            this.queries = queries;
            
            if (this.queries.isEmpty()) {
                log.warn("No query definitions found in \"{}\"", path);
            }
            else {
                log.debug("Loaded "+Integer.valueOf(this.queries.size())+" queries from \""+path+"\" for "+owner);
            }
        }
        catch (Exception e) {
            log.error("SQL queries definitions ("+path+") loading failed: "+e);
            throw new RuntimeException(e);
        }
    }

    //-------------------------------------------------------------------------
    // Specific implementation
    //-------------------------------------------------------------------------

    /**
     * Returns a SQL query.
     * @param  key   the name of the query
     *
     * @return the SQL query string.
     * @throws IllegalArgumentException if <code>key</code> is unknown.
     */
    public String get(String key) {
        if (! this.queries.containsKey(key)) {
            throw new IllegalArgumentException(key + " unknown");
        }
        return this.queries.get(key);
    }

}
