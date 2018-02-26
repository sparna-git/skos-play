package fr.sparna.rdf.skosplay;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.google.GoogleConnector;

public class SessionFilter implements Filter {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(
			ServletRequest request,
			ServletResponse response,
			FilterChain chain)
	throws IOException, ServletException {
		 // Check type request.
        if (request instanceof HttpServletRequest) {
            // Cast back to HttpServletRequest.
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Parse HttpServletRequest.
            HttpServletRequest parsedRequest = filterRequest(httpRequest);

            // Continue with filter chain.
            chain.doFilter(parsedRequest, response);
        } else {
            // Not a HttpServletRequest.
            chain.doFilter(request, response);
        }
		
	}

	private HttpServletRequest filterRequest(HttpServletRequest request) {
		
		SessionData session = SessionData.get(request.getSession());
		if(session == null) {
			log.debug("No session data present. Will create it.");
			session = new SessionData();
			session.store(request.getSession());
			
			// set up Locale
			session.setUserLocale(request.getLocale());
			// initialize pre-loaded labels
			initPreLoadedLabels(session);
			
			try {
				// init base URL
				URL baseURL = new URL("http://"+request.getServerName()+((request.getServerPort() != 80)?":"+request.getServerPort():"")+request.getContextPath());
				log.debug("Setting the base URL to "+baseURL.toString());
				session.setBaseUrl(baseURL.toString());		
			
				// init GoogleConnector
				String redirectUrl = baseURL.toString()+"/login";
				session.setGoogleConnector(new GoogleConnector("SKOS Play!", redirectUrl));
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		if(request.getParameter("lang") != null) {
			log.debug("Detected 'lang' param. Will set a new user locale.");
			session.setUserLocale(new Locale(request.getParameter("lang")));
			
			// initialize pre-loaded labels
			initPreLoadedLabels(session);
		}
		
		return request;
	}
	
	private void initPreLoadedLabels(SessionData session) {
		// set up pre-loaded data cache
		String thesaurusDirectory = SkosPlayConfig.getInstance().getApplicationData().getThesaurusDirectory();
		if(thesaurusDirectory != null) {
			File dir = new File(thesaurusDirectory);
			if(dir.exists()) {
				try {
					URL[] urls = {dir.toURI().toURL()};
					ClassLoader loader = new URLClassLoader(urls);
					session.setPreLoadedDataLabels(ResourceBundle.getBundle(ApplicationData.DEFAULT_THESAURUS_LABELS_BUNDLE, session.getUserLocale(), loader));
					log.debug("Set pre-loaded data labels resource bundle to "+ApplicationData.DEFAULT_THESAURUS_LABELS_BUNDLE+" in dir "+dir.getAbsolutePath());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (MissingResourceException mre) {
					log.info("Pre-loaded data labels at "+ApplicationData.DEFAULT_THESAURUS_LABELS_BUNDLE+" in dir "+dir.getAbsolutePath()+" cannot be found");
				}
			}
		}
	}
}
