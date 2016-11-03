package fr.sparna.rdf.sesame.jena.repository;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerBase;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.base.RepositoryBase;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerBase;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * A Sesame repository wrapping a Jena Model.
 * 
 * @author Thomas Francart
 *
 */
public class JenaRepository extends RepositoryBase implements Repository {

	protected Model model;
	protected ValueFactory valueFactory;
	
	public JenaRepository(Model model) {
		super();
		this.model = model;
		this.valueFactory = new ValueFactoryImpl();
	}

	/**
	 * Returns a {@link JenaRepositoryConnection}
	 */
	@Override
	public RepositoryConnection getConnection() throws RepositoryException {
		return new JenaRepositoryConnection(this);
	}

	/**
	 * Returns a basic ValueFactoryImpl
	 */
	@Override
	public ValueFactory getValueFactory() {
		return valueFactory;
	}

	/**
	 * @return false
	 */
	@Override
	public boolean isWritable() throws RepositoryException {
		// for the moment we are returning false
		return false;
	}

	/**
	 * Calling this method on a JenaRepository has no effect and returns null
	 * @return null
	 */
	@Override
	public File getDataDir() {
		// nothing
		return null;
	}
	
	/**
	 * Calling this method on a JenaRepository has no effect
	 */
	@Override
	public void setDataDir(File arg0) {
		// nothing
	}

	/**
	 * Calling this method on a JenaRepository has no effect
	 */
	@Override
	protected void initializeInternal() throws RepositoryException {
		// nothing
	}

	/**
	 * Call close() on the wrapped Model
	 */
	@Override
	protected void shutDownInternal() throws RepositoryException {
		this.model.close();
	}
	
	
	public static void main(String... args) throws Exception {
		Model model = ModelFactory.createDefaultModel();
		model.read(new FileInputStream(args[0]), RDF.NAMESPACE, Rio.getParserFormatForFileName(args[0]).orElse(RDFFormat.RDFXML).getName());
		JenaRepository repository = new JenaRepository(model);
		repository.initialize();
		repository.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, "SELECT DISTINCT ?type WHERE { ?x a ?type }").evaluate(new TupleQueryResultHandlerBase() {

			@Override
			public void handleSolution(BindingSet bindingSet)
			throws TupleQueryResultHandlerException {
				for (String aBindingName : bindingSet.getBindingNames()) {
					System.out.println(aBindingName+" : "+bindingSet.getValue(aBindingName));
				}
			}
			
		});
		
		repository.getConnection().prepareGraphQuery(QueryLanguage.SPARQL, "CONSTRUCT {?x a ?type} WHERE { ?x a ?type }").evaluate(new RDFHandlerBase() {

			@Override
			public void handleStatement(Statement st)
			throws RDFHandlerException {
				System.out.println(st);
			}
			
		});
		System.out.println("**********");
		RepositoryResult<Statement> st = repository.getConnection().getStatements(null, RDF.TYPE, null, true);
		while(st.hasNext()) {
			System.out.println(st.next());
		}
	}
}
