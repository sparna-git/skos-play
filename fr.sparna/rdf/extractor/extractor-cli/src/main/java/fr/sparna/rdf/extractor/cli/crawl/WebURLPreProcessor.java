package fr.sparna.rdf.extractor.cli.crawl;

import edu.uci.ics.crawler4j.url.WebURL;

public interface WebURLPreProcessor {

	public WebURL preProcess(WebURL curURL);
	
}
