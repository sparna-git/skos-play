<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<html style="height:100%">
<head>

	<!-- JQuery and bootstrap stuff -->
	<script src="js/jquery-1.9.1.min.js" charset="utf-8"></script>
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <!-- end JQuery and bootstrap stuff -->

	<!--  JQuery UI stuff -->
	<link rel="stylesheet" href="jquery-ui-1.11.2.custom/jquery-ui.min.css">
	<script src="jquery-ui-1.11.2.custom/jquery-ui.min.js"></script>
	<!-- end JQuery UI stuff -->

	<style>
		#global-wrapper {
			margin-top:5%;
		}
	
		#autocomplete-wrapper {

		}

		#thesaurus-autocomplete {
			font-size:1.5em;
			padding: 0.3em 0.3em;
			line-height:1.5em;
		}
		
		#concept-display {
			margin-top: 1.5em;
		}
		
		.att {
			padding-left: 1em;
			font-size: 80%
		}
		
		.pref {
			font-weight: bold;
		}
		
		.alt {
			font-style:italic;
		}
	</style>

	<script>
		var params = ${items};
	
		var normalize = function(term) {
		    var r = term.toLowerCase();
		    non_asciis = {'a': '[àáâãäå]', 'ae': 'æ', 'c': 'ç', 'e': '[èéêë]', 'i': '[ìíîï]', 'n': 'ñ', 'o': '[òóôõö]', 'oe': 'œ', 'u': '[ùúûűü]', 'y': '[ýÿ]'};
		    for (i in non_asciis) { 
		    	r = r.replace(new RegExp(non_asciis[i], 'g'), i);
		    }
		    return r;
		};
		
		$(function() {
			
			$( "#thesaurus-autocomplete" ).autocomplete({
				minLength: 0,
				// search without accents
				// and limit to 20 results in the list
				// see http://stackoverflow.com/questions/7617373/limit-results-in-jquery-ui-autocomplete
				// and http://stackoverflow.com/questions/990904/javascript-remove-accents-diacritics-in-strings
				source: function(request, response) {
					var matcher = new RegExp( $.ui.autocomplete.escapeRegex( request.term ), "i" );
					
					response( $.grep( params.items, function( value ) {
						 value = value.label || value.value || value;
						 // test sur la chaine ou bien la chaine sans accents
						 return matcher.test( value ) || matcher.test( normalize( value ) );
					// le slice limite a 20
				 	}).slice(0, 20) );
			    },
				focus: function( event, ui ) {
					$( "#thesaurus-autocomplete" ).val( ui.item.label );
					return false;
				},
			
				select: function( event, ui ) {
					// put label in input box
					$( "#thesaurus-autocomplete" ).val( ui.item.label );
					// set selected uri in hidden field
					$( "#concept-uri" ).val( ui.item.uri );
					
					var labelToSearch = ui.item.label;
			        if(ui.item.pref) {
			          var labelToSearch = ui.item.pref;
			        }

					// build a display for the selected concept
					var html = ""+ labelToSearch + " (<a href=\""+ui.item.uri+"\" target=\"_blank\">"+ui.item.uri+"</a>)"+"";
					// set concept display
					$( "#concept-display" ).html( html );
			        
			        // search in iframe
			        var escapedLabel  = escape(labelToSearch);
			        var resultsUrl = "http://duckduckgo.com?q=" + labelToSearch + "";
			        console.log(resultsUrl);
			        // set iframe content
			        $('#results').attr('src', resultsUrl);
					
					return false;
				}
			})
			.autocomplete( "instance" )._renderItem = function( ul, item ) {
				
				var srchTerm = $.trim(this.term).split(/\s+/).join('|');
				// TODO : be able to correctly underline accented text when searching for non accents
		        var strNewLabel = item.label;
				regexp = new RegExp ('(' + srchTerm + ')', "ig");
				var strNewLabel = strNewLabel.replace(regexp,"<u>$1</u>");
				
				var html = "<li>";
				
				html += "<span class=\""+item.type+"\">"+strNewLabel+"</span>";
				
				html +="<div class=\"att\">";
				if(item.pref) {
					html += "EM : "+item.pref+"<br />";
				}
				if(item.definition) {
					html +=item.definition+"<br />";
				}
				if(item.scopeNote) {
					html +="<em>&#9733; "+item.scopeNote+"</em>";
				}
				html += "</div>";
				
				html += "</li>" ;
				
				return $(html).appendTo( ul );
			};
			
			// so that long definitions are wrapped cleanly
			$( "#thesaurus-autocomplete" ).autocomplete( "instance" )._resizeMenu = function() {
				this.menu.element.outerWidth( $( "#thesaurus-autocomplete" ).width() );
			};
			
		});
	</script>




</head>

<body style="height:100%">

	<div class="container">
		<div id="global-wrapper" class="col-sm-offset-1">
			<div id="autocomplete-wrapper" class="col-sm-12">
				<input id="thesaurus-autocomplete" class="col-sm-10" />
				<input type="hidden" id="concept-uri" />
			</div>
			<div class="col-sm-12">
				<p id="concept-display"></p>
			</div>
		</div>
	</div>
	
	<script>
	$(document).ready(function() {
		// set the placeholder on the field
		var placeholder = "<fmt:message key="viz.autocomplete.placeholder.begin" /> "+params.items.length+" <fmt:message key="viz.autocomplete.placeholder.end" />";
		if(params.thesaurusName && params.thesaurusName != '') {
			placeholder += " - "+params.thesaurusName;
		} else {
			placeholder += "...";
		}
		$('#thesaurus-autocomplete').attr("placeholder", placeholder);
		
		// focus on search field		
	    $('#thesaurus-autocomplete').focus();
  	});	
	</script>

	<iframe id="results" width="100%" height="100%" style="border:0px;margin-top:20px;">
	</iframe>

</body>

</html>