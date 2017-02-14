package fr.sparna.rdf.skosplay.log;

public class HistogrammeData {

	/**
	 * Dans le cas du mois : contient le numéro du mois, de 1 à 12
	 * Dans le cas de l'année : contient l'année sur 4 chiffres
	 * Dans le cas du jour : contient la date sous la forme "yyyy-MM-dd"
	 */
	protected String dayOrMonthOrYear;
	
	protected int nbreConvert;
	
	protected int nbrePrint;

	
	public Integer getNbreConvert() {
		return nbreConvert;
	}
	
	public void setNbreConvert(int nbreConvert) {
		this.nbreConvert = nbreConvert;
	}
	
	public Integer getNbrePrint() {
		return nbrePrint;
	}
	
	public void setNbrePrint(int nbrePrint) {
		this.nbrePrint = nbrePrint;
	}

	public String getDayOrMonthOrYear() {
		return dayOrMonthOrYear;
	}

	public void setDayOrMonthOrYear(String dayOrMonthOrYear) {
		this.dayOrMonthOrYear = dayOrMonthOrYear;
	}	
	
}
