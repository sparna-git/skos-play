package fr.sparna.rdf.skos.xls2skos.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.skos.xls2skos.ModelWriterFactory;
import fr.sparna.rdf.skos.xls2skos.ModelWriterIfc;
import fr.sparna.rdf.skos.xls2skos.Xls2SkosConverter;

public class Convert implements CliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsConvert a = (ArgumentsConvert)args;
		
		// get an input stream on input file
		if(!a.getInput().exists()) {
			log.error("Given input file "+a.getInput().getAbsolutePath()+" does not exist.");
			return;
		}
		
		// if user asked for graph files, but without outputting in a directory or in a zip, this is an error
		if(a.isGenerateGraphFiles() && !(a.getOutput().getName().endsWith("zip") || a.isOutputAsDirectory())) {
			log.error("If you need to generate graph files please use the option to output in a directory, or provide an output file with .zip extension.");
			return;
		}
		
		// determine output format
		RDFFormat theFormat = RDFFormat.RDFXML;
		if(a.getRdfFormat() != null) {
			if(!RDFWriterRegistry.getInstance().getFileFormatForMIMEType(a.getRdfFormat()).isPresent()) {
				log.error("Unknown output format : "+a.getRdfFormat());
				return;
			} else {
				theFormat = RDFWriterRegistry.getInstance().getFileFormatForMIMEType(a.getRdfFormat()).get();
			}
		} else {
			// determine format from file extension
			if(Rio.getWriterFormatForFileName(a.getOutput().getName()).isPresent()) {
				theFormat = Rio.getWriterFormatForFileName(a.getOutput().getName()).get();
			}
		}
		log.debug("Will use output format : "+theFormat.getDefaultMIMEType());
		
		// determine output mode
		boolean useZip = a.getOutput().getName().endsWith("zip");
		ModelWriterFactory factory = new ModelWriterFactory(useZip, theFormat, a.isGenerateGraphFiles());
		
		ModelWriterIfc modelWriter = null;
		FileOutputStream fileStream = null;
		if(a.isOutputAsDirectory()) {
			modelWriter = factory.buildNewModelWriter(a.getOutput());
		} else {
			// create the file if it does not exists
			a.getOutput().createNewFile();
			fileStream = new FileOutputStream(a.getOutput());
			modelWriter = factory.buildNewModelWriter(fileStream);
		}
		
		log.debug("Will use ModelWriter : "+modelWriter.getClass().getName());
		Xls2SkosConverter converter = new Xls2SkosConverter(modelWriter, a.getLang());
		converter.setGenerateXl(a.isXlify());
		converter.setGenerateXlDefinitions(a.isXlifyDefinitions());
		converter.setApplyPostProcessings(!a.isNoPostProcessings());
		if(a.getExternalData() != null) {
			
			Model externalData = new LinkedHashModelFactory().createEmptyModel();
			
			if(a.getExternalData().isFile()) {
				log.debug("Loading external data from  : "+a.getExternalData().getName());
				RDFFormat f = RDFParserRegistry.getInstance().getFileFormatForFileName(a.getExternalData().getName()).orElse(RDFFormat.RDFXML);
				RDFParser rdfParser = Rio.createParser(f);
				rdfParser.setRDFHandler(new StatementCollector(externalData));
				
				rdfParser.parse(new FileInputStream(a.getExternalData()), a.getExternalData().toURI().toURL().toString());
			} else {
				for (File anExternalFile : a.getExternalData().listFiles()) {
					log.debug("Loading external data from  : "+anExternalFile.getName());
					RDFFormat f = RDFParserRegistry.getInstance().getFileFormatForFileName(anExternalFile.getName()).orElse(RDFFormat.RDFXML);
					RDFParser rdfParser = Rio.createParser(f);
					rdfParser.setRDFHandler(new StatementCollector(externalData));
					
					rdfParser.parse(new FileInputStream(anExternalFile), anExternalFile.toURI().toURL().toString());
				}
			}
			
			converter.setSupportModel(externalData);			
		}
		
		if(a.getInput().isFile()) {
			try(InputStream in = new FileInputStream(a.getInput())) {			
				converter.processInputStream(in);			
			}
		} else {
			for (File f : a.getInput().listFiles()) {
				try(InputStream in = new FileInputStream(f)) {			
					converter.processInputStream(in);			
				}
			}
		}
		
		if(fileStream != null) {
			fileStream.flush();
			fileStream.close();
		}
	}
	
}
