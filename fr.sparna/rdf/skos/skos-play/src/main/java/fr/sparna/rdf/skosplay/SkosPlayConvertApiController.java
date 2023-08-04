package fr.sparna.rdf.skosplay;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.skosplay.log.LogEntry;
import fr.sparna.rdf.xls2rdf.ModelWriterIfc;
import fr.sparna.rdf.xls2rdf.Xls2RdfConverter;
import fr.sparna.rdf.xls2rdf.Xls2RdfException;
import fr.sparna.rdf.xls2rdf.Xls2RdfPostProcessorIfc;
import fr.sparna.rdf.xls2rdf.postprocess.SkosPostProcessor;
import fr.sparna.rdf.xls2rdf.postprocess.SkosXlPostProcessor;
import fr.sparna.rdf.xls2rdf.write.ModelWriterFactory;




/**
 * 
 * @author Thomas Francart
 *
 */
@Controller
@RequestMapping(value = "/api")
public class SkosPlayConvertApiController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ServletContext servletContext;

	@RequestMapping(value = "/convert",method = RequestMethod.GET)
	public ModelAndView convertRDF(
			@RequestParam(value="input", required=true) String input,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
	) throws Exception {
		
		final SessionData sessionData = SessionData.get(request.getSession());
		
		String format = "text/turtle";
		String language = "fr";
		boolean useskosxl = false;
		boolean useZip = false;
		boolean useGraph = false;
		boolean ignorePostProc = false;
		String url = input;
		
		// format
		RDFFormat theFormat = RDFWriterRegistry.getInstance().getFileFormatForMIMEType(format).orElse(RDFFormat.RDFXML);		

		URL baseURL = new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath());
		log.debug("Base URL is "+baseURL.toString());
		ConvertFormData data = new ConvertFormData();
		data.setBaseUrl(baseURL.toString());
		
		
		/**************************CONVERSION RDF**************************/
		InputStream in = null;
		String resultFileName = "skos-play-convert";
		

		log.debug("*Conversion à partir d'une Google Spreadsheet : "+input);
		
		// String googleSpreadsheetUrlString = input+"/export?format=xlsx";
		String googleSpreadsheetUrlString = input;
		log.debug("Google spreadsheet URL "+googleSpreadsheetUrlString);
		URL googleSpreadsheetUrl = new URL(googleSpreadsheetUrlString);
		
		InputStream urlInputStream = googleSpreadsheetUrl.openStream(); // throws an IOException
		urlInputStream = new DataInputStream(new BufferedInputStream(urlInputStream));

		final java.io.File tempFile = java.io.File.createTempFile("skosplay", ".xlsx");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(urlInputStream, out);
        }
        
        in = new FileInputStream(tempFile);
		
		try {
			log.debug("*Lancement de la conversion avec lang="+language+" et usexl="+useskosxl);
			// le content type est toujours positionné à "application/zip" si on nous a demandé un zip, sinon il dépend du format de retour demandé
			response.setContentType((useZip)?"application/zip":theFormat.getDefaultMIMEType());	
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
			response.reset();
			e.printStackTrace();
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

	private List<String> runConversion(ModelWriterIfc Writer, InputStream filefrom, String lang, boolean generatexl, boolean ignorePostProc) {
		Xls2RdfConverter converter = new Xls2RdfConverter(Writer, lang);
		List<Xls2RdfPostProcessorIfc> postProcessors = new ArrayList<>();
		if(!ignorePostProc) {
			postProcessors.add(new SkosPostProcessor());
		}
		if(!ignorePostProc && generatexl) {
			postProcessors.add(new SkosXlPostProcessor(generatexl, generatexl));
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
