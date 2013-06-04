package fr.sparna.rdf.skosplay;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.i18n.StrictResourceBundleControl;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.SelectSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.EndpointRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromURL;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;

public class UploadServlet extends HttpServlet {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	// source of the data : a file, a URL, an embedded example
	private static final String PARAM_SOURCE = "source";
	
	// if source == FILE, the uploaded file
	private static final String PARAM_FILE = "file";
	
	// if source == URL, the url
	private static final String PARAM_URL = "url";
	
	// if source == ENDPOINT, the endpoint
	private static final String PARAM_ENDPOINT = "endpoint";

	// if source == EXAMPLE, the resource path
	private static final String PARAM_EXAMPLE = "example";
	
	private enum SOURCE_TYPE {
		FILE,
		URL,
		EXAMPLE,
		ENDPOINT
	}
	
	@Override
	protected void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response
	) throws ServletException, IOException {
		
		// retrieve resource bundle for error messages
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.sparna.rdf.skosplay.i18n.Bundle",
				request.getLocale(),
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
		
		
		Repository repository;		
		try {
			switch(sourceType) {
			case FILE : {
				RepositoryBuilder builder = new RepositoryBuilder();
				// get uploaded file
				Object dataParam = request.getAttribute(PARAM_FILE);
				if(dataParam instanceof FileUploadException) {
					doError(request, response, "FileUploadException : "+((FileUploadException)dataParam).getMessage());
					return;
				}
				FileItem data = (FileItem)dataParam;
				
				// hack : fool the default Sesame's behavior to interpet *.xml files as RDF/XML
				builder.addOperation(new LoadFromStream(data.getInputStream(), RDFFormat.forFileName(data.getName().replaceAll("\\.xml", ".rdf"), RDFFormat.RDFXML)));
				repository = builder.createNewRepository();
				break;
			}
			case URL : {
				RepositoryBuilder builder = new RepositoryBuilder();
				// get url param
				String urlParam = (request.getParameter(PARAM_URL) != null && !request.getParameter(PARAM_URL).equals(""))?request.getParameter(PARAM_URL):null;
				builder.addOperation(new LoadFromURL(new URL(urlParam), false));
				repository = builder.createNewRepository();
				break;
			}
			case EXAMPLE : {
				// get resource param
				String resourceParam = (request.getParameter(PARAM_EXAMPLE) != null && !request.getParameter(PARAM_EXAMPLE).equals(""))?request.getParameter(PARAM_EXAMPLE):null;
				if(resourceParam == null || resourceParam.equals("")) {
					doError(request, response, "Select an example from the list.");
					return;
				}
				repository = (Repository)getServletContext().getAttribute(resourceParam);
				break;
			}
			case ENDPOINT : {				
				// get endpoint
				String endpointParam = (request.getParameter(PARAM_ENDPOINT) != null && !request.getParameter(PARAM_ENDPOINT).equals(""))?request.getParameter(PARAM_ENDPOINT):null;
				RepositoryBuilder builder = new RepositoryBuilder(new EndpointRepositoryFactory(endpointParam, false));
				repository = builder.createNewRepository();
				break;
			}
			default : {
				repository = null;
				break;
			}
			}
		} catch (RepositoryFactoryException e) {
			doError(request, response, e.getMessage());
			return;
		}
		
//		try {
//			// apply inference
//			ApplyUpdates au = new ApplyUpdates(SPARQLUpdate.fromUpdateList(SKOSRules.getRulesetLite()));
//			au.execute(repository);
//		} catch (RepositoryOperationException e1) {
//			doError(request, response, e1.getMessage());
//			return;
//		}

		int count = -1;
		try {			
			// check that data does not contain more than X concepts
			count = Perform.on(repository).count(new SPARQLQuery(new SPARQLQueryBuilder(this, "CountConcepts.rq")));
			
			// check that data contains at least one SKOS Concept
			if(count <= 0) {
				doError(request, response, b.getString("upload.error.noConceptsFound"));
				return;
			}
			
			if(
					sourceType != SOURCE_TYPE.EXAMPLE
					&&
					count > 5000
			) {
				doError(request, response, b.getString("upload.error.dataTooLarge"));
				return;
			}
		} catch (SPARQLExecutionException e) {
			doError(request, response, e.getMessage());
			return;
		}
		
		// initiate session and store SessionData
		SessionData sessionData = new SessionData();
		sessionData.store(request.getSession());
		
		// store repository in the session
		sessionData.setRepository(repository);
		
		// store label reader in the session
		// 'en' is the fallback language if the preferred language is not found
		final LabelReader labelReader = new LabelReader(repository, "en", request.getLocale().getLanguage());
		sessionData.setLabelReader(labelReader);
		
		// build data structure
		final PrintFormData printFormData = new PrintFormData();
		sessionData.setPrintFormData(printFormData);
		
		// store success message with number of concepts
		printFormData.setSuccessMessage(MessageFormat.format(b.getString("print.message.numberOfConcepts"), count));
		
		try {
			// ask if some hierarchy exists
			if(!Perform.on(repository).ask(new SPARQLQuery(new SPARQLQueryBuilder(this, "AskBroadersOrNarrowers.rq")))) {
				printFormData.setEnableHierarchical(false);
				printFormData.setWarningMessage(b.getString("upload.warning.noHierarchyFound"));
			}
		} catch (SPARQLExecutionException e) {
			printFormData.setEnableHierarchical(false);
			printFormData.setWarningMessage(b.getString("upload.warning.noHierarchyFound"));
		}
			
		try {
			// retrieve number of concepts per concept schemes
			Perform.on(repository).select(new SelectSPARQLHelper(
					new SPARQLQueryBuilder(this, "ConceptCountByConceptSchemes.rq"),
					new TupleQueryResultHandlerBase() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							if(bindingSet.getValue("scheme") != null) {
								try {
									printFormData.getConceptCountByConceptSchemes().put(
											new LabeledResource(
													java.net.URI.create(bindingSet.getValue("scheme").stringValue()),
													LabelReader.display(labelReader.getLabels((org.openrdf.model.URI)bindingSet.getValue("scheme")))
											),
											(bindingSet.getValue("conceptCount") != null)?
													((Literal)bindingSet.getValue("conceptCount")).intValue()
													:0
									);
								} catch (SPARQLExecutionException e) {
									throw new TupleQueryResultHandlerException(e);
								}
							}
						}						
					}
			));
			
			// retrieve list of declared languages in the data
			Perform.on(repository).select(new SelectSPARQLHelper(
					new SPARQLQueryBuilder(this, "ListOfSkosLanguages.rq"),
					new TupleQueryResultHandlerBase() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							printFormData.getLanguages().add(bindingSet.getValue("language").stringValue());
						}
						
					}
			));
		} catch (SPARQLExecutionException e) {
			doError(request, response, e.getMessage());
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
		List<String> exampleDatas = Arrays.asList(new String[]{
				"data/unesco/unescothes.ttl",
				"data/w/matieres.rdf",
				"data/nyt/nyt-descriptors.ttl"
		});
		for (String aData : exampleDatas) {
			try {
				RepositoryBuilder builder = new RepositoryBuilder();
				builder.addOperation(new LoadFromStream(this, aData));
				this.getServletContext().setAttribute(aData, builder.createNewRepository());
			} catch (RepositoryFactoryException e) {
				throw new ServletException(e);
			}
		}
	}


	protected void doError(
			HttpServletRequest request,
			HttpServletResponse response,
			String errorMessage
	) throws ServletException, IOException {
		UploadFormData data = new UploadFormData();
		data.setErrorMessage(errorMessage);
		request.setAttribute(UploadFormData.KEY, data);
		getServletContext().getRequestDispatcher("/upload.jsp").forward(request, response);
		return;
	}

}
