package fr.sparna.rdf.skosplay;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SessionFilter implements Filter {

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
			session = new SessionData();
			session.store(request.getSession());
			
			// set up Locale
			session.setUserLocale(request.getLocale());
		}
		
		if(request.getParameter("lang") != null) {
			session.setUserLocale(new Locale(request.getParameter("lang")));
		}
		
		return request;
	}
}
