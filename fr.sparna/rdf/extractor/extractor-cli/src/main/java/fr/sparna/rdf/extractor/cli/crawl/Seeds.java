package fr.sparna.rdf.extractor.cli.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlController;

public class Seeds {

	private Set<String> urls = new HashSet<>();
	private String path;
	
	public Seeds(String path) throws IOException {
		super();
		this.path = path;
		this.init();
	}
	
	private void init() throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	this.getUrls().add(line);
		    }
		}
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}
	
	public void register(CrawlController controller) {
		for (String aSeed : urls) {
			controller.addSeed(aSeed);
		}
	}
	
	public Set<String> getDomains() {
		HashSet<String> result = new HashSet<String>();
		for (String aSeed : urls) {
			try {
				URL url = new URL(aSeed);
				result.add(url.getHost());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "Seeds [urls=" + urls + "]";
	}
	
}
