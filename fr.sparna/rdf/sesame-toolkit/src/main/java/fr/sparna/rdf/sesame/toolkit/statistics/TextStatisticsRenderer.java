package fr.sparna.rdf.sesame.toolkit.statistics;

import java.io.PrintStream;
import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Generates a simple text-rendering of the statistics.
 * 
 * @author Thomas Francart
 *
 */
public class TextStatisticsRenderer implements StatisticsRenderer {

	protected PrintStream out;
	protected boolean useCountSort = false;

	public TextStatisticsRenderer(PrintStream out, boolean useCountSort) {
		super();
		this.out = out;
		this.useCountSort = useCountSort;
	}

	@Override
	public void render(StatisticsHandler handler) {
		// print predicates by type
		out.println("++++++++++ Triple breakdown by predicate ++++++++++");
		TreeMap<URI,Integer> sortedPredicates = new TreeMap<URI,Integer>(
				new StatisticsMapComparator(handler.getNumberOfTripleByPredicate(), useCountSort)
		);
		sortedPredicates.putAll(handler.getNumberOfTripleByPredicate());

		for (URI aURI : sortedPredicates.keySet()) {
			out.println("  "+aURI+"\t"+sortedPredicates.get(aURI));
		}
		out.println();

		// print breakdown by namespace
		out.println("++++++++++ Triple breakdown by namespaces ++++++++++");
		TreeMap<String,Integer> sortedNamespaces = new TreeMap<String,Integer>(
				new StatisticsMapComparator(handler.getNumberOfTripleByNamespace(), useCountSort)
		);
		sortedNamespaces.putAll(handler.getNumberOfTripleByNamespace());
		for (String aNamespace : sortedNamespaces.keySet()) {
			out.println("  "+aNamespace+"\t"+sortedNamespaces.get(aNamespace));
		}
		out.println();

		// print number of instances per class
		out.println("++++++++++ Instances per class ++++++++++");
		TreeMap<URI,Integer> sortedClasses = new TreeMap<URI,Integer>(
				new StatisticsMapComparator(handler.getNumberOfInstancesByClass(), useCountSort)
		);
		sortedClasses.putAll(handler.getNumberOfInstancesByClass());
		for (URI aURI : sortedClasses.keySet()) {
			out.println("  "+aURI+"\t"+sortedClasses.get(aURI));
		}
		out.println();

		// print predicates by physical types
		out.println("++++++++++ Triple breakdown by object / datatypeProperties ++++++++++");
		out.println("  Object properties : "+handler.getNumberOfObjectTriples());
		out.println("  Datatype properties : "+handler.getNumberOfDatatypeTriples());
		out.println();

		// print total number of triples and subjects
		out.println("++++++++++ Totals ++++++++++");
		out.println("  Total number of URIs (subject of triples) : "+handler.getNumberOfSubjectURI());
		out.println("  Total number of blank nodes (subject of triples) : "+handler.getNumberOfSubjectBlankNodes());
		out.println("  Total number of triples : "+handler.getNumberOfTriples());
	}

	class StatisticsMapComparator implements Comparator {

		protected Map base;
		protected boolean useCountSort;

		public StatisticsMapComparator(Map base, boolean useCountSort) {
			this.base = base;
			this.useCountSort = useCountSort;
		}

		public int compare(Object a, Object b) {
			if(useCountSort) {
				if((Integer)base.get(a) < (Integer)base.get(b)) {
					return 1;
				} else if(base.get(a) == base.get(b)) {
					return 0;
				} else {
					return -1;
				}
			} else {
				return a.toString().compareTo(b.toString());
			}
		}
	}


}
