package fr.sparna.rdf.extractor.cli.crawl;

import java.io.FileOutputStream;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import fr.sparna.rdf.extractor.cli.ExtractorCliCommandIfc;

public class Crawl implements ExtractorCliCommandIfc {

	@Override
	public void execute(Object args) throws Exception {
		ArgumentsCrawl a = (ArgumentsCrawl)args;

		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		int numberOfCrawlers = a.getThreads();

		CrawlSpringContext ctx = new CrawlSpringContext(a.getConfig());
		CrawlController controller = ctx.getCrawlController();
		WebCrawlerFactory<ExtractorCrawler> webCrawlerFactory = ctx.getExtractorCrawlerFactory();
		RepositoryFactoryFromString repositoryFactory = ctx.getRepositoryFactory();
		
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(webCrawlerFactory, numberOfCrawlers);
		
		if(repositoryFactory.isFileRepository()) {
			// dump the content of the repo in a file
			RDFWriter writer = Rio.createWriter(
					Rio.getParserFormatForFileName(repositoryFactory.getRepositoryString()).orElse(RDFFormat.RDFXML),
					new FileOutputStream(repositoryFactory.getRepositoryString())
			);
			
			try(RepositoryConnection c = ctx.getRepository().getConnection()) {
				c.export(writer);
			}
		}
	}

}
