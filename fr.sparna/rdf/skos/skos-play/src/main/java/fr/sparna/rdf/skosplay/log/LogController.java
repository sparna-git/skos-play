package fr.sparna.rdf.skosplay.log;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import fr.sparna.rdf.skosplay.SkosPlayConfig;
import fr.sparna.rdf.skosplay.log.SQLLogComptageDao.Range;

/**
 *  Controller 
 * @author clarvie
 *
 */
@Controller
public class LogController {

	@Autowired
	protected ServletContext servletContext;
	
	
	@RequestMapping(value = "/log")
	public ModelAndView Postlog(
			
			@RequestParam(value="statistique", required=false, defaultValue="alltime") String choixPeriode,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
		) throws Exception {
		
		LogData data = new LogData();
		SQLLogComptageDao dao = new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlDb(),SkosPlayConfig.getInstance().getSqlQueryRegistry());
		data.setFormat(dao.getNumberOfFormat());
		data.setPrintLangue(dao.getNumberOfPrintOrConvertLanguage("print"));
		data.setConvertLangue(dao.getNumberOfPrintOrConvertLanguage("convert"));
		data.setRendu(dao.getNumberOfRendu());
		data.setAllprintAndConvert(dao.ListAllLog());
		data.setPrintConvertLast365Days(dao.getprintConvertLast365Days());
		SQLLogComptageDao.Range periode=SQLLogComptageDao.Range.valueOf(choixPeriode.toUpperCase());
		
		switch(periode)
		{
			case ALLTIME:
							log(data,LogData.Range.DAY, dao,SQLLogComptageDao.Range.ALLTIME,choixPeriode);
							break;
			case MONTH:
							log(data,LogData.Range.MONTH, dao,SQLLogComptageDao.Range.MONTH,choixPeriode);
							break;
			case YEAR:
							log(data,LogData.Range.YEAR, dao,SQLLogComptageDao.Range.YEAR,choixPeriode);
							break;		
		}
		
		return new ModelAndView("log", LogData.KEY, data);
	}	
	
	@RequestMapping(value = "/listingconvert")
	public ModelAndView listing(
			@RequestParam(value="indexDebut", required=false, defaultValue="0") int indexDebut,
			@RequestParam(value="periode", required=false, defaultValue="alltime") String periode,
			@RequestParam(value="jour", required=false, defaultValue="default") String jour,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
			) throws Exception {


		LogData data = new LogData();
		ListingData listing=null;
		data.setIndex(indexDebut);
		data.setJour(jour);
		data.setChoixperiodelisting(periode);
		SQLLogComptageDao dao=new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlDb(),SkosPlayConfig.getInstance().getSqlQueryRegistry());
		SQLLogComptageDao.Range periodes=SQLLogComptageDao.Range.valueOf(periode.toUpperCase());
		
		switch(periodes){
		
		case ALLTIME:
						listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.ALLTIME, indexDebut,jour,"convert");
						break;
		case MONTH:
						listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.ALLTIME, indexDebut,jour,"convert");;						
						break;
						 
		case YEAR:
						listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.ALLTIME, indexDebut,jour,"convert");
						break;
		case TODAY:
						listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.ALLTIME, indexDebut,jour,"convert");
						break;
		case LASTWEEK:
						listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.ALLTIME, indexDebut,jour,"convert");
						break;
						 
		}
	
		return new ModelAndView("listingconvert", LogData.KEY, data);
	}


	@RequestMapping(value = "/listingprint")
	public ModelAndView listingprint(
			@RequestParam(value="indexDebut", required=false, defaultValue="0") Integer indexDebut,
			@RequestParam(value="periode", required=false, defaultValue="alltime") String periode,
			@RequestParam(value="jour", required=false, defaultValue="default") String jour,
			// the request
			HttpServletRequest request,
			// the response
			HttpServletResponse response			
			) throws Exception {


		LogData data = new LogData();
		ListingData listing=null;
		data.setJour(jour);
		data.setIndex(indexDebut);
		data.setChoixperiodelisting(periode);
		SQLLogComptageDao dao=new SQLLogComptageDao(SkosPlayConfig.getInstance().getSqlDb(),SkosPlayConfig.getInstance().getSqlQueryRegistry());
		SQLLogComptageDao.Range periodes=SQLLogComptageDao.Range.valueOf(periode.toUpperCase());
		
		switch(periodes){
		
		case ALLTIME:
						 listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.ALLTIME, indexDebut,jour,"print");
						 break;
		case MONTH:
						 listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.MONTH,indexDebut,jour,"print");
						 break;
						 
		case YEAR:
						 listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.YEAR,indexDebut,jour,"print");
						 break;
		case TODAY:
						 listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.TODAY,indexDebut,jour,"print");
						 break;
						 
		case LASTWEEK:
						 listingConvertPrint(listing, data,dao, SQLLogComptageDao.Range.LASTWEEK,indexDebut,jour,"print");
						 break;
						 
		}
		return new ModelAndView("listingprint", LogData.KEY, data);
	}
	
	/**
	 * retourne le listing des conversion ou print pour une periode donnée
	 * @param listing
	 * @param data
	 * @param dao
	 * @param periode
	 * @param indexDebut
	 */
	public void listingConvertPrint(ListingData listing, LogData data, SQLLogComptageDao dao, Range periode, int indexDebut, String jour, String type){
		
		if(type.equals("convert")){
			listing=dao.getConvertedOrPrintUrl(periode, jour,"convert");
			data.setListe(listing);
			listing=dao.getConvertOrPrintUri(indexDebut,periode,jour,"convert");
			data.setIdliste(listing);
		}else if(type.equals("print")){
			listing=dao.getConvertedOrPrintUrl(periode, jour,"print");
			data.setListe(listing);
			listing=dao.getConvertOrPrintUri(indexDebut,periode,jour,"print");
			data.setIdliste(listing);
		}
	}
	/**
	 * retourne les logs d'une periode donnée
	 * @param data
	 * @param dao
	 * @param periode
	 */
	public void log(LogData data, LogData.Range periodeRange, SQLLogComptageDao dao, Range periodeHisto, String choix){
		
		data.setHistogrammeData(dao.getNumberConvertOrPrintPerDayMonthYear(periodeHisto));
		data.setHistogrammeRange(periodeRange);
		data.setChoixperiode(choix); 
		
	}
}
