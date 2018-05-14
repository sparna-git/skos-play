package fr.sparna.rdf.extractor.content;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import fr.sparna.rdf.extractor.DataExtractionException;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.HtmlExtractor;

public class ContentExtractor extends HtmlExtractor implements DataExtractor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private IRI contentProperty;
	
	public ContentExtractor() {
		this(SimpleValueFactory.getInstance().createIRI("http://rdfs.org/sioc/ns#content"));
	}

	public ContentExtractor(IRI contentProperty) {
		super();
		this.contentProperty = contentProperty;
	}

	@Override
	public void extract(DataExtractionSource in, RDFHandler out) throws DataExtractionException {
		log.debug(this.getClass().getSimpleName()+" - Extracting from {}", in.getIri());
		try {
			// TODO : handle encoding
			String text = ArticleExtractor.INSTANCE.getText(new String(in.getContent()));
			
			log.debug(this.getClass().getSimpleName()+" - Extracted text "+((text.length() > 30)?text.substring(0, 30):text));
			
			// output statement
			SimpleValueFactory svf = SimpleValueFactory.getInstance();
			out.handleStatement(svf.createStatement(
					in.getDocumentIri(),
					this.contentProperty,
					svf.createLiteral(text)
			));
		} catch (RDFHandlerException e) {
			throw new DataExtractionException(e);
		} catch (BoilerpipeProcessingException e) {
			throw new DataExtractionException(e);
		}
	}

	public IRI getContentProperty() {
		return contentProperty;
	}

	public void setContentProperty(IRI contentProperty) {
		this.contentProperty = contentProperty;
	}

}
