package fr.sparna.rdf.skosplay;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import com.google.api.services.drive.Drive;

import fr.sparna.google.GoogleConnector;
import fr.sparna.google.GoogleUser;
import fr.sparna.rdf.rdf4j.toolkit.handler.DebugHandler;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.EndpointRepositorySupplier;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;

public class SessionData {

	public static final String KEY = SessionData.class.getCanonicalName();
	
	// The currently uploaded data
	protected SkosPlayModel skosPlayModel;
	
	// The user Locale
	protected Locale userLocale;
	
	// data for the PrintForm
	protected PrintFormData printFormData;
	
	// the pre-loaded data resource bundle
	protected ResourceBundle preLoadedDataLabels;
	
	protected Drive service;
	
	protected ConvertFormData convertFormData;
	
	protected GoogleUser user;
	
	protected String baseUrl;
	
	protected GoogleConnector googleConnector;
	
	protected String periodeView;

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

	public SkosPlayModel getSkosPlayModel() {
		return skosPlayModel;
	}

	public void setSkosPlayModel(SkosPlayModel skosPlayModel) {
		this.skosPlayModel = skosPlayModel;
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
	

	public String getPeriodeView() {
		
		return periodeView;
	}

	public void setPeriodeView(String periodeView) {
		this.periodeView = periodeView;
	}
	

	public static void main(String...strings) throws Exception {
		RepositoryBuilder builder = new RepositoryBuilder(new EndpointRepositorySupplier("http://poolparty.reegle.info/PoolParty/sparql/glossary", false));
		Repository repository = builder.get();
		try(RepositoryConnection connection = repository.getConnection()) {
			Perform.on(connection).select(
					"PREFIX skos:<http://www.w3.org/2004/02/skos/core#> SELECT (COUNT(?concept) AS ?nbOfConcepts) WHERE { ?concept a skos:Concept . } ",
					new DebugHandler()
			);
		}
		
	}
	
	
	
}
