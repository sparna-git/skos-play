package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.url.WebURL;

public class MatchesListRegexDecideRule extends PredicatedDecideRule {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected boolean logicalOr = true;
	protected List<String> regexes;
	private transient List<Pattern> patterns;
	
	@PostConstruct
	public void init() {
		patterns = new ArrayList<Pattern>();
		for (String aRegex : regexes) {
			patterns.add(Pattern.compile(aRegex));
		}
	}
	
	@Override
	protected boolean evaluate(WebURL uri) {
        if(regexes.size()==0){
            return false;
        }

        String str = uri.toString();

        for (Pattern p: patterns) {
            boolean matches = p.matcher(str).matches();

            if (log.isTraceEnabled()) {
            	log.trace("Tested '" + str + "' match with regex '" +
                    p.pattern() + " and result was " + matches);
            }
            
            if(matches){
                if(logicalOr){
                    // OR based and we just got a match, done!
                    log.trace("Matched: " + str);
                    return true;
                }
            } else {
                if(logicalOr == false){
                    // AND based and we just found a non-match, done!
                    return false;
                }
            }
        }
        
        if (logicalOr) {
            return false;
        } else {
            return true;
        }
	}

	public boolean isLogicalOr() {
		return logicalOr;
	}

	public void setLogicalOr(boolean logicalOr) {
		this.logicalOr = logicalOr;
	}

	public List<String> getRegexes() {
		return regexes;
	}

	public void setRegexes(List<String> regexes) {
		this.regexes = regexes;
	}
	
	
}
