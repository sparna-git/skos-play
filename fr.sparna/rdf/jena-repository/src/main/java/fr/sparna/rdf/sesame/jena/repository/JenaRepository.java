package fr.sparna.rdf.sesame.jena.repository;

import java.io.File;
import java.io.FileInputStream;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.base.RepositoryBase;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

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
		model.read(new FileInputStream(args[0]), RDF.NAMESPACE, RDFFormat.forFileName(args[0]).getName());
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
