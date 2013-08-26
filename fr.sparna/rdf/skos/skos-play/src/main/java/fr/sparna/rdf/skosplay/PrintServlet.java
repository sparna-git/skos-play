package fr.sparna.rdf.skosplay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQuery;
import fr.sparna.rdf.sesame.toolkit.query.builder.SPARQLQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.reader.AbstractBodyReader;
import fr.sparna.rdf.skos.printer.reader.AlphabeticalIndexBodyReader;
import fr.sparna.rdf.skos.printer.reader.ConceptBlockReader;
import fr.sparna.rdf.skos.printer.reader.ConceptListBodyReader;
import fr.sparna.rdf.skos.printer.reader.HeaderReader;
import fr.sparna.rdf.skos.printer.reader.HierarchicalBodyReader;
import fr.sparna.rdf.skos.printer.reader.TranslationTableBodyReader;
import fr.sparna.rdf.skos.printer.schema.Display;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;
import fr.sparna.rdf.skos.toolkit.JsonSKOSTreePrinter;
import fr.sparna.rdf.skos.toolkit.SKOSTreeBuilder;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode;

public class PrintServlet extends HttpServlet {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String PARAM_OUTPUT = "output";
	
	private static final String PARAM_DISPLAY = "display";
	
	private static final String PARAM_LANGUAGE = "language";
	
	private static final String PARAM_TARGET_LANGUAGE = "targetLanguage";
	
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
		TRANSLATION_TABLE,
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
		
		// get target language param - only for translations
		String paramTargetLanguage = (request.getParameter(PARAM_TARGET_LANGUAGE) != null && !request.getParameter(PARAM_TARGET_LANGUAGE).equals(""))?request.getParameter(PARAM_TARGET_LANGUAGE):null;
		String targetLanguage = (paramTargetLanguage != null)?(paramTargetLanguage.equals("no-language")?null:paramTargetLanguage):null;
		
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
			// request.setAttribute("language", language);
			// request.setAttribute("root", (scheme != null)?scheme.toString():"");
			
			SKOSTreeBuilder builder = new SKOSTreeBuilder(r, language);
			try {
				GenericTree<SKOSTreeNode> tree = JsonServlet.buildTree(builder, (scheme != null)?URI.create(scheme.toString()):null);			
				
				// writes json output
				LabelReader labelReader = new LabelReader(r, "en", language);
				JsonSKOSTreePrinter printer = new JsonSKOSTreePrinter(labelReader);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				printer.print(tree, baos);
				request.setAttribute("dataset", baos.toString().replaceAll("'", "\\\\'"));
				
			} catch (SPARQLPerformException e) {
				throw new ServletException(e);
			}
			
			
			// forward to the JSP
			getServletContext().getRequestDispatcher("/viz-partition.jsp").forward(request, response);
			
			return;
		}
		case TREELAYOUT : {
			// set attributes
			// request.setAttribute("language", language);
			// request.setAttribute("root", (scheme != null)?scheme.toString():"");
			
			SKOSTreeBuilder builder = new SKOSTreeBuilder(r, language);
			try {
				GenericTree<SKOSTreeNode> tree = JsonServlet.buildTree(builder, (scheme != null)?URI.create(scheme.toString()):null);			
				
				// writes json output
				LabelReader labelReader = new LabelReader(r, "en", language);
				JsonSKOSTreePrinter printer = new JsonSKOSTreePrinter(labelReader);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				printer.print(tree, baos);
				request.setAttribute("dataset", baos.toString().replaceAll("'", "\\\\'"));
				
			} catch (SPARQLPerformException e) {
				throw new ServletException(e);
			}
			
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
			HeaderReader headerReader = new HeaderReader(r);
			DisplayHeader header = headerReader.read(language, scheme);
			display.setHeader(header);
			
			// pass on Repository to skos-printer level
			AbstractBodyReader bodyReader = null;
			switch(displayType) {
			case ALPHABETICAL : {			
				bodyReader = new AlphabeticalIndexBodyReader(r, new ConceptBlockReader(r));			
				break;
			}
			case ALPHABETICAL_EXPANDED : {			
				bodyReader = new AlphabeticalIndexBodyReader(r, new ConceptBlockReader(r, AlphabeticalIndexBodyReader.EXPANDED_SKOS_PROPERTIES));
				break;
			}
			case HIERARCHICAL : {
				bodyReader = new HierarchicalBodyReader(r, new ConceptBlockReader(r));
				break;
			}
			case HIERARCHICAL_EXPANDED : {
				bodyReader = new HierarchicalBodyReader(r, new ConceptBlockReader(r, HierarchicalBodyReader.EXPANDED_SKOS_PROPERTIES));
				break;
			}
			case CONCEPTLISTING : {
				bodyReader = new ConceptListBodyReader(r, new ConceptBlockReader(r, ConceptListBodyReader.EXPANDED_SKOS_PROPERTIES));
				break;
			}
			case TRANSLATION_TABLE : {
				bodyReader = new TranslationTableBodyReader(r, new ConceptBlockReader(r), targetLanguage);
				break;
			}
			default :
				throw new InvalidParameterException("Unknown display type "+displayType);
			}	
			
			// read the body
			display.setBody(bodyReader.readBody(language, scheme));

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
