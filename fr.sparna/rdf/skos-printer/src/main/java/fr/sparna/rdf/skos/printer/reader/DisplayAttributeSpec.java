package fr.sparna.rdf.skos.printer.reader;

class DisplayAttributeSpec {
	
	protected String skosProperty;
	protected String displayString;
	protected boolean isObjectProperty;
	
	public DisplayAttributeSpec(
			String skosProperty,
			String displayString,
			boolean isObjectProperty
	) {
		super();
		this.skosProperty = skosProperty;
		this.displayString = displayString;
		this.isObjectProperty = isObjectProperty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((skosProperty == null) ? 0 : skosProperty.hashCode());
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
		DisplayAttributeSpec other = (DisplayAttributeSpec) obj;
		if (skosProperty == null) {
			if (other.skosProperty != null)
				return false;
		} else if (!skosProperty.equals(other.skosProperty))
			return false;
		return true;
	}
	
	
}