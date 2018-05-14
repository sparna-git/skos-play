package fr.sparna.rdf.skos.toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReader;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReaderFactory;

public class SKOSRules {

	public static List<String> LITE_RULESET = Arrays.asList(new String[] { 
			"rules/inferlite/S8.rq",
			"rules/inferlite/S7.rq",
			"rules/inferlite/S25.rq",
			"rules/inferlite/S22.rq",
			"rules/inferlite/S23.rq",
			"rules/inferlite/S26.rq",
			"rules/inferlite/S24-broader.rq",
			"rules/inferlite/S24-narrower.rq",
			"rules/inferlite/X1.rq",
	});
	
	public static List<String> OWL2SKOS_RULESET = Arrays.asList(new String[] { 
			"owl2skos/01-skos-Concept.ru",
			"owl2skos/02-skos-prefLabel.ru",
			"owl2skos/03-skos-broader-skos-narrower.ru",
			"owl2skos/04-skos-inScheme.ru",
			"owl2skos/05-skos-ConceptScheme.ru",
			"owl2skos/06-skos-definition.ru",
			"owl2skos/07-skos-prefLabel-default.ru",
	});
	
	public static List<String> SKOSXL2SKOS_RULESET = Arrays.asList(new String[] { 
			"skosxl2skos/S55.ru",
			"skosxl2skos/S56.ru",
			"skosxl2skos/S57.ru",
			// special handling of skos:definition from VocBench
			"skosxl2skos/reified-definition.ru",
	});
	
	public static List<String> SKOSXL2SKOS_CLEAN_RULESET = Arrays.asList(new String[] { 
			"skosxl2skos/clean-S55.ru",
			"skosxl2skos/clean-S56.ru",
			"skosxl2skos/clean-S57.ru",
			"skosxl2skos/clean-reified-definition.ru",
	});
	
	public static List<String> SKOS2SKOSXL_URI_RULESET = Arrays.asList(new String[] { 
			"skos2skosxl/S55-S56-S57-URIs.ru"
	});
	
	public static List<String> SKOS2SKOSXL_BNODE_RULESET = Arrays.asList(new String[] { 
			"skos2skosxl/S55-S56-S57-bnodes.ru"
	});
	
	public static List<String> SKOS2SKOSXL_NOTES_URI_RULESET = Arrays.asList(new String[] { 
			"skos2skosxl/S16-URIs.ru"
	});
	
	public static List<String> SKOS2SKOSXL_NOTES_BNODE_RULESET = Arrays.asList(new String[] { 
			"skos2skosxl/S16-bnodes.ru"
	});

	public static List<SimpleQueryReader> getRulesetLite() {
		return SimpleQueryReaderFactory.fromResources(SKOSRules.class, LITE_RULESET);
	}
	
	public static List<SimpleQueryReader> getOWL2SKOSRuleset() {
		return SimpleQueryReaderFactory.fromResources(SKOSRules.class, OWL2SKOS_RULESET);
	}
	
	public static List<SimpleQueryReader> getSkosXl2SkosRuleset() {
		return getSkosXl2SkosRuleset(false);
	}
	
	public static List<SimpleQueryReader> getSkosXl2SkosRuleset(boolean cleanXl) {
		if(cleanXl) {
			List<String> rules = new ArrayList<String>(SKOSXL2SKOS_RULESET);
			rules.addAll(SKOSXL2SKOS_CLEAN_RULESET);
			return SimpleQueryReaderFactory.fromResources(SKOSRules.class, rules);
		} else {
			return SimpleQueryReaderFactory.fromResources(SKOSRules.class, SKOSXL2SKOS_RULESET);
		}
	}
	
	public static List<SimpleQueryReader> getSkos2SkosXlRuleset(boolean useBnodes) {
		if(useBnodes) {
			return SimpleQueryReaderFactory.fromResources(SKOSRules.class, SKOS2SKOSXL_BNODE_RULESET);
		} else {
			return SimpleQueryReaderFactory.fromResources(SKOSRules.class, SKOS2SKOSXL_URI_RULESET);
		}
	}
	
	public static List<SimpleQueryReader> getSkos2SkosXlNotesRuleset(boolean useBnodes) {
		if(useBnodes) {
			return SimpleQueryReaderFactory.fromResources(SKOSRules.class, SKOS2SKOSXL_NOTES_BNODE_RULESET);
		} else {
			return SimpleQueryReaderFactory.fromResources(SKOSRules.class, SKOS2SKOSXL_NOTES_URI_RULESET);
		}
	}

}
