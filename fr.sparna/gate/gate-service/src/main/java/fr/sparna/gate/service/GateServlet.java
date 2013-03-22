package fr.sparna.gate.service;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * Application Gate par défaut si le paramètre PARAM_APPLICATION n'est pas précisé
	 */
	private static final String DEFAULT_GATE_APP = "application.gapp";
	
	private static final String PARAM_ANNOTATIONS = "annotations";
	
	private static final String PARAM_ENCODING = "encoding";
	
	private static final String PARAM_XSLT = "xslt";
	
	private static final String PARAM_DOCID = "docId";

	/**
	 * Path to gate application. Searched under GATE HOME. Example value is : gate/application.gapp
	 */
	private static final String PARAM_APPLICATION = "application";
	
	/**
	 * Les applications gate par path
	 */
	private static Map<String, GateServiceApplication> applicationsCache = new HashMap<String, GateServiceApplication>();
	
	/**
	 * XSL Transformer à appliquer aux résultats
	 */
	private static Map<String, Transformer> transformersCache = new HashMap<String, Transformer>();
	
	// **************** INITIALIZATION **************** 
	
	@Override
	public void init() throws ServletException {
		
	}

	// ********************* DESTROY ************************
	
	@Override
	public void destroy() {
		super.destroy();
		// destroy toutes les applications
		for(GateServiceApplication anApp : applicationsCache.values()) {
			anApp.destroy();
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

		// get application param
		String app = (request.getParameter(PARAM_APPLICATION) != null && !request.getParameter(PARAM_APPLICATION).equals(""))?request.getParameter(PARAM_APPLICATION):DEFAULT_GATE_APP;
		GateServiceApplication serviceApplication;
		
		// add to cache if not already there
		if(!applicationsCache.containsKey(app)) {
			serviceApplication = new GateServiceApplication(app);
			try {
				serviceApplication.init();
			} catch (Exception e) {
				throw new ServletException(e);
			}
			applicationsCache.put(app, serviceApplication);
		} else {
			serviceApplication = applicationsCache.get(app);
		}
		
		// get input encoding
		String encoding = (request.getParameter(PARAM_ENCODING) != null && !request.getParameter(PARAM_ENCODING).equals(""))?request.getParameter(PARAM_ENCODING):"UTF-8";
		
		// get xslt param
		String xslt = (request.getParameter(PARAM_XSLT) != null && !request.getParameter(PARAM_XSLT).equals(""))?request.getParameter(PARAM_XSLT):null;
		
		// get docId param
		String docId = (request.getParameter(PARAM_DOCID) != null && !request.getParameter(PARAM_DOCID).equals(""))?request.getParameter(PARAM_DOCID):null;
		
		// Get annotation types
		Set<String> annotations = (request.getParameter(PARAM_ANNOTATIONS) != null && !request.getParameter(PARAM_ANNOTATIONS).equals(""))?
				new HashSet<String>(Arrays.asList(request.getParameter(PARAM_ANNOTATIONS).split(",")))
				:null;
		
		// Get request content
		StringBuffer payload = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), encoding));
			while ((line = reader.readLine()) != null) {
				payload.append(line);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		// recuperer l'application du pool
		CorpusController application = serviceApplication.takeApplication();
		
		try {
			String result = processText(application, payload.toString(), docId, encoding, true, annotations);
			
			// if xslt reference was provided, apply XSLT
			if(xslt != null) {
				if(!transformersCache.containsKey(xslt)) {
					// s'il y a une erreur, on sortira en exception
					this.initXSLT(xslt);
				}
				result = applyXSLT(transformersCache.get(xslt), result, docId, encoding);
			}
			
			// write to the response
			response.setContentType("text/xml;charset="+encoding);
			response.getWriter().print(result);
			response.getWriter().flush();
		} catch (GateServletException e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new ServletException(e);
		} finally {
			// remettre l'application dans le bon pool
			serviceApplication.returnApplication(application);
		}
	}
	
	private Transformer initXSLT(String xslPath) throws GateServletException, TransformerConfigurationException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(xslPath);
		if(is == null) {
			log.error("Cannot find XSL resource at '"+xslPath+"'");
			throw new GateServletException("Cannot find XSL resource at '"+xslPath+"'");
		}
		
		TransformerFactory factory = TransformerFactory.newInstance();
		return factory.newTransformer(new StreamSource(is));
	}
	
	private String applyXSLT(Transformer t, String input, String docId, String encoding) throws TransformerException {
		try {
			StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(input.getBytes(encoding)));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult xslResult = new StreamResult(new OutputStreamWriter(baos,"UTF-8"));
			t.setParameter("docId", docId);
			t.transform(xmlSource, xslResult);
			
			// le meme encoding que celui du StreamResult
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
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