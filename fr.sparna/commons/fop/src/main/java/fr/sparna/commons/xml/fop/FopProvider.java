package fr.sparna.commons.xml.fop;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class FopProvider {
	
	private static Logger log = LoggerFactory.getLogger(FopProvider.class);
	
	private String fopUserConfigPath;
	protected String outputMimeType = MimeConstants.MIME_PDF;
	
	// need to garantee that fopFactory is initialized only once;
	private FopFactory fopFactory;

	public FopProvider() {
		super();
	}

	public FopProvider(String fopUserConfigPath) {
		super();
		this.fopUserConfigPath = fopUserConfigPath;
	}

	public FopFactory getFopFactory() {
		if(fopFactory == null) {
			fopFactory = this.createNewFopFactory();
		}
		
		return fopFactory;
	}
	
	private FopFactory createNewFopFactory() {
		FopFactory aFopFactory = FopFactory.newInstance();
		if(fopUserConfigPath != null) {
			try {
				aFopFactory.setUserConfig(new File(fopUserConfigPath));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			// s'il on ne donne pas de config, on en utilise une par defaut
			try {
				// on positionne la base URL
				String fontBaseURL = getClass().getResource("/fr/sparna/commons/xml/fop").toExternalForm();
				// jar:file:/home/thomas/.m2/repository/fr/sparna/commons/sparna-commons-fop/0.33-SNAPSHOT/sparna-commons-fop-0.33-SNAPSHOT.jar!/fr/sparna/commons/xml/fop
				log.debug("FopProvider setting FontBaseURL to "+fontBaseURL);
				aFopFactory.getFontManager().setFontBaseURL(fontBaseURL);
				// jar:file:/home/thomas/.m2/repository/fr/sparna/commons/sparna-commons-fop/0.33-SNAPSHOT/sparna-commons-fop-0.33-SNAPSHOT.jar!/fr/sparna/commons/xml/fop/fop-config.xml
				String fopConfigURI = this.getClass().getResource("fop-config.xml").toURI().toString();
				log.debug("FopProvider setting FOP config to "+fopConfigURI);
				aFopFactory.setUserConfig(fopConfigURI);
			} catch (Exception ignore) {
				throw new RuntimeException(ignore);
			}
		}
		
		return aFopFactory;
	}
	
//	public FOUserAgent createFOUserAgent() {
//		// create an instance of fop factory
//		FopFactory fopFactory = getFopFactory();
//		// a user agent is needed for transformation
//		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
//		
//		return foUserAgent;
//	}
	
	public Fop createFop(OutputStream outStream) throws FOPException, TransformerException {
		// create an instance of fop factory
		FopFactory fopFactory = this.getFopFactory();
		// a user agent is needed for transformation
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

		// Construct fop with desired output format
		Fop fop = fopFactory.newFop(
				this.outputMimeType,
				foUserAgent,
				outStream
		);

		return fop;
	}

	public String getOutputMimeType() {
		return outputMimeType;
	}

	public void setOutputMimeType(String outputMimeType) {
		this.outputMimeType = outputMimeType;
	}

	public String getFopUserConfigPath() {
		return fopUserConfigPath;
	}

}
