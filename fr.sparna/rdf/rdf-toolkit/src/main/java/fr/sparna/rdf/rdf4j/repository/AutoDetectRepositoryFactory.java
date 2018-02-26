package fr.sparna.rdf.rdf4j.repository;


import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;

import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;

public class AutoDetectRepositoryFactory implements Supplier<Repository> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<String> aParameter;

	public AutoDetectRepositoryFactory(List<String> aParameter) {
		super();
		this.aParameter = aParameter;
	}
	
	public AutoDetectRepositoryFactory(String aParameter) {
		this(Collections.singletonList(aParameter));
	}

	@Override
	public Repository get() {
		if(aParameter.size() == 1) {
			// 1. Try with Spring
			log.debug("Attempt to build a Repository by parsing a Spring file...");
			Supplier<Repository> supplier =  new SpringRepositorySupplier(this.aParameter.get(0));
			try {
				return supplier.get();
				// a noter que si une exception intervient dans la factory
				// configurée dans Spring, on sortira ici en exception, puisqu'on distingue les
				// BeansException de la RepositoryFactoryException
			} catch (BeansException e1) {
				if(
						e1 instanceof XmlBeanDefinitionStoreException
						||
						e1 instanceof BeanDefinitionStoreException
						) {
					// la lecture avec une config Spring a echoue
					// 2. on essaie avec un RP qui lit soit depuis une URL, soit depuis un fichier
					log.debug("Attempt with Spring file failed ("+e1.getMessage()+"), exception indicating that the input param was not a Sping file, try by interpreting a String...");
					RepositoryBuilderFactory builderFactory = new RepositoryBuilderFactory(this.aParameter.get(0));
					return builderFactory.get().get();
				} else {
					// erreur pendant l'init de Spring : "vraie" exception Spring
					// on essaie quand meme, au cas ou, en lisant depuis un fichier ou une URL
					log.debug("Attempt with Spring file failed ("+e1.getMessage()+"), but param seemed to be a Spring file.");
					log.debug("Will attempt by interpreting a String anyway...");
					RepositoryBuilderFactory builderFactory = new RepositoryBuilderFactory(this.aParameter.get(0));
					return builderFactory.get().get();				
				}
			}
		} else {
			// plus d'un parametre, on considere que ce sont des fichiers ou des repertoires
			RepositoryBuilderFactory builderFactory = new RepositoryBuilderFactory(aParameter);
			return builderFactory.get().get();
		}
	}

}
