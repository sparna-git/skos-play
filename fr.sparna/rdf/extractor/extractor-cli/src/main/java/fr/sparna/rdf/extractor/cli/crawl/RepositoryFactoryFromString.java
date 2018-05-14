package fr.sparna.rdf.extractor.cli.crawl;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class RepositoryFactoryFromString {
	
	protected String repositoryString;

	public RepositoryFactoryFromString(String repositoryString) {
		super();
		this.repositoryString = repositoryString;
	}

	public Repository newRepository() {
		// initialize our repository
		if(this.repositoryString.startsWith("http")) {
			Repository repo = new HTTPRepository(this.repositoryString);
			repo.initialize();			
			return repo;
		} else {
			Repository repo = new SailRepository(new MemoryStore());
			repo.initialize();			
			return repo;
		}
		
	}
	
	public boolean isFileRepository() {
		return !isRemoteRespotiory();
	}
	
	public boolean isRemoteRespotiory() {
		return repositoryString.startsWith("http");
	}

	public String getRepositoryString() {
		return repositoryString;
	}

	public void setRepositoryString(String repositoryString) {
		this.repositoryString = repositoryString;
	}
	
}
