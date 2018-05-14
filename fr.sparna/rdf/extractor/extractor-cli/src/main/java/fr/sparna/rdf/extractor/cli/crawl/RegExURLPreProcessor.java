package fr.sparna.rdf.extractor.cli.crawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.url.WebURL;

public class RegExURLPreProcessor implements WebURLPreProcessor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String regex;
	protected String replacement;
	
	private Pattern compiledPattern;
	
	public RegExURLPreProcessor(String regex, String replacement) {
		super();
		this.regex = regex;
		this.replacement = replacement;
	}

	@Override
	public WebURL preProcess(WebURL curURL) {
		if(compiledPattern == null) {
			this.compiledPattern = Pattern.compile(regex);
		}
		Matcher matcher = this.compiledPattern.matcher(curURL.getURL());
		if(matcher.find()) {
			String newUrl = matcher.replaceAll(replacement);
			log.debug("Changed original URL "+curURL.getURL()+" to "+newUrl);
			curURL.setURL(newUrl);
		}
		return curURL;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

}
