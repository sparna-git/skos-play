package fr.sparna.cli;

import java.net.URI;
import java.net.URISyntaxException;

import com.beust.jcommander.IStringConverter;

public class URIConverter implements IStringConverter<URI> {

	@Override
	public URI convert(String value) {
		try {
			return new URI(value);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Cannot parse "+value+" as a URI", e);
		}
	}
}
