package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import fr.sparna.rdf.extractor.cli.crawl.Seeds;

public class OnSeedsDomainDecideRule extends OnDomainDecideRule {

    protected Seeds seeds;	
    
    
	public OnSeedsDomainDecideRule(Seeds seeds) {
		super();
		this.seeds = seeds;
		this.setDomains(seeds.getDomains());
	}

	public Seeds getSeeds() {
		return seeds;
	}

}
