<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="fr.sparna.rdf.skosplay.SessionData" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<html>
	<head>
		<title>SKOS Play ! - Visualiser des thesaurus SKOS</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
		
		<link rel="alternate" hreflang="en" href="?lang=en" />
	</head>
	<body>
		<div class="container">
			<div class="page-header">
				<div class="row">
			    	<div class="span6"><h1>SKOS Play !</h1></div>
			    	<div class="span6">
			    		<ul class="nav nav-pills pull-right">
			    			<li class="active"><a href="home"><fmt:message key="menu.home" /></a></li>
			    			<li><a href="upload.jsp"><fmt:message key="menu.start" /></a></li>
					    	<li><a href="about"><fmt:message key="menu.about" /></a></li>
					    	<li><a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank"><fmt:message key="menu.feedback" /></a></li>
							<li class="dropdown">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#">
									<c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}">fr</c:when>
										<c:otherwise>en</c:otherwise>
									</c:choose>
									<b class="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li>
									<c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}"><a href="?lang=en">en</a></c:when>
										<c:otherwise><a href="?lang=fr">fr</a></c:otherwise>
									</c:choose>
									</li>
								</ul>
							</li>
					    </ul>
					</div>
			    </div>	      		
	      	</div>
			<div style="font-size:1.6em; line-height: 1.3em; text-align:justify;" class="span10 offset1">
				<p>
				SKOS Play est un service gratuit de visualisation de thesaurus, taxonomies ou vocabulaires contrôlés au format 
				<a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">SKOS</a>.
				</p>
				<p>
				SKOS Play permet d'imprimer des systèmes d'organisation de connaissances exprimés en SKOS,
				dans des pages HTML ou des PDF, et de les visualiser dans des représentations graphiques.
				</p>
				<p>
				SKOS Play est un démonstrateur des possibilités de manipulation des langages
				documentaires et des référentiels d'entreprise, et des technologies du web de données.
				</p>
				<br />
				<div style="text-align:center">
                  	<a href="upload.jsp"><button class="btn btn-large btn-success">Commencer</button></a>
                  	<a href="about"><button class="btn btn-large" type="button">En savoir plus</button></a>                
              	</div>
              	<br />
			</div>

      	</div>
      	<jsp:include page="footer.jsp" />
	</body>
</html>