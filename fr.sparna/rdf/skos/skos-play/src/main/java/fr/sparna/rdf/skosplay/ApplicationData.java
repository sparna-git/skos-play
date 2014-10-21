package fr.sparna.rdf.skosplay;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;

/**
 * Everything that needs to be loaded at an application-wide level
 * 
 * @author Thomas Francart
 */
public class ApplicationData {

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
		if(thesaurusDirectory != null) {
			File thesaurusDirectoryDir = new File(thesaurusDirectory);
			if(thesaurusDirectoryDir.exists() && thesaurusDirectoryDir.list() != null) {
				String[] dataFiles = thesaurusDirectoryDir.list();
				for (String aFileName : dataFiles) {
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
		
		// store example data in application data
		this.setExampleDatas(exampleDataMap);	
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
	
}
