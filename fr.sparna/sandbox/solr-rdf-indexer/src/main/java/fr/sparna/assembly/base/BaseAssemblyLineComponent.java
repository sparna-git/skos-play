package fr.sparna.assembly.base;

import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.AssemblyLineComponent;
import fr.sparna.assembly.LifecycleException;

/**
 * Base implementation of <code>AssemblyLineComponent</code> with empty lifecycle
 * methods.
 * @author Thomas Francart.
 */
public class BaseAssemblyLineComponent<X> implements AssemblyLineComponent<X> {
	
	protected AssemblyLine<X> assemblyLine;
	
	@Override
	public void init(AssemblyLine<X> assemblyLine) throws LifecycleException {
		this.assemblyLine = assemblyLine;
	}

	@Override
	public void destroy() throws LifecycleException {
	}

	public AssemblyLine<X> getAssemblyLine() {
		return assemblyLine;
	}

}
