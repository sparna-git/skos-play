package fr.sparna.rdf.sesame.toolkit.repository;


import org.openrdf.repository.Repository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;

public class AutoDetectRepositoryFactory implements RepositoryFactoryIfc {

	protected String aParameter;
	
	public AutoDetectRepositoryFactory(String aParameter) {
		super();
		this.aParameter = aParameter;
	}

	@Override
	public Repository createNewRepository() throws RepositoryFactoryException {
		// 1. Try with Spring
		RepositoryFactoryIfc factory =  new SpringRepositoryFactory(this.aParameter);
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
				factory = new StringRepositoryFactory(this.aParameter);
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
	}

}
