package fr.sparna.rdf.skos.xls2skos;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModelFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class ValueGeneratorsTest {

	private SimpleValueFactory vf = SimpleValueFactory.getInstance();
	private Resource subject;
	private Model model;

	private ColumnHeaderParser parser;
	private PrefixManager prefixManager;
	
	
	@Before
	public void before() {
		this.subject = vf.createIRI("http://sparna.fr");
		this.model = new LinkedHashModelFactory().createEmptyModel();
		
		this.prefixManager = new PrefixManager();
		this.prefixManager.register("skos", SKOS.NAMESPACE);
		this.prefixManager.register("xsd", XMLSchema.NAMESPACE);
		parser = new ColumnHeaderParser(this.prefixManager);
	}
	
	@Test
	public void plainLiteralTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.plainLiteral(SKOS.NOTATION);		
		vg.addValue(model, subject, "1", null);		
		Assert.assertTrue(model.contains(subject, SKOS.NOTATION, vf.createLiteral("1")));
	}
	
	@Test
	public void langOrPlainLiteralTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.langOrPlainLiteral(SKOS.PREF_LABEL);		
		vg.addValue(model, subject, "sparna", null);
		vg.addValue(model, subject, "SPARNA", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.PREF_LABEL, vf.createLiteral("sparna")));
		Assert.assertTrue(model.contains(subject, SKOS.PREF_LABEL, vf.createLiteral("SPARNA", "fr")));
	}
	
	@Test
	public void resourceOrLiteralTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.resourceOrLiteral(this.parser.parse("skos:prefLabel^^xsd:string"), prefixManager);		
		vg.addValue(model, subject, "sparna", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.PREF_LABEL, vf.createLiteral("sparna", XMLSchema.STRING)));
	}
	
	@Test
	public void overwriteLangTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.resourceOrLiteral(this.parser.parse("skos:prefLabel@en"), prefixManager);		
		vg.addValue(model, subject, "sparna", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.PREF_LABEL, vf.createLiteral("sparna", "fr")));
	}
	
	@Test
	public void splitLangLiteralTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.split(
				ValueGeneratorFactory.resourceOrLiteral(this.parser.parse("skos:altLabel"), prefixManager),
				","
		);
		vg.addValue(model, subject, "sparna, SPARNA", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.ALT_LABEL, vf.createLiteral("sparna", "fr")));
		Assert.assertTrue(model.contains(subject, SKOS.ALT_LABEL, vf.createLiteral("SPARNA", "fr")));
	}
	
	@Test
	public void splitDatatypeLiteralTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.split(
				ValueGeneratorFactory.resourceOrLiteral(this.parser.parse("skos:altLabel^^xsd:string"), prefixManager),
				","
		);
		vg.addValue(model, subject, "sparna, SPARNA", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.ALT_LABEL, vf.createLiteral("sparna", XMLSchema.STRING)));
		Assert.assertTrue(model.contains(subject, SKOS.ALT_LABEL, vf.createLiteral("SPARNA", XMLSchema.STRING)));
	}
	
	@Test
	public void splitFullUriTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.split(
				ValueGeneratorFactory.resourceOrLiteral(this.parser.parse("skos:exactMatch"), prefixManager),
				","
		);
		vg.addValue(model, subject, "http://blog.sparna.fr, http://SPARNA.fr", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.EXACT_MATCH, vf.createIRI("http://blog.sparna.fr")));
		Assert.assertTrue(model.contains(subject, SKOS.EXACT_MATCH, vf.createIRI("http://SPARNA.fr")));
	}
	
	@Test
	public void splitPrefixedUriTest() {
		ValueGeneratorIfc vg = ValueGeneratorFactory.split(
				ValueGeneratorFactory.resourceOrLiteral(this.parser.parse("skos:exactMatch"), prefixManager),
				","
		);
		vg.addValue(model, subject, "skos:notation, skos:prefLabel", "fr");
		Assert.assertTrue(model.contains(subject, SKOS.EXACT_MATCH, SKOS.PREF_LABEL));
		Assert.assertTrue(model.contains(subject, SKOS.EXACT_MATCH, SKOS.NOTATION));
	}
}
