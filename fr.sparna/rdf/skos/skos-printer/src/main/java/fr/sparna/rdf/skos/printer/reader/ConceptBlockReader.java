package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class ConceptBlockReader {

	protected ResourceBundle tagsBundle;
	
	protected Repository repository;
	
	protected List<String> skosPropertiesToRead;
	
	// additional readers
	protected List<PropertyReader> additionalReaders = new ArrayList<PropertyReader>();

	// prefLabelReader
	protected PropertyReader prefLabelReader;
	
	public ConceptBlockReader(
			Repository repository,
			List<String> skosPropertiesToRead
	) {
		super();
		this.repository = repository;
		this.skosPropertiesToRead = skosPropertiesToRead;
	}
	
	public ConceptBlockReader(
			Repository repository
	) {
		this(repository, null);
	}
	
	// called by BodyReader
	protected void initInternal(ResourceBundle tagsBundle, String lang, final URI conceptScheme) {
		this.setTagsBundle(tagsBundle);
		
		// no concept scheme filtering - we want to be able to read prefLabel independently from the conceptScheme
		// no concept scheme filtering here - we want to be able to read prefLabel independently from the conceptScheme
		prefLabelReader = new PropertyReader(
				this.repository,
				URI.create(SKOS.PREF_LABEL),
				// additional path
				// TODO : could read rdfs:label ?
				null,
				// language of property to read
				lang,
				// additional criteria predicate
				null,
				// additional criteria object
				null
		);
		// if we need to disable preload
		prefLabelReader.setPreLoad(false);

		// setup additional readers
		this.additionalReaders = new ArrayList<PropertyReader>();
		
		if(this.skosPropertiesToRead != null) {
			for (String aProperty : this.skosPropertiesToRead) {
				String inverseProperty = SKOS.getInverseOf(aProperty);
				additionalReaders.add(
						(conceptScheme != null)
						?new PropertyReader(
								this.repository,
								URI.create(aProperty),
								(inverseProperty != null)?"^<"+inverseProperty+">":null,
								(SKOS.isDatatypeProperty(aProperty))?lang:null,
								URI.create(SKOS.IN_SCHEME),
								URI.create(conceptScheme.toString())
						)
						:new PropertyReader(
								this.repository,
								URI.create(aProperty),
								(inverseProperty != null)?"^<"+inverseProperty+">":null,
								(SKOS.isDatatypeProperty(aProperty))?lang:null,
								null,
								null
						)
				);
			}
		}
	}
	
	public ConceptBlock readConceptBlock(final String uri, boolean setLabelAsPref)
	throws SPARQLPerformException {
		// set label (or URI if no label can be found)
		String label = valueListToString(prefLabelReader.read(URI.create(uri)));
		label = (label.trim().equals(""))?uri:label;
		return this.readConceptBlock(uri, label, setLabelAsPref);
	}
	
	/**
	 * HierarchicalBodyReader does not want to have all labels bolded, so will set the setLabelAsPref to false
	 * 
	 * @param uri
	 * @param prefLabel
	 * @param setLabelAsPref
	 * @return
	 * @throws SPARQLPerformException
	 */
	public ConceptBlock readConceptBlock(final String uri, String prefLabel, boolean setLabelAsPref)
	throws SPARQLPerformException {
		String conceptBlockId = Integer.toString((uri+prefLabel).hashCode());
		ConceptBlock cb = SchemaFactory.createConceptBlock(conceptBlockId, uri, prefLabel, setLabelAsPref?"pref":null);

		for (PropertyReader predicateReader : additionalReaders) {
			List<Value> values = predicateReader.read(URI.create(uri));
			for (Value value : values) {

				if(value instanceof Literal) {
					cb.getAttOrRef().add(
							SchemaFactory.createAtt(
									((Literal)value).stringValue(),
									this.tagsBundle.getString(predicateReader.getPropertyURI().toString().substring(SKOS.NAMESPACE.length())),
									(predicateReader.getPropertyURI().toString().equals(SKOS.ALT_LABEL))?"alt":null
									)
							);
				} else {
					org.openrdf.model.URI aRef = (org.openrdf.model.URI)value;
					List<Value> prefs = prefLabelReader.read(URI.create(aRef.stringValue()));
					String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue();
					String entryRef = Integer.toString((aRef.stringValue()+refPrefLabel).hashCode());
					cb.getAttOrRef().add(
							SchemaFactory.createRef(
									entryRef,
									aRef.stringValue(),
									refPrefLabel,
									this.tagsBundle.getString(predicateReader.getPropertyURI().toString().substring(SKOS.NAMESPACE.length())),
									"pref"
									)
							);
				}
			}
		}
		
		return cb;
	}
	
	private String valueListToString(List<Value> values) {
		StringBuffer sb = new StringBuffer();
		if(values != null && values.size() > 0) {
			for (Value aValue : values) {
				sb.append(((Literal)aValue).getLabel()+", ");
			}
			// remove last ", "
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	public ResourceBundle getTagsBundle() {
		return tagsBundle;
	}

	public void setTagsBundle(ResourceBundle tagsBundle) {
		this.tagsBundle = tagsBundle;
	}

	public List<String> getSkosPropertiesToRead() {
		return skosPropertiesToRead;
	}

	public void setSkosPropertiesToRead(List<String> skosPropertiesToRead) {
		this.skosPropertiesToRead = skosPropertiesToRead;
	}

	public PropertyReader getPrefLabelReader() {
		return prefLabelReader;
	}
	
}
