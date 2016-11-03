package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.net.URL;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerBase;

import fr.sparna.rdf.sesame.toolkit.query.ConstructSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

/**
 * Deletes all unknown (non XMLSchema) datatypes from the repository.
 * 
 * @author Thomas Francart
 *
 */
public class CleanDatatypes implements RepositoryOperationIfc {

	@Override
	public void execute(final Repository repository)
			throws RepositoryOperationException {
		try {
			final RepositoryConnection connection = repository.getConnection();
			Perform.on(repository).construct(
					new ConstructSparqlHelper(
							"CONSTRUCT { ?s ?p ?o } " +
									" WHERE {" +
									"   ?s ?p ?o . " +
									"   FILTER(" +
									"		isLiteral(?o)" +
									"		&&" +
									"		datatype(?o) != <http://www.w3.org/2001/XMLSchema#string>" +
									"		&&" +
									"		datatype(?o) != ''" +
									"	)" +
									" }",
									new RDFHandlerBase() {
								@Override
								public void handleStatement(Statement st)
										throws RDFHandlerException {
									System.out.println(st);
									URI datatype = ((Literal)st.getObject()).getDatatype();
									if(
											datatype != null &&
											!datatype.equals(XMLSchema.STRING) &&
											!datatype.equals(XMLSchema.BOOLEAN) &&
											!datatype.equals(XMLSchema.DECIMAL) &&
											!datatype.equals(XMLSchema.INTEGER) &&
											!datatype.equals(XMLSchema.INT) &&
											!datatype.equals(XMLSchema.FLOAT) &&
											!datatype.equals(XMLSchema.DOUBLE) &&
											!datatype.equals(XMLSchema.DATETIME) &&
											!datatype.equals(XMLSchema.DATE) &&
											!datatype.equals(XMLSchema.GMONTHDAY) &&
											!datatype.equals(XMLSchema.ANYURI)
											) {
										System.out.println("Unknown datatype : "+datatype);
										try {
											connection.remove(st);
											connection.add(repository.getValueFactory().createStatement(
													st.getSubject(),
													st.getPredicate(),
													repository.getValueFactory().createLiteral(((Literal)st.getObject()).getLabel())
													)
													);
										} catch (RepositoryException e) {
											throw new RDFHandlerException(e);
										}
									}
								}

							}
							)
					);
			connection.close();
		} catch (Exception e) {
			throw new RepositoryOperationException(e);
		}
	}
	
	public static void main(String... args) throws Exception {
		RepositoryBuilder builder = new RepositoryBuilder(new LocalMemoryRepositoryFactory());
		builder.addOperation(new LoadFromUrl(new URL("http://dbpedia.org/resource/Paris")));
		builder.addOperation(new CleanDatatypes());
		Repository r = builder.createNewRepository();
	}


}
