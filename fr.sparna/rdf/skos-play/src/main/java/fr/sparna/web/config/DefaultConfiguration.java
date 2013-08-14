package fr.sparna.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.lang.VersatileProperties;


public class DefaultConfiguration extends Configuration {

    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------
    
    //-------------------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------------------

    private final static Logger log = LoggerFactory.getLogger(DefaultConfiguration.class);

    //-------------------------------------------------------------------------
    // Instance members
    //-------------------------------------------------------------------------

    /**
     * The configuration data as a properties object. It is initialized
     * to an empty properties map to allow {@link #getProperty(String)}
     * to always resolve system properties and environment variables,
     * even when the loading of the DataLift configuration fails.
     */
    private VersatileProperties props = new VersatileProperties();
    
    /**
     * The property to read to get the application home directory
     */
    private final String applicationHomeProperty;
    
    /**
     * The name of the file containing application properties
     */
    private final String applicationPropertiesFile;

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    /**
     * Default constructor.
     * @param  props   the application runtime environment.
     *
     * @throws TechnicalException if any error occurred loading or
     *         parsing the configuration data.
     */
    public DefaultConfiguration(Properties props, String applicationHomeProperty, String applicationPropertiesFile) {
    	this.applicationHomeProperty = applicationHomeProperty;
    	this.applicationPropertiesFile = applicationPropertiesFile;
        this.props = this.loadConfiguration(props);
    }

    //-------------------------------------------------------------------------
    // Configuration contract support
    //-------------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public String getProperty(String key) {
        return this.props.getProperty(key);
    }

    /** {@inheritDoc} */
    @Override
    public String getProperty(String key, String def) {
        return this.props.getProperty(key, def);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<String> getPropertyNames() {
        Collection<String> names = new HashSet<String>();
        for (Object o : this.props.keySet()) {
            if (o instanceof String) {
                names.add((String)o);
            }
        }
        return Collections.unmodifiableCollection(names);
    }

    /** {@inheritDoc} */
    @Override
    public Properties loadProperties(String path, Class<?> owner)
    throws IOException {
        if ((path == null) || (path.length() == 0)) {
            throw new IllegalArgumentException("path");
        }
        return this.loadFromClasspath(path, this.props, owner);
    }

    //-------------------------------------------------------------------------
    // Specific implementation
    //-------------------------------------------------------------------------

    /**
     * Shuts down this configuration, freeing all attached resources
     * and closing all repository connections.
     */
    /* package */ void shutdown() {
        
    }

    /**
     * Loads the application configuration, reading the configuration file
     * path from the specified properties.
     * @param  props   the bootstrap properties.
     *
     * @throws TechnicalException if any error occurred accessing or
     *         parsing the DataLift configuration file.
     */
    private VersatileProperties loadConfiguration(Properties props) {
    	VersatileProperties config = null;
        try {
            String cfgFilePath = this.applicationPropertiesFile;
            String homePath = this.getProperty(this.applicationHomeProperty);
            if (homePath != null) {
                File f = new File(new File(homePath), "conf/" + cfgFilePath);
                if ((f.isFile()) && (f.canRead())) {
                    cfgFilePath = f.getPath();
                }
            }
            // Load configuration.
            config = this.loadFromClasspath(cfgFilePath, props, null);
            log.info("Configuration loaded from {}", cfgFilePath);
        }
        catch (IOException e) {
            RuntimeException error = new RuntimeException("Configuration file not found : "+this.applicationPropertiesFile, e);
            log.error(error.getMessage());
            throw error;
        }
        return config;
    }

    /**
     * Initializes this configuration.
     */
    /* package */ void init() {

    }

    /**
     * Loads the specified Java {@link Properties properties file} on
     * behalf of the specified class.
     * @param  filePath   the properties file path, relative to the
     *                    classloader of the owner class
     * @param  defaults   the (optional) parent properties.
     * @param  owner      the class on behalf of which loading the file
     *                    or <code>null</code> to use the default
     *                    class loader.
     *
     * @return the properties, loaded from the file.
     * @throws IOException if any error occurred accessing the file or
     *         parsing the property values.
     */
    private VersatileProperties loadFromClasspath(String filePath,
                                        Properties defaults, Class<?> owner)
                                                            throws IOException {
    	VersatileProperties p = (defaults != null)?
                                        new VersatileProperties(defaults):
                                        new VersatileProperties();
        InputStream in = null;
        try {
            if (owner == null) {
                // No owner specified. => Use default classloader.
                owner = this.getClass();
            }
            in = owner.getClassLoader().getResourceAsStream(filePath);
            if (in == null) {
                in = new FileInputStream(filePath);
            }
            p.load(in);
        }
        finally {
            if (in != null) {
                try { in.close(); } catch (Exception e) { /* Ignore... */ }
            }
        }
        return p;
    }

}
