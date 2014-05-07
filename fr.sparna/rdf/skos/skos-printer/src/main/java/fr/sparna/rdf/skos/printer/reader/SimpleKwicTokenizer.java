package fr.sparna.rdf.skos.printer.reader;

public class SimpleKwicTokenizer implements KwicIndexTokenizerIfc {

	protected static final String REGEX = "[\\s\\p{Punct}]";
	
	@Override
	public String[] tokenize(String s) {
		return s.split(REGEX);
	}
	
}
