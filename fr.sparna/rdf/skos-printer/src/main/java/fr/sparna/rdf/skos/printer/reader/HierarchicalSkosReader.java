package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.skos.SKOS;
import fr.sparna.rdf.sesame.toolkit.skos.SKOSNodeTypeReader;
import fr.sparna.rdf.sesame.toolkit.skos.SKOSTreeBuilder;
import fr.sparna.rdf.sesame.toolkit.skos.SKOSTreeNode;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.Display;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;
import fr.sparna.rdf.skos.printer.schema.Entry;
import fr.sparna.rdf.skos.printer.schema.Hierarchical;

public class HierarchicalSkosReader {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public static final List<String> EXPANDED_SKOS_PROPERTIES = Arrays.asList(new String[] {
			SKOS.NOTATION,
			SKOS.DEFINITION,
			SKOS.SCOPE_NOTE,
			SKOS.EXAMPLE,
			SKOS.CHANGE_NOTE,			
			SKOS.HISTORY_NOTE,
			SKOS.EDITORIAL_NOTE,
			SKOS.RELATED			
	});		
	
	protected Repository repository;

	protected ResourceBundle tagsBundle;
	
	protected List<String> skosPropertiesToRead;

	private PropertyReader prefLabelReader;
	
	public HierarchicalSkosReader(Repository r) {
		super();
		this.repository = r;
	}

	public List<Hierarchical> read(final String lang, final URI conceptScheme) 
	throws SPARQLPerformException {
		log.debug("Reading hierarchical structure in '"+lang+"' for conceptScheme '"+conceptScheme+"'...");
		
		// init tag resource bundle if not set
		if(this.tagsBundle == null) {
			tagsBundle = ResourceBundle.getBundle(
					"fr.sparna.rdf.skos.display.Tags",
					new Locale(lang),
					new fr.sparna.i18n.StrictResourceBundleControl()
			);
		}

		final List<PropertyReader> additionalReaders = new ArrayList<PropertyReader>();
		
		if(this.skosPropertiesToRead != null) {
			for (String aProperty : this.skosPropertiesToRead) {
				log.debug("Will read additional property '"+aProperty+"'");
				additionalReaders.add(
						(conceptScheme != null)
						?new PropertyReader(this.repository, URI.create(aProperty), (SKOS.isDatatypeProperty(aProperty))?lang:null, URI.create(SKOS.IN_SCHEME), URI.create(conceptScheme.toString()))
						:new PropertyReader(this.repository, URI.create(aProperty), (SKOS.isDatatypeProperty(aProperty))?lang:null, null, null)
				);
			}
		}
		
		// no concept scheme filtering here - we want to be able to read prefLabel independently from the conceptScheme
		prefLabelReader = new PropertyReader(
				this.repository,
				URI.create(SKOS.PREF_LABEL),
				// language of property to read
				lang,
				// additional criteria predicate
				null,
				// additional criteria object
				null
		);
		// if we need to disable preload
		prefLabelReader.setPreLoad(false);
		
		// read types and load them in mem
		PropertyReader typeReader = new PropertyReader(repository, URI.create(RDF.TYPE.stringValue()));
		typeReader.setPreLoad(false);
		SKOSNodeTypeReader nodeTypeReader = new SKOSNodeTypeReader(typeReader);
		
		SKOSTreeBuilder treeBuilder = new SKOSTreeBuilder(repository, prefLabelReader, nodeTypeReader);

		// build our hierarchical display
		final List<Hierarchical> hierarchicals = new ArrayList<Hierarchical>();
		
		if(conceptScheme != null) {
			log.debug("Concept Scheme is not null, will read the tree under it.");
			GenericTree<SKOSTreeNode> skosTree = treeBuilder.buildTree(conceptScheme);
			Hierarchical h = new Hierarchical();
			h.getEntry().add(buildEntryRec(skosTree.getRoot(), additionalReaders));
			hierarchicals.add(h);
		} else {
			log.debug("Concept Scheme is null, will read all the trees.");
			List<GenericTree<SKOSTreeNode>> skosTrees = treeBuilder.buildTrees();
			log.debug("Finish reading "+skosTrees.size()+" trees");
			for (GenericTree<SKOSTreeNode> genericTree : skosTrees) {
				Hierarchical h = new Hierarchical();
				h.getEntry().add(buildEntryRec(genericTree.getRoot(), additionalReaders));
				hierarchicals.add(h);
			}
		}
		
		return hierarchicals;
	}
	
