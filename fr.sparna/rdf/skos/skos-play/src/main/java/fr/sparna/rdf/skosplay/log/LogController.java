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
		SQLLogComptageDao dao = new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlDb(),SkosPlayConfig.getInstance().getSqlQueryRegistry());
		data.setFormat(dao.getNumberOfFormat());
		data.setLangue(dao.getNumberOflanguage());
		data.setRendu(dao.getNumberOfRendu());
		data.setAllprintAndConvert(dao.ListAllLog());
		data.setPrintConvertLast365Days(dao.getprintConvertLast365Days());
		
		switch(choixPeriode)
		{
			case "jour":
							data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear(SQLLogComptageDao.Range.ALLTIME));
							data.setHistogrammeRange(LogData.Range.DAY);
							data.setChoixperiode("jour");
							break;
			case "mois":
							data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear(SQLLogComptageDao.Range.MONTH));
							data.setHistogrammeRange(LogData.Range.MONTH);
							data.setChoixperiode("mois");
							break;
			case "annee":
							data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear(SQLLogComptageDao.Range.YEAR));
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
		SQLLogComptageDao url=new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlDb(),SkosPlayConfig.getInstance().getSqlQueryRegistry());
		
		switch(periode){
		
		case "alltime":
						
						listing=url.getUrlConverted(indexDebut,SQLLogComptageDao.Range.ALLTIME);
						data.setListe(listing);
						listing=url.getIdConverted(SQLLogComptageDao.Range.ALLTIME);
						data.setIdliste(listing);
						
						break;
		case "lastmonth":
						 
						 listing=url.getUrlConverted(indexDebut,SQLLogComptageDao.Range.MONTH);
						 data.setListe(listing);
						 listing=url.getIdConverted(SQLLogComptageDao.Range.MONTH);
						 data.setIdliste(listing);
						
						 break;
						 
		case "lastyear":
						 listing=url.getUrlConverted(indexDebut,SQLLogComptageDao.Range.YEAR);
						 data.setListe(listing);
						 listing=url.getIdConverted(SQLLogComptageDao.Range.YEAR);
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
		SQLLogComptageDao url=new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlDb(),SkosPlayConfig.getInstance().getSqlQueryRegistry());
		
		switch(periode){
		
		case "alltime":
						 listing=url.getUrlPrint(indexDebut,SQLLogComptageDao.Range.ALLTIME);
						 data.setListe(listing);
						 listing=url.getIdPrint(SQLLogComptageDao.Range.ALLTIME);
						 data.setIdliste(listing);
						
						 break;
		case "lastmonth":
						 listing=url.getUrlPrint(indexDebut,SQLLogComptageDao.Range.MONTH);
						 data.setListe(listing);
						 listing=url.getIdPrint(SQLLogComptageDao.Range.MONTH);
						 data.setIdliste(listing);
						 break;
						 
		case "lastyear":
						 listing=url.getUrlPrint(indexDebut,SQLLogComptageDao.Range.YEAR);
						 data.setListe(listing);
						 listing=url.getIdPrint(SQLLogComptageDao.Range.YEAR);
						 data.setIdliste(listing);
						 break;
		
						 
		}
		

		return new ModelAndView("listingprint", LogData.KEY, data);
	}


}
