package fr.sparna.rdf.extractor.jsonld;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.sparna.commons.xml.SimpleNamespaceContext;
import fr.sparna.rdf.extractor.CompositeExtractor;
import fr.sparna.rdf.extractor.DataExtractionException;
import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractionSourceFactory;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.DataExtractorHandlerFactory;
import fr.sparna.rdf.extractor.HtmlExtractor;
import fr.sparna.rdf.extractor.WebPageExtractorFactory;

public class JsonLDExtractor extends HtmlExtractor implements DataExtractor {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private static final String XPATH_JSON_LD = "//xhtml:script[@type='application/ld+json']";

	// we prefer to keep this and init this only once in the hope the JsonLD contexts will be cached
	protected RDFParser parser;
	
	public JsonLDExtractor() {
		parser = Rio.createParser(RDFFormat.JSONLD);
	}
	
	public void extract(DataExtractionSource in, RDFHandler out) throws DataExtractionException {
		log.debug(this.getClass().getSimpleName()+" - Extracting from {}", in.getIri());
		long start = System.currentTimeMillis();

		// I. Turn into DOM
		Document dom = in.getContentDom();

		// II.Extract script tags
		NodeList nodes = null;
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			SimpleNamespaceContext snc = new SimpleNamespaceContext();
			snc.setBindings(new HashMap<String, String>(){{ put("xhtml", "http://www.w3.org/1999/xhtml"); }});
			xPath.setNamespaceContext(snc);
			log.debug("Extracting script tags with XPath "+XPATH_JSON_LD);
			nodes = (NodeList)xPath.evaluate(
					XPATH_JSON_LD,
					dom.getDocumentElement(),
					XPathConstants.NODESET
					);
		} catch (XPathExpressionException ignore) {
			ignore.printStackTrace();
		}

