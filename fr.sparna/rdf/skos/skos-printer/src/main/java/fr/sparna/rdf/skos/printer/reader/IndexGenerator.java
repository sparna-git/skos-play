package fr.sparna.rdf.skos.printer.reader;

import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
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

import fr.sparna.commons.lang.StringUtil;
import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.skos.printer.DisplayPrinter;
import fr.sparna.rdf.skos.printer.schema.Att;
import fr.sparna.rdf.skos.printer.schema.Index;
import fr.sparna.rdf.skos.printer.schema.IndexEntry;
import fr.sparna.rdf.skos.printer.schema.KosDisplay;
import fr.sparna.rdf.skos.printer.schema.KosDocument;
import fr.sparna.rdf.skos.printer.schema.KosDocumentHeader;
import fr.sparna.rdf.skos.printer.schema.Label;
import fr.sparna.rdf.skos.printer.schema.Section;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class IndexGenerator extends AbstractKosDisplayGenerator {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public enum IndexType {
		KWIC,
		KWAC,
		KWOC
	}
	
	
	protected ConceptBlockReader conceptBlockReader;
	protected IndexTokenizerIfc tokenizer;
	protected IndexType indexType;
	
	public IndexGenerator(Repository r, String displayId, IndexType indexType) {
		super(r, displayId);
		this.indexType = indexType;
	}
	
	public IndexGenerator(Repository r, IndexType indexType) {
		super(r);
		this.indexType = indexType;
	}
	
	@Override
	protected KosDisplay doGenerate(String mainLang, URI conceptScheme)
	throws SparqlPerformException {
		
		// init a tokenizer based on language (for stopwords)
		this.tokenizer = new LuceneTokenizer(mainLang);
				
		conceptBlockReader = new ConceptBlockReader(this.repository);
		conceptBlockReader.initInternal(mainLang, conceptScheme, this.displayId);
		
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
		
		List<IndexEntry> entries = new ArrayList<IndexEntry>();
		for (QueryResultRow aRow : queryResultRows) {
			List<IndexEntry> local = buildIndexEntries(aRow, mainLang);
			for (IndexEntry kwicEntry : local) {
				boolean found = false;
				for (IndexEntry existingEntry : entries) {
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
		Collections.sort(entries, new Comparator<IndexEntry>() {
			@Override
			public int compare(IndexEntry o1, IndexEntry o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null) return -1;
				if(o2 == null) return 1;
				return collator.compare(o1.getKey(), o2.getKey());
			}			
		});
		
		// build our display	
		KosDisplay d = new KosDisplay();
		// if too many entries, add sections to the output document
		if(entries.size() > 1000) {
			log.debug("Large list, will add sections to the output");
			String currentLetter = null;			
			List<IndexEntry> currentList = new ArrayList<IndexEntry>();
			for (IndexEntry anEntry : entries) {
				String entrySectionTitle = StringUtil.withoutAccents(anEntry.getKey()).toUpperCase().substring(0, 1);
				// first step only
				if(currentLetter == null) {
					currentLetter = entrySectionTitle;
				}
				
				if(!entrySectionTitle.equals(currentLetter)) {
					// we're on a new first character, which means new section
					d.getSection().add(createIndexSection(currentList, currentLetter));
					
					// and then reset the current list for next section
					currentList = new ArrayList<IndexEntry>();
				}
				
				// keep storing current list of entries
				currentList.add(anEntry);
				
				currentLetter = entrySectionTitle;
			}
			// and the last section...
			d.getSection().add(createIndexSection(currentList, currentLetter));
		} else {
			log.debug("Single section added to output");
			d.getSection().add(createIndexSection(entries, null));
		}
		
		// set column count of the display
		if(this.indexType != IndexType.KWIC) {
			d.setColumnCount(BigInteger.valueOf(3));
		}
		
		return d;	
	}
	
	protected Section createIndexSection(List<IndexEntry> entries, String title) {
		Section s = new Section();
		s.setTitle(title);
		Index idx = new Index();
		
		// set indexType
		idx.setIndexStyle(this.indexType.name().toLowerCase());	
		idx.getEntry().addAll(entries);

		s.setIndex(idx);
		return s;
	}
	
	class QueryResultRow {
		String label;
		String prefLabel;
		String conceptURI;
	}
	
	protected boolean equals(IndexEntry e1, IndexEntry e2) {
		if(e1.getKey() != null && e2.getKey() != null && !e1.getKey().equals(e2.getKey())) {
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
	
	protected List<IndexEntry> buildIndexEntries(QueryResultRow r, String lang) {
		List<IndexEntry> entries = new ArrayList<IndexEntry>();
		String labelToProcess = r.label;
		
		boolean cutLabels = (this.indexType == IndexType.KWIC);
		
		// create sourceConceptLabel and type
		Label label = SchemaFactory.createLabel(labelToProcess, (r.prefLabel != null)?"alt":"pref");
		
		String[] words = tokenizer.tokenize(labelToProcess);	
		
		int currentOffset = 0;
		for(int i=0; i<words.length; i++) {
			String token = words[i];
			if(token.length() > 1) {
				String before = labelToProcess.substring(0, labelToProcess.indexOf(token, currentOffset));
				String after = labelToProcess.substring(labelToProcess.indexOf(token, currentOffset) + token.length());
				IndexEntry entry = SchemaFactory.createIndexEntry(
						this.conceptBlockReader.computeConceptBlockId(r.conceptURI, labelToProcess),
						r.conceptURI,
						label,
						((cutLabels && before.length() > 40)?"..."+before.substring(before.length()-37):before),
						token,
						((cutLabels && after.length() > 40)?after.substring(0, 37)+"...":after)
				);
				
				// if the entry corresponds to an alt sourceConceptLabel, add a reference to its pref
				if(r.prefLabel != null) {
					Att a = SchemaFactory.createAttLink(
							this.conceptBlockReader.computeRefId(r.conceptURI, r.prefLabel, true),
							r.conceptURI,
							((cutLabels && r.prefLabel.length() > 40)?r.prefLabel.substring(0, 37)+"...":r.prefLabel),
							SKOSTags.getString(URI.create(SKOS.PREF_LABEL)),
							"pref"
					);
					entry.setAtt(a);
				}
				
				entries.add(entry);
			}
			currentOffset = labelToProcess.indexOf(token, currentOffset);
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

	protected static int occurs(String[] words, String occurrence) {
		int result = 0;
		for(int i=0;i<words.length;i++) {
			if(words[i].equals(occurrence)) {
				result++;
			}
		}
		return result;
	}
	
	
	public static void main(String... args) throws Exception {			
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		final String LANG = "fr";
		
		Repository r = RepositoryBuilder.fromString(args[0]);
		
		// build result document
		KosDocument document = new KosDocument();
		
		// build and set header
		HeaderAndFooterReader headerReader = new HeaderAndFooterReader(r);
		KosDocumentHeader header = headerReader.readHeader(LANG, (args.length > 1)?URI.create(args[1]):null);
		document.setHeader(header);
		
		// IndexGenerator reader = new IndexGenerator(r, new SimpleTokenizer());
		IndexGenerator reader = new IndexGenerator(r, IndexType.KWIC);
		BodyReader bodyReader = new BodyReader(reader);
		document.setBody(bodyReader.readBody(LANG, (args.length > 1)?URI.create(args[1]):null));

		Marshaller m = JAXBContext.newInstance("fr.sparna.rdf.skos.printer.schema").createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		// m.marshal(display, System.out);
		m.marshal(document, new File("src/main/resources/kwic-output-test.xml"));
		
		DisplayPrinter printer = new DisplayPrinter();
		printer.setDebug(true);
		printer.printToHtml(document, new File("display-test.html"), LANG);
		printer.printToPdf(document, new File("display-test.pdf"), LANG);

	}

}
