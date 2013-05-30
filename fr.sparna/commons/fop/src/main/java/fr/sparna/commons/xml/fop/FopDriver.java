package fr.sparna.commons.xml.fop;

import java.io.OutputStream;

import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

public class FopDriver {

	protected String outputMimeType = MimeConstants.MIME_PDF;

	public Fop createFop(OutputStream outStream) throws FOPException, TransformerException {
		// create an instance of fop factory
		FopFactory fopFactory = FopFactory.newInstance();
		// a user agent is needed for transformation
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

		// Construct fop with desired output format
		Fop fop = fopFactory.newFop(this.outputMimeType, foUserAgent, outStream);

		return fop;
	}

	public String getOutputMimeType() {
		return outputMimeType;
	}

	public void setOutputMimeType(String outputMimeType) {
		this.outputMimeType = outputMimeType;
	}

}
