package fr.sparna.rdf.skos.printer.autocomplete;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONWriter {

	protected boolean indent = true;
	
	public String write(Items items) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		// mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		
		if(indent) {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		// don't write null values
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapper.writeValue(baos, items);
		return baos.toString("UTF-8");
	}

	public boolean isIndent() {
		return indent;
	}

	public void setIndent(boolean indent) {
		this.indent = indent;
	}
	

}
