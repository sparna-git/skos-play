package fr.sparna.commons.xml.fop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
		// FOP 1.1
		/*
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
		*/
		
		FopFactory aFopFactory;
		if(fopUserConfigPath != null) {
			try {
				aFopFactory = FopFactory.newInstance(new File(fopUserConfigPath));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			// s'il on ne donne pas de config, on en utilise une par defaut
			try {
				
				// use default fop-config.xml
				URL fopConfigUrl = this.getClass().getResource("fop-config.xml");
				log.debug("Init FOP with base URI "+fopConfigUrl.toURI());
				InputStream fopConfig = this.getClass().getResourceAsStream("fop-config.xml");
				
				// Now use a builder with a custom resource resolver able to read from classpath
				FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI(), new CustomPathResolver());
				aFopFactory = builder.setConfiguration(new DefaultConfigurationBuilder().build(fopConfig)).build();
			} catch (Exception ignore) {
				throw new RuntimeException(ignore);
			}
		}
		
		
		return aFopFactory;
	}
	
	public Fop createFop(OutputStream outStream) throws FOPException, TransformerException {
		// create an instance of fop factory
		FopFactory fopFactory = this.getFopFactory();
		// a user agent is needed for transformation
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		
		boolean isComplexScriptEnabled = foUserAgent.isComplexScriptFeaturesEnabled();
		log.debug("FOUserAgent complex scripts enabled ? "+isComplexScriptEnabled);

		// Construct fop with desired output format
		Fop fop = fopFactory.newFop(
				this.outputMimeType,
				foUserAgent,
				outStream
		);

		return fop;
	}
	
	/**
	 * Custom resource resolver that can load font files from the classpath
	 * @author Thomas Francart
	 *
	 */
    private static final class CustomPathResolver implements ResourceResolver {
        @Override
        public OutputStream getOutputStream(URI uri) throws IOException {
            return Thread.currentThread().getContextClassLoader().getResource(uri.toString()).openConnection()
                    .getOutputStream();
        }

        @Override
        public Resource getResource(URI uri) throws IOException {
        	// see https://stackoverflow.com/questions/17745133/load-a-font-from-jar-for-fop
        	//  InputStream inputStream = ClassLoader.getSystemResourceAsStream("fop/" + uri.get);
        	log.debug("Getting resource " + uri.toString());
        	String resourcePath = uri.toString().substring("file:/".length());
        	log.debug("Loading resource " + resourcePath);
        	if(Thread.currentThread().getContextClassLoader().getResource(resourcePath) == null) {
        		return null;
        	}
        	InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourcePath);
            return new Resource(inputStream);
        }
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
