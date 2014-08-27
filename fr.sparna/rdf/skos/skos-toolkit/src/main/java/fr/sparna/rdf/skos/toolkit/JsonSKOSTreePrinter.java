package fr.sparna.rdf.skos.toolkit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Value;
import org.openrdf.repository.Repository;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.commons.tree.GenericTreeVisitorException;
import fr.sparna.commons.tree.GenericTreeVisitorIfc;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlUpdate;
import fr.sparna.rdf.sesame.toolkit.query.builder.SparqlQueryBuilderList;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.operation.ApplyUpdates;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryWriter;

public class JsonSKOSTreePrinter {

	private LabelReader labelReader;
	private boolean prettyPrinting = false;

	public JsonSKOSTreePrinter(LabelReader labelReader) {
		super();
		this.labelReader = labelReader;
	}
	
	public String printToString(GenericTree<SKOSTreeNode> tree) 
	throws SparqlPerformException, IOException, JsonGenerationException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		print(tree, baos);
		return baos.toString("UTF-8");
	}
	
	public void print(GenericTree<SKOSTreeNode> tree, OutputStream out) 
	throws SparqlPerformException, IOException, JsonGenerationException {
		JsonFactory jsonF = new JsonFactory();
		// let's write to the stream, using UTF-8 encoding (only sensible one)
		JsonGenerator jg = jsonF.createGenerator(out, JsonEncoding.UTF8);
		if(this.prettyPrinting) {
			// enable indentation to make debug/testing easier
			jg.useDefaultPrettyPrinter(); 
		}
		
		Map<URI, List<Value>> labels = new HashMap<URI, List<Value>>();
		
		try {
			URIHarvester harvester = new URIHarvester();
			tree.visit(harvester);
			labels = this.labelReader.getValues(harvester.uris);
		} catch (GenericTreeVisitorException e) {
			e.printStackTrace();
		}
		
		printConceptRec(tree.getRoot(), jg, labels);
		jg.close();
	}
	
	private void printConceptRec(GenericTreeNode<SKOSTreeNode> aNode, final JsonGenerator jg, Map<URI, List<Value	>> labels) 
	throws SparqlPerformException, JsonGenerationException, IOException {
		
		jg.writeStartObject();
		// write URI
		jg.writeStringField("uri", aNode.getData().uri.toString());
		
		// write name
		if(labelReader != null) {
			String label = LabelReader.display(labels.get(aNode.getData().getUri()));
			jg.writeStringField("name", label);
		}
		
		// write children
		if(aNode.getChildren() != null && aNode.getChildren().size() > 0) {
			jg.writeArrayFieldStart("children");
			for (GenericTreeNode<SKOSTreeNode> aChild : aNode.getChildren()) {
				printConceptRec(aChild, jg, labels);
			}
			jg.writeEndArray();
		}
		
//		else {
//			// no children, set a size attribute of 1
//			jg.writeNumberField("size", 1);
//			// replace with this line to have a random size computed
//			// jg.writeNumberField("size", Math.ceil(30*Math.random()));
//		}
		
		// write the size
		jg.writeNumberField("size", aNode.getData().getWeight());
		
		jg.writeEndObject();	
	}
	
	class URIHarvester implements GenericTreeVisitorIfc<SKOSTreeNode> {

		List<java.net.URI> uris = new ArrayList<java.net.URI>();
		
		@Override
		public boolean visit(GenericTreeNode<SKOSTreeNode> node)
		throws GenericTreeVisitorException {
			this.uris.add(node.getData().uri);
			return true;
		}
		
	}

	public boolean isPrettyPrinting() {
		return prettyPrinting;
	}

	public void setPrettyPrinting(boolean prettyPrinting) {
		this.prettyPrinting = prettyPrinting;
	}

	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_scheme a skos:ConceptScheme ; skos:hasTopConcept test:_1 ."+"\n" +
				"test:_1 a skos:Concept ; skos:prefLabel \"1\"@fr ." +
				"test:_2 a skos:Concept ; skos:prefLabel \"a\"@fr; skos:broader test:_1 ." +
				"test:_3 a skos:Concept ; skos:prefLabel \"B\"@fr; skos:broader test:_1 ."
		);
		
		ApplyUpdates au = new ApplyUpdates(SparqlUpdate.fromUpdateList(SparqlQueryBuilderList.fromClasspathDirectory("rules/inference-lite")));
		au.execute(r);
		
		RepositoryWriter.writeToFile("output.ttl", r);
		
		SimpleSKOSTreePrinter printer = new SimpleSKOSTreePrinter(r, "fr");
		System.out.println(printer.printTree());
		
//		SKOSTreeBuilder builder = new SKOSTreeBuilder(r, "fr");
//		List<GenericTree<SKOSTreeNode>> trees = builder.buildTrees();
		
//		JsonSKOSTreePrinter jsonPrinter = new JsonSKOSTreePrinter(new LabelReader(r, "fr"));
//		System.out.println(jsonPrinter.printToString(trees.get(0)));
		
//		Perform.on(r).select(new SelectSparqlHelper(
//				"SELECT ?uri ?label WHERE { ?uri <"+SKOS.PREF_LABEL+"> ?label . } VALUES ?uri { <http://www.test.fr/skos/_1> <http://www.test.fr/skos/_2> }",
//				new DebugHandler()
//		));
	}
}
