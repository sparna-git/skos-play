package fr.sparna.rdf.extractor.cli.crawl.deciderules;


/**
 * The decision of a DecideRule.
 * 
 * @author pjack
 */
public enum DecideResult {

    /** Indicates the URI was accepted. */
    ACCEPT, 
    
    /** Indicates the URI was neither accepted nor rejected. */
    NONE, 
    
    /** Indicates the URI was rejected. */
    REJECT;

    
    public static DecideResult invert(DecideResult result) {
        switch (result) {
            case ACCEPT:
                return REJECT;
            case REJECT:
                return ACCEPT;
            default:
                return result;
        }
    }
}
