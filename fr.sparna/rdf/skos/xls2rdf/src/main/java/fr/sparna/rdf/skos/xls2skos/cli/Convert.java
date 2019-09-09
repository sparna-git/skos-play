package fr.sparna.rdf.skos.xls2skos.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
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
		
		Repository supportRepository = new SailRepository(new MemoryStore());
		supportRepository.initialize();
		
		try(RepositoryConnection connection = supportRepository.getConnection()) {
			if(a.getExternalData() != null) {			
				if(a.getExternalData().isFile()) {
					log.debug("Loading external data from  : "+a.getExternalData().getName());
					RDFFormat f = RDFParserRegistry.getInstance().getFileFormatForFileName(a.getExternalData().getName()).orElse(RDFFormat.RDFXML);
					connection.add(a.getExternalData(), a.getExternalData().toURI().toString(), f);
				} else {
					for (File anExternalFile : a.getExternalData().listFiles()) {
						log.debug("Loading external data from  : "+anExternalFile.getName());
						RDFFormat f = RDFParserRegistry.getInstance().getFileFormatForFileName(anExternalFile.getName()).orElse(RDFFormat.RDFXML);
						connection.add(anExternalFile, anExternalFile.toURI().toString(), f);
					}
				}			
			}
		}
		
		converter.setSupportRepository(supportRepository);
		
		if(a.getInput().isFile()) {
			try(InputStream in = new FileInputStream(a.getInput())) {			
				converter.processInputStream(in);			
			}
		} else {
			// sort files to guarantee alphabetical processing
			List<File> files = Arrays.asList(a.getInput().listFiles());
			files.sort((f1, f2) -> { return f1.getName().compareTo(f2.getName()) ;});
			
			// process each file, and add resulting data in supportRepository
			for (File f : files) {
				try(InputStream in = new FileInputStream(f)) {			
					List<Model> result = converter.processInputStream(in);
					for (Model m : result) {
						try(RepositoryConnection connection = supportRepository.getConnection()) {
							connection.add(m);
						}
					}
				}
			}
		}
		
		if(fileStream != null) {
			fileStream.flush();
			fileStream.close();
		}
	}
	
}
