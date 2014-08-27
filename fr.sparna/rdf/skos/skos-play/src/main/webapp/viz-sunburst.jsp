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

		g.ext-link {
		  font-weight: bold;
		}

    </style>
  </head>
  <body>
    <div id="header" style="text-align:center; font-size: 0.9em;">
  		<span id="help-popover"><i class="icon-info-sign"></i><fmt:message key="viz.help.label" /></span>
  		<!-- uncomment for count/size switch -->
  		<!-- 
  		<form>
		  <label><input type="radio" name="mode" value="size"> Size</label>
		  <label><input type="radio" name="mode" value="count" checked> Count</label>
		</form>
		-->
  	</div>
    <div id="body">
    </div>
    <script type="text/javascript">

    var width = 960,
    height = 700,
    radius = Math.min(width, height) / 2;

    // necessary for switch
    // Keep track of the node that is currently being displayed as the root.
    var currentRoot;
    
	var x = d3.scale.linear()
	    .range([0, 2 * Math.PI]);
	
	var y = d3.scale.linear()
	    .range([0, radius]);
	
	var color = d3.scale.category20c();
	
	var svg = d3.select("body").append("svg")
	    .attr("width", width)
	    .attr("height", height)
	    .append("g")
	    .attr("transform", "translate(" + width / 2 + "," + (height / 2 + 10) + ")");
	
	var partition = d3.layout.partition()
	    .value(function(d) { return d.size; });
	
	var arc = d3.svg.arc()
	    .startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
	    .endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
	    .innerRadius(function(d) { return Math.max(0, y(d.y)); })
	    .outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });
	
	  var dataset = '${dataset}';
	  var json = JSON.parse( dataset );
	  root = json;
	  // necessary for switch
	  currentRoot = root;
	  
	// d3.json("flare.json", function(error, root) {
	  var g = svg.selectAll("g")
	      .data(partition.nodes(root))
	      .enter().append("g");
	
	  var path = g.append("path")
	    .attr("d", arc)
	    .style("fill", function(d) {
	    	return color((d.children ? d : d.parent).name); 
	    })
	    .on("click", click);
	  
	  // uncomment and comment above to have count/size switch working
// 	  var path = svg.datum(root).selectAll("path")
//       	.data(partition.nodes)
//    		.enter().append("path")
//       	.attr("d", arc)
//       	.style("fill", function(d) { return color((d.children ? d : d.parent).name); })
//       	.on("click", click)
//       	.each(stash);

	  // add a link to the concept
	  var a = g.append("a")
		  .attr("xlink:href", function(d) { return d.uri; })
		  .attr("target", "_blank");	  
	  
	  var text = a.append("text")
	    .attr("transform", function(d) { return "rotate(" + computeTextRotation(d) + ")"; })
	    .attr("x", function(d) { return y(d.y); })
	    .attr("dx", "6") // margin
	    .attr("dy", ".35em") // vertical-align
	    .text(function(d) { return d.name; });
	
	  function click(d) {
		// necessary for switch
		currentRoot = d;
		  
	    // fade out all text elements
	    text.transition().attr("opacity", 0);
	
	    path.transition()
	      .duration(750)
	      .attrTween("d", arcTween(d))
	      .each("end", function(e, i) {
	          // check if the animated element's data e lies within the visible angle span given in d
	          if (e.x >= d.x && e.x < (d.x + d.dx)) {
	            // get a selection of the associated text element
	            var arcText = d3.select(this.parentNode).select("text");
	            
	            // fade in the text element and recalculate positions
	            arcText.transition().duration(750)
	              .attr("opacity", 1)
	              .attr("transform", function() { return "rotate(" + computeTextRotation(e) + ")" })
	              .attr("x", function(d) { return y(d.y); });
	          }
	      });
	  }
//	});
	
	d3.select(self.frameElement).style("height", height + "px");
	
	// Interpolate the scales!
	function arcTween(d) {
	  var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
	      yd = d3.interpolate(y.domain(), [d.y, 1]),
	      yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
	  return function(d, i) {
	    return i
	        ? function(t) { return arc(d); }
	        : function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
	  };
	}
	
	function computeTextRotation(d) {
	  return (x(d.x + d.dx / 2) - Math.PI / 2) / Math.PI * 180;
	}
	
	// uncomment to have size/count switch
	// switch between size and number of children
// 	d3.selectAll("input").on("change", function change() {
// 		    var value = this.value === "count"
// 		        ? function() { return 1; }
// 		        : function(d) { console.log(d.size); return d.size; };
		    
// 		    console.log("change");
// 		    console.log(path.data(partition.value(value).nodes));
// 		    console.log("change2");
// 		    path.data(partition.value(value).nodes)
// 		        .transition()
// 		        .duration(1000)
// 		        .attrTween("d", arcTweenData);
// 	});

	// uncomment to have size/count switch
// 	// Setup for switching data: stash the old values for transition.
// 	function stash(d) {
// 	  console.log("stash");
// 	  d.x0 = d.x;
// 	  d.dx0 = d.dx;
// 	}
	
	
// 	// When switching data: interpolate the arcs in data space.
// 	function arcTweenData(a, i) {
// 		console.log("arcTweenData");
//       var oi = d3.interpolate({x: a.x0, dx: a.dx0}, a);
// 	  function tween(t) {
// 	    var b = oi(t);
// 	    a.x0 = b.x;
// 	    a.dx0 = b.dx;
// 	    return arc(b);
// 	  }
// 	  if (i == 0) {
// 	   // If we are on the first arc, adjust the x domain to match the root node
// 	   // at the current zoom level. (We only need to do this once.)
// 	    var xd = d3.interpolate(x.domain(), [currentRoot.x, currentRoot.x + currentRoot.dx]);
// 	    return function(t) {
// 	      x.domain(xd(t));
// 	      return tween(t);
// 	    };
// 	  } else {
// 	    return tween;
// 	  }
// 	}
	
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
              content: '<fmt:message key="viz.sunburst.help.content" />',
              placement: "bottom"
          });
          $('#help-popover').css("text-decoration", "underline").css("cursor", "pointer");
        });         
    </script>
  </body>
</html>