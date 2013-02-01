package fr.sparna.commons.jetty;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import fr.sparna.commons.io.ClasspathUnzip;
import fr.sparna.commons.io.FileUtil;

public class JettyRunner implements Runnable {

	// path to the war resource in the classpath
	protected String warPath;
	
	// port number to run the server on
	protected int port = 5151;
	
	// context URL to run the server on
	protected String context;
	
	// working directory of the server where war file will be copied
	protected String workingDir = ".tmp";
	
	protected CountDownLatch latch;
	
	public JettyRunner(String warPath, String context, CountDownLatch latch) {
		this.warPath = warPath;
		this.context = context;
		this.latch = latch;
	}
	
	public JettyRunner(String warPath, CountDownLatch latch) {
		this(warPath, "/", latch);
	}
	
	public void run() {
		// create working directory
		final File workingDir = new File(this.workingDir);		
		
		if(this.getClass().getClassLoader().getResource(this.warPath) != null) {
			try {
				// unzip war in working directory
				ClasspathUnzip.unzipFileFromClassPath(this.warPath, workingDir.getAbsolutePath());
			} catch (IOException e) {
				throw new JettyRunnerException("Unable to extract "+this.warPath+" to directory "+workingDir.getAbsolutePath(), e);
			}
		} else {
			// TODO : don't need to copy, we could just make a reference to the file path
			// try with a file
			File warFile = new File(this.warPath);
			if(warFile.exists()) {
				try {
					FileUtil.copyFile(warFile, new File(workingDir.getAbsolutePath(), warFile.getName()));
				} catch (IOException e) {
					throw new JettyRunnerException("Unable to copy "+this.warPath+" to workingDir "+this.workingDir, e);
				}
			} else {
				throw new JettyRunnerException("Unable to find "+this.warPath+" in classpath or as a file");
			}
		}
		
		Server server = new Server(this.port);
		 
        WebAppContext webapp = new WebAppContext();
        System.out.println("Setting contextPath to '"+this.context+"'");
        webapp.setContextPath(this.context);
        System.out.println("Setting war to '"+new File(workingDir, new File(this.warPath).getName()).getAbsolutePath()+"'");
        webapp.setWar(new File(workingDir, new File(this.warPath).getName()).getAbsolutePath());
        server.setHandler(webapp);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
        	public void run() {
                FileUtil.deleteFileRecursive(workingDir);
            }
        });
        
        try {
        	System.out.println("Starting server...");
			server.start();
			latch.countDown();
			System.out.println("Joining server...");
			server.join();
		} catch (Exception e) {
			throw new JettyRunnerException(e);
		}
	}
	
	public int getPort() {
		return port;
	}

	public String getContext() {
		return context;
	}

	public static void main(String... args) throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		JettyRunner runner = new JettyRunner("/home/thomas/workspace/SolR/explorer-for-apache-solr-0.9.0/embedded.war", latch);
			
		ExecutorService s = Executors.newSingleThreadExecutor();
		s.execute(runner);
		
		latch.await();
		
		if(Desktop.isDesktopSupported()) {
			System.out.println("Desktop supported");
			Desktop.getDesktop().browse(URI.create("http://localhost:"+runner.getPort()+runner.getContext()));
		} else {
			System.out.println("Desktop not supported...");
		}
		
		System.out.println("toto");
		System.out.println("toto");
		System.out.println("toto");
	}
	
}
