package fr.sparna.rdf.datapress.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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

}
