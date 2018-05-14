 package fr.sparna.rdf.extractor.cli.crawl;

import com.beust.jcommander.Parameter;

public class ArgumentsCrawl {

	@Parameter(
			names = { "-c", "--config" },
			description = "Path to the file containing the crawl and extraction configuration",
			required = true
	)
	private String config;
	
	@Parameter(
			names = { "-t", "--threads" },
			description = "Number of crawl threads",
			required = false
	)
	private int threads = 2;

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
	
}
