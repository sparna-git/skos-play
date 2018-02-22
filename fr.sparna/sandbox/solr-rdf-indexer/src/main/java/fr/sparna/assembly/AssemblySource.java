package fr.sparna.assembly;

import java.util.Iterator;

/**
 * Abstraction for objects capable of iterating over the entries to be processed
 * in an <code>AssemblyLine</code>.
 * 
 * @author Thomas Francart
 * @param <X>
 */
public interface AssemblySource<X> extends AssemblyLineComponent<X>, Iterator<Assembly<X>> {
	
	/**
	 * Returns a size estimate of this indexing source, that does not need to be precise,
	 * to compute advance percentage.
	 * @return
	 */
	public int sizeEstimate();
	
}
