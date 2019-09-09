package fr.sparna.rdf.skos.xls2rdf;

import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.sparna.rdf.skos.xls2rdf.ColumnHeader;
import fr.sparna.rdf.skos.xls2rdf.ColumnHeaderParser;
import fr.sparna.rdf.skos.xls2rdf.PrefixManager;

public class ColumnHeaderParserTest {

	private ColumnHeaderParser parser;
	private PrefixManager prefixManager;
	
	@Before
	public void before() {
		this.prefixManager = new PrefixManager();
		this.prefixManager.register("skos", SKOS.NAMESPACE);
		this.prefixManager.register("xsd", XMLSchema.NAMESPACE);
		parser = new ColumnHeaderParser(this.prefixManager);
	}
	
	@Test
	public void test1() {
		String TEST = "http://www.w3.org/2004/02/skos/core#prefLabel";
		ColumnHeader header = this.parser.parse(TEST);
		Assert.assertTrue(header.getDeclaredProperty().equals(SKOS.PREF_LABEL.toString()));
	}
	
	@Test
	public void test2() {
		String TEST = "skos:prefLabel";
		ColumnHeader header = this.parser.parse(TEST);
		Assert.assertTrue(header.getDeclaredProperty().equals(TEST));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
	}
	
	@Test
	public void test3() {
		String TEST = "skos:prefLabel@en";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("skos:prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(header.getLanguage().get().equals("en"));
	}
	
	@Test
	public void test4() {
		String TEST = "skos:prefLabel^^xsd:date";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("skos:prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(header.getDatatype().get().equals(XMLSchema.DATE));
	}
	
	@Test
	public void test5() {
		String TEST = "skos:prefLabel^^<http://www.w3.org/2001/XMLSchema#date>";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("skos:prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(header.getDatatype().get().equals(XMLSchema.DATE));
	}
	
	@Test
	public void test6() {
		String TEST = "http://www.w3.org/2004/02/skos/core#prefLabel^^<http://www.w3.org/2001/XMLSchema#date>";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("http://www.w3.org/2004/02/skos/core#prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(header.getDatatype().get().equals(XMLSchema.DATE));
		Assert.assertTrue(header.getParameters().isEmpty());
	}
	
	@Test
	public void test10() {
		String TEST = "skos:prefLabel(separator=\",\")";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("skos:prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(!header.getDatatype().isPresent());
		Assert.assertTrue(!header.getLanguage().isPresent());
		Assert.assertTrue(header.getParameters().get("separator").equals(","));
	}
	
	@Test
	public void test11() {
		String TEST = "skos:prefLabel@en(separator=\",\")";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("skos:prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(!header.getDatatype().isPresent());
		Assert.assertTrue(header.getLanguage().get().equals("en"));
		Assert.assertTrue(header.getParameters().get("separator").equals(","));
	}
	
	@Test
	public void test12() {
		String TEST = "skos:prefLabel^^xsd:date(separator=\",\")";
		ColumnHeader header = this.parser.parse(TEST);
		// System.out.println(header);
		Assert.assertTrue(header.getDeclaredProperty().equals("skos:prefLabel"));
		Assert.assertTrue(header.getProperty().toString().equals(SKOS.PREF_LABEL.toString()));
		Assert.assertTrue(header.getDatatype().get().equals(XMLSchema.DATE));
		Assert.assertTrue(!header.getLanguage().isPresent());
		Assert.assertTrue(header.getParameters().get("separator").equals(","));
	}
}
