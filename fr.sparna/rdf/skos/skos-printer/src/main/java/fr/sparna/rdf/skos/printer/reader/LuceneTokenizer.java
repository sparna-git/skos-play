package fr.sparna.rdf.skos.printer.reader;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.ElisionFilter;

import fr.sparna.lucene.CharArraySetFactory;
import fr.sparna.lucene.LuceneUtil;
import fr.sparna.lucene.LuceneVersion;


public class LuceneTokenizer implements IndexTokenizerIfc {

	protected Analyzer analyzer;
	protected String lang;
	
	public LuceneTokenizer(final String lang) {
		this.lang = lang;
		analyzer = new Analyzer() {
			
			@Override
			protected TokenStreamComponents createComponents(String arg0, Reader reader) {
				Tokenizer source = new StandardTokenizer(LuceneVersion.VERSION, reader);
				StopFilter stopFilter = new StopFilter(LuceneVersion.VERSION, source, CharArraySetFactory.createStopWordsFactory().createCharArraySet(lang, true));
				ElisionFilter elisionFilter = new ElisionFilter(stopFilter, CharArraySetFactory.createContractionsFactory().createCharArraySet(lang, true));
				return new TokenStreamComponents(source, elisionFilter);
			}
			
		};
	}
	
	@Override
	public String[] tokenize(String s) {
		return LuceneUtil.tokenizeString(this.analyzer, s).toArray(new String[] {});
	}

	
	
}
