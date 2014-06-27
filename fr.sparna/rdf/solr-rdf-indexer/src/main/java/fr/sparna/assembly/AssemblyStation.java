package fr.sparna.assembly;


/**
 * Abstraction for objects capable of building a <code>Assembly</code>.
 * Typically each <code>AssemblyStation</code> in an AssemblyLine
 * will be responsible for constructing a part, or a field, of the <code>Assembly</code>.
 * 
 * @author Thomas Francart
 *
 * @param <X>	The type of object to index
 */
public interface AssemblyStation<X> extends AssemblyLineComponent<X> {

	public void process(Assembly<X> indexable) throws AssemblyException;
	
}
