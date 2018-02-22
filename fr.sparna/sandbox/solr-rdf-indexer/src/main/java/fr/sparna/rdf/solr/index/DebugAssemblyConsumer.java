package fr.sparna.rdf.solr.index;

import java.util.Iterator;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.AssemblyConsumer;
import fr.sparna.assembly.ConsumeException;
import fr.sparna.assembly.base.BaseAssemblyLineComponent;

/**
 * Prints the content of the consumed SolrInputDocument in the log stream.
 * @author Thomas Francart
 */
public class DebugAssemblyConsumer extends BaseAssemblyLineComponent<SolrInputDocument> implements AssemblyConsumer<SolrInputDocument> {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void consume(Assembly<SolrInputDocument> idxable) throws ConsumeException {
		log.debug("Consuming doc : ");
		for (String aFieldName : idxable.getDocument().getFieldNames()) {
			log.debug("  "+aFieldName+" : ");
			for (Iterator i = idxable.getDocument().getFieldValues(aFieldName).iterator(); i.hasNext();) {
				Object o = (Object) i.next();
				log.debug("    "+o);
			}
		}
	}

	@Override
	public void commit() throws ConsumeException {
		log.debug("commit() called");
	}

	@Override
	public void rollback() throws ConsumeException {
		log.debug("roolback() called");
	}	
	
}
