package fr.sparna.assembly.base;

import fr.sparna.assembly.Assembly;

public interface AssemblyFactory<X> {

	public Assembly<X> buildIndexable(String id);
	
}
