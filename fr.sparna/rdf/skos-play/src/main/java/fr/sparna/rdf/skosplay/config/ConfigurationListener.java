package fr.sparna.rdf.skosplay.config;

import java.io.IOException;
import java.io.InputStream;
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
        
        // register timestamp in servlet context
        String timestamp = "unknown";
        InputStream in = getClass().getClassLoader().getResourceAsStream("version.properties");
        if(in != null) {
        	Properties props = new Properties();
        	try {
				props.load(in);
				timestamp = props.getProperty("build.timestamp");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        s.getServletContext().setAttribute("buildTimestamp", timestamp);
        
        
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
