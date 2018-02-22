package fr.sparna.dbpedia.lookup.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONSerialize {

	public static String serialize(Object o, boolean indent) throws IOException {
		// serialize template in JSON
		ObjectMapper mapper = new ObjectMapper();
		// mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		if(indent) {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		mapper.setSerializationInclusion(Include.NON_NULL);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		mapper.writeValue(baos, o);
		return baos.toString("UTF-8");
	}
	
}
