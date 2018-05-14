package fr.sparna.rdf.extractor.cli.crawl.deciderules;


import java.io.Serializable;

import edu.uci.ics.crawler4j.url.WebURL;

public abstract class DecideRule implements Serializable {
	
    protected String comment = "";
    protected boolean enabled = true;
    
    public DecideRule() {

    }
    
    /**
     * Returns true if this rule accpets the given URL.
     * @param uri
     * @return
     */
    public boolean accepts(WebURL uri) {
        return DecideResult.ACCEPT == decisionFor(uri);
    }
    
    /**
     * Returns the decision for the given URL.
     * @param uri
     * @return
     */
    public DecideResult decisionFor(WebURL uri) {
        if (!isEnabled()) {
            return DecideResult.NONE;
        }
        
        return innerDecide(uri);
    }
    
    
    protected abstract DecideResult innerDecide(WebURL uri);
    
    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
}
