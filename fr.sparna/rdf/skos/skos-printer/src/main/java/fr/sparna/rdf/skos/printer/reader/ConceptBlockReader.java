package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.sesame.toolkit.reader.UriLang;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.builders.GetLabels;
import fr.sparna.rdf.skos.toolkit.builders.GetTopConceptsOfConcept;

public class ConceptBlockReader {

	protected Repository repository;
	
	// list of URIs of SKOS properties to read for each concept block
	protected List<String> skosPropertiesToRead;
	// additional readers corresponding to skosPropertiesToRead
	// TODO : need to make ProeprtyReader and KeyValueReader implement the same interface
	protected List<Object> additionalReaders = new ArrayList<Object>();

	// prefLabelReader
	protected PropertyReader prefLabelReader;
	
	// should we include linguistic equivalents ?
	protected List<String> additionalLabelLanguagesToInclude = null;
	// use a TreeMap to garantee ordering by language code
	protected Map<String, KeyValueReader<UriLang, Literal>> additionalLabelLanguagesReaders = new TreeMap<String, KeyValueReader<UriLang, Literal>>();
	
	// concept ID prefixes, to distinguish multiple concept block of the same concept in a document containing multiple displays
	protected String conceptBlockIdPrefix;
	// link prefixes, to make links point to a concept block in another display
	protected String linkDestinationIdPrefix;
	protected boolean styleAttributes = true;
	
	// already generated IDs, to avoid clashes
	protected List<String> generatedIds = new ArrayList<String>();
	
	public ConceptBlockReader(
			Repository repository
	) {
		super();
		this.repository = repository;
	}	
	
