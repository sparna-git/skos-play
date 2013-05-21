package fr.sparna.rdf.sesame.toolkit.repository;


import java.util.Collections;
import java.util.List;

import org.openrdf.repository.Repository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;

import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;

public class AutoDetectRepositoryFactory implements RepositoryFactoryIfc {

	protected List<String> aParameter;

	public AutoDetectRepositoryFactory(List<String> aParameter) {
		super();
		this.aParameter = aParameter;
	}
	
	public AutoDetectRepositoryFactory(String aParameter) {
		this(Collections.singletonList(aParameter));
	}

	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		if(aParameter.size() == 1) {
			// 1. Try with Spring
			RepositoryFactoryIfc factory =  new SpringRepositoryFactory(this.aParameter.get(0));
			try {
				return factory.createNewRepository();
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
					factory = new StringRepositoryFactory(this.aParameter.get(0));
					try {
						return factory.createNewRepository();
					} catch (RepositoryFactoryException e2) {
						throw e2;
					}
				} else {
					// erreur pendant l'init de Spring : "vraie" exception Spring
					throw new RepositoryFactoryException(e1);
				}
			}
		} else {
			// plus d'un parametre, on considere que ce sont des fichiers ou des repertoires
			RepositoryBuilder builder = new RepositoryBuilder(new LocalMemoryRepositoryFactory());
			builder.addOperation(new LoadFromFileOrDirectory(aParameter));
			return builder.createNewRepository();
		}
	}

}
