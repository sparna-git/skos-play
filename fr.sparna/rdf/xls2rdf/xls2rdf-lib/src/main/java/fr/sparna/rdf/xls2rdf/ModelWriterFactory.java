package fr.sparna.rdf.xls2rdf;

import java.io.File;
import java.io.OutputStream;

import org.eclipse.rdf4j.rio.RDFFormat;

public class ModelWriterFactory {

	protected boolean useZip = false;
	protected boolean useGraph = false;
	protected RDFFormat format;
	
	public ModelWriterFactory(boolean useZip, RDFFormat format) {
		super();
		this.useZip = useZip;
		this.format = format;
	}
	
	public ModelWriterFactory(boolean useZip, RDFFormat format, boolean useGraph) {
		super();
		this.useZip = useZip;
		this.format = format;
		this.useGraph = useGraph;
	}

	public ModelWriterIfc buildNewModelWriter(OutputStream out) {
		// if useGraph, force a ZIP output
		if(useGraph || useZip) {
			ZipOutputStreamModelWriter modelWriter = new ZipOutputStreamModelWriter(out);
			modelWriter.setFormat(format);
			modelWriter.setSaveGraphFile(useGraph);
			return modelWriter;
		} else {
			OutputStreamModelWriter modelWriter = new OutputStreamModelWriter(out);
			modelWriter.setFormat(format);
			return modelWriter;
		}
	}
	
	public ModelWriterIfc buildNewModelWriter(File directory) {
		if(!directory.exists()) {
			directory.mkdirs();
		}
		DirectoryModelWriter modelWriter = new DirectoryModelWriter(directory);
		modelWriter.setFormat(format);
		modelWriter.setSaveGraphFile(this.useGraph);
		return modelWriter;
	}

	public boolean isUseZip() {
		return useZip;
	}

	public void setUseZip(boolean useZip) {
		this.useZip = useZip;
	}

	public RDFFormat getFormat() {
		return format;
	}

	public void setFormat(RDFFormat format) {
		this.format = format;
	}

	public boolean isUseGraph() {
		return useGraph;
	}

	public void setUseGraph(boolean useGraph) {
		this.useGraph = useGraph;
	}
	
}
