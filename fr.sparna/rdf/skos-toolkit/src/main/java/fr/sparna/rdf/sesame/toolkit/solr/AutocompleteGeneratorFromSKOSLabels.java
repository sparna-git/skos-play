package fr.sparna.rdf.sesame.toolkit.solr;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Resource;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ReadWriteTextFile;
import fr.sparna.commons.lang.ListMap;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.skos.SKOS;

public class AutocompleteGeneratorFromSKOSLabels {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Repository repository;
	
	// le resultat
	protected StringBuffer result;
	// les langues dans lesquelles on veut generer la liste d'autocompletion
	protected List<String> langs;
	// include skos:prefLabels
	protected boolean includePrefLabels = true;
	// include skos:altLabels
	protected boolean includeAltLabels = true;
	// include skos:hiddenLabels
	protected boolean includeHiddenLabels = true;
	// weight of prefLabels
	protected float prefLabelsWeight = 3.0f;
	
	public AutocompleteGeneratorFromSKOSLabels(Repository repository) {
		super();
		this.repository = repository;
	}

	public String generate() 
	throws SPARQLExecutionException {
		// on initialise
		this.result = new StringBuffer(500);
		
		AutocompleteGeneratorHelper helper = new AutocompleteGeneratorHelper();
		SesameSPARQLExecuter.newExecuter(this.repository).executeSelect(helper);
		
		addToAutocompleteFile(helper.prefLabels, 2.0f);
		addToAutocompleteFile(helper.altLabels, 1.0f);
		addToAutocompleteFile(helper.hiddenLabels, 1.0f);
		return this.result.toString();
	}
	
	public void addToAutocompleteFile(Map<URI, List<String>> labels, float weight) {
		for (URI aURI : labels.keySet()) {
			StringBuffer aLine = new StringBuffer();
			List<String> uriLabels = labels.get(aURI);
			for (String aLabel : uriLabels) {
				aLine.append(aLabel+"\t"+weight+"\r\n");
			}
			this.result.append(aLine);
		}
	}

	
	class AutocompleteGeneratorHelper extends fr.sparna.rdf.sesame.toolkit.skos.GetLabelsHelper {

		private ListMap<URI, String> prefLabels = new ListMap<URI, String>();
		private ListMap<URI, String> altLabels = new ListMap<URI, String>();
		private ListMap<URI, String> hiddenLabels = new ListMap<URI, String>();

		public AutocompleteGeneratorHelper() {
			super(
					// no concept,on ramene tout
					null,
					// includePreLabels
					includePrefLabels,
					// includeAltLabels
					includeAltLabels,
					// includeHiddenLabels
					includeHiddenLabels,
					// langs
					langs);
		}

		
		@Override
		protected void handleLabel(
				Resource concept,
				org.openrdf.model.URI labelType,
				String label,
				String lang)
				throws TupleQueryResultHandlerException {
			try {
				String trimmedLabel = label.trim();
				if(trimmedLabel.equals("")) {
					log.warn("Found an empty trimmed label of tyep '"+labelType+"' on concept "+concept.stringValue());
				}
				
				if(labelType.stringValue().equals(SKOS.PREF_LABEL)) {
					prefLabels.add(new URI(concept.stringValue()), label);
				} else if(labelType.stringValue().equals(SKOS.ALT_LABEL)) {
					altLabels.add(new URI(concept.stringValue()), label);
				} else if(labelType.stringValue().equals(SKOS.HIDDEN_LABEL)) {
					hiddenLabels.add(new URI(concept.stringValue()), label);
				} 
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public String getResultAsString() {
		return result.toString();
	}	

	public List<String> getLangs() {
		return langs;
	}

	public void setLangs(List<String> langs) {
		this.langs = langs;
	}
	
	public boolean isIncludePrefLabels() {
		return includePrefLabels;
	}

	public void setIncludePrefLabels(boolean includePrefLabels) {
		this.includePrefLabels = includePrefLabels;
	}

	public boolean isIncludeAltLabels() {
		return includeAltLabels;
	}

	public void setIncludeAltLabels(boolean includeAltLabels) {
		this.includeAltLabels = includeAltLabels;
	}

	public boolean isIncludeHiddenLabels() {
		return includeHiddenLabels;
	}

	public void setIncludeHiddenLabels(boolean includeHiddenLabels) {
		this.includeHiddenLabels = includeHiddenLabels;
	}
	
	public float getPrefLabelsWeight() {
		return prefLabelsWeight;
	}

	public void setPrefLabelsWeight(float prefLabelsWeight) {
		this.prefLabelsWeight = prefLabelsWeight;
	}

	/**
	 * args[0] : chemin vers le fichier SKOS a parser
	 * args[1] : chemin vers le fichier d'output
	 * args[x] : liste de langues (optionnelles)
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromString(args[0]);		
		AutocompleteGeneratorFromSKOSLabels generator = new AutocompleteGeneratorFromSKOSLabels(r);
		
		if(args.length > 2) {
			List<String> langs = new ArrayList<String>();
			for (String anArg : Arrays.copyOfRange(args, 2, args.length)) {
				System.out.println("Handling language "+anArg);
				langs.add(anArg);
			}
			generator.setLangs(langs);
		}
		
		File outputFile = new File(args[1]);
		if(!outputFile.exists()) {
			outputFile.createNewFile();
		}
		
		ReadWriteTextFile.setContents(outputFile, generator.generate(), "UTF-8");
	}
	
}
