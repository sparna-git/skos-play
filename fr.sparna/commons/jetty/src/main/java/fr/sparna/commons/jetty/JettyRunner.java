package fr.sparna.commons.jetty;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import fr.sparna.commons.io.ClasspathUnzip;
import fr.sparna.commons.io.FileUtil;

public class JettyRunner {

	// path to the war resource in the classpath
	protected String warPath;
	
	// port number to run the server on
	protected int port = 5151;
	
	// context URL to run the server on
	protected String context;
	
	// working directory of the server where war file will be copied
	protected String workingDir = ".tmp";
	
	
	public JettyRunner(String warPath) {
		this.warPath = warPath;
	}
	
	public void run() throws JettyRunnerException {
		// create working directory
		final File workingDir = new File(this.workingDir);
		
		try {
			// unzip war in working directory
			ClasspathUnzip.unzipFileFromClassPath(this.warPath, workingDir.getAbsolutePath());
		} catch (IOException e) {
			throw new JettyRunnerException("Unable to extract "+this.warPath+" to directory "+workingDir.getAbsolutePath(), e);
		}
		
		Server server = new Server(this.port);
		 
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(this.context);
        webapp.setWar(new File(workingDir, new File(this.warPath).getName()).getAbsolutePath());
        server.setHandler(webapp);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                FileUtil.deleteFileRecursive(workingDir);
            }
        });
        
        try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new JettyRunnerException(e);
		}
	}
	
}
