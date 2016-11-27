package fr.sparna.rdf.skos.xls2skos;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
			if(value.startsWith("http://") || prefixManager.usesKnownPrefix(value.trim())) {
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
			} else {
				// consider it like a literal
				if(datatype != null) {
					Literal l = null;
					if(datatype.stringValue().equals("http://www.w3.org/2001/XMLSchema#date")) {
						Date d = ExcelHelper.asCalendar(value).getTime();
						l = SimpleValueFactory.getInstance().createLiteral(
								new SimpleDateFormat("yyyy-MM-dd").format(d),
								SimpleValueFactory.getInstance().createIRI("http://www.w3.org/2001/XMLSchema#date")
						);
					} else if(datatype.stringValue().equals("http://www.w3.org/2001/XMLSchema#dateTime")) {
						try {
							l = SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)ExcelHelper.asCalendar(value)));
						} catch (DatatypeConfigurationException e) {
							e.printStackTrace();
						}
						
					} else {
						l = SimpleValueFactory.getInstance().createLiteral(value, datatype);
					}
					
					model.add(subject, property, l);
				} else if(language != null) {
					model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value, language));
				} else {
					model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value));
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
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value, language));
			return null;
		};
	}

	public static ValueGeneratorIfc plainLiteral(IRI property) {
		return (model, subject, value, language, datatype) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value));
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