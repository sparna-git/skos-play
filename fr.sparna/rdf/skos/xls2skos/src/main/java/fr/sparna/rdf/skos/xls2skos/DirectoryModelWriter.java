package fr.sparna.rdf.skos.xls2skos;


import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * Saves each Model in a separate file in the given directory, and optionnaly generates a graph file for easy loading into Virtuoso.
 * 
 * @author thomas
 *
 */
public class DirectoryModelWriter implements ModelWriterIfc {
	
	private File outputFolder;
	private boolean saveGraphFile = true;
	private RDFFormat format = RDFFormat.RDFXML;
	private String graphSuffix = null;
	
	public DirectoryModelWriter(File outputFolder) {
		super();
		this.outputFolder = outputFolder;
	}

	/* (non-Javadoc)
	 * @see fr.sparna.rdf.skos.xls2skos.ModelSaverIfc#saveGraphModel(java.lang.String, org.eclipse.rdf4j.model.Model)
	 */
	@Override
	public void saveGraphModel(String graph, Model model) {
		graph = graph + ((this.graphSuffix != null)?graphSuffix:"");
		
		try {
			String filename = URLEncoder.encode(graph, "UTF-8");
			File file = new File(outputFolder, filename + "." + format.getDefaultFileExtension());
			try (FileOutputStream fos = new FileOutputStream(file)) {
				RDFWriter w = RDFWriterRegistry.getInstance().get(format).get().getWriter(fos);
				exportModel(model, w);
				fos.flush();
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to save model", e);
			}
			
			if(saveGraphFile) {
				File graphFile = new File(outputFolder, file.getName() + ".graph");
				IOUtils.write(graph, new FileOutputStream(graphFile));
			}
		} catch(Exception e) {
			throw Xls2SkosException.rethrow(e);
		}
	}
	
	public void exportModel(Model model, RDFHandler handler) {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		RepositoryConnection c = r.getConnection();
		c.setNamespace("skos", SKOS.NAMESPACE);
		c.setNamespace("skosxl", SKOSXL.NAMESPACE);
		c.setNamespace("euvoc", "http://publications.europa.eu/ontology/euvoc#");
		c.setNamespace("dcterms", DCTERMS.NAMESPACE);
		c.add(model);
		c.export(handler);
	} 

	public String getGraphSuffix() {
		return graphSuffix;
	}

	public void setGraphSuffix(String graphSuffix) {
		this.graphSuffix = graphSuffix;
	}

	@Override
	public void beginWorkbook() {

	}

	@Override
	public void endWorkbook() {

	}

	public boolean isSaveGraphFile() {
		return saveGraphFile;
	}

	public void setSaveGraphFile(boolean saveGraphFile) {
		this.saveGraphFile = saveGraphFile;
	}

	public RDFFormat getFormat() {
		return format;
	}

	public void setFormat(RDFFormat format) {
		this.format = format;
	}

}