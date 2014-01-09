package fr.sparna.rdf.sesame.toolkit.repository;

import org.openrdf.repository.Repository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringRepositoryFactory implements RepositoryFactoryIfc {

	private static String DEFAULT_SPRING_FILE = "repository-config.xml";
	private static String DEFAULT_BEAN_ID = "RepositoryBuilder";
	
	protected String springFile = DEFAULT_SPRING_FILE;
	protected String beanId = DEFAULT_BEAN_ID;
	
	public SpringRepositoryFactory(String springFile, String beanId) {
		super();
		this.springFile = springFile;
		this.beanId = beanId;
	}
	
	public SpringRepositoryFactory(String springFile) {
		this(springFile, DEFAULT_BEAN_ID);
	}
	
	public SpringRepositoryFactory() {
		this(DEFAULT_SPRING_FILE, DEFAULT_BEAN_ID);
	}	
	
	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		ApplicationContext appContext;
		try {
			appContext = new ClassPathXmlApplicationContext(this.springFile);
		} catch (BeansException e) {
			// on essaie avec le chemin vers un fichier
			try {
				appContext = new FileSystemXmlApplicationContext(this.springFile);
			} catch (BeansException e1) {
				throw e1;
			}
		}
		RepositoryFactoryIfc delegate = (RepositoryFactoryIfc)appContext.getBean(this.beanId);
		return delegate.createNewRepository();
	}
	
	public String getSpringFile() {
		return springFile;
	}

	public void setSpringFile(String springFile) {
		this.springFile = springFile;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

}
