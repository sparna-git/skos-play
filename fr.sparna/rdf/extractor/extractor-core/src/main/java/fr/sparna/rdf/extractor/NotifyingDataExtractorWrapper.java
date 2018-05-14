package fr.sparna.rdf.extractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.rio.RDFHandler;

/**
 * Wraps a DataExtractor to add listening capabilities to it.
 * @author Thomas Francart
 *
 */
public class NotifyingDataExtractorWrapper implements NotifyingDataExtractor {

	private DataExtractor extractor;
	private List<DataExtractorListener> listeners;
	
	public NotifyingDataExtractorWrapper(DataExtractor extractor) {
		super();
		this.extractor = extractor;
		this.listeners = new ArrayList<DataExtractorListener>();
	}
	
	public NotifyingDataExtractorWrapper(DataExtractor press, DataExtractorListener listener) {
		this(press);
		addListener(listener);
	}

	@Override
	public void addListener(DataExtractorListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(DataExtractorListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void extract(
			DataExtractionSource in,
			RDFHandler out
	) throws DataExtractionException {
		
		// notifies the listeners of the start of processing
		notifyBegin(in);
		
		try {
			this.extractor.extract(in, out);
		} catch (DataExtractionException e) {
			// notify the exception
			notifyEnd(in, false);
			// rethrow the exception up
			throw(e);
		}
		
		// notifies the listeners of the end of processing
		notifyEnd(in, true);
	}
	
	@Override
	public boolean canHandle(DataExtractionSource source) throws DataExtractionException {
		return extractor.canHandle(source);
	}

	private void notifyBegin(DataExtractionSource in) {
		for (DataExtractorListener aListener : listeners) {
			aListener.begin(in);
		}
	}
	
	private void notifyEnd(DataExtractionSource in, boolean success) {
		for (DataExtractorListener aListener : listeners) {
			aListener.end(in, success);
		}
	}
	
}
