package fr.sparna.rdf.skos.xls2skos;

import java.io.OutputStream;

import org.eclipse.rdf4j.rio.RDFFormat;

public class ModelWriterFactory {

	protected boolean useZip = false;
	protected RDFFormat format;
	
	public ModelWriterFactory(boolean useZip, RDFFormat format) {
		super();
		this.useZip = useZip;
		this.format = format;
	}

	public ModelWriterIfc buildNewModelWriter(OutputStream out) {
		// initialisation du modelWriter
		ModelWriterIfc modelWriter;
		if(useZip) {
			modelWriter = new ZipOutputStreamModelWriter(out);
			((ZipOutputStreamModelWriter)modelWriter).setFormat(format);
		} else {
			modelWriter=new OutputStreamModelWriter(out);
			((OutputStreamModelWriter)modelWriter).setFormat(format);				
		}
		
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
	
}
