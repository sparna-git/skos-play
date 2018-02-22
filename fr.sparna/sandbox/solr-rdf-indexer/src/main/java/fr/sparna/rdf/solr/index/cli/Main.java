package fr.sparna.rdf.solr.index.cli;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.solr.common.SolrInputDocument;

import com.beust.jcommander.JCommander;

import fr.sparna.assembly.AssemblyLine;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.solr.index.config.AssemblyLineBuilder;
import fr.sparna.rdf.solr.index.config.schema.Config;

public class Main {

	public static void main(String[] args) throws Exception {
		Main me = new Main();
		me.start(args);
	}

	public Main() {

	}

	public void start(String... args) throws Exception {
		Parameters p = new Parameters();
		new JCommander(p, args);

		// create repository from command-line parameter
		StringRepositoryFactory repositoryFactory = new StringRepositoryFactory(p.getRdf());
		
		// create SolrServer from command-line parameter
		SolrServerFactory solrFactory = new SolrServerFactory(p.getSolr());
		
		// read config file
		// JAXBContext jaxbContext = JAXBContext.newInstance("fr.sparna.rdf.solr.index.config.schema");
		JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Config c = (Config) jaxbUnmarshaller.unmarshal(p.getConfig()); 
		
		// create assembly
		AssemblyLineBuilder builder = new AssemblyLineBuilder(
				repositoryFactory.createNewRepository(),
				solrFactory.createSolrServer()
		);
		
		// list assembly lines
		List<AssemblyLine<SolrInputDocument>> assemblies = builder.buildAssemblies(c);
		for (AssemblyLine<SolrInputDocument> anAssembly : assemblies) {
			anAssembly.start();
		}
	}
}
