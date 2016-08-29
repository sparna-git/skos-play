package fr.sparna.rdf.datapress.web;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterFactory;
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

import fr.sparna.rdf.RepositoryRDFHandler;
import fr.sparna.rdf.datapress.CompositePress;
import fr.sparna.rdf.datapress.DataPressException;
import fr.sparna.rdf.datapress.DataPressHandlerFactory;

@Controller
public class Api {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ServletContext servletContext;

	@Autowired
	protected DataPressHandlerFactory handlerFactory;
	
	@Autowired
	protected CompositePress thePress;
	
	private static final String DEFAULT_MEDIA_TYPE = "text/turtle";
	
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
	 * @throws DataPressException
	 */
	@RequestMapping(
			value="/api/v1/presse",
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
	) throws IOException, DataPressException {	
		log.debug("Presse GET. uri='{}', accept='{}', format='{}'", uri, accept, forceFormat);
        
        // create inMemory DB
        Repository cuve = new SailRepository(new MemoryStore());
        cuve.initialize();
        
        // parse
        RepositoryRDFHandler base = new RepositoryRDFHandler(cuve, SimpleValueFactory.getInstance().createIRI(uri));
        RDFHandler handler = this.handlerFactory.newHandler(base);
        thePress.press(uri, handler);
        
        // clean result
        cleanOutput(cuve, uri);
        
        // write final output
        final RDFFormat format = getFormat(accept.get(0), forceFormat);
        RDFWriter writer = RDFWriterRegistry.getInstance().get(format).get().getWriter(response.getOutputStream());
		// buffer and sort output
        cuve.getConnection().export(new BufferedGroupingRDFHandler(1024*24, writer));
        
        // set proper mime type in the response
        response.setContentType(writer.getRDFFormat().getDefaultMIMEType());
	}

	/**
	 * curl -X POST -F "content=</home/thomas/sparna/00-Clients/Sparna/Touraine/cartographie-v2/test-local-sparna.html" http://localhost:8080/extractor/api/presse
	 * 
	 * @param request
	 * @param response
	 * @param content
	 * @param uri
	 * @throws IOException
	 */
	@RequestMapping(
			value="/api/v1/presse",
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
	) throws IOException, DataPressException {	
	    log.debug("Presse POST. uri='{}', accept='{}', format='{}'", uri, accept, forceFormat);
        
        // create inMemory DB
        Repository cuve = new SailRepository(new MemoryStore());
        cuve.initialize();
        
        // parse
        RepositoryRDFHandler base = new RepositoryRDFHandler(cuve, SimpleValueFactory.getInstance().createIRI(uri));
        RDFHandler handler = this.handlerFactory.newHandler(base);
        thePress.press(content.getBytes(), uri, handler);
        
        // clean result
        cleanOutput(cuve, uri);
        
        // write final output
        final RDFFormat format = getFormat(accept.get(0), forceFormat);
        RDFWriter writer = RDFWriterRegistry.getInstance().get(format).get().getWriter(response.getOutputStream());
        // buffer and sort output
        cuve.getConnection().export(new BufferedGroupingRDFHandler(1024*24, writer)); 
        
        // set proper mime type in the response
        response.setContentType(writer.getRDFFormat().getDefaultMIMEType());
	}
	
	
	@RequestMapping(
			value="/api/v1/stocke",
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
	) throws IOException, DataPressException {
		log.debug("Stocke. uri='{}', accept='{}', format='{}'",uri, accept, forceFormat);
		
		// open connection to target repository
		Repository cuve = new HTTPRepository(Config.getInstance().getRepository());
		cuve.initialize();
		
		// clean target graph
		cuve.getConnection().clear(cuve.getValueFactory().createIRI(uri));
		
        // parse
		RepositoryRDFHandler base = new RepositoryRDFHandler(cuve, SimpleValueFactory.getInstance().createIRI(uri));
        RDFHandler handler = this.handlerFactory.newHandler(base);
        thePress.press(uri, handler);
        
        // write final output
        final RDFFormat format = getFormat(accept.get(0), forceFormat);
        RDFWriter writer = RDFWriterRegistry.getInstance().get(format).get().getWriter(response.getOutputStream());
        // buffer and sort output
        cuve.getConnection().export(new BufferedGroupingRDFHandler(1024*24, writer));
        
        // set proper mime type in the response
        response.setContentType(writer.getRDFFormat().getDefaultMIMEType());
	}
	
	private RDFFormat getFormat(MediaType mediaType, String forceFormat) {       
        String mediaTypeString;

		if(forceFormat != null) {
			log.debug("Forced media type to '{}'",forceFormat);
			mediaTypeString = forceFormat;
		} else {
			if (mediaType == null) {
	        	log.debug("No mediaType specified, using default media type '{}'",DEFAULT_MEDIA_TYPE);
	        	mediaTypeString = DEFAULT_MEDIA_TYPE;
	        } else {
	        	mediaTypeString = mediaType.getType()+"/"+mediaType.getSubtype();
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
	

	
	private void cleanOutput(Repository r, String documentUrl) {
		RepositoryConnection c = r.getConnection();
		try {
			final String DELETE_RDFA_UNRESOLVED_TERMS = "DELETE WHERE { ?s a <http://www.w3.org/ns/rdfa#UnresolvedTerm> . ?s ?p ?o }";
			Update u1 = c.prepareUpdate(DELETE_RDFA_UNRESOLVED_TERMS);
			// TODO : executer dans un Dataset
			u1.execute();
		} finally {
			c.close();
		}
	}
	

    
}
