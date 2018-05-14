package fr.sparna.rdf.extractor.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.helpers.BufferedGroupingRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import fr.sparna.rdf.extractor.CompositeExtractor;
import fr.sparna.rdf.extractor.DataExtractionException;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractionSourceFactory;
import fr.sparna.rdf.extractor.DataExtractorHandlerFactory;
import fr.sparna.rdf.extractor.NotifyingDataExtractor;
import fr.sparna.rdf.extractor.NotifyingDataExtractorWrapper;
import fr.sparna.rdf.extractor.RepositoryManagementListener;

@Controller
public class Api {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ServletContext servletContext;

	@Autowired
	protected DataExtractorHandlerFactory handlerFactory;
	
	@Autowired
	protected CompositeExtractor extractor;
	
	// we want RDF/XML as a default because some browser don't Accept XML be default
	// we thus get a raw downloadable Turtle file instead of something that opens
	// in the browser
	private static final String DEFAULT_MEDIA_TYPE = "application/rdf+xml";
	
	/**
	 * curl http://localhost:8080/data-press/api/presse?uri=http://sparna.fr
	 * curl --header "Accept: application/ld+json" http://localhost:8080/data-press/api/presse?uri=http://sparna.fr
	 * curl --header "Accept: application/rdf+xml" http://localhost:8080/data-press/api/presse?uri=http://sparna.fr
	 * curl  "http://localhost:8080/data-press/api/presse?uri=http://sparna.fr&format=application/rdf%2Bxml"
	 * 
	 * @param request
	 * @param response
	 * @param uri
	 * @throws IOException
	 * @throws DataExtractionException
	 * @throws SAXException 
	 */
	@RequestMapping(
			value="/api/v1/extract",
			method = RequestMethod.GET
	)
	public void presse(
			HttpServletRequest request,
			HttpServletResponse response,
			// input URI to process
			@RequestParam(value="uri", required=true) String uri,
			// Accept header for conneg
			@RequestHeader(value="Accept") List<MediaType> accept,
			// format parameter to force output format
			@RequestParam(value="format", required=false) String forceFormat
	) throws IOException, DataExtractionException, SAXException {	
		log.debug("Presse GET. uri='{}', accept='{}', format='{}'", uri, accept, forceFormat);
        
        // create inMemory DB
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();
        
        // create source
        DataExtractionSource source = new DataExtractionSourceFactory().buildSource(repo.getValueFactory().createIRI(uri));
        
        // create a notifying DataPress that will handle cleaning and administrive metadata of the repository
        NotifyingDataExtractor notifyingDataExtractor = new NotifyingDataExtractorWrapper(extractor, new RepositoryManagementListener(repo));
        
        try(RepositoryConnection connection = repo.getConnection()) {
	        // create the target handler
	        RDFHandler handler = this.handlerFactory.newHandler(connection, source.getDocumentIri());
	        
	        // extract
	        notifyingDataExtractor.extract(source, handler);    
        }
        
        // determine output format and write response
        final RDFFormat format = getFormat(accept, forceFormat);
        output(
        		response,
        		this.handlerFactory.getTargetGraphIri(source.getDocumentIri()),
        		format,
        		repo
        );
	}

