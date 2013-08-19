package fr.sparna.rdf.skos.toolkit.solr;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Resource;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ReadWriteTextFile;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

public class SynonymsGeneratorFromSKOSLabels {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Repository repository;
	
	// le resultat
	protected StringBuffer result;
	// les langues dans lesquelles on veut inclure les synonymes
	protected List<String> langs;
	// si on veut inclure l'URI ou pas au début
	protected boolean includeURI = false;
	// include skos:prefLabels
	protected boolean includePrefLabels = true;
	// include skos:altLabels
	protected boolean includeAltLabels = true;
	// include skos:hiddenLabels
	protected boolean includeHiddenLabels = true;
	
	public SynonymsGeneratorFromSKOSLabels(Repository repository) {
		super();
		this.repository = repository;
	}

	public String generate() 
	throws SPARQLPerformException {
		// on initialise
		this.result = new StringBuffer(500);
		
		SKOSSynonymSPARQLHelper helper = new SKOSSynonymSPARQLHelper();
		log.debug(helper.getSPARQL());
		Perform.on(repository).select(helper);
		Map<URI, List<String>> labels = helper.labels;
		
		generateSynonymFile(labels);
		return this.result.toString();
	}
	
	public void generateSynonymFile(Map<URI, List<String>> labels) {
		for (URI aURI : labels.keySet()) {
			StringBuffer aLine = new StringBuffer();
			if(this.includeURI) {
				aLine.append(aURI.toString()+",");
			}
			List<String> uriLabels = labels.get(aURI);
			for (String aLabel : uriLabels) {
				aLine.append(aLabel+",");
			}
			
			if(aLine.length() > 0) {
				// on enleve la derniere virgule
				aLine = aLine.deleteCharAt(aLine.length() - 1);
				// on append au StringBuffer global
				this.result.append(aLine+"\n");
			}
		}
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



	class SKOSSynonymSPARQLHelper extends fr.sparna.rdf.skos.toolkit.GetLabelsHelper {

		private Map<URI, List<String>> labels = new HashMap<URI, List<String>>();	

		public SKOSSynonymSPARQLHelper() {
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
				if(!labels.containsKey(new URI(concept.stringValue()))) {
					labels.put(new URI(concept.stringValue()), new ArrayList<String>(Arrays.asList(new String[] {label})));
				} else {
					labels.get(new URI(concept.stringValue())).add(label);
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

	public boolean isIncludeURI() {
		return includeURI;
	}

	public void setIncludeURI(boolean includeURI) {
		this.includeURI = includeURI;
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
		SynonymsGeneratorFromSKOSLabels generator = new SynonymsGeneratorFromSKOSLabels(r);
		
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
