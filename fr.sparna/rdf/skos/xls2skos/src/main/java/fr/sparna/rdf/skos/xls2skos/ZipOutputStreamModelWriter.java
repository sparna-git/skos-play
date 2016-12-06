package fr.sparna.rdf.skos.xls2skos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
 * @author thomas
 *
 */
public class ZipOutputStreamModelWriter implements ModelWriterIfc {
	
	private OutputStream underlyingStream;
	private ZipOutputStream out;
	private RDFFormat format = RDFFormat.RDFXML;
	private String graphSuffix = null;
	private boolean saveGraphFile = false;
	
	public ZipOutputStreamModelWriter(ZipOutputStream out) {
		super();
		this.out = out;
	}
	
	public ZipOutputStreamModelWriter(File f) {
		super();
		try {
			if(!f.exists()) {
				f.createNewFile();
			}
			this.underlyingStream = new FileOutputStream(f);
			this.out = new ZipOutputStream(this.underlyingStream, Charset.forName("UTF-8"));
			this.out.setLevel(9);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public ZipOutputStreamModelWriter(OutputStream underlyingStream) {
		super();
		try {
			this.underlyingStream = underlyingStream;
			this.out = new ZipOutputStream(this.underlyingStream, Charset.forName("UTF-8"));
			this.out.setLevel(9);
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
			// declare a new ZipEntry in the Zip file
			graph = graph + ((this.graphSuffix != null)?graphSuffix:"");
			
			String entryname = URLEncoder.encode(graph, "UTF-8") + "." + format.getDefaultFileExtension();
			// String entryname = graph.substring(graph.lastIndexOf('/')+1) + "." + format.getDefaultFileExtension();
			System.out.println(entryname);
			out.putNextEntry(new ZipEntry(entryname));
			
			// writes in the entry
			RDFWriter w = RDFWriterRegistry.getInstance().get(format).get().getWriter(out);
			exportModel(model, w, prefixes);
			
			// close the entry
			out.closeEntry();
			
			if(saveGraphFile) {
				String graphFileName = entryname + ".graph";
				out.putNextEntry(new ZipEntry(graphFileName));
				out.write(graph.getBytes());
			}
			
		} catch(Exception e) {
			throw Xls2SkosException.rethrow(e);
		}
	}
	
	public void exportModel(Model model, RDFHandler handler, Map<String, String> prefixes) {
		Repository r = new SailRepository(new MemoryStore());
		r.initialize();
		try(RepositoryConnection c = r.getConnection()) {
			// register the prefixes
			prefixes.entrySet().forEach(e -> c.setNamespace(e.getKey(), e.getValue()));
			c.add(model);
			c.export(handler);
		}
	}	
	
	public String getGraphSuffix() {
		return graphSuffix;
	}

	public void setGraphSuffix(String graphSuffix) {
		this.graphSuffix = graphSuffix;
	}
	
	public boolean isSaveGraphFile() {
		return saveGraphFile;
	}

	public void setSaveGraphFile(boolean saveGraphFile) {
		this.saveGraphFile = saveGraphFile;
	}

	@Override
	public void beginWorkbook() {

	}

	@Override
	public void endWorkbook() {
		try {
			out.flush();
			out.close();
			this.underlyingStream.close();
			
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
