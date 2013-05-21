package fr.sparna.rdf.sesame.toolkit.repository;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.io.ClasspathUnzip;

/**
 * Provides a config for the OWLIM repository and inference engine. The config file
 * is read from the classpath, the ruleset is read from the classpath and extracted
 * into a File (OWLIM needs to read the ruleset from a File).
 * 
 * @author Thomas Francart
 */
public class OWLIMConfigProvider implements ConfigProviderIfc {

	public static final OWLIMConfigProvider RDFS_CONFIG_PROVIDER = new OWLIMConfigProvider("owlim-base.ttl","RDFS.pie");
	public static final OWLIMConfigProvider OWL_REDUCED_CONFIG_PROVIDER = new OWLIMConfigProvider("owlim-base.ttl","Owl2Rl-reduced_builtin.pie");
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String configPath;
	protected String ruleset;
	
	//An empty constructor for compatibility purpose
	public OWLIMConfigProvider() {

	}
	
	public OWLIMConfigProvider(String configPath, String ruleset) {
		super();
		this.configPath = configPath;
		this.ruleset = ruleset;
		
		// rajouter l'extension automatiquement si non presente
		if(!this.ruleset.endsWith(".pie")) {
			this.ruleset = this.ruleset+".pie";
		}
	}

	@Override
	public InputStream getConfigAsStream() {
	
		// lire le contenu de la config du repository depuis le classpath
		log.debug("Reading OWLIM config from "+this.configPath);
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath);
		if(stream == null) {
			log.debug("Cannot find config at "+this.configPath+" will try with src/main/resources"+this.configPath);
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("src/main/resources"+configPath);
		}
		
		log.debug("Using a line separator for owlim config of length "+System.getProperty("line.separator").length()+" : "+System.getProperty("line.separator"));
		StringBuilder stringBuilder = new StringBuilder(1000);
	    Scanner scanner = new Scanner(stream);
	    while (scanner.hasNextLine()) {
	    	// ATTENTION de bien utiliser la propriete et de ne PAS mettre en dur "\n"
	        stringBuilder.append(scanner.nextLine()+System.getProperty("line.separator"));
	    }
	    String configContent = stringBuilder.toString();
	    
	    // extract ruleset
	    // le ruleset est obligatoirement lu par OWLIM dans un fichier
	    // on ne peut pas le recuperer en stream et on est oblige de l'extraire
	    try {

	    	File ruleFile = new File(this.ruleset);
	    	if(ruleFile.exists()) {
	    		log.debug("Ruleset file : "+this.ruleset+" already exists, will not extract it again");
	    	} else {
		    	if(Thread.currentThread().getContextClassLoader().getResource(this.ruleset) == null) {
		    		log.debug("Unzipping src/main/resources/"+this.ruleset+" from the classpath to a file...");
		    		ClasspathUnzip.unzipFileFromClassPath("src/main/resources/"+this.ruleset, ".");
		    	} else {
		    		log.debug("Unzipping "+this.ruleset+" from the classpath to a file...");
		    		ClasspathUnzip.unzipFileFromClassPath(this.ruleset, ".");
		    	}
	    	}
			
		} catch (IOException e) {
			// TODO : handle exception (need to update method signature)
			e.printStackTrace();
		}
	    
	    // search/replace the ruleset
	    // Compile regular expression
	    // on remplace entre les quotes les caracteres, le -, le ., le /, et les espaces
	    // les \\s remplacent un espace ou une tabulation, et le ++ en demande 1 ou plusieurs (mais minimum 1)
		log.debug("Replaced ruleset in OWLIM config with "+this.ruleset);
		configContent = configContent.replaceAll(
				"owlim:ruleset[\\s]++\"[a-zA-Z-\\. /]*\"[\\s]++;",
				"owlim:ruleset \""+this.ruleset+"\" ;"
		);
		
		log.debug("Final OWLIM config content :\n"+configContent);
		
		return new ByteArrayInputStream(configContent.getBytes());
	}

	@Override
	public RDFFormat getConfigFormat() {
		return RDFFormat.TURTLE;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getRuleset() {
		return ruleset;
	}

	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	
}
