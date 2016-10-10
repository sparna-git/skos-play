package fr.sparna.rdf.datapress.rdfa;

import java.io.ByteArrayInputStream;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.rdf.rdfa.RdfaParser;
import org.semarglproject.source.StreamProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.datapress.DataPress;
import fr.sparna.rdf.datapress.DataPressException;
import fr.sparna.rdf.datapress.DataPressSource;
import nu.validator.htmlparser.common.XmlViolationPolicy;

public class RdfaPress implements DataPress {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public RdfaPress() {
	}
	
	public void press(DataPressSource in, RDFHandler out) throws DataPressException {
		log.debug(this.getClass().getSimpleName()+" - Pressing {}", in.getIri());
		long start = System.currentTimeMillis();

		try {
			// connect the sink to our handler
			StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(new SesameSink(out)));
			// StreamProcessor streamProcessor = new StreamProcessor(RdfaParser.connect(new DebugSink()));
	        streamProcessor.setProperty(RdfaParser.ENABLE_VOCAB_EXPANSION, true);

	        nu.validator.htmlparser.sax.HtmlParser reader = new nu.validator.htmlparser.sax.HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
	        streamProcessor.setProperty(StreamProcessor.XML_READER_PROPERTY, reader);
	        
			streamProcessor.process(new ByteArrayInputStream(in.getContent()), in.getIri().stringValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		log.debug(this.getClass().getSimpleName()+" - Done pressing {} in {}ms", in.getIri(), System.currentTimeMillis()-start);
	}

}
