package fr.sparna.dbpedia.lookup.client.gui;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.dbpedia.lookup.client.DBpediaLookupClient;
import fr.sparna.dbpedia.lookup.client.DBpediaLookupResultPrinter;
import fr.sparna.dbpedia.lookup.client.schema.ArrayOfResult;
import fr.sparna.dbpedia.lookup.client.schema.Result;

/**
 * The main entry point.
 * @AppController indicates this class will be the application controller, the main entry point.
 * 
 * @author Thomas Francart
 *
 */
@Controller
public class AppController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@RequestMapping("/about.do")
	public ModelAndView home() {
		return new ModelAndView("about");
	}
	
	/**
	 * Prepare and return the search form
	 * @return
	 */
	@RequestMapping("/search-form.do")
	public ModelAndView search() {
		return new ModelAndView("search-form");
	}

	/**
	 * Prepare and return the search form
	 * @return
	 */
	@RequestMapping(
			value = "/instantSearch.do",
			params={"key"}
	)
	public ResponseEntity<String> instantSearch(
			@RequestParam(value="key", required=true)  String key,
			@RequestParam(value="callback",required=false) String callback,
			HttpServletRequest request
	) throws Exception {
		DBpediaLookupClient client = new DBpediaLookupClient();
		ArrayOfResult array = client.prefixSearch(key);
		
		fr.sparna.dbpedia.lookup.client.gui.json.Results results = toJsonResults(array);
		
		// return SpringJsonResponse.serializeJSONResponse("{ \"results\":[ {\"title\":\""+key+"\"},{\"title\":\"second\"} ] }", callback);
		// return SpringJsonResponse.serializeJSONResponse(JSONSerialize.serialize(results, true), callback);
		return SpringJsonResponse.serializeJSONResponse(JSONSerialize.serialize(array, true), callback);
	}
	
	private static fr.sparna.dbpedia.lookup.client.gui.json.Results toJsonResults(ArrayOfResult arrayOfResults) {
		fr.sparna.dbpedia.lookup.client.gui.json.Results results = new fr.sparna.dbpedia.lookup.client.gui.json.Results();
		
		for (Result aResult : arrayOfResults.getResult()) {
			fr.sparna.dbpedia.lookup.client.gui.json.Result r = new fr.sparna.dbpedia.lookup.client.gui.json.Result();
			r.setUri(aResult.getURI());
			r.setLabel(aResult.getLabel());
			results.getResults().add(r);
		}
		
		return results;
	}
}
