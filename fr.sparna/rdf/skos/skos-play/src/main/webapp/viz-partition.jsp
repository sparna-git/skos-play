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
    <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
    <script src="http://code.jquery.com/jquery-1.9.1.min.js" charset="utf-8"></script>
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <style type="text/css">

.chart {
  display: block;
  margin: auto;
  margin-top: 10px;
  font-size: 11px;
}

rect {
  stroke: #eee;
  fill: #aaa;
  fill-opacity: .8;
}

rect.parent {
  cursor: pointer;
  fill: steelblue;
}

text {
  font-size: 14px;
  text-decoration:underline;
  cursor: pointer;
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

var w = 1120,
    h = 600,
    x = d3.scale.linear().range([0, w]),
    y = d3.scale.linear().range([0, h]);

var vis = d3.select("#body").append("div")
    .attr("class", "chart")
    .style("width", w + "px")
    .style("height", h + "px")
  .append("svg:svg")
    .attr("width", w)
    .attr("height", h);

var partition = d3.layout.partition()
    .value(function(d) { return d.size; });

var dataset = '${dataset}';
var json = JSON.parse( dataset );

// d3.json("json?language=${language}&root=${root}", function(root) {
  root = json;
  
  var g = vis.selectAll("g")
      .data(partition.nodes(root))
      .enter().append("svg:g")
      .attr("transform", function(d) { return "translate(" + x(d.y) + "," + y(d.x) + ")"; })
      .on("click", click);

  var kx = w / root.dx,
      ky = h / 1;

  g.append("svg:rect")
      .attr("width", root.dy * kx)
      .attr("height", function(d) { return d.dx * ky; })
      .attr("class", function(d) { return d.children ? "parent" : "child"; });
  
  // add a link to the concept
  var a = g.append("a")
	  .attr("xlink:href", function(d){ return d.uri; })
	  .attr("target", "_blank");
  
  // inside the link, put the text
  a.append("svg:text")
		.attr("transform", transform)
		// position verticale a partir du haut (?)
		.attr("dy", ".35em")
		.style("opacity", function(d) { return d.dx * ky > 12 ? 1 : 0; })
		.text(function(d) { return d.name; })

  d3.select(window)
      .on("click", function() { click(root); })

  function click(d) {
    if (!d.children) return;

    kx = (d.y ? w - 40 : w) / (1 - d.y);
    ky = h / d.dx;
    x.domain([d.y, 1]).range([d.y ? 40 : 0, w]);
    y.domain([d.x, d.x + d.dx]);

    var t = g.transition()
        .duration(d3.event.altKey ? 7500 : 750)
        .attr("transform", function(d) { return "translate(" + x(d.y) + "," + y(d.x) + ")"; });

    t.select("rect")
        .attr("width", d.dy * kx)
        .attr("height", function(d) { return d.dx * ky; });

    t.select("text")
        .attr("transform", transform)
        .style("opacity", function(d) { return d.dx * ky > 12 ? 1 : 0; });

    d3.event.stopPropagation();
  }

  function transform(d) {
    return "translate(8," + d.dx * ky / 2 + ")";
  }
// });

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
              content: '<fmt:message key="viz.partition.help.content" />',
              placement: "bottom"
          });
          $('#help-popover').css("text-decoration", "underline").css("cursor", "pointer");
        });         
    </script>
  </body>
</html>
