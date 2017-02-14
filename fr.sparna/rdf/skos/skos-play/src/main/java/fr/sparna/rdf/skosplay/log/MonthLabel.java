package fr.sparna.rdf.skosplay.log;

public class MonthLabel {
	
	
public void Month(String data, StringBuffer buffer){
		
		switch(data){
			case "1": buffer.append("Janvier");
					  break;
			
			case "2": buffer.append("Février");
			  		  break;
			  		  
			case "3": buffer.append("Mars");
			  	      break;
			  	      
			case "4": buffer.append("Avril");
			          break;
			          
			case "5": buffer.append("Mai");
			          break;
			          
			case "6": buffer.append("Juin");
			          break;
			          
			case "7": buffer.append("Juillet");
			          break;
			          
			case "8": buffer.append("Août");
			          break;
			          
			case "9": buffer.append("Septembre");
			          break;
			          
			case "10": buffer.append("Octobre");
			           break;
			           
			case "11": buffer.append("Novembre");
			           break;
			           
			case "12": buffer.append("Décembre");
			           break;
					 
		}
	}

}
