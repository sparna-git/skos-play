package fr.sparna.rdf.skosplay;

import java.net.URI;
import java.text.Collator;
import java.util.Locale;

public class LabeledResource implements Comparable {

	protected java.net.URI uri;
	protected String label;
	
	public LabeledResource(URI uri, String label) {
		super();
		this.uri = uri;
		this.label = label;
	}
	
	public LabeledResource(URI uri) {
		super();
		this.uri = uri;
	}

	public java.net.URI getUri() {
		return uri;
	}
	
	public void setUri(java.net.URI uri) {
		this.uri = uri;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int compareTo(Object o) {
		if(!(o instanceof LabeledResource)) {
			return -1;
		} else {
			LabeledResource that = (LabeledResource)o;
			return this.getLabel().compareToIgnoreCase(that.getLabel());
		}
	}

	
}