	/**
	 * curl -X POST -F "content=</home/thomas/sparna/00-Clients/Sparna/Touraine/cartographie-v2/test-local-sparna.html" http://localhost:8080/extractor/api/presse
	 * 
	 * @param request
	 * @param response
	 * @param content
	 * @param uri
	 * @throws IOException
	 * @throws SAXException 
	 */
	@RequestMapping(
			value="/api/v1/extract",
			method = RequestMethod.POST
	)
	public void presse(
			HttpServletRequest request,
			HttpServletResponse response,
			// content to process
			@RequestParam(value="content", required=true) String content,
			// input URI to process
			@RequestParam(value="uri", required=true) String uri,
			// Accept header for conneg
			@RequestHeader(value="Accept") List<MediaType> accept,
			// format parameter to force output format
			@RequestParam(value="format", required=false) String forceFormat
	) throws IOException, DataExtractionException, SAXException {	
	    log.debug("Presse POST. uri='{}', accept='{}', format='{}'", uri, accept, forceFormat);
        
        // create inMemory DB
        Repository repo = new SailRepository(new MemoryStore());
        repo.initialize();
        
        // create source
        DataExtractionSource source = new DataExtractionSourceFactory().buildSource(repo.getValueFactory().createIRI(uri), content.getBytes());
        
        // create a notifying DataPress that will handle cleaning and administrive metadata of the repository
        NotifyingDataExtractor notifyingDataExtractor = new NotifyingDataExtractorWrapper(extractor, new RepositoryManagementListener(repo));
        
        try(RepositoryConnection connection = repo.getConnection()) {
	        // create the target handler
	        RDFHandler handler = this.handlerFactory.newHandler(connection, source.getDocumentIri());
	        
	        // extract
	        notifyingDataExtractor.extract(source, handler);  
        }
        
        // determine output format and write response
        final RDFFormat format = getFormat(accept, forceFormat);
        output(
        		response,
        		this.handlerFactory.getTargetGraphIri(source.getDocumentIri()),
        		format,
        		repo
        );

	}
	
	
	@RequestMapping(
			value="/api/v1/store",
			method = RequestMethod.GET
	)
	private void stocke(
			HttpServletRequest request,
			HttpServletResponse response,
			// input URI to process
			@RequestParam(value="uri", required=true) String uri,
			// Accept header for conneg
			@RequestHeader(value="Accept") List<MediaType> accept,
			// format parameter to force output format
			@RequestParam(value="format", required=false) String forceFormat
	) throws IOException, DataExtractionException, SAXException {
		log.debug("Stocke GET. uri='{}', accept='{}', format='{}'",uri, accept, forceFormat);
		
		// open connection to target repository
		Repository repo = new HTTPRepository(Config.getInstance().getRepository());
		repo.initialize();
		
		// create source
        DataExtractionSource source = new DataExtractionSourceFactory().buildSource(repo.getValueFactory().createIRI(uri));
		
        // create a notifying DataPress that will handle cleaning and administrive metadata of the repository
        NotifyingDataExtractor notifyingDataExtractor = new NotifyingDataExtractorWrapper(extractor, new RepositoryManagementListener(repo));
		
        try(RepositoryConnection connection = repo.getConnection()) {
	        // create the target handler
	        RDFHandler handler = this.handlerFactory.newHandler(connection, source.getDocumentIri());
	        
	        // extract
	        notifyingDataExtractor.extract(source, handler);
        }

      	// determine output format and write response
      	final RDFFormat format = getFormat(accept, forceFormat);
      	output(
      			response,
      			this.handlerFactory.getTargetGraphIri(source.getDocumentIri()),
      			format,
      			repo
      	);
	}

