<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 		prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 		prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<c:set var="data" value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].printFormData}" />
<html>
	<head>
		<title>SKOS Play ! - Visualize SKOS Thesaurus</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
	</head>
	<body>
		<div class="container">
			<%-- see http://stackoverflow.com/questions/19150683/passing-parameters-to-another-jsp-file-in-jspinclude --%>
			<!-- no active params here -->
			<jsp:include page="header.jsp" />
			
			<div class="messages">
				<c:if test="${data.successMessage != null}">
					<div class="alert alert-success fade in">
						<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
						<h4><fmt:message key="success" /></h4>
						${data.successMessage}
					</div>
				</c:if>
				<c:forEach items="${data.warningMessages}" var="warningMessage">
					<div class="alert alert-warning fade in">
						<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
						<h4><fmt:message key="warning" /></h4>
						${warningMessage}
					</div>
				</c:forEach>
			</div>
			
<%-- 			<a href="#" style="cursor:default;"><h4><fmt:message key="print.form.legend" /></h4></a> --%>
			<form class="form-horizontal">
				<fieldset>
				<legend><fmt:message key="print.form.legend" /></legend>
				
				<div class="control-group">
					<label class="control-label"><fmt:message key="print.form.conceptScheme.legend" /></label>
					<div class="controls">
						<select name="scheme" id="scheme" class="span5">
						<c:choose>
							<c:when test="${empty data.conceptCountByConceptSchemes}">
								<option value="no-scheme"><fmt:message key="print.form.conceptScheme.noConceptSchemeFound" /></option>
							</c:when>
							<c:otherwise>
								<c:forEach items="${data.conceptCountByConceptSchemes}" var="entry">
									<option value="${entry.key.uri}">${entry.key.label} (${entry.value} concepts)</option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><fmt:message key="print.form.language.legend" /></label>
					<div class="controls">
						<select id="language" name="language" class="span2">
						<c:choose>
							<c:when test="${empty data.languages}">
								<option value="no-language"><fmt:message key="print.form.language.noLanguageFound" /></option>
							</c:when>
							<c:otherwise>
								<c:forEach items="${data.languages}" var="entry">
									<option value="${entry.key}">${entry.key} - ${entry.value}</option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
						</select>
					</div>
				</div>
				</fieldset>
			</form><!-- end concept scheme and language selection -->
			

		    <div class="accordion" id="accordion2">
    			<div class="accordion-group">
    				<div class="accordion-heading">
    					<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne"><h4><fmt:message key="print.form.section.print" /></h4></a>
    				</div>
    				<div id="collapseOne" class="accordion-body collapse in"><div class="accordion-inner">
    				
    				<form id="print_form" action="print" method="post" enctype="multipart/form-data" class="form-horizontal">
    					<input type="hidden" name="scheme"></input>
    					<input type="hidden" name="language"></input>
    				
	    				<div class="control-group">
						<label class="control-label"><fmt:message key="print.form.displayType.legend" /></label>
						<div class="controls">
							<label class="radio">
								<input type="radio" name="display" id="conceptListing" value="conceptListing">
								<fmt:message key="print.form.displayType.conceptListing" />
								<span class="help-inline"><fmt:message key="print.form.displayType.conceptListing.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="translation_table" value="translation_table">
								<fmt:message key="print.form.displayType.translation_table" />
								<span class="help-inline"><fmt:message key="print.form.displayType.translation_table.help" /></span>
							</label>
							<div style="margin-left:30px; font-size:smaller;">
								<fmt:message key="print.form.displayType.translation_table.targetLanguage" /> : <select id="targetLanguage" name="targetLanguage" class="span2">
								<c:choose>
									<c:when test="${empty data.languages}">
										<option value="no-language"><fmt:message key="print.form.language.noLanguageFound" /></option>
									</c:when>
									<c:otherwise>
										<c:forEach items="${data.languages}" var="entry">
											<option value="${entry.key}">${entry.key} - ${entry.value}</option>
										</c:forEach>
									</c:otherwise>
								</c:choose>
								</select>
							</div>
							<label class="radio">
								<input type="radio" name="display" id="alphabetical" value="alphabetical">
								<fmt:message key="print.form.displayType.alphabetical" />
								<span class="help-inline"><fmt:message key="print.form.displayType.alphabetical.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="alphabetical_expanded" value="alphabetical_expanded" checked>
								<fmt:message key="print.form.displayType.alphabetical_expanded" />
								<span class="help-inline"><fmt:message key="print.form.displayType.alphabetical_expanded.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="hierarchical" value="hierarchical" hierarchical="true">
								<fmt:message key="print.form.displayType.hierarchical" />
								<span class="help-inline"><fmt:message key="print.form.displayType.hierarchical.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="complete_monolingual" value="complete_monolingual" hierarchical="true">
								<fmt:message key="print.form.displayType.complete_monolingual" />
								<span class="help-inline"><fmt:message key="print.form.displayType.complete_monolingual.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="complete_multilingual" value="complete_multilingual" hierarchical="true">
								<fmt:message key="print.form.displayType.complete_multilingual" />
								<span class="help-inline"><fmt:message key="print.form.displayType.complete_multilingual.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="permuted_index" value="permuted_index">
								<fmt:message key="print.form.displayType.permuted_index" />
								<span class="help-inline"><fmt:message key="print.form.displayType.permuted_index.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="kwic_index" value="kwic_index">
								<fmt:message key="print.form.displayType.kwic_index" />
								<span class="help-inline"><fmt:message key="print.form.displayType.kwic_index.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="alignment_by_scheme" value="alignment_by_scheme">
								<fmt:message key="print.form.displayType.alignment_by_scheme" />
								<span class="help-inline"><fmt:message key="print.form.displayType.alignment_by_scheme.help" /></span>
							</label>
							<label class="radio">
								<input type="radio" name="display" id="alignment_alpha" value="alignment_alpha">
								<fmt:message key="print.form.displayType.alignment_alpha" />
								<span class="help-inline"><fmt:message key="print.form.displayType.alignment_alpha.help" /></span>
							</label>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><fmt:message key="print.form.outputFormat.legend" /></label>
						<div class="controls">
							<label class="radio">
								<input type="radio" name="output" id="html" value="html" checked>
								<fmt:message key="print.form.outputFormat.html" />
							</label>
							<label class="radio">
								<input type="radio" name="output" id="pdf" value="pdf">
								<fmt:message key="print.form.outputFormat.pdf" />
							</label>
						</div>
					</div>
					<div class="form-actions">
						<script>document.write('<a href="'+document.referrer+'"><button id="previous-button-print" class="btn btn-large" type="button"><fmt:message key="previous" /></button></a>');</script>
						<button id="submit-button-print" type="submit" class="btn btn-large btn-primary"><fmt:message key="print.form.print" /></button>
						<img src="images/ajax-loader.gif" id="loading-print" hidden="hidden" />
					</div>   				
    				
    				</form>    				
    				
    				</div></div>
    			</div><!-- end accordion-group : Print -->
    			<div class="accordion-group">
    				<div class="accordion-heading">
    					<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo"><h4><fmt:message key="print.form.section.visualize" /></h4></a>
    				</div>
    				<div id="collapseTwo" class="accordion-body collapse"><div class="accordion-inner">

						<form id="viz_form" action="visualize" method="post" enctype="multipart/form-data" class="form-horizontal">
							<input type="hidden" name="scheme"></input>
    						<input type="hidden" name="language"></input>
							<input type="hidden" name="display"></input>

							<div class="control-group">
								<label class="control-label"><fmt:message key="print.form.displayType.legend" /></label>
								<div class="controls">
									<label class="radio">
										<input type="radio" name="viztype" id="viz-treelayout" value="treelayout" hierarchical="true" checked>
										<fmt:message key="print.form.displayType.treelayout" />
										<span class="help-inline"><fmt:message key="print.form.displayType.treelayout.help" /></span>
									</label>
									<label class="radio">
										<input type="radio" name="viztype" id="viz-partition" value="partition" hierarchical="true">
										<fmt:message key="print.form.displayType.partition" />
										<span class="help-inline"><fmt:message key="print.form.displayType.partition.help" /></span>
									</label>
									<label class="radio">
										<input type="radio" name="viztype" id="viz-sunburst" value="sunburst" hierarchical="true">
										<fmt:message key="print.form.displayType.sunburst" />
										<span class="help-inline"><fmt:message key="print.form.displayType.sunburst.help" /></span>
									</label>
									<label class="radio">
										<input type="radio" name="viztype" id="viz-autocomplete" value="autocomplete">
										<fmt:message key="print.form.displayType.autocomplete" />
										<span class="help-inline"><fmt:message key="print.form.displayType.autocomplete.help" /></span>
									</label>
								</div>
							</div>

							<div class="form-actions">
								<script>document.write('<a href="'+document.referrer+'"><button id="previous-button-viz" class="btn btn-large" type="button"><fmt:message key="previous" /></button></a>');</script>
								<button id="submit-button-viz" type="submit" class="btn btn-large btn-primary"><fmt:message key="print.form.visualize" /></button>
								<img src="images/ajax-loader.gif" id="loading-viz" hidden="hidden" />
							</div>

						</form>
						
    				</div></div>
    			</div><!-- end accordion-group : Visualise -->
    		</div><!-- end accordion -->
			
      	</div>
      	<jsp:include page="footer.jsp" />
      	<script>
		
		$(document).ready(function () {
			// disable concept scheme selection if not available
	        $('#scheme:has(option[value="no-scheme"])').attr('disabled', 'disabled');

	     	// disable hierarchical fields if not available
			<c:if test="${!data.enableHierarchical}">				
				$(':radio[hierarchical="true"]').attr('disabled', 'disabled');
				$('#submit-button-viz').attr('disabled', 'disabled');
			</c:if>
			
			// disable translation table if needed
			<c:if test="${!data.enableTranslations}">				
				$('#translation_table').attr('disabled', 'disabled');
				$('#complete_multilingual').attr('disabled', 'disabled');
			</c:if>
			// always disable target language - will be re-enabled only if translation_table can be selected
			$('#targetLanguage').attr('disabled', 'disabled');
			
			// disable mappings if needed
			<c:if test="${!data.enableMappings}">				
				$('#alignment_by_scheme').attr('disabled', 'disabled');
				$('#alignment_alpha').attr('disabled', 'disabled');
			</c:if>
			
			
			$(':radio[name="display"]').click(function() {				
				// enable-disable target language selection based on display selection
				<c:if test="${data.enableTranslations}">	
					if(this.id.indexOf('translation_table') == 0) {
						$('#targetLanguage').removeAttr('disabled');
					} else {
						$('#targetLanguage').attr('disabled', 'disabled');
					}
				</c:if>
			});
			
	     			
		    $('#print_form, #viz_form').submit(function() {
		    	// copy scheme and language selection into hidden fields
		    	$('input[name="scheme"]').val($('#scheme').val());
		    	$('input[name="language"]').val($('#language').val());
		    	
			    // copy viztype into a field named display for compatibility
			    $('input[name="display"][type="hidden"]').val($("input:radio[name='viztype']:checked" ).val());
		    	
		    	// disable submit buttons on click
		    	$('#submit-button-print, #submit-button-viz').attr('disabled', true);
		    	$('#previous-button-print, #previous-button-viz').attr('disabled', true);
		        $('#loading-print, #loading-viz').show();
		    });
			
			// re-enable buttons for click on browser back
			$(window).unload(function() {
	      		$('#loading-print, #loading-hide').hide();
			    $('#submit-button-print, #submit-button-viz').attr('disabled', false);
			    $('#previous-button-print, #previous-button-viz').attr('disabled', false);
			    $('#targetLanguage').attr('disabled', false);
	      	});

	    });
		</script>
	</body>
</html>