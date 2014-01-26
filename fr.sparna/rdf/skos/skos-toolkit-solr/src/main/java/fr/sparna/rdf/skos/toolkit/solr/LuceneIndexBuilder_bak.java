package fr.sparna.rdf.skos.toolkit.solr;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneIndexBuilder_bak {

	private static Version LUCENE_VERSION = Version.LUCENE_43;
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected Repository repository;

	public LuceneIndexBuilder_bak(Repository repository) {
		super();
		this.repository = repository;
	}
	
	public Directory buildIndex(String lang) throws IOException {
		// Store the index in memory
	    Directory directory = new RAMDirectory();
	    
	    // create Analyzer based on language
	    AnalyzerFactoryIfc af = new InlineAnalyzerFactory(lang);
	    Analyzer analyzer = af.createAnalyzer();
	    
	    IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
	    IndexWriter iwriter = new IndexWriter(directory, config);
	    
	    /**
	     * Structure d'un document pour construire un index des libellés :
	     * TODO
	     * 
	     */
	    
	    FieldType myType = new FieldType();
	    myType.setStored(true);
	    myType.setIndexed(true);
	    myType.setStoreTermVectors(true);
	    myType.setStoreTermVectorOffsets(true);
	    myType.setStoreTermVectorPositions(true);
	    
	    Document doc1 = new Document();
	    doc1.add(new Field("uri", "http://www.exemple.fr/doc1", TextField.TYPE_STORED));
	    doc1.add(new Field("pref", "Population trends and problems", myType));
	    doc1.add(new Field("alt", "Population growth", myType));
	    doc1.add(new Field("alt", "Population overwelm", myType));
	    doc1.add(new Field("label", "Population overwelm", myType));
	    doc1.add(new Field("label", "Population growth", myType));
	    doc1.add(new Field("label", "Population trends and problems", myType));
	    iwriter.addDocument(doc1);
	    
	    Document doc2 = new Document();
	    doc2.add(new Field("uri", "http://www.exemple.fr/doc2", TextField.TYPE_STORED));
	    doc2.add(new Field("pref", "Population of Africa", myType));
	    doc2.add(new Field("label", "Population of Africa", myType));
	    iwriter.addDocument(doc2);
	    
	    iwriter.close();
	    
	    // Now search the index:
	    DirectoryReader ireader = DirectoryReader.open(directory);
	    IndexSearcher isearcher = new IndexSearcher(ireader);
	    
	    SlowCompositeReaderWrapper atomicReader = new SlowCompositeReaderWrapper(ireader);
	    Terms t = atomicReader.terms("label");
	    TermsEnum e = t.iterator(null);
	    
	    for(BytesRef bytesRef = e.next();bytesRef != null;bytesRef = e.next()) {
	    	System.out.println("\n"+bytesRef.utf8ToString());
//	    	AttributeSource attributes = e.attributes();
//	    	Iterator<Class<? extends Attribute>> i = attributes.getAttributeClassesIterator();
//	    	while(i.hasNext()) {
//	    		Class<? extends Attribute> c = i.next();
//	    		System.out.println(c.getName());
//	    	}
	    	// CharTermAttribute termAttribute = attributes.getAttribute(CharTermAttribute.class);
	    	// System.out.println(attributes);
	    	TopDocs results = isearcher.search(new TermQuery(new Term("label", bytesRef)), 10000);
	    	for (int i = 0; i < results.scoreDocs.length; i++) {
				System.out.println(results.scoreDocs[i]);
				Document d = ireader.document(results.scoreDocs[i].doc);
				System.out.println("Got doc number "+results.scoreDocs[i].doc+" : "+d.getValues("label")[0]);
				
				Terms termVector = ireader.getTermVector(results.scoreDocs[i].doc, "pref");
				System.out.println("hasOffset ? "+termVector.hasOffsets());
				TermsEnum termVectorEnum = termVector.iterator(null);
			    
			    for(BytesRef termVectorBytesRef = termVectorEnum.next();termVectorBytesRef != null;termVectorBytesRef = termVectorEnum.next()) {
			    	System.out.println("-- "+termVectorBytesRef.utf8ToString());
			    	AttributeSource attributes = termVectorEnum.attributes();
			    	Iterator<Class<? extends Attribute>> attributesIterator = attributes.getAttributeClassesIterator();
			    	while(attributesIterator.hasNext()) {
			    		Class<? extends Attribute> c = attributesIterator.next();
			    		System.out.println(c.getName());
			    	}
			    }
			}
	    }
	    
	    
	    TokenStream stream = analyzer.tokenStream("field", new StringReader("Population trends and problem"));
	    
	    // get the CharTermAttribute from the TokenStream
	    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
	    OffsetAttribute offsetAtt = stream.addAttribute(OffsetAttribute.class);

	    List<Token> tokens = new ArrayList<Token>();
	    try {
	      stream.reset();
	    
	      // print all tokens until stream is exhausted
	      while (stream.incrementToken()) {
	    	Token aToken = new Token(new String(termAtt.toString()), offsetAtt.startOffset(), offsetAtt.endOffset());
	    	tokens.add(aToken);
	        System.out.println(termAtt.toString());
	        System.out.println(offsetAtt.startOffset());
	      }
	      
	    
	    
	      stream.end();
	    } finally {
	      stream.close();
	    }
	    
	    return directory;
	}
	
	
	public static void main(String... args) throws Exception {
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		
		LuceneIndexBuilder_bak b = new LuceneIndexBuilder_bak(r);
		b.buildIndex("en");
	}
	
}
