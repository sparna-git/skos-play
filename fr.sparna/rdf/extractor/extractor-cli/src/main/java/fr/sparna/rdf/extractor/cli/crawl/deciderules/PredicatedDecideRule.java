package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import edu.uci.ics.crawler4j.url.WebURL;

public abstract class PredicatedDecideRule extends DecideRule {
	
	protected DecideResult decision = DecideResult.ACCEPT;
	
	
    
    public PredicatedDecideRule() {
    	
    }        

    public DecideResult getDecision() {
		return decision;
	}

	public void setDecision(DecideResult decision) {
		this.decision = decision;
	}


	@Override
    protected DecideResult innerDecide(WebURL uri) {
        if (evaluate(uri)) {
            return getDecision();
        }
        return DecideResult.NONE;
    }

    protected abstract boolean evaluate(WebURL object);
}
