package fr.sparna.rdf.skosplay;


import fr.sparna.rdf.skosplay.log.LogEntry;
import fr.sparna.rdf.xls2rdf.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/skos-rest")
public class SkosPlayRestController {
  private Logger log = LoggerFactory.getLogger(this.getClass().getName());

  static ResponseEntity<ByteArrayResource> transformToByteArrayResource(String filename,String contentType, byte[] file) {
    return Optional.ofNullable(file)
                   .map(u -> ResponseEntity.ok()
                                           .contentType(MediaType.parseMediaType(contentType))
                                           .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +filename + "\"")
                                           .body(new ByteArrayResource(file)))
                   .orElse(ResponseEntity.badRequest().body(null));
  }

  private enum SOURCE_TYPE {
    FILE,
    URL,
    EXAMPLE,
  }

  /**
   * @param sourceString type of source ("file", "url", "example", "google")
   * @param file uploaded file if source=file
   * @param language language of the labels to generate
   * @param googleId ID of the google drive file if source=google
   * @param url URL of the file if source=url
   * @param format output format of the generated files
   * @param example reference of the example if source=example
   * @param useskosxl flag to generate SKOS-XL or not
   * @param useZip flag to output result in a ZIP file or not
   * @param useGraph flag to indicate if graph files should be generated or not
   * @param ignorePostProc flag to indicate if graph files should be generated or not
   * @param request the request
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/convert", method = RequestMethod.POST)
  public ResponseEntity<ByteArrayResource> convertRDF(
    @RequestParam(value="source", required=true) String sourceString,
    @RequestParam(value="file", required=false) MultipartFile file,
    @RequestParam(value="language", required=false) String language,
    @RequestParam(value="url", required=false) String url,
    @RequestParam(value="output", required=false) String format,
    @RequestParam(value="example", required=false) String example,
    @RequestParam(value="useskosxl", required=false) boolean useskosxl,
    @RequestParam(value="usezip", required=false) boolean useZip,
    @RequestParam(value="usegraph", required=false) boolean useGraph,
    @RequestParam(value="ignorePostProc", required=false) boolean ignorePostProc,
    HttpServletRequest request
  ) throws Exception {
    SkosPlayRestController.SOURCE_TYPE source = SkosPlayRestController.SOURCE_TYPE.valueOf(sourceString.toUpperCase());
    RDFFormat theFormat = RDFWriterRegistry.getInstance().getFileFormatForMIMEType(format).orElse(RDFFormat.RDFXML);

    URL baseURL = new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath());
    log.debug("Base URL is "+baseURL.toString());
    ConvertFormData data = new ConvertFormData();
    data.setBaseUrl(baseURL.toString());


    /**************************CONVERSION RDF**************************/
    InputStream in = null;
    String resultFileName = "skos-play-convert";

    switch(source) {
      case FILE : {
        log.debug("*Conversion à partir d'un fichier uploadé : "+file.getOriginalFilename());
        if(file.isEmpty()) {
          throw new RuntimeException("Uploaded file is empty");
        }

        in = file.getInputStream();
        // set the output file name to the name of the input file
        resultFileName = (file.getOriginalFilename().contains("."))?file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')):file.getOriginalFilename();
        break;
      }
      case URL: {
        log.debug("*Conversion à partir d'une URL : "+url);
        if(url.isEmpty()) {
          throw new RuntimeException("Uploaded link file is empty");
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
          log.error("error", e);
          throw new RuntimeException(e);
        }

        break;
      }
      default:
        throw new NotImplementedException();
    }

    try {
      log.debug("*Lancement de la conversion avec lang="+language+" et usexl="+useskosxl);
      resultFileName = (resultFileName.contains("."))?resultFileName.substring(0, resultFileName.lastIndexOf('.')):resultFileName;
      String extension = (useZip)?"zip":theFormat.getDefaultFileExtension();

      // add the date in the filename
      String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

      ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();

      List<String> identifiant = runConversion(
        new ModelWriterFactory(useZip, theFormat, useGraph).buildNewModelWriter(responseOutputStream),
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

      String filename = String.format("%s-%s.%s",resultFileName,dateString,extension);
      String contentType = useZip ?  "application/zip" : theFormat.getDefaultMIMEType();
      return  transformToByteArrayResource(filename, contentType, responseOutputStream.toByteArray());
    } catch (Exception e) {
      log.error("error",e);
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  private List<String> runConversion(ModelWriterIfc writer, InputStream filefrom, String lang, boolean generatexl, boolean ignorePostProc) {
    Xls2RdfConverter converter = new Xls2RdfConverter(writer, lang);
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
}
