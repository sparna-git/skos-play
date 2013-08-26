package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Level;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.ConceptBlock;
import fr.sparna.rdf.skos.printer.schema.Display;
import fr.sparna.rdf.skos.printer.schema.DisplayBody;
import fr.sparna.rdf.skos.printer.schema.DisplayHeader;
import fr.sparna.rdf.skos.printer.schema.Node;
import fr.sparna.rdf.skos.printer.schema.NodeData;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.printer.schema.Tree;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.SKOSNodeTypeReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeBuilder;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode;

public class HierarchicalBodyReader extends AbstractBodyReader {
	
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
	
	public HierarchicalBodyReader(Repository r, ConceptBlockReader cbReader) {
		super(r);
		this.cbReader = cbReader;
	}

	@Override
	public DisplayBody doRead(final String lang, final URI conceptScheme) 
	throws SPARQLPerformException {
		log.debug("Reading hierarchical structure in '"+lang+"' for conceptScheme '"+conceptScheme+"'...");
		
		// init ConceptBlockReader
		this.cbReader.initInternal(this.tagsBundle, lang, conceptScheme);
		
		// read types - this could be preloaded
		PropertyReader typeReader = new PropertyReader(repository, URI.create(RDF.TYPE.stringValue()));
		typeReader.setPreLoad(false);
		SKOSNodeTypeReader nodeTypeReader = new SKOSNodeTypeReader(typeReader);
		
		// init the tree builder
		// TODO : we use the same same PrefLabelReader as the ConceptBlockReader - dunno if this can cause problems
		SKOSTreeBuilder treeBuilder = new SKOSTreeBuilder(repository, this.cbReader.getPrefLabelReader(), nodeTypeReader);

		// build our display body
		final DisplayBody body = new DisplayBody();
		
		if(conceptScheme != null) {
			log.debug("Concept Scheme is not null, will read the tree under it.");
			GenericTree<SKOSTreeNode> skosTree = treeBuilder.buildTree(conceptScheme);
			Section s = new Section();
			Tree t = new Tree();
			s.setTree(t);
			t.setNode(buildNodeRec(skosTree.getRoot()));
			body.getSection().add(s);
		} else {
			log.debug("Concept Scheme is null, will read all the trees.");
			List<GenericTree<SKOSTreeNode>> skosTrees = treeBuilder.buildTrees();
			log.debug("Finish reading "+skosTrees.size()+" trees");
			for (GenericTree<SKOSTreeNode> genericTree : skosTrees) {
				Section s = new Section();
				Tree t = new Tree();
				s.setTree(t);
				t.setNode(buildNodeRec(genericTree.getRoot()));
				body.getSection().add(s);
			}
		}
		
		return body;
	}
	
	private Node buildNodeRec(GenericTreeNode<SKOSTreeNode> treeNode) 
	throws SPARQLPerformException {
		log.debug("Creating entry for "+treeNode.getData().getUri().toString()+"...");
		
		// create node and conceptBlock
		Node n = new Node();		
		NodeData nd = new NodeData();
		n.setNodeData(nd);
		
		ConceptBlock cb = this.cbReader.readConceptBlock(treeNode.getData().getUri().toString(), false);
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
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build display result
		Display display = new Display();
		
		// build and set header
		HeaderReader headerReader = new HeaderReader(r);
		DisplayHeader header = headerReader.read("fr", (args.length > 1)?URI.create(args[1]):null);
		display.setHeader(header);
		
		HierarchicalBodyReader reader = new HierarchicalBodyReader(r, new ConceptBlockReader(r, EXPANDED_SKOS_PROPERTIES));
		display.setBody(reader.readBody("fr", (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(display, new File("src/main/resources/hierarchical-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.printToHtml(display, new File("display-test.html"));
		printer.printToPdf(display, new File("display-test.pdf"));
	}
	
}
