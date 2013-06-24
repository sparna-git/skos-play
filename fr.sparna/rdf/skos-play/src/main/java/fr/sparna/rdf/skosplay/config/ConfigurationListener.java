package fr.sparna.rdf.skosplay.config;

import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class ConfigurationListener
 *
 */
public class ConfigurationListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public ConfigurationListener() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent s) {
        DefaultConfiguration cfg = new DefaultConfiguration(new Properties());
        Configuration.setDefault(cfg);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
