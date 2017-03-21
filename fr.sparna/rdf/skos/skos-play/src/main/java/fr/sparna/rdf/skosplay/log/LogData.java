package fr.sparna.rdf.skosplay.log;

import java.util.List;
import java.util.Map;

/**
 * Cette classe représente les données à passer à la jsp
 * 
 * @author clarvie
 *
 */

public class LogData {

	public static final String KEY = LogData.class.getCanonicalName();
	
	enum Range {
		DAY,
		MONTH,
		YEAR
	}
	
	protected Map<String, Integer> nombreDeLogTotal;
	
	protected Map<String, Integer> rendu;
	
	protected Map<String, Integer> format;
	
	protected Map<String, Integer> allprintAndConvert;
	
	protected Map<String, Integer> printConvertLast365Days;
	
	protected Map<String, Integer> urlsConverted;
	
	protected Map<String, Integer> urlsPrint;
	
	protected List<HistogrammeData> histogrammeData;
	
	protected Range histogrammeRange;
	
	protected String choixperiode;
	
	protected Integer index;
	
	protected String choixperiodelisting;
	
	protected Map<String, Integer> convertLangue;
	
	protected Map<String, Integer> printLangue;
	
	protected ListingData liste;
	
	protected ListingData idliste;
	
	protected String jour;
	

	
	public String getJsonHistogrammeData() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		
		for (HistogrammeData aData : histogrammeData) {
			buffer.append("[");
			buffer.append("\"");
			switch(histogrammeRange) {
			case DAY:
				buffer.append(aData.getDayOrMonthOrYear());
				break;
			case MONTH:
				// TODO : convertir le numéro du mois en libellé
				buffer.append(Month.fromNumber(aData.getDayOrMonthOrYear()).getLabel());
				break;
			case YEAR:
				buffer.append(aData.getDayOrMonthOrYear());
				break;
			default:
				break;				
			}
			buffer.append("\""+",");			
			buffer.append(aData.nbrePrint+",");
			buffer.append(aData.nbreConvert);
			buffer.append("],");
		}

		buffer.append("]");
		return buffer.toString();
	}
	
	
	
	public Map<String, Integer> getPrintLangue() {
		return printLangue;
	}

	public void setPrintLangue(Map<String, Integer> printLangue) {
		this.printLangue = printLangue;
	}



	public Map<String, Integer> getConvertLangue() {
		return convertLangue;
	}

	public void setConvertLangue(Map<String, Integer> langue) {
		this.convertLangue =langue;
	}

	public List<HistogrammeData> getHistogrammeData() {
		return histogrammeData;
	}

	public void setHistogrammeData(List<HistogrammeData> histogrammeData) {
		this.histogrammeData = histogrammeData;
	}

	public Range getHistogrammeRange() {
		return histogrammeRange;
	}

	public void setHistogrammeRange(Range histogrammeRange) {
		this.histogrammeRange = histogrammeRange;
	}

	public Map<String, Integer> getNombreDeLogTotal() {
		return nombreDeLogTotal;
	}

	public void setNombreDeLogTotal(Map<String, Integer> nombreDeLogTotal) {
		this.nombreDeLogTotal = nombreDeLogTotal;
	}
	
	public Map<String, Integer> getRendu() {
		return rendu;
	}

	public void setRendu(Map<String, Integer> rendu) {
		this.rendu = rendu;
	}

	public Map<String, Integer> getFormat() {
		return format;
	}

	public void setFormat(Map<String, Integer> format) {
		this.format = format;
	}

	public Map<String, Integer> getAllprintAndConvert() {
		return allprintAndConvert;
	}

	public void setAllprintAndConvert(Map<String, Integer> allprintAndConvert) {
		this.allprintAndConvert = allprintAndConvert;
	}

	public Map<String, Integer> getPrintConvertLast365Days() {
		return printConvertLast365Days;
	}

	public void setPrintConvertLast365Days(Map<String, Integer> printConvertLast365Days) {
		this.printConvertLast365Days = printConvertLast365Days;
	}

	public String getChoixperiode() {
		return choixperiode;
	}

	public void setChoixperiode(String choixperiode) {
		this.choixperiode = choixperiode;
	}

	public Integer getIndex() {
		return index;
	}



	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public Map<String, Integer> getUrlsConverted() {
		return urlsConverted;
	}

	public void setUrlsConverted(Map<String, Integer> urlsConverted) {
		this.urlsConverted = urlsConverted;
	}

	public Map<String, Integer> getUrlsPrint() {
		return urlsPrint;
	}

	public void setUrlsPrint(Map<String, Integer> urlsPrint) {
		this.urlsPrint = urlsPrint;
	}

	public String getChoixperiodelisting() {
		return choixperiodelisting;
	}

	public void setChoixperiodelisting(String choixperiodelisting) {
		this.choixperiodelisting = choixperiodelisting;
	}

	public ListingData getListe() {
		return liste;
	}

	public void setListe(ListingData liste) {
		this.liste = liste;
	}

	public ListingData getIdliste() {
		return idliste;
	}

	public void setIdliste(ListingData idliste) {
		this.idliste = idliste;
	}

	public String getJour() {
		return jour;
	}

	public void setJour(String jour) {
		this.jour = jour;
	}
	
	
	
	
	
}
