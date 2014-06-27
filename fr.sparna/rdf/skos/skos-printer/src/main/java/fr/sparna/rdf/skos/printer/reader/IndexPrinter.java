package fr.sparna.rdf.skos.printer.reader;

import java.security.InvalidParameterException;

import fr.sparna.rdf.skos.printer.schema.Index;
import fr.sparna.rdf.skos.printer.schema.IndexEntry;

public class IndexPrinter {

	public static enum DisplayMode {
		// keyword in context
		KWIC,
		// keyword alongside context
		KWAC,
	}
	
	public static String debug(Index kwic, DisplayMode mode) {
		switch(mode) {
		case KWIC : {
			return displayKwic(kwic);
		}
		case KWAC : {
			return displayKwac(kwic);
		}
		default : {
			throw new InvalidParameterException("Unkwown mode value "+mode);
		}
		}
	}
	
	public static String displayKwic(Index index) {
		StringBuffer s = new StringBuffer();
		
		// compute the maximum length of the 'before' part
		int maxBeforeLength = 0;
		for (IndexEntry anEntry : index.getEntry()) {
			if(anEntry.getBefore().length() > maxBeforeLength) {
				maxBeforeLength = anEntry.getBefore().length();
			}
		}
		
		for (IndexEntry anEntry : index.getEntry()) {
			int missingSpaces = maxBeforeLength - anEntry.getBefore().length();
			String paddingSpaces = "";
			for(int i=0;i<missingSpaces;i++) {
				paddingSpaces += " ";
			}
			s.append(paddingSpaces+anEntry.getBefore());
			s.append(" "+anEntry.getKey());
			s.append(anEntry.getAfter());
			s.append("\n");
		}
		
		return s.toString();
	}
	
	public static String displayKwac(Index index) {
		StringBuffer s = new StringBuffer();
		
		for (IndexEntry anEntry : index.getEntry()) {
			s.append(anEntry.getKey());
			s.append(" "+anEntry.getAfter());
			if(!anEntry.getBefore().equals("")) {
				s.append(" ("+anEntry.getBefore()+" ~)");
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
}
