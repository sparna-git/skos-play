package fr.sparna.commons.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

public class XSLProcessor {

	protected String factoryClassName;
	protected URIResolver uriResolver;
	
	// private constructor
	private XSLProcessor() {	
	}
	
	/**
	 * Creates a processor with JVM default processor
	 * @return
	 */
	public static XSLProcessor createDefaultProcessor() {
		XSLProcessor p = new XSLProcessor();
		return p;
	}
	
	public static XSLProcessor createSaxonProcessor() {
		XSLProcessor p = new XSLProcessor();
		p.factoryClassName = "net.sf.saxon.TransformerFactoryImpl";
		
		return p;
	}

	
	public Transformer createTransformer(Source xsltSource) 
	throws TransformerConfigurationException {
		TransformerFactory factory;
		if(this.factoryClassName != null) {
			factory = TransformerFactory.newInstance(this.factoryClassName, this.getClass().getClassLoader());
		} else {
			factory = TransformerFactory.newInstance();
		}
		
		if(this.uriResolver != null) {
			factory.setURIResolver(this.uriResolver);
		}		
		
		Transformer t = factory.newTransformer(xsltSource);
		// set indent to true
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		// necessaire pour avoir une indentation (avec l'implementation par defaut)
		// see http://stackoverflow.com/questions/1384802/java-how-to-indent-xml-generated-by-transformer
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		return t;
	}
	
	public void transform(
			InputStream xslInput,
			Node node,
			OutputStream output)
	throws TransformerException {
		this.createTransformer(new StreamSource(xslInput)).transform(new DOMSource(node), new StreamResult(output));
	}
	
	public void transform(
			String xslResource,
			Node node,
			OutputStream output)
	throws TransformerException {
		InputStream xslStream = this.getClass().getClassLoader().getResourceAsStream(xslResource);
		if(xslStream == null) {
			throw new InvalidParameterException("Cannot find XSL '"+xslResource+"' on the classpath");
		}
		this.createTransformer(new StreamSource(xslStream)).transform(new DOMSource(node), new StreamResult(output));
	}

	public String getFactoryClassName() {
		return factoryClassName;
	}

	public void setFactoryClassName(String factoryClassName) {
		this.factoryClassName = factoryClassName;
	}

	public URIResolver getUriResolver() {
		return uriResolver;
	}

	public void setUriResolver(URIResolver uriResolver) {
		this.uriResolver = uriResolver;
	}
	
}
