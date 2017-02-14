package fr.sparna.rdf.skosplay.log;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.skosplay.SessionData;
import fr.sparna.rdf.skosplay.SkosPlayConfig;


@Controller
public class LogController {

	@Autowired
	protected ServletContext servletContext;
	
	
	@RequestMapping(value = "/log")
	public ModelAndView Postlog(
			
			@RequestParam(value="statistique", required=false, defaultValue="jour") String choixPeriode,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
		) throws Exception {
		
		LogData data = new LogData();
		SQLLogComptageDao dao = new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlQueryRegistry());
		data.setFormat(dao.getNumberOfFormat());
		data.setLangue(dao.getNumberOflanguage());
		data.setRendu(dao.getNumberOfRendu());
		data.setAllprintAndConvert(dao.ListAllLog());
		data.setPrintConvertLast365Days(dao.getprintConvertLast365Days());
		
		switch(choixPeriode)
		{
			case "jour":
							data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear(" "));
							data.setHistogrammeRange(LogData.Range.DAY);
							data.setChoixperiode("jour");
							break;
			case "mois":
							data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear("MONTH"));
							data.setHistogrammeRange(LogData.Range.MONTH);
							data.setChoixperiode("mois");
							break;
			case "annee":
							data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear("YEAR"));
							data.setHistogrammeRange(LogData.Range.YEAR);
							data.setChoixperiode("annee");
							break;		
		}
		
		return new ModelAndView("log", LogData.KEY, data);
	}	
	@RequestMapping(value = "/listingconvert")
	public ModelAndView listing(
			@RequestParam(value="indexdebut", required=false, defaultValue="0") int indexDebut,
			@RequestParam(value="periode", required=false, defaultValue="alltime") String periode,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
			) throws Exception {


		LogData data = new LogData();
		ListingData listing;
		data.setChoixperiodelisting(periode);
		
		switch(periode){
		
		case "alltime":
						SQLUrlAlltime url=new SQLUrlAlltime(SkosPlayConfig.getInstance().getSqlQueryRegistry());
						listing=url.getUrlConvertedAllTime(indexDebut);
						data.setListe(listing);
						listing=url.getIdConvertedAllTime(indexDebut);
						data.setIdliste(listing);
						
						break;
		case "lastmonth":
						 SQLUrlLastMonth mois=new SQLUrlLastMonth(SkosPlayConfig.getInstance().getSqlQueryRegistry());
						 listing=mois.getUrlConvertedLastMonth(indexDebut);
						 data.setListe(listing);
						 listing=mois.getIdConvertedLastMonth(indexDebut);
						 data.setIdliste(listing);
						
						 break;
						 
		case "lastyear":
						 SQLUrlLastYear annee=new SQLUrlLastYear(SkosPlayConfig.getInstance().getSqlQueryRegistry());
						 listing=annee.getUrlConvertedLastYear(indexDebut);
						 data.setListe(listing);
						 listing=annee.getIdConvertedLastYear(indexDebut);
						 data.setIdliste(listing);
						 break;
		
						 
		}
	
		return new ModelAndView("listingconvert", LogData.KEY, data);
	}


	@RequestMapping(value = "/listingprint")
	public ModelAndView listingprint(
			@RequestParam(value="indexdebut", required=false, defaultValue="0") int indexDebut,
			@RequestParam(value="periode", required=false, defaultValue="alltime") String periode,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
			) throws Exception {


		LogData data = new LogData();
		ListingData listing;
		data.setChoixperiodelisting(periode);
		
		switch(periode){
		
		case "alltime":
						SQLUrlAlltime url=new SQLUrlAlltime(SkosPlayConfig.getInstance().getSqlQueryRegistry());
						listing=url.getUrlPrintAllTime(indexDebut);
						data.setListe(listing);
						listing=url.getIdPrintAllTime(indexDebut);
						data.setIdliste(listing);
						
						break;
		case "lastmonth":
						 SQLUrlLastMonth mois=new SQLUrlLastMonth(SkosPlayConfig.getInstance().getSqlQueryRegistry());
						 listing=mois.getUrlPrintLastMonth(indexDebut);
						 data.setListe(listing);
						 listing=mois.getIdPrintLastMonth(indexDebut);
						 data.setIdliste(listing);
						 break;
						 
		case "lastyear":
						 SQLUrlLastYear annee=new SQLUrlLastYear(SkosPlayConfig.getInstance().getSqlQueryRegistry());
						 listing=annee.getUrlPrintLastYear(indexDebut);
						 data.setListe(listing);
						 listing=annee.getIdPrintLastYear(indexDebut);
						 data.setIdliste(listing);
						 break;
		
						 
		}
		

		return new ModelAndView("listingprint", LogData.KEY, data);
	}


}
