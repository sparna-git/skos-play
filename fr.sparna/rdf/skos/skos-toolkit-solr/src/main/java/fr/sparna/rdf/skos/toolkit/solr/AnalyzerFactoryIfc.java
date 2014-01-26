package fr.sparna.rdf.skos.toolkit.solr;

import org.apache.lucene.analysis.Analyzer;

public interface AnalyzerFactoryIfc {

	public Analyzer createAnalyzer();
	
}
