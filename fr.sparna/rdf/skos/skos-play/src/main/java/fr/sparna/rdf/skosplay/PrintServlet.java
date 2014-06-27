package fr.sparna.rdf.skosplay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.reader.AbstractKosDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.AlphaIndexDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.BodyReader;
import fr.sparna.rdf.skos.printer.reader.ConceptBlockReader;
import fr.sparna.rdf.skos.printer.reader.ConceptListDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.HeaderAndFooterReader;
import fr.sparna.rdf.skos.printer.reader.HierarchicalDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.IndexGenerator;
import fr.sparna.rdf.skos.printer.reader.IndexGenerator.IndexType;
import fr.sparna.rdf.skos.printer.reader.TranslationTableDisplayGenerator;
import fr.sparna.rdf.skos.printer.reader.TranslationTableReverseDisplayGenerator;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
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
//		HIERARCHICAL_EXPANDED,
		CONCEPTLISTING,
		TRANSLATION_TABLE,
		PARTITION,
		TREELAYOUT,
		COMPLETE_MONOLINGUAL,
		COMPLETE_MULTILINGUAL,
		PERMUTED_INDEX,
		KWIC_INDEX
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
		String language = languageParam.equals("no-language")?"":languageParam;
		
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
			String aRandomConcept = Perform.on(r).read(new SparqlQuery(new SparqlQueryBuilder(this, "ReadRandomConcept.rq"))).stringValue();
			log.info("PRINT,"+SimpleDateFormat.getDateTimeInstance().format(new Date())+","+scheme+","+aRandomConcept+","+language+","+displayType+","+outputType);
		} catch (SparqlPerformException e1) {
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
				LabelReader labelReader = new LabelReader(r, language);
				JsonSKOSTreePrinter printer = new JsonSKOSTreePrinter(labelReader);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				printer.print(tree, baos);
				request.setAttribute("dataset", baos.toString("UTF-8").replaceAll("'", "\\\\'"));
				
			} catch (SparqlPerformException e) {
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
				LabelReader labelReader = new LabelReader(r, language);
				JsonSKOSTreePrinter printer = new JsonSKOSTreePrinter(labelReader);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				printer.print(tree, baos);
				request.setAttribute("dataset", baos.toString("UTF-8").replaceAll("'", "\\\\'"));
				
			} catch (SparqlPerformException e) {
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
		KosDocument document = new KosDocument();
		
		try {
			
			HeaderAndFooterReader headerReader = new HeaderAndFooterReader(r);
			headerReader.setApplicationString("Generated by SKOS Play!, sparna.fr");
			// on désactive complètement le header pour les PDF
			if(outputType != OUTPUT_TYPE.PDF) {
				// build and set header
				document.setHeader(headerReader.readHeader(language, scheme));
			}
			// all the time, set footer
			document.setFooter(headerReader.readFooter(language, scheme));
			
			// pass on Repository to skos-printer level
			BodyReader bodyReader;
			switch(displayType) {
			case ALPHABETICAL : {			
				ConceptBlockReader cbr = new ConceptBlockReader(r);
				bodyReader = new BodyReader(new AlphaIndexDisplayGenerator(r, cbr));			
				break;
			}
			case ALPHABETICAL_EXPANDED : {			
				ConceptBlockReader cbr = new ConceptBlockReader(r);
				cbr.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
				bodyReader = new BodyReader(new AlphaIndexDisplayGenerator(r, cbr));
				break;
			}
			case HIERARCHICAL : {
				bodyReader = new BodyReader(new HierarchicalDisplayGenerator(r, new ConceptBlockReader(r)));
				break;
			}
//			case HIERARCHICAL_EXPANDED : {
//				displayGenerator = new HierarchicalDisplayGenerator(r, new ConceptBlockReader(r, HierarchicalDisplayGenerator.EXPANDED_SKOS_PROPERTIES));
//				break;
//			}
			case CONCEPTLISTING : {
				ConceptBlockReader cbr = new ConceptBlockReader(r);
				cbr.setSkosPropertiesToRead(ConceptListDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
				List<String> additionalLanguages = new ArrayList<String>();
				for (String aLang : SessionData.get(request.getSession()).getPrintFormData().getLanguages().keySet()) {
					if(!aLang.equals(language)) {
						additionalLanguages.add(aLang);
					}
				}
				cbr.setAdditionalLabelLanguagesToInclude(additionalLanguages);
				
				bodyReader = new BodyReader(new ConceptListDisplayGenerator(r, cbr));
				break;
			}
			case TRANSLATION_TABLE : {
				bodyReader = new BodyReader(new TranslationTableDisplayGenerator(r, new ConceptBlockReader(r), targetLanguage));
				break;
			}
			case PERMUTED_INDEX : {
				bodyReader = new BodyReader(new IndexGenerator(r, IndexType.KWAC));
				break;
			}
			case KWIC_INDEX : {
				bodyReader = new BodyReader(new IndexGenerator(r, IndexType.KWIC));
				break;
			}
			case COMPLETE_MONOLINGUAL : {
				
				// prepare a list of generators
				List<AbstractKosDisplayGenerator> generators = new ArrayList<AbstractKosDisplayGenerator>();
					
				// alphabetical display
				ConceptBlockReader alphaCbReader = new ConceptBlockReader(r);
				alphaCbReader.setStyleAttributes(false);
				alphaCbReader.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
				alphaCbReader.setLinkDestinationIdPrefix("hier");
				AlphaIndexDisplayGenerator alphaGen = new AlphaIndexDisplayGenerator(
						r,
						alphaCbReader,
						"alpha"
				);
				generators.add(alphaGen);
				
				// hierarchical display
				ConceptBlockReader hierCbReader = new ConceptBlockReader(r);
				hierCbReader.setLinkDestinationIdPrefix("alpha");
				HierarchicalDisplayGenerator hierarchyGen = new HierarchicalDisplayGenerator(
						r,
						hierCbReader,
						"hier"
				);
				generators.add(hierarchyGen);
				
				bodyReader = new BodyReader(generators);				
				
				break;
			}
			case COMPLETE_MULTILINGUAL : {
				
				// prepare a list of generators
				List<AbstractKosDisplayGenerator> generators = new ArrayList<AbstractKosDisplayGenerator>();
				
				// read all potential languages and exclude the main one
				final List<String> additionalLanguages = new ArrayList<String>();
				for (String aLang : SessionData.get(request.getSession()).getPrintFormData().getLanguages().keySet()) {
					if(!aLang.equals(language)) {
						additionalLanguages.add(aLang);
					}
				}
					
				// alphabetical display
				ConceptBlockReader alphaCbReader = new ConceptBlockReader(r);
				alphaCbReader.setStyleAttributes(false);
				alphaCbReader.setSkosPropertiesToRead(AlphaIndexDisplayGenerator.EXPANDED_SKOS_PROPERTIES_WITH_TOP_TERMS);
				alphaCbReader.setAdditionalLabelLanguagesToInclude(additionalLanguages);
				alphaCbReader.setLinkDestinationIdPrefix("hier");
				AlphaIndexDisplayGenerator alphaGen = new AlphaIndexDisplayGenerator(
						r,
						alphaCbReader,
						"alpha"
				);
				generators.add(alphaGen);
				
				// hierarchical display
				ConceptBlockReader hierCbReader = new ConceptBlockReader(r);
				hierCbReader.setLinkDestinationIdPrefix("alpha");
				HierarchicalDisplayGenerator hierarchyGen = new HierarchicalDisplayGenerator(
						r,
						hierCbReader,
						"hier"
				);
				generators.add(hierarchyGen);
				
				// add translation tables for each additional languages
				for (int i=0;i<additionalLanguages.size(); i++) {
					String anAdditionalLang = additionalLanguages.get(i);
					ConceptBlockReader aCbReader = new ConceptBlockReader(r);
					aCbReader.setLinkDestinationIdPrefix("alpha");
					TranslationTableReverseDisplayGenerator ttGen = new TranslationTableReverseDisplayGenerator(
							r,
							aCbReader,
							anAdditionalLang,
							"trans"+i);
					generators.add(ttGen);
				}
				
				bodyReader = new BodyReader(generators);
				
				break;
			}
			default :
				throw new InvalidParameterException("Unknown display type "+displayType);
			}	
			
			// read the body
			document.setBody(bodyReader.readBody(language, scheme));

			DisplayPrinter printer = new DisplayPrinter();
			// TODO : use Spring for configuration for easier debugging config
			// for the moment we desactivate debugging completely
			printer.setDebug(false);
			
			switch(outputType) {
			case HTML : {
				printer.printToHtml(document, response.getOutputStream(), SessionData.get(request.getSession()).getUserLocale().getLanguage());
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
				printer.printToPdf(document, response.getOutputStream(), SessionData.get(request.getSession()).getUserLocale().getLanguage());
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
