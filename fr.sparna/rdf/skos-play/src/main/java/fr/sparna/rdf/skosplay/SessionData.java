package fr.sparna.rdf.skosplay;

import javax.servlet.http.HttpSession;

import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.util.LabelReader;

public class SessionData {

	public static final String KEY = SessionData.class.getCanonicalName();
	
	// The repository
	protected Repository repository;
	// The label reader (with a cache)
	protected LabelReader labelReader;
	// data for the PrintForm
	protected PrintFormData printFormData;
	
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

}
