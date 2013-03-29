package fr.sparna.cli;

import java.net.URI;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;

public class URIFactory implements IStringConverterFactory {
	
	@Override
	public Class<? extends IStringConverter<?>> getConverter(Class forType) {
		if (forType.equals(URI.class)) {
			return URIConverter.class;
		} else {
			return null;
		}
	}
	
}
	
