package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.rdf4j.toolkit.reader.PropertyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.TypeReader;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.rdf4j.toolkit.util.PreferredPropertyReader;
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
	
	public HierarchicalDisplayGenerator(RepositoryConnection connection, ConceptBlockReader cbReader, String displayId) {
		super(connection, displayId);
		this.cbReader = cbReader;
	}	
	
	public HierarchicalDisplayGenerator(RepositoryConnection connection, ConceptBlockReader cbReader) {
		super(connection);
		this.cbReader = cbReader;
	}

	@Override
	public KosDisplay doGenerate(final String lang, final IRI conceptScheme) {
		log.debug("Reading hierarchical structure in '"+lang+"' for conceptScheme '"+conceptScheme+"'...");
		
		// build our display
		final KosDisplay d = new KosDisplay();
			
		// init ConceptBlockReader
		this.cbReader.initInternal(lang, conceptScheme, this.displayId);
		
		// read types - this could be preloaded
		TypeReader typeReader = new TypeReader();
		typeReader.setPreLoad(false);
		SKOSNodeTypeReader nodeTypeReader = new SKOSNodeTypeReader(typeReader, connection);
		
		// init the tree builder
		// First sort on the notation, then the prefLabel if notation is not available
		PreferredPropertyReader ppr = new PreferredPropertyReader(
				connection,
				Arrays.asList(new IRI[] { SimpleValueFactory.getInstance().createIRI(SKOS.NOTATION), SimpleValueFactory.getInstance().createIRI(SKOS.PREF_LABEL) }),
				lang
		);
		ppr.setCaching(true);
		SKOSTreeBuilder treeBuilder = new SKOSTreeBuilder(connection, new SKOSNodeSortCriteriaPreferredPropertyReader(ppr), nodeTypeReader);
		treeBuilder.setUseConceptSchemesAsFirstLevelNodes(false);
		
		
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
		
		PropertyValueReader notationReader = new PropertyValueReader(SimpleValueFactory.getInstance().createIRI(SKOS.NOTATION));
		notationReader.setPreLoad(false);
		
		if(skosTrees.stream().allMatch(t -> t.getNumberOfNodes() == 1)) {
			log.debug("Flat list of trees - no hierarchy - not outputting anything.");
			return d;
		}
		
		for (GenericTree<SKOSTreeNode> genericTree : skosTrees) {
			Section s = new Section();
			// sets the name of the root node as section title
			String title = LabelReader.display(this.cbReader.getPrefLabelReader().read(genericTree.getRoot().getData().getIri(), connection));
			
			// prepend notation
			List<Value> notations = notationReader.read(genericTree.getRoot().getData().getIri(), connection);
			String aNotation = (notations.size() > 0)?notations.get(0).stringValue():null;
			title = ((aNotation != null)?aNotation+" ":"")+title;
			
			s.setTitle(title);				
			
			Tree t = new Tree();
			s.setTree(t);
			t.setNode(buildNodeRec(genericTree.getRoot(), connection));
			d.getSection().add(s);
		}
		
		// ask for 2 columns
		d.setColumnCount(BigInteger.valueOf(2));
		
		return d;
	}
	
	private Node buildNodeRec(GenericTreeNode<SKOSTreeNode> treeNode, RepositoryConnection connection) {
		log.debug("Creating entry for "+treeNode.getData().getIri().toString()+"...");
		
		// create node and conceptBlock
		Node n = new Node();		
		NodeData nd = new NodeData();
		n.setNodeData(nd);
		
		ConceptBlock cb = this.cbReader.readConceptBlock(
				connection,
				treeNode.getData().getIri().toString(),
				false,
				// attempt to prepend a notation if node type is collection
				(treeNode.getData().getNodeType() == SKOSTreeNode.NodeType.COLLECTION)
		);
		nd.setConceptBlock(cb);
		
		// recurse on children
		if(treeNode.getChildren() != null) {
			for (GenericTreeNode<SKOSTreeNode> aChild : treeNode.getChildren()) {
				n.getNode().add(buildNodeRec(aChild, connection));
			}
		}
		
		return n;
	}

	public static void main(String... args) throws Exception {
		// BasicConfigurator.configure();

		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(ch.qos.logback.classic.Level.INFO);
	    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("fr.sparna.rdf")).setLevel(ch.qos.logback.classic.Level.TRACE);
	    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("httpclient.wire")).setLevel(ch.qos.logback.classic.Level.DEBUG);
	    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("org.apache.commons.httpclient")).setLevel(ch.qos.logback.classic.Level.DEBUG);
		
		// final String LANG = "fr";
		final String LANG = null;
		
		Repository r = RepositoryBuilderFactory.fromString(args[0]).get();
		try(RepositoryConnection connection = r.getConnection()) {
			
			// build display result
			KosDocument document = new KosDocument();
			
			// build and set header
			HeaderAndFooterReader headerReader = new HeaderAndFooterReader(connection);
			headerReader.setApplicationString("Generated by SKOS Play!");
			KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null);
			document.setHeader(header);
			document.setFooter(headerReader.readFooter(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null));
			
			ConceptBlockReader cbReader = new ConceptBlockReader();
			cbReader.setSkosPropertiesToRead(EXPANDED_SKOS_PROPERTIES);
			HierarchicalDisplayGenerator reader = new HierarchicalDisplayGenerator(connection, cbReader);
			BodyReader bodyReader = new BodyReader(reader);
			document.setBody(bodyReader.readBody(LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null));
	
			Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
			m.setProperty("jaxb.formatted.output", true);
			// m.marshal(display, System.out);
			m.marshal(document, new File("src/main/resources/hierarchical-output-test.xml"));
			
			DisplayPrinter printer = new DisplayPrinter();
			printer.printToHtml(document, new File("display-test.html"), LANG);
			printer.printToPdf(document, new File("display-test.pdf"), LANG);
		}
	}
	
}
