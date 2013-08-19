package fr.sparna.rdf.skosplay;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.repository.Repository;

import fr.sparna.commons.tree.GenericTree;
import fr.sparna.commons.tree.GenericTreeNode;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLPerformException;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.skos.toolkit.JsonSKOSTreePrinter;
import fr.sparna.rdf.skos.toolkit.SKOSTreeBuilder;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode;
import fr.sparna.rdf.skos.toolkit.SKOSTreeNode.NodeType;

public class JsonServlet extends HttpServlet {

	private static final String PARAM_LANGUAGE = "language";
	
	private static final String PARAM_ROOT = "root";
	
	@Override
	protected void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response
	) throws ServletException, IOException {
		
		// get language param
		String languageParam = (request.getParameter(PARAM_LANGUAGE) != null && !request.getParameter(PARAM_LANGUAGE).equals(""))?request.getParameter(PARAM_LANGUAGE):null;
		// if no language param, set a default value
		String language = (languageParam == null)?"en":languageParam;
		
		// get root param
		String rootParam = (request.getParameter(PARAM_ROOT) != null && !request.getParameter(PARAM_ROOT).equals(""))?request.getParameter(PARAM_ROOT):null;
		
		// retrieve data from session
		Repository r = SessionData.get(request.getSession()).getRepository();
		// recreate a labelReader
		// TODO
		// LabelReader labelReader = SessionData.get(request.getSession()).getLabelReader();
		LabelReader labelReader = new LabelReader(r, "en", language);
		
		// create a tree builder
		SKOSTreeBuilder builder = new SKOSTreeBuilder(r, language);
		GenericTree<SKOSTreeNode> tree = null;
		
		// set content-type and response encoding
		response.setContentType("application/json; charset=UTF-8");
		
		try {
			tree = buildTree(builder, (rootParam != null)?URI.create(rootParam):null);			
			// writes json output
			JsonSKOSTreePrinter printer = new JsonSKOSTreePrinter(labelReader);
			printer.print(tree, response.getOutputStream());
		} catch (SPARQLPerformException e) {
			throw new ServletException(e);
		}
	}	
	

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response
	) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	public static GenericTree<SKOSTreeNode> buildTree(SKOSTreeBuilder builder, URI root)
	throws SPARQLPerformException {
		GenericTree<SKOSTreeNode> tree = new GenericTree<SKOSTreeNode>();
		
		if(root != null) {	
			// generates tree				
			tree = builder.buildTree(root);
		} else {
			// fetch all trees
			List<GenericTree<SKOSTreeNode>> trees = builder.buildTrees();
			
			// if only one, set it as root
			if(trees.size() == 1) {
				tree = trees.get(0);
			} else {
				// otherwise, create a fake root
				GenericTreeNode<SKOSTreeNode> fakeRoot = new GenericTreeNode<SKOSTreeNode>();
				fakeRoot.setData(new SKOSTreeNode(URI.create("skosplay:allData"), "", NodeType.UNKNOWN));

				// add all the trees under it					
				for (GenericTree<SKOSTreeNode> genericTree : trees) {
					fakeRoot.addChild(genericTree.getRoot());
				}
				
				// set the root of the tree
				tree.setRoot(fakeRoot);
			}				
		}
		
		return tree;
	}
	
}
