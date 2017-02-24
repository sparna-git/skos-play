package fr.sparna.rdf.skosplay.log;

import java.util.Map;

/**
 * Cette classe donne la liste des listings convert et print à passer à la jsp
 * 
 * @author clarvie
 *
 */

public class ListingData {

	protected Map<String, Integer> data;
	
	protected Map<String, Integer> idlist;
	
	protected Integer indexDebut;
	
	protected String periode;
	
	protected Integer totalLignes;
	
	protected boolean disableNext;
	
	

	public Map<String, Integer> getData() {
		return data;
	}

	public void setData(Map<String, Integer> data) {
		this.data = data;
	}

	public Integer getIndexDebut() {
		return indexDebut;
	}

	public void setIndexDebut(Integer indexDebut) {
		this.indexDebut = indexDebut;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}

	public Integer getTotalLignes() {
		return totalLignes;
	}

	public void setTotalLignes(Integer totalLignes) {
		this.totalLignes = totalLignes;
	}

	public boolean isDisableNext() {
		return disableNext;
	}

	public void setDisableNext(boolean disableNext) {
		this.disableNext = disableNext;
	}

	public Map<String, Integer> getIdlist() {
		return idlist;
	}

	public void setIdlist(Map<String, Integer> idlist) {
		this.idlist = idlist;
	}
	
	
	
}
