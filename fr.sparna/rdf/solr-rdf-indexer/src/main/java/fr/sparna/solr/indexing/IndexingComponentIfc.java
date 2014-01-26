package fr.sparna.solr.indexing;

public interface IndexingComponentIfc {

	public void init() throws LifecycleException;
	
	public void destroy() throws LifecycleException;
	
}
