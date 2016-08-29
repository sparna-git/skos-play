package fr.sparna.rdf.skos.toolkit.skos2skosxl;

import org.openrdf.repository.Repository;

public class SKOS2SKOSXLConverter {

	private boolean useBlankNodes = false;
	
	public void convertSkos2SkosXl(Repository repository) {
		
	}

	public boolean isUseBlankNodes() {
		return useBlankNodes;
	}

	public void setUseBlankNodes(boolean useBlankNodes) {
		this.useBlankNodes = useBlankNodes;
	}
	
}
