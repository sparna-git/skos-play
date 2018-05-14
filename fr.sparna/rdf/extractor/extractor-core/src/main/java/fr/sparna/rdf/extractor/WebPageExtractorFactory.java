package fr.sparna.rdf.extractor;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.extractor.content.ContentExtractor;
import fr.sparna.rdf.extractor.jsonld.JsonLDExtractor;
import fr.sparna.rdf.extractor.microdata.MicrodataExtractor;
import fr.sparna.rdf.extractor.rdfa.RdfaExtractor;

/**
 * A base factory for creating a DataExtractor that extracts JsonLD, RDFa, Microdata and content.
 * @author Thomas Francart
 *
 */
public class WebPageExtractorFactory {

	protected boolean extractJsonLd = true;
	protected boolean extractRdfa = true;
	protected boolean extractMicrodata = true;
	protected boolean extractContent = true;
	protected boolean attemptXhtmlParsing = false;

	
	public CompositeExtractor buildExtractor() {
		List<DataExtractor> extractors = new ArrayList<DataExtractor>();
		if(this.extractJsonLd) {
			extractors.add(new JsonLDExtractor());
		}
		if(this.extractRdfa) {
			RdfaExtractor rdfae = new RdfaExtractor();
			rdfae.setAttemptXhtmlParsing(attemptXhtmlParsing);
			extractors.add(rdfae);
		}
		if(this.extractMicrodata) {
			extractors.add(new MicrodataExtractor());
		}
		if(this.extractContent) {
			extractors.add(new ContentExtractor());
		}
		
		return new CompositeExtractor(extractors);		
	}
	
	
	public boolean isExtractJsonLd() {
		return extractJsonLd;
	}
	public void setExtractJsonLd(boolean extractJsonLd) {
		this.extractJsonLd = extractJsonLd;
	}
	public boolean isExtractRdfa() {
		return extractRdfa;
	}
	public void setExtractRdfa(boolean extractRdfa) {
		this.extractRdfa = extractRdfa;
	}
	public boolean isExtractMicrodata() {
		return extractMicrodata;
	}
	public void setExtractMicrodata(boolean extractMicrodata) {
		this.extractMicrodata = extractMicrodata;
	}
	public boolean isExtractContent() {
		return extractContent;
	}
	public void setExtractContent(boolean extractContent) {
		this.extractContent = extractContent;
	}
	public boolean isAttemptXhtmlParsing() {
		return attemptXhtmlParsing;
	}
	public void setAttemptXhtmlParsing(boolean attemptXhtmlParsing) {
		this.attemptXhtmlParsing = attemptXhtmlParsing;
	}
	
}
