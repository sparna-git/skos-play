package fr.sparna.rdf.sesame.toolkit.reader;

import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class UriLang {

	protected org.eclipse.rdf4j.model.URI uri;
	protected String lang;
	
	public UriLang(URI uri, String lang) {
		super();
		this.uri = uri;
		this.lang = lang;
	}
	
	public UriLang(String uri, String lang) {
		this(SimpleValueFactory.getInstance().createIRI(uri), lang);
	}

	public org.eclipse.rdf4j.model.URI getUri() {
		return uri;
	}

	public void setUri(org.eclipse.rdf4j.model.URI uri) {
		this.uri = uri;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	@Override
	public String toString() {
		return "UriLang [uri=" + uri + ", lang=" + lang + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UriLang other = (UriLang) obj;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
}
