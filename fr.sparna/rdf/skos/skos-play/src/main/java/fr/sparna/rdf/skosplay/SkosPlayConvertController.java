package fr.sparna.rdf.skosplay;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import fr.sparna.google.DriveHelper;
import fr.sparna.google.GoogleConnector;
import fr.sparna.google.GoogleUser;
import fr.sparna.rdf.skosplay.log.LogEntry;

import fr.sparna.rdf.xls2rdf.ModelWriterIfc;

import fr.sparna.rdf.xls2rdf.Xls2RdfConverter;
import fr.sparna.rdf.xls2rdf.Xls2RdfException;
import fr.sparna.rdf.xls2rdf.Xls2RdfPostProcessorIfc;
import fr.sparna.rdf.xls2rdf.postprocess.SkosPostProcessor;
import fr.sparna.rdf.xls2rdf.postprocess.SkosXlPostProcessor;
import fr.sparna.rdf.xls2rdf.write.ModelWriterFactory;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;




/**
 *
 * @author Thomas Francart
 *
 */
@Controller
public class SkosPlayConvertController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ServletContext servletContext;

	private enum SOURCE_TYPE {
		FILE,
		URL,
		EXAMPLE,
		GOOGLE,
	}


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(
			@RequestParam(value="code", required=true) String code,
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException  {

		try{
			final SessionData sessionData = SessionData.get(request.getSession());
			GoogleConnector gc = sessionData.getGoogleConnector();

			// récupération du token d'accès
			String token = gc.getAccessToken(code);
			// récupération des infos utilisateur
			GoogleUser user = gc.readUserInfo(token);
			// enregistrement des infos utilisateur dans la session
			sessionData.setUser(user);
			// création et stockage en session d'un "Credential" google
			gc.createAndRegisterCredential(token);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.sendRedirect("convert");
		return null;
	}


	@RequestMapping(value = "/convert", method = RequestMethod.GET)
	public ModelAndView convertForm(
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException  {
		final SessionData sessionData = SessionData.get(request.getSession());
		ConvertFormData data = new ConvertFormData();

		if(sessionData.getUser() != null) {
			GoogleConnector gc = sessionData.getGoogleConnector();
			// récupération du service de Drive Google
			Drive service = gc.getDriveService();
			DriveHelper driveHelper = new DriveHelper(service);
			// récupération de la liste de spreadsheets et enregistrement dans la session
			List<File> listeSpreadsheets = driveHelper.listSpreadsheets();
			data.setGoogleFiles(listeSpreadsheets);
		}

		data.setDefaultLanguage(SessionData.get(request.getSession()).getUserLocale().getLanguage());
		return new ModelAndView("convert", ConvertFormData.KEY, data);
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
			@RequestParam(value="useskosxl", required=false) boolean useskosxl,
			// flag to generate broaderTransitive or not
			@RequestParam(value="broaderTransitive", required=false) boolean broaderTransitive,
			// flag to output result in a ZIP file or not
			@RequestParam(value="usezip", required=false) boolean useZip,
			// flag to indicate if graph files should be generated or not
			@RequestParam(value="usegraph", required=false) boolean useGraph,
			// flag to indicate if graph files should be generated or not
			@RequestParam(value="ignorePostProc", required=false) boolean ignorePostProc,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response
	) throws Exception {


		log.debug("convert(source="+sourceString+",file="+file+"format="+format+",usexl="+useskosxl+",broaderTransitive="+broaderTransitive+",useZip="+useZip+",language="+language+",url="+url+",ex="+example+")");
		final SessionData sessionData = SessionData.get(request.getSession());
		//source, it can be: file, example, url or google
		SOURCE_TYPE source = SOURCE_TYPE.valueOf(sourceString.toUpperCase());
		// format
		RDFFormat theFormat = RDFWriterRegistry.getInstance().getFileFormatForMIMEType(format).orElse(RDFFormat.RDFXML);

		URL baseURL = new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath());
		log.debug("Base URL is "+baseURL.toString());
		ConvertFormData data = new ConvertFormData();
		data.setBaseUrl(baseURL.toString());


		/**************************CONVERSION RDF**************************/
		InputStream in = null;
		String resultFileName = "skos-play-convert";

		switch(source) {

		case GOOGLE:   {
			log.debug("*Conversion à partir d'une Google Spreadsheet : "+googleId);

			if(googleId.isEmpty()) {
				return doErrorConvert(request, "Google ID is empty");
			}

			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				new DriveHelper(sessionData.getGoogleConnector().getDriveService()).readSpreadsheet(googleId, outputStream);
				in = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
			} catch (Exception e1) {
				String msg = e1.getMessage();
				int indexOfBeginMessage = msg.lastIndexOf("\"message\":")+"\"message\":".length()+2;
				int indexOfEndMessage = msg.indexOf("\"", indexOfBeginMessage);
				log.debug("message d'erreur lié à l'ID google->"+msg.substring(indexOfBeginMessage, indexOfEndMessage));
				return doErrorConvert(request,"Google message : \""+msg.substring(indexOfBeginMessage, indexOfEndMessage)+"\"");
			}

			break;
		}

		case EXAMPLE : {
			log.debug("*Conversion à partir d'un fichier d'exemple : "+example);
			URL exampleUrl = new URL(example);
			InputStream urlInputStream = exampleUrl.openStream(); // throws an IOException
			in = new DataInputStream(new BufferedInputStream(urlInputStream));
			// set the output file name to the name of the example
			resultFileName = (!exampleUrl.getPath().equals(""))?exampleUrl.getPath():resultFileName;
			// keep only latest file, after final /
			resultFileName = (resultFileName.contains("/"))?resultFileName.substring(resultFileName.lastIndexOf("/")+1):resultFileName;
			break;
		}
		case FILE : {
			log.debug("*Conversion à partir d'un fichier uploadé : "+file.getOriginalFilename());
			if(file.isEmpty()) {
				return doErrorConvert(request, "Uploaded file is empty");
			}

			in = file.getInputStream();
			// set the output file name to the name of the input file
			resultFileName = (file.getOriginalFilename().contains("."))?file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')):file.getOriginalFilename();
			break;
		}
		case URL: {
			log.debug("*Conversion à partir d'une URL : "+url);
			if(url.isEmpty()) {
				return doErrorConvert(request, "Uploaded link file is empty");
			}

			try {
				URL urls = new URL(url);
				InputStream urlInputStream = urls.openStream(); // throws an IOException
				in = new DataInputStream(new BufferedInputStream(urlInputStream));

				// set the output file name to the final part of the URL
				resultFileName = (!urls.getPath().equals(""))?urls.getPath():resultFileName;
				// keep only latest file, after final /
				resultFileName = (resultFileName.contains("/"))?resultFileName.substring(0, resultFileName.lastIndexOf("/")):resultFileName;
			} catch(IOException e) {
				e.printStackTrace();
				return doErrorConvert(request, e.getMessage());
			}

			break;
		}
		default:
			break;
		}


		try {
			
			// Always disable use of scientific annotation on numbers 
			System.setProperty("org.eclipse.rdf4j.rio.turtle.abbreviate_numbers", "false");			
			
			log.debug("*Lancement de la conversion avec lang="+language+" et usexl="+useskosxl);
			// le content type est toujours positionné à "application/zip" si on nous a demandé un zip, sinon il dépend du format de retour demandé
			response.setContentType((useZip)?"application/zip":theFormat.getDefaultMIMEType());
			// set response charset corresponding to the format, if applicable
			if(theFormat.hasCharset()) {
				response.setCharacterEncoding(theFormat.getCharset().name());
			}
			// le nom du fichier de retour
			// strip extension, if any
			resultFileName = (resultFileName.contains("."))?resultFileName.substring(0, resultFileName.lastIndexOf('.')):resultFileName;
			String extension = (useZip)?"zip":theFormat.getDefaultFileExtension();

			// add the date in the filename
			String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

			response.setHeader("Content-Disposition", "inline; filename=\""+resultFileName+"-"+dateString+"."+extension+"\"");

			List<String> identifiant = runConversion(
					new ModelWriterFactory(useZip, theFormat, useGraph).buildNewModelWriter(response.getOutputStream()),
					in,
					language.equals("")?null:language,
					useskosxl,
					broaderTransitive,
					ignorePostProc
			);

			// sort to garantee order
			List<String> uri=new ArrayList<String>(identifiant);
			Collections.sort(uri);

			// insert a log
			SkosPlayConfig.getInstance().getSqlLogDao().insertLog(new LogEntry(
					language,
					null,
					null,
					url,
					"convert",
					uri.toString()
			));

		} catch (Xls2RdfException e) {
			e.printStackTrace();
			response.reset();
			return doErrorConvert(request, e.getMessage());
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException ioe) { }
		}

		return null;
	}

	private List<String> runConversion(ModelWriterIfc writer, InputStream filefrom, String lang, boolean generatexl, boolean broaderTransitive, boolean ignorePostProc) {
		Xls2RdfConverter converter;
		if(lang == null || lang.trim().equals("")) {
			converter = new Xls2RdfConverter(writer);
		} else {
			converter = new Xls2RdfConverter(writer, lang);
		}
		converter.setFailIfNoReconcile(true);
		
		List<Xls2RdfPostProcessorIfc> postProcessors = new ArrayList<>();

		if(!ignorePostProc) {
			postProcessors.add(new SkosPostProcessor(broaderTransitive));

			if (generatexl) {
				postProcessors.add(new SkosXlPostProcessor(generatexl, generatexl));
			}
		}
		converter.setPostProcessors(postProcessors);

		converter.processInputStream(filefrom);
		return converter.getConvertedVocabularyIdentifiers();
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

}
