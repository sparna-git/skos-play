package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.url.WebURL;

public class DecideRuleSequence extends DecideRule {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private static final long serialVersionUID = 3L;
    
      
    protected List<DecideRule> rules = new ArrayList<DecideRule>();
    
    /**
     * If enabled, log decisions to file named logs/{spring-bean-id}.log. Format
     * is: [timestamp] [decisive-rule-num] [decisive-rule-class] [decision]
     * [uri]
     * 
     * Relies on Spring Lifecycle to initialize the log. Only top-level
     * beans get the Lifecycle treatment from Spring, so bean must be top-level
     * for logToFile to work. (This is true of other modules that support
     * logToFile, and anything else that uses Lifecycle, as well.)
     */
    protected boolean logToFile = false;
    
    protected transient Logger fileLogger = null;
    
    protected String beanName;
    

    public DecideResult innerDecide(WebURL uri) {
        DecideRule decisiveRule = null;
        int decisiveRuleNumber = -1;
        DecideResult result = DecideResult.NONE;
        List<DecideRule> rules = getRules();
        int max = rules.size();
        
        for (int i = 0; i < max; i++) {
            DecideRule rule = rules.get(i);
            DecideResult r = rule.decisionFor(uri);
            if (r != result) {
                if (log.isTraceEnabled()) {
                    log.trace("DecideRule #" + i + " " + 
                            rule.getClass().getName() + " returned " + r + " for url: " + uri);
                }
                if (r != DecideResult.NONE) {
                    result = r;
                    decisiveRule = rule;
                    decisiveRuleNumber = i;
                }
            }
        }

        if (fileLogger != null) {
            fileLogger.info(decisiveRuleNumber + " " + decisiveRule.getClass().getSimpleName() + " " + result + " " + uri);
        }

        return result;
    }

	public boolean isLogToFile() {
		return logToFile;
	}

	public void setLogToFile(boolean logToFile) {
		this.logToFile = logToFile;
	}

	public List<DecideRule> getRules() {
        return rules;
    }
    
    public void setRules(List<DecideRule> rules) {
        this.rules = rules;
    }

}
