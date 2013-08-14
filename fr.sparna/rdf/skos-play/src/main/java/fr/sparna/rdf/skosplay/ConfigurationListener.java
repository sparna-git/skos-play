package fr.sparna.rdf.skosplay;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;
import fr.sparna.web.config.Configuration;
import fr.sparna.web.config.DefaultConfiguration;

/**
 * Application Lifecycle Listener implementation class ConfigurationListener
 *
 */
public class ConfigurationListener implements ServletContextListener {

    /** The application working directory system property/environment variable. */
    public final static String SKOSPLAY_HOME = "skosplay.home";
    
    /** The application properties configuration file. */
    public final static String CONFIGURATION_FILE = "skosplay-application.properties";
	
    /**
     * Default constructor. 
     */
    public ConfigurationListener() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent s) {
    	// create configuration
        DefaultConfiguration cfg = new DefaultConfiguration(new Properties(), SKOSPLAY_HOME, CONFIGURATION_FILE);
        Configuration.setDefault(cfg);
        
        // create ApplicationData object and register it
        ApplicationData appData = new ApplicationData();
        
        // retrieve application compilation timestamp
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
        appData.setBuildTimestamp(timestamp);
        
        // load example thesaurii included in the war
        Map<String, Repository> exampleDataMap = new HashMap<String, Repository>();
        
		List<String> exampleDatas = Arrays.asList(new String[]{
				"data/unesco/unescothes.ttl",
				"data/w/matieres.rdf",
				"data/nyt/nyt-descriptors.ttl"
		});
		for (String aData : exampleDatas) {
			try {
				RepositoryBuilder builder = new RepositoryBuilder();
				builder.addOperation(new LoadFromStream(this, aData));
				exampleDataMap.put(aData, builder.createNewRepository());				
			} catch (RepositoryFactoryException e) {
				e.printStackTrace();
			}
		}
		
		// try to load example files included in SkosPlayProperties.PROP_THESAURUS_DIRECTORY
		String thesaurusDirectoryPath = Configuration.getDefault().getProperty(SkosPlayProperties.PROP_THESAURUS_DIRECTORY);
		if(thesaurusDirectoryPath != null) {
			File thesaurusDirectory = new File(thesaurusDirectoryPath);
			if(thesaurusDirectory.exists() && thesaurusDirectory.list() != null) {
				String[] dataFiles = thesaurusDirectory.list();
				for (String aFileName : dataFiles) {
					File aFile = new File(thesaurusDirectory, aFileName);
					try {
						RepositoryBuilder builder = new RepositoryBuilder();
						builder.addOperation(new LoadFromFileOrDirectory(aFile.getAbsolutePath()));
						// use the file name as a key
						exampleDataMap.put(aFile.getName(), builder.createNewRepository());
					} catch (RepositoryFactoryException e) {
						e.printStackTrace();
					}
				}
			}
		}			
		
		// store example data in application data
		appData.setExampleDatas(exampleDataMap);

		// register application data in servlet context
		appData.register(s.getServletContext());
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
