package fr.sparna.commons.lang;

import java.util.Iterator;

public interface ClosableIterator<E> extends Iterator<E> {

	public void close();
	
}
