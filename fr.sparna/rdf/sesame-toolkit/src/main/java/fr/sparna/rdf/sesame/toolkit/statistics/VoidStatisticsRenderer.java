package fr.sparna.rdf.sesame.toolkit.statistics;

import java.io.FileOutputStream;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.BNode;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;

import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;


public class VoidStatisticsRenderer implements StatisticsRenderer {

	// the output
	protected Repository output;
	protected URI datasetURI;
	
	
	public VoidStatisticsRenderer(Repository output, URI datasetURI) {
		this.output = output;
		this.datasetURI = datasetURI;
	}
	
	@Override
	public void render(StatisticsHandler handler) {
		try {
			// get connection and value factory
			RepositoryConnection connection = this.output.getConnection();
			ValueFactory vf = this.output.getValueFactory();
			
			// declare Dataset
			connection.add(vf.createStatement(
					vf.createURI(this.datasetURI.toString()),
					RDF.TYPE,
					vf.createURI(VOID.DATASET))
			);
			
			// predicates by type
			for (Map.Entry<URI, Integer> anEntry : handler.getNumberOfTripleByPredicate().entrySet()) {
				// create partition BNode
				BNode propertyPartitionBNode = vf.createBNode();
				
				// attach partition to dataset
				connection.add(vf.createStatement(
						vf.createURI(this.datasetURI.toString()),
						vf.createURI(VOID.PROPERTY_PARTITION),
						propertyPartitionBNode)
				);
				
				// say this partition is about this property
				connection.add(vf.createStatement(
						propertyPartitionBNode,
						vf.createURI(VOID.PROPERTY),
						vf.createURI(anEntry.getKey().toString()))
				);
				
				// tell the number of triples
				connection.add(vf.createStatement(
						propertyPartitionBNode,
						vf.createURI(VOID.TRIPLES),
						vf.createLiteral(anEntry.getValue()))
				);
			}

		} catch (Exception e) {
			// TODO : handle exceptions
			e.printStackTrace();
		}
		
		
		// predicates by type
		
		
//		// print predicates by type
//				out.println("++++++++++ Triple breakdown by predicate ++++++++++");
//				TreeMap<URI,Integer> sortedPredicates = new TreeMap<URI,Integer>(
//						new StatisticsMapComparator(handler.getNumberOfTripleByTypes(), useCountSort)
//				);
//				sortedPredicates.putAll(handler.getNumberOfTripleByTypes());
//
//				for (URI aURI : sortedPredicates.keySet()) {
//					out.println("  "+aURI+"\t"+sortedPredicates.get(aURI));
//				}
//				out.println();
//
//				// print breakdown by namespace
//				out.println("++++++++++ Triple breakdown by namespaces ++++++++++");
//				TreeMap<String,Integer> sortedNamespaces = new TreeMap<String,Integer>(
//						new StatisticsMapComparator(handler.getNumberOfTripleByNamespace(), useCountSort)
//				);
//				sortedNamespaces.putAll(handler.getNumberOfTripleByNamespace());
//				for (String aNamespace : sortedNamespaces.keySet()) {
//					out.println("  "+aNamespace+"\t"+sortedNamespaces.get(aNamespace));
//				}
//				out.println();
//
//				// print number of instances per class
//				out.println("++++++++++ Instances per class ++++++++++");
//				TreeMap<URI,Integer> sortedClasses = new TreeMap<URI,Integer>(
//						new StatisticsMapComparator(handler.getNumberOfInstancesByClass(), useCountSort)
//				);
//				sortedClasses.putAll(handler.getNumberOfInstancesByClass());
//				for (URI aURI : sortedClasses.keySet()) {
//					out.println("  "+aURI+"\t"+sortedClasses.get(aURI));
//				}
//				out.println();
//
//				// print predicates by physical types
//				out.println("++++++++++ Triple breakdown by object / datatypeProperties ++++++++++");
//				out.println("  Object properties : "+handler.getNumberOfObjectTriples());
//				out.println("  Datatype properties : "+handler.getNumberOfDatatypeTriples());
//				out.println();
//
//				// print total number of triples and subjects
//				out.println("++++++++++ Totals ++++++++++");
//				out.println("  Total number of URIs (subject of triples) : "+handler.getNumberOfSubjectURI());
//				out.println("  Total number of blank nodes (subject of triples) : "+handler.getNumberOfSubjectBlankNodes());
//				out.println("  Total number of triples : "+handler.getNumberOfTriples());

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Repository r = RepositoryBuilder.fromString(args[0]);
		StatisticsHandler handler = new StatisticsHandler();
		r.getConnection().export(handler);
		Repository output = new LocalMemoryRepositoryFactory().createNewRepository();
		VoidStatisticsRenderer renderer = new VoidStatisticsRenderer(
				output,
				URI.create("http://rdf.insee.org/COG")
		);
		
		renderer.render(handler);
		
		RepositoryWriter.writeToFile(args[1], output);
	}

}
