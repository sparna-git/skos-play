package fr.sparna.rdf.skosplay;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.eclipse.rdf4j.repository.Repository;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;

import fr.sparna.google.GoogleAuthHelper;
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
	
	protected GoogleAuthHelper googleAuthHelper;

	// to download the google conversion result
	protected ByteArrayOutputStream googleConversionResult;	
	protected String googleConversionResultContentType;
	
	protected Credential googleCredential;
	
	protected Drive service;
	
	protected ConvertFormData convertFormData;
	
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
	
	public Repository getRepository() {
		return repository;
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public Credential getGoogleCredential() {
		return googleCredential;
	}

	public void setGoogleCredential(Credential googleCredential) {
		this.googleCredential = googleCredential;
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

	public GoogleAuthHelper getGoogleAuthHelper() {
		return googleAuthHelper;
	}

	public void setGoogleAuthHelper(GoogleAuthHelper googleAuthHelper) {
		this.googleAuthHelper = googleAuthHelper;
	}

	public ByteArrayOutputStream getGoogleConversionResult() {
		return googleConversionResult;
	}

	public void setGoogleConversionResult(ByteArrayOutputStream googleConversionResult) {
		this.googleConversionResult = googleConversionResult;
	}

	public String getGoogleConversionResultContentType() {
		return googleConversionResultContentType;
	}

	public void setGoogleConversionResultContentType(String googleConversionResultContentType) {
		this.googleConversionResultContentType = googleConversionResultContentType;
	}

	public ConvertFormData getConvertFormData() {
		return convertFormData;
	}

	public void setConvertFormData(ConvertFormData convertFormData) {
		this.convertFormData = convertFormData;
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
