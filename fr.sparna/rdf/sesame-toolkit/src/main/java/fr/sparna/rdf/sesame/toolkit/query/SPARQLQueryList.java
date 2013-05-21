package fr.sparna.rdf.sesame.toolkit.query;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilderIfc;

public final class SPARQLQueryList {

	public static List<SPARQLQuery> fromBuilderList(List<SPARQLQueryBuilderIfc> builders) {
		if(builders == null) {
			return null;
		}
		
		ArrayList<SPARQLQuery> result = new ArrayList<SPARQLQuery>();
		for (SPARQLQueryBuilderIfc aBuilder : builders) {
			result.add(new SPARQLQuery(aBuilder));
		}
		return result;
	}
	
}
