package fr.sparna.i18n;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle.Control;

public class StrictResourceBundleControl extends Control {
	
	@Override
    public List<Locale> getCandidateLocales(String baseName, Locale locale) {
    	// avoid searching the default locale
    	return Arrays.asList(
                locale,
                Locale.ROOT);
    }
	
}
