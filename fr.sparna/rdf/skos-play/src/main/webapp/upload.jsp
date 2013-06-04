<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language of the request -->
<fmt:setLocale value="${pageContext.request.locale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<c:set var="data" value="${requestScope['fr.sparna.rdf.skosplay.UploadFormData']}" />
<html>
	<head>
		<title>SKOS Play ! - Visualize SKOS data</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>

		<script type="text/javascript">
	
			function enabledInput(selected) {
				document.getElementById('source-' + selected).checked = true;
				// document.getElementById('url').disabled = selected != 'url';
				// document.getElementById('example').disabled = selected != 'example';
				// document.getElementById('file').disabled = selected != 'file';				
			}	
			
	    </script>


	</head>
	<body>
		<div class="container">
			<div class="page-header">
				<div class="row">
			    	<div class="span6"><h1>SKOS Play !</h1></div>
			    	<div class="span6">
			    		<ul class="nav nav-pills pull-right">
					    	<li><a href="home"><fmt:message key="menu.home" /></a></li>
					    	<li class="active"><a href="upload.jsp"><fmt:message key="menu.start" /></a></li>
					    	<li><a href="about"><fmt:message key="menu.about" /></a></li>
					    	<li><a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank"><fmt:message key="menu.feedback" /></a></li>
					    </ul>
					</div>
			    </div>	      		
	      	</div>
			<div class="messages">
				<c:if test="${data.errorMessage != null}">
					<div class="alert alert-error">
						<h4><fmt:message key="error" /></h4>
						${data.errorMessage}
					</div>
				</c:if>
			</div>			
			<form id="upload_form" action="upload" method="post" enctype="multipart/form-data">
			<fieldset>
				<legend><fmt:message key="upload.form.legend" /></legend>
				<table>
					<tr>
						<td></td>
						<td>
							<input
								type="radio"
								name="source"
								id="source-example"
								value="example"
								onchange="enabledInput('example')"
								checked="checked" />
							<fmt:message key="upload.form.providedExample" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<select
								class="span4"
								name="example"
								id="example"
								onchange="enabledInput('example')">
								<option value="data/unesco/unescothes.ttl"><fmt:message key="upload.form.providedExample.unesco" /></option>
								<option value="data/w/matieres.rdf"><fmt:message key="upload.form.providedExample.w" /></option>
								<option value="data/nyt/nyt-descriptors.ttl"><fmt:message key="upload.form.providedExample.nyt" /></option>
							</select>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<input
								type="radio"
								name="source"
								id="source-file"
								value="file"
								onchange="enabledInput('file')" />
							<fmt:message key="upload.form.localFile" />
					</tr>
					<tr>
						<td></td>
						<td>
							<div class="fileupload fileupload-new" data-provides="fileupload">
								<div class="input-append">
									<div class="uneditable-input span4">
										<i class="icon-file fileupload-exists"></i> <span class="fileupload-preview"></span>
									</div>
									<span class="btn btn-file">
										<span class="fileupload-new"><fmt:message key="upload.form.localFile.select" /></span>
										<span class="fileupload-exists"><fmt:message key="upload.form.localFile.change" /></span>
										<input type="file" name="file" id="file" onchange="enabledInput('file')" />
									</span>
									<a href="#" class="btn fileupload-exists" data-dismiss="fileupload"><fmt:message key="upload.form.localFile.remove" /></a>
								</div>
							</div>
							<span class="help-block"><i><fmt:message key="upload.form.localFile.help" /></i></span>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<input
								type="radio"
								name="source"
								id="source-url"
								value="url"
								onchange="enabledInput('url')" />
							<fmt:message key="upload.form.remoteUrl" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<input
								type="text"
								id="url"
								name="url"
								value=""
								onkeypress="enabledInput('url');" />
							<span class="help-block"><i><fmt:message key="upload.form.remoteUrl.help" /></i></span>
						</td>
					</tr>
					<!-- 
					<tr>
						<td></td>
						<td>
							<input
								type="radio"
								name="source"
								id="source-endpoint"
								value="endpoint"
								onchange="enabledInput('endpoint')" />
							<fmt:message key="upload.form.endpoint" />
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<input
								type="text"
								id="endpoint"
								name="endpoint"
								value=""
								onkeypress="enabledInput('endpoint');" />
							<span class="help-block"><i><fmt:message key="upload.form.endpoint.help" /></i></span>
						</td>
					</tr>
					 -->
				</table>
				<div class="form-actions">
					<button type="submit" id="submit-button" class="btn btn-large btn-success"><fmt:message key="next" /></button>
					<img src="images/ajax-loader.gif" id="loading" hidden="hidden" />
				</div>
			</fieldset>
			</form>
      	</div>
      	<footer>
      	SKOS Play! by <a href="http://francart.fr" target="_blank">Thomas Francart</a> for <a href="http://sparna.fr" target="_blank">Sparna</a>
      	&nbsp;|&nbsp;
      	<a rel="license" href="http://creativecommons.org/licenses/by-sa/3.0/deed.fr"><img alt="Licence Creative Commons" style="border-width:0" src="http://i.creativecommons.org/l/by-sa/3.0/88x31.png" /></a>
      	</footer>
      	<script>
	      	$(document).ready(function() {
				// disable submit button on click		
			    $('#upload_form').submit(function() {
			    	$('#submit-button').attr('disabled', true);
			        $('#loading').show();
			    });
				
		      	$(window).unload(function() {
		      		$('#loading').hide();
				    $('#submit-button').attr('disabled', false);
		      	});
	      	});
	      	

		</script>
	</body>
</html>