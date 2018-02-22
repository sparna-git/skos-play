package fr.sparna.assembly;

/**
 * An object to be processed by an <code>AssemblyLine</code>
 * 
 * @author Thomas Francart
 *
 * @param <X> the underlying data structure to be manipulated
 */
public interface Assembly<X> {

	public String getId();
	
	public X getDocument();
	
}
