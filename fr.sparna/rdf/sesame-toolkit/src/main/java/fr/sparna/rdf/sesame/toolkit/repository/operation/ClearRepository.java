package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Clears the given repository, or only given named graphs in the repository.
 * 
 * @author Thomas Francart
 */
public class ClearRepository implements RepositoryOperationIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<String> namedGraphsToClear;	

	public ClearRepository() {
		super();
	}

	public ClearRepository(String aNamedGraphToClean) {
		super();
		this.setNamedGraphToClear(aNamedGraphToClean);
	}

	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		try {
			log.info("RepositoryCleaner cleaning...");
			if(this.namedGraphsToClear == null || this.namedGraphsToClear.size() == 0) {
				repository.getConnection().clear();
			} else {
				repository.getConnection().clear(toResourceArray(this.namedGraphsToClear, repository.getValueFactory()));
			}			
			log.info("RepositoryCleaner committing...");
			repository.getConnection().commit();
			log.info("RepositoryCleaner successfully cleaned context : '"+this.namedGraphsToClear+"'");
		} catch (RepositoryException e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	/**
	 * Turns a list of Strings into a list of Resource objects using the given ValueFactory.
	 * 
	 * @deprecated use UriUtil instead
	 * @param uris
	 * @param factory
	 * @return an array of ressources
	 */
	protected static Resource[] toResourceArray(List<String> uris, ValueFactory factory) {
		List<Resource> result = new ArrayList<Resource>();
		if(uris != null) {
			for (String aString : uris) {
				result.add(factory.createURI(aString));
			}
		}
		return result.toArray(new Resource[] {});
	}

	/**
	 * Convenience method to set only one named graph URI to clear. 
	 * @param namedGraphToClear
	 */
	public void setNamedGraphToClear(String namedGraphToClear) {
		setNamedGraphsToClear(Arrays.asList(new String[] {namedGraphToClear}));
	}
	
	public List<String> getNamedGraphsToClear() {
		return namedGraphsToClear;
	}

	public void setNamedGraphsToClear(List<String> namedGraphsToClear) {
		this.namedGraphsToClear = namedGraphsToClear;
	}	

}
