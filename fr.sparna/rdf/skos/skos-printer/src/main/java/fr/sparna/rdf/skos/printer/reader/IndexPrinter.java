package fr.sparna.rdf.skos.printer.reader;

import java.security.InvalidParameterException;

import fr.sparna.rdf.skos.printer.schema.KwicEntry;
import fr.sparna.rdf.skos.printer.schema.KwicIndex;

public class IndexPrinter {

	public static enum DisplayMode {
		// keyword in context
		KWIC,
		// keyword alongside context
		KWAC,
	}
	
	public static String debug(KwicIndex kwic, DisplayMode mode) {
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
	
	public static String displayKwic(KwicIndex kwic) {
		StringBuffer s = new StringBuffer();
		
		// compute the maximum length of the 'before' part
		int maxBeforeLength = 0;
		for (KwicEntry anEntry : kwic.getEntry()) {
			if(anEntry.getBefore().length() > maxBeforeLength) {
				maxBeforeLength = anEntry.getBefore().length();
			}
		}
		
		for (KwicEntry anEntry : kwic.getEntry()) {
			int missingSpaces = maxBeforeLength - anEntry.getBefore().length();
			String paddingSpaces = "";
			for(int i=0;i<missingSpaces;i++) {
				paddingSpaces += " ";
			}
			s.append(paddingSpaces+anEntry.getBefore());
			s.append(" "+anEntry.getKeyLabel());
			s.append(" "+anEntry.getAfter());
			s.append("\n");
		}
		
		return s.toString();
	}
	
	public static String displayKwac(KwicIndex kwic) {
		StringBuffer s = new StringBuffer();
		
		for (KwicEntry anEntry : kwic.getEntry()) {
			s.append(anEntry.getKeyLabel());
			s.append(" "+anEntry.getAfter());
			if(!anEntry.getBefore().equals("")) {
				s.append(" ("+anEntry.getBefore()+" ~)");
			}
			s.append("\n");
		}
		
		return s.toString();
	}
	
}
