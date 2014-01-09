package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
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

	protected SKOSTags skosTags;
	
	protected Repository repository;
	
	// list of URIs of SKOS properties to read for each concept block
	protected List<String> skosPropertiesToRead;
	// additional readers corresponding to skosPropertiesToRead
	protected List<PropertyReader> additionalReaders = new ArrayList<PropertyReader>();

	// prefLabelReader
	protected PropertyReader prefLabelReader;
	
	// should we add the Top Concepts ?
	protected boolean includeTopConcepts = false;
	protected KeyValueReader<org.openrdf.model.URI, org.openrdf.model.URI> topConceptReader;
	
	// should we include linguistic equivalents ?
	protected List<String> additionalLabelLanguagesToInclude = null;
	// use a TreeMap to garantee ordering by language code
	protected Map<String, KeyValueReader<UriLang, Literal>> additionalLabelLanguagesReaders = new TreeMap<String, KeyValueReader<UriLang, Literal>>();
	
	// concept ID prefixes, to distinguish multiple concept block of the same concept in a document containing multiple displays
	protected String conceptBlockIdPrefix;
	// link prefixes, to make links point to a concept block in another display
	protected String linkDestinationIdPrefix;
	
	public ConceptBlockReader(
			Repository repository,
			List<String> skosPropertiesToRead,
			List<String> additionalLabelLanguagesToInclude
	) {
		super();
		this.repository = repository;
		this.skosPropertiesToRead = skosPropertiesToRead;
		this.additionalLabelLanguagesToInclude = additionalLabelLanguagesToInclude;
	}	
	
	public ConceptBlockReader(
			Repository repository,
			List<String> skosPropertiesToRead
	) {
		this(repository, skosPropertiesToRead, null);
	}
	
	public ConceptBlockReader(
			Repository repository
	) {
		this(repository, null);
	}
	
	// called by a DisplayGenerator
	protected void initInternal(
			String lang,
			final URI conceptScheme,
			String conceptBlockIdPrefix,
			String linkDestinationIdPrefix
	) {
		this.skosTags = SKOSTags.getInstance(lang);
		this.setIncludeTopConcepts(includeTopConcepts);
		
		this.conceptBlockIdPrefix = conceptBlockIdPrefix;
		this.linkDestinationIdPrefix = linkDestinationIdPrefix;
		
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
		
		// for each SKOS property to read...
		if(this.skosPropertiesToRead != null) {
			for (String aProperty : this.skosPropertiesToRead) {
				// add a PropertyReader to read the corresponding property
				// or its inverse property
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
		
		// init topConcepts reader
		this.topConceptReader = new KeyValueReader<org.openrdf.model.URI, org.openrdf.model.URI>(
				repository,
				new GetTopConceptsOfConcept(null)
		);
		
		// init additional languages reader
		if(this.additionalLabelLanguagesToInclude != null) {
			for (String anAdditionalLang : this.additionalLabelLanguagesToInclude) {
				additionalLabelLanguagesReaders.put(
						anAdditionalLang, 
						new KeyValueReader<UriLang, Literal>(
								repository,
								new GetLabels(SKOS.PREF_LABEL, anAdditionalLang, conceptScheme.toString())
						)
				);
			}
		}
	}
	
	public ConceptBlock readConceptBlockForSynonym(final String uri, final String altLabel, final String prefLabel)
	throws SparqlPerformException {
		ConceptBlock cb = SchemaFactory.createConceptBlock(computeConceptBlockId(uri, altLabel), uri, SchemaFactory.createLabel(altLabel, "alt"));
		cb.getAtt().add(SchemaFactory.createAttLink(
				computeRefId(uri, prefLabel),
				uri,
				prefLabel,
				this.skosTags.getStringForURI(SKOS.PREF_LABEL),
				"pref")
		);
		return cb;
	}
	
	
	public ConceptBlock readConceptBlock(final String uri, boolean setLabelAsPref)
	throws SparqlPerformException {
		// set label (or URI if no label can be found)
		String label = LabelReader.display(prefLabelReader.read(URI.create(uri)));
		label = (label.trim().equals(""))?uri:label;
		return this.readConceptBlock(uri, label, setLabelAsPref);
	}
	
	public ConceptBlock readConceptBlock(final String uri, String prefLabel, boolean setLabelAsPref)
	throws SparqlPerformException {
		return readConceptBlock(uri, prefLabel, computeConceptBlockId(uri, prefLabel), setLabelAsPref);
	}
	
	/**
	 * HierarchicalDisplayGenerator does not want to have all labels bolded, so will set the setLabelAsPref to false
	 * 
	 * @param uri
	 * @param prefLabel
	 * @param setLabelAsPref
	 * @return
	 * @throws SparqlPerformException
	 */
	public ConceptBlock readConceptBlock(final String uri, String prefLabel, String blockId, boolean setLabelAsPref)
	throws SparqlPerformException {
		
		final ConceptBlock cb;
		if(!this.conceptBlockIdPrefix.equals(this.linkDestinationIdPrefix)) {
			cb = SchemaFactory.createConceptBlock(
					blockId,
					uri,
					SchemaFactory.createLabelLink(computeRefId(uri, prefLabel), uri, prefLabel, setLabelAsPref?"pref":null)
					);
		} else {
			cb = SchemaFactory.createConceptBlock(
					blockId,
					uri,
					SchemaFactory.createLabel(prefLabel, setLabelAsPref?"pref":null)
					);
		}
		
		// add additional languages first
		if(this.additionalLabelLanguagesToInclude != null) {			
			for (Map.Entry<String, KeyValueReader<UriLang, Literal>> anEntry : this.additionalLabelLanguagesReaders.entrySet()) {
				String lang = anEntry.getKey();
				
				cb.getAtt().add(
						SchemaFactory.createAtt(
								LabelReader.display(anEntry.getValue().read(new UriLang(uri, lang))),
								// set the language code as the attribute key
								lang,
								null
						)
				);
				
			}
		}
		
		for (PropertyReader predicateReader : additionalReaders) {
			List<Value> values = predicateReader.read(URI.create(uri));
			for (Value value : values) {

				if(value instanceof Literal) {
					cb.getAtt().add(
							SchemaFactory.createAtt(
									((Literal)value).stringValue(),
									this.skosTags.getString(predicateReader.getPropertyURI()),
									(predicateReader.getPropertyURI().toString().equals(SKOS.ALT_LABEL))?"alt":null
									)
							);
				} else {
					org.openrdf.model.URI aRef = (org.openrdf.model.URI)value;
					List<Value> prefs = prefLabelReader.read(URI.create(aRef.stringValue()));
					String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue();
					cb.getAtt().add(
							SchemaFactory.createAttLink(
									computeRefId(aRef.stringValue(), refPrefLabel),
									aRef.stringValue(),
									refPrefLabel,
									this.skosTags.getString(predicateReader.getPropertyURI()),
									null
									)
							);
				}
			}
		}
		
		if(this.includeTopConcepts) {
			List<org.openrdf.model.URI> tops = this.topConceptReader.read(ValueFactoryImpl.getInstance().createURI(uri));
			for (org.openrdf.model.URI top : tops) {
				List<Value> prefs = prefLabelReader.read(URI.create(top.stringValue()));
				String refPrefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():top.stringValue();
				String entryRef = Integer.toString((top.stringValue()+refPrefLabel).hashCode());
				cb.getAtt().add(
					SchemaFactory.createAttLink(
							entryRef,
							top.stringValue(),
							refPrefLabel,
							skosTags.getString(SKOSTags.KEY_TOP_CONCEPT),
							null
							)
				);
			}
		}
		
		return cb;
	}
	
	public String computeConceptBlockId(String uri, String label) {
		// Generate an ID for this concept block, based on the URI and the prefLabel.
		// We need to be able to regenerate the same ID when building a reference to this concept
		String conceptBlockId = ((this.conceptBlockIdPrefix != null)?this.conceptBlockIdPrefix:"")+Integer.toString((uri+label).hashCode());
		return conceptBlockId;
	}
	
	public String computeRefId(String uri, String label) {
		// recreate the same ID for the concept we are referencing
		String entryRefId = ((this.linkDestinationIdPrefix != null)?this.linkDestinationIdPrefix:"")+Integer.toString((uri+label).hashCode());	
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

	public boolean isIncludeTopConcepts() {
		return includeTopConcepts;
	}

	public void setIncludeTopConcepts(boolean includeTopConcepts) {
		this.includeTopConcepts = includeTopConcepts;
	}

	public List<String> getAdditionalLabelLanguagesToInclude() {
		return additionalLabelLanguagesToInclude;
	}

	public void setAdditionalLabelLanguagesToInclude(List<String> additionalLabelLanguagesToInclude) {
		this.additionalLabelLanguagesToInclude = additionalLabelLanguagesToInclude;
	}
	
}
