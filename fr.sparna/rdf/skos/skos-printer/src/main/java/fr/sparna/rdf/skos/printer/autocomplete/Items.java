package fr.sparna.rdf.skos.printer.autocomplete;

import java.util.ArrayList;
import java.util.List;

public class Items {

	protected String thesaurusName;
	protected List<Item> items = new ArrayList<Item>();

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public String getThesaurusName() {
		return thesaurusName;
	}

	public void setThesaurusName(String thesaurusName) {
		this.thesaurusName = thesaurusName;
	}

}
