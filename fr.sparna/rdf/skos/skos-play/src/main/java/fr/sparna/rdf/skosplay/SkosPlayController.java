package fr.sparna.rdf.skosplay;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerBase;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.sparna.commons.io.ReadWriteTextFile;
import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.google.GoogleAuthHelper;
import fr.sparna.i18n.StrictResourceBundleControl;
import fr.sparna.rdf.sesame.toolkit.languages.Languages.Language;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromUrl;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.autocomplete.Items;
import fr.sparna.rdf.skos.printer.autocomplete.JSONWriter;
import fr.sparna.rdf.skos.printer.reader.AbstractKosDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.AlignmentDataHarvesterCachedLoader;
import fr.sparna.rdf.skos.printer.reader.AlignmentDataHarvesterIfc;
import fr.sparna.rdf.skos.printer.reader.AlignmentDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.AlphaIndexDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.AutocompleteItemsReader;
import fr.sparna.rdf.skos.printer.reader.BodyReader;
import fr.sparna.rdf.skos.printer.reader.ConceptBlockReader;
import fr.sparna.rdf.skos.printer.reader.ConceptListDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.HeaderAndFooterReader;
import fr.sparna.rdf.skos.printer.reader.HierarchicalDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.IndexGenerator;
import fr.sparna.rdf.skos.printer.reader.IndexGenerator.IndexType;
import fr.sparna.rdf.skos.printer.reader.TranslationTableDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.TranslationTableReverseDisplayGenerator;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.toolkit.JsonSKOSTreePrinter;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.SKOSNodeSortCriteriaPreferredPropertyReader;
import fr.sparna.rdf.skos.toolkit.SKOSNodeTypeReader;
import fr.sparna.rdf.skos.toolkit.SKOSRules;
import fr.sparna.rdf.skos.toolkit.SKOSTreeBuilder;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode.NodeType;
import fr.sparna.rdf.skos.xls2skos.ModelWriterFactory;
import fr.sparna.rdf.skos.xls2skos.ModelWriterIfc;
import fr.sparna.rdf.skos.xls2skos.Xls2SkosConverter;
import fr.sparna.rdf.skosplay.log.LogController;
import fr.sparna.rdf.skosplay.log.SQLLogDao;
import fr.sparna.rdf.skosplay.log.UserDataDAO;




/**
 * The main entry point.
 * @Controller indicates this class will be the application controller, the main entry point.
 * 
 * To add an extra RequestMapping here, add the corresponding path to web.xml mappings.
 * 
 * @author Thomas Francart
 *
 */
