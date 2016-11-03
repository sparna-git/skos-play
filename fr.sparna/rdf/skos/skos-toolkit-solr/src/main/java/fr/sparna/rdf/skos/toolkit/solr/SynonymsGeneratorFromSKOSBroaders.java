package fr.sparna.rdf.skos.toolkit.solr;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ReadWriteTextFile;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

public class SynonymsGeneratorFromSKOSBroaders {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected Repository repository;
	
	// le resultat
	protected StringBuffer result;
	// les langues dans lesquelles on veut inclure les labels
	protected List<String> langs;
	
	public SynonymsGeneratorFromSKOSBroaders(Repository repository) {
		super();
		this.repository = repository;
	}

	public String generate() 
	throws SparqlPerformException {
		// on initialise
		this.result = new StringBuffer(500);

		LabelsSPARQLHelper labelsHelper = new LabelsSPARQLHelper();
		log.debug(labelsHelper.getSPARQL());
		Perform.on(repository).select(labelsHelper);
		Map<URI, List<String>> labels = labelsHelper.labels;

		BroaderSPARQLHelper broaderHelper = new BroaderSPARQLHelper();
		log.debug(broaderHelper.getSPARQL());
		Perform.on(repository).select(broaderHelper);
		Map<URI, List<URI>> hierarchy = broaderHelper.broaders;

		generateBroaderLabelsFile(labels, hierarchy);
		return this.result.toString();
	}
	
	private List<String> getBroaderLabelsRec(URI conceptURI, Map<URI, List<String>> conceptLabels, Map<URI, List<URI>> hierarchy) {
		log.debug("Reading broaders of "+conceptURI);
		List<String> broaderLabels = new ArrayList<String>();
		if(hierarchy.containsKey(conceptURI)) {
			for (URI aBroader : hierarchy.get(conceptURI)) {
				if(conceptLabels.get(aBroader) != null) {
					broaderLabels.addAll(conceptLabels.get(aBroader));
				}
				broaderLabels.addAll(getBroaderLabelsRec(aBroader, conceptLabels, hierarchy));
			}
		}
		return broaderLabels;
	}

	public void generateBroaderLabelsFile(Map<URI, List<String>> conceptLabels, Map<URI, List<URI>> hierarchy) {
		for (URI aURI : conceptLabels.keySet()) {
			StringBuffer aLine = new StringBuffer();
			List<String> uriLabels = conceptLabels.get(aURI);
			List<String> broaderLabels = getBroaderLabelsRec(aURI, conceptLabels, hierarchy);

			if(broaderLabels != null && broaderLabels.size() > 0) {
				for (String aLabel : uriLabels) {
					aLine.append(aLabel+",");
				}
				// on enleve la derniere virgule
				aLine = aLine.deleteCharAt(aLine.length() - 1);
				// on insere le caractere =>
				aLine.append(" => ");
				// on insere les labels des broader
				for (String aBroaderLabel : broaderLabels) {
					aLine.append(aBroaderLabel+",");
				}
				// on enleve la derniere virgule
				aLine = aLine.deleteCharAt(aLine.length() - 1);
				// on append au StringBuffer global
				this.result.append(aLine+"\n");
			}
		}
	}

	class LabelsSPARQLHelper extends fr.sparna.rdf.skos.toolkit.GetLabelsHelper {

		private Map<URI, List<String>> labels = new HashMap<URI, List<String>>();	

		public LabelsSPARQLHelper() {
			super(
					// no concept, on ramene tout
					null,
					// includePreLabels
					true,
					// includeAltLabels
					false,
					// includeHiddenLabels
					false,
					// langs
					langs);
		}

		@Override
		protected void handleLabel(
				Resource concept,
				org.eclipse.rdf4j.model.URI labelType,
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

	class BroaderSPARQLHelper extends fr.sparna.rdf.skos.toolkit.GetBroadersHelper {

		// on veur gérer la poly-hierarchie
		private Map<URI, List<URI>> broaders = new HashMap<URI, List<URI>>();

		public BroaderSPARQLHelper() {
			super(
					// concept URI
					null, 
					// order by lang
					null
			);
		}

		@Override
		protected void handleBroaderConcept(Resource concept, Resource broader)
		throws TupleQueryResultHandlerException {
			log.trace("Concept "+concept.stringValue()+" has broader "+broader.stringValue());
			URI conceptURI = URI.create(concept.stringValue());
			URI broaderURI = URI.create(broader.stringValue());
			if(!broaders.containsKey(conceptURI)) {
				broaders.put(conceptURI, new ArrayList<URI>(Arrays.asList(new URI[] { broaderURI })));
			} else {
				broaders.get(conceptURI).add(broaderURI);
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

	/**
	 * args[0] : chemin vers le fichier SKOS a parser
	 * args[1] : chemin vers le fichier d'output
	 * args[x] : liste de langues (optionnelles)
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... args) throws Exception {
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		SynonymsGeneratorFromSKOSBroaders generator = new SynonymsGeneratorFromSKOSBroaders(r);

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