	private Entry buildEntryRec(GenericTreeNode<SKOSTreeNode> treeNode, List<PropertyReader> additionalReaders) 
	throws SPARQLPerformException {
		log.debug("Creating entry for "+treeNode.getData().getUri().toString()+"...");
		
		// create entry
		Entry e = new Entry();
		// set concept URI
		e.setConcept(treeNode.getData().getUri().toString());
		// set label (or URI if no label can be found)
		String label = valueListToString(prefLabelReader.read(treeNode.getData().getUri()));
		label = (label.trim().equals(""))?treeNode.getData().getUri().toString():label;
		e.setLabel(SchemaFactory.createLabel(
				label,
				null
		));
		// set entryId
		e.setEntryId(Integer.toString((treeNode.getData().getUri().toString() + label).hashCode()));
		
		// add additional attributes
		for (PropertyReader PropertyReader : additionalReaders) {
			List<Value> values = PropertyReader.read(treeNode.getData().getUri());
			for (Value value : values) {

				if(value instanceof Literal) {
					e.getAttOrRef().add(
							SchemaFactory.createAtt(
									((Literal)value).stringValue(),
									this.tagsBundle.getString(PropertyReader.getPropertyURI().toString().substring(SKOS.NAMESPACE.length())),
									(PropertyReader.getPropertyURI().toString().equals(SKOS.ALT_LABEL))?"alt":null
									)
							);
				} else {
					org.openrdf.model.URI aRef = (org.openrdf.model.URI)value;
					List<Value> prefs = prefLabelReader.read(URI.create(aRef.stringValue()));
					String prefLabel = (prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue();
					String entryRef = Integer.toString((aRef.stringValue()+prefLabel).hashCode());
					e.getAttOrRef().add(
							SchemaFactory.createRef(
									entryRef,
									aRef.stringValue(),
									(prefs.size() > 0)?prefs.get(0).stringValue():aRef.stringValue(),
									this.tagsBundle.getString(PropertyReader.getPropertyURI().toString().substring(SKOS.NAMESPACE.length())),
									"pref"
									)
							);
				}
			}
		}
		
		// recurse on children
		if(treeNode.getChildren() != null) {
			for (GenericTreeNode<SKOSTreeNode> aChild : treeNode.getChildren()) {
				e.getEntry().add(buildEntryRec(aChild, additionalReaders));
			}
		}
		
		return e;
	}
	
//	private void sortEntry(Entry e, final Collator collator) {
//		
//		Collections.sort(e.getEntry(), new Comparator<Entry>() {
//			@Override
//			public int compare(Entry o1, Entry o2) {
//				if(o1 == null && o2 == null) return 0;
//				if(o1 == null) return -1;
//				if(o2 == null) return 1;
//				return collator.compare(o1.getLabel().getStr().getValue(), o2.getLabel().getStr().getValue());
//			}			
//		});
//		
//		for (Entry entry : e.getEntry()) {
//			sortEntry(entry, collator);
//		}
//	}
	
	private String valueListToString(List<Value> values) {
		StringBuffer sb = new StringBuffer();
		if(values != null && values.size() > 0) {
			for (Value aValue : values) {
				sb.append(((Literal)aValue).getLabel()+", ");
			}
			// remove last ", "
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}
	
	public List<String> getSkosPropertiesToRead() {
		return skosPropertiesToRead;
	}

	public void setSkosPropertiesToRead(List<String> skosPropertiesToRead) {
		this.skosPropertiesToRead = skosPropertiesToRead;
	}

	public void setTagsBundle(ResourceBundle tagsBundle) {
		this.tagsBundle = tagsBundle;
	}

	public static void main(String... args) throws Exception {
		// BasicConfigurator.configure();

		// reduce all logs
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
		// except the SPARQL queries
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(Level.TRACE);
		// and if we need to log the request and responses to a remote server...
		org.apache.log4j.Logger.getLogger("httpclient.wire").setLevel(Level.DEBUG);
		org.apache.log4j.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.DEBUG);
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build display result
		Display display = new Display();
		
		// build and set header
		DisplayHeaderSkosReader headerReader = new DisplayHeaderSkosReader(r);
		DisplayHeader header = headerReader.read("fr", (args.length > 1)?URI.create(args[1]):null);
		display.setHeader(header);
		
		HierarchicalSkosReader reader = new HierarchicalSkosReader(r);
		reader.setSkosPropertiesToRead(EXPANDED_SKOS_PROPERTIES);
		display.getAlphabeticalOrHierarchical().addAll(reader.read("fr", (args.length > 1)?URI.create(args[1]):null));

		DisplayPrinter printer = new DisplayPrinter();
		printer.printToHtml(display, new File("display-test.html"));
		printer.printToPdf(display, new File("display-test.pdf"));
	}
	
}
