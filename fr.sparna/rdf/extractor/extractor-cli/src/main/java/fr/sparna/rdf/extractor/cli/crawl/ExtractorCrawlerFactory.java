package fr.sparna.rdf.extractor.cli.crawl;

import java.util.List;

import org.eclipse.rdf4j.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.NotifyingDataExtractorWrapper;
import fr.sparna.rdf.extractor.RepositoryManagementListener;
import fr.sparna.rdf.extractor.WebPageExtractorFactory;
import fr.sparna.rdf.extractor.cli.crawl.deciderules.DecideRule;
import fr.sparna.rdf.handler.RDFHandlerWrapperFactory;

public class ExtractorCrawlerFactory implements WebCrawlerFactory<ExtractorCrawler> {
	
	@Autowired(required=true)
	protected DecideRule decideRule;
	@Autowired(required=true)
	protected WebPageExtractorFactory extractorFactory;
	@Autowired(required=true)
	protected Repository repository;
	// this one is not required
	@Autowired(required=false)
	protected List<RDFHandlerWrapperFactory> additionnalWrapperFactories;
	protected boolean graphAware = false;
	// this one is not required
	@Autowired(required=false)
	protected List<WebURLPreProcessor> urlPreProcessors;
	
	public ExtractorCrawlerFactory() {
		super();
	}

	@Override
	public ExtractorCrawler newInstance() throws Exception {
		ExtractorCrawler extractorCrawler = new ExtractorCrawler();
		
		// sets the repository
		extractorCrawler.setRepository(this.repository);
		
		// set the extractor
		DataExtractor extractor = extractorFactory.buildExtractor();
		if(this.graphAware) {
			extractor = new NotifyingDataExtractorWrapper(extractor, new RepositoryManagementListener(repository));
		}
		extractorCrawler.setExtractor(extractor);		
		
		// set the decide rules
		extractorCrawler.setDecideRule(decideRule);
		
		// set the URL preprocessors
		extractorCrawler.setUrlPreProcessors(this.urlPreProcessors);
		
		// set the additionnal RDFHandlerWrapper factories
		extractorCrawler.setAdditionnalWrapperFactories(additionnalWrapperFactories);
		
		return extractorCrawler;
	}

	public DecideRule getDecideRule() {
		return decideRule;
	}

	public void setDecideRule(DecideRule decideRule) {
		this.decideRule = decideRule;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public WebPageExtractorFactory getExtractorFactory() {
		return extractorFactory;
	}

	public void setExtractorFactory(WebPageExtractorFactory extractorFactory) {
		this.extractorFactory = extractorFactory;
	}

	public List<RDFHandlerWrapperFactory> getAdditionnalWrapperFactories() {
		return additionnalWrapperFactories;
	}

	public void setAdditionnalWrapperFactories(List<RDFHandlerWrapperFactory> additionnalWrapperFactories) {
		this.additionnalWrapperFactories = additionnalWrapperFactories;
	}

	public boolean isGraphAware() {
		return graphAware;
	}

	public void setGraphAware(boolean graphAware) {
		this.graphAware = graphAware;
	}

	public List<WebURLPreProcessor> getUrlPreProcessors() {
		return urlPreProcessors;
	}

	public void setUrlPreProcessors(List<WebURLPreProcessor> urlPreProcessors) {
		this.urlPreProcessors = urlPreProcessors;
	}

}
