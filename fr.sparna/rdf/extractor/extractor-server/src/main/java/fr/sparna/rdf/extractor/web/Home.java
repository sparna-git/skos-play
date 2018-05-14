package fr.sparna.rdf.extractor.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Home {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ServletContext servletContext;
	
	@RequestMapping("/acceuil")
	public ModelAndView home(HttpServletRequest request) {	
		return new ModelAndView("home");	
	}
	
	@RequestMapping("/presser")
	public ModelAndView presser(HttpServletRequest request) {	
		return new ModelAndView("input");	
	}
	
	@RequestMapping("/sparql")
	public ModelAndView sparql(
			HttpServletRequest request,
			HttpServletResponse response
	) {
		SparqlPageData page = new SparqlPageData();
		page.setEndpoint(Config.getInstance().getRepository());
		
		return new ModelAndView("sparql", SparqlPageData.KEY, page);	
	}
	
	@RequestMapping("/dashboard")
	public ModelAndView dashboard(
			HttpServletRequest request,
			HttpServletResponse response
	) {
		DashboardData page = new DashboardData();
		page.setEndpoint(Config.getInstance().getRepository());
		
		return new ModelAndView("dashboard", DashboardData.KEY, page);	
	}

}
