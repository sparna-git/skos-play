package fr.sparna.rdf.datapress;

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

public class DataPressSourceFactory {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Create a source from an IRI (fetch the IRI content, mount it in DOM)
	 * 
	 * @param iri
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public DataPressSource buildSource(IRI iri) throws IOException, SAXException {
		SimpleDataPressSource source = new SimpleDataPressSource(iri);
		source.setContent(read(iri.stringValue()));
		source.setContentDom(createDOM(source.getContent()));
		
		String iriString = iri.stringValue();
		source.setDocumentIri(SimpleValueFactory.getInstance().createIRI(
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
	public DataPressSource buildSource(IRI iri, byte[] content) throws SAXException, IOException {
		SimpleDataPressSource source = new SimpleDataPressSource(iri);
		source.setContent(content);
		source.setContentDom(createDOM(source.getContent()));
		
		String iriString = iri.stringValue();
		source.setDocumentIri(SimpleValueFactory.getInstance().createIRI(
				((URI.create(iriString).getFragment() != null)?iriString.substring(0, iriString.lastIndexOf('#')):iriString)
      	));
		
		return source;
	}

	/**
	 * Fetches a URL content into a raw byte array.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private byte[] read(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpget);
		
		byte[] result = null;
		try {
		    HttpEntity entity = response.getEntity();
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        try {
		            result = InputStreamUtil.readToBytes(instream);
		        } finally {
		            instream.close();
		        }
		    }
		} finally {
		    response.close();
		}
		
		return result;
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
