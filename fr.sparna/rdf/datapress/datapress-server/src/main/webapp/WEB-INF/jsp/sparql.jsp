<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="data" value="${requestScope['fr.sparna.rdf.datapress.web.SparqlPageData']}" />

<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Datapress SPARQL</title>
  <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
  <link href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)' rel='stylesheet' type='text/css'/>
</head>
<body>

  <div class="container"> 
  	<div id="yasqe"></div>
  	<div id="yasr"></div>
  </div>
  
  <script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
  <script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>
  <script type="application/javascript">
	  var yasqe = YASQE(document.getElementById("yasqe"), {
			sparql: {
				showQueryButton: true,
				endpoint: "${data.endpoint}"
			}
		});
		var yasr = YASR(document.getElementById("yasr"), {
			//this way, the URLs in the results are prettified using the defined prefixes in the query
			getUsedPrefixes: yasqe.getPrefixesFromQuery
		});
	
		//link both together
		yasqe.options.sparql.callbacks.complete = yasr.setResponse;  
  </script>
</body>