		// III. Parse every JSON-LD piece found in the page		
		if(nodes != null) {
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element e = (Element) nodes.item(i);
				String content = e.getTextContent();
				log.debug("Parsing JSON-LD :\n {}"+content);

	            try {
	            	// parse and use documentUrl as base URI
//	            	Repository r = new SailRepository(new MemoryStore());
//	    			r.initialize();
//	    			try(RepositoryConnection tempConnection = r.getConnection()) {
//	    				parser.setRDFHandler(new RDFInserter(tempConnection));
//		            	parser.parse(new StringReader(e.getTextContent()), in.getIri().stringValue());
//		            	forceUniqueBlankNodes(tempConnection);
//		            	tempConnection.export(out);
//	    				// TurtleTripleCallback ttc = new TurtleTripleCallback();
//	    				// JsonLdProcessor.toRDF(JsonUtils.fromInputStream(new ByteArrayInputStream(e.getTextContent().getBytes())), ttc);
//	    			}	 
	            	
	            	// parse into a Model
	            	Model newModel = new LinkedHashModel();
	            	parser.setRDFHandler(new StatementCollector(newModel));
	            	parser.parse(new StringReader(e.getTextContent()), in.getIri().stringValue());
	            	// enforce globally unique blank nodes in this model, and send triples to the output stream
	            	forceUniqueBlankNodes(newModel, out);
	            	
				} catch (Exception e1) {
					log.error("Exception while parsing JSON-LD : {}, moving to next JSON-LD piece", e1.getMessage());
					e1.printStackTrace();
				}
			}
		}		

		log.debug(this.getClass().getSimpleName()+" - Done extracting from {} in {}ms", in.getIri(), System.currentTimeMillis()-start);
	}
	
	public void forceUniqueBlankNodes(RepositoryConnection connection) {
		long uniqueKey = System.currentTimeMillis();
		
		String bNodeUpdate = ""
				+ " DELETE { ?bNode ?p ?o . ?y ?z ?bNode }"
				+ " INSERT { ?newBNode ?p ?o . ?y ?z ?newBNode }"
				+ " WHERE {"
				+ "  {"
				+ "  SELECT ?bNode ?newBNode"
				+ "  WHERE {"
				+ "  { "
				+ "    SELECT DISTINCT ?bNode"
				+ "    WHERE { "
				+ "      ?bNode ?A ?B ."
				+ "      FILTER(isBlank(?bNode))"
				+ "    } "
				+ "  }"
				+ "  BIND(BNODE(\""+uniqueKey+"\") AS ?newBNode)"
				+ "  }"
				+ "  }"
				+ "  ?bNode ?p ?o . OPTIONAL { ?y ?z ?bNode } "
				+ " }";
		
		Update u = connection.prepareUpdate(bNodeUpdate);
		u.execute();
	}
	
	public void forceUniqueBlankNodes(Model m, RDFHandler out) {
		long uniqueKey = System.currentTimeMillis();
		
		Map<String, String> newBNodeIds = new HashMap<>();
		
		int counter = 0;
		for (Statement s : m) {
			Resource subject = s.getSubject();
			IRI predicate = s.getPredicate();
			Value object = s.getObject();
			if(subject instanceof BNode) {
				String id = ((BNode)subject).getID();
				if(!newBNodeIds.containsKey(id)) {
					String newId = uniqueKey+"_"+counter;
					newBNodeIds.put(id, newId);
				} 
				subject = SimpleValueFactory.getInstance().createBNode(newBNodeIds.get(id));
			}
			
			if(object instanceof BNode) {
				String id = ((BNode)object).getID();
				if(!newBNodeIds.containsKey(id)) {
					String newId = uniqueKey+"_"+counter;
					newBNodeIds.put(id, newId);
				} 
				object = SimpleValueFactory.getInstance().createBNode(newBNodeIds.get(id));
			}
			
			out.handleStatement(SimpleValueFactory.getInstance().createStatement(subject, predicate, object));
			
			counter++;
		}
	}
	
	
	public static void main(String...strings) throws Exception {
		BasicConfigurator.configure();
		JsonLDExtractor me = new JsonLDExtractor();
		// Repository r = new SailRepository(new MemoryStore());
		Repository r = new HTTPRepository("http://localhost:7200/repositories/touraine");
		r.initialize();
		
		
		
		LogManager.getLogger("org.apache.http").setLevel(Level.INFO);

		try(RepositoryConnection connection = r.getConnection()) {
			DataExtractionSource source = new DataExtractionSourceFactory("StructuredDataCrawler (http://www.sparna.fr/structureddatacrawler)").buildSource(SimpleValueFactory.getInstance().createIRI("https://37.agendaculturel.fr/concert/joue-les-tours/alain-chamfort.html"));
			// RDFHandler originalHandler = new RDFInserter(connection);
			DataExtractorHandlerFactory dataExtractorHandlerFactory = new DataExtractorHandlerFactory();
			RDFHandler handler = dataExtractorHandlerFactory.newHandler(connection, source.getDocumentIri());
			// me.extract(new DataExtractionSourceFactory().buildSource(SimpleValueFactory.getInstance().createIRI("http://www.37degres-mag.fr/")), new RDFInserter(connection));
			// https://37.agendaculturel.fr/concert/tours/kendji-girac-5.html
			// me.extract(source, handler);
			// me.extract(new DataExtractionSourceFactory().buildSource(SimpleValueFactory.getInstance().createIRI("https://37.agendaculturel.fr/concert/tours/m-pokora.html")), new RDFInserter(connection));
//			try {
//				me.extract(new DataExtractionSourceFactory().buildSource(SimpleValueFactory.getInstance().createIRI("http://sparna.fr")), new RDFInserter(connection));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				me.extract(new DataExtractionSourceFactory().buildSource(SimpleValueFactory.getInstance().createIRI("http://sparna.fr")), new RDFInserter(connection));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			WebPageExtractorFactory wpef = new WebPageExtractorFactory();
			CompositeExtractor ce = wpef.buildExtractor();
			ce.extract(source, handler);
		}
		
		// r.getConnection().export(new TurtleWriter(System.out));
	}
	
	
