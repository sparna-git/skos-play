package fr.sparna.ling.dictionary;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Headword {
	
	private static Logger log = LoggerFactory.getLogger(Headword.class.getName());

	private String lemma = "";
	private String originalFlexion;
	private String annotation;
	private SortedSet<String> flexions;
	private String grammaticalCategory;
	private String itmAnnotation = "";
	private SortedSet<String> grammaticalAnalyses = new TreeSet<String>();
	private TreeMap<String, SortedSet<String>> flexionGrammaticalAnalyse = new TreeMap<String, SortedSet<String>>();
	
	
	public Headword (){
		flexions = new TreeSet<String>();
	}

	//getters
	public String getLemma() {return this.lemma;	}
	public String getOriginalFlexion() {return originalFlexion;}
	public String getAnnotation() {return annotation;}
	public SortedSet<String> getFlexions() {return flexions;}
	public String getGrammaticalCategory() {return grammaticalCategory;}
	public String getItmAnnotation(){return this.itmAnnotation;}
	public SortedSet<String> getGrammaticalAnalyses(){return this.grammaticalAnalyses;}
	
	//Setters
	public void setLemma(String lemma) {this.lemma = lemma;	flexions.add(lemma);}
	public void setOriginalFlexion(String originalFlexion) {this.originalFlexion = originalFlexion;}
	public void setAnnotation(String annotation) {this.annotation = annotation;}
	public void setGrammaticalCategory(String grammaticalCategory) {this.grammaticalCategory = grammaticalCategory;}
	public void setITMAnnotation(String itmAnnotation) {this.itmAnnotation = itmAnnotation;}
	
	public void addGrammaticalAnalyses(SortedSet<String> grammaticalAnalyses) {this.grammaticalAnalyses.addAll(grammaticalAnalyses);}
	
	public void addFlexion(String flexion){
		flexions.add(flexion);
	}


	public void editHeadword() {
		log.debug(this.originalFlexion);
		Iterator<String> it = flexions.iterator();
		while(it.hasNext()){
			String flexion = it.next();
			log.debug(" -"+flexion);
		}
		log.debug(" -"+this.annotation);
		
	}

	public SortedSet<String> getFlexionGrammaticalAnalyse(String flexion){
		if(this.flexionGrammaticalAnalyse.containsKey(flexion)){
			return this.flexionGrammaticalAnalyse.get(flexion);
		}
		return new TreeSet<String>();
	}
	
	
	public void setFlexionGrammaticalAnalyse(String flexion,
			SortedSet<String> grammaticalAnalyse) {
		
		this.flexionGrammaticalAnalyse.put(flexion, grammaticalAnalyse);
	}

}
