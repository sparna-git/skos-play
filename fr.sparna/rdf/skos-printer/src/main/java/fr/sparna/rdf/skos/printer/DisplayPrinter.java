package fr.sparna.rdf.skos.printer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.xml.XSLProcessor;
import fr.sparna.commons.xml.fop.FopProcessor;
import fr.sparna.rdf.skos.printer.schema.Display;

public class DisplayPrinter {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static String STYLESHEET_DISPLAY_TO_HTML = "stylesheets/display-to-html.xsl";
	public static String STYLESHEET_DISPLAY_TO_FOP = "stylesheets/display-to-fop.xsl";
	
	protected boolean debug = true;
	
	protected Map<String, Object> transformerParams = new HashMap<String, Object>();
	
	
	public void printToPdf(
			Display display,
			File outputFile
	) throws FOPException, TransformerException, IOException, JAXBException {
		Marshaller m = createMarshaller();
		debugJAXBMarshalling(m, display);
		
		FopProcessor p = new FopProcessor();
		p.setDebugFo(debug);
		StreamSource xslSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(STYLESHEET_DISPLAY_TO_FOP));
		Transformer t = XSLProcessor.createDefaultProcessor().createTransformer(xslSource);
		if(this.transformerParams != null) {
			for (String aKey : this.transformerParams.keySet()) {
				t.setParameter(aKey, this.transformerParams.get(aKey));
			}
		}
		p.processToFile(
				new JAXBSource(m, display),
				t,
				outputFile
		);
	}
	
	public void printToPdf(
			Display display,
			OutputStream os
	) throws FOPException, TransformerException, IOException, JAXBException {
		Marshaller m = createMarshaller();
		debugJAXBMarshalling(m, display);
		
		FopProcessor p = new FopProcessor();
		p.setDebugFo(debug);
		StreamSource xslSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(STYLESHEET_DISPLAY_TO_FOP));
		Transformer t = XSLProcessor.createDefaultProcessor().createTransformer(xslSource);
		if(this.transformerParams != null) {
			for (String aKey : this.transformerParams.keySet()) {
				t.setParameter(aKey, this.transformerParams.get(aKey));
			}
		}
		p.process(
				new JAXBSource(m, display),
				t,
				os
		);
	}
	
	public void printToHtml(
			Display display,
			File htmlFile
	) throws FileNotFoundException, JAXBException, TransformerException {
		
		if(!htmlFile.exists()) {
			try {
				if(htmlFile.getParentFile() != null) {
					htmlFile.getParentFile().mkdirs();
				}
				htmlFile.createNewFile();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		}
		
		printToHtml(
				display,
				new BufferedOutputStream(new FileOutputStream(htmlFile))
		);
	}
	
	public void printToHtml(
			Display display,
			OutputStream os
	) throws FileNotFoundException, JAXBException, TransformerException {
		Marshaller m = createMarshaller();
		debugJAXBMarshalling(m, display);
		
		try {
			StreamSource xslSource = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(STYLESHEET_DISPLAY_TO_HTML));
			Transformer t = XSLProcessor.createDefaultProcessor().createTransformer(xslSource);
			if(this.transformerParams != null) {
				for (String aKey : this.transformerParams.keySet()) {
					t.setParameter(aKey, this.transformerParams.get(aKey));
				}
			}
			t.transform(new JAXBSource(m, display), new StreamResult(os));
		} finally {
			if(os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
		}	
	}
	
	private Marshaller createMarshaller() {
		try {
			return JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void debugJAXBMarshalling(Marshaller m, Display display) throws JAXBException {
		if(debug) {
			File debugFile = new File(".DisplayPrinter-debug.xml");
			log.debug("Will debug JAXB Marshalling in "+debugFile.getAbsolutePath());
			m.setProperty("jaxb.formatted.output", true);
			m.marshal(display, debugFile);
		}		
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Map<String, Object> getTransformerParams() {
		return transformerParams;
	}

	public void setTransformerParams(Map<String, Object> transformerParams) {
		this.transformerParams = transformerParams;
	}
	
	
	
}
