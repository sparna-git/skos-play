package fr.sparna.rdf.skos.printer.reader;

import java.util.Arrays;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.rdf4j.toolkit.reader.KeyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.PropertyLangValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.PropertyValueReader;
import fr.sparna.rdf.rdf4j.toolkit.reader.TypeReader;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.rdf4j.toolkit.util.PreferredPropertyReader;
import fr.sparna.rdf.skos.printer.freemind.schema.Map;
import fr.sparna.rdf.skos.printer.freemind.schema.Node;
import fr.sparna.rdf.skos.toolkit.SKOS;
import fr.sparna.rdf.skos.toolkit.SKOSNodeSortCriteriaPreferredPropertyReader;
import fr.sparna.rdf.skos.toolkit.SKOSNodeTypeReader;
import fr.sparna.rdf.skos.toolkit.SKOSTreeBuilder;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode;

public class FreemindReader {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private RepositoryConnection connection;
	
	protected KeyValueReader<IRI, Literal> prefLabelReader;

	public FreemindReader(RepositoryConnection connection) {
		this.connection = connection;
		
		
	}	

	public Map generateFreemindMap(String mainLang, final IRI conceptScheme) {

		this.prefLabelReader = new PropertyLangValueReader(
				SimpleValueFactory.getInstance().createIRI(SKOS.PREF_LABEL),
				mainLang
		);
		
		// read types - this could be preloaded
		TypeReader typeReader = new TypeReader();
		typeReader.setPreLoad(false);
		SKOSNodeTypeReader nodeTypeReader = new SKOSNodeTypeReader(typeReader, connection);

		// init the tree builder
		// First sort on the notation, then the prefLabel if notation is not available
		PreferredPropertyReader ppr = new PreferredPropertyReader(
				connection,
				Arrays.asList(new IRI[] { SimpleValueFactory.getInstance().createIRI(SKOS.NOTATION), SimpleValueFactory.getInstance().createIRI(SKOS.PREF_LABEL) }),
				mainLang
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

		Map m = new Map();
		if(skosTrees.size() == 1) {
			m.setNode(treeToFreeMindNode(skosTrees.get(0)));
		} else {
			Node root = new Node("root", "root");
			m.setNode(root);
			for (GenericTree<SKOSTreeNode> genericTree : skosTrees) {
				root.getChildrens().add(treeToFreeMindNode(genericTree));
			}
		}
		
		return m;

	}
	
	private Node treeToFreeMindNode(GenericTree<SKOSTreeNode> tree) {
		GenericTreeNode<SKOSTreeNode> root = tree.getRoot();
		return treeNodeToFreeMindNode(root);
	}
	
	private Node treeNodeToFreeMindNode(GenericTreeNode<SKOSTreeNode> node) {
		SKOSTreeNode treeNode = node.getData();
		String label =  LabelReader.display(prefLabelReader.read(treeNode.getIri(), connection));
		Node n = new Node(treeNode.getIri().toString(), label);
		
		if(node.hasChildren()) {
			for (GenericTreeNode<SKOSTreeNode>  aChild : node.getChildren()) {
				n.getChildrens().add(treeNodeToFreeMindNode(aChild));
			}
		}
		
		return n;
	}

}
