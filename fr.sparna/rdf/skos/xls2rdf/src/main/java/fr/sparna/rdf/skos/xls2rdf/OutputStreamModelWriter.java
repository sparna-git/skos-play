package fr.sparna.rdf.skos.xls2rdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.helpers.BufferedGroupingRDFHandler;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * @author thomas
 *
 */
public class OutputStreamModelWriter implements ModelWriterIfc {
	
	private OutputStream out;
	private RDFFormat format = RDFFormat.RDFXML;
	private Repository outputRepository;
	
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
	public void saveGraphModel(String graph, Model model, Map<String, String> prefixes) {
		try {
			try(RepositoryConnection c = this.outputRepository.getConnection()) {
				// register the prefixes
				prefixes.entrySet().forEach(e -> c.setNamespace(e.getKey(), e.getValue()));
				c.add(model, SimpleValueFactory.getInstance().createIRI(graph));
			}
		} catch(Exception e) {
			throw Xls2SkosException.rethrow(e);
		}
	}
	
	@Override
	public void beginWorkbook() {
		this.outputRepository = new SailRepository(new MemoryStore());
		this.outputRepository.initialize();

	}

	@Override
	public void endWorkbook() {
		RDFHandler handler = new BufferedGroupingRDFHandler(20000, RDFWriterRegistry.getInstance().get(format).get().getWriter(out));
		try(RepositoryConnection c = this.outputRepository.getConnection()) {
			c.setNamespace("skos", SKOS.NAMESPACE);
			c.setNamespace("skosxl", SKOSXL.NAMESPACE);
			c.setNamespace("euvoc", "http://publications.europa.eu/ontology/euvoc#");
			c.setNamespace("dcterms", DCTERMS.NAMESPACE);
			c.export(handler);
		}
		
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
