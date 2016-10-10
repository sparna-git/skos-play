package fr.sparna.rdf.skos.xls2skos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * @author thomas
 *
 */
public class OutputStreamModelWriter implements ModelWriterIfc {
	
	private OutputStream out;
	private RDFFormat format = RDFFormat.RDFXML;
	
	public OutputStreamModelWriter(OutputStream out) {
		super();
		this.out = out;
	}
	
	public OutputStreamModelWriter(File f) {
		super();
		try {
			if(!f.exists()) {
				f.createNewFile();
			}
			this.out = new FileOutputStream(f);
			// determine format automatically based on file extension
			this.format = RDFWriterRegistry.getInstance().getFileFormatForFileName(f.getName()).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see fr.sparna.rdf.skos.xls2skos.ModelSaverIfc#saveGraphModel(java.lang.String, org.eclipse.rdf4j.model.Model)
	 */
	@Override
	public void saveGraphModel(String graph, Model model) {
		try {
			// could be : - but how to handle namespaces properly ?
			// Rio.write(model, out, format);
			RDFWriter w = RDFWriterRegistry.getInstance().get(format).get().getWriter(out);
			exportModel(model, w);
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
	
	@Override
	public void beginWorkbook() {

	}

	@Override
	public void endWorkbook() {
		try {
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public RDFFormat getFormat() {
		return format;
	}

	public void setFormat(RDFFormat format) {
		this.format = format;
	}

}
