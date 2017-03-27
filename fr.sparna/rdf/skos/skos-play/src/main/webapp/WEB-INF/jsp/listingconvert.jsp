<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>
<c:set var="data" value="${requestScope['fr.sparna.rdf.skosplay.log.LogData']}" />
<c:set var="sessiondata" value="${sessionScope['fr.sparna.rdf.skosplay.SessionData']}" />
<c:set var="applicationData" value="${applicationScope.applicationData}" />
<html>
<head>
		<title><c:out value="${applicationData.skosPlayConfig.applicationTitle}" /></title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/jasny-bootstrap.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<link href="style/custom.css" rel="stylesheet" />
		
		<link rel="stylesheet" href="css/jquery-editable-select.min.css">
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/jasny-bootstrap.min.js"></script>
		<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
		
</head>
<body>
         
		 <div class="container">
		 	<jsp:include page="header.jsp"/>
		 	<a href="log"> Résumé des logs</a>|
					 	<a href="listingconvert">Listing des conversions </a>| 
						<a href="listingprint"> Listing des prints</a>
			        
			 <table  style="margin:auto;" class="table table-condensed">
					<thead>
						  <tr>
						     <th>
						     	<a href="?periode=alltime&indexDebut=0"  <c:if test="${data.choixperiodelisting == 'alltime'}">Style="color:green;"</c:if>>tout le temps</a>
						     </th>
						     <th>
						     	<a href="?periode=year&indexDebut=0" <c:if test="${data.choixperiodelisting == 'year'}">Style="color:green;"</c:if>>dernière année</a>
						     </th>
						     <th>
						     	<a href="?periode=month&indexDebut=0"<c:if test="${data.choixperiodelisting == 'month'}">Style="color:green;"</c:if>>dernier mois</a>
						     </th>
						     <th>
						     	<a href="?periode=lastweek&indexDebut=0"  <c:if test="${data.choixperiodelisting == 'lastweek'}">Style="color:green;"</c:if>>dernière semaine</a>
						     </th>
						     <th>
						     	<a href="?periode=today&indexDebut=0" <c:if test="${data.choixperiodelisting == 'today'}">Style="color:green;"</c:if>>aujourd'hui</a>
						     </th>
						 </tr>
					</thead>
			 </table>
			 
			 <h1>
				 <a>
				 	<c:if test="${data.jour == 'default'}">Listing des conversions</c:if>
				 	<c:if test="${data.jour != 'default'}">Listing des conversions pour le ${data.jour}</c:if>
				 </a>
			 </h1>
			 <label for="tableconversion"><h4>Vocabulaires convertis :</h4></label>
			<table class="table table-bordered" id="tableconversion">
			    <thead>
			      <tr>
			        <th>Identifiant du vocabulaire</th>
			        <th>Conversions</th>
			      </tr>
			    </thead>
			    <tbody>
			    					    		
			    	  <c:forEach items="${data.idliste.idlist}" var="liste">        				
			    	 		<tr>
					      		<td>${liste.key} </td>
					      		<td>${liste.value}</td>
					      	</tr>
			          </c:forEach>
			      	
			    </tbody>
		    </table> 
		     <table style="margin:auto;">
			 <tr> 
			 	<td>
				 	<a id="previouslien" href="?periode=${data.choixperiodelisting}&indexDebut=${data.index -10}&jour=${data.jour}" >
				 	<button class="btn btn-primary" id="previous"  <c:if test="${data.index==0}"> disabled="disabled"</c:if>>Précédent</button>
			 		</a>
			 	</td>
			 	
			  	<td>
			  		<a id="nextlien" style="margin-left:10px;" href="?periode=${data.choixperiodelisting}&indexDebut=${data.index + 10}&jour=${data.jour}">
			  			<button class="btn btn-primary" id="next" <c:if test="${data.idliste.totalLignes <= 10}">disabled="disabled"</c:if>>Suivant</button>
			  		</a>
			  	</td>
			 </tr>
		    </table>   
			<label for="tableconversion"><h4>URLs des fichiers convertis :</h4></label>
			<table class="table table-bordered" id="tableconversion">
			    <thead>
			      <tr>
			        <th>URL du fichier</th>
			        <th>Conversions</th>
			      </tr>
			    </thead>
			    <tbody>
			    					    		
			    	  <c:forEach items="${data.liste.data}" var="liste">        				
			    	 		<tr>
					      		<td>${liste.key} </td>
					      		<td>${liste.value}</td>
					      	</tr>
			          </c:forEach>
			      	
			    </tbody>
		    </table>
		</div>
</body>
</html>