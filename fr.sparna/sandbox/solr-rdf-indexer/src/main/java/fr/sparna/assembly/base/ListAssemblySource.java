package fr.sparna.assembly.base;

import java.util.Iterator;
import java.util.List;

import fr.sparna.assembly.Assembly;
import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.AssemblySource;
import fr.sparna.assembly.LifecycleException;

public class ListAssemblySource<X> extends BaseAssemblyLineComponent<X> implements AssemblySource<X> {

	protected AssemblyFactory<X> factory;
	protected List<String> list;

	protected transient Iterator<String> iterator;
	
	public ListAssemblySource(List<String> list, AssemblyFactory<X> factory) {
		super();
		this.factory = factory;
		this.list = list;
	}

	@Override
	public void init(AssemblyLine<X> assemblyLine) throws LifecycleException {
		super.init(assemblyLine);
		this.iterator = list.iterator();
	}
	
	@Override
	public void destroy() throws LifecycleException {

	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Assembly<X> next() {
		String x = iterator.next();
		return factory.buildIndexable(x);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int sizeEstimate() {
		return list.size();
	}
	
}
