package fr.sparna.rdf.skos.printer.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.reader.IriLang;
import fr.sparna.rdf.rdf4j.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.PropertyLangValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.PropertyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.PropertyValueReader.GenericQuerySupplier;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.builders.CollectionsOfConceptReader;
import fr.sparna.rdf.skos.toolkit.builders.TopConceptsOfConceptReader;

public class ConceptBlockReader {
	
	// list of URIs of SKOS properties to read for each concept block
	protected List<String> skosPropertiesToRead;
	// additional readers corresponding to skosPropertiesToRead
	// each reader is associated to the property URI to insert in the result
	// TODO : need to make PropertyReader and KeyValueReader implement the same interface
	protected Map<String, Object> additionalReaders = new HashMap<String, Object>();

	// prefLabelReader
	protected KeyValueReader<IRI, Literal> prefLabelReader;
	
	// notationReader
	protected KeyValueReader<IRI, Value> notationReader;
	
	// should we include linguistic equivalents ?
	protected List<String> additionalLabelLanguagesToInclude = null;
	// use a TreeMap to garantee ordering by language code
	protected Map<String, KeyValueReader<IriLang, Literal>> additionalLabelLanguagesReaders = new TreeMap<String, KeyValueReader<IriLang, Literal>>();
	
	// concept ID prefixes, to distinguish multiple concept block of the same concept in a document containing multiple displays
	protected String conceptBlockIdPrefix;
	// link prefixes, to make links point to a concept block in another display
	protected String linkDestinationIdPrefix;
	// whether or not to add a style ('pref', 'alt', etc.) to the attributes for each concept block
	protected boolean styleAttributes = true;
	
	// already generated IDs, to avoid clashes
	protected List<String> generatedIds = new ArrayList<String>();
	
	public ConceptBlockReader() {
		super();
	}	
	
	// called by a DisplayGenerator
	protected void initInternal(
			String lang,
			final IRI conceptScheme,
			String conceptBlockIdPrefix
	) {
		this.conceptBlockIdPrefix = conceptBlockIdPrefix;
		
		// no concept scheme filtering here - we want to be able to read prefLabel independently from the conceptScheme
		prefLabelReader = new PropertyLangValueReader(
				SimpleValueFactory.getInstance().createIRI(SKOS.PREF_LABEL),
				lang
		);
		// if we need to disable preload
		prefLabelReader.setPreLoad(false);
		
		notationReader = new PropertyValueReader(
				SimpleValueFactory.getInstance().createIRI(SKOS.NOTATION)
		);
		notationReader.setPreLoad(false);

		// setup additional readers
		this.additionalReaders = new HashMap<String, Object>();
		
		// for each SKOS property to read...
		if(this.skosPropertiesToRead != null) {
			for (String aProperty : this.skosPropertiesToRead) {
				
				if(aProperty.equals(SKOSPLAY.TOP_TERM)) {
					additionalReaders.put(
						SKOSPLAY.TOP_TERM,
						new TopConceptsOfConceptReader(null)
					);					
				} else if(aProperty.equals(SKOSPLAY.MEMBER_OF)) {
					additionalReaders.put(
						SKOSPLAY.MEMBER_OF,
						new CollectionsOfConceptReader(null)
					);					
				} else {
					
					// add a PropertyReader to read the corresponding property
					// or its inverse property
					String inverseProperty = SKOS.getInverseOf(aProperty);
					additionalReaders.put(aProperty,
							(conceptScheme != null)
							?new PropertyValueReader(new GenericQuerySupplier(
									(inverseProperty != null)?("<"+aProperty+">"+"/"+"^<"+inverseProperty+">"):"<"+aProperty+">",
									(SKOS.isDatatypeProperty(aProperty) && !aProperty.equals(SKOS.NOTATION))?lang:null,
									SimpleValueFactory.getInstance().createIRI(SKOS.IN_SCHEME),
									SimpleValueFactory.getInstance().createIRI(conceptScheme.toString())
							))
							:new PropertyValueReader(new GenericQuerySupplier(
									(inverseProperty != null)?("<"+aProperty+">"+"/"+"^<"+inverseProperty+">"):"<"+aProperty+">",
									(SKOS.isDatatypeProperty(aProperty) && !aProperty.equals(SKOS.NOTATION))?lang:null,
									null,
									null
							))
					);					
				}
			}
		}
		
		// init additional languages reader
		if(this.additionalLabelLanguagesToInclude != null) {
			for (String anAdditionalLang : this.additionalLabelLanguagesToInclude) {
				additionalLabelLanguagesReaders.put(
						anAdditionalLang, 
						new fr.sparna.rdf.skos.toolkit.builders.LabelReader(SKOS.PREF_LABEL, anAdditionalLang, (conceptScheme != null)?conceptScheme.toString():null)
				);
			}
		}
	}
	
