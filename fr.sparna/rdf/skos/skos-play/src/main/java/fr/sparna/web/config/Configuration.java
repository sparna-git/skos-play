package fr.sparna.web.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public abstract class Configuration {

    //-------------------------------------------------------------------------
    // Class members
    //-------------------------------------------------------------------------

    private static Configuration defaultConfiguration = null;

    //-------------------------------------------------------------------------
    // Configuration contract definition
    //-------------------------------------------------------------------------

    /**
     * Return the value of a configuration property as a string.
     * @param  key   the name of the configuration property.
     *
     * @return the value of the configuration property or
     *         <code>null</code> if <code>key</code> is unknown or its
     *         value is not a string.
     */
    abstract public String getProperty(String key);

    /**
     * Return the value of a configuration property as a string.
     * @param  key   the name of the configuration property.
     * @param  def   a default value for the property.
     *
     * @return the value of the configuration property or
     *         <code>def</code> if <code>key</code> is unknown or its
     *         value is not a string.
     */
    abstract public String getProperty(String key, String def);

    /**
     * Returns the names of all properties in this {@link Configuration}
     * or an empty collection if there are no properties.
     *
     * @return the names of all properties.
     */
    abstract public Collection<String> getPropertyNames();

    /**
     * Loads a third-party properties file.
     * @param  path    the path of the properties file, absolute or
     *                 relative to the DataLift configuration file.
     * @param  owner   the class on behalf of which loading the file
     *                 or <code>null</code> if the file is not part
     *                 of a DataLift module.
     *
     * @return the loaded properties.
     * @throws IOException if <code>path</code> does not exist, can
     *         not be accessed or is not a valid properties file.
     */
    abstract public Properties loadProperties(String path, Class<?> owner)
                                                            throws IOException;

    //-------------------------------------------------------------------------
    // Singleton access and installation methods
    //-------------------------------------------------------------------------

    /**
     * Returns the current application configuration.
     * @return the current configuration.
     */
    public static Configuration getDefault() {
        return defaultConfiguration;
    }

    /**
     * Sets the current application configuration.
     * @param  configuration   the configuration to install as default.
     *
     * @throws IllegalArgumentException if another configuration has
     *         already been installed.
     */
    public static void setDefault(Configuration configuration) {
        if (defaultConfiguration != null) {
            throw new IllegalStateException();
        }
        defaultConfiguration = configuration;
    }

}
