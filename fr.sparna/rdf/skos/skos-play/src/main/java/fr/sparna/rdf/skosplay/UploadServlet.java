package fr.sparna.rdf.skosplay;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.openrdf.model.Literal;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.i18n.StrictResourceBundleControl;
import fr.sparna.rdf.sesame.toolkit.languages.Languages.Language;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromUrl;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.toolkit.SKOSRules;
import fr.sparna.web.config.Configuration;

public class UploadServlet extends HttpServlet {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	// source of the data : a file, a URL, an embedded example
	private static final String PARAM_SOURCE = "source";
	
	// if source == FILE, the uploaded file
	private static final String PARAM_FILE = "file";
	
	// if source == URL, the url
	private static final String PARAM_URL = "url";

	// if source == EXAMPLE, the resource path
	private static final String PARAM_EXAMPLE = "example";
	
	// RDFS inference
	private static final String PARAM_RDFS = "rdfs-inference";
	
	// OWL2SKOS inference
	private static final String PARAM_OWL2SKOS = "owl2skos";
	
	private enum SOURCE_TYPE {
		FILE,
		URL,
		EXAMPLE
	}
	
	@Override
	protected void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response
	) throws ServletException, IOException {
		
		// retrieve session
		final SessionData sessionData = SessionData.get(request.getSession());
		
		// retrieve resource bundle for error messages
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.sparna.rdf.skosplay.i18n.Bundle",
				sessionData.getUserLocale(),
				new StrictResourceBundleControl()
		);
		
		// determine source
		String sourceParam = (request.getParameter(PARAM_SOURCE) != null && !request.getParameter(PARAM_SOURCE).equals(""))?request.getParameter(PARAM_SOURCE):null;
		log.debug(PARAM_SOURCE+" : "+sourceParam);
		SOURCE_TYPE sourceType = (sourceParam != null)?SOURCE_TYPE.valueOf(sourceParam.toUpperCase()):null;
		if(sourceType == null) {
			doError(request, response, "Missing required parameter : "+PARAM_SOURCE);
			return;
		}
		
			
		RepositoryBuilder localRepositoryBuilder;
		
		// determine rdfs inference
		String rdfsParam = (request.getParameter(PARAM_RDFS) != null && !request.getParameter(PARAM_RDFS).equals(""))?request.getParameter(PARAM_RDFS):null;
		log.debug(PARAM_RDFS+" : "+rdfsParam);
		boolean doRdfs = (rdfsParam != null && !"".equals(rdfsParam));	
		if(doRdfs) {
			localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));
		} else {
			localRepositoryBuilder = new RepositoryBuilder();
		}
		
		// determine owl2skos inference
		String owl2skosParam = (request.getParameter(PARAM_OWL2SKOS) != null && !request.getParameter(PARAM_OWL2SKOS).equals(""))?request.getParameter(PARAM_OWL2SKOS):null;
		log.debug(PARAM_OWL2SKOS+" : "+owl2skosParam);
		boolean doOwl2Skos = (owl2skosParam != null && !"".equals(owl2skosParam));	
		
		Repository repository;		
		try {
			switch(sourceType) {
			case FILE : {
				// get uploaded file
				Object dataParam = request.getAttribute(PARAM_FILE);
				if(dataParam instanceof FileUploadException) {
					doError(request, response, "FileUploadException : "+((FileUploadException)dataParam).getMessage());
					return;
				}
				FileItem data = (FileItem)dataParam;
				localRepositoryBuilder.addOperation(new LoadFromStream(data.getInputStream(), Rio.getParserFormatForFileName(data.getName(), RDFFormat.RDFXML)));
				repository = localRepositoryBuilder.createNewRepository();
				break;
			}			
			case EXAMPLE : {
				// get resource param
				String resourceParam = (request.getParameter(PARAM_EXAMPLE) != null && !request.getParameter(PARAM_EXAMPLE).equals(""))?request.getParameter(PARAM_EXAMPLE):null;
				if(resourceParam == null || resourceParam.equals("")) {
					doError(request, response, "Select an example from the list.");
					return;
				}
				repository = ApplicationData.get(getServletContext()).getExampleDatas().get(resourceParam);
				break;
			}
			case URL : {
				// get url param
				String urlParam = (request.getParameter(PARAM_URL) != null && !request.getParameter(PARAM_URL).equals(""))?request.getParameter(PARAM_URL):null;
				repository = RepositoryBuilder.fromString(urlParam, doRdfs);
				break;
			}
			default : {
				repository = null;
				break;
			}
			}
		} catch (RepositoryFactoryException e) {
			doError(request, response, e);
			return;
		}
		
		// apply OWL2SKOS rules if needed
		try {
			// apply inference
			ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getOWL2SKOSRuleset()));
			au.execute(repository);
		} catch (RepositoryOperationException e1) {
			doError(request, response, e1.getMessage());
			return;
		}		
		
