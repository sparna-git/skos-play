package fr.sparna.lucene;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.util.CharArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class CharArraySetFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String resourceBase;
	protected String filePrefix;
	protected String fileSuffix;
	
	
	
	protected CharArraySetFactory(
			String resourceBase,
			String filePrefix,
			String fileSuffix) {
		super();
		this.resourceBase = resourceBase;
		this.filePrefix = filePrefix;
		this.fileSuffix = fileSuffix;
	}

	public static CharArraySetFactory createStopWordsFactory() {
		return new CharArraySetFactory("fr/sparna/lucene/stopwords/", "stopwords_", ".txt");
	}
	
	public static CharArraySetFactory createContractionsFactory() {
		return new CharArraySetFactory("fr/sparna/lucene/contractions/", "contractions_", ".txt");
	}

	public CharArraySet createCharArraySet(String lang, boolean ignoreCase) {
		List<String> fileContents = new ArrayList<String>();
		
		// look for a resource with the appropriate language
		String soughtResource = this.resourceBase+this.filePrefix+lang+this.fileSuffix;
		log.debug("Looking for resource : "+soughtResource);
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(soughtResource);
		if(stream == null) {
			log.warn("Cannot find resource "+soughtResource);
		} else {
			Scanner s = new Scanner(stream, "UTF-8");
			while(s.hasNext()) {
				fileContents.add(s.next());
			}
		}
		
		log.debug("Got a CharArraySet with "+fileContents.size()+" elements.");
		return new CharArraySet(LuceneVersion.VERSION, fileContents, ignoreCase);
	}
	
	public static void main(String... args) throws Exception {
		
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
	    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("fr.sparna.rdf")).setLevel(Level.TRACE);
		
		CharArraySetFactory f = CharArraySetFactory.createStopWordsFactory();
		f.createCharArraySet("fr", true);
	}
	
}
