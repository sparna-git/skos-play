<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<c:set var="data" value="${requestScope['fr.sparna.rdf.skosplay.ConvertFromData']}" />
<c:set var="applicationData" value="${applicationScope.applicationData}" />

<html>
	<head>
		<title><c:out value="${applicationData.skosPlayConfig.applicationTitle}" /></title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/jasny-bootstrap.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<link href="style/custom.css" rel="stylesheet" />
		
		<link rel="stylesheet" href="css/editable-select.css">
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/jasny-bootstrap.min.js"></script>

		<script type="text/javascript">
	
			function enabledInput(selected) {
				document.getElementById('source-' + selected).checked = true;
				document.getElementById('url').disabled = selected != 'url';
				document.getElementById('example').disabled = selected != 'example';
				document.getElementById('file').disabled = selected != 'file';
				document.getElementById('google').disabled = selected != 'google';
				if((selected!='google')||(selected!='url'))
					{
					 document.formulaire.google.style.borderColor = "gray";
					 document.formulaire.url.style.borderColor = "gray";
					 $('#length').hide();
					}
				
			   if(selected==='google')
				verifID();
				
				
			}	
			
			function verifID(){
      			var currlength = $('#google').val().length;
	      		 if((currlength!=44))
	      			 {
		      			document.formulaire.google.style.borderColor = "#f5500c";
	      				$('#length').show();
	      				
	      			 }else{
	      				 
	      				document.formulaire.google.style.borderColor = "#80ff00";
	      				$('#length').hide();
	      			 }
	      		 
      		 }
			function dowloadExample(){
				var urlExample= $('#example option:selected').val();
		    	var exampleText= $('#example option:selected').text();
			    $('#lien').removeAttr('href');
			    $('#lien').attr('href', urlExample);
			    $('a#lien').text('Télécharger le fichier d\'exemple fourni '+exampleText);
			}
			
	    </script>


	</head>
	<body>
		<div class="container">
			<%-- see http://stackoverflow.com/questions/19150683/passing-parameters-to-another-jsp-file-in-jspinclude --%>
			<jsp:include page="header.jsp">
				<jsp:param name="active" value="convert"/>
			</jsp:include>
			
			<div class="messages">
				<c:if test="${data.errorMessagefile!= null}">
					<div class="alert alert-danger">
						<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
						<h4><fmt:message key="error" /></h4>
						${data.errorMessagefile}
					</div>
				</c:if>
			</div>	
					
			<form id="upload_form" action="convert" method="post"name="formulaire" enctype="multipart/form-data" class="form-horizontal">	
			
			<fieldset>
				<legend><fmt:message key="convert.form.legend" /></legend>
				 
				<div class="form-group">
					<input
							class="col-sm-1"
							type="radio"
							name="source"
							id="source-example"
							value="example"
							checked="checked"
							onchange="enabledInput('example')" />					
					<label class="col-sm-2 control-label">
							<fmt:message key="convert.form.providedExample" />
					</label>
					<div class="col-sm-9" >
						<select style=" width:80%;" class="ui-select" name="example" id="example" onchange="dowloadExample()">
							<option value="${data.baseUrl}/excel_test/testExcelNative.xlsx" >Simple example 1</option>
							<option value="${data.baseUrl}/excel_test/test2.xlsx">Simple example 2</option>	 
						</select>
					</div>
					
						<a id="lien" href="${data.baseUrl}/excel_test/testExcelNative.xlsx" style="margin-left:15px;"><fmt:message key="convert.form.Example.download" /></a>
					
			    </div>	
			
				
				<div class="form-group">
					<input
								
								class="col-sm-1"
								type="radio"
								name="source"
								id="source-file"
								value="file"
								onchange="enabledInput('file')" />
					
					<label class="col-sm-2 control-label">
							<fmt:message key="convert.form.localFile" />
					</label>
					<div class="col-sm-9" >
						<div class="fileinput fileinput-new input-group"  data-provides="fileinput" id="fileupload" style="width:80%;">
						  <div class="form-control" data-trigger="fileinput">
						  	<i class="glyphicon glyphicon-file fileinput-exists"></i> 
						  	<span class="fileinput-filename"></span>
						  </div>
						  <span class="input-group-addon btn btn-default btn-file">
						  	<span class="fileinput-new">
						  		<fmt:message key="convert.form.localFile.select" />
						  	</span>
						  	<span class="fileinput-exists">
						  		<fmt:message key="convert.form.localFile.change" />
						  	</span>
						  	<input type="file"  name="file" id="file" onchange="enabledInput('file')" >
						  </span>
						  <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput"><fmt:message key="convert.form.localFile.remove" /></a>
						</div>
						
						
						<span class="help-block"><i><fmt:message key="convert.form.localFile.help" /></i></span>
					</div>
				</div>
				<div class="form-group">
					<input
							class="col-sm-1"
							type="radio"
							name="source"
							id="source-url"
							value="url"
							onchange="enabledInput('url')" />
					<label class="col-sm-2 control-label">
						
						<fmt:message key="convert.form.remoteUrl" />
					</label>
					<div class="col-sm-9" >
						<input
							
							type="text"
							id="url"
							name="url"
							value=""
							placeholder="http://..."
							class="form-control"
							onkeypress="enabledInput('url');" style="width:80%;"/>
						<span class="help-block"><i><fmt:message key="convert.form.remoteUrl.help" /></i></span>
					</div>
					</div>
							<div class="form-group">
						<input
								class="col-sm-1"
								type="radio"
								name="source"
								id="source-google"
								value="google"
								onchange="enabledInput('google')" />
						<label class="col-sm-2 control-label">							
							<fmt:message key="convert.form.remoteUrl.Google" />
						</label>
						<div class="col-sm-9" >
						<span id="length" hidden="hidden" ><fmt:message   key="convert.form.length.googleID.error" /></span>
							<input								
								type="text"
								id="google"
								name="google"
								value="${data.googleId}"
								placeholder="1aNS3e1tpW1CCaDFpN97zEz3g9aULjStCXagTdDVgu"
								class="form-control"
								onchange="verifID()"
								onkeypress="enabledInput('google');" style="width:80%;"/>
							<span class="help-block"><i><fmt:message key="convert.form.remoteUrl.Google.help" /></i></span>
						</div>
					</div>
				
			</fieldset>
			
			<!-- Choix de la langue -->		
			<fieldset>
				<legend><fmt:message key="convert.form.legend.language" /></legend>
				<div class="form-group" style="margin-left:105px; ">					
					<label class="col-sm-2 control-label">
							<fmt:message key="convert.form.language.legend" />
					</label>
					<div class="col-sm-10" style=" width:80%;">
						<select id="choice_Language" class="ui-select " required name="language" id="lg">				
							<option value="en">en</option>									 
							<option value="fr">fr</option>									 
							<option value="de">de</option>						 
						</select>
					</div>
				</div>	
			</fieldset>
			
			<br />
			<br />

			<div class="panel-group" id="myAccordion">
				<div class="panel panel-default">
	   				<div class="panel-heading">
	   					<a class="accordion-toggle" data-toggle="collapse" data-parent="#myAccordion" href="#collapse1"><h4><fmt:message key="convert.form.advanced.legend" /></h4></a>
	   				</div>
	   				<div id="collapse1" class="panel-collapse collapse in"><div class="panel-body">
						
						<div class="form-group">
							<div class="col-sm-4">							
								<label>
										<fmt:message key="convert.form.outputFormat.legend" />
								</label>
							</div>
							<div class="col-sm-1">
								<select  required name="output" >						
									<option value="application/rdf+xml" selected="selected">RDF/XML</option>
									<option value="text/turtle">Turtle</option>		 
									<option value="application/x-trig">TriG</option>
									<option value="text/plain">N-Triples</option>
									<option value="text/x-nquads">N-Quads</option>
									<option value="text/n3">N3</option>								 
								</select>
							</div>						
						</div>	
						<div class="form-group">
							<label class="col-sm-4">
								<fmt:message key="convert.form.useskosxl" />
							</label>
							<div class="col-sm-1">
								<input
									type="checkbox"
									id="useskosxl"
									name="useskosxl" />
								<span class="help-block"><i></i></span>
							</div>													
						</div>
						
						<div class="form-group">
							<label class="col-sm-4">
								<fmt:message key="convert.form.usezip" />
							</label>
							<div class="col-sm-1">
								<!-- check it by default -->
								<input
									type="checkbox"
									id="usezip"
									name="usezip"
									checked="checked" />
								<span class="help-block"><i></i></span>
							</div>														
						</div>
						<!-- ****GENERATE GRAPH****** -->
						
						<!-- <div class="form-group">
							<div class="col-sm-1">
								<input
									type="checkbox"
									id="usegraph"
									name="usegraph" />
								<span class="help-block"><i></i></span>
							</div>
							<label class="col-sm-4">
								<fmt:message key="upload.form.usegraph" />
							</label> 
							</div>
							-->
						
	   				</div></div>
	   			</div><!-- end accordion-group : Advanced options -->
   			</div>		
			
			<div class="form-actions">
				<div class="col-sm-offset-2 col-sm-10">
					<button type="submit"  id="submit-button" class="btn btn-info btn-lg "><fmt:message key="convert" /></button>
					<img src="images/ajax-loader.gif" id="loading" hidden="hidden" />
					<span class="help-block" style="margin-left:120px; margin-top:-40px;"> <i><fmt:message key="convert.form.luxembourg" /></i><img src="images/logo-luxembourg.png" /></span>
				</div>
				
			</div>
			
			</form>
      	</div>
      	<jsp:include page="footer.jsp" />
      	<script>
	      	$(document).ready(function() {
	      		
	      		 
	      		// activate example choice
	      		$('.exampleEntry').click(function() {
	      			$('#example').val($(this).attr('data-value'));
	      			$('#exampleLabel').html($(this).html());
	      		});
	      		
				// disable submit button on click
			    $('#upload_form').submit(function() {
			    	$('#submit-button').attr('disabled', true);
			        $('#loading').show();
			    });
				
			    $(window).on('beforeunload', function(){
			    	$('#loading').hide();
				    $('#submit-button').attr('disabled', false);
			    });
		   		
			    <c:if test="${data.googleId != null}">
			    	enabledInput('google');
			    	window.open('downloadGoogleResult', '_blank');
			    </c:if>
			    
			    	
			    	
			   
			   
	      	});
	      	

		</script>
	</body>
	<script src="js/jquery.min.js"></script>
	<script src="js/jquery-editable-select.js"></script>
	<script>
	 
	$(function(){
	 
	$('#choice_Language').editableSelect();
	 
	});
	 
	</script>
</html>