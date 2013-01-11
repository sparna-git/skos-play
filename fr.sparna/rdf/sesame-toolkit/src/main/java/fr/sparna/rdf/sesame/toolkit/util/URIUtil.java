package fr.sparna.rdf.sesame.toolkit.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;

public class URIUtil {

	public static Resource[] toResourceArray(Set<URI> uris, ValueFactory factory) {
		if(uris == null) {
			return null;
		}
		return toResourceArray(uris.toArray(new URI[]{}), factory);
	}
	
	public static Resource[] toResourceArray(List<URI> uris, ValueFactory factory) {
		if(uris == null) {
			return null;
		}
		return toResourceArray(uris.toArray(new URI[]{}), factory);
	}
	
	public static Resource[] toResourceArray(URI[] uris, ValueFactory factory) {
		if(uris == null) {
			return null;
		}

		List<Resource> result = new ArrayList<Resource>();
		for (URI uri : uris) {
			result.add(factory.createURI(uri.toString()));
		}
		return result.toArray(new Resource[]{});
	}

}
