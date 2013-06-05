package fr.sparna.rdf.skosplay;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.i18n.StrictResourceBundleControl;

public class AboutServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {		
		// retrieve resource bundle for error messages
		ResourceBundle b = ResourceBundle.getBundle(
				"fr.sparna.rdf.skosplay.i18n.Bundle",
				SessionData.get(request.getSession()).getUserLocale(),
				new StrictResourceBundleControl()
		);

		getServletContext().getRequestDispatcher(b.getString("about.jsp")).forward(request, response);
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		this.doGet(request, response);
	}



}
