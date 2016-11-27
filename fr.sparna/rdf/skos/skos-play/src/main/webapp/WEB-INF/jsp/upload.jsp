<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<c:set var="data" value="${requestScope['fr.sparna.rdf.skosplay.UploadFormData']}" />
<c:set var="applicationData" value="${applicationScope.applicationData}" />

<html>
	<head>
		<title><c:out value="${applicationData.skosPlayConfig.applicationTitle}" /></title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/jasny-bootstrap.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<link href="style/custom.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/jasny-bootstrap.min.js"></script>

		<script type="text/javascript">
	
			function enabledInput(selected) {
				document.getElementById('source-' + selected).checked = true;
				document.getElementById('url').disabled = selected != 'url';
				document.getElementById('example').disabled = selected != 'example';
				document.getElementById('file').disabled = selected != 'file';
				// document.getElementById('endpoint').disabled = selected != 'endpoint';
			}	
			
	    </script>


	</head>
	<body>
		<div class="container">
			<%-- see http://stackoverflow.com/questions/19150683/passing-parameters-to-another-jsp-file-in-jspinclude --%>
			<jsp:include page="header.jsp">
				<jsp:param name="active" value="upload"/>
			</jsp:include>
			
			<div class="messages">
				<c:if test="${data.errorMessage != null}">
					<div class="alert alert-error fade in">
						<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
						<h4><fmt:message key="error" /></h4>
						${data.errorMessage}
					</div>
				</c:if>
			</div>	
					
			<form id="upload_form" action="upload" method="post" enctype="multipart/form-data" class="form-horizontal">	
			
			<c:choose>
			<c:when test="${!data.skosPlayConfig.publishingMode}">	
			
			<fieldset>
				<legend><fmt:message key="upload.form.legend" /></legend>
				
				<!-- Ancienne partie avec une dropdown simple -->
				<!--
				<div class="control-group">
					<label class="control-label">
							<input
								type="radio"
								name="source"
								id="source-example"
								value="example"
								onchange="enabledInput('example')"
								checked="checked" />
							<fmt:message key="upload.form.providedExample" />
					</label>
					<div class="controls">
						<select
							class="span4"
							name="example"
							id="example"
							onchange="enabledInput('example')">
							
							<c:forEach items="${applicationData.exampleDatas}" var="entry">
								<option value="${entry.key}">
									<c:catch var="labelNotFoundException">
										 ${sessionScope['fr.sparna.rdf.skosplay.SessionData'].preLoadedDataLabels.getString(entry.key)}
									</c:catch>
									<c:if test="${labelNotFoundException != null}">${entry.key}</c:if>
								
								</option>
							</c:forEach>
						</select>
					</div>
				</div>
				 -->
				
				<!-- Nouvelle section avec une dropdown bootstrap permettant d'inclure des images -->
				<div class="form-group">
					<input
								class="col-sm-1"
								type="radio"
								name="source"
								id="source-example"
								value="example"
								onchange="enabledInput('example')"
								checked="checked" />
					<label  class="col-sm-2 control-label">
							
							<fmt:message key="upload.form.providedExample" />
					</label>
					<div class="col-sm-9" style="padding-top:1em;">
						<c:choose>
							<c:when test="${applicationData.exampleDatas != null && fn:length(applicationData.exampleDatas) > 0 }">							
								
								<!--  generate the dropdown. See http://getbootstrap.com/2.3.2/components.html#dropdowns -->
								<div class="dropdown">
								
									<!-- Trick to init hidden field and default selection with the first entry in the map -->					
									<c:forEach items="${applicationData.exampleDatas}" var="entry" varStatus="status">
										<c:if test="${status.first}">
											<!-- Hidden field initialized with first entry key -->
											<input
												type="hidden"
												name="example"
												id="example"
												value="${entry.key}" />
											<!-- Display first entry label -->
											<a id="selected" class="btn btn-default dropdown-toggle " data-toggle="dropdown" href="#"><span  id="exampleLabel">${sessionScope['fr.sparna.rdf.skosplay.SessionData'].preLoadedDataLabels.getString(entry.key)}</span> <b class="caret"></b></a>
										</c:if>
									</c:forEach>
								
									<ul class=" dropdown-menu" role="menu" aria-labelledby="dLabel">
										<!-- Re-iterate on entries -->
										<c:forEach items="${applicationData.exampleDatas}" var="entry">
											<li><a class="exampleEntry" data-value="${entry.key}" href="#">
								    			<c:catch var="labelNotFoundException">
													 ${sessionScope['fr.sparna.rdf.skosplay.SessionData'].preLoadedDataLabels.getString(entry.key)}
												</c:catch>
												<c:if test="${labelNotFoundException != null}">${entry.key}</c:if>
												
												<!--
												<c:set value="upload.form.providedExample.${entry.key}" var="messageKey"/>
												<c:set value="???${pageScope.messageKey}???" var="unknownValue"/>
												<fmt:message key="${pageScope.messageKey}" var="exampleDataName"/>
				
												<c:choose>
													<c:when test="${pageScope.exampleDataName == pageScope.unknownValue}">${entry.key}</c:when>
													<c:otherwise>${pageScope.exampleDataName}</c:otherwise>
												</c:choose>
												-->
								    		</a></li>
										</c:forEach>
									</ul>
								</div>
							</c:when>							
							<c:otherwise>
								<!-- No values in the map, create an empty hidden field -->
								<input
									type="hidden"
									name="example"
									id="example" />
							</c:otherwise>
						</c:choose>
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
							<fmt:message key="upload.form.localFile" />
					</label>
					<div class="col-sm-9" >
						<div class="fileinput fileinput-new input-group" data-provides="fileinput" id="fileupload" style="width:60%;">
						  <div class="form-control" data-trigger="fileinput">
						  	<i class="glyphicon glyphicon-file fileinput-exists"></i> 
						  	<span class="fileinput-filename"></span>
						  </div>
						  <span class="input-group-addon btn btn-default btn-file">
						  	<span class="fileinput-new">
						  		<fmt:message key="upload.form.localFile.select" />
						  	</span>
						  	<span class="fileinput-exists">
						  		<fmt:message key="upload.form.localFile.change" />
						  	</span>
						  	<input type="file" name="file" id="file"onchange="enabledInput('file')" >
						  </span>
						  <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput"><fmt:message key="upload.form.localFile.remove" /></a>
						</div>
						
						
						<!-- <div class="fileinput fileinput-new" data-provides="fileinput">
							<div class="input-append">
								<div class="uneditable-input span4">
									<i class="icon-file fileinput-exists"></i> <span class="fileinput-preview"></span>
								</div>
								<span class="btn btn-file">
									<span class="fileinput-new"></span>
									<span class="fileinput-exists"></span>
									<input type="file" name="file" id="file" onchange="enabledInput('file')" />
								</span>
								<a href="#" class="btn fileinput-exists" data-dismiss="fileinput"></a>
							</div>
						</div>-->
						<span class="help-block"><i><fmt:message key="upload.form.localFile.help" /></i></span>
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
						
						<fmt:message key="upload.form.remoteUrl" />
					</label>
					<div class="col-sm-9" >
						<input
							
							type="text"
							id="url"
							name="url"
							value=""
							placeholder="http://..."
							class="form-control"
							onkeypress="enabledInput('url');" />
						<span class="help-block"><i><fmt:message key="upload.form.remoteUrl.help" /></i></span>
					</div>
				</div>
				
				<!--
				<div class="control-group">
					<label class="control-label">
						<input
							type="radio"
							name="source"
							id="source-endpoint"
							value="endpoint"
							onchange="enabledInput('endpoint')" />
						<fmt:message key="upload.form.endpoint" />
					</label>
					<div class="controls">
						<input
							type="text"
							id="endpoint"
							name="endpoint"
							value=""
							onkeypress="enabledInput('endpoint');" />
						<span class="help-block"><i><fmt:message key="upload.form.endpoint.help" /></i></span>
					</div>
				</div>
				 -->
			</fieldset>

			<div class="panel-group" id="myAccordion">
				<div class="panel panel-default">
	   				<div class="panel-heading">
	   					<a class="accordion-toggle" data-toggle="collapse" data-parent="#myAccordion" href="#collapse1"><h4><fmt:message key="upload.form.advanced.legend" /></h4></a>
	   				</div>
	   				<div id="collapse1" class="panel-collapse collapse in" ><div class="panel-body">
	
						<div class="form-group">
							<label class="col-sm-2">
								<fmt:message key="upload.form.rdfs" />
							</label>
							<div class="col-sm-10">
								<input
									type="checkbox"
									id="rdfsInference"
									name="rdfsInference" />
								<span class="help-block"><i><fmt:message key="upload.form.rdfs.help" /></i></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2">
								<fmt:message key="upload.form.skosxl2skos" />
							</label>
							<div class="col-sm-10">
								<!-- check it by default -->
								<input
									type="checkbox"
									id="skosxl2skos"
									name="skosxl2skos"
									checked="checked" />
								<span class="help-block"><i><fmt:message key="upload.form.skosxl2skos.help" /></i></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2">
								<fmt:message key="upload.form.owl2skos" />
							</label>
							<div class="col-sm-10">
								<input
									type="checkbox"
									id="owl2skos"
									name="owl2skos" />
								<span class="help-block"><i><fmt:message key="upload.form.owl2skos.help" /></i></span>
							</div>
						</div>
						
	   				</div></div>
	   			</div><!-- end accordion-group : Advanced options -->
   			</div>
   			
   			<!-- end NOT publishing mode (normal mode -->
   			</c:when>
   			<c:otherwise>
   			
   				<!-- hidden fields -->
   				<input
					type="hidden"
					name="source"
					id="source-example"
					value="example" />
				<input
					type="hidden"
					id="skosxl2skos"
					name="skosxl2skos"
					value="true" />
   			
   				<label class="col-sm-2">
   					<fmt:message key="upload.form.publishingMode.selectVocabulary" />
   				</label>
   				<div class="col-sm-10" style="padding-top:1em;">
	   				<!--  generate the dropdown. See http://getbootstrap.com/2.3.2/components.html#dropdowns -->
					<div class="dropdown">
					
						<!-- Trick to init hidden field and default selection with the first entry in the map -->					
						<c:forEach items="${applicationData.exampleDatas}" var="entry" varStatus="status">
							<c:if test="${status.first}">
								<!-- Hidden field initialized with first entry key -->
								<input
									type="hidden"
									name="example"
									id="example"
									value="${entry.key}" />
								<!-- Display first entry label -->
								<a id="selected" class="dropdown-toggle btn" data-toggle="dropdown" href="#"><span id="exampleLabel">${sessionScope['fr.sparna.rdf.skosplay.SessionData'].preLoadedDataLabels.getString(entry.key)}</span> <b class="caret"></b></a>
							</c:if>
						</c:forEach>
					
						<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
							<!-- Re-iterate on entries -->
							<c:forEach items="${applicationData.exampleDatas}" var="entry">
								<li><a class="exampleEntry" data-value="${entry.key}" href="#">
					    			<c:catch var="labelNotFoundException">
										 ${sessionScope['fr.sparna.rdf.skosplay.SessionData'].preLoadedDataLabels.getString(entry.key)}
									</c:catch>
									<c:if test="${labelNotFoundException != null}">${entry.key}</c:if>
									
									<!--
									<c:set value="upload.form.providedExample.${entry.key}" var="messageKey"/>
									<c:set value="???${pageScope.messageKey}???" var="unknownValue"/>
									<fmt:message key="${pageScope.messageKey}" var="exampleDataName"/>
	
									<c:choose>
										<c:when test="${pageScope.exampleDataName == pageScope.unknownValue}">${entry.key}</c:when>
										<c:otherwise>${pageScope.exampleDataName}</c:otherwise>
									</c:choose>
									-->
					    		</a></li>
							</c:forEach>
						</ul>
					</div>
				</div>
   			
   			
   			</c:otherwise>
   			</c:choose>
			
			<div class="form-actions">
				<div class="col-sm-offset-2 col-sm-10">
					<button type="submit" id="submit-button" class="btn btn-primary btn-lg "><fmt:message key="next" /></button>
					<img src="images/ajax-loader.gif" id="loading" hidden="hidden" />
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
			    
	      	});
			    
		</script>
	</body>
</html>
