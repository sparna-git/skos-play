package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.url.WebURL;


public class OnDomainDecideRule extends PredicatedDecideRule {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8046212450832101986L;
	
	private Set<String> domains;

    public Set<String> getDomains() {
		return domains;
	}

	public void setDomains(Set<String> domains) {
		this.domains = domains;
	}

	@Override
    protected boolean evaluate(WebURL uri) {
		String withSubDomains = (uri.getSubDomain() != null && !uri.getSubDomain().equals(""))?uri.getSubDomain()+"."+uri.getDomain():uri.getDomain();
		log.trace(this.getClass().getSimpleName()+" - "+"Testing if "+withSubDomains+" is in "+this.domains);
		if(domains.contains(withSubDomains)) {
        	return true;
        } else {
        	return false;
        }
    }

}
