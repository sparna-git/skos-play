package fr.sparna.rdf.datapress;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.rio.RDFHandler;

public class NotifyingDataPressWrapper implements NotifyingDataPress {

	private DataPress press;
	private List<DataPressListener> listeners;
	
	public NotifyingDataPressWrapper(DataPress press) {
		super();
		this.press = press;
		this.listeners = new ArrayList<DataPressListener>();
	}
	
	public NotifyingDataPressWrapper(DataPress press, DataPressListener listener) {
		this(press);
		addListener(listener);
	}

	@Override
	public void addListener(DataPressListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(DataPressListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void press(
			DataPressSource in,
			RDFHandler out
	) throws DataPressException {
		
		notifyBegin(in);
		
		try {
			this.press.press(in, out);
		} catch (DataPressException e) {
			// notify the exception
			notifyEnd(in, false);
			// rethrow the exception up
			throw(e);
		}
		
		notifyEnd(in, true);
	}
	
	private void notifyBegin(DataPressSource in) {
		for (DataPressListener aListener : listeners) {
			aListener.begin(in);
		}
	}
	
	private void notifyEnd(DataPressSource in, boolean success) {
		for (DataPressListener aListener : listeners) {
			aListener.end(in, success);
		}
	}
	
}
