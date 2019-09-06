package fr.sparna.rdf.skos.xls2skos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryFactory;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.repository.util.RepositoryUtil;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 * Don't rename this class otherwise it could be picked up by Maven plugin to execute test.
 * @author thomas
 *
 */
public class Xls2SkosConverterTestExecution implements Test {

	protected File testFolder;
	protected Xls2SkosConverter converter;
	private Repository outputRepository;
	
	public Xls2SkosConverterTestExecution(File testFolder) {
		super();
		this.testFolder = testFolder;
		this.outputRepository = new SailRepository(new MemoryStore());
		this.outputRepository.initialize();
		
		this.converter = new Xls2SkosConverter(new RepositoryModelWriter(outputRepository), "fr");
		this.converter.setGenerateXl(false);
	}

	@Override
	public int countTestCases() {
		return 1;
	}

	@Override
	public void run(TestResult result) {
		result.startTest(this);
		final File input = new File(this.testFolder, "input.xls");
		final File expected = new File(this.testFolder, "expected.ttl");
		System.out.println("Testing "+input.getAbsolutePath());

		// set external data for reconcile if present
		final File external = new File(this.testFolder, "external.ttl");
		if(external.exists()) {
			Repository externalRepository = new SailRepository(new MemoryStore());
			externalRepository.initialize();
			try(RepositoryConnection c = externalRepository.getConnection()) {
				c.add(Rio.parse(new FileInputStream(external), external.toURI().toURL().toString(), RDFFormat.TURTLE));
			} catch (Exception e) {
				result.addError(this, e);
				throw new IllegalArgumentException("Problem with external.ttl in unit test "+this.testFolder.getName(), e);
			}
			this.converter.setSupportRepository(externalRepository);
		}
		
		// convert
		this.converter.processFile(input);
		
		// get expected repository
//		Model expectedModel;
//		try {
//			expectedModel = Rio.parse(new FileInputStream(expected), expected.toURI().toURL().toString(), RDFFormat.TURTLE);
//		} catch (Exception e) {
//			result.addError(this, e);
//			throw new IllegalArgumentException("Problem with expected.ttl in unit test "+this.testFolder.getName(), e);
//		}
		
		Repository expectedRepository = new SailRepository(new MemoryStore());
		expectedRepository.initialize();
		try(RepositoryConnection expectedConnection = expectedRepository.getConnection()) {
			expectedConnection.add(Rio.parse(new FileInputStream(expected), expected.toURI().toURL().toString(), RDFFormat.TURTLE));
		} catch (Exception e) {
			result.addError(this, e);
			throw new IllegalArgumentException("Problem with expected.ttl in unit test "+this.testFolder.getName(), e);
		}
		
		// reput everything in flat repositories for proper comparisons without the graphs
		Repository outputRepositoryToCompare = new SailRepository(new MemoryStore());
		outputRepositoryToCompare.initialize();
		
		Model outputModel = new LinkedHashModelFactory().createEmptyModel();
		try(RepositoryConnection connection = outputRepository.getConnection()) {
			// print result in ttl (notes: prints all graphs)
			connection.export(new TurtleWriter(System.out));
			connection.export(new StatementCollector(outputModel));
			try(RepositoryConnection connectionToCompare = outputRepositoryToCompare.getConnection()) {
				connectionToCompare.add(outputModel, (Resource)null);
			}
		}
		
		// test if isomorphic		
		// if(!Models.isomorphic(expectedModel, outputModel)) {			
		if(!RepositoryUtil.equals(outputRepositoryToCompare, expectedRepository)) {
			result.addFailure(this, new AssertionFailedError("Test failed on "+this.testFolder+":"
					+ "\nStatements in output not in expected:\n"+RepositoryUtil.difference(outputRepositoryToCompare, expectedRepository)
					+ "\nStatements in expected missing in output:\n"+RepositoryUtil.difference(expectedRepository, outputRepositoryToCompare)
			));
		}
		
		result.endTest(this);
	}

	@Override
	public String toString() {
		return testFolder.getName();
	}
	
	

}
