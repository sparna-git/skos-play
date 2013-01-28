package fr.sparna.gate.service;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Nombre d'applications dans le pool = nombre maximal d'appels concurrents
	 */
	private static final int POOL_SIZE = 3;
	
	/**
	 * Default gate application file. looked under under GATE HOME. example of value is : gate/application.gapp
	 */
	private static final String DEFAULT_GATE_APP = "application.gapp";

	/**
	 * Le pool d'applications gate
	 */
	private static BlockingQueue<CorpusController> pool;
	
	// **************** INITIALIZATION **************** 
	
	@Override
	public void init() throws ServletException {
		try {
			pool = new LinkedBlockingQueue<CorpusController>();
			for(int i = 0; i < POOL_SIZE; i++) {
				pool.add(createApplication());
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	private CorpusController createApplication() throws PersistenceException, ResourceInstantiationException, IOException {
		// TODO : pouvoir passer le nom de l'application a charger en parametre System
		return (CorpusController)PersistenceManager.loadObjectFromFile(new File(Gate.getGateHome(),GateServlet.DEFAULT_GATE_APP));
	}

	// ********************* DESTROY ************************
	
	@Override
	public void destroy() {
		super.destroy();
		for(CorpusController c : pool) {
			Factory.deleteResource(c);
		}
	}
	
	// ********************* PROCESS ************************
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		// appel a doPost
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

		// Get request content
		StringBuffer payload = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				payload.append(line);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		// Get annotation types
		String annotationParam = request.getParameter("annotations");
		Set<String> annotations = null;
		if(annotationParam != null) {
			annotations = new HashSet<String>(Arrays.asList(annotationParam.split(",")));
		}
		
		CorpusController application = null;
		try {
			// blocks if the pool is empty
			application = pool.take();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			String result = processText(application, payload.toString(), null, "UTF-8", true, annotations);
			
			// write to the response
			// TODO : encoding ?
			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().print(result);
			response.getWriter().flush();
		} catch (GateServletException e) {
			throw new ServletException(e);
		} finally {
			pool.add(application);
		}

	}


	/**
	 * Annote le texte donné en argument sous la forme d'une String.
	 * En sortie, la méthode renvoie le xml sérialisé de Gate avec les annotations.
	 * 
	 * @param text
	 * @param resource
	 * @param encoding
	 * @return xml sérialisé de Gate avec les annotations.
	 */
	public String processText(CorpusController application, String text, String resource, String encoding, boolean preserveXML, Set<String> annotationTypes) 
	throws GateServletException {

		log.info("processing text of resource '"+resource+"' with encoding '"+encoding+"'");
		log.info("retrieving annotations "+annotationTypes);
		long start = System.currentTimeMillis();
		
		if (text == null || text.isEmpty()) {
			log.error("Text to process is null or empty, cannot proceed");
			throw new GateServletException("No content to analyze");
		} else {
			log.info("Text to process starts with \""+((text.length() > 50)?text.substring(0,49)+" (...)":text)+"\"");
		}

		if(application == null) {
			throw new GateServletException("Gate application is null - cannot proceed");
		}

		try {
			log.info("setCorpus...");
			setCorpus(application, text, resource, encoding);

			log.trace("executeApplication entering...");
			application.execute();
			log.trace("executeApplication done.");

			String annotations = getAnnotations(application, preserveXML, annotationTypes);

			log.info("processText-exiting successfully, total TIME : "+(System.currentTimeMillis()-start)+" ms.");

			return annotations;
		} catch (ResourceInstantiationException e) {
			throw new GateServletException(e);
		} catch (ExecutionException e) {
			throw new GateServletException(e);
		} finally {
			// libérer les resources
			clearCorpus(application);
		}
	}

	/**
	 * Sets corpus from a string
	 * @param text
	 * @param resource
	 * @param charset
	 * @throws ResourceInstantiationException
	 */
	private void setCorpus(CorpusController application, String text, String resource, String charset) throws ResourceInstantiationException {

		log.debug("setCorpus start : Creating doc for from text...");
		log.debug("charset in set Corpus with text= "+charset);

		// create a GATE corpus and add a text document
		Corpus corpus = Factory.newCorpus("corpus");

		//create a doc
		Document doc = Factory.newDocument(text);

		//do the params
		FeatureMap params = Factory.newFeatureMap();
		//need for encoding? already decoded String here?
		params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, charset);
		//doubled? already in constructor Factory.newDocument ?
		params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, text);
		doc.setParameterValues(params);

		//do the features
		FeatureMap features = Factory.newFeatureMap();
		features.put("resource", resource);
		doc.setFeatures(features);

		log.debug("Document is markup aware ? "+doc.getMarkupAware());
		corpus.add(doc);
		application.setCorpus(corpus);

		log.debug("setCorpus end : Corpus was set into GATE Application!");
	}
	
	private void clearCorpus(CorpusController application) {
		log.debug("clearCorpus() ...");
		
		if(application == null) {
			return;
		}
		
		//get all docs to be deleted
		List<Document> docsResourceToDelete = new ArrayList<Document>();
		for (Document cdoc : application.getCorpus()) {
			docsResourceToDelete.add(cdoc);
		}
		log.debug("clearCorpus() clear corpus from corpus application...");

		//clear corpus
		application.getCorpus().clear();
		
		log.debug("clearCorpus() delete all Document resources...");
		//delete all Document resources
		for (Document doc : docsResourceToDelete) {
			Factory.deleteResource(doc);
		}
		log.debug("clearCorpus() done.");
	}
	
	/**
	 * Creates XML Serialization of GATE Process Result, in a String
	 * @return
	 */
	private String getAnnotations(CorpusController application, boolean preserveXML, Set<String> annotationTypes) {
		log.debug("getAnnotations() enter - Xml serialisation...");
		StringBuilder annotatedCorpus = new StringBuilder();

		// for each document, get an XML document with the
		// entities annotated
		for (Document doc : application.getCorpus() ) {
			log.debug("getAnnotations() ...processing a new document");
			Boolean isPreserveOriginalContent = doc.getPreserveOriginalContent();
			Boolean isMarkupAware = doc.getMarkupAware();
			log.debug("getAnnotations() ...isPreserveOriginalContent?"+isPreserveOriginalContent);
			log.debug("getAnnotations() ...isMarkupAware?"+isMarkupAware);
			
			// les annotations d'un document
			AnnotationSet defaultAnnotSet = doc.getAnnotations();
			// seulement les annotations de certains types
			AnnotationSet annotationSet = defaultAnnotSet.get(annotationTypes);

			for (Annotation a : annotationSet) {
				FeatureMap attributes = a.getFeatures();
				// on ajoute explicitement les attributs d'offset a l'annotation pour que ca sorte dans le XML
				attributes.put("startOffset", a.getStartNode().getOffset());
				attributes.put("endOffset", a.getEndNode().getOffset());
				a.setFeatures(attributes);
			}

			log.debug("getAnnotations() ...doc to XML!");
			if (preserveXML) {
				log.debug("getAnnotations() ...preserves input XML with features");
				annotatedCorpus.append(doc.toXml(annotationSet, true));
			} else {
				log.debug("getAnnotations() ...output GATE XML Format");
				annotatedCorpus.append(doc.toXml());
			}
		}

		log.debug("getAnnotations() ...End of xml serialisation");
		log.debug("getAnnotations() exit");
		return annotatedCorpus.toString();
	}
}