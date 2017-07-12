package fr.sparna.rdf.rdf4j.toolkit.query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.sparna.rdf.rdf4j.toolkit.util.Namespaces;

public class PrefixPrepender {

	public static String prependPrefixes(String sparql) {
		// find all prefixed qNames in the query
		Pattern p = Pattern.compile("([a-zA-Z1-9-_]*):[a-zA-Z1-9]*");
		Matcher m = p.matcher(sparql);
		
		List<String> prefixes = new ArrayList<String>();
		while(m.find()) {
			String prefix = m.group(1);
			if(!prefixes.contains(prefix)) {
				prefixes.add(prefix);
			}
		}
		
		// System.out.println(prefixes);
		
		StringBuffer queryBuf = new StringBuffer();
		prefixes.stream().forEach(aPrefix -> {
			String iri = Namespaces.getInstance().getURI(aPrefix);
			if(iri != null) {
				queryBuf.append("PREFIX "+aPrefix+": <"+iri+">"+"\n");
			}
		});
		queryBuf.append(sparql);
		
		return queryBuf.toString();
	}
	
}
