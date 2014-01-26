package fr.sparna.rdf.skos.toolkit.solr;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SKOSIndexBuilder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected AnalyzerFactoryIfc analyzerFactory;
	
	public SKOSIndexBuilder(AnalyzerFactoryIfc analyzerFactory) {
		super();
		this.analyzerFactory = analyzerFactory;
	}
	
	public Directory index(Repository repository) throws IOException {
		// Store the index in memory
	    Directory directory = new RAMDirectory();
	    
	    // create Analyzer based on language
	    Analyzer analyzer = this.analyzerFactory.createAnalyzer();
	    
	    IndexWriterConfig config = new IndexWriterConfig(LuceneVersion.VERSION, analyzer);
	    IndexWriter iwriter = new IndexWriter(directory, config);
	    
	    iwriter.close();
	    
	    return directory;
	}
	
	
	public static void main(String... args) throws Exception {
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		
		AnalyzerFactoryIfc af = new InlineAnalyzerFactory("en");
		
		SKOSIndexBuilder b = new SKOSIndexBuilder(af);
		b.index(r);
	}
	
}
