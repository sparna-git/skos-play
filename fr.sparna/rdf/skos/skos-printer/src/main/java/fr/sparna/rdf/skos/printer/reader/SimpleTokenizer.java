package fr.sparna.rdf.skos.printer.reader;

public class SimpleTokenizer implements IndexTokenizerIfc {

	protected static final String REGEX = "[\\s\\p{Punct}]";
	
	@Override
	public String[] tokenize(String s) {
		return s.split(REGEX);
	}
	
}