@Controller
public class SkosPlayController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private static final String MIME_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; 
	
	
	@Autowired
	protected ServletContext servletContext;

	private enum SOURCE_TYPE {
		FILE,
		URL,
		EXAMPLE,
		GOOGLE,
	}

	@RequestMapping("/home")
	public ModelAndView home(HttpServletRequest request) {	
		if(SkosPlayConfig.getInstance().isPublishingMode()) {
			// if publishing mode, no home page
			return uploadForm();

		} else {
			// retrieve resource bundle for path to home page
			ResourceBundle b = ResourceBundle.getBundle(
					"fr.sparna.rdf.skosplay.i18n.Bundle",
					SessionData.get(request.getSession()).getUserLocale(),
					new StrictResourceBundleControl()
					);

			return new ModelAndView(b.getString("home.jsp"));
		}		
	}

	@RequestMapping("/about")
	public ModelAndView about(HttpServletRequest request) {

		// retrieve resource bundle for error messages
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.sparna.rdf.skosplay.i18n.Bundle",
				SessionData.get(request.getSession()).getUserLocale(),
				new StrictResourceBundleControl()
				);

		return new ModelAndView(b.getString("about.jsp"));
	}

	@RequestMapping("/style/custom.css")
	public void style(
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {	

		if(SkosPlayConfig.getInstance().getCustomCss() != null) {
			try {
				log.debug("Reading and returning custom CSS from "+SkosPlayConfig.getInstance().getCustomCss());
				String content = ReadWriteTextFile.getContents(SkosPlayConfig.getInstance().getCustomCss());
				response.getOutputStream().write(content.getBytes());
				response.flushBuffer();
			} catch (FileNotFoundException e) {
				// should not happen
				throw e;
			} catch (IOException e) {
				log.error("Exception while reading custom CSS from "+SkosPlayConfig.getInstance().getCustomCss().getAbsolutePath());
				throw e;
			}
		}
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView uploadForm() {
		// set an empty Model - just so that JSP can access SkosPlayConfig through it
		UploadFormData data = new UploadFormData();
		return new ModelAndView("upload", UploadFormData.KEY, data);
	}
	
	/**
	 * Méthode de téléchargement du résultat de la conversion google stockée dans la session
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/googleDriveConversion", method = RequestMethod.GET)
	public ModelAndView downloadGoogleResult(
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException  {
		log.debug("googleDriveConversion");
		// 1. récupérer la session de l'utilisateur
		final SessionData sessionData = SessionData.get(request.getSession());
		// 2. récupérer le résultat de la conversion stockée dans la session
		ByteArrayOutputStream conversionResult = sessionData.getGoogleConversionResult();
		String contentType = sessionData.getGoogleConversionResultContentType();
		// 3. écrire le résultat de la conversion dans la response
		response.setContentType(contentType);
		OutputStream out = response.getOutputStream();
		out.write(conversionResult.toByteArray());
		out.flush();
		return null;
	}

@RequestMapping(value = "/test",method = RequestMethod.GET)
	public ModelAndView connexion(
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception  {
		
		UploadFormData data = new UploadFormData();
		

		return new ModelAndView("test", UploadFormData.KEY, data);
		
	}	
			
@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
			@RequestParam(value="code", required=true) String code,
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException  {
		
	
		
		
		try{
			final SessionData sessionData = SessionData.get(request.getSession());
			// create the credentials and store it in session
			
			
			
			
			
		String urlParameters = "code="
                + code
                + "&client_id=611030822832-ea9cimuarqabdaof7e1munk90hr67mlo.apps.googleusercontent.com"
                + "&client_secret=vN-Q9H2dI7Oc4-I4KVaRJ2RI"
                + "&redirect_uri=http://localhost:8080/skos-play/login"
                + "&grant_type=authorization_code";
        
        //post parameters
        URL url = new URL("https://accounts.google.com/o/oauth2/token");
        URLConnection urlConn = url.openConnection();
        urlConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(
                urlConn.getOutputStream());
        writer.write(urlParameters);
        writer.flush();
        
        //get output in outputString 
        String line, outputString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                urlConn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            outputString += line;
        }
        System.out.println(outputString);
        
        //get Access Token 
        JsonObject json = (JsonObject)new JsonParser().parse(outputString);
        String access_token = json.get("access_token").getAsString();
        System.out.println(access_token);

        //get User Info 
        url = new URL(
                "https://www.googleapis.com/oauth2/v1/userinfo?access_token="
                        + access_token);
        urlConn = url.openConnection();
        outputString = "";
        reader = new BufferedReader(new InputStreamReader(
                urlConn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            outputString += line;
        }
        System.out.println(outputString);
        
        
        GooglePojo data = new Gson().fromJson(outputString, GooglePojo.class);
        sessionData.setUser(data);
        
        GoogleCredential credential = new GoogleCredential().setAccessToken(access_token);
        sessionData.setGoogleCredential(credential);
        Drive service = null;
		try {
			GoogleAuthHelper helper = new GoogleAuthHelper(null);
			sessionData.setGoogleAuthHelper(helper);
			helper.setApplicationName("SKOS Play !");
			service = helper.getDriveService(credential);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		String pageToken = null;
		
		do {
		    FileList result = service.files().list()
		            .setQ("mimeType='application/vnd.google-apps.spreadsheet'")
		            .setSpaces("drive")
		            .setFields("nextPageToken, files(id, name)")
		            .setPageToken(pageToken)
		            .execute();
		    
		   sessionData.setGoogleFile(result.getFiles());
		    for(File file: result.getFiles()) {
		    	
		        System.out.printf("Found file: %s (%s)\n",
		                file.getName(), file.getId());
		    }
		    pageToken = result.getNextPageToken();
		} while (pageToken != null);


        writer.close();
        reader.close();
        
		 
        
    } catch (MalformedURLException e) {
        System.out.println( e);
    } catch (ProtocolException e) {
        System.out.println( e);
    } catch (IOException e) {
        System.out.println( e);
    }
    System.out.println("leaving doGet");
    response.sendRedirect("convert");
    return null;
	}
	
	
	@RequestMapping(value = "/convert", method = RequestMethod.GET)
	public ModelAndView convertForm(
			HttpServletRequest request,
			HttpServletResponse response
	) throws IOException  {
		final SessionData sessionData = SessionData.get(request.getSession());
		if(sessionData.getGoogleCredential() != null && sessionData.getConvertFormData() != null && sessionData.getConvertFormData().getGoogleId() != null) {
			GoogleAuthHelper auth = sessionData.getGoogleAuthHelper();
			ConvertFormData data = sessionData.getConvertFormData();
			Drive service=auth.getDriveService(sessionData.getGoogleCredential());
			// format
			RDFFormat theFormat=RDFWriterRegistry.getInstance().getFileFormatForMIMEType(data.getOutput()).orElse(RDFFormat.RDFXML);
			// reset login markup in session
			sessionData.setConvertFormData(null);
			// convert
			return convert(
					service,
					data.getGoogleId(),
					sessionData,
					data.isUseXl(),
					data.isUseZip(),
					data.getLanguage(),
					theFormat,
					request
			);
		} else {
			// si pas de code en paramètre, on renvoie vers la page de formulaire
			ConvertFormData data = new ConvertFormData();
			URL baseURL = new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath());
			data.setBaseUrl(baseURL.toString());
			data.setDefaultLanguage(SessionData.get(request.getSession()).getUserLocale().getLanguage());
			return new ModelAndView("convert", ConvertFormData.KEY, data);
		}
	}

	@RequestMapping(value = "/convert",method = RequestMethod.POST)
	public ModelAndView convertRDF(
			// type of source ("file", "url", "example", "google")
			@RequestParam(value="source", required=true) String sourceString,
			// uploaded file if source=file
			@RequestParam(value="file", required=false) MultipartFile file,		
			// language of the labels to generate
			@RequestParam(value="language", required=false) String language,
			// ID of the google drive file if source=google
			@RequestParam(value="google", required=false) String googleId,
			// URL of the file if source=url
			@RequestParam(value="url", required=false) String url,
			// output format of the generated files
			@RequestParam(value="output", required=false) String format,
			// reference of the example if source=example
			@RequestParam(value="example", required=false) String example,
			// flag to generate SKOS-XL or not
			@RequestParam(value="usexl", required=false) boolean usexl,
			// flag to output result in a ZIP file or not
			@RequestParam(value="usezip", required=false) boolean useZip,
			// flag to indicate if graph files should be generated or not
			@RequestParam(value="usegraph", required=false) boolean useGraph,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
	) throws Exception {
		
		
		log.debug("convert(source="+sourceString+",file="+file+"format="+format+",usexl="+usexl+",useZip="+useZip+"language="+language+",url="+url+",ex="+example+")");
		final SessionData sessionData = SessionData.get(request.getSession());
		//source, it can be: file, example, url or google
		SOURCE_TYPE source = SOURCE_TYPE.valueOf(sourceString.toUpperCase());
		// format
		RDFFormat theFormat = RDFWriterRegistry.getInstance().getFileFormatForMIMEType(format).orElse(RDFFormat.RDFXML);		
		URL baseURL = new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath());
		log.debug("Base URL is "+baseURL.toString());
		ConvertFormData data = new ConvertFormData();
		data.setBaseUrl(baseURL.toString());
		
		
		if(sourceString=="example")
			sessionData.setFichierexample(true);
		else sessionData.setFichierexample(true);
		
		/**************************CONVERSION RDF**************************/
		InputStream in = null;
		switch(source) {

		case GOOGLE:   {	

			log.debug("*Conversion à partir d'une Google Spreadsheet : "+googleId);

			if(googleId.isEmpty()) {
				return doErrorConvert(request, "Google ID is empty");
			}

			Credential credential=sessionData.getGoogleCredential();
			if(credential!=null) {	
				log.debug("credential found->authorization already granted");
				log.debug("conversion en cours...");
				GoogleAuthHelper auth=sessionData.getGoogleAuthHelper();
				Drive service=auth.getDriveService(credential);
				
				return convert(service,googleId,sessionData,usexl,useZip,language,theFormat,request);
			} else {
				/*log.debug("credential not found->authorization process");
				GoogleAuthHelper me = new GoogleAuthHelper(url_Redirect);
				me.setScopes(Arrays.asList(DriveScopes.DRIVE));
				me.setApplicationName("SKOS Play !");
				// enregistrement en session
				SessionData.get(request.getSession()).setGoogleAuthHelper(me);
				
				// on stocke également les paramètres du formulaire en session
				ConvertFormData cfd = new ConvertFormData();
				cfd.setGoogleId(googleId);
				cfd.setLanguage(language);
				cfd.setOutput(format);
				cfd.setUseXl(usexl);
				cfd.setUseZip(useZip);
				SessionData.get(request.getSession()).setConvertFormData(cfd);
				
				//redirection vers google pour l'autorisation
				response.sendRedirect(me.getAuthorizationUrl());
				// on sort tout de suite de la méthode après avoir fait un redirect
				return null;*/
			}
		}					

		case EXAMPLE : {
			log.debug("*Conversion à partir d'un fichier d'exemple : "+example);
			URL urls = new URL(example);
			InputStream urlInputStream = urls.openStream(); // throws an IOException
			in = new DataInputStream(new BufferedInputStream(urlInputStream));
			break;
		}
		case FILE : {

			log.debug("*Conversion à partir d'un fichier uploadé : "+file.getName());
			if(file.isEmpty()) {
				return doErrorConvert(request, "Uploaded file is empty");
			}		

			in = file.getInputStream();
			break;
		}
		case URL: {
			log.debug("*Conversion à partir d'une URL : "+url);
			sessionData.setListurl(url);
			if(url.isEmpty()) {
				return doErrorConvert(request, "Uploaded link file is empty");
			}

			try {
				URL urls = new URL(url);
				InputStream urlInputStream = urls.openStream(); // throws an IOException
				in = new DataInputStream(new BufferedInputStream(urlInputStream));
			} catch(MalformedURLException errors) {
				errors.printStackTrace();
				return doErrorConvert(request, errors.getMessage()); 
			} catch (IOException ioeErrors) {
				ioeErrors.printStackTrace();
				return doErrorConvert(request, ioeErrors.getMessage()); 
			}
			

			break;
		}
		default:
			break;
		}
		SQLLogDao logs=new SQLLogDao();
		

		try {
			log.debug("*Lancement de la conversion avec lang="+language+" et usexl="+usexl);
			// le content type est toujours positionné à "application/zip" si on nous a demandé un zip, sinon il dépend du format de retour demandé
			response.setContentType((useZip)?"application/zip":theFormat.getDefaultMIMEType());	
			Set<String> identifiant=generateType(new ModelWriterFactory(useZip, theFormat, useGraph).buildNewModelWriter(response.getOutputStream()),in,language,usexl);
			String [] array=new String[identifiant.size()];
			array=identifiant.toArray(array);
			List uri=new ArrayList<String>(Arrays.asList(array));
			Collections.sort(uri);
			
			logs.writelog(language, null, null, SessionData.get(request.getSession()).getListurl(),"convert",uri.toString());
			
			System.out.println("vocabulaireid : "+uri);
			
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException ioe) { }
		}

		return null;
	}

	private Set<String> generateType(ModelWriterIfc Writer, InputStream filefrom, String lang, boolean generatexl) {
		Xls2SkosConverter converter = new Xls2SkosConverter(Writer, lang);
		converter.setGenerateXl(generatexl);
		converter.setGenerateXlDefinitions(generatexl);
		converter.processInputStream(filefrom);
		Set<String> identifiant=converter.getCsModels().keySet();
		return identifiant;
	}
	private ModelAndView convert(
			Drive service, 
			String id_fichier, 
			SessionData sessionData, 
			boolean useXl, 
			boolean useZip, 
			String language, 
			RDFFormat theFormat, 
			HttpServletRequest request
			)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		InputStream fluxLecture;
		DataInputStream content;
		try {
			service.files().export(id_fichier, MIME_TYPE_EXCEL).executeMediaAndDownloadTo(outputStream);
			fluxLecture = new ByteArrayInputStream(outputStream.toByteArray());
			content = new DataInputStream(new BufferedInputStream(fluxLecture));
		} catch (Exception e1) {
			String msg = e1.getMessage();
			int indexOfBeginMessage = msg.lastIndexOf("\"message\":")+"\"message\":".length()+2;
			int indexOfEndMessage = msg.indexOf("\"", indexOfBeginMessage);
			log.debug("message d'erreur lié à l'ID google->"+msg.substring(indexOfBeginMessage, indexOfEndMessage));
			return doErrorConvert(request,"Google message : \""+msg.substring(indexOfBeginMessage, indexOfEndMessage)+"\""); 
		}

		try
		{
			log.debug("Exécution de la conversion");
			// lancer la conversion et récupérer le résultat en mémoire
			ByteArrayOutputStream conversionResult = new ByteArrayOutputStream();
			Set<String> identifiant=generateType(new ModelWriterFactory(useZip, theFormat).buildNewModelWriter(conversionResult),content,language,useXl);
			log.debug("Conversion terminée");
			sessionData.setGoogleConversionResult(conversionResult);
			// le content type est toujours positionné à "application/zip" si on nous a demandé un zip, sinon il dépend du format de retour demandé
			sessionData.setGoogleConversionResultContentType((useZip)?"application/zip":theFormat.getDefaultMIMEType());

			ConvertFormData data = new ConvertFormData();
			data.setGoogleId(id_fichier);

			return new ModelAndView("convert", ConvertFormData.KEY, data);
		} catch(Exception e) {
			e.printStackTrace();
			return doErrorConvert(request,e.getMessage());
		}

		finally {
			try {
				if(fluxLecture!= null) {
					fluxLecture.close();
				}
			} 
			catch (IOException ioe) { }
		}
	}


	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView upload(
			// radio box indicating type of input
			@RequestParam(value="source", required=true) String sourceString,
			// uploaded file if source=file
			@RequestParam(value="file", required=false) MultipartFile file,
			// reference example if source=example
			@RequestParam(value="example", required=false) String example,
			// url of file or SPARQL endpoint if source=url
			@RequestParam(value="url", required=false) String url,
			// flag indicating to apply RDFS inference or not
			@RequestParam(value="rdfsInference", required=false) boolean rdfsInference,
			// flag indicating to transform OWL to SKOS
			@RequestParam(value="owl2skos", required=false) boolean owl2skos,
			// flag indicating to transform SKOS-XL to SKOS
			@RequestParam(value="skosxl2skos", required=false) boolean skosxl2skos,
			HttpServletRequest request
			) throws IOException {
		
		log.debug("upload(source="+sourceString+",example="+example+",url="+url+",rdfsInference="+rdfsInference+", owl2skos="+owl2skos+")");

		// get the source
		SOURCE_TYPE source = SOURCE_TYPE.valueOf(sourceString.toUpperCase());		

		// retrieve session
		final SessionData sessionData = SessionData.get(request.getSession());
		
		// prepare data structure
		final PrintFormData printFormData = new PrintFormData();
		sessionData.setPrintFormData(printFormData);
		
		if(sourceString=="example")
			 sessionData.setFichierexample(true);
		else sessionData.setFichierexample(false);

		// retrieve resource bundle for error messages
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.sparna.rdf.skosplay.i18n.Bundle",
				sessionData.getUserLocale(),
				new StrictResourceBundleControl()
				);
		
		RepositoryBuilder localRepositoryBuilder;

		if(rdfsInference) {
			localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));			
			// load the SKOS model to be able to infer skos:inScheme from skos:isTopConceptOf
			localRepositoryBuilder.addOperation(new LoadFromFileOrDirectory("skos.rdf"));
		} else {
			localRepositoryBuilder = new RepositoryBuilder();
		}

		Repository repository;		
		try {
			switch(source) {
			case FILE : {
				// get uploaded file
				if(file.isEmpty()) {
					return doError(request, "Uploaded file is empty");
				}

				log.debug("Uploaded file name is "+file.getOriginalFilename());
				localRepositoryBuilder.addOperation(new LoadFromStream(file.getInputStream(), Rio.getParserFormatForFileName(file.getOriginalFilename()).orElse(RDFFormat.RDFXML)));
				repository = localRepositoryBuilder.createNewRepository();

				// apply rules if needed
				try {
					if(owl2skos) {
						// apply inference
						ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getOWL2SKOSRuleset()));
						au.execute(repository);
					}

					if(skosxl2skos) {
						// apply inference
						ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getSkosXl2SkosRuleset()));
						au.execute(repository);
					}
				} catch (RepositoryOperationException e1) {
					return doError(request, e1.getMessage());
				}

				break;
			}			
			case EXAMPLE : {
				// get resource param
				String resourceParam = example;
				
				if(resourceParam == null || resourceParam.equals("")) {
					return doError(request, "Select an example from the list.");
				}
				repository = SkosPlayConfig.getInstance().getApplicationData().getExampleDatas().get(resourceParam);

				// apply rules if needed
				try {
					if(owl2skos) {
						// apply inference
						ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getOWL2SKOSRuleset()));
						au.execute(repository);
					}

					if(skosxl2skos) {
						// apply inference
						ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getSkosXl2SkosRuleset()));
						au.execute(repository);
					}
				} catch (RepositoryOperationException e1) {
					return doError(request, e1.getMessage());
				}

				// set the loaded data name
				try {
					printFormData.setLoadedDataName(sessionData.getPreLoadedDataLabels().getString(example));
				} catch (Exception e) {
					// missing label : set the key
					printFormData.setLoadedDataName(example);
				}

				break;
			}
			case URL : {				
				// we are loading an RDF file from the web, use the localRepositoryBuilder and apply inference if required
				if(!StringRepositoryFactory.isEndpointURL(url)) {

					localRepositoryBuilder.addOperation(new LoadFromUrl(new URL(url)));
					repository = localRepositoryBuilder.createNewRepository();

					// apply OWL2SKOS rules if needed
					try {
						if(owl2skos && !StringRepositoryFactory.isEndpointURL(url)) {
							// apply inference
							ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getOWL2SKOSRuleset()));
							au.execute(repository);
						}
					} catch (RepositoryOperationException e1) {
						return doError(request, e1.getMessage());
					}
					sessionData.setListurl(url);
				} else {
					// this is a endpoint
					repository = RepositoryBuilder.fromString(url, rdfsInference);
				}

				break;
			}
			default : {
				repository = null;
				break;
			}
			}
		} catch (RepositoryFactoryException e) {
			return doError(request, e);
		}

		int count = -1;
		try {			
			// check that data does not contain more than X concepts
			count = Perform.on(repository).count(new SparqlQuery(new SparqlQueryBuilder(this, "CountConcepts.rq")));

			// check that data contains at least one SKOS Concept
			if(count <= 0) {
				return doError(request, b.getString("upload.error.noConceptsFound"));
			}

			int limitConfiguration = SkosPlayConfig.getInstance().getConceptsLimit();
			if(
					source != SOURCE_TYPE.EXAMPLE
					&&
					limitConfiguration > 0
					&&
					count > limitConfiguration
					) {
				return doError(
						request,
						MessageFormat.format(
								b.getString("upload.error.dataTooLarge"),
								limitConfiguration
								)
						);
			}			

		} catch (SparqlPerformException e) {
			e.printStackTrace();
			return doError(request, e);
		}

		// set loaded data licence, if any
		try {
			Value license = Perform.on(repository).read(new SparqlQuery(new SparqlQueryBuilder(this, "ReadLicense.rq")));
			if(license != null && license instanceof Literal) {
				printFormData.setLoadedDataLicense(((Literal)license).stringValue());
			}
		} catch (SparqlPerformException e) {
			e.printStackTrace();
			return doError(request, e);
		}


		// store repository in the session
		sessionData.setRepository(repository);

		// store sourceConceptLabel reader in the session
		// default to no language
		final LabelReader labelReader = new LabelReader(repository, "", sessionData.getUserLocale().getLanguage());
		// add dcterms title and dc title
		labelReader.getProperties().add(URI.create(DCTERMS.TITLE.toString()));
		labelReader.getProperties().add(URI.create(DC.TITLE.toString()));
		sessionData.setLabelReader(labelReader);

		// store success message with number of concepts
		printFormData.setSuccessMessage(MessageFormat.format(b.getString("print.message.numberOfConcepts"), count));

		if(DisplayType.needHierarchyCheck() || VizType.needHierarchyCheck()) {
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
		}

		if(DisplayType.needTranslationCheck()) {
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
		}

		if(DisplayType.needAlignmentCheck()) {
			try {
				// ask if some alignments exists
				if(!Perform.on(repository).ask(new SparqlQuery(new SparqlQueryBuilder(this, "AskMappings.rq")))) {
					printFormData.setEnableMappings(false);
					printFormData.getWarningMessages().add(b.getString("upload.warning.noMappingsFound"));
				}
			} catch (SparqlPerformException e) {
				printFormData.setEnableMappings(false);
				printFormData.getWarningMessages().add(b.getString("upload.warning.noMappingsFound"));
			}
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
													LabelReader.display(labelReader.getValues((org.eclipse.rdf4j.model.URI)bindingSet.getValue("scheme")))
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
			return doError(request, e);
		}


		return new ModelAndView("print");
	}

	protected ModelAndView doError(
			HttpServletRequest request,
			Exception e
			) {
		// print stack trace
		e.printStackTrace();
		// build on-screen error message
		StringBuffer message = new StringBuffer(e.getMessage());
		Throwable current = e.getCause();
		while(current != null) {
			message.append(". Cause : "+current.getMessage());
			current = current.getCause();
		}
		return doError(request, message.toString());
	}

	protected ModelAndView doError(
			HttpServletRequest request,
			String message
			) {
		UploadFormData data = new UploadFormData();
		data.setErrorMessage(message);
		request.setAttribute(UploadFormData.KEY, data);
		return new ModelAndView("upload");
	}

	protected ModelAndView doErrorConvert(
			HttpServletRequest request,
			String message
			) {
		ConvertFormData data = new ConvertFormData();
		data.setErrorMessagefile(message);
		request.setAttribute(ConvertFormData.KEY, data);
		return new ModelAndView("/convert");
	}


	@RequestMapping(
			value = "/visualize",
			method = RequestMethod.POST
			)
	public ModelAndView visualize(
			// output type, PDF or HTML
			@RequestParam(value="display", required=true) String displayParam,
			@RequestParam(value="language", defaultValue="no-language") String language,
			@RequestParam(value="scheme", defaultValue="no-scheme") String schemeParam,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {

		// get viz type param
		VizType displayType = (displayParam != null)?VizType.valueOf(displayParam.toUpperCase()):null;

		// get scheme param
		URI scheme = (schemeParam.equals("no-scheme"))?null:URI.create(schemeParam);

		// update source language param - only for translations
		language = (language.equals("no-language"))?null:language;

		// retrieve data from session
		Repository r = SessionData.get(request.getSession()).getRepository();

		// make a log to trace usage
		String aRandomConcept = Perform.on(r).read(new SparqlQuery(new SparqlQueryBuilder(this, "ReadRandomConcept.rq"))).stringValue();
		log.info("PRINT,"+SimpleDateFormat.getDateTimeInstance().format(new Date())+","+scheme+","+aRandomConcept+","+language+","+displayType+","+"HTML");
		SQLLogDao log=new SQLLogDao();
		log.writelog(language, "datavize", displayParam, SessionData.get(request.getSession()).getListurl(),"print",schemeParam);
		
		switch(displayType) {
		case PARTITION : {		
			request.setAttribute("dataset", generateJSON(r, language, scheme));
			// forward to the JSP
			return new ModelAndView("viz-partition");
		}
		case TREELAYOUT : {
			request.setAttribute("dataset", generateJSON(r, language, scheme));
			// forward to the JSP
			return new ModelAndView("viz-treelayout");
		}
		case SUNBURST : {
			request.setAttribute("dataset", generateJSON(r, language, scheme));
			// forward to the JSP
			return new ModelAndView("viz-sunburst");
		}
		/*case TREEMAP : {
			request.setAttribute("dataset", generateJSON(r, language, scheme));
			// forward to the JSP
			return new ModelAndView("viz-treemap");
		}*/
		case AUTOCOMPLETE : {
			AutocompleteItemsReader autocompleteReader = new AutocompleteItemsReader();
			Items items = autocompleteReader.readItems(r, language, scheme);
			JSONWriter writer = new JSONWriter();
			request.setAttribute("items", writer.write(items));
			// forward to the JSP
			return new ModelAndView("viz-autocomplete");
		}
		
		default : {
			throw new InvalidParameterException("Unknown display type "+displayType);
		}
		
		}
		
	}

	@RequestMapping(
			value = "/getData",
			method = RequestMethod.GET
			)
	public void getData(
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {

		// retrieve data from session
		Repository r = SessionData.get(request.getSession()).getRepository();

		// serialize and return data
		RepositoryWriter writer = new RepositoryWriter(r);
		writer.writeToStream(response.getOutputStream(), RDFFormat.TURTLE);

		// flush
		response.flushBuffer();
	}




	@RequestMapping(value = "/print", method = RequestMethod.POST)
	public void print(
			// output type, PDF or HTML
			@RequestParam(value="output", required=true) String outputParam,
			@RequestParam(value="display", required=true) String displayParam,
			@RequestParam(value="language", defaultValue="no-language") String language,
			@RequestParam(value="scheme", defaultValue="no-scheme") String schemeParam,
			@RequestParam(value="targetLanguage", defaultValue="no-language") String targetLanguageParam,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {

		// get output type param
		OutputType outputType = (outputParam != null)?OutputType.valueOf(outputParam.toUpperCase()):null;

		// get display type param
		DisplayType displayType = (displayParam != null)?DisplayType.valueOf(displayParam.toUpperCase()):null;

		// get scheme param
		URI scheme = (schemeParam.equals("no-scheme"))?null:URI.create(schemeParam);

		// update source language param - only for translations
		language = (language.equals("no-language"))?null:language;

		// get target language param - only for translations
		String targetLanguage = (targetLanguageParam != null)?(targetLanguageParam.equals("no-language")?null:targetLanguageParam):null;

		// retrieve data from session
		Repository r = SessionData.get(request.getSession()).getRepository();

		// make a log to trace usage
		String aRandomConcept = Perform.on(r).read(new SparqlQuery(new SparqlQueryBuilder(this, "ReadRandomConcept.rq"))).stringValue();
		log.info("PRINT,"+SimpleDateFormat.getDateTimeInstance().format(new Date())+","+scheme+","+aRandomConcept+","+language+","+displayType+","+outputType);

		// build display result
		KosDocument document = new KosDocument();


		HeaderAndFooterReader headerReader = new HeaderAndFooterReader(r);
		headerReader.setApplicationString("Generated by SKOS Play!, sparna.fr");
		// on désactive complètement le header pour les PDF
		if(outputType != OutputType.PDF) {
			// build and set header
			document.setHeader(headerReader.readHeader(language, scheme));
		}
		// all the time, set footer
		document.setFooter(headerReader.readFooter(language, scheme));

		// pass on Repository to skos-printer level
		BodyReader bodyReader;
		
		
		switch(displayType) {
		case ALPHABETICAL : {			
			ConceptBlockReader cbr = new ConceptBlockReader(r);
			bodyReader = new BodyReader(new AlphaIndexDisplayGenerator(r, cbr));			
			break;
		}
		case ALPHABETICAL_EXPANDED : {			
			ConceptBlockReader cbr = new ConceptBlockReader(r);
			cbr.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
			bodyReader = new BodyReader(new AlphaIndexDisplayGenerator(r, cbr));
			break;
		}
		case HIERARCHICAL : {
			bodyReader = new BodyReader(new HierarchicalDisplayGenerator(r, new ConceptBlockReader(r)));
			break;
		}
		case HIERARCHICAL_TREE : {
			bodyReader = new BodyReader(new HierarchicalDisplayGenerator(r, new ConceptBlockReader(r)));
			break;
		}
		//			case HIERARCHICAL_EXPANDED : {
		//				displayGenerator = new HierarchicalDisplayGenerator(r, new ConceptBlockReader(r, HierarchicalDisplayGenerator.EXPANDED_SKOS_PROPERTIES));
		//				break;
		//			}
		case CONCEPT_LISTING : {
			ConceptBlockReader cbr = new ConceptBlockReader(r);
			cbr.setSkosPropertiesToRead(ConceptListDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
			List<String> additionalLanguages = new ArrayList<String>();
			for (String aLang : SessionData.get(request.getSession()).getPrintFormData().getLanguages().keySet()) {
				if(!aLang.equals(language)) {
					additionalLanguages.add(aLang);
				}
			}
			cbr.setAdditionalLabelLanguagesToInclude(additionalLanguages);

			bodyReader = new BodyReader(new ConceptListDisplayGenerator(r, cbr));
			break;
		}
		case TRANSLATION_TABLE : {
			bodyReader = new BodyReader(new TranslationTableDisplayGenerator(r, new ConceptBlockReader(r), targetLanguage));
			break;
		}
		case PERMUTED_INDEX : {
			bodyReader = new BodyReader(new IndexGenerator(r, IndexType.KWAC));
			break;
		}
		case KWIC_INDEX : {
			bodyReader = new BodyReader(new IndexGenerator(r, IndexType.KWIC));
			break;
		}
		case COMPLETE_MONOLINGUAL : {

			// prepare a list of generators
			List<AbstractKosDisplayGenerator> generators = new ArrayList<AbstractKosDisplayGenerator>();

			// alphabetical display
			ConceptBlockReader alphaCbReader = new ConceptBlockReader(r);
			alphaCbReader.setStyleAttributes(true);
			alphaCbReader.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
			alphaCbReader.setLinkDestinationIdPrefix("hier");
			AlphaIndexDisplayGenerator alphaGen = new AlphaIndexDisplayGenerator(
					r,
					alphaCbReader,
					"alpha"
					);
			generators.add(alphaGen);

			// hierarchical display
			ConceptBlockReader hierCbReader = new ConceptBlockReader(r);
			hierCbReader.setLinkDestinationIdPrefix("alpha");
			HierarchicalDisplayGenerator hierarchyGen = new HierarchicalDisplayGenerator(
					r,
					hierCbReader,
					"hier"
					);
			generators.add(hierarchyGen);

			bodyReader = new BodyReader(generators);				

			break;
		}
		case COMPLETE_MULTILINGUAL : {

			// prepare a list of generators
			List<AbstractKosDisplayGenerator> generators = new ArrayList<AbstractKosDisplayGenerator>();

			// read all potential languages and exclude the main one
			final List<String> additionalLanguages = new ArrayList<String>();
			for (String aLang : SessionData.get(request.getSession()).getPrintFormData().getLanguages().keySet()) {
				if(!aLang.equals(language)) {
					additionalLanguages.add(aLang);
				}
			}

			// alphabetical display
			ConceptBlockReader alphaCbReader = new ConceptBlockReader(r);
			alphaCbReader.setStyleAttributes(true);
			alphaCbReader.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
			alphaCbReader.setAdditionalLabelLanguagesToInclude(additionalLanguages);
			alphaCbReader.setLinkDestinationIdPrefix("hier");
			AlphaIndexDisplayGenerator alphaGen = new AlphaIndexDisplayGenerator(
					r,
					alphaCbReader,
					"alpha"
					);
			generators.add(alphaGen);

			// hierarchical display
			ConceptBlockReader hierCbReader = new ConceptBlockReader(r);
			hierCbReader.setLinkDestinationIdPrefix("alpha");
			HierarchicalDisplayGenerator hierarchyGen = new HierarchicalDisplayGenerator(
					r,
					hierCbReader,
					"hier"
					);
			generators.add(hierarchyGen);

			// add translation tables for each additional languages
			for (int i=0;i<additionalLanguages.size(); i++) {
				String anAdditionalLang = additionalLanguages.get(i);
				ConceptBlockReader aCbReader = new ConceptBlockReader(r);
				aCbReader.setLinkDestinationIdPrefix("alpha");
				TranslationTableReverseDisplayGenerator ttGen = new TranslationTableReverseDisplayGenerator(
						r,
						aCbReader,
						anAdditionalLang,
						"trans"+i);
				generators.add(ttGen);
			}

			bodyReader = new BodyReader(generators);

			break;
		}
		case ALIGNMENT_ALPHA : {
			AlignmentDataHarvesterIfc harvester = new AlignmentDataHarvesterCachedLoader(null, RDFFormat.RDFXML);
			AlignmentDisplayGenerator adg = new AlignmentDisplayGenerator(r, new ConceptBlockReader(r), harvester);
			// this is the difference with other alignment display
			adg.setSeparateByTargetScheme(false);
			bodyReader = new BodyReader(adg);
			break;
		}
		case ALIGNMENT_BY_SCHEME : {
			AlignmentDataHarvesterIfc harvester = new AlignmentDataHarvesterCachedLoader(null, RDFFormat.RDFXML);
			AlignmentDisplayGenerator adg = new AlignmentDisplayGenerator(r, new ConceptBlockReader(r), harvester);
			// this is the difference with other alignment display
			adg.setSeparateByTargetScheme(true);
			bodyReader = new BodyReader(adg);
			break;
		}
		default :
			throw new InvalidParameterException("Unknown display type "+displayType);
		}	
		// read the body
		document.setBody(bodyReader.readBody(language, scheme));

		DisplayPrinter printer = new DisplayPrinter();
		// TODO : use Spring for configuration for easier debugging config
		// for the moment we desactivate debugging completely
		printer.setDebug(false);
		
		
		switch(outputType) {
		case HTML : {
			if(displayType==DisplayType.HIERARCHICAL)
			{
				printer.printToHtml(document, response.getOutputStream(), SessionData.get(request.getSession()).getUserLocale().getLanguage());

			}
			if(displayType==DisplayType.HIERARCHICAL_TREE)
			{
				printer.printToHtmlTree(document, response.getOutputStream(), SessionData.get(request.getSession()).getUserLocale().getLanguage());

			}
			break;
		}
		case PDF : {
			response.setContentType("application/pdf");
			// if alphabetical or concept listing display, set 2-columns layout
			if(
					displayType == DisplayType.ALPHABETICAL
					||
					displayType == DisplayType.CONCEPT_LISTING
					||
					displayType == DisplayType.ALPHABETICAL_EXPANDED
					) {
				printer.getTransformerParams().put("column-count", 2);
			}
			printer.printToPdf(document, response.getOutputStream(), SessionData.get(request.getSession()).getUserLocale().getLanguage());
			break;
		}
		}
		SQLLogDao log=new SQLLogDao();
		log.writelog(language, outputParam, displayParam, SessionData.get(request.getSession()).getListurl(),"print",schemeParam);
		response.flushBuffer();		
	}

	protected String generateJSON (
			Repository r,
			String language,
			URI scheme
			) throws Exception {

		// Careful : we need to use the same init code here than in the hierarhical display generator to get a consistent output
		PreferredPropertyReader ppr = new PreferredPropertyReader(
				r,
				Arrays.asList(new URI[] { URI.create(SKOS.NOTATION), URI.create(SKOS.PREF_LABEL) }),
				language
				);
		ppr.setCaching(true);

		PropertyReader typeReader = new PropertyReader(r, URI.create(RDF.TYPE.stringValue()));
		typeReader.setPreLoad(false);
		SKOSNodeTypeReader nodeTypeReader = new SKOSNodeTypeReader(typeReader, r);

		SKOSTreeBuilder builder = new SKOSTreeBuilder(r, new SKOSNodeSortCriteriaPreferredPropertyReader(ppr), nodeTypeReader);

		builder.setUseConceptSchemesAsFirstLevelNodes(false);

		GenericTree<SKOSTreeNode> tree = buildTree(builder, (scheme != null)?URI.create(scheme.toString()):null);			

		// writes json output
		LabelReader labelReader = new LabelReader(r, language);
		JsonSKOSTreePrinter printer = new JsonSKOSTreePrinter(labelReader);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		printer.print(tree, baos);
		return baos.toString("UTF-8").replaceAll("'", "\\\\'");
	}

	public GenericTree<SKOSTreeNode> buildTree(SKOSTreeBuilder builder, URI root)
			throws SparqlPerformException {
		GenericTree<SKOSTreeNode> tree = new GenericTree<SKOSTreeNode>();

		List<GenericTree<SKOSTreeNode>> trees;

		if(root != null) {	
			// generates tree
			log.debug("Building tree with root "+root);
			trees = builder.buildTrees(root);
		} else {
			// fetch all trees
			log.debug("Building tree with no particular root ");
			trees = builder.buildTrees();
		}

		// if only one, set it as root
		if(trees.size() == 1) {
			log.debug("Single tree found in the result");
			tree = trees.get(0);
		} else if (trees.size() ==0) {
			log.warn("Warning, no trees found");
		} else {
			log.debug("Multiple trees found ("+trees.size()+"), will create a fake root to group them all");
			// otherwise, create a fake root
			GenericTreeNode<SKOSTreeNode> fakeRoot = new GenericTreeNode<SKOSTreeNode>();
			fakeRoot.setData(new SKOSTreeNode(URI.create("skosplay:allData"), "", NodeType.UNKNOWN));

			// add all the trees under it					
			for (GenericTree<SKOSTreeNode> genericTree : trees) {
				log.debug("Addind tree under fake root : "+genericTree.getRoot().getData().getUri());
				fakeRoot.addChild(genericTree.getRoot());
			}

			// set the root of the tree
			tree.setRoot(fakeRoot);
		}				

		return tree;
	}

}
