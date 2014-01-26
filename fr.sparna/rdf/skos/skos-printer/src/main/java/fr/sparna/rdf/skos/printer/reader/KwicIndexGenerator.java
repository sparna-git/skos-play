package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.printer.reader.IndexPrinter.DisplayMode;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.KwicEntry;
import fr.sparna.rdf.skos.printer.schema.KwicIndex;
import fr.sparna.rdf.skos.printer.schema.Label;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;

public class KwicIndexGenerator extends AbstractKosDisplayGenerator {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public KwicIndexGenerator(Repository r, String displayId) {
		super(r, displayId);
	}
	
	public KwicIndexGenerator(Repository r) {
		super(r);
	}
	
	@Override
	protected KosDisplay doGenerate(String mainLang, URI conceptScheme)
	throws SparqlPerformException {
		// build our display	
		KosDisplay d = new KosDisplay();
		
		final List<QueryResultRow> queryResultRows = new ArrayList<QueryResultRow>();
		GetLabelsInSchemeHelper helper = new GetLabelsInSchemeHelper(
				mainLang,
				conceptScheme
		) {
			@Override
			protected void handleLabel(
					Literal label,
					Literal prefLabel,
					Resource concept
			) throws TupleQueryResultHandlerException {
				QueryResultRow es = new QueryResultRow();
				es.conceptURI = concept.stringValue();
				es.label = label.stringValue();
				es.prefLabel = (prefLabel != null)?prefLabel.stringValue():null;	
				queryResultRows.add(es);
			}
		};
		
		Perform.on(repository).select(helper);		
		
		Section s = new Section();
		KwicIndex kwic = new KwicIndex();
		s.setKwicIndex(kwic);
		List<KwicEntry> entries = new ArrayList<KwicEntry>();
		for (QueryResultRow aRow : queryResultRows) {
			List<KwicEntry> local = buildKwicEntries(aRow);
			for (KwicEntry kwicEntry : local) {
				boolean found = false;
				for (KwicEntry existingEntry : entries) {
					if(equals(kwicEntry, existingEntry)) {
						found = true;
						break;
					}
				}
				
				if(!found) {
					entries.add(kwicEntry);
				}
			}
			//entries.addAll(buildKwicEntries(aRow));
		}
		
		
		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(mainLang));
		collator.setStrength(Collator.SECONDARY);
		
		// sort entries according to the key
		Collections.sort(entries, new Comparator<KwicEntry>() {
			@Override
			public int compare(KwicEntry o1, KwicEntry o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null) return -1;
				if(o2 == null) return 1;
				return collator.compare(o1.getKeyLabel(), o2.getKeyLabel());
			}			
		});
		kwic.getEntry().addAll(entries);
		log.debug(IndexPrinter.debug(kwic, DisplayMode.KWIC));
		
		d.getSection().add(s);
		
		// ask for two columns
		d.setColumnCount(BigInteger.valueOf(2));

		return d;	
	}
	
	class QueryResultRow {
		String label;
		String prefLabel;
		String conceptURI;
	}
	
	protected boolean equals(KwicEntry e1, KwicEntry e2) {
		if(e1.getKeyLabel() != null && e2.getKeyLabel() != null && !e1.getKeyLabel().equals(e2.getKeyLabel())) {
			return false;
		}
		if(e1.getBefore() != null && e2.getBefore() != null && !e1.getBefore().equals(e2.getBefore())) {
			return false;
		}
		if(e1.getAfter() != null && e2.getAfter() != null && !e1.getAfter().equals(e2.getAfter())) {
			return false;
		}
		
		return true;
	}
	
	protected List<KwicEntry> buildKwicEntries(QueryResultRow r) {
		List<KwicEntry> entries = new ArrayList<KwicEntry>();
		String labelToProcess = (r.prefLabel != null)?r.prefLabel:r.label;
		
		// create label and type
		Label label = SchemaFactory.createLabel(labelToProcess, (r.prefLabel != null)?"pref":"alt");
		
		String[] words = labelToProcess.split(" ");
		for(int i=0; i<words.length; i++) {
			String token = words[i];
			if(token.length() > 1) {
				entries.add(SchemaFactory.createKwicEntry(
						label,
						implode(Arrays.copyOfRange(words, 0, i)),
						token,
						implode(Arrays.copyOfRange(words, i+1, words.length))
				));
			}
		}
				
		return entries;
	}
	
	protected static String implode(String[] words) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<words.length;i++) {
			sb.append(words[i]);
			if(i != words.length-1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	
	public static void main(String... args) throws Exception {			
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build result document
		KosDocument document = new KosDocument();
		
		// build and set header
		HeaderReader headerReader = new HeaderReader(r);
		KosDocumentHeader header = headerReader.read("en", (args.length > 1)?URI.create(args[1]):null);
		document.setHeader(header);
		
		KwicIndexGenerator reader = new KwicIndexGenerator(r);
		BodyReader bodyReader = new BodyReader(reader);
		document.setBody(bodyReader.readBody("en", (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(document, new File("src/main/resources/kwic-output-test.xml"));
		
//		DisplayPrinter printer = new DisplayPrinter();
//		printer.printToHtml(document, new File("display-test.html"));
//		printer.printToPdf(document, new File("display-test.pdf"));

	}

}
