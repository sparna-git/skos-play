package fr.sparna.rdf.skosplay;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.reader.AlphabeticalSkosReader;
import fr.sparna.rdf.skos.printer.reader.ConceptListSkosReader;
import fr.sparna.rdf.skos.printer.reader.DisplayHeaderSkosReader;
import fr.sparna.rdf.skos.printer.reader.HierarchicalSkosReader;
import fr.sparna.rdf.skos.printer.schema.Display;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;

public class PrintServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String PARAM_OUTPUT = "output";
	
	private static final String PARAM_DISPLAY = "display";
	
	private static final String PARAM_LANGUAGE = "language";
	
	private static final String PARAM_SCHEME = "scheme";
	
	private enum OUTPUT_TYPE {
		HTML,
		PDF
	}
	
	private enum DISPLAY_TYPE {
		ALPHABETICAL,
		ALPHABETICAL_EXPANDED,
		HIERARCHICAL,
		HIERARCHICAL_EXPANDED,
		CONCEPTLISTING,
		PARTITION,
		TREELAYOUT,
	}
	
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response
	) throws ServletException, IOException {
		
		// get output type param
		String outputParam = (request.getParameter(PARAM_OUTPUT) != null && !request.getParameter(PARAM_OUTPUT).equals(""))?request.getParameter(PARAM_OUTPUT):null;
		log.debug(PARAM_OUTPUT+" : "+outputParam);
		OUTPUT_TYPE outputType = (outputParam != null)?OUTPUT_TYPE.valueOf(outputParam.toUpperCase()):null;

		// get display type param
		String displayParam = (request.getParameter(PARAM_DISPLAY) != null && !request.getParameter(PARAM_DISPLAY).equals(""))?request.getParameter(PARAM_DISPLAY):null;
		log.debug(PARAM_DISPLAY+" : "+displayParam);
		DISPLAY_TYPE displayType = (displayParam != null)?DISPLAY_TYPE.valueOf(displayParam.toUpperCase()):null;
		if(displayType == null) {
			throw new ServletException("Missing required parameter : "+PARAM_DISPLAY);
		}
		
		// get language param
		String languageParam = (request.getParameter(PARAM_LANGUAGE) != null && !request.getParameter(PARAM_LANGUAGE).equals(""))?request.getParameter(PARAM_LANGUAGE):null;
		String language = languageParam.equals("no-language")?null:languageParam;
		
		// get scheme param
		String paramScheme = (request.getParameter(PARAM_SCHEME) != null && !request.getParameter(PARAM_SCHEME).equals(""))?request.getParameter(PARAM_SCHEME):null;
		URI scheme = (paramScheme == null || paramScheme.equals("no-scheme"))?null:URI.create(paramScheme);
		
		// retrieve data from session
		Repository r = SessionData.get(request.getSession()).getRepository();
		
		// make a log to trace usage
		try {
			String aRandomConcept = Perform.on(r).read(new SPARQLQuery(new SPARQLQueryBuilder(this, "ReadRandomConcept.rq"))).stringValue();
			log.info("PRINT,"+SimpleDateFormat.getDateTimeInstance().format(new Date())+","+scheme+","+aRandomConcept+","+language+","+displayType+","+outputType);
		} catch (SPARQLPerformException e1) {
			throw new ServletException(e1);
		}
		
		switch(displayType) {
		case PARTITION : {
			// set attributes
			request.setAttribute("language", language);
			request.setAttribute("root", (scheme != null)?scheme.toString():"");
			// forward to the JSP
			getServletContext().getRequestDispatcher("/viz-partition.jsp").forward(request, response);
			
			return;
		}
		case TREELAYOUT : {
			// set attributes
			request.setAttribute("language", language);
			request.setAttribute("root", (scheme != null)?scheme.toString():"");
			// forward to the JSP
			getServletContext().getRequestDispatcher("/viz-treelayout.jsp").forward(request, response);
			return;
		}
		default : {
			break;
		}
		}
		
		// build display result
		Display display = new Display();
		
		try {
			
			// build and set header
			DisplayHeaderSkosReader headerReader = new DisplayHeaderSkosReader(r);
			DisplayHeader header = headerReader.read(language, scheme);
			display.setHeader(header);
			
			// pass on Repository to skos-printer level
			switch(displayType) {
			case ALPHABETICAL : {			
				AlphabeticalSkosReader reader = new AlphabeticalSkosReader(r);
				display.getAlphabeticalOrHierarchical().add(reader.read(language, scheme));
				break;
			}
			case ALPHABETICAL_EXPANDED : {			
				AlphabeticalSkosReader reader = new AlphabeticalSkosReader(r);
				reader.setSkosPropertiesToRead(AlphabeticalSkosReader.EXPANDED_SKOS_PROPERTIES);
				display.getAlphabeticalOrHierarchical().add(reader.read(language, scheme));
				break;
			}
			case HIERARCHICAL : {
				HierarchicalSkosReader reader = new HierarchicalSkosReader(r);
				display.getAlphabeticalOrHierarchical().addAll(reader.read(language, scheme));
				break;
			}
			case HIERARCHICAL_EXPANDED : {
				HierarchicalSkosReader reader = new HierarchicalSkosReader(r);
				reader.setSkosPropertiesToRead(HierarchicalSkosReader.EXPANDED_SKOS_PROPERTIES);
				display.getAlphabeticalOrHierarchical().addAll(reader.read(language, scheme));
				break;
			}
			case CONCEPTLISTING : {
				ConceptListSkosReader reader = new ConceptListSkosReader(r);
				display.getAlphabeticalOrHierarchical().add(reader.read(language, scheme));
				break;
			}
			default :
				break;
			}		

			DisplayPrinter printer = new DisplayPrinter();
			switch(outputType) {
			case HTML : {
				printer.printToHtml(display, response.getOutputStream());
				break;
			}
			case PDF : {
				response.setContentType("application/pdf");
				// if alphabetical or concept listing display, set 2-columns layout
				if(
						displayType == DISPLAY_TYPE.ALPHABETICAL
						||
						displayType == DISPLAY_TYPE.CONCEPTLISTING
						||
						displayType == DISPLAY_TYPE.ALPHABETICAL_EXPANDED
				) {
					printer.getTransformerParams().put("column-count", 2);
				}
				printer.printToPdf(display, response.getOutputStream());
				break;
			}
			}

			response.flushBuffer();
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response
	) throws ServletException, IOException {
		this.doGet(request, response);
	}

	
	
}
