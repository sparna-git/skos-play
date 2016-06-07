package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Level;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.Node;
import fr.sparna.rdf.skos.printer.schema.NodeData;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.printer.schema.Tree;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.SKOSNodeSortCriteriaPreferredPropertyReader;
import fr.sparna.rdf.skos.toolkit.SKOSNodeTypeReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeBuilder;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode;

public class HierarchicalDisplayGenerator extends AbstractKosDisplayGenerator {
	
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
	
	protected ConceptBlockReader cbReader;
	
	public HierarchicalDisplayGenerator(Repository r, ConceptBlockReader cbReader, String displayId) {
		super(r, displayId);
		this.cbReader = cbReader;
	}	
	
	public HierarchicalDisplayGenerator(Repository r, ConceptBlockReader cbReader) {
		super(r);
		this.cbReader = cbReader;
	}

	@Override
	public KosDisplay doGenerate(final String lang, final URI conceptScheme) 
	throws SparqlPerformException {
		log.debug("Reading hierarchical structure in '"+lang+"' for conceptScheme '"+conceptScheme+"'...");
		
		// init ConceptBlockReader
		this.cbReader.initInternal(lang, conceptScheme, this.displayId);
		
		// read types - this could be preloaded
		PropertyReader typeReader = new PropertyReader(repository, URI.create(RDF.TYPE.stringValue()));
		typeReader.setPreLoad(false);
		SKOSNodeTypeReader nodeTypeReader = new SKOSNodeTypeReader(typeReader, this.repository);
		
		// init the tree builder
		// First sort on the notation, then the prefLabel if notation is not available
		PreferredPropertyReader ppr = new PreferredPropertyReader(
				repository,
				Arrays.asList(new URI[] { URI.create(SKOS.NOTATION), URI.create(SKOS.PREF_LABEL) }),
				lang
		);
		ppr.setCaching(true);
		SKOSTreeBuilder treeBuilder = new SKOSTreeBuilder(repository, new SKOSNodeSortCriteriaPreferredPropertyReader(ppr), nodeTypeReader);
		treeBuilder.setUseConceptSchemesAsFirstLevelNodes(false);
		
		// build our display
		final KosDisplay d = new KosDisplay();
		List<GenericTree<SKOSTreeNode>> skosTrees;
		if(conceptScheme != null) {
			log.debug("Concept Scheme is not null, will read the tree for it.");
			skosTrees = treeBuilder.buildTrees(conceptScheme);
			log.debug("Finish reading "+skosTrees.size()+" trees");
		} else {
			log.debug("Concept Scheme is null, will read all the trees.");
			skosTrees = treeBuilder.buildTrees();
			log.debug("Finish reading "+skosTrees.size()+" trees");
		}
		
		PropertyReader notationReader = new PropertyReader(
				this.repository,
				URI.create(SKOS.NOTATION)
		);
		notationReader.setPreLoad(false);
		
		for (GenericTree<SKOSTreeNode> genericTree : skosTrees) {
			Section s = new Section();
			// sets the name of the root node as section title
			String title = LabelReader.display(this.cbReader.getPrefLabelReader().read(genericTree.getRoot().getData().getUri()));
			
			// prepend notation
			List<Value> notations = notationReader.read(genericTree.getRoot().getData().getUri());
			String aNotation = (notations.size() > 0)?notations.get(0).stringValue():null;
			title = ((aNotation != null)?aNotation+" ":"")+title;
			
			s.setTitle(title);				
			
			Tree t = new Tree();
			s.setTree(t);
			t.setNode(buildNodeRec(genericTree.getRoot()));
			d.getSection().add(s);
		}
		
		// ask for 2 columns
		d.setColumnCount(BigInteger.valueOf(2));
		
		return d;
	}
	
	private Node buildNodeRec(GenericTreeNode<SKOSTreeNode> treeNode) 
	throws SparqlPerformException {
		log.debug("Creating entry for "+treeNode.getData().getUri().toString()+"...");
		
		// create node and conceptBlock
		Node n = new Node();		
		NodeData nd = new NodeData();
		n.setNodeData(nd);
		
		ConceptBlock cb = this.cbReader.readConceptBlock(
				treeNode.getData().getUri().toString(),
				false,
				// attempt to prepend a notation if node type is collection
				(treeNode.getData().getNodeType() == SKOSTreeNode.NodeType.COLLECTION)
		);
		nd.setConceptBlock(cb);
		
		// recurse on children
		if(treeNode.getChildren() != null) {
			for (GenericTreeNode<SKOSTreeNode> aChild : treeNode.getChildren()) {
				n.getNode().add(buildNodeRec(aChild));
			}
		}
		
		return n;
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
		
		final String LANG = "fr";
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build display result
		KosDocument document = new KosDocument();
		
		// build and set header
		HeaderAndFooterReader headerReader = new HeaderAndFooterReader(r);
		headerReader.setApplicationString("Generated by SKOS Play!");
		KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?URI.create(args[1]):null);
		document.setHeader(header);
		document.setFooter(headerReader.readFooter(LANG, (args.length > 1)?URI.create(args[1]):null));
		
		ConceptBlockReader cbReader = new ConceptBlockReader(r);
		cbReader.setSkosPropertiesToRead(EXPANDED_SKOS_PROPERTIES);
		HierarchicalDisplayGenerator reader = new HierarchicalDisplayGenerator(r, cbReader);
		BodyReader bodyReader = new BodyReader(reader);
		document.setBody(bodyReader.readBody(LANG, (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(document, new File("src/main/resources/hierarchical-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.printToHtml(document, new File("display-test.html"), LANG);
		printer.printToPdf(document, new File("display-test.pdf"), LANG);
	}
	
}
