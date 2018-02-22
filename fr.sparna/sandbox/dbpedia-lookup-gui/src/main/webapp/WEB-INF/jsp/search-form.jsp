<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<html>
  <head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<title>Search</title>
	<!-- bootstrap stuff -->
	<script src="js/jquery.min.js"></script>
	<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- end bootstrap stuff -->
    
    <link href="css/style.css" rel="stylesheet" />
  </head>
  <body style="margin-top: 30px;">

  	<div class="container-fluid  text-center">
	    <div class="row">
	    	<div class="col-md-6 col-md-offset-3">
		    	<form id="search" action="search.do" method="GET" autocomplete="off">
			  		<div>
					    <input
					    	 type="text"
					    	 id="search-box"
					    	 name="search-box"
					    	 class="form-control input-lg"
					    	 placeholder="search DBpedia..."
					    />				    
				    </div>
				    <div id="results"></div> 
		    	</form>
			</div>
		</div>	
  	</div>
  	
  	<script>
	  
	// focus on search input on page load
	$('#search-box').ready(function () {
		$('#search-box').focus();
	});
	  
	// arrow key navigation
	$(document).keydown(function(e){
	
	    // jump from search field to search results on keydown
	    if (e.keyCode == 40) { 
	        $("#search-box").blur();
	          return false;
	    }
	
	    // hide search results on ESC
	    if (e.keyCode == 27) { 
	        $("#results").hide();
	        $("#search-box").blur();
	          return false;
	    }
	
	    // focus on search field on back arrow or backspace press
	    if (e.keyCode == 37 || e.keyCode == 8) { 
	        $("#search-box").focus();
	    }
	
	});
	// end arrow key navigation

	function results( data ) {
		
		if(!data.result || data.result.length == 0) {
    		return;
    	}
		
		var html = "<ul>";
		$.each(data.result, function(index, r) {
			html += "<li>";
			html += "  <span class=\"result-label\"><a href=\""+r.uri+"\" target=\"_blank\">"+r.label+"</a></span>";
			html += "  <span class=\"result-refcount\">("+r.refcount+")</span>";
			html += "  <span class=\"result-description\">"+r.description+"</span>";
			
			/*
			html += "  <span class=\"classes\">";
			$.each(r.classes.clazz, function(index, aClass) {
				html += "  <span class=\"result-class\"><a href=\""+aClass.uri+"\" target=\"_blank\">"+aClass.label+"</span>";
			});			
			html += "  </span>";
			
			html += "  <span class=\"categories\">";
			$.each(r.categories.category, function(index, aCat) {
				html += "  <span class=\"result-category\"><a href=\""+aCat.uri+"\" target=\"_blank\">"+aCat.label+"</span>";
			});		
			html += "  </span>";
			*/
			
			html += "</li>";
		});
		html += "</ul>";
		
		console.log(html);
		$('#results').html(html);
		$('#results').show();
	}

	 $(document).ready(function() {

	    // post form on keydown or onclick, get results
	    $("#search-box").bind('keyup click', function() {
	    	// don't submit if 0 or 1 characters
	    	if($("#search-box").val().length > 1) {
		    	$.ajax({
		    	    type: 'GET',
		    	    url: "instantSearch.do?key="+$("#search-box").val(),
		    	    dataType: 'json',
		    	    success: function( data ) {
		    	    	console.log(JSON.stringify(data));
		    	    	
		    	    	results( data );
		    	    },
		    	    error: function(jqXHR, textStatus) {
		    	    	console.log(textStatus);
		    	    	// $('#modal-results-list').html("<em>Oups, an error happened !</em>");
		    	    },
		    	    async: true
		    	 }); // end ajax
	    	}
	    });
	    // end post form

	    // hide results when clicked outside of search field
	    $("body").click(function() {
	        $("#results").hide();
	    });
	    // end hide results

	});

	
	
  	</script>
  	
  </body>
</html>