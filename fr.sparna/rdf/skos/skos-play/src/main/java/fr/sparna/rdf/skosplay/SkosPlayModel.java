package fr.sparna.rdf.skosplay;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

import fr.sparna.rdf.sesame.toolkit.languages.Languages.Language;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SelectSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQuery;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryFactoryException;
import fr.sparna.rdf.sesame.toolkit.repository.StringRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromStream;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromUrl;
import fr.sparna.rdf.sesame.toolkit.repository.operation.RepositoryOperationException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class SkosPlayModel {

	protected Repository repository;
	
	public void load(InputStream file, RDFFormat format, boolean rdfsInference, boolean owl2skos)
	throws SkosPlayModelException {
		
		try {
			RepositoryBuilder localRepositoryBuilder = createRepositoryBuilder(rdfsInference);		
			localRepositoryBuilder.addOperation(new LoadFromStream(file, format));
			repository = localRepositoryBuilder.createNewRepository();
		} catch (RepositoryFactoryException e) {
			throw new SkosPlayModelException("Exception when loading input data", e);
		}

		// apply OWL2SKOS rules if needed
		try {
			if(owl2skos) {
				// apply inference
				ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getOWL2SKOSRuleset()));
				au.execute(repository);
			}
		} catch (RepositoryOperationException e) {
			throw new SkosPlayModelException("Exception when applying OWL2SKOS rules", e);
		}		
	}
	
	public void load(String url, boolean rdfsInference, boolean owl2skos)
	throws SkosPlayModelException {
		// we are loading an RDF file from the web, use the localRepositoryBuilder and apply inference if required
		if(!StringRepositoryFactory.isEndpointURL(url)) {
			
			try {
				RepositoryBuilder localRepositoryBuilder = createRepositoryBuilder(rdfsInference);
				localRepositoryBuilder.addOperation(new LoadFromUrl(new URL(url)));
				repository = localRepositoryBuilder.createNewRepository();
			} catch (Exception e) {
				throw new SkosPlayModelException("Exception when trying to load URL "+url, e);
			}
			
			// apply OWL2SKOS rules if needed
			try {
				if(owl2skos && !StringRepositoryFactory.isEndpointURL(url)) {
					// apply inference
					ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SKOSRules.getOWL2SKOSRuleset()));
					au.execute(repository);
				}
			} catch (RepositoryOperationException e) {
				throw new SkosPlayModelException("Exception when applying OWL2SKOS rules", e);
			}
			
		} else {
			try {
				// this is a endpoint
				repository = RepositoryBuilder.fromString(url, rdfsInference);
			} catch (RepositoryFactoryException e) {
				throw new SkosPlayModelException("Exception when trying to connect to endpoint "+url, e);
			}
		}
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public boolean isHierarchical()
	throws SkosPlayModelException {
		try {
			return Perform.on(repository).ask(new SparqlQuery(new SparqlQueryBuilder(this, "AskBroadersOrNarrowers.rq")));
		} catch (SparqlPerformException e) {
			throw new SkosPlayModelException(e);
		}
	}
	
	public boolean isMultilingual()
	throws SkosPlayModelException {
		try {
			return Perform.on(repository).ask(new SparqlQuery(new SparqlQueryBuilder(this, "AskTranslatedConcepts.rq")));
		} catch (SparqlPerformException e) {
			throw new SkosPlayModelException(e);
		}
	}
	
	public int getConceptCount()
	throws SkosPlayModelException {
		try {
			return Perform.on(repository).count(new SparqlQuery(new SparqlQueryBuilder(this, "CountConcepts.rq")));
		} catch (SparqlPerformException e) {
			throw new SkosPlayModelException(e);
		}
	}
	
	public Map<String, String> getLanguages(final String locale)
	throws SkosPlayModelException {
		final HashMap<String, String> result = new HashMap<String, String>();
		
		try {
			// retrieve list of declared languages in the data
			Perform.on(repository).select(new SelectSparqlHelper(
					new SparqlQueryBuilder(this, "ListOfSkosLanguages.rq"),
					new TupleQueryResultHandlerBase() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							String rdfLanguage = bindingSet.getValue("language").stringValue();
							Language l = fr.sparna.rdf.sesame.toolkit.languages.Languages.getInstance().withIso639P1(rdfLanguage);
							String languageName = (l != null)?l.displayIn(locale):rdfLanguage;
							result.put(
									bindingSet.getValue("language").stringValue(),
									languageName									
							);
						}
						
					}
			));
		} catch (SparqlPerformException e) {
			throw new SkosPlayModelException(e);
		}
		
		return result;
	}
	
	public Map<LabeledResource, Integer> getConceptCountByConceptScheme(final LabelReader labelReader)
	throws SkosPlayModelException {
		
		final Map<LabeledResource, Integer> conceptCountByConceptSchemes = new TreeMap<LabeledResource, Integer>();
		
		try {
			// retrieve number of concepts per concept schemes
			Perform.on(repository).select(new SelectSparqlHelper(
					new SparqlQueryBuilder(this, "ConceptCountByConceptSchemes.rq"),
					new TupleQueryResultHandlerBase() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							if(bindingSet.getValue("scheme") != null) {
								try {
									conceptCountByConceptSchemes.put(
											new LabeledResource(
													java.net.URI.create(bindingSet.getValue("scheme").stringValue()),
													LabelReader.display(labelReader.getValues((org.openrdf.model.URI)bindingSet.getValue("scheme")))
											),
											(bindingSet.getValue("conceptCount") != null)?
													((Literal)bindingSet.getValue("conceptCount")).intValue()
													:0
									);
								} catch (SparqlPerformException e) {
									throw new TupleQueryResultHandlerException(e);
								}
							}
						}						
					}
			));
		} catch (SparqlPerformException e) {
			throw new SkosPlayModelException(e);
		}
		
		return conceptCountByConceptSchemes;
	}

	private RepositoryBuilder createRepositoryBuilder(boolean rdfsInference) {
		RepositoryBuilder localRepositoryBuilder;
		
		if(rdfsInference) {
			localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));			
			// load the SKOS model to be able to infer skos:inScheme from skos:isTopConceptOf
			localRepositoryBuilder.addOperation(new LoadFromFileOrDirectory("skos.rdf"));
		} else {
			localRepositoryBuilder = new RepositoryBuilder();
		}
		
		return localRepositoryBuilder;
	}
	
	
	
}
