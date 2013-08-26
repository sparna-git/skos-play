package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.Locale;
import java.util.ResourceBundle;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.skos.printer.schema.DisplayBody;

public abstract class AbstractBodyReader {

	protected ResourceBundle tagsBundle;
	
	protected Repository repository;
	
	public AbstractBodyReader(Repository repository) {
		super();
		this.repository = repository;
	}

	public DisplayBody readBody(String mainLang, final URI conceptScheme) 
	throws SPARQLPerformException {
		this.initTagsBundle(mainLang);
		
		// prevent null language
		if(mainLang == null) {
			mainLang = Locale.getDefault().getLanguage();
		}
		
		return this.doRead(mainLang, conceptScheme);
	}
	
	protected void initTagsBundle(String lang) {
		// init tag resource bundle if not set
		if(this.tagsBundle == null) {
			tagsBundle = ResourceBundle.getBundle(
					"fr.sparna.rdf.skos.display.Tags",
					new Locale(lang),
					new fr.sparna.i18n.StrictResourceBundleControl()
			);
		}
	}
	
	protected abstract DisplayBody doRead(String mainLang, final URI conceptScheme)
	throws SPARQLPerformException ;
	
}
