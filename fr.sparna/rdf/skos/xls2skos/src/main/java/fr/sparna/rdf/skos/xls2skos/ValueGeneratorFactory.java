package fr.sparna.rdf.skos.xls2skos;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;

public final class ValueGeneratorFactory {

	public static ValueGeneratorIfc resources(IRI property, char separator, PrefixManager prefixManager) {
		return (model, subject, value, language, datatype) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}

			Arrays.stream(
					StringUtils.split(value, separator)
					).forEach(
							uri -> model.add(subject, property, SimpleValueFactory.getInstance().createIRI(prefixManager.uri(uri.trim(), true)))
			);
			return null;
		};
	}
	
	public static ValueGeneratorIfc resourcesOrLiteral(IRI property, char separator, String lang, PrefixManager prefixManager, boolean inverse) {	
		return (model, subject, value, language, datatype) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}

			// if the value starts with http://, or uses a known namespace, then try to parse it as a resource
			// only if no language or datatype have been specified, in which case this will default to a literal
			if(
					datatype == null
					&&
					(value.startsWith("http://") || prefixManager.usesKnownPrefix(value.trim()))
			) {
				if(!inverse) {
					Arrays.stream(
							StringUtils.split(value, separator)
							).forEach(
									uri -> model.add(subject, property, SimpleValueFactory.getInstance().createIRI(prefixManager.uri(uri.trim(), false)))
					);
				} else {
					Arrays.stream(
							StringUtils.split(value, separator)
							).forEach(
									uri -> model.add(SimpleValueFactory.getInstance().createIRI(prefixManager.uri(uri.trim(), false)), property,subject)
					);
				}				
			// handling of rdf:list
			} else if(value.startsWith("(") && value.endsWith(")")) {
				// create the head
				BNode head = SimpleValueFactory.getInstance().createBNode();
				// split and convert to a java List then convert to an RDF list
				RDFCollections.asRDF(
						// split the string on " ", then map each substring to a URI
						Arrays.asList(value.substring(1, value.length()-1).trim().split(" ")).stream().map(new Function<String, Value>() {
							@Override
							public Value apply(String s) {
								if(s.startsWith("http://") || prefixManager.usesKnownPrefix(s.trim())) {
									return SimpleValueFactory.getInstance().createIRI(prefixManager.uri(s.trim(), false));
								} else {
									// consider it like a literal
									if(language != null) {
										return SimpleValueFactory.getInstance().createLiteral(value.trim(), language);
									} else {
										return SimpleValueFactory.getInstance().createLiteral(value.trim());
									} 
								}
							}	
						// then collect the result in a list
						}).collect(Collectors.toList()),
						// provide the head of the list
						head,
						// add the resulting list to the given model
						model
				);
				// add the property pointing to the list
				model.add(subject, property, head);
			} else {
				// if the value is surrounded with quotes, remove them, they were here to escape a URI to be considered as a literal
				String unescapedValue = (value.startsWith("\"") && value.endsWith("\""))?value.substring(1, value.length()-1):value;
				
				// consider it like a literal
				if(datatype != null) {
					Literal l = null;
					if(datatype.stringValue().equals("http://www.w3.org/2001/XMLSchema#date")) {
						Date d = ExcelHelper.asCalendar(unescapedValue.trim()).getTime();
						l = SimpleValueFactory.getInstance().createLiteral(
								new SimpleDateFormat("yyyy-MM-dd").format(d),
								SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2001/XMLSchema#date")
						);
					} else if(datatype.stringValue().equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
						try {
							l = SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)ExcelHelper.asCalendar(unescapedValue.trim())));
						} catch (DatatypeConfigurationException e) {
							e.printStackTrace();
						}
						
					} else {
						l = SimpleValueFactory.getInstance().createLiteral(unescapedValue.trim(), datatype);
					}
					
					model.add(subject, property, l);
				} else if(language != null) {
					model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(unescapedValue.trim(), language));
				} else {
					model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(unescapedValue.trim()));
				}
			}
			
			return null;
		};
	}

	public static ValueGeneratorIfc dateLiteral(IRI property) {
		return (model, subject, value, language, datatype) -> {

			if (StringUtils.isBlank(value)) return null;

			try {
				model.add(
						subject,
						property,
						SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)ExcelHelper.asCalendar(value))));
			}
			catch (NumberFormatException ignore) {
			}
			catch (DatatypeConfigurationException ignore) {
				ignore.printStackTrace();
			}
			return null;
		};
	}

	public static ValueGeneratorIfc langLiteral(IRI property, String lang) {
		return (model, subject, value, language, datatype) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value.trim(), language));
			return null;
		};
	}

	public static ValueGeneratorIfc plainLiteral(IRI property) {
		return (model, subject, value, language, datatype) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value.trim()));
			return null;
		};
	}

	public static ValueGeneratorIfc skosXlLabel(IRI xlLabelProperty, PrefixManager prefixManager) {
		return (model, subject, value, language, datatype) -> {
			// String labelUri = ConceptSchemeFromExcel.fixUri(value);
			String labelUri = prefixManager.uri(value, true);
			IRI labelResource = SimpleValueFactory.getInstance().createIRI(labelUri);
			model.add(labelResource, RDF.TYPE, SKOSXL.LABEL);
			model.add(subject, xlLabelProperty, labelResource);
			return labelResource;
		};
	}

	public ValueGeneratorIfc failIfFilledIn(String property) {
		return (model, subject, value, language, datatype) -> {
			if (StringUtils.isBlank(value)) return null;
			throw new Xls2SkosException("Property not supported {} if filled in- {} - {}", property, subject, value);
		};
	}

	
}
