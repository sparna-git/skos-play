package fr.sparna.rdf.extractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.sparna.commons.io.InputStreamUtil;
import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

/**
 * Creates DataExtractionSource.
 * @author Thomas Francart
 *
 */
public class DataExtractionSourceFactory {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	// @see http://webmasters.stackexchange.com/questions/6205/what-user-agent-should-i-set
	protected String userAgent;
	
	public DataExtractionSourceFactory() {
		this(null);
	}
	
	public DataExtractionSourceFactory(String userAgent) {
		super();
		this.userAgent = userAgent;
	}

	/**
	 * Create a source from an IRI (fetch the IRI content, mount it in DOM)
	 * 
	 * @param iri
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public DataExtractionSource buildSource(IRI iri) throws IOException, SAXException {
		SimpleDataExtractionSource source = new SimpleDataExtractionSource(iri);
		
		// fetch the input document
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(iri.stringValue());
		if(this.userAgent != null) {
			httpget.setHeader("User-Agent", userAgent);
		}
		CloseableHttpResponse response = httpclient.execute(httpget);
		
		try {
		    HttpEntity entity = response.getEntity();
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        try {
		        	// read content to byte
		        	source.setContent(InputStreamUtil.readToBytes(instream));
		        } finally {
		            instream.close();
		        }
		        
		        // keep the response content type
				log.debug("Building a source with content/type "+entity.getContentType().getValue());
		        source.setContentType(entity.getContentType().getValue());		        
		    }
		} finally {
		    response.close();
		}
		
		// parse the DOM
		source.setContentDom(createDOM(source.getContent()));
		
		// parse the document IRI
		String iriString = iri.stringValue();
		source.setDocumentIri(SimpleValueFactory.getInstance().createIRI(
				// extract the domain IRI
      			((URI.create(iriString).getFragment() != null)?iriString.substring(0, iriString.lastIndexOf('#')):iriString)
      	));
		
		return source;
	}
	
	/**
	 * Create a source from a IRI and its content (mount the content in DOM)
	 * 
	 * @param iri
	 * @param content
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public DataExtractionSource buildSource(IRI iri, byte[] content) throws SAXException, IOException {
		SimpleDataExtractionSource source = new SimpleDataExtractionSource(iri);
		
		source.setContent(content);
		// TODO : have the content-type as a parameter ? for the moment suppose it is HTML
		source.setContentType("text/html");
		source.setContentDom(createDOM(source.getContent()));
		
		String iriString = iri.stringValue();
		source.setDocumentIri(SimpleValueFactory.getInstance().createIRI(
				((URI.create(iriString).getFragment() != null)?iriString.substring(0, iriString.lastIndexOf('#')):iriString)
      	));
		
		return source;
	}
	
	/**
	 * Parses a raw content into a DOM, using an HTML parser.
	 * 
	 * @param in
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document createDOM(byte[] in) throws SAXException, IOException {
		log.debug("Parsing document into DOM...");
		long start = System.currentTimeMillis();
		ByteArrayInputStream bais = new ByteArrayInputStream(in);
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(XmlViolationPolicy.ALTER_INFOSET);
		Document result = builder.parse(bais);
		log.debug("DOM parsing took "+(System.currentTimeMillis() - start)+"ms");
		return result;
	}
	
}
