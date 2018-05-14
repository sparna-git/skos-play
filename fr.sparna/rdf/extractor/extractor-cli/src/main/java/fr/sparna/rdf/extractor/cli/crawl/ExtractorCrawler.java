package fr.sparna.rdf.extractor.cli.crawl;


import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractionSourceFactory;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.DataExtractorHandlerFactory;
import fr.sparna.rdf.extractor.cli.crawl.deciderules.DecideRule;
import fr.sparna.rdf.handler.RDFHandlerWrapperFactory;

/**
 * @author Yasser Ganjisaffar
 */
public class ExtractorCrawler extends WebCrawler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private DecideRule decideRule;
	private DataExtractor extractor;
	private Repository repository;
	protected List<RDFHandlerWrapperFactory> additionnalWrapperFactories;
	protected DataExtractorHandlerFactory dataExtractorHandlerFactory = new DataExtractorHandlerFactory();
	private List<WebURLPreProcessor> urlPreProcessors;
	
	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		if(decideRule != null) {
			return decideRule.accepts(url);
		} else {
			return true;
		}
	}
	
	@Override
	protected WebURL handleUrlBeforeProcess(WebURL curURL) {
		if(urlPreProcessors != null) {
			for (WebURLPreProcessor aPreProcessor : urlPreProcessors) {
				curURL = aPreProcessor.preProcess(curURL);
			}
		}
		return curURL;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		log.debug("Docid: {}", docid);
		log.info("URL: {}", url);
		log.trace("Domain: '{}'", domain);
		log.trace("Sub-domain: '{}'", subDomain);
		log.trace("Path: '{}'", path);
		log.trace("Parent page: {}", parentUrl);
		log.trace("Anchor text: {}", anchor);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			log.trace("Text length: {}", text.length());
			log.trace("Html length: {}", html.length());
			log.trace("Number of outgoing links: {}", links.size());
		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			log.trace("Response headers:");
			for (Header header : responseHeaders) {
				log.trace("\t{}: {}", header.getName(), header.getValue());
			}
		}
		
		try {
			// create a source
			DataExtractionSourceFactory f = new DataExtractionSourceFactory();
			byte[] data;
			if (page.getParseData() instanceof HtmlParseData) {
				data = ((HtmlParseData) page.getParseData()).getHtml().getBytes();
				
				DataExtractionSource source = f.buildSource(
						SimpleValueFactory.getInstance().createIRI(url),
						data
				);
				
				// extract
				try(RepositoryConnection connection = repository.getConnection()) {
					// create handler
					RDFHandler handler = dataExtractorHandlerFactory.newHandler(connection, source.getDocumentIri());
					if(this.additionnalWrapperFactories != null) {
						for (RDFHandlerWrapperFactory aWrapperFactory : additionnalWrapperFactories) {
							handler = aWrapperFactory.createRdfHandlerWrapper(handler);
						}
					}							
					
					synchronized(extractor) {
						extractor.extract(
								source,
								handler
						);
					}
				}
				
			} else {
				// data = ((TextParseData) page.getParseData()).getTextContent().getBytes();
				log.info("Can't extract from "+page.getWebURL().getURL()+" since it is not HTML.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("=============");
	}


	public DecideRule getDecideRule() {
		return decideRule;
	}

	public void setDecideRule(DecideRule decideRule) {
		this.decideRule = decideRule;
	}

	public DataExtractor getExtractor() {
		return extractor;
	}

	public void setExtractor(DataExtractor extractor) {
		this.extractor = extractor;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public List<RDFHandlerWrapperFactory> getAdditionnalWrapperFactories() {
		return additionnalWrapperFactories;
	}

	public void setAdditionnalWrapperFactories(List<RDFHandlerWrapperFactory> additionnalWrapperFactories) {
		this.additionnalWrapperFactories = additionnalWrapperFactories;
	}

	public DataExtractorHandlerFactory getDataExtractorHandlerFactory() {
		return dataExtractorHandlerFactory;
	}

	public void setDataExtractorHandlerFactory(DataExtractorHandlerFactory dataExtractorHandlerFactory) {
		this.dataExtractorHandlerFactory = dataExtractorHandlerFactory;
	}
	
	public List<WebURLPreProcessor> getUrlPreProcessors() {
		return urlPreProcessors;
	}

	public void setUrlPreProcessors(List<WebURLPreProcessor> urlPreProcessors) {
		this.urlPreProcessors = urlPreProcessors;
	}

	public static void main(String...args) {
		String test = "http://www.univ-tours.fr/l-universite/deliberations-et-decisions-246294.kjsp?RF=1319617190202&RH=1334240483623";
		WebURL wurl = new WebURL();
		wurl.setURL(test);
		ExtractorCrawler me = new ExtractorCrawler();
		System.out.println(me.handleUrlBeforeProcess(wurl).getURL());
	}

}
