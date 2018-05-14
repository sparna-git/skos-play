package fr.sparna.rdf.skos.toolkit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

import fr.sparna.commons.io.ResourceList;
import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.commons.tree.GenericTreeVisitorException;
import fr.sparna.commons.tree.GenericTreeVisitorIfc;
import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReader;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleQueryReaderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.rdf4j.toolkit.util.RepositoryWriter;

public class JsonSKOSTreePrinter {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private LabelReader labelReader;
	private boolean prettyPrinting = false;

	public JsonSKOSTreePrinter(LabelReader labelReader) {
		super();
		this.labelReader = labelReader;
	}
	
	public String printToString(GenericTree<SKOSTreeNode> tree) 
	throws IOException, JsonGenerationException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		print(tree, baos);
		return baos.toString("UTF-8");
	}
	
	public void print(GenericTree<SKOSTreeNode> tree, OutputStream out) 
	throws IOException, JsonGenerationException {
		JsonFactory jsonF = new JsonFactory();
		// let's write to the stream, using UTF-8 encoding (only sensible one)
		JsonGenerator jg = jsonF.createGenerator(out, JsonEncoding.UTF8);
		if(this.prettyPrinting) {
			// enable indentation to make debug/testing easier
			jg.useDefaultPrettyPrinter(); 
		}
		
		Map<IRI, List<Value>> labels = new HashMap<IRI, List<Value>>();
		
		try {
			IRIHarvester harvester = new IRIHarvester();
			tree.visit(harvester);
			log.debug("JsonSKOSTreePrinter : getting labels for "+harvester.iris.size()+" nodes");
			labels = this.labelReader.getValues(harvester.iris);
		} catch (GenericTreeVisitorException e) {
			e.printStackTrace();
		}
		
		printConceptRec(tree.getRoot(), jg, labels);
		jg.close();
	}
	
	private void printConceptRec(GenericTreeNode<SKOSTreeNode> aNode, final JsonGenerator jg, Map<IRI, List<Value>> labels) 
	throws JsonGenerationException, IOException {
		
		jg.writeStartObject();
		// write URI
		jg.writeStringField("uri", aNode.getData().iri.toString());
		
		// write name
		if(labelReader != null) {
			String label = LabelReader.display(labels.get(aNode.getData().getIri()));
			// make sure we have a label
			if(label == null || label.equals("")) {
				// default to the URI if no label has been generated
				label = aNode.getData().iri.toString();
			}
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
	
	class IRIHarvester implements GenericTreeVisitorIfc<SKOSTreeNode> {

		List<IRI> iris = new ArrayList<IRI>();
		
		@Override
		public boolean visit(GenericTreeNode<SKOSTreeNode> node)
		throws GenericTreeVisitorException {
			this.iris.add(node.getData().iri);
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
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		try(RepositoryConnection connection = r.getConnection()) {
			Collection<URL> resources = ResourceList.listDirectoryResources("rules/inferlite");
			List<SimpleQueryReader> readers = SimpleQueryReaderFactory.fromUrls(new ArrayList<URL>(resources));
			for (SimpleQueryReader aQueryReader : readers) {
				Perform.on(connection).update(aQueryReader.get());
			}
			
			RepositoryWriter.writeToFile("output.ttl", connection);
			
			SimpleSKOSTreePrinter printer = new SimpleSKOSTreePrinter(connection, "fr");
			System.out.println(printer.printTree());			
		}
		

	}
}
