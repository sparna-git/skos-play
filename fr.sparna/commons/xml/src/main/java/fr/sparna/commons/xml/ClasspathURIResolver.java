package fr.sparna.commons.xml;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class ClasspathURIResolver implements URIResolver {

	protected String baseDirectory;

	public ClasspathURIResolver(String baseDirectory) {
		super();
		this.baseDirectory = baseDirectory;
	}
	
	public ClasspathURIResolver() {
		this(null);
	}

	@Override
	public Source resolve(String href, String base) throws TransformerException {
		String toResolve = (this.baseDirectory != null && !this.baseDirectory.equals(""))?this.baseDirectory+"/"+href:href;
		return new StreamSource(this.getClass().getClassLoader().getResourceAsStream(toResolve));
	}

}
