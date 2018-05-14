package fr.sparna.rdf.rdf4j.toolkit.reader;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * A key that associates a IRI and an language; typically to read a property in a given language.
 * @author Thomas Francart
 *
 */
public class IriLang {

	protected IRI iri;
	protected String lang;
	
	public IriLang(IRI iri, String lang) {
		super();
		this.iri = iri;
		this.lang = lang;
	}
	
	public IriLang(String uri, String lang) {
		this(SimpleValueFactory.getInstance().createIRI(uri), lang);
	}

	public IRI getIri() {
		return iri;
	}

	public void setIri(IRI uri) {
		this.iri = uri;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	@Override
	public String toString() {
		return "IriLang [uri=" + iri + ", lang=" + lang + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
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
		IriLang other = (IriLang) obj;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		return true;
	}
	
}
