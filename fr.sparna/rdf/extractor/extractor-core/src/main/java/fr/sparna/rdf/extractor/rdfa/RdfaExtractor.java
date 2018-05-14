package fr.sparna.rdf.extractor.rdfa;

import java.io.ByteArrayInputStream;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.source.StreamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.extractor.DataExtractionException;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.HtmlExtractor;
import nu.validator.htmlparser.common.XmlViolationPolicy;

public class RdfaExtractor extends HtmlExtractor implements DataExtractor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private boolean attemptXhtmlParsing = false;
	
	public RdfaExtractor() {
	}
	
	public void extract(DataExtractionSource in, RDFHandler out) throws DataExtractionException {
		log.debug(this.getClass().getSimpleName()+" - Extracting from {}", in.getIri());
		long start = System.currentTimeMillis();

		try {			
			// connect the sink to our handler
			StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(new SesameSink(out)));
			// StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(new DebugSink()));
	        // streamProcessor.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION, true);

			if(attemptXhtmlParsing) {
				try {
					streamProcessor.process(new ByteArrayInputStream(in.getContent()), in.getIri().stringValue());
				} catch (Exception e) {
					nu.validator.htmlparser.sax.HtmlParser reader = new nu.validator.htmlparser.sax.HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
			        streamProcessor.setProperty(StreamProcessor.XML_READER_PROPERTY, reader);
			        streamProcessor.process(new ByteArrayInputStream(in.getContent()), in.getIri().stringValue());
				}
			} else {
				nu.validator.htmlparser.sax.HtmlParser reader = new nu.validator.htmlparser.sax.HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
		        streamProcessor.setProperty(StreamProcessor.XML_READER_PROPERTY, reader);		        
				streamProcessor.process(new ByteArrayInputStream(in.getContent()), in.getIri().stringValue());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		log.debug(this.getClass().getSimpleName()+" - Done extracting from {} in {}ms", in.getIri(), System.currentTimeMillis()-start);
	}

	public boolean isAttemptXhtmlParsing() {
		return attemptXhtmlParsing;
	}

	public void setAttemptXhtmlParsing(boolean attemptXhtmlParsing) {
		this.attemptXhtmlParsing = attemptXhtmlParsing;
	}

}
