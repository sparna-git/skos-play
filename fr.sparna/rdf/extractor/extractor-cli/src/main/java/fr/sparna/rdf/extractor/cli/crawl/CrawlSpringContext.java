package fr.sparna.rdf.extractor.cli.crawl;

import org.eclipse.rdf4j.repository.Repository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.uci.ics.crawler4j.crawler.CrawlController;

public class CrawlSpringContext {

	protected String configPath;
	private transient ApplicationContext context;
	
	@Autowired(required=true)
	protected CrawlController crawlController;
	
	@Autowired(required=true)
	protected ExtractorCrawlerFactory extractorCrawlerFactory;
	
	@Autowired(required=true)
	protected RepositoryFactoryFromString repositoryFactory;
	
	@Autowired(required=true)
	protected Repository repository;
	
	@Autowired(required=true)
	protected Seeds seeds;
	
	public CrawlSpringContext(String configPath) {
		super();
		this.configPath = configPath;
		this.initContext();
	}
	
	private void initContext() {
		// parse the config
		try {
			context = new ClassPathXmlApplicationContext(configPath);
		} catch (BeansException e) {
			// on essaie avec le chemin vers un fichier
			try {
				context = new FileSystemXmlApplicationContext(this.configPath);
			} catch (BeansException e1) {
				throw new RuntimeException("Unable to initialize crawl config from "+configPath, e1);
			}
		}
		
		// Autowire me
		AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
		factory.autowireBeanProperties( this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true );
		
		// register seeds on the crawlController
		seeds.register(crawlController);
	}
	
	public CrawlController getCrawlController() {		
		return crawlController;
	}
	
	public ExtractorCrawlerFactory getExtractorCrawlerFactory() {
		return extractorCrawlerFactory;
	}

	public RepositoryFactoryFromString getRepositoryFactory() {
		return repositoryFactory;
	}

	public void setCrawlController(CrawlController crawlController) {
		this.crawlController = crawlController;
	}

	public void setExtractorCrawlerFactory(ExtractorCrawlerFactory extractorCrawlerFactory) {
		this.extractorCrawlerFactory = extractorCrawlerFactory;
	}

	public void setRepositoryFactory(RepositoryFactoryFromString repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Seeds getSeeds() {
		return seeds;
	}
	
}
