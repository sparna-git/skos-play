package fr.sparna.rdf.skos.toolkit;

import java.util.Arrays;
import java.util.List;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderList;

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

	public static List<SPARQLQueryBuilder> getRulesetLite() {
		return SPARQLQueryBuilderList.fromResources(SKOSRules.class, LITE_RULESET);
	}
}
