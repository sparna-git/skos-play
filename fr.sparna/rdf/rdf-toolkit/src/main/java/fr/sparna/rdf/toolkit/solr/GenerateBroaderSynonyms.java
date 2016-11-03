package fr.sparna.rdf.toolkit.solr;

import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ReadWriteTextFile;
import fr.sparna.rdf.sesame.toolkit.repository.AutoDetectRepositoryFactory;
import fr.sparna.rdf.skos.toolkit.solr.SynonymsGeneratorFromSKOSBroaders;
import fr.sparna.rdf.skos.toolkit.solr.SynonymsGeneratorFromSKOSLabels;
import fr.sparna.rdf.toolkit.ToolkitCommandIfc;

public class GenerateBroaderSynonyms implements ToolkitCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object o) throws Exception {
		// retrieve arguments
		ArgumentsGenerateBroaderSynonyms args = (ArgumentsGenerateBroaderSynonyms)o;
	
		// lire le RDF d'input
		Repository r = new AutoDetectRepositoryFactory(args.getInput()).createNewRepository();

		// Préparer le générateur
		SynonymsGeneratorFromSKOSBroaders gen = new SynonymsGeneratorFromSKOSBroaders(r);
		if(args.getLanguages() != null && args.getLanguages().size() > 0) {
			gen.setLangs(args.getLanguages());
		}
		
		// Générer
		ReadWriteTextFile.setContents(args.getOutput(), gen.generate());
	}
	
}
