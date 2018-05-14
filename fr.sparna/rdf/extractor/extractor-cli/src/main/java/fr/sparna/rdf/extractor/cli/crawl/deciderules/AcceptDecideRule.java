package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import edu.uci.ics.crawler4j.url.WebURL;


public class AcceptDecideRule extends DecideRule {
	
    private static final long serialVersionUID = 3L;


    @Override
    protected DecideResult innerDecide(WebURL uri) {
        return DecideResult.ACCEPT;
    }

}
