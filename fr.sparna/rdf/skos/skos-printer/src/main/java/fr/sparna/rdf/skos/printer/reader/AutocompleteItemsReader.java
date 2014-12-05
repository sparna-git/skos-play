package fr.sparna.rdf.skos.printer.reader;

import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;

import fr.sparna.rdf.sesame.toolkit.query.Perform;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory;
import fr.sparna.rdf.sesame.toolkit.repository.RepositoryBuilder;
import fr.sparna.rdf.sesame.toolkit.repository.LocalMemoryRepositoryFactory.FactoryConfiguration;
import fr.sparna.rdf.sesame.toolkit.repository.operation.LoadFromFileOrDirectory;
import fr.sparna.rdf.sesame.toolkit.util.LabelReader;
import fr.sparna.rdf.sesame.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.autocomplete.Item;
import fr.sparna.rdf.skos.printer.autocomplete.Items;
import fr.sparna.rdf.skos.printer.autocomplete.JSONWriter;
import fr.sparna.rdf.skos.printer.reader.AlphaIndexDisplayGenerator.QueryResultRow;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class AutocompleteItemsReader {

	
	
	public Items readItems(Repository r, String lang, URI conceptScheme) throws SparqlPerformException {
		
		final Items items = new Items();

		GetLabelsInSchemeHelper helper = new GetLabelsInSchemeHelper(
				lang,
				conceptScheme
		) {
			@Override
			protected void handleLabel(
					Literal label,
					Literal prefLabel,
					Resource concept
			) throws TupleQueryResultHandlerException {
				Item i = new Item();
				i.setUri(concept.stringValue());
				i.setLabel(label.stringValue());
				if(prefLabel != null) {
					i.setPref(prefLabel.stringValue());
					i.setType("alt");
				} else {
					i.setType("pref");
				}
				items.getItems().add(i);
			}
		};
		
		// run
		Perform.on(r).select(helper);
		
		// lire les scopes notes
		PropertyReader scopeNoteReader = new PropertyReader(
				r,
				URI.create(SKOS.SCOPE_NOTE),
				// additionnal path
				null,
				// lang
				lang,
				// additionnal criteria property
				(conceptScheme != null)?URI.create(SKOS.IN_SCHEME):null,				
				// additionnal criteria object
				(conceptScheme != null)?URI.create(conceptScheme.toString()):null
		);
		scopeNoteReader.setPreLoad(true);
		
		// lire les définitions
		PropertyReader definitionReader = new PropertyReader(
				r,
				URI.create(SKOS.DEFINITION),
				// additionnal path
				null,
				// lang
				lang,
				// additionnal criteria property
				(conceptScheme != null)?URI.create(SKOS.IN_SCHEME):null,				
				// additionnal criteria object
				(conceptScheme != null)?URI.create(conceptScheme.toString()):null
		);
		definitionReader.setPreLoad(true);
		
		for (Item anItem : items.getItems()) {
			if(anItem.getType().equals("pref")) {
				// TODO : handle multiple values
				List<Value> scopeNotes = scopeNoteReader.read(URI.create(anItem.getUri()));
				if(scopeNotes.size() >= 1) {
					anItem.setScopeNote(scopeNotes.get(0).stringValue());
				}
				List<Value> definitions = definitionReader.read(URI.create(anItem.getUri()));
				if(definitions.size() >= 1) {
					anItem.setDefinition(definitions.get(0).stringValue());
				}
			}
		}
		
		// sort the list, so that the default list that is printed in the page
		// is ordered correctly
		
		// setup Collator
		final Collator collator = Collator.getInstance(new Locale(lang));
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(items.getItems(), new Comparator<Item>() {

			@Override
			public int compare(Item o1, Item o2) {
				if(o1 == null && o2 == null) return 0;
				if(o1 == null) return -1;
				if(o2 == null) return 1;
				return collator.compare(o1.getLabel(), o2.getLabel());
			}
			
		});
		
		if(conceptScheme != null) {
			// store the thesaurus name
			// prepare a skos:prefLabel reader for concepts of our thesaurus
			List<String> fallbackLanguages = new ArrayList<String>();
			// if language variant, check it first
			if(lang.indexOf("-") > 0) {
				fallbackLanguages.add(lang.substring(0, lang.indexOf("-")));
			}
			// then check for no language
			fallbackLanguages.add("");
			// then check for english
			fallbackLanguages.add("en");
			
			LabelReader schemeLabelReader = new LabelReader(
					r,
					Arrays.asList(new URI[] {
							URI.create(SKOS.PREF_LABEL),
							// pour DBPedia
							URI.create(RDFS.LABEL.toString()),
							// pour les concept schemes
							URI.create(DCTERMS.TITLE.toString()),
							URI.create(DC.TITLE.toString())}),
					fallbackLanguages,
					lang
			);
			
			items.setThesaurusName(LabelReader.display(schemeLabelReader.getValues(conceptScheme)));
		}
		
		
		return items;
	}
	
	public static void main(String...args) throws Exception {
		AutocompleteItemsReader me = new AutocompleteItemsReader();
		
		final String LANG = "fr-fr";
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		// Repository r = RepositoryBuilder.fromString(args[0]);
		RepositoryBuilder localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));
		localRepositoryBuilder.addOperation(new LoadFromFileOrDirectory(args[0]));
		Repository r = localRepositoryBuilder.createNewRepository();
		
		System.out.println("reading...");
		Items items = me.readItems(r, LANG, (args.length > 1)?URI.create(args[1]):null);
		
		JSONWriter writer = new JSONWriter();
		System.out.println(writer.write(items));
	}
	
}