	@RequestMapping(
			value="/api/v1/store",
			method = RequestMethod.POST
	)
	private void stocke(
			HttpServletRequest request,
			HttpServletResponse response,
			// content to process
			@RequestParam(value="content", required=true) String content,
			// input URI to process
			@RequestParam(value="uri", required=true) String uri,
			// Accept header for conneg
			@RequestHeader(value="Accept") List<MediaType> accept,
			// format parameter to force output format
			@RequestParam(value="format", required=false) String forceFormat
	) throws IOException, DataExtractionException, SAXException {
		log.debug("Stocke POST. uri='{}', accept='{}', format='{}'",uri, accept, forceFormat);
		
		// open connection to target repository
		Repository repo = new HTTPRepository(Config.getInstance().getRepository());
		repo.initialize();
		
		// create source
        DataExtractionSource source = new DataExtractionSourceFactory().buildSource(repo.getValueFactory().createIRI(uri), content.getBytes());
		
        // create a notifying DataPress that will handle cleaning and administrive metadata of the repository
        NotifyingDataExtractor notifyingDataExtractor = new NotifyingDataExtractorWrapper(extractor, new RepositoryManagementListener(repo));
		
        try(RepositoryConnection connection = repo.getConnection()) {
	        // create the target handler
	        RDFHandler handler = this.handlerFactory.newHandler(connection, source.getDocumentIri());
	        
	        // extract
	        notifyingDataExtractor.extract(source, handler);
        }

      	// determine output format and write response
      	final RDFFormat format = getFormat(accept, forceFormat);
      	output(
      			response,
      			this.handlerFactory.getTargetGraphIri(source.getDocumentIri()),
      			format,
      			repo
      	);
	}	
	
	
	private void output(
			HttpServletResponse response,
			IRI targetGraph,
			RDFFormat format,			
			Repository repo
	) throws IOException, DataExtractionException {
		// obtain correct writer
        RDFWriter writer = RDFWriterRegistry.getInstance().get(format).get().getWriter(response.getOutputStream());
        
        // set proper mime type in the response
        response.setContentType(writer.getRDFFormat().getDefaultMIMEType());
        // set proper charset in the response
        response.setCharacterEncoding(format.getCharset().name());
        
		// buffer and sort output
        repo.getConnection().export(new BufferedGroupingRDFHandler(1024*24, writer), targetGraph);		
	}
	
	private RDFFormat getFormat(List<MediaType> mediaTypes, String forceFormat) {       
        String mediaTypeString = null;

		if(forceFormat != null) {
			log.debug("Forced media type to '{}'",forceFormat);
			mediaTypeString = forceFormat;
		} else {
			if (mediaTypes == null) {
	        	log.debug("No mediaType specified, using default media type '{}'",DEFAULT_MEDIA_TYPE);
	        	mediaTypeString = DEFAULT_MEDIA_TYPE;
	        } else {
	        	// determine media-type to use, take the first one that corresponds to an RDF format
	        	for (MediaType aMediaType : mediaTypes) {
					if(getFormatForMediaType(aMediaType) != null) {
						log.debug("Found valid mediaType : '{}'", aMediaType);
						mediaTypeString = aMediaType.getType()+"/"+aMediaType.getSubtype();
						break;
					} else {
						log.debug("Can't find RDF format for '{}'", aMediaType);
					}
				}
	        	
	        	if(mediaTypeString == null) {
	            	// if none is applicable, take the default one
	            	log.debug("No known mediaType found, defaulting to '{}'",DEFAULT_MEDIA_TYPE);
	            	mediaTypeString = DEFAULT_MEDIA_TYPE;
	        	}
	        }
		}

        RDFWriterRegistry registry = RDFWriterRegistry.getInstance();
        Optional<RDFFormat> outputRdfFormat = registry.getFileFormatForMIMEType(mediaTypeString);
        if(!outputRdfFormat.isPresent()) {
        	outputRdfFormat = registry.getFileFormatForMIMEType(DEFAULT_MEDIA_TYPE);
        	log.debug("Can't find RDF format for '{}', defaulting to '{}'", mediaTypeString, outputRdfFormat.get());
        }
        
        log.debug("Determined output RDF format '{}'",outputRdfFormat.get().getDefaultMIMEType());       
        return registry.get(outputRdfFormat.get()).get().getRDFFormat();
    }
	
	/**
	 * Return the associated RDFFormat, or null if none is applicable
	 * @param mediaType
	 * @return
	 */
	private RDFFormat getFormatForMediaType(MediaType mediaType) {       
        String mediaTypeString = mediaType.getType()+"/"+mediaType.getSubtype();

        RDFWriterRegistry registry = RDFWriterRegistry.getInstance();
        Optional<RDFFormat> outputRdfFormat = registry.getFileFormatForMIMEType(mediaTypeString);
        if(!outputRdfFormat.isPresent()) {
        	return null;
        } else {
        	return registry.get(outputRdfFormat.get()).get().getRDFFormat();
        }        
    }	

    
}
