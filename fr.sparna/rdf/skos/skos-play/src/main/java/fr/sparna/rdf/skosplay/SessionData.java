package fr.sparna.rdf.skosplay;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.eclipse.rdf4j.repository.Repository;

import com.google.api.services.drive.Drive;

import fr.sparna.google.GoogleConnector;
import fr.sparna.google.GoogleUser;
import fr.sparna.rdf.sesame.toolkit.handler.DebugHandler;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;

public class SessionData {

	public static final String KEY = SessionData.class.getCanonicalName();
	
	// The repository
	protected Repository repository;
	
	// The user Locale
	protected Locale userLocale;
	
	// The sourceConceptLabel reader (with a cache)
	protected LabelReader labelReader;
	
	// data for the PrintForm
	protected PrintFormData printFormData;
	
	// the pre-loaded data resource bundle
	protected ResourceBundle preLoadedDataLabels;
	
	protected Drive service;
	
	protected ConvertFormData convertFormData;
	
	protected GoogleUser user;
	
	protected String baseUrl;
	
	protected GoogleConnector googleConnector;

	/**
	 * Stores this data into session
	 * @param session
	 */
	public void store(HttpSession session) {
		session.setAttribute(KEY, this);
	}
	
	/**
	 * Retrieves the SessionData object stored into the session.
	 * 
	 * @param session
	 * @return
	 */
	public static SessionData get(HttpSession session) {
		return (SessionData)session.getAttribute(KEY);
	}
	
	public GoogleUser getUser() {
		return user;
	}

	public void setUser(GoogleUser user) {
		this.user = user;
	}

	public Repository getRepository() {
		return repository;
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public LabelReader getLabelReader() {
		return labelReader;
	}
	
	public void setLabelReader(LabelReader labelReader) {
		this.labelReader = labelReader;
	}

	public PrintFormData getPrintFormData() {
		return printFormData;
	}

	public void setPrintFormData(PrintFormData printFormData) {
		this.printFormData = printFormData;
	}

	public Locale getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}

	public ResourceBundle getPreLoadedDataLabels() {
		return preLoadedDataLabels;
	}

	public void setPreLoadedDataLabels(ResourceBundle preLoadedDataLabels) {
		this.preLoadedDataLabels = preLoadedDataLabels;
	}

	public ConvertFormData getConvertFormData() {
		return convertFormData;
	}

	public void setConvertFormData(ConvertFormData convertFormData) {
		this.convertFormData = convertFormData;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public GoogleConnector getGoogleConnector() {
		return googleConnector;
	}

	public void setGoogleConnector(GoogleConnector googleConnector) {
		this.googleConnector = googleConnector;
	}

	public static void main(String...strings) throws Exception {
		RepositoryBuilder builder = new RepositoryBuilder(new EndpointRepositoryFactory("http://poolparty.reegle.info/PoolParty/sparql/glossary", false));
		Repository repository = builder.createNewRepository();
		Perform.on(repository).select(new SelectSparqlHelper(
				"PREFIX skos:<http://www.w3.org/2004/02/skos/core#> SELECT (COUNT(?concept) AS ?nbOfConcepts) WHERE { ?concept a skos:Concept . } ",
				new DebugHandler()
		));
		
	}
	
	
	
}
