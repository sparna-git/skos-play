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
			<div class="page-header">
				<div class="row">
			    	<div class="span6"><h1>SKOS Play !</h1></div>
			    	<div class="span6">
			    		<ul class="nav nav-pills pull-right">
					    	<li><a href="home"><fmt:message key="menu.home" /></a></li>
					    	<li><a href="upload.jsp"><fmt:message key="menu.start" /></a></li>
					    	<li><a href="about"><fmt:message key="menu.about" /></a></li>
					    	<li><a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank"><fmt:message key="menu.feedback" /></a></li>
					    </ul>
					</div>
			    </div>	      		
	      	</div>
			<div class="messages">
				<c:forEach items="${data.warningMessages}" var="warningMessage">
					<div class="alert alert-warning">
						<h4><fmt:message key="warning" /></h4>
						${warningMessage}
					</div>
				</c:forEach>
				<c:if test="${data.successMessage != null}">
					<div class="alert alert-success">
						<h4><fmt:message key="success" /></h4>
						${data.successMessage}
					</div>
				</c:if>
			</div>
			<form id="print_form" action="print" method="post" enctype="multipart/form-data" class="form-horizontal">
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
						<select name="language" class="span2">
						<c:choose>
							<c:when test="${empty data.languages}">
								<option value="no-language"><fmt:message key="print.form.language.noLanguageFound" /></option>
							</c:when>
							<c:otherwise>
								<c:forEach items="${data.languages}" var="lang">
									<option value="${lang}">${lang}</option>
								</c:forEach>
							</c:otherwise>
						</c:choose>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><fmt:message key="print.form.displayType.legend" /></label>
					<div class="controls">
						<label class="radio">
							<input type="radio" name="display" id="conceptListing" value="conceptListing" checked>
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
									<c:forEach items="${data.languages}" var="lang">
										<option value="${lang}">${lang}</option>
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
							<input type="radio" name="display" id="alphabetical_expanded" value="alphabetical_expanded">
							<fmt:message key="print.form.displayType.alphabetical_expanded" />
							<span class="help-inline"><fmt:message key="print.form.displayType.alphabetical_expanded.help" /></span>
						</label>
						<label class="radio">
							<input type="radio" name="display" id="hierarchical" value="hierarchical" hierarchical="true">
							<fmt:message key="print.form.displayType.hierarchical" />
							<span class="help-inline"><fmt:message key="print.form.displayType.hierarchical.help" /></span>
						</label>
						<label class="radio">
							<input type="radio" name="display" id="hierarchical_expanded" value="hierarchical_expanded" hierarchical="true">
							<fmt:message key="print.form.displayType.hierarchical_expanded" />
							<span class="help-inline"><fmt:message key="print.form.displayType.hierarchical_expanded.help" /></span>
						</label>
						<label class="radio">
							<input type="radio" name="display" id="viz-partition" value="partition" hierarchical="true">
							<fmt:message key="print.form.displayType.partition" />
							<span class="help-inline"><fmt:message key="print.form.displayType.partition.help" /></span>
						</label>
						<label class="radio">
							<input type="radio" name="display" id="viz-treelayout" value="treelayout" hierarchical="true">
							<fmt:message key="print.form.displayType.treelayout" />
							<span class="help-inline"><fmt:message key="print.form.displayType.treelayout.help" /></span>
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
					<script>document.write('<a href="'+document.referrer+'"><button id="previous-button" class="btn btn-large" type="button"><fmt:message key="previous" /></button></a>');</script>
					<button id="submit-button" type="submit" class="btn btn-large btn-success"><fmt:message key="print.form.generate" /></button>
					<img src="images/ajax-loader.gif" id="loading" hidden="hidden" />
				</div>
			</fieldset>
			</form>
      	</div>
      	<jsp:include page="footer.jsp" />
      	<script>

		
		$(document).ready(function () {
			// disable concept scheme selection if not available
	        $('#scheme:has(option[value="no-scheme"])').attr('disabled', 'disabled');

	     	// disable hierarchical fields if not available
			<c:if test="${!data.enableHierarchical}">				
				$(':radio[hierarchical="true"]').attr('disabled', 'disabled');
			</c:if>
			
			// disable translation table if needed
			<c:if test="${!data.enableTranslations}">				
				$('#translation_table').attr('disabled', 'disabled');
			</c:if>
			// always disable target language - will be re-enabled only if translation_table can be selected
			$('#targetLanguage').attr('disabled', 'disabled');
			
			
			$(':radio[name="display"]').click(function() {
				// enable-disable output formats based on display type selection
				if(this.id.indexOf('viz-') == 0) {
					$(':radio[name="output"]').attr('disabled', 'disabled');
				} else {
					$(':radio[name="output"]').removeAttr('disabled');
				}
				
				// enable-disable target language selection base on display selection
				<c:if test="${data.enableTranslations}">	
					if(this.id.indexOf('translation_table') == 0) {
						$('#targetLanguage').removeAttr('disabled');
					} else {
						$('#targetLanguage').attr('disabled', 'disabled');
					}
				</c:if>
			});
			
	     	// disable submit button on click		
		    $('#print_form').submit(function() {
		    	$('#submit-button').attr('disabled', true);
		    	$('#previous-button').attr('disabled', true);
		        $('#loading').show();
		    });
			
			// re-enable buttons for click on browser back
			$(window).unload(function() {
	      		$('#loading').hide();
			    $('#submit-button').attr('disabled', false);
			    $('#previous-button').attr('disabled', false);
			    $('#targetLanguage').attr('disabled', false);
	      	});

	    });
		</script>
	</body>
</html>