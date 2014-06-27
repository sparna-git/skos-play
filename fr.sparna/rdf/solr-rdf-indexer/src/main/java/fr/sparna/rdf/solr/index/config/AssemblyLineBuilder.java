package fr.sparna.rdf.solr.index.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

import fr.sparna.assembly.AssemblyLine;
import fr.sparna.assembly.AssemblySource;
import fr.sparna.assembly.AssemblyStation;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.reader.KeyValueHelperBase;
import fr.sparna.rdf.sesame.toolkit.reader.UriKeyMappingGenerator;
import fr.sparna.rdf.sesame.toolkit.reader.UriToLiteralBindingSetReader;
import fr.sparna.rdf.solr.index.SolrIndexableFactory;
import fr.sparna.rdf.solr.index.config.schema.Config;
import fr.sparna.rdf.solr.index.config.schema.EntityConfig;
import fr.sparna.rdf.solr.index.config.schema.Field;
import fr.sparna.rdf.solr.index.config.schema.FieldMap;
import fr.sparna.rdf.solr.index.config.schema.Source;
import fr.sparna.rdf.solr.index.consume.SolrConsumer;
import fr.sparna.rdf.solr.index.source.SparqlRdfAssemblySource;
import fr.sparna.rdf.solr.index.step.KeyValueRdfIndexingStation;


public class AssemblyLineBuilder {

	protected Repository repository;
	protected SolrServer solrServer;
	
	public AssemblyLineBuilder(Repository repository, SolrServer solrServer) {
		super();
		this.repository = repository;
		this.solrServer = solrServer;
	}

	public List<AssemblyLine<SolrInputDocument>> buildAssemblies(Config config) throws ConfigurationException {
		
		List<AssemblyLine<SolrInputDocument>> result = new ArrayList<AssemblyLine<SolrInputDocument>>();
		
		for (EntityConfig anEntityConfig : config.getEntityConfig()) {
			result.add(buildAssembly(anEntityConfig));
		}
		
		return result;		
		
	}
	
	public AssemblyLine<SolrInputDocument> buildAssembly(EntityConfig config) throws ConfigurationException {
		
		AssemblySource<SolrInputDocument> source = buildAssemblySource(config.getSource(), config.getFieldMap().getUriField());
		List<AssemblyStation<SolrInputDocument>> stations = buildAssemblyStations(config.getFieldMap());
		
		AssemblyLine<SolrInputDocument> assemblyLine = new AssemblyLine<SolrInputDocument>(
				source,
				stations,
				new SolrConsumer(this.solrServer)
		);
		
		return assemblyLine;
	}
	
	
	private AssemblySource<SolrInputDocument> buildAssemblySource(Source source, String idField) throws ConfigurationException {
		SparqlQuery q;
		if(source.getType() != null) {
			q = new SparqlQuery(new SparqlQueryBuilder(
					"SELECT ?x WHERE { ?x a <"+source.getType()+"> }"
			));
		} else {
			q = new SparqlQuery(new SparqlQueryBuilder(
					source.getSparql().getValue()
			));
		}
		
		AssemblySource<SolrInputDocument> result;
		try {
			result = new SparqlRdfAssemblySource<SolrInputDocument>(
					repository,
					q,
					new SolrIndexableFactory(idField)
			);
		} catch (SparqlPerformException e) {
			throw new ConfigurationException(e);
		}
		
		return result;
	}
	
	private List<AssemblyStation<SolrInputDocument>> buildAssemblyStations(FieldMap fieldMap) {
		List<AssemblyStation<SolrInputDocument>> result = new ArrayList<AssemblyStation<SolrInputDocument>>();
			
		
		for (Field aField : fieldMap.getField()) {

			KeyValueHelperBase<URI, Literal> kvh;
			if(aField.getPath() != null) {
				StringBuffer sb = new StringBuffer();
				
				sb.append("SELECT ?key ?value WHERE { "+"\n");
				sb.append("  ?key <"+aField.getPath().getValue()+"> ?value ."+"\n");
				if(aField.getPath().getLang() != null) {
					sb.append(" FILTER langMatches(?value, '"+aField.getPath().getLang()+"') "+"\n");
				}
				sb.append("}");
				
				kvh = new KeyValueHelperBase<URI, Literal>(
						new SparqlQueryBuilder(sb.toString()),
						new UriKeyMappingGenerator("key"),
						new UriToLiteralBindingSetReader("key", "value")
				);
			} else {
				kvh = new KeyValueHelperBase<URI, Literal>(
						new SparqlQueryBuilder(aField.getSparql().getValue()),
						new UriKeyMappingGenerator(aField.getSparql().getEntityVar()),
						new UriToLiteralBindingSetReader(aField.getSparql().getEntityVar())
				);
			}
			
			KeyValueRdfIndexingStation aStation = new KeyValueRdfIndexingStation(
					repository,
					aField.getName(),
					kvh
			);
			
			result.add(aStation);
		}
		
		return result;
	}
	
	
}
