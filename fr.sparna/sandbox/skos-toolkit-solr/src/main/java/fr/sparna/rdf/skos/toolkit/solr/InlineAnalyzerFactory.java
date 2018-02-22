package fr.sparna.rdf.skos.toolkit.solr;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class InlineAnalyzerFactory implements AnalyzerFactoryIfc {

	protected String lang;
	
	public InlineAnalyzerFactory(String lang) {
		super();
		this.lang = lang;
	}

	@Override
	public Analyzer createAnalyzer() {
		Analyzer analyzer = new Analyzer() {
			
			@Override
			protected TokenStreamComponents createComponents(String arg0, Reader reader) {
				Tokenizer source = new StandardTokenizer(LuceneVersion.VERSION, reader);
				StopFilter stopFilter = new StopFilter(LuceneVersion.VERSION, source, CharArraySetFactory.createStopWordsFactory().createCharArraySet(lang, true));
				return new TokenStreamComponents(source, stopFilter);
			}
			
		};
		return analyzer;
	}

}
