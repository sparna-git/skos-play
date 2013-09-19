package fr.sparna.rdf.sesame.toolkit.repository;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

public class VirtuosoReflectionRepositoryFactory implements RepositoryFactoryIfc {

	protected String jdbcURL;
	protected String login;
	protected String password;	
	
	public VirtuosoReflectionRepositoryFactory(
			String jdbcURL,
			String login,
			String password
	) {
		super();
		System.out.println("Connecting to Virtuoso at "+jdbcURL+" with login "+login+" and password with "+password.length()+" characters");
		this.jdbcURL = jdbcURL;
		this.login = login;
		this.password = password;
	}
	
	public VirtuosoReflectionRepositoryFactory(
			String fullURL
	) {
		if(fullURL.lastIndexOf("?") == -1) {
			throw new InvalidParameterException("Unable to find param marker '?' in '"+fullURL+"'");
		}
		String jdbcUrlString = fullURL.split("\\?")[0];
		String paramsString = fullURL.split("\\?")[1];
		String[] params = paramsString.split("&");
		String login = null;
		String password = null;
		for (String aParam : params) {
			if(aParam.startsWith("login")) {
				login = aParam.substring(aParam.indexOf("=")+1);
			} else if(aParam.startsWith("password")) {
				password = aParam.substring(aParam.indexOf("=")+1);
			}
		}
		
		if(login == null || password == null) {
			throw new InvalidParameterException("Cannot retrieve login and password from '"+fullURL+"'");
		}
		
		System.out.println("Connecting to Virtuoso at "+jdbcUrlString+" with login "+login+" and password with "+password.length()+" characters");
		this.jdbcURL = jdbcUrlString;
		this.login = login;
		this.password = password;
	}

	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		Repository repository;
		try {
			Class virtuosoRepositoryClass = Class.forName("virtuoso.sesame2.driver.VirtuosoRepository");
			Constructor virtuosoRepositoryClassConstructor = virtuosoRepositoryClass.getConstructor(String.class, String.class, String.class);
			repository = (Repository)virtuosoRepositoryClassConstructor.newInstance(this.jdbcURL, this.login, this.password);
		} catch (Exception e) {
			throw new RepositoryFactoryException("Unable to instantiate VirtuosoRepository class, make sure the Virtuoso dependency is in your classpath",e);
		}
		try {
			repository.initialize();
		} catch (RepositoryException e) {
			throw new RepositoryFactoryException(e);
		}
		return repository;
	}

}