//	public static void main(String...strings) throws Exception {
//		Repository r = new SailRepository(new MemoryStore());
//		r.initialize();
//		
//		try(RepositoryConnection connection = r.getConnection()) {
//			// RDFParser parser = Rio.createParser(RDFFormat.JSONLD);
//			RDFParser parser = new JSONLDParser(new ValueStore(new File("/tmp")));
//			// RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
//			parser.setRDFHandler(new RDFInserter(connection));
//			
//			String input1 = "{ \"@type\":\"http://schema.org/Organization\", \"http://schema.org/name\":\"Sparna\" }";
//			// String input1 = "[ a <http://schema.org/Organization> ; <http://schema.org/name> \"Sparna\" ; ] .";
//			parser.parse(new ByteArrayInputStream(input1.getBytes()), RDF.NS.getName());
//			
//			String input2 = "{ \"@type\":\"http://schema.org/Place\", \"http://schema.org/name\":\"Tours\" }";
//			// String input2 = "[ a <http://schema.org/Place> ; <http://schema.org/name> \"Tours\" ; ] .";
//			parser.parse(new ByteArrayInputStream(input2.getBytes()), RDF.NS.getName());
//		}
//		
//		r.getConnection().export(new TurtleWriter(System.out));
//		
//		new JsonLDExtractor().test();
//		
//	}
//	
//	public void test() throws Exception {
//		Repository target = new SailRepository(new MemoryStore());
//		target.initialize();
//		
//		RDFParser parser = Rio.createParser(RDFFormat.JSONLD);
//		
//		try(RepositoryConnection targetConnection = target.getConnection()) {
//		
//			Repository r = new SailRepository(new MemoryStore());
//			r.initialize();
//			try(RepositoryConnection connection = r.getConnection()) {
//				parser.setRDFHandler(new RDFInserter(connection));
//				String input1 = "{ \"@type\":\"http://schema.org/Organization\", \"http://schema.org/name\":\"Sparna\" }";
//				parser.parse(new ByteArrayInputStream(input1.getBytes()), RDF.NS.getName());
//				
//				String update = createBNodeUpdateQuery(Long.toString(System.currentTimeMillis()));
//				System.out.println("Executing :\n"+update);
//				Update u = connection.prepareUpdate(update);
//				u.execute();
//				
//				connection.export(new RDFInserter(targetConnection));
//			}
//			
//			r = new SailRepository(new MemoryStore());
//			r.initialize();
//			try(RepositoryConnection connection = r.getConnection()) {
//				parser.setRDFHandler(new RDFInserter(connection));
//				String input1 = "{ \"@type\":\"http://schema.org/Place\", \"http://schema.org/name\":\"Tours\" }";
//				parser.parse(new ByteArrayInputStream(input1.getBytes()), RDF.NS.getName());
//				
//				String update = createBNodeUpdateQuery(Long.toString(System.currentTimeMillis()));
//				System.out.println("Executing :\n"+update);
//				Update u = connection.prepareUpdate(update);
//				u.execute();
//				
//				connection.export(new RDFInserter(targetConnection));
//			}
//			
//			System.out.println("Printing : \n");
//			targetConnection.export(new TurtleWriter(System.out));
//		}
//		
//		
//	}
//	
//	public String createBNodeUpdateQuery(String uniqueKey) {
////		String bNodeUpdate = ""
////				+ " DELETE { ?bNode ?p ?o . ?y ?z ?bNode }"
////				+ " INSERT { ?newBnode ?p ?o . ?y ?z ?newBnode }"
////				+ " WHERE {"
////				+ "  ?bNode ?p ?o . OPTIONAL { ?y ?z ?bNode }"
////				+ "  FILTER(isBlank(?bNode))"
////				+ "  BIND(BNODE(CONCAT(STR(?bNode), \""+uniqueKey+"\")) AS ?newBnode)"
////				+ " }";
//		
////		String bNodeUpdate = ""
////				+ " DELETE { ?bNode ?p ?o . ?y ?z ?bNode }"
////				+ " INSERT { ?oneNewBNode ?p ?o . ?y ?z ?oneNewBNode }"
////				+ " WHERE {"
////				+ "  { "
////				+ "    SELECT ?bNode ( SAMPLE(?newBNode) AS ?oneNewBNode )"
////				+ "    WHERE { "
////				+ "      ?bNode ?p ?o ."
////				+ "      FILTER(isBlank(?bNode))"
////				+ "      BIND(BNODE(\""+uniqueKey+"\") AS ?newBNode)"
////				+ "    } "
////				+ "    GROUP BY ?bNode"
////				+ "  }"
////				+ "  ?bNode ?p ?o . OPTIONAL { ?y ?z ?bNode } "
////				+ " }";
//		
//		String bNodeUpdate = ""
//				+ " DELETE { ?bNode ?p ?o . ?y ?z ?bNode }"
//				+ " INSERT { ?newBNode ?p ?o . ?y ?z ?newBNode }"
//				+ " WHERE {"
//				+ "  {"
//				+ "  SELECT ?bNode ?newBNode"
//				+ "  WHERE {"
//				+ "  { "
//				+ "    SELECT DISTINCT ?bNode"
//				+ "    WHERE { "
//				+ "      ?bNode ?p ?o ."
//				+ "      FILTER(isBlank(?bNode))"
//				+ "    } "
//				+ "  }"
//				+ "  BIND(BNODE(\""+uniqueKey+"\") AS ?newBNode)"
//				+ "  }"
//				+ "  }"
//				+ "  ?bNode ?p ?o . OPTIONAL { ?y ?z ?bNode } "
//				+ " }";
//		
//		return bNodeUpdate;
//	}

}
