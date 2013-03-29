package fr.sparna.ling.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DelaDictionary {
	
	private Logger log = LoggerFactory.getLogger(DelaDictionary.class.getName());

	private TreeMap<String, Headword> gateLemmaGazetteerMap;
	private TreeMap<String, Headword> gateFlexionGazetteerMap;
	private String language;

	private void init(InputStream dictionaryStream, String charset, String language) {

		debug("Initializing DELA dictionary...");

		this.language = language;

		if(!language.equals("fr") && !language.equals("en")) {
			info("Only french and english have DELA dictionaries - cannot initialize DELA.");
			return;
		}

		this.gateFlexionGazetteerMap = new TreeMap<String,Headword>();
		this.gateLemmaGazetteerMap = new TreeMap<String,Headword>();

		BufferedReader br = null;
		try {
			InputStreamReader sourceContent = new InputStreamReader(dictionaryStream, charset);
			br = new BufferedReader (sourceContent);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			log.error("Unknown charset "+charset+", check your JVM !", e1);
		}
		
		// lire le dictionnaire
		try{
//			if(!delaRepository.endsWith("\\") || !delaRepository.endsWith("/"))
//				delaRepository += "/";
//
//			File dictionaryFile = new File(delaRepository+"dela_"+language+".lst");
//			InputStream is = new FileInputStream(dictionaryFile);
//			InputStreamReader sourceContent = new InputStreamReader(is,charset);
//			BufferedReader br = new BufferedReader (sourceContent);
//			String line;
			
			
			String line;

			while((line = br.readLine()) != null){

				Pattern regex = Pattern.compile("^([^&]+)(&.*)$");
				Matcher matcher = regex.matcher(line);

				if(matcher.find()) {

					String flexion = ""+matcher.group(1);
					String annotation = ""+matcher.group(2);
					String lemma = "";
					String category = "";
					SortedSet<String> grammaticalAnalyse = new TreeSet<String>();

					Pattern lemmaRegex = Pattern.compile("&lemma=([^&]+)");
					Matcher lemmaMatcher = lemmaRegex.matcher(annotation);

					if(lemmaMatcher.find())
						lemma = lemmaMatcher.group(1);

					Pattern categoryRegex = Pattern.compile("&category=([^&]+)");
					Matcher categoryMatcher = categoryRegex.matcher(annotation);

					if(categoryMatcher.find())
						category = categoryMatcher.group(1);

					//on ne garde pas les dérivations verbales
					if(!category.equals("V")){
						//TODO pb : l'analyse grammaticale (infinitif, masc sing...) n'a de sens que pour une flexion,
						//pas pour l'ensemble d'un headword : à ne faire que pr gategazetteer, car contient les flexions

						Pattern gramRegex = Pattern.compile("&gram=([^&]+)");
						Matcher gramMatcher = gramRegex.matcher(annotation);

						while(gramMatcher.find())
							grammaticalAnalyse.add(gramMatcher.group(1));

						//construit l'ensemble du dictionnaire
						Headword headword = new Headword();
						headword.setAnnotation(annotation);
						headword.setLemma(lemma);
						headword.setGrammaticalCategory(category);
						headword.addGrammaticalAnalyses(grammaticalAnalyse);
						headword.setFlexionGrammaticalAnalyse(flexion, grammaticalAnalyse);

						headword.addFlexion(flexion);
						if(flexion.startsWith("prod")) {
							System.out.println(flexion);
						}
						gateFlexionGazetteerMap.put(flexion, headword);

						if(gateLemmaGazetteerMap.containsKey(lemma)){
							Headword headwordbyLemma = gateLemmaGazetteerMap.get(lemma);
							headwordbyLemma.addFlexion(flexion);
							headwordbyLemma.setFlexionGrammaticalAnalyse(flexion, grammaticalAnalyse);
							gateLemmaGazetteerMap.put(lemma, headwordbyLemma);	        			
						} else {
							gateLemmaGazetteerMap.put(lemma, headword);
						}
					}
				}
			}

			debug("Done initializing DELA dictionary");

		} catch(IOException e){
			log.warn("Error while reading DELA dictionary file", e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Parses a "Dela" dictionary converted into an intern format
	 * 
	 * @param dictionaryPath
	 * @param charset
	 */	
	public DelaDictionary(String delaRepository, String charset, String language) {
		if(!delaRepository.endsWith("\\") || !delaRepository.endsWith("/")) {
			delaRepository += "/";
		}

		File dictionaryFile = new File(delaRepository+"dela_"+language+".lst");
		InputStream is = null;
		try {			
			is = new FileInputStream(dictionaryFile);
			this.init(is, charset, language);
		} catch (FileNotFoundException e) {
			log.error("Cannot find dictionary file "+dictionaryFile.getAbsolutePath());
			e.printStackTrace();
		} finally {
			if(is != null) { try {	is.close();	} catch (IOException e) { e.printStackTrace();	} }
		}
	}
	
	/**
	 * Parses a "Dela" dictionary converted into an intern format
	 * 
	 * @param dictionaryPath
	 * @param charset
	 */	
	public DelaDictionary(InputStream stream, String charset, String language) {
		this.init(stream, charset, language);
	}

	/**
	 * 
	 * @param dictionaryPath
	 * @param encoding
	 */
	public Set<String> getFlexion(String originalFlexion, boolean isCaseSensitive, boolean includeOriginalFlexion) {

		Set<String> flexionsRes = new TreeSet<String>();

		String flexionToLook = originalFlexion;

		if(!isCaseSensitive)
			flexionToLook = originalFlexion.toLowerCase();

		if(this.gateFlexionGazetteerMap.containsKey(flexionToLook)){
			Headword headword = this.gateLemmaGazetteerMap.get(this.gateFlexionGazetteerMap.get(flexionToLook).getLemma());
			SortedSet<String> flexions = headword.getFlexions();
			for (String flexion : flexions) {
				flexionsRes.add(flexion);
			}
		} else {//décomposition du mot avant flexion

			String[] words = originalFlexion.split("(-|[«»+=)°{}_\"'\\/!;?#,.():\\[\\] ])+");
			
			List<SortedSet<String>> flexionsList = new ArrayList<SortedSet<String>>();

			boolean atLeastOneWordisFlexible = false;

			// on ne traite pas les libelles superieurs a 4 mots
			if(words.length > 4) {
				if(includeOriginalFlexion) {
					flexionsRes.add(originalFlexion);
				}
				return flexionsRes;
			}

			for(int i=0; i<words.length; i++){
				String word = words[i];
				String wordToLook = word;

				if(!isCaseSensitive)
					wordToLook = word.toLowerCase();

				if(this.gateFlexionGazetteerMap.containsKey(wordToLook)){
					Headword headword = this.gateFlexionGazetteerMap.get(wordToLook);
					String lemma = headword.getLemma();
					
					//TODO à mettre en param (comme caseSensitive)
					//exclusion des verbes
					if(!headword.getGrammaticalCategory().equals("V")){
						atLeastOneWordisFlexible = true;
						flexionsList.add(getFlexions(lemma));	
					}else{
						boolean doDerivation = false;
						boolean noDerivation = false;

						SortedSet<String> grammaticalAnalyses = headword.getGrammaticalAnalyses();
						Iterator<String> iterator = grammaticalAnalyses.iterator();
						while(iterator.hasNext()){
							String grammaticalAnalyse = iterator.next();
							if(grammaticalAnalyse.equals("W")||
									grammaticalAnalyse.equals("Kfs")||
									grammaticalAnalyse.equals("Kfp")||
									grammaticalAnalyse.equals("Kms")||
									grammaticalAnalyse.equals("Kmp"))
								//dérivation des verbes
								doDerivation = false;
							//	        							doDerivation = true;
							//	        						else if(grammaticalAnalyse.equals("ms"))
							//	        							noDerivation = true;
						}

						if(!noDerivation && doDerivation){
							atLeastOneWordisFlexible = true;
							if(this.language.equals("french")){
								SortedSet<String> wantedGrammaticalAnalyses = new TreeSet<String>();
								wantedGrammaticalAnalyses.add("Kfs");
								wantedGrammaticalAnalyses.add("Kfp");
								wantedGrammaticalAnalyses.add("Kms");
								wantedGrammaticalAnalyses.add("Kmp");
								SortedSet<String> specificFlexions = getSpecificFlexions(lemma, wantedGrammaticalAnalyses);
								specificFlexions.add(wordToLook);

								if(!word.equals(wordToLook))
									specificFlexions.add(word);

								flexionsList.add(specificFlexions);
							}else
								flexionsList.add(getFlexions(lemma));

							//Kfs,Kfp,Kms,Kmp
						}else{
							SortedSet<String> flexions = new TreeSet<String>();
							flexions.add(word);
							flexionsList.add(flexions);
							debug("Not derivated:"+ word + "("+originalFlexion+")");
						}
					}
				} else {
					SortedSet<String> flexions = new TreeSet<String>();
					flexions.add(word);
					flexionsList.add(flexions);
					debug("partly not flexible:"+ word + "("+originalFlexion+")");
				}
			}

			if(!atLeastOneWordisFlexible) {
				flexionsRes.add(originalFlexion);
				debug("not flexible at all:"+ originalFlexion);
			}

			SortedSet<String> completeHeadwordflexions = new TreeSet<String>();
			SortedSet<String> uncompleteHeadwordflexions = new TreeSet<String>();

			String[] separators = this.getSeparators(originalFlexion);


			for(int i=0; i<flexionsList.size();i++){
				if( i==0 && flexionsList.size()> 1){
					uncompleteHeadwordflexions = this.addFlexionsToFlexions(
							flexionsList.get(i), 
							flexionsList.get(i+1), 
							separators[i]);
				}
				if (i > 0 && i < flexionsList.size()-1){
					uncompleteHeadwordflexions = this.addFlexionsToFlexions(
							uncompleteHeadwordflexions, 
							flexionsList.get(i+1), 
							separators[i]);
				}
			}
			completeHeadwordflexions = uncompleteHeadwordflexions;

			Iterator<String> iterator = completeHeadwordflexions.iterator();
			while (iterator.hasNext()){
				String flexion = iterator.next();
				flexionsRes.add(flexion);
			}				
		}
		
		// filter originalFlexion if needed
		if(!includeOriginalFlexion) {
			// TODO : case-sensitive ?
			flexionsRes.remove(originalFlexion);
		}
		return flexionsRes;
	}
	
	
	private SortedSet<String> getFlexions(String lemma) {
		Headword headword = this.gateLemmaGazetteerMap.get(lemma);
		return headword.getFlexions();
	}
	
	
	/**
	 * Gets flexions that have the given grammatical analyses
	 * @param lemma
	 * @param grammaticalAnalyses
	 * @return
	 */
	private SortedSet<String>  getSpecificFlexions(String lemma, SortedSet<String> grammaticalAnalyses) {
		SortedSet<String> resultFlexions = new TreeSet<String>();
		Headword headword = this.gateLemmaGazetteerMap.get(lemma);
		SortedSet<String> flexions = headword.getFlexions();

		Iterator<String> it = flexions.iterator();
		while(it.hasNext()){
			String flexion = it.next();
			debug(">>>flexion<<<"+flexion);
			SortedSet<String> grammaticalAnalyse = headword.getFlexionGrammaticalAnalyse(flexion);

			Iterator<String> iter = grammaticalAnalyse.iterator();
			while(iter.hasNext()){
				String gramAnalyse = iter.next();
				debug(">>flexion<<"+flexion+" "+gramAnalyse);
				Iterator<String> iterator = grammaticalAnalyses.iterator();
				while (iterator.hasNext()){
					String analyse = iterator.next();
					if(analyse.equals(gramAnalyse)){
						resultFlexions.add(flexion);
						debug(">flexion prise:"+flexion);
					}else{
						debug(">flexion non prise:"+flexion+" "+gramAnalyse);
					}
				}
			}
		}
		return resultFlexions;
	}
	
	
	/**
	 * 
	 * @param flexion
	 * @param flexions
	 * @param separator
	 * @return
	 */
	private SortedSet<String> addFlexions(String flexion, SortedSet<String> flexions, String separator){

		SortedSet<String> resultFlexionsSet = new TreeSet<String>();
		Iterator<String> it = flexions.iterator();

		while (it.hasNext()){
			String followingFlexion = it.next();
			resultFlexionsSet.add(flexion + separator + followingFlexion);
		}
		return resultFlexionsSet;
	}


	/**
	 * 
	 * @param flexionsOne
	 * @param flexionsTwo
	 * @param separator
	 * @return
	 */
	private SortedSet<String> addFlexionsToFlexions (
			SortedSet<String> flexionsOne, 
			SortedSet<String> flexionsTwo, 
			String separator
	){

		SortedSet<String> resultFlexionsSet = new TreeSet<String>();
		Iterator<String> it = flexionsOne.iterator();

		while (it.hasNext()){	
			String flexion = it.next();
			resultFlexionsSet.addAll(this.addFlexions(flexion, flexionsTwo, separator));
		}
		return resultFlexionsSet;
	}
	
	/**
	 * Gets all word boundaries found in param 'words'
	 * @param words
	 * @return
	 */
	private String[] getSeparators(String words) {

		List<String> separatorList = new ArrayList<String>();

		Pattern pattern = Pattern.compile("(-|[«»+=)°{}_\"'\\/!;?#,.():\\[\\] ])+");
		Matcher matcher = pattern.matcher(words);
		while(matcher.find()){
			String separator = matcher.group(0);
			separatorList.add(separator);
		}

		return separatorList.toArray(new String[separatorList.size()]);
	}

	/**
	 * Clears the dictionary
	 */
	public void clear() {
		gateLemmaGazetteerMap.clear();
		gateFlexionGazetteerMap.clear();
	}
	
	private void debug(String o) {
		System.out.println(o);
		// log.debug(o);
	}
	
	private void info(String o) {
		System.out.println(o);
		// log.info(o);
	}
	
	public static void main(String... args) throws Exception {
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
		DelaDictionary dico = new DelaDictionary(DelaDictionary.class.getClassLoader().getResourceAsStream("dela/dela_fr.lst"), "UTF-8", "fr");
//		testFlexions(dico, "chauffeur");
//		testFlexions(dico, "agent administratif");
//		testFlexions(dico, "assistante de direction");
//		testFlexions(dico, "Permis Bus Autocar");
//		testFlexions(dico, "Camion citerne");
//		testFlexions(dico, "Zone courte");
		testFlexions(dico, "Produits pétroliers");
		testFlexions(dico, "Produit pétrolier");
//		testFlexions(dico, "Livraison au Hayon");
	}
	
	private static void testFlexions(DelaDictionary dico, String test) {
		System.out.println("*** "+test);
		Set<String> flexions = dico.getFlexion(test, false, false);
		for (String aFlexion : flexions) {
			System.out.println("  "+aFlexion);
		}
	}
}
