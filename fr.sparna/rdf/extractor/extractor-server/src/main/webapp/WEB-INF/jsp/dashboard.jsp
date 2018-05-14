<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="data" value="${requestScope['fr.sparna.rdf.datapress.web.DashboardData']}" />

<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Datapress Dashboard</title>
  <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
  <script src="http://d3js.org/d3.v3.min.js"></script>
  <script src='js/d3sparql.js'></script>
  <script>
  
  function dashboard() {
    /* Uncomment to see debug information in console */
    d3sparql.debug = true
    var endpoint = "${data.endpoint}";
    
    // display repartition by domain
    d3sparql.query(
    		endpoint,
    		d3.select("#by-domain-sparql").property("value"),
	   		  function renderByDomain(json) {
	   		    var config = {
	   		      "label_x": "Domaine",
	   		      "label_y": "Nombre de pages",
	   		      "width":  700,  // canvas width
	   		      "height": 400,  // canvas height
	   		      "margin":  150,  // canvas margin
	   		      "selector": "#by-domain"
	   		    }
	
	   		    d3sparql.barchart(json, config)
	   		  }    
    );
    
    
    // display repartition by type
    d3sparql.query(
    		endpoint,
    		d3.select("#by-type-sparql").property("value"),
	   		  function renderByDomain(json) {
	   		    var config = {
	   		      "label_x": "Type",
	   		      "label_y": "Nombre d'instances",
	   		      "width":  700,  // canvas width
	   		      "height": 400,  // canvas height
	   		      "margin":  150,  // canvas margin
	   		      "selector": "#by-type"
	   		    }
	
	   		    d3sparql.barchart(json, config)
	   		  }    
    );
  }



  </script>
</head>
<body onload="dashboard()">

	<h1>DataPress Dashboard</h1>
	<div class="row">
		<div class="col-md-6">
			<h3>Pages with data by domain</h3>
			<form style="display: none">
				<textarea id="by-domain-sparql">
PREFIX s: <http://schema.org/>
PREFIX dcterms: <http://purl.org/dc/terms/>
select ?domain (COUNT(?page) AS ?count)
where {
	SELECT DISTINCT ?domain ?page
	WHERE {
		 ?page dcterms:isPartOf ?d .
		 BIND(STRAFTER(STR(?d), '://') AS ?domain)
		 GRAPH ?page {
		 	?s ?p ?o .
		 }
	}
}
GROUP BY ?domain
ORDER BY DESC(?count)</textarea>
			</form>
			<div id="by-domain"></div>
		</div>
		
		<div class="col-md-6">
			<h3>Repartition by entity types</h3>
			<form style="display: none">
				<textarea id="by-type-sparql">
PREFIX s: <http://schema.org/>
PREFIX dcterms: <http://purl.org/dc/terms/>
select ?labelType (COUNT(?s) AS ?count)
where {
	GRAPH ?page {
	  ?s a ?type
	}
	{
		{ ?type rdfs:label ?labelType }
		UNION
		{
		  ?type ?p ?o .
		  FILTER NOT EXISTS {
		  	?type rdfs:label ?l .
		  }
		  BIND(STRAFTER(STR(?type), '://') AS ?labelType)
		}
	}
}
GROUP BY ?labelType
ORDER BY DESC(?count)
LIMIT 20
</textarea>
			</form>
			<div id="by-type"></div>		
		</div>
	</div>


</body>