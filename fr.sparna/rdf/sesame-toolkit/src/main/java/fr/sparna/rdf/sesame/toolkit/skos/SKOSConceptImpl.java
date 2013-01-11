package fr.sparna.rdf.sesame.toolkit.skos;

import java.net.URI;
import java.util.Set;

public class SKOSConceptImpl implements SKOSConcept {

	protected java.net.URI uri;
	protected Set<SKOSLiteral> prefLabels;
	protected Set<SKOSLiteral> altLabels;
	protected Set<SKOSLiteral> hiddenLabels;
	
	private SKOSConceptImpl(URI uri) {
		super();
		this.uri = uri;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public Set<SKOSLiteral> getPrefLabels() {
		return prefLabels;
	}

	@Override
	public Set<SKOSLiteral> getAltLabels() {
		return altLabels;
	}

	@Override
	public Set<SKOSLiteral> getHiddenLabels() {
		return hiddenLabels;
	}

	public void setPrefLabels(Set<SKOSLiteral> prefLabels) {
		this.prefLabels = prefLabels;
	}

	public void setAltLabels(Set<SKOSLiteral> altLabels) {
		this.altLabels = altLabels;
	}

	public void setHiddenLabels(Set<SKOSLiteral> hiddenLabels) {
		this.hiddenLabels = hiddenLabels;
	}
	
	

}
