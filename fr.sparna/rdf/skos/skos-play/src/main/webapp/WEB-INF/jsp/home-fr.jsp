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
		<title><c:out value="${applicationData.skosPlayConfig.applicationTitle}" /></title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<link href="style/custom.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>		
		
		<link rel="alternate" hreflang="en" href="?lang=en" />
	</head>
	<body>
		<div class="container">
			<%-- see http://stackoverflow.com/questions/19150683/passing-parameters-to-another-jsp-file-in-jspinclude --%>
			<jsp:include page="header.jsp">
				<jsp:param name="active" value="home"/>
			</jsp:include>
			
			<div style="font-size:1.6em; line-height: 1.3em; text-align:justify;">
				<p>
				SKOS Play est un service gratuit de visualisation de thesaurus, taxonomies ou vocabulaires contrôlés au format 
				<a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">SKOS</a>.
				</p>
				<p>
				SKOS Play permet d'imprimer des systèmes d'organisation de connaissances exprimés en SKOS,
				dans des pages HTML ou des PDF, et de les visualiser dans des représentations graphiques.
				</p>
				<p>
				SKOS Play c'est :
				</p>
				<ul>
					<li style="line-height: 1.3em;">un <em>outil de vérification</em> en phase conception du vocabulaire;</li>
					<li style="line-height: 1.3em;">un <em>outil de communication</em> en phase de validation avec les experts du domaine;</li>
					<li style="line-height: 1.3em;">un <em>outil de publication</em> en phase de diffusion sur le web d'un vocabulaire métier.</li>
				</ul> 
				
				<br />
				<div style="text-align:center">
                  	<a href="upload"><button class="btn btn-primary btn-lg ">Commencer</button></a>
                  	<a href="about"><button class="btn btn-default btn-lg" type="button">En savoir plus</button></a>                
              	</div>
              	<br />
              	<hr />
              	<p><span style="color: #772953;"><span class="glyphicon glyphicon glyphicon-certificate" aria-hidden="true"></span></span>&nbsp;SKOS Play permet également de convertir des feuilles de calcul Excel en <a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">fichier SKOS</a></p>
              	<p>Cela facilite la production de fichiers SKOS pour les taxonomies et la liste des autorités.</p>
              	<p>Le convertisseur peut même générer des données dans d'autres vocabulaires RDF.</p>
				<br />
              	<div style="text-align:center">
					<a href="convert"><button class="btn btn-info btn-lg ">Convertir un Excel en SKOS</button></a>         
              	</div>
              	<hr />
              	<p><span style="color: #38b44a;"><span class="glyphicon glyphicon glyphicon-certificate" aria-hidden="true"></span></span>&nbsp;Vous pouvez valider vos données SKOS en utilisant l'<strong><a href="http://labs.sparna.fr/skos-testing-tool">outil de test de vocabulaires SKOS</a></strong>.</p>
              	<p>L'outil de test de vocabulaires SKOS est une interface pour le validateur <a href="https://github.com/cmader/qSKOS" target="_blank">qSKOS</a> de Christian Mader.</p>
				<br />
              	<div style="text-align:center">
					<a href="http://labs.sparna.fr/skos-testing-tool"><button class="btn btn-success btn-lg ">SKOS Testing Tool</button></a>         
              	</div>
              	<br />
              	<br />
			</div>
      	</div>
      	<jsp:include page="footer.jsp" />
	</body>
</html>