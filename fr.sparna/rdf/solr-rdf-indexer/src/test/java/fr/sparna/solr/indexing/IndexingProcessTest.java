package fr.sparna.solr.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.sparna.solr.indexing.base.DebugIndexingConsumer;
import fr.sparna.solr.indexing.base.MockIndexingProcessor;
import fr.sparna.solr.indexing.base.MockIndexingSourceIfc;

public class IndexingProcessTest {

	@Test
	public void testIndexing() throws Exception {
		IndexingSourceIfc<String> source = new MockIndexingSourceIfc(Arrays.asList(new String[] {"http://www.ex.fr/1", "http://www.ex.fr/2"}), "uri");
		List<IndexingProcessorIfc<String>> processors = new ArrayList<IndexingProcessorIfc<String>>();
		processors.add(new MockIndexingProcessor<String>("processedField"));
		List<IndexingConsumerIfc> consumers = Arrays.asList(new IndexingConsumerIfc[] {new DebugIndexingConsumer()});
		
		IndexingProcess<String> p = new IndexingProcess<String>(source, processors, consumers);
		p.start();
	}
	
}
