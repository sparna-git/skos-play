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
import fr.sparna.rdf.sesame.toolkit.skos.JsonSKOSTreePrinter;
import fr.sparna.rdf.sesame.toolkit.skos.SKOSTreeBuilder;
import fr.sparna.rdf.sesame.toolkit.skos.SKOSTreeNode;
import fr.sparna.rdf.sesame.toolkit.skos.SKOSTreeNode.NodeType;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;

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
		// reconstruire un labelReader
		// TODO
		// LabelReader labelReader = SessionData.get(request.getSession()).getLabelReader();
		LabelReader labelReader = new LabelReader(r, "en", language);
		
		// craete a tree builder
		SKOSTreeBuilder builder = new SKOSTreeBuilder(r, language);
		GenericTree<SKOSTreeNode> tree = new GenericTree<SKOSTreeNode>();

		try {
			if(rootParam != null) {	
				// generates tree				
				tree = builder.buildTree(URI.create(rootParam));
			} else {
				// fetch all trees
				List<GenericTree<SKOSTreeNode>> trees = builder.buildTrees();
				
				// if only one, set it as root
				if(trees.size() == 1) {
					tree = trees.get(0);
				} else {
					// otherwise, create a fake root
					GenericTreeNode<SKOSTreeNode> root = new GenericTreeNode<SKOSTreeNode>();
					root.setData(new SKOSTreeNode(URI.create("skosplay:allData"), "", NodeType.UNKNOWN));

					// add all the trees under it					
					for (GenericTree<SKOSTreeNode> genericTree : trees) {
						root.addChild(genericTree.getRoot());
					}
					
					// set the root of the tree
					tree.setRoot(root);
				}				
			}
		} catch (SPARQLPerformException e) {
			throw new ServletException(e);
		}
		
		
		// set content-type and response encoding
		response.setContentType("application/json; charset=UTF-8");
		
		try {
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
	
}
