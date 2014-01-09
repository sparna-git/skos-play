package fr.sparna.rdf.sesame.toolkit.query;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderIfc;

public final class SparqlQueryList {

	public static List<SparqlQuery> fromBuilderList(List<SparqlQueryBuilderIfc> builders) {
		if(builders == null) {
			return null;
		}
		
		ArrayList<SparqlQuery> result = new ArrayList<SparqlQuery>();
		for (SparqlQueryBuilderIfc aBuilder : builders) {
			result.add(new SparqlQuery(aBuilder));
		}
		return result;
	}
	
}
