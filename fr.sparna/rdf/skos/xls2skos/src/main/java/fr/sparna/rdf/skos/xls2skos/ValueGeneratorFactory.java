package fr.sparna.rdf.skos.xls2skos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

public final class ValueGeneratorFactory {

	public static ValueGeneratorIfc resources(IRI property, char separator) {
		return (model, subject, value) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}

			Arrays.stream(
					StringUtils.split(value, separator)
					).forEach(
							uri -> model.add(subject, property, SimpleValueFactory.getInstance().createIRI(ConceptSchemeFromExcel.fixUri(uri.trim())))
			);
			return null;
		};
	}
	
	public static ValueGeneratorIfc resourcesOrLiteral(IRI property, char separator, String lang) {	
		return (model, subject, value) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}

			// if the value starts with http://, then try to parse it as resources
			if(value.startsWith("http://")) {
				Arrays.stream(
						StringUtils.split(value, separator)
						).forEach(
								uri -> model.add(subject, property, SimpleValueFactory.getInstance().createIRI(ConceptSchemeFromExcel.fixUri(uri.trim())))
				);
			} else {
				// consider it like a literal
				model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value, lang));
			}
			
			return null;
		};
	}

	public static ValueGeneratorIfc dateLiteral(IRI property) {
		return (model, subject, value) -> {

			if (StringUtils.isBlank(value)) return null;

			try {
				Calendar calendar = DateUtil.getJavaCalendar(Double.valueOf(value));
				calendar.setTimeZone(TimeZone.getTimeZone("CEST"));
				model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)calendar)));
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
		return (model, subject, value) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value, lang));
			return null;
		};
	}

	public static ValueGeneratorIfc plainLiteral(IRI property) {
		return (model, subject, value) -> {
			model.add(subject, property, SimpleValueFactory.getInstance().createLiteral(value));
			return null;
		};
	}

	public static ValueGeneratorIfc skosXlLabel(IRI xlLabelProperty) {
		return (model, subject, value) -> {
			String labelUri = ConceptSchemeFromExcel.fixUri(value);
			IRI labelResource = SimpleValueFactory.getInstance().createIRI(labelUri);
			model.add(labelResource, RDF.TYPE, SKOSXL.LABEL);
			model.add(subject, xlLabelProperty, labelResource);
			return labelResource;
		};
	}

	public ValueGeneratorIfc failIfFilledIn(String property) {
		return (model, subject, value) -> {
			if (StringUtils.isBlank(value)) return null;
			throw new Xls2SkosException("Property not supported {} if filled in- {} - {}", property, subject, value);
		};
	}

	
}
