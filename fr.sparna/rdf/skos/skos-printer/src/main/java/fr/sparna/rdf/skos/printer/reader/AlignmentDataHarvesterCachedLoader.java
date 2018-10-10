package fr.sparna.rdf.skos.printer.reader;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;

import fr.sparna.rdf.rdf4j.toolkit.handler.LoadURIHandler;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromString;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromUrl;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class AlignmentDataHarvesterCachedLoader implements AlignmentDataHarvesterIfc {

	protected String cacheDir;
	protected RDFFormat contentType;

	public AlignmentDataHarvesterCachedLoader(String cacheDir, RDFFormat contentType) {
		super();
		this.cacheDir = cacheDir;
		this.contentType = contentType;
	}
	
	public AlignmentDataHarvesterCachedLoader(String cacheDir) {
		this(cacheDir, null);
	}

	@Override
	public void harvestData(RepositoryConnection connection, IRI conceptScheme) {
		LoadFromUrl loader = new LoadFromUrl(null);
		loader.setCacheDir(cacheDir);
		loader.setAcceptContentType(this.contentType);
		
		// preload the data for all linked concepts
		String uriSparql = "" +
				"SELECT DISTINCT ?otherConcept "+"\n" +
				"WHERE {"+"\n" +
				"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
				((conceptScheme != null)?"?concept <"+SKOS.IN_SCHEME+"> <"+conceptScheme+"> . ":"")+"\n" +
				" ?concept ?align ?otherConcept . "+"\n" +
				// load only the concepts for which we don't know the prefLabel
				// " FILTER NOT EXISTS { ?otherConcept <"+SKOS.PREF_LABEL+"> ?otherConceptPref } . "+"\n" +
				" VALUES ?align { <"+SKOS.EXACT_MATCH+"> <"+SKOS.CLOSE_MATCH+"> <"+SKOS.RELATED_MATCH+"> <"+SKOS.BROAD_MATCH+"> <"+SKOS.NARROW_MATCH+"> }"+"\n" +
				" } ORDER BY ?otherConcept"
				;
		
		// trigger the load
		Perform.on(connection).select(
				uriSparql,
				new LoadURIHandler(connection, loader)
		);
		
		// now preload the concept schemes.
		// this would also preload the schemes from the original data, and we avoid this with a FILTER NOT EXISTS
		Perform.on(connection).select(
				"SELECT DISTINCT ?scheme "+"\n" +
				"WHERE {"+"\n" +
				"	?concept a <"+SKOS.CONCEPT+"> ."+"\n" +
				"   ?concept <"+SKOS.IN_SCHEME+"> ?scheme . "+ "\n" +
				"   FILTER NOT EXISTS { ?scheme ?p ?o } "+ "\n" +
				"}",
				new LoadURIHandler(connection, loader)
		);
		
		// remove the data from Getty
		connection.remove(
				connection.getValueFactory().createIRI("http://vocab.getty.edu/aat/"),
				RDFS.LABEL,
				null,
				connection.getValueFactory().createIRI("http://vocab.getty.edu/aat/")
		);
		
		// now add special data for some well-know thesaurus names
		final String wellKnownThesauriNames = "@prefix skos:<http://www.w3.org/2004/02/skos/core#> . "
				+ "<http://dbpedia.org/resource/> skos:prefLabel \"DBpedia (english)\"@en , \"DBpedia (anglophone)\"@fr ."
				+ "<http://fr.dbpedia.org/resource/> skos:prefLabel \"DBpedia (french)\"@en , \"DBpedia (francophone)\"@fr ."
				+ "<http://data.bnf.fr/ark:/12148/> skos:prefLabel \"DataBnF (Rameau)\"@en , \"DataBnF (Rameau)\"@fr ."
				+ "<http://vocab.getty.edu/aat/> skos:prefLabel \"AAT Getty\"@en , \"AAT Getty\"@fr ."
				;
		
		new LoadFromString(wellKnownThesauriNames, "turtle").accept(connection);	
		
	}
	
	
	
}
