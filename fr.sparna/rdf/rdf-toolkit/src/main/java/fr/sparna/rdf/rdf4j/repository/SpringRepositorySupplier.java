package fr.sparna.rdf.rdf4j.repository;

import java.util.function.Supplier;

import org.eclipse.rdf4j.repository.Repository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;

public class SpringRepositorySupplier implements Supplier<Repository> {

	private static String DEFAULT_SPRING_FILE = "repository-config.xml";
	private static String DEFAULT_BEAN_ID = "RepositoryBuilder";
	
	protected String springFile = DEFAULT_SPRING_FILE;
	protected String beanId = DEFAULT_BEAN_ID;
	
	public SpringRepositorySupplier(String springFile, String beanId) {
		super();
		this.springFile = springFile;
		this.beanId = beanId;
	}
	
	public SpringRepositorySupplier(String springFile) {
		this(springFile, DEFAULT_BEAN_ID);
	}
	
	public SpringRepositorySupplier() {
		this(DEFAULT_SPRING_FILE, DEFAULT_BEAN_ID);
	}	
	
	@Override
	public Repository get() {
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
		RepositoryBuilder delegate = (RepositoryBuilder)appContext.getBean(this.beanId);
		return delegate.get();
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
