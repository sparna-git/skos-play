package fr.sparna.rdf.sesame.toolkit.repository;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.base.RepositoryConnectionWrapper;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

public class ContextCachingRepositoryConnection extends RepositoryConnectionWrapper {

	protected Set<Resource> contextsInTransaction;
	
	public ContextCachingRepositoryConnection(
			Repository repository,
			RepositoryConnection delegate
	) {
		super(repository, delegate);
		this.contextsInTransaction = new HashSet<Resource>();
	}

	public ContextCachingRepositoryConnection(Repository repository) {
		super(repository);
	}

	@Override
	protected boolean isDelegatingAdd() throws RepositoryException {
		return false;
	}

	@Override
	protected boolean isDelegatingRead() throws RepositoryException {
		return false;
	}

	@Override
	protected boolean isDelegatingRemove() throws RepositoryException {
		return false;
	}

	@Override
	public void commit() throws RepositoryException {
		super.commit();
		this.contextsInTransaction = new HashSet<Resource>();
	}

	@Override
	public void rollback() throws RepositoryException {
		super.rollback();
		this.contextsInTransaction = new HashSet<Resource>();
	}

	@Override
	public void add(
			URL url,
			String baseURI,
			RDFFormat dataFormat,
			Resource... contexts
	) throws IOException, RDFParseException, RepositoryException {
		if(contexts == null) {
			// aucun contexte, on ajoute normalement
			super.add(url, baseURI, dataFormat, contexts);
		} else {
			for (Resource aContext : contexts) {
				if(!contextInRepository(aContext) || this.contextsInTransaction.contains(aContext)) {
					super.add(url, baseURI, dataFormat, aContext);
				}
			}
		}
	}

	@Override
	protected void addWithoutCommit(
			Resource subject,
			IRI predicate,
			Value object,
			Resource... contexts
	) throws RepositoryException {
		if(contexts == null) {
			// si aucun contexte, on ajoute normalement
			super.addWithoutCommit(subject, predicate, object);
		} else {
			// sinon, pour chaque contexte
			for (Resource aContext : contexts) {
				// si on ne le connait pas dans le repository ou bien s'il est connu mais que c'est dans la meme transaction
				if(!contextInRepository(aContext) || this.contextsInTransaction.contains(aContext)) {
					System.out.println("YES, will add statement "+subject+" "+predicate+" "+object+" in ctx "+aContext);
					// alors on l'ajoute dans le contexte
					super.addWithoutCommit(subject, predicate, object, aContext);
					// et on garde trace qu'on est en train de traiter ce contexte dans cette transaction
					this.contextsInTransaction.add(aContext);
				} else {
					// sinon on n'ajoute pas le statement, le contexte a deja ete traite
					System.out.println("NO, will not add statement "+subject+" "+predicate+" "+object+" cause "+aContext+" is already here");
				}
			}
		}
	}

	protected boolean contextInRepository(Resource context) throws RepositoryException {
		RepositoryResult<Resource> rr = this.getContextIDs();
		while(rr.hasNext()) {
			Resource aContext = rr.next();
			if(aContext.equals(context)) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String... args) throws Exception {
		final String TEST_URL = "http://dbpedia.org/resource/Marseille";
		
		Repository r = new LocalMemoryRepositoryFactory().createNewRepository();
		ContextCachingRepositoryConnection cc = new ContextCachingRepositoryConnection(r, r.getConnection());
		cc.setAutoCommit(false);
		cc.add(new URL(TEST_URL), RDF.NAMESPACE, Rio.getParserFormatForFileName(TEST_URL).orElse(RDFFormat.RDFXML), r.getValueFactory().createIRI(TEST_URL));
		cc.commit();
		cc.add(new URL(TEST_URL), RDF.NAMESPACE, Rio.getParserFormatForFileName(TEST_URL).orElse(RDFFormat.RDFXML), r.getValueFactory().createIRI(TEST_URL));
	}
		
}
