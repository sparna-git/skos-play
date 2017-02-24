<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="fr.sparna.rdf.skosplay.SessionData" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<c:set var="data" value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].user}" />
<c:set var="applicationData" value="${applicationScope.applicationData}" />

<html>
	<head>
		
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
		
			<c:if test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].user!= null}">
					<div class="alert alert-success">
						<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
						${sessionScope['fr.sparna.rdf.skosplay.SessionData'].user.name}
					</div>
			</c:if>
			<c:if test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].user== null}">
			<!-- https://www.googleapis.com/auth/spreadsheets.readonly -->
					<a href="https://accounts.google.com/o/oauth2/auth?scope=profile%20https://www.googleapis.com/auth/drive&redirect_uri=http://localhost:8080/skos-play/login&response_type=code&client_id=611030822832-ea9cimuarqabdaof7e1munk90hr67mlo.apps.googleusercontent.com&approval_prompt=force"><button type ="submit">LOGIN</button></a>
			</c:if>
			
			
		 
			
      	</div>
      	
	</body>
</html>