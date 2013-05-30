package fr.sparna.commons.xml.fop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

public class FopProcessor {

	protected String outputMimeType = MimeConstants.MIME_PDF;
	protected boolean debugFo = false;
	
	public Fop createFop(OutputStream outStream) throws FOPException, TransformerException {
		// create an instance of fop factory
		FopFactory fopFactory = FopFactory.newInstance();
		// a user agent is needed for transformation
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

		// Construct fop with desired output format
		Fop fop = fopFactory.newFop(this.outputMimeType, foUserAgent, outStream);

		return fop;
	}

	/**
	 * Generates a PDF based on XSL-FO transformation of input XML.
	 * <p>If you need to write to a file :
	 * <code><pre>
	  		OutputStream out = new java.io.FileOutputStream(outputFile);
	    	BufferedOutputStream buf = new java.io.BufferedOutputStream(out);
			this.process(xmlSource, xslFoSource, buf);
			out.close();
			str.close();
	 * </pre></code>
	 * </p>
	 * 
	 * <p>If you need to write to a response :
	 * <code><pre>
			response.setContentLength(pdfBytes.length);
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment;filename=pdffile.pdf");
			response.getOutputStream().write(pdfBytes);
			response.getOutputStream().flush();
	 * </pre></code>
	 * </p>
	 * 
	 * @param xmlSource
	 * @param xslFoSource
	 * @return
	 * @throws FOPException
	 * @throws TransformerException
	 */
	public void process(
			Source xmlSource,
			Transformer t,
			OutputStream outStream
	) throws FOPException, TransformerException {

		// Construct fop with desired output format
		Fop fop = createFop(outStream);
		// Resulting SAX events (the generated FO) must be piped through to FOP
		Result res = new SAXResult(fop.getDefaultHandler());
		
		if(debugFo) {
			File debugFile = new File(".FopProcessor-debug.xml");
			if(!debugFile.exists()) {
				try {
					debugFile.createNewFile();
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
			
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(debugFile);
			} catch (FileNotFoundException ignore) {
				ignore.printStackTrace();
			}
			
			t.transform(xmlSource, new StreamResult(fos));
		}
		
		// Start XSLT transformation and FOP processing
		// everything will happen here..
		t.transform(xmlSource, res);
	}
	
	public void processToFile(
		Source xmlSource,
		Transformer t,
		File outputFile
	) throws FOPException, TransformerException, IOException {
		
		if(!outputFile.exists()) {
			if(outputFile.getParentFile() != null) {
				outputFile.getParentFile().mkdirs();
			}
			outputFile.createNewFile();
		}
		
	    BufferedOutputStream buf = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outputFile));
		try {
			this.process(xmlSource, t, buf);
		} finally {
			if(buf != null) { 
				buf.flush();
				buf.close();
				try { buf.close(); } catch(IOException ignore) { ignore.printStackTrace();	} 
			}
		}
	}

	public String getOutputMimeType() {
		return outputMimeType;
	}

	public void setOutputMimeType(String outputMimeType) {
		this.outputMimeType = outputMimeType;
	}

	public boolean isDebugFo() {
		return debugFo;
	}

	public void setDebugFo(boolean debugFo) {
		this.debugFo = debugFo;
	}	
	
}
