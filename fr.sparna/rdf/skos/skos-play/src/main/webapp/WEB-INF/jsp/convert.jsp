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
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-3.xlsx">Example 3 (multilingual columns)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-4.xlsx">Example 4 (schema.org, datatypes, multiple sheets)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-5.xlsx">Example 5 (skos:Collection, inverse columns)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-6.xlsx">Example 6 (skos:OrderedCollection, dealing with rdf:Lists)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-7.xlsx">Example 7 (different subjects with subjectColumn parameter)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-8.xlsx">Example 8 (ease references with lookupColumn parameter)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-9.xlsx">Example 9 (real-world thesaurus : maintaining concept hierarchy in Excel)</option>
							<option value="${sessionData.baseUrl}/excel_test/excel2skos-exemple-10.xlsx">Example 10 (real-world person authority file)</option>  
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
							<option value="de" <c:if test="${data.defaultLanguage == 'de'}">selected</c:if>>de</option>
							<option value="en" <c:if test="${data.defaultLanguage == 'en'}">selected</c:if>>en</option>
							<option value="es" <c:if test="${data.defaultLanguage == 'es'}">selected</c:if>>es</option>	
							<option value="fr" <c:if test="${data.defaultLanguage == 'fr'}">selected</c:if>>fr</option>
							<option value="it" <c:if test="${data.defaultLanguage == 'it'}">selected</c:if>>it</option>
							<option value="ru" <c:if test="${data.defaultLanguage == 'ru'}">selected</c:if>>ru</option>					 
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
				<div class="col-sm-offset-2 col-sm-4">
					<button type="submit"   id="submit-button" class="btn btn-info btn-lg "><fmt:message key="convert" /></button>
					<img src="images/ajax-loader.gif" id="loading" hidden="hidden" />
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
				<h4>What is this tool ?</h4>
				<p>
					This is an Excel-to-SKOS converter. It can generate SKOS RDF files from Excel spreadsheets structured in a specific way.
					<br />Using the same Excel spreadsheet structure, it is also possible to produce other RDF data than SKOS (lists of foaf:Person, of schema:Event, etc.)
					<br />This converter does not require any configuration file to work, only the Excel document to convert.
				</p>
				<h4>Can I convert <i>any</i> Excel file in RDF ?</h4>
				<p>
					No. The spreadsheet has to follow <a href="#excel-file-structure">the specific structure described below</a>.
				</p>
				<h4>What should the Excel file look like ?</h4>
				<p>
					Start by downloading and looking at <a href="#source-example">one of the provided examples above</a>. You can start from one of these files and adapt it. Look at the <a href="#excel-file-structure">documentation below</a>
					for an explanation on the expected spreadsheet format.
				</p>
				<h4>Do you know of any similar tools ?</h4>
				<p>
					There are other converters from Excel to SKOS or RDF out there :
					<ul>
						<li><a href="http://xlwrap.sourceforge.net/">XLWrap</a> (I used it quite a bit, it is good but uses complex configuration files)</li>
						<li><a href="http://art.uniroma2.it/sheet2rdf/">Sheet2RDF</a>, from the team that makes <a href="http://vocbench.uniroma2.it/">VocBench</a></li>
						<li><a href="http://www.openanzo.org/">Open Anzo</a> (never tested it)</li>
						<li>You can check for other tools on the <a href="https://www.w3.org/wiki/ConverterToRdf#Excel">W3C RDF converter wiki page</a>.</li>
					</ul>
				</p>
			</fieldset>
			
			<!-- Excel File structure -->		
			<fieldset style="margin-top:3em;">
				<legend><a href="#excel-file-structure" id="excel-file-structure"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Excel File structure</legend>
				Your excel file <strong>MUST</strong> follow the structure described below to be converted to RDF. Otherwise you will get an exception or an empty RDF file.
				Download and look at <a href="#source-example">the examples above</a>.
				<h4><a href="#spreadsheet" id="spreadsheet"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Spreadsheet processing</h4>
				Your file can contain any number of sheets. All the sheets are processed, and the extractor attempts to convert RDF from all of them.
				If the structure of a sheet doesn't correspond to the expected template, the converter simply moves to the next one.
				<h4><a href="#sheet-header" id="sheet-header"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Sheet header processing</h4>
					<strong>ConceptScheme URI</strong> : To be converted to RDF, a sheet <em>MUST contain a URI in cell B1</em>. This is interpreted as the URI of a <code>skos:ConceptScheme</code>.
					<p /><strong>ConceptScheme metadata</strong> : The header CAN contain descriptive metadata of the ConceptScheme, by specifying a property URI in column A, either using a declared prefix
					(e.g. <code>dct:title</code>, see below) or as a full URI (starting with 'http');
					<p /><strong>Prefix declaration</strong> : Prefixes can be declared in the header :
					<ul>
						<li>column A contains the special keyword "PREFIX" (case-insensitive)</li>
						<li>column B contains the prefix</li>
						<li>column C contains the URI to be prefixed</li>
					</ul>
					Default prefixes are already known and don't have to be declared (see below).
					<p /><strong>Other lines</strong> : the header CAN contain other lines that will be ignored if column A does not contain a known prefixed property or the "PREFIX" keyword.
					<p />This is how a typical header can look like :
					<img src="images/convert-screenshot-header.png" width="100%" />
				<h4><a href="#sheet-body" id="sheet-body"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Sheet body processing</h4>
					<p /><strong>Title row</strong> : The body MUST start by a row that declares the property corresponding to each column (e.g. <code>skos:prefLabel</code>, <code>skos:definition</code>), except column A,
					that will contain the URI for each resource being generated.
					<p />This is how a typical title row can look like :
					<img src="images/convert-screenshot-title-row.png" width="100%" />
					<p /><strong>Line</strong> : Each line after the title row generates one resource with the URI read from column A. The column A MUST contain the URI of a resource, either as a
					full URI (starting with 'http'), or using a declared prefix.
					<p /><strong>Cell</strong> : Each cell in a line is processed, and the value is converted to a literal or object property :
					<ul>
						<li>If the cell value starts with 'http' or 'mailto' or with a declared prefix, it will be interpreted as an object property;</li>
						<li>Multiple URIs can be given in single cell, by separating them with commas <code>, </code>;</li>
						<li>Otherwise, the value is interpreted as a literal;</li>
					</ul>
					<p />This is how a typical body part can look like :
					<img src="images/convert-screenshot-body.png" width="100%" />
				<h4><a href="#languages" id="languages"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Generating multilingual values</h4>
					<p />You can specify the language to be assigned to a column by appending <code>@en</code> (or another language code) to the property declaration in the title row.
					This also works in the header part for the metadata of the ConceptScheme.
					<p />This is an example of multilingual columns declaration :
					<img src="images/convert-screenshot-multilingual.png" width="100%" />
				<h4><a href="#datatypes" id="datatypes"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Generating values with datatypes</h4>
					<p />You can specify the datatype to be assigned to a column by appending <code>^^xsd:date</code> (or another datatype) to the property declaration in the title row.
					<p />This is an example of columns declaration with a datatype :
					<img src="images/convert-screenshot-datatype.png" width="100%" />
				<h4><a href="#split" id="split"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Generating multiple values</h4>
					<p />You can specify a separator on a colum by appending <code>(separator=",")</code> (or another separator) to the property declaration in the title row.
					This indicates that the values in the cells of that columns will be splitted on that separator, and multiple values will be generated.
					You can combine this with a language or datatype declaration, for example <code>schema:name@en(separator=",")</code>.
					<br />The alternative is to create multiple columns with the same property, which is allowed.
				<h4><a href="#collections" id="collections"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Generating skos:Collection with object-to-subject columns</h4>
					<p />By default, each line in the body generates an instance of skos:Concept. If you need to generate instances of skos:Collection (or other classes, by the way), do the following :
					<ol>
						<li>Add a column with the title <code>rdf:type</code>;</li>
						<li>Add a column with the title <code>^skos:member</code>; note the '^' character at the beginning of the column name; this tells the converter to generate the corresponding property (here, skos:member)
						<em>from the value given in the cell to the URI of the resource generated for this row</em>; 
						</li>
						<li>On the row corresponding to the collection, specify <code>skos:Collection</code> in the <code>rdf:type</code> column; for rows corresponding to skos:Concept, you can leave this column empty
						or specify skos:Concept explicitely if you want;</li>
						<li>On each row of skos:Concept that belongs to the collection, enter the collection URI in the <code>^skos:member</code> column;</li>						
					</ol>
					<p />This is an example of expressing collections using object-to-subject column :
					<img src="images/convert-screenshot-collection.png" width="100%" />
				<h4><a href="#lists" id="lists"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Dealing with skos:OrderedCollection and rdf:Lists</h4>
					<p />If you need to deal with skos:OrderedCollection, do the following :
					<ol>
						<li>Add a column with the title <code>rdf:type</code>;</li>
						<li>Add a column with the title <code>skos:memberList</code>;</li>
						<li>On the row corresponding to the ordered collection, specify <code>skos:OrderedCollection</code> in the <code>rdf:type</code> column; for rows corresponding to skos:Concept, you can leave this column empty
						or specify skos:Concept explicitely if you want;</li>
						<li>On the row corresponding to the ordered collection, in the <code>skos:memberList</code> column, write the list of values like you would do in the Turtle, that is :
							<ul>
								<li>Put the whole list between parenthesis;</li>
								<li>Separate each value with a whitespace character;</li>
							</ul>
						</li>
					</ol>
					<p />The same technique can be used to declare any rdf:List (see below to generate plain RDF).
					<p />This is an example of expressing ordered collections using rdf:list syntax :
					<img src="images/convert-screenshot-ordered-collection.png" width="100%" />
			</fieldset>
			
			<!-- Default SKOS processings -->		
			<fieldset style="margin-top:3em;">
				<legend><a href="#post-processings" id="post-processings"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Default SKOS post-processings</legend>
				<p />After each line in the body has been converted, the following SKOS post-processings are applied :
				<p /><strong>skos:inScheme</strong> : a <code>skos:inScheme</code> is added to every instance of skos:Concept and skos:Collection, with the value of the ConceptScheme given in cell B1;
				<p /><strong>skos:broader and skos narrower inverse</strong> : the inverse of <code>skos:broader</code> and <code>skos:narrower</code> are automatically added;
				<p /><strong>skos:hasTopConcept and skos:topConceptOf</strong> : every <code>skos:Concept</code> without <code>skos:broader</code> or not referenced by a <code>skos:narrower</code> is given a <code>skos:topConceptOf</code>
				and its inverse <code>skos:hasTopConcept</code>;
				<p /><strong>SKOS-XL generation</strong> : if requested by the corresponding parameter, labels are turned into SKOS-XL;	
			</fieldset>
			
			<!-- Generating plain RDF-->		
			<fieldset style="margin-top:3em;">
				<legend><a href="#generic-rdf" id="generic-rdf"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Generating plain RDF (not SKOS)</legend>
				<p />The converter can actually generate other RDF vocabularies than SKOS. For this :
				<ul>
					<li>Add an <code>rdf:type</code> column to your data, and specify an explicit rdf:type for each row. Each row not having an explicit rdf:type will be considered a skos:Concept;</li>
					<li>Make sure you still declare a URI in cell B1, this will be the URI of the <em>named graph</em> 	in which the data will be generated; note that to see this named graph in the output, you need to select an RDF format that supports named graphs (NQuads or TriG);</li>
					<li>If you declare metadata in the header, these will be interpreted as metadata of the named graph;</li>
				</ul>
				<p />This is how this kind of file could look like :
				<img src="images/convert-screenshot-other-skos.png" width="100%" />
			</fieldset>
			
			<!-- Advanced features -->		
			<fieldset style="margin-top:3em;">
				<legend><a href="#advanced-features" id="advanced-features"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Advanced features</legend>
				<h4><a href="#blank-nodes" id="blank-nodes"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Creating blank nodes with [...]</h4>
					<p />The converter understands the blank node syntax with "[...]" : simply put a cell value between square brackets and write the blank node data inside like you would do in a Turtle file.
					This can be useful to generate references to reified SKOS definitions or SKOS-XL Labels. For example, if a cell with title <code>skos:definition</code> contains the following value :<br />
					<code>[ rdf:value "Definition blah blah"; dcterms:created "2017-02-21"^^xsd:date ]</code>, then a reference to a blank node will be created. You need to use the prefixes defined in the file in your
					blank node content. The blank node is parsed exactly as a piece of Turtle, so it can contain any piece of valid Turtle syntax. If anything goes wrong during the parsing, the converter
					will generate a Literal with the cell content instead.
				<h4><a href="#striketrough" id="striketrough"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Disabling cell conversion with a <strike>strikethrough</strike></h4>
					<p />When working on a file, if you are unsure about the conversion of a certain cell but you don't want to delete the value, use a <strike>strikethrough font</strike> : the converter will ignore any
					cell with such a font style. You can keep uncertain values in the files and simply change the font back to normal once the value is validated.
					<p />
				<h4><a href="#graph-management" id="graph-management"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Named graph management</h4>
					<p />The converter actually puts all the triples generated in one sheet in a graph with the URI in cell B1. This is usually the same URI as the URI of the ConceptScheme;
					but in case of processing generic RDF data, this cell B1 can be used to indicate the URI of the graph, with its associated metadata in the header.
					<p />
				<h4><a href="#subjectColumn" id="subjectColumn"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Changing Subject Column</h4>
					<p />By default, the property in each column is expressed on the subject URI of the first column of the spreadsheet. It is possible to state that a given column is expressed on a subject URI
					in a different column on the table. To do this, add a column parameter <code>subjectColumn</code> with a reference to the column letter containing the URI of the subject.
					For example <code>schema:name(subjectColumn="N")</code> means this column is the name of the URI stored in column N.
					<p />This is how such a header could look like :
				<img src="images/convert-screenshot-subjectColumn.png" width="100%" />
				<h4><a href="#lookupColumn" id="lookupColumn"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Refering to URIs using their labels</h4>
					<p />SKOS involves creating hierarchies of concepts, and connecting related concepts. This involves making references to other concept URIs, typically in
					a column <code>skos:broader</code>. But URIs can be opaque, and copy-pasting concept URIs across cells can be tedious. The lookupColumn parameter is a mechanism
					that allows you to <em>reference a concept URI through one of its label (or other unique key) stored in another column</em>. To use it, add a column parameter
					<code>lookupColumn</code> with a reference to the column in which the string value of this column will be searched. The reference can be either a reference to the
					Excel column letter or to the corresponding property in which you want to lookup. A typical example is <code>skos:broader(lookupColumn=skos:prefLabel)</code>,
					which means that you want to create a skos:broader having as a value the URI of the Concept that have in its <code>skos:prefLabel</code> column the value you indicate
					in your skos:broader column.
					<p />This is how it would look like, have a look at example 8 in the included examples :
				<img src="images/convert-screenshot-lookupColumn.png" width="100%" />
					<p />You can view the <code>lookupColumn</code> parameter as the equivalent of Excel "VLOOKUP" / "RECHERCHEV" function, except easier to write.

					<p />
			</fieldset>
			
			<!-- Prefixes -->		
			<fieldset style="margin-top:3em;">
				<legend><a href="#prefixes" id="prefixes"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>&nbsp;Default prefixes known in the converter</legend>
				<p />This is the list of known prefixes in the converter. You don't have to declare them in the header.
				<ul>
					<li><a href="http://prefix.cc/rdf"><code>rdf</code></a></li>
					<li><a href="http://prefix.cc/rdfs"><code>rdfs</code></a></li>
					<li><a href="http://prefix.cc/owl"><code>owl</code></a></li>
					<li><a href="http://prefix.cc/skos"><code>skos</code></a></li>
					<li><a href="http://prefix.cc/skosxl"><code>skosxl</code></a></li>
					<li><a href="http://prefix.cc/foaf"><code>foaf</code></a></li>
					<li><a href="http://prefix.cc/org"><code>org</code></a></li>
					<li><a href="http://prefix.cc/prov"><code>prov</code></a></li>
					<li><a href="http://prefix.cc/schema"><code>schema</code></a></li>
					<li><a href="http://prefix.cc/dc"><code>dc</code></a></li>
					<li><a href="http://prefix.cc/dct"><code>dct</code></a> or <a href="http://prefix.cc/dcterms"><code>dcterms</code></a></li>
					<li><a href="http://prefix.cc/xsd"><code>xsd</code></a></li>
					<li><a href="http://prefix.cc/qb"><code>qb</code></a></li>
					<li><a href="http://prefix.cc/dcat"><code>dcat</code></a></li>
				</ul>
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