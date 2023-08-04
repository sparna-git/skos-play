<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<c:set var="data" value="${requestScope['fr.sparna.rdf.skosplay.ConvertFormData']}" />
<c:set var="sessionData" value="${sessionScope['fr.sparna.rdf.skosplay.SessionData']}" />
<c:set var="applicationData" value="${applicationScope.applicationData}" />

<html>
	<head>
		<title><c:out value="${applicationData.skosPlayConfig.applicationTitle}" /></title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/jasny-bootstrap.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<link href="style/custom.css" rel="stylesheet" />
		<link rel="stylesheet" href="css/jquery-editable-select.min.css">
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
				if((selected != 'google')||(selected!='url')) {
					 document.formulaire.google.style.borderColor = "gray";
					 document.formulaire.url.style.borderColor = "gray";
					 $('#length').hide();
				}
				
			   if(selected==='google')
				verifID();			
			}	
			
			function verifID(){
      			var currlength = $('#google').val().length;
	      		 if((currlength!=44)) {
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
			    $('#lien').attr('href', urlExample);
			    $('a#lien').text(exampleText);
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
					
			<form id="upload_form" action="convert" method="post" name="formulaire" enctype="multipart/form-data" class="form-horizontal">	
			
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
						<select style=" width:40%;" name="example" id="example" onchange="dowloadExample()">
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-1.xlsx" selected>Example 1 (simple exemple, in english)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-2.xlsx">Example 2 (prefixes)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-3.xlsx">Example 3 (multilingual columns and deprecation)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-4.xlsx">Example 4 (schema.org, datatypes, multiple sheets)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-5.xlsx">Example 5 (skos:Collection, inverse columns)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-6.xlsx">Example 6 (skos:OrderedCollection, dealing with rdf:Lists)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-7.xlsx">Example 7 (different subjects with subjectColumn parameter)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-8.xlsx">Example 8 (ease references with lookupColumn parameter)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-use-case-1.xlsx">Use-case 1 (real-world thesaurus : maintaining concept hierarchy in Excel)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-use-case-2.xlsx">Use-case 2 (real-world person authority file)</option>  
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-use-case-3.xlsx">Use-case 3 (real-world SHACL constraints)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-use-case-4.xlsx">Use-case 4 (Metadata template from fairdatacollective.com)</option>    
						</select>						
						<span class="help-block"><i><fmt:message key="convert.form.Example.download" />&nbsp;<a id="lien" href="${sessionData.baseUrl}/excel_test/excel2skos-exemple-1.xlsx">Example 1 (simple exemple, in english)</a></i></span>
					</div>
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
					
					<!-- Hide Google Drive option
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
							<div class="col-sm-10" style="margin-left:-15px;">
							<c:choose>
								<c:when test="${sessionData.user != null}">					
									<select 
										id="google"
										class="form-control"
										onchange="enabledInput('google');verifID();"
										name="google"
										style="width:100%;">	
										<c:forEach var="currentFile" items="${data.googleFiles}" >
										   <option value="${currentFile.id}">${currentFile.name} (${currentFile.modifiedTime})</option>
										</c:forEach>							 					 
									</select>				  					
				  					<span class="help-block"><i><fmt:message key="convert.form.remoteUrl.Google.help" /></i></span>
								</c:when>
								<c:otherwise>
									<div class="alert alert-info">
										<fmt:message key="convert.form.googleID.notLogged" />
									</div>
								</c:otherwise>
							</c:choose>	
							</div><br/><br/>
						</div>
					</div>
				 	-->
			</fieldset>
			
			<!-- Choix de la langue -->		
			<fieldset>
				<legend><fmt:message key="convert.form.legend.language" /></legend>
				<div class="form-group" style="margin-left:105px; ">					
					<label class="col-sm-2 control-label">
							<fmt:message key="convert.form.language.legend" />
					</label>
					<div class="col-sm-10">
						<select id="choice_Language" class="ui-select" name="language" id="lg" style="width:4em;">
							<option value="" selected></option>	
							<option value="de">de</option>
							<option value="en">en</option>
							<option value="es">es</option>	
							<option value="fr">fr</option>
							<option value="it">it</option>
							<option value="ru">ru</option>					 
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
									<option value="text/turtle" selected="selected">Turtle</option>
									<option value="application/rdf+xml">RDF/XML</option>	 
									<option value="text/plain">N-Triples</option>
									<option value="text/x-nquads">N-Quads</option>
									<option value="text/n3">N3</option>
									<option value="application/x-trig">TriG</option>							 
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
								<fmt:message key="convert.form.broaderTransitive" />
							</label>
							<div class="col-sm-1">
								<input
									type="checkbox"
									id="broaderTransitive"
									name="broaderTransitive" />
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
									name="usezip" />
								<span class="help-block"><i></i></span>
							</div>														
						</div>
						<!-- ****GENERATE GRAPH****** -->
						
						<div class="form-group">
							<label class="col-sm-4">
								<fmt:message key="convert.form.usegraph" />
							</label>
							<div class="col-sm-8">
								<input
									type="checkbox"
									id="usegraph"
									name="usegraph" />
								<span class="help-block"><i></i></span>
							</div>	
						</div>
						
						<div class="form-group">
							<label class="col-sm-4">
								<fmt:message key="convert.form.ignorepostproc" />
							</label>
							<div class="col-sm-1">
								<input
									type="checkbox"
									id="ignorePostProc"
									name="ignorePostProc" />
								<span class="help-block"><i></i></span>
							</div>														
						</div>
						
	   				</div></div>
	   			</div><!-- end accordion-group : Advanced options -->
   			</div>			
			<div class="form-actions">
				<div class="col-sm-offset-1 col-sm-4">
					<button type="submit"   id="submit-button" class="btn btn-info btn-lg "><fmt:message key="convert" /></button>
					<img src="images/ajax-loader.gif" id="loading" hidden="hidden" />
					<p><em>03/05/2022 : The converter is now available as an <code><a href="http://xls2rdf.sparna.fr/rest">API</a></code> !</em></p>
				</div>
				<div class="col-sm-offset-2 col-sm-4">
					<img src="images/logo-luxembourg.png"/>
					<br />
					<span class="help-block" style="font-size:85%;">&nbsp;<i><fmt:message key="convert.form.luxembourg" /></i></span>
				</div>
			</div>
			
			</form>
			
			<!-- Documentation -->		
			<fieldset id="documentation" style="margin-top:10em;">
				<legend><a href="#documentation" id="documentation"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;General Documentation</legend>
				
				<p/><strong>/!\</strong> The documentation has moved to the <a href="https://xls2rdf.sparna.fr/rest/doc.html">xls2rdf online API service</a>.
			</fieldset>
			
			
			
      	</div>
      	
      	<jsp:include page="footer.jsp" />
      	
      	
      	
      	<script src="js/jquery.min.js"></script>
		<script src="js/jquery-editable-select.min.js"></script>
      	<script>
	      	$(document).ready(function() {	      		
	      		 
	      		// activate example choice
	      		$('.exampleEntry').click(function() {
	      			$('#example').val($(this).attr('data-value'));
	      			$('#exampleLabel').html($(this).html());
	      		});
	      		
	      		$('#usegraph').click(function() {
	      			if($(this).is(':checked')) {
		      			$('#usezip').attr('checked', true);
	      			}
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
		   		
			    <c:if test="${sessionData.user == null}">
			    	$('#source-google').attr('disabled',true);
		    	</c:if>
		    	<c:if test="${sessionData.user != null}">
		    		$('#source-google').attr('disabled',false);
	    		</c:if>
			    
				$(function(){	 
					$('#choice_Language').editableSelect();
				});
	      	});
	      	
	      	
		</script>
	</body>

</html>