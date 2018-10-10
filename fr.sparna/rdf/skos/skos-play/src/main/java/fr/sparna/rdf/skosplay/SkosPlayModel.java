package fr.sparna.rdf.skosplay;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.AbstractTupleQueryResultHandler;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerBase;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;

import fr.sparna.rdf.rdf4j.toolkit.languages.Languages;
import fr.sparna.rdf.rdf4j.toolkit.languages.Languages.Language;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReader;
import fr.sparna.rdf.rdf4j.toolkit.repository.LocalMemoryRepositorySupplier;
import fr.sparna.rdf.rdf4j.toolkit.repository.LocalMemoryRepositorySupplier.FactoryConfiguration;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.ApplyUpdates;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromFileOrDirectory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromStream;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromUrl;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.toolkit.SKOSRules;

public class SkosPlayModel {

	protected Repository repository;
	
	public void load(InputStream file, RDFFormat format, boolean rdfsInference, boolean owl2skos)
	throws SkosPlayModelException {
		
		RepositoryBuilder localRepositoryBuilder = createRepositoryBuilder(rdfsInference);		
		localRepositoryBuilder.addOperation(new LoadFromStream(file, format));
		repository = localRepositoryBuilder.get();

		// apply OWL2SKOS rules if needed
		if(owl2skos) {
			try(RepositoryConnection connection = repository.getConnection()) {
				// apply inference
				ApplyUpdates au = ApplyUpdates.fromQueryReaders(SKOSRules.getOWL2SKOSRuleset());
				au.accept(connection);
			}
		}	
	}
	
	public void load(String url, boolean rdfsInference, boolean owl2skos)
	throws SkosPlayModelException {
		// we are loading an RDF file from the web, use the localRepositoryBuilder and apply inference if required
		if(!RepositoryBuilderFactory.isEndpointURL(url)) {
			
			try {
				RepositoryBuilder localRepositoryBuilder = createRepositoryBuilder(rdfsInference);
				localRepositoryBuilder.addOperation(new LoadFromUrl(new URL(url)));
				repository = localRepositoryBuilder.get();
			} catch (Exception e) {
				throw new SkosPlayModelException("Exception when trying to load URL "+url, e);
			}
			
			// apply OWL2SKOS rules if needed
			if(owl2skos && !RepositoryBuilderFactory.isEndpointURL(url)) {
				try(RepositoryConnection connection = repository.getConnection()) {
					// apply inference
					ApplyUpdates au = ApplyUpdates.fromQueryReaders(SKOSRules.getOWL2SKOSRuleset());
					au.accept(connection);
				}
			}
			
		} else {
			try {
				// this is a endpoint
				repository = RepositoryBuilderFactory.fromString(url).get();
			} catch (Exception e) {
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
		try(RepositoryConnection connection = repository.getConnection()) {
			return Perform.on(connection).ask(new SimpleQueryReader(this, "AskBroadersOrNarrowers.rq").get());
		} catch (Exception e) {
			throw new SkosPlayModelException(e);
		}
	}
	
	public boolean isMultilingual()
	throws SkosPlayModelException {
		try(RepositoryConnection connection = repository.getConnection()) {
			return Perform.on(connection).ask(new SimpleQueryReader(this, "AskTranslatedConcepts.rq").get());
		} catch (Exception e) {
			throw new SkosPlayModelException(e);
		}
	}
	
	public int getConceptCount()
	throws SkosPlayModelException {
		try(RepositoryConnection connection = repository.getConnection()) {
			return Perform.on(connection).count(new SimpleQueryReader(this, "CountConcepts.rq").get());
		} catch (Exception e) {
			throw new SkosPlayModelException(e);
		}
	}
	
	public Map<String, String> getLanguages(final String locale)
	throws SkosPlayModelException {
		final HashMap<String, String> result = new HashMap<String, String>();
		
		try(RepositoryConnection connection = repository.getConnection()) {
			// retrieve list of declared languages in the data
			Perform.on(connection).select(
					new SimpleQueryReader(this, "ListOfSkosLanguages.rq").get(),
					new AbstractTupleQueryResultHandler() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							String rdfLanguage = bindingSet.getValue("language").stringValue();
							Language l = Languages.getInstance().withIso639P1(rdfLanguage);
							String languageName = (l != null)?l.displayIn(locale):rdfLanguage;
							result.put(
									bindingSet.getValue("language").stringValue(),
									languageName									
							);
						}
						
					}
			);
		} catch (Exception e) {
			throw new SkosPlayModelException(e);
		}
		
		return result;
	}
	
	public Map<LabeledResource, Integer> getConceptCountByConceptScheme(final LabelReader labelReader)
	throws SkosPlayModelException {
		
		final Map<LabeledResource, Integer> conceptCountByConceptSchemes = new TreeMap<LabeledResource, Integer>();
		
		try(RepositoryConnection connection = repository.getConnection()) {
			// retrieve number of concepts per concept schemes
			Perform.on(connection).select(
					new SimpleQueryReader(this, "ConceptCountByConceptSchemes.rq").get(),
					new AbstractTupleQueryResultHandler() {

						@Override
						public void handleSolution(BindingSet bindingSet)
						throws TupleQueryResultHandlerException {
							if(bindingSet.getValue("scheme") != null) {
								try {
									conceptCountByConceptSchemes.put(
											new LabeledResource(
													java.net.URI.create(bindingSet.getValue("scheme").stringValue()),
													LabelReader.display(labelReader.getValues((IRI)bindingSet.getValue("scheme")))
											),
											(bindingSet.getValue("conceptCount") != null)?
													((Literal)bindingSet.getValue("conceptCount")).intValue()
													:0
									);
								} catch (Exception e) {
									throw new TupleQueryResultHandlerException(e);
								}
							}
						}						
					}
			);
		} catch (Exception e) {
			throw new SkosPlayModelException(e);
		}
		
		return conceptCountByConceptSchemes;
	}

	private RepositoryBuilder createRepositoryBuilder(boolean rdfsInference) {
		RepositoryBuilder localRepositoryBuilder;
		
		if(rdfsInference) {
			localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositorySupplier(FactoryConfiguration.RDFS_AWARE));			
			// load the SKOS model to be able to infer skos:inScheme from skos:isTopConceptOf
			localRepositoryBuilder.addOperation(new LoadFromFileOrDirectory("skos.rdf"));
		} else {
			localRepositoryBuilder = new RepositoryBuilder();
		}
		
		return localRepositoryBuilder;
	}
	
	
	
}