	public ConceptBlock readConceptBlockForSynonym(final String uri, final String altLabel, final String prefLabel) {
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
	
	
	public ConceptBlock readConceptBlock(RepositoryConnection connection, final String uri, boolean styleLabel, boolean prependNotation) {
		// set sourceConceptLabel (or URI if no sourceConceptLabel can be found)
		String label = LabelReader.display(prefLabelReader.read(SimpleValueFactory.getInstance().createIRI(uri), connection));
		
		if(prependNotation) {
			List<Value> notations = notationReader.read(SimpleValueFactory.getInstance().createIRI(uri), connection);
			label = ((notations.size() > 0)?notations.get(0).stringValue()+" ":"")+label;
		}
		
		// defaults to displaying the URI if the generated label is empty, and display the short URI
		label = (label.trim().equals(""))?uri:label;
		return this.readConceptBlock(connection, uri, label, styleLabel);
	}
	
	public ConceptBlock readConceptBlock(RepositoryConnection connection, final String uri, String prefLabel, boolean styleLabel) {
		return readConceptBlock(connection, uri, prefLabel, computeConceptBlockId(uri, prefLabel), styleLabel);
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
	public ConceptBlock readConceptBlock(RepositoryConnection connection, final String uri, String prefLabel, String blockId, boolean styleLabel) {
		
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
			for (Map.Entry<String, KeyValueReader<IriLang, Literal>> anEntry : this.additionalLabelLanguagesReaders.entrySet()) {
				String lang = anEntry.getKey();
				
				String labelInOtherLanguage = LabelReader.display(anEntry.getValue().read(new IriLang(uri, lang), connection));
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
		
		for (Map.Entry<String, Object> entry : additionalReaders.entrySet()) {
			
			Object o = entry.getValue();
			
			if(o instanceof PropertyValueReader) {
				PropertyValueReader predicateReader = (PropertyValueReader)o;
			
				List<Value> values = predicateReader.read(SimpleValueFactory.getInstance().createIRI(uri), connection);
				for (Value value : values) {
	
					if(value instanceof Literal) {
						cb.getAtt().add(
								SchemaFactory.createAtt(
										((Literal)value).stringValue(),
										SKOSTags.getStringForURI(entry.getKey()),
										(styleAttributes && entry.getKey().equals(SKOS.ALT_LABEL))?"alt-att":null
										)
								);
					} else {
						IRI aRef = (IRI)value;
						List<Literal> prefs = prefLabelReader.read(SimpleValueFactory.getInstance().createIRI(aRef.stringValue()), connection);
						String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue();
						cb.getAtt().add(
								SchemaFactory.createAttLink(
										computeRefId(aRef.stringValue(), refPrefLabel, true),
										aRef.stringValue(),
										refPrefLabel,
										SKOSTags.getStringForURI(entry.getKey()),
										(styleAttributes)?"pref":null
										)
								);
					}
				}			
			} else if(o instanceof KeyValueReader) {
				// get the result of the reader
				KeyValueReader<IRI, IRI> reader = (KeyValueReader<IRI, IRI>)o;
				List<IRI> values = reader.read(SimpleValueFactory.getInstance().createIRI(uri), connection);
				
				// lookup the label of the values
				for (IRI aValue : values) {
					List<Literal> prefs = prefLabelReader.read(SimpleValueFactory.getInstance().createIRI(aValue.stringValue()), connection);
					String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aValue.stringValue();
					
					String refNotation = null;
					if(entry.getKey().equals(SKOSPLAY.MEMBER_OF)) {
						// in case we are referencing a collection / micro-thesaurus, attempt to fetch the notation (UNESCO thesaurus)
						List<Value> notations = notationReader.read(SimpleValueFactory.getInstance().createIRI(aValue.stringValue()), connection);
						refNotation = (notations.size() > 0)?notations.get(0).stringValue():null;
					}
					
					cb.getAtt().add(
						SchemaFactory.createAttLink(
								computeRefId(aValue.stringValue(), refPrefLabel, true),
								aValue.stringValue(),
								((refNotation != null)?refNotation+" ":"")+refPrefLabel,
								SKOSTags.getString(entry.getKey()),
								(styleAttributes)?"pref":null
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

	public KeyValueReader<IRI, Literal> getPrefLabelReader() {
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
