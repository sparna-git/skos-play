package fr.sparna.solr.indexing.base;

import java.util.Iterator;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.solr.indexing.ConsumingException;
import fr.sparna.solr.indexing.IndexingConsumerIfc;

public class DebugIndexingConsumer extends BaseIndexingComponent implements IndexingConsumerIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void consume(SolrInputDocument doc) throws ConsumingException {
		log.debug("Consuming doc : ");
		for (String aFieldName : doc.getFieldNames()) {
			log.debug("  "+aFieldName+" : ");
			for (Iterator i = doc.getFieldValues(aFieldName).iterator(); i.hasNext();) {
				Object o = (Object) i.next();
				log.debug("    "+o);
			}
			
		}
	}

	@Override
	public void commit() throws ConsumingException {
		log.debug("commit() called");
	}

	@Override
	public void rollback() throws ConsumingException {
		log.debug("roolback() called");
	}	
	
}
