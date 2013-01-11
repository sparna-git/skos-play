package fr.sparna.rdf.sesame.toolkit.skos;

import java.util.Set;

public interface SKOSConcept {

	public java.net.URI getUri();
	public Set<SKOSLiteral> getPrefLabels();
	public Set<SKOSLiteral> getAltLabels();
	public Set<SKOSLiteral> getHiddenLabels();
	// TODO : notes
	
}
