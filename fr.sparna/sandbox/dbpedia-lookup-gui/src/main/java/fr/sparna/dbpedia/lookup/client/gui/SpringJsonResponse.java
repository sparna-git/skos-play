package fr.sparna.dbpedia.lookup.client.gui;

import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class SpringJsonResponse {

	public static ResponseEntity<String> serializeJSONResponse(String json, String callback) 
	throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON, new HashMap<String, String>() {{ put("charset", "UTF-8"); }});
		responseHeaders.setContentType(jsonMediaType);

		return new ResponseEntity<String>(
				(callback != null)?getJsonP(callback, json):json,
						responseHeaders,
						HttpStatus.CREATED
				);
	}

	public static String getJsonP(String callback, String s){
		return callback + "(" + s + ");";
	}
	
}
