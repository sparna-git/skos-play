package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.net.URL;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;

public class CleanDatatypesOperation implements RepositoryOperationIfc {

	@Override
	public void execute(final Repository repository)
			throws RepositoryOperationException {
		try {
			final RepositoryConnection connection = repository.getConnection();
			SesameSPARQLExecuter.newExecuter(repository).executeConstruct(
					new ConstructSPARQLHelper(
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
		builder.addOperation(new LoadFromURL(new URL("http://dbpedia.org/resource/Paris")));
		builder.addOperation(new CleanDatatypesOperation());
		Repository r = builder.createNewRepository();
	}


}
