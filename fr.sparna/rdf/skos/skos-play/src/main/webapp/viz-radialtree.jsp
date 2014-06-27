<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <script src="js/d3.v3.min.js" charset="utf-8"></script>
    <script src="js/jquery-1.9.1.min.js" charset="utf-8"></script>
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <script src="bootstrap/js/bootstrap.min.js"></script>
    
    <style type="text/css">

.node circle {
  cursor: pointer;
  fill: #fff;
  stroke: steelblue;
  stroke-width: 1.5px;
}

.node text {
  font-size: 12px;
}

path.link {
  fill: none;
  stroke: #aaa;
  stroke-width: 3px;
}

.ext-link {
  font-weight: bold;
}

    </style>
  </head>
  <body>
    <div id="header" style="text-align:center; font-size: 0.9em;">
  		<span id="help-popover"><i class="icon-info-sign"></i><fmt:message key="viz.help.label" /></span>
  	</div>
    <div id="body">
    </div>
    <script type="text/javascript">

    var diameter = 960;

    var tree = d3.layout.tree()
        .size([360, diameter / 2 - 120])
        .separation(function(a, b) { return (a.parent == b.parent ? 1 : 2) / a.depth; });

    var diagonal = d3.svg.diagonal.radial()
        .projection(function(d) { return [d.y, d.x / 180 * Math.PI]; });

    var vis = d3.select("#body").append("svg:svg")
        .attr("width", diameter)
        .attr("height", diameter - 150)
        .append("svg:g")
        .attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");

    var dataset = '${dataset}';
    var json = JSON.parse( dataset );
    root = json;
    
    var nodes = tree.nodes(root),
        links = tree.links(nodes);

    var link = vis.selectAll(".link")
        .data(links)
        .enter().append("path")
        .attr("class", "link")
        .attr("d", diagonal);

    var node = vis.selectAll(".node")
        .data(nodes)
        .enter().append("g")
          .attr("class", "node")
          .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })

    node.append("circle")
        .attr("r", 4.5);

    node.append("text")
        .attr("dy", ".31em")
        .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
        .attr("transform", function(d) { return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)"; })
        .text(function(d) { return (d.name != null)?d.name:d.uri ; });

    d3.select(self.frameElement).style("height", diameter - 150 + "px");    

    </script>
    
    <script>
        $(document).ready(function () {
          // add external link behavior to every external link
          $('text').mouseover(function() {
            $(this).attr("class", "ext-link");
          });
          $('text').mouseout(function() {
            $(this).attr("class", "");
          });
          
          $('#help-popover').popover({
        	  html: true,
              trigger : "click",
              delay: { show: 0, hide: 400 },
              content: '<fmt:message key="viz.treelayout.help.content" />',
              placement: "bottom"
          });
          $('#help-popover').css("text-decoration", "underline").css("cursor", "pointer");
        });         
    </script>
  </body>
</html>