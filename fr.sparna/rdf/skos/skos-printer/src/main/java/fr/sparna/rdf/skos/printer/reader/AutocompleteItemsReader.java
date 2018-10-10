package fr.sparna.rdf.skos.printer.reader;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilderFactory;
import fr.sparna.rdf.rdf4j.toolkit.repository.init.LoadFromFileOrDirectory;
import fr.sparna.rdf.rdf4j.toolkit.util.LabelReader;
import fr.sparna.rdf.rdf4j.toolkit.util.PropertyReader;
import fr.sparna.rdf.skos.printer.autocomplete.Item;
import fr.sparna.rdf.skos.printer.autocomplete.Items;
import fr.sparna.rdf.skos.printer.autocomplete.JSONWriter;
import fr.sparna.rdf.skos.printer.reader.AlphaIndexDisplayGenerator.QueryResultRow;
import fr.sparna.rdf.skos.toolkit.GetLabelsInSchemeHelper;
import fr.sparna.rdf.skos.toolkit.SKOS;

public class AutocompleteItemsReader {

	
	
	public Items readItems(Repository r, String lang, IRI conceptScheme) {
		
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
		
		try(RepositoryConnection connection = r.getConnection()) {
			// run
			Perform.on(connection).select(helper);
			
			// lire les scopes notes
			PropertyReader scopeNoteReader = new PropertyReader(
					connection,
					SimpleValueFactory.getInstance().createIRI(SKOS.SCOPE_NOTE),
					// additionnal path
					null,
					// lang
					lang,
					// additionnal criteria property
					(conceptScheme != null)?SimpleValueFactory.getInstance().createIRI(SKOS.IN_SCHEME):null,				
					// additionnal criteria object
					(conceptScheme != null)?SimpleValueFactory.getInstance().createIRI(conceptScheme.toString()):null
			);
			scopeNoteReader.setPreLoad(true);
			
			// lire les définitions
			PropertyReader definitionReader = new PropertyReader(
					connection,
					SimpleValueFactory.getInstance().createIRI(SKOS.DEFINITION),
					// additionnal path
					null,
					// lang
					lang,
					// additionnal criteria property
					(conceptScheme != null)?SimpleValueFactory.getInstance().createIRI(SKOS.IN_SCHEME):null,				
					// additionnal criteria object
					(conceptScheme != null)?SimpleValueFactory.getInstance().createIRI(conceptScheme.toString()):null
			);
			definitionReader.setPreLoad(true);
			
			for (Item anItem : items.getItems()) {
				if(anItem.getType().equals("pref")) {
					// TODO : handle multiple values
					List<Value> scopeNotes = scopeNoteReader.read(SimpleValueFactory.getInstance().createIRI(anItem.getUri()));
					if(scopeNotes.size() >= 1) {
						anItem.setScopeNote(scopeNotes.get(0).stringValue());
					}
					List<Value> definitions = definitionReader.read(SimpleValueFactory.getInstance().createIRI(anItem.getUri()));
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
						connection,
						Arrays.asList(new IRI[] {
								SimpleValueFactory.getInstance().createIRI(SKOS.PREF_LABEL),
								// pour DBPedia
								RDFS.LABEL,
								// pour les concept schemes
								DCTERMS.TITLE,
								DC.TITLE}),
						fallbackLanguages,
						lang
				);
				
				items.setThesaurusName(LabelReader.display(schemeLabelReader.getValues(conceptScheme)));
			}
		} // end try(connection)
		
		
		return items;
	}
	
	public static void main(String...args) throws Exception {
		AutocompleteItemsReader me = new AutocompleteItemsReader();
		
		final String LANG = "fr-fr";
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
		org.apache.log4j.Logger.getLogger("fr.sparna.rdf").setLevel(org.apache.log4j.Level.TRACE);
		
		// Repository r = RepositoryBuilder.fromString(args[0]);
//		RepositoryBuilder localRepositoryBuilder = new RepositoryBuilder(new LocalMemoryRepositoryFactory(FactoryConfiguration.RDFS_AWARE));
//		localRepositoryBuilder.addOperation(new LoadFromFileOrDirectory(args[0]));
//		Repository r = localRepositoryBuilder.createNewRepository();
		Repository r = RepositoryBuilderFactory.fromString(args[0]).get();
		
		System.out.println("reading...");
		Items items = me.readItems(r, LANG, (args.length > 1)?SimpleValueFactory.getInstance().createIRI(args[1]):null);
		
		JSONWriter writer = new JSONWriter();
		System.out.println(writer.write(items));
	}
	
}
