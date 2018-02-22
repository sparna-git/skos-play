package fr.sparna.assembly;

/**
 * An base abstraction for all the building blocks of an <code>AssemblyLine</code>,
 * declaring lifecycle-related methodes.
 * 
 * @author Thomas Francart
 *
 */
public interface AssemblyLineComponent<X> {

	/**
	 * Initializes the component at the beginning of an <code>AssemblyLine</code>.
	 * @throws LifecycleException
	 */
	public void init(AssemblyLine<X> assemblyLine) throws LifecycleException;
	
	/**
	 * Destroys the component at the end of an <code>AssemblyLine</code>, to
	 * free all ressources.
	 * 
	 * @throws LifecycleException
	 */
	public void destroy() throws LifecycleException;
	
}