	// called by a DisplayGenerator
	protected void initInternal(
			String lang,
			final URI conceptScheme,
			String conceptBlockIdPrefix
	) {
		this.conceptBlockIdPrefix = conceptBlockIdPrefix;
		
		// no concept scheme filtering here - we want to be able to read prefLabel independently from the conceptScheme
		prefLabelReader = new PropertyReader(
				this.repository,
				URI.create(SKOS.PREF_LABEL),
				// additional path
				// TODO : could read rdfs:sourceConceptLabel ?
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
		this.additionalReaders = new ArrayList<Object>();
		
		// for each SKOS property to read...
		if(this.skosPropertiesToRead != null) {
			for (String aProperty : this.skosPropertiesToRead) {
				
				if(aProperty.equals(SKOSPLAY.TOP_TERM)) {

					additionalReaders.add(
						new KeyValueReader<org.openrdf.model.URI, org.openrdf.model.URI>(
								repository,
								new GetTopConceptsOfConcept(null)
					));
					
				} else {
					
					// add a PropertyReader to read the corresponding property
					// or its inverse property
					String inverseProperty = SKOS.getInverseOf(aProperty);
					additionalReaders.add(
							(conceptScheme != null)
							?new PropertyReader(
									this.repository,
									URI.create(aProperty),
									(inverseProperty != null)?"^<"+inverseProperty+">":null,
									(SKOS.isDatatypeProperty(aProperty) && !aProperty.equals(SKOS.NOTATION))?lang:null,
									URI.create(SKOS.IN_SCHEME),
									URI.create(conceptScheme.toString())
							)
							:new PropertyReader(
									this.repository,
									URI.create(aProperty),
									(inverseProperty != null)?"^<"+inverseProperty+">":null,
									(SKOS.isDatatypeProperty(aProperty) && !aProperty.equals(SKOS.NOTATION))?lang:null,
									null,
									null
							)
					);
					
				}
				

			}
		}
		
		// init additional languages reader
		if(this.additionalLabelLanguagesToInclude != null) {
			for (String anAdditionalLang : this.additionalLabelLanguagesToInclude) {
				additionalLabelLanguagesReaders.put(
						anAdditionalLang, 
						new KeyValueReader<UriLang, Literal>(
								repository,
								new GetLabels(SKOS.PREF_LABEL, anAdditionalLang, (conceptScheme != null)?conceptScheme.toString():null)
						)
				);
			}
		}
	}
	
	public ConceptBlock readConceptBlockForSynonym(final String uri, final String altLabel, final String prefLabel)
	throws SparqlPerformException {
		ConceptBlock cb = SchemaFactory.createConceptBlock(computeConceptBlockId(uri, altLabel), uri, SchemaFactory.createLabel(altLabel, "alt"));
		cb.getAtt().add(SchemaFactory.createAttLink(
				computeRefId(uri, prefLabel, true),
				uri,
				prefLabel,
				SKOSTags.getStringForURI(SKOS.PREF_LABEL),
				"pref")
		);
		return cb;
	}
	
	
	public ConceptBlock readConceptBlock(final String uri, boolean styleLabel)
	throws SparqlPerformException {
		// set sourceConceptLabel (or URI if no sourceConceptLabel can be found)
		String label = LabelReader.display(prefLabelReader.read(URI.create(uri)));
		label = (label.trim().equals(""))?uri:label;
		return this.readConceptBlock(uri, label, styleLabel);
	}
	
	public ConceptBlock readConceptBlock(final String uri, String prefLabel, boolean styleLabel)
	throws SparqlPerformException {
		return readConceptBlock(uri, prefLabel, computeConceptBlockId(uri, prefLabel), styleLabel);
	}
	
	/**
	 * HierarchicalDisplayGenerator does not want to have all labels in bold, so will set the styleLabel to false
	 * 
	 * @param uri
	 * @param prefLabel
	 * @param setLabelAsPref
	 * @return
	 * @throws SparqlPerformException
	 */
	public ConceptBlock readConceptBlock(final String uri, String prefLabel, String blockId, boolean styleLabel)
	throws SparqlPerformException {
		
		final ConceptBlock cb;
		
		// if we are not in the master section, we will generate a link on the sourceConceptLabel,
		// pointing to the corresponding entry in the master section
		if(!this.conceptBlockIdPrefix.equals(this.linkDestinationIdPrefix)) {
			cb = SchemaFactory.createConceptBlock(
					blockId,
					uri,
					SchemaFactory.createLabelLink(computeRefId(uri, prefLabel, false), uri, prefLabel, styleLabel?"pref":null)
					);
		} else {
			cb = SchemaFactory.createConceptBlock(
					blockId,
					uri,
					SchemaFactory.createLabel(prefLabel, styleLabel?"pref":null)
					);
		}
		
		// add additional languages first
		if(this.additionalLabelLanguagesToInclude != null) {			
			for (Map.Entry<String, KeyValueReader<UriLang, Literal>> anEntry : this.additionalLabelLanguagesReaders.entrySet()) {
				String lang = anEntry.getKey();
				
				String labelInOtherLanguage = LabelReader.display(anEntry.getValue().read(new UriLang(uri, lang)));
				// don't display if there is no sourceConceptLabel for this language
				if(labelInOtherLanguage != null && !labelInOtherLanguage.equals("")) {
					cb.getAtt().add(
							SchemaFactory.createAtt(
									labelInOtherLanguage,
									// set the language code as the attribute key
									"lang:"+lang.toUpperCase(),
									null
							)
					);					
				}				
			}
		}
		
		for (Object o : additionalReaders) {
			
			if(o instanceof PropertyReader) {
				PropertyReader predicateReader = (PropertyReader)o;
			
				List<Value> values = predicateReader.read(URI.create(uri));
				for (Value value : values) {
	
					if(value instanceof Literal) {
						cb.getAtt().add(
								SchemaFactory.createAtt(
										((Literal)value).stringValue(),
										SKOSTags.getString(predicateReader.getPropertyURI()),
										(styleAttributes && predicateReader.getPropertyURI().toString().equals(SKOS.ALT_LABEL))?"alt-att":null
										)
								);
					} else {
						org.openrdf.model.URI aRef = (org.openrdf.model.URI)value;
						List<Value> prefs = prefLabelReader.read(URI.create(aRef.stringValue()));
						String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue();
						cb.getAtt().add(
								SchemaFactory.createAttLink(
										computeRefId(aRef.stringValue(), refPrefLabel, true),
										aRef.stringValue(),
										refPrefLabel,
										SKOSTags.getString(predicateReader.getPropertyURI()),
										null
										)
								);
					}
				}			
			} else if(o instanceof KeyValueReader) {
				KeyValueReader<org.openrdf.model.URI, org.openrdf.model.URI> trReader = (KeyValueReader<org.openrdf.model.URI, org.openrdf.model.URI>)o;
				List<org.openrdf.model.URI> tops = trReader.read(ValueFactoryImpl.getInstance().createURI(uri));
				for (org.openrdf.model.URI top : tops) {
					List<Value> prefs = prefLabelReader.read(URI.create(top.stringValue()));
					String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():top.stringValue();
					cb.getAtt().add(
						SchemaFactory.createAttLink(
								computeRefId(top.stringValue(), refPrefLabel, true),
								top.stringValue(),
								refPrefLabel,
								// TODO : need to have a Map of String properties to readers
								SKOSTags.getString(SKOSTags.KEY_TOP_CONCEPT),
								null
								)
					);
				}
			}
		}
		
		return cb;
	}
	
	public String computeConceptBlockId(String uri, String label) {
		// Generate an ID for this concept block, based on the URI and the prefLabel.
		// We need to be able to regenerate the same ID when building a reference to this concept
		
		int hashCode = (uri+label).hashCode();
		String generatedBlockId = ((this.conceptBlockIdPrefix != null)?this.conceptBlockIdPrefix:"")+Integer.toString(hashCode);
		while(this.generatedIds.contains(generatedBlockId)) {
			hashCode = hashCode + 1;
			generatedBlockId = ((this.conceptBlockIdPrefix != null)?this.conceptBlockIdPrefix:"")+Integer.toString(hashCode);
		}
		this.generatedIds.add(generatedBlockId);
		return generatedBlockId;
	}
	
	public String computeRefId(String uri, String label, boolean isInternalToThisSection) {
		// recreate the same ID for the concept we are referencing
		String refPrefix = (isInternalToThisSection)?((this.conceptBlockIdPrefix != null)?this.conceptBlockIdPrefix:""):((this.linkDestinationIdPrefix != null)?this.linkDestinationIdPrefix:"");
		String entryRefId = refPrefix+Integer.toString((uri+label).hashCode());	
		return entryRefId;
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

	public List<String> getAdditionalLabelLanguagesToInclude() {
		return additionalLabelLanguagesToInclude;
	}

	public void setAdditionalLabelLanguagesToInclude(List<String> additionalLabelLanguagesToInclude) {
		this.additionalLabelLanguagesToInclude = additionalLabelLanguagesToInclude;
	}

	public String getConceptBlockIdPrefix() {
		return conceptBlockIdPrefix;
	}

	public void setConceptBlockIdPrefix(String conceptBlockIdPrefix) {
		this.conceptBlockIdPrefix = conceptBlockIdPrefix;
	}

	public String getLinkDestinationIdPrefix() {
		return linkDestinationIdPrefix;
	}

	public void setLinkDestinationIdPrefix(String linkDestinationIdPrefix) {
		this.linkDestinationIdPrefix = linkDestinationIdPrefix;
	}

	public boolean isStyleAttributes() {
		return styleAttributes;
	}

	public void setStyleAttributes(boolean styleAttributes) {
		this.styleAttributes = styleAttributes;
	}
	
	
}
