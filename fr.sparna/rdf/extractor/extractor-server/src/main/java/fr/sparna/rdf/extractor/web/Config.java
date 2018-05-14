package fr.sparna.rdf.extractor.web;

public class Config {

	private static Config instance;
	
	private String repository;
	
	private Config() {
		
	}
	
	public static Config getInstance() {
		if(instance == null) {
			Config.instance = new Config();
		}
		return instance;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}
	
	
	
}
