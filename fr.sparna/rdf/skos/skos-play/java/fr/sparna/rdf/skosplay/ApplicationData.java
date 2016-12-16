package fr.sparna.rdf.skosplay;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;

/**
 * Everything that needs to be loaded at an application-wide level
 * 
 * @author Thomas Francart
 */
public class ApplicationData {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public static final String DEFAULT_THESAURUS_LABELS_BUNDLE = "thesaurus-labels";
	
	protected Map<String, Repository> exampleDatas;

	protected String thesaurusDirectory;
	
	protected String buildVersion;	
	protected String buildTimestamp;
	
	public ApplicationData(String thesaurusDirectory) {
		super();
		this.thesaurusDirectory = thesaurusDirectory;
	
        this.init();	
	}
	
	protected void init() {
		// load example thesaurii included in the war
        Map<String, Repository> exampleDataMap = new TreeMap<String, Repository>();
		
		// try to load example files included in SkosPlayProperties.PROP_THESAURUS_DIRECTORY
		if(thesaurusDirectory != null) {
			log.debug("Pre-loading data from "+thesaurusDirectory);
			File thesaurusDirectoryDir = new File(thesaurusDirectory);
			if(thesaurusDirectoryDir.exists() && thesaurusDirectoryDir.list() != null) {
				String[] dataFiles = thesaurusDirectoryDir.list();
				for (String aFileName : dataFiles) {
					if(!aFileName.endsWith(".properties")) {
						log.debug("Pre-loading data from file/dir "+aFileName);
						File aFile = new File(thesaurusDirectoryDir, aFileName);
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
		}
		
		// store example data in application data
		this.setExampleDatas(exampleDataMap);	
	}
	
	
	/**
	 * Expose SkosPlayConfig in a getter so that it is accessible in JSP pages
	 * @return
	 */
	public SkosPlayConfig getSkosPlayConfig() {
		return SkosPlayConfig.getInstance();
	}

	public Map<String, Repository> getExampleDatas() {
		return exampleDatas;
	}

	public void setExampleDatas(Map<String, Repository> exampleDatas) {
		this.exampleDatas = exampleDatas;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}

	public String getThesaurusDirectory() {
		return thesaurusDirectory;
	}

	public void setThesaurusDirectory(String thesaurusDirectory) {
		this.thesaurusDirectory = thesaurusDirectory;
	}
	
}