//		try {
//			// apply inference
//			ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getRulesetLite()));
//			au.execute(repository);
//		} catch (RepositoryOperationException e1) {
//			doError(request, response, e1.getMessage());
//			return;
//		}

		int count = -1;
		try {			
			// check that data does not contain more than X concepts
			count = Perform.on(repository).count(new SparqlQuery(new SparqlQueryBuilder(this, "CountConcepts.rq")));
			
			// check that data contains at least one SKOS Concept
			if(count <= 0) {
				doError(request, response, b.getString("upload.error.noConceptsFound"));
				return;
			}
			
			String limitConfiguration = Configuration.getDefault().getProperty(SkosPlayProperties.PROP_CONCEPTS_LIMIT);
			if(
					sourceType != SOURCE_TYPE.EXAMPLE
					&&
					limitConfiguration != null
					&&
					Integer.parseInt(limitConfiguration) > 0
					&&
					count > Integer.parseInt(limitConfiguration)
			) {
				doError(
						request,
						response,
						MessageFormat.format(
								b.getString("upload.error.dataTooLarge"),
								limitConfiguration
						)
				);
				return;
			}
			
			
		} catch (SparqlPerformException e) {
			e.printStackTrace();
			doError(request, response, e);
			return;
		}
		
		// store repository in the session
		sessionData.setRepository(repository);
		
		// store label reader in the session
		// default to no language
		final LabelReader labelReader = new LabelReader(repository, "", sessionData.getUserLocale().getLanguage());
		// add dcterms title and dc title
		labelReader.getProperties().add(URI.create(DCTERMS.TITLE.toString()));
		labelReader.getProperties().add(URI.create(DC.TITLE.toString()));
		sessionData.setLabelReader(labelReader);
		
		// build data structure
		final PrintFormData printFormData = new PrintFormData();
		sessionData.setPrintFormData(printFormData);
		
		// store success message with number of concepts
		printFormData.setSuccessMessage(MessageFormat.format(b.getString("print.message.numberOfConcepts"), count));
		
		try {
			// ask if some hierarchy exists
			if(!Perform.on(repository).ask(new SparqlQuery(new SparqlQueryBuilder(this, "AskBroadersOrNarrowers.rq")))) {
				printFormData.setEnableHierarchical(false);
				printFormData.getWarningMessages().add(b.getString("upload.warning.noHierarchyFound"));
			}
		} catch (SparqlPerformException e) {
			printFormData.setEnableHierarchical(false);
			printFormData.getWarningMessages().add(b.getString("upload.warning.noHierarchyFound"));
		}
		
		try {
			// ask if some translations exists
			if(!Perform.on(repository).ask(new SparqlQuery(new SparqlQueryBuilder(this, "AskTranslatedConcepts.rq")))) {
				printFormData.setEnableTranslations(false);
				printFormData.getWarningMessages().add(b.getString("upload.warning.noTranslationsFound"));
			}
		} catch (SparqlPerformException e) {
			printFormData.setEnableTranslations(false);
			printFormData.getWarningMessages().add(b.getString("upload.warning.noTranslationsFound"));
		}
			
		try {
			// retrieve number of concepts per concept schemes
			Perform.on(repository).select(new SelectSparqlHelper(
					new SparqlQueryBuilder(this, "ConceptCountByConceptSchemes.rq"),
					new TupleQueryResultHandlerBase() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							if(bindingSet.getValue("scheme") != null) {
								try {
									printFormData.getConceptCountByConceptSchemes().put(
											new LabeledResource(
													java.net.URI.create(bindingSet.getValue("scheme").stringValue()),
													LabelReader.display(labelReader.getValues((org.openrdf.model.URI)bindingSet.getValue("scheme")))
											),
											(bindingSet.getValue("conceptCount") != null)?
													((Literal)bindingSet.getValue("conceptCount")).intValue()
													:0
									);
								} catch (SparqlPerformException e) {
									throw new TupleQueryResultHandlerException(e);
								}
							}
						}						
					}
			));
			
			// retrieve list of declared languages in the data
			Perform.on(repository).select(new SelectSparqlHelper(
					new SparqlQueryBuilder(this, "ListOfSkosLanguages.rq"),
					new TupleQueryResultHandlerBase() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							String rdfLanguage = bindingSet.getValue("language").stringValue();
							Language l = fr.sparna.rdf.sesame.toolkit.languages.Languages.getInstance().withIso639P1(rdfLanguage);
							String languageName = (l != null)?l.displayIn(sessionData.getUserLocale().getLanguage()):rdfLanguage;
							printFormData.getLanguages().put(
									bindingSet.getValue("language").stringValue(),
									languageName									
							);
						}
						
					}
			));
		} catch (SparqlPerformException e) {
			doError(request, response, e);
			return;
		}
		
		// forward to the JSP
		getServletContext().getRequestDispatcher("/print.jsp").forward(request, response);
	}	
	

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response
	) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	@Override
	public void init(ServletConfig cfg) throws ServletException {
		// call superclass to store the servletconfig
		super.init(cfg);
	}


	protected void doError(
			HttpServletRequest request,
			HttpServletResponse response,
			Exception e
	) throws ServletException, IOException {
		// print stack trace
		e.printStackTrace();
		// build on-screen error message
		StringBuffer message = new StringBuffer(e.getMessage());
		Throwable current = e.getCause();
		while(current != null) {
			message.append(". Cause : "+current.getMessage());
			current = current.getCause();
		}
		doError(request, response, message.toString());
		return;
	}
	
	protected void doError(
			HttpServletRequest request,
			HttpServletResponse response,
			String message
	) throws ServletException, IOException {
		UploadFormData data = new UploadFormData();
		data.setErrorMessage(message);
		request.setAttribute(UploadFormData.KEY, data);
		getServletContext().getRequestDispatcher("/upload.jsp").forward(request, response);
		return;
	}

}
