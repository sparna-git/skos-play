package fr.sparna.rdf.skos.xls2rdf;

import static fr.sparna.rdf.skos.xls2rdf.ExcelHelper.getCellValue;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.skos.xls2rdf.reconcile.ReconciliableValueSetIfc;

public final class ValueGeneratorFactory {
	
	private static Logger log = LoggerFactory.getLogger(ValueGeneratorFactory.class.getName());
	
	public static ValueGeneratorIfc split(ValueGeneratorIfc delegate, String separator) {
		return (model, subject, value, language) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}

			Arrays.stream(StringUtils.split(value, separator)).forEach(
				aValue -> delegate.addValue(model, subject, aValue.trim(), language)
			);
			return null;
		};
	}
	
	public static ValueGeneratorIfc resource(IRI property, PrefixManager prefixManager) {
		return (model, subject, value, language) -> {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			
			IRI iri = SimpleValueFactory.getInstance().createIRI(prefixManager.uri(value.trim(), true));
			
			// can be null if we expected an IRI but we had a literal
			if(iri == null) {
				throw new Xls2SkosException("Expected a URI but got '"+value.trim()+"'");
			}
			
			model.add(subject, property, iri);
			return null;
		};
	}
	
	public static ValueGeneratorIfc lookup(ColumnHeader header, Sheet sheet, short lookupColumn, short uriColumn, PrefixManager prefixManager) {
		return (model, subject, value, language) -> {
			String lookupValue = value;
			
			if(lookupValue.equals("")) {
				return null;
			}
			
			Row foundRow = ExcelHelper.columnLookup(lookupValue, sheet, lookupColumn);
			if(foundRow != null) {
				String iriCellValue = getCellValue(foundRow.getCell(uriColumn));				
				ResourceOrLiteralValueGenerator g = new ResourceOrLiteralValueGenerator(header, prefixManager);
				return g.addValue(model, subject, iriCellValue, language);				
			} else {
				// throw Exception if a reference was not found
				log.error("Unable to find value '"+lookupValue+"' in column of index "+lookupColumn+", while trying to generate property "+header.getProperty());
				// keep the triple as a literal with special predicate ?				
				// throw new Xls2SkosException("Unable to find value '"+lookupValue+"' in column of index "+lookupColumn+", while trying to generate property "+property);
			}
			
			return null;
		};
	}
	
	public static ValueGeneratorIfc reconcile(ColumnHeader header, PrefixManager prefixManager, ReconciliableValueSetIfc reconciledValues) {
		return (model, subject, value, language) -> {
			String lookupValue = value.trim();
			
			if(lookupValue.equals("")) {
				return null;
			}
			
			IRI result = reconciledValues.getReconciledValue(value);
			if(result != null) {
				ResourceOrLiteralValueGenerator g = new ResourceOrLiteralValueGenerator(header, prefixManager);
				return g.addValue(model, subject, result.toString(), language);						
			} else {
				log.error("Unable to find value '"+lookupValue+"'@"+language+" in reconciled values");
			}
			
//			if(filteredStatements.size() == 1) {
//			ResourceOrLiteralValueGenerator g = new ResourceOrLiteralValueGenerator(header, prefixManager);
//			return g.addValue(model, subject, filteredStatements.get(0).getSubject().toString(), language);		
//		} else if(filteredStatements.size() > 1) {
//			log.error("Found multiple values for '"+lookupValue+"' in type/scheme '"+reconcileOn+"' : "+filteredStatements.stream().map(s -> s.getSubject().toString()).collect(Collectors.joining(", ")));
//		} else {
//			log.error("Unable to find value '"+lookupValue+"'@"+language+" in a type/scheme '"+ reconcileOn +"' in the model");
//		}
			
//			try(RepositoryConnection c = supportRepository.getConnection()) {
//				// look for every value in any predicate
//				List<Statement> statementsWithValue = Iterations.asList(c.getStatements(null, null, SimpleValueFactory.getInstance().createLiteral(lookupValue, language)));
//				
//				List<Statement> filteredStatements = new ArrayList<Statement>();
//				// filter with the reconcileOn if present
//				if(reconcileOn != null) {
//					for (Statement s : model) {
//						filteredStatements.addAll(Iterations.asList(
//								c.getStatements(s.getSubject(), RDF.TYPE, reconcileOn)
//						));
//						filteredStatements.addAll(Iterations.asList(
//								c.getStatements(s.getSubject(), SKOS.IN_SCHEME, reconcileOn)
//						));
//					}
//				} else {
//					filteredStatements = statementsWithValue;
//				}
//				
//				if(filteredStatements.size() == 1) {
//					ResourceOrLiteralValueGenerator g = new ResourceOrLiteralValueGenerator(header, prefixManager);
//					return g.addValue(model, subject, filteredStatements.get(0).getSubject().toString(), language);		
//				} else if(filteredStatements.size() > 1) {
//					log.error("Found multiple values for '"+lookupValue+"' in type/scheme '"+reconcileOn+"' : "+filteredStatements.stream().map(s -> s.getSubject().toString()).collect(Collectors.joining(", ")));
//				} else {
//					log.error("Unable to find value '"+lookupValue+"'@"+language+" in a type/scheme '"+ reconcileOn +"' in the model");
//				}
//			}		
			
			return null;
		};
	}

	@Deprecated
	public static ValueGeneratorIfc reconcileLocal(ColumnHeader header, PrefixManager prefixManager, IRI reconcileOn, Repository supportRepository) {
		return (model, subject, value, language) -> {
			String lookupValue = value.trim();
			
			if(lookupValue.equals("")) {
				return null;
			}
			
			try(RepositoryConnection c = supportRepository.getConnection()) {
				// look for every value in any predicate
				List<Statement> statementsWithValue = Iterations.asList(c.getStatements(null, null, SimpleValueFactory.getInstance().createLiteral(lookupValue, language)));
				
				List<Statement> filteredStatements = new ArrayList<Statement>();
				// filter with the reconcileOn if present
				if(reconcileOn != null) {
					for (Statement s : model) {
						filteredStatements.addAll(Iterations.asList(
								c.getStatements(s.getSubject(), RDF.TYPE, reconcileOn)
						));
						filteredStatements.addAll(Iterations.asList(
								c.getStatements(s.getSubject(), SKOS.IN_SCHEME, reconcileOn)
						));
					}
				} else {
					filteredStatements = statementsWithValue;
				}
				
				if(filteredStatements.size() == 1) {
					ResourceOrLiteralValueGenerator g = new ResourceOrLiteralValueGenerator(header, prefixManager);
					return g.addValue(model, subject, filteredStatements.get(0).getSubject().toString(), language);		
				} else if(filteredStatements.size() > 1) {
					log.error("Found multiple values for '"+lookupValue+"' in type/scheme '"+reconcileOn+"' : "+filteredStatements.stream().map(s -> s.getSubject().toString()).collect(Collectors.joining(", ")));
				} else {
					log.error("Unable to find value '"+lookupValue+"'@"+language+" in a type/scheme '"+ reconcileOn +"' in the model");
				}
			}		
			
			return null;
		};
	}
	
	public static ValueGeneratorIfc resourceOrLiteral(ColumnHeader header, PrefixManager prefixManager) {
		ResourceOrLiteralValueGenerator g = new ResourceOrLiteralValueGenerator(header, prefixManager);
		return g;
	}

	public static ValueGeneratorIfc turtleParsing(IRI property, PrefixManager prefixManager) {
		return (model, subject, value, language) -> {
			// create a small piece of Tutle by concatenating...
			StringBuffer turtle = new StringBuffer();
			// ... the prefixes				
			turtle.append(prefixManager.getPrefixesTurtleHeader());
			// ... the subject and the predicate
			turtle.append("<"+subject.stringValue()+">"+" "+"<"+property.stringValue()+"> ");
			// ... the blank node value
			turtle.append(value);
			// ... and a final dot if there is not one already at the end
			if(!value.trim().endsWith(".")) {
				turtle.append(".");
			}
			
			// to debug created turtle
			// System.out.println(turtle);
			
			// now parse the Turtle String and collect the statements in a StatementCollector
			StatementCollector collector = new StatementCollector();
			RDFParser parser = RDFParserRegistry.getInstance().get(RDFFormat.TURTLE).get().getParser();
			parser.setRDFHandler(collector);
			try {
				parser.parse(new StringReader(turtle.toString()), RDF.NS.toString());
				// then add all the resulting statements to the final Model
				model.addAll(collector.getStatements());
			} catch (Exception e) {
				// if anything goes wrong, default to creating a literal
				log.error("Error in parsing Turtle :\n"+turtle);
				e.printStackTrace();
				langOrPlainLiteral(property).addValue(model, subject, value, language);
			}
			
			return null;
		};
	}
	
	public static ValueGeneratorIfc dateLiteral(IRI property) {
		return (model, subject, value, language) -> {

			if (StringUtils.isBlank(value)) return null;

			Literal literal = null; 
			try {
				literal = SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)ExcelHelper.asCalendar(value)));
				model.add(subject, property,literal);
			}
			catch (NumberFormatException ignore) {
			}
			catch (DatatypeConfigurationException ignore) {
				ignore.printStackTrace();
			}
			return literal;
		};
	}

	public static ValueGeneratorIfc langLiteral(IRI property) {
		return (model, subject, value, language) -> {
			Literal literal = SimpleValueFactory.getInstance().createLiteral(value.trim(), language);
			model.add(subject, property, literal);
			return literal;
		};
	}

	public static ValueGeneratorIfc plainLiteral(IRI property) {
		return (model, subject, value, language) -> {
			Literal literal = SimpleValueFactory.getInstance().createLiteral(value.trim());
			model.add(subject, property, literal);
			return literal;
		};
	}
	
	public static ValueGeneratorIfc langOrPlainLiteral(IRI property) {
		return (model, subject, value, language) -> {
			Literal literal;
			if(language != null) {
				literal = SimpleValueFactory.getInstance().createLiteral(value.trim(), language);
			} else {
				literal = SimpleValueFactory.getInstance().createLiteral(value.trim());
			}
			model.add(subject, property, literal);
			return literal;
		};
	}

	public static ValueGeneratorIfc skosXlLabel(IRI xlLabelProperty, PrefixManager prefixManager) {
		return (model, subject, value, language) -> {
			// String labelUri = ConceptSchemeFromExcel.fixUri(value);
			String labelUri = prefixManager.uri(value, true);
			IRI labelResource = SimpleValueFactory.getInstance().createIRI(labelUri);
			model.add(labelResource, RDF.TYPE, SKOSXL.LABEL);
			model.add(subject, xlLabelProperty, labelResource);
			return labelResource;
		};
	}

	public static ValueGeneratorIfc failIfFilledIn(String property) {
		return (model, subject, value, language) -> {
			if (StringUtils.isBlank(value)) return null;
			throw new Xls2SkosException("Property not supported {} if filled in - {} - {}", property, subject, value);
		};
	}

	public static class ResourceOrLiteralValueGenerator implements ValueGeneratorIfc {

		protected ColumnHeader header;
		protected PrefixManager prefixManager;
		
		public ResourceOrLiteralValueGenerator(ColumnHeader header, PrefixManager prefixManager) {
			super();
			this.header = header;
			this.prefixManager = prefixManager;
		}

		@Override
		public Value addValue(Model model, Resource subject, String value, String language) {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			
			IRI datatype = header.getDatatype().orElse(null);

			// if the value starts with http, or uses a known namespace, then try to parse it as a resource
			// only if no datatype or language have been explicitely specified, in which case this will default to a literal
			if(
					datatype == null
					&&
					!header.getLanguage().isPresent()
					&&
					(value.startsWith("http") || prefixManager.usesKnownPrefix(value.trim()))
			) {
				if(!header.isInverse()) {
					model.add(subject, header.getProperty(), SimpleValueFactory.getInstance().createIRI(prefixManager.uri(value.trim(), false)));
				} else {
					model.add(SimpleValueFactory.getInstance().createIRI(prefixManager.uri(value.trim(), false)), header.getProperty(),subject);
				}				
			
			} else if(value.startsWith("(") && value.endsWith(")")) {
				// handle rdf:list
				turtleParsing(header.getProperty(), prefixManager).addValue(model, subject, value, language);	
			} else if(datatype == null && value.startsWith("[") && value.endsWith("]")) {
				// handle blank nodes
				turtleParsing(header.getProperty(), prefixManager).addValue(model, subject, value, language);
			} else {
				// if the value is surrounded with quotes, remove them, they were here to escape a URI to be considered as a literal
				String unescapedValue = (value.startsWith("\"") && value.endsWith("\""))?value.substring(1, value.length()-1):value;
				
				// consider it like a literal
				if(datatype != null) {
					Literal l = null;
					if(datatype.stringValue().equals(XMLSchema.DATE.stringValue())) {
						try {
							Date d = ExcelHelper.asCalendar(unescapedValue.trim()).getTime();
							l = SimpleValueFactory.getInstance().createLiteral(
									new SimpleDateFormat("yyyy-MM-dd").format(d),
									XMLSchema.DATE
							);
						} catch (Exception e) {
							// date parsing failed in the case the cell has a string format - then simply default to a typed literal creation
							l = SimpleValueFactory.getInstance().createLiteral(unescapedValue.trim(), datatype);
						}
					} else if(datatype.stringValue().equals(XMLSchema.DATETIME.stringValue())) {
						try {
							try {
								l = SimpleValueFactory.getInstance().createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)ExcelHelper.asCalendar(unescapedValue.trim())));
							} catch (DatatypeConfigurationException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							// date parsing failed in the case the cell has a string format - then simply default to a typed literal creation
							l = SimpleValueFactory.getInstance().createLiteral(unescapedValue.trim(), datatype);
						}
						
					} else {
						l = SimpleValueFactory.getInstance().createLiteral(unescapedValue.trim(), datatype);
					}
					
					model.add(subject, header.getProperty(), l);
				} else {
					langOrPlainLiteral(header.getProperty()).addValue(model, subject, value, language);
				}
			}
			
			return null;
		};		
	}
	
}
