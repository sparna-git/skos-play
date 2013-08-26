<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="fr.sparna.rdf.skosplay.SessionData" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

<html>
	<head>
		<title>SKOS Play ! - Visualize SKOS Thesaurus - About</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
		
		<link rel="alternate" hreflang="fr" href="?lang=fr" />
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
					    	<li class="active"><a href="about"><fmt:message key="menu.about" /></a></li>
					    	<li><a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank"><fmt:message key="menu.feedback" /></a></li>
					    	<li class="dropdown">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#">
									<c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}">fr</c:when>
										<c:otherwise>en</c:otherwise>
									</c:choose>
									<b class="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li>
									<c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}"><a href="?lang=en">en</a></c:when>
										<c:otherwise><a href="?lang=fr">fr</a></c:otherwise>
									</c:choose>
									</li>
								</ul>
							</li>
					    </ul>
					</div>
			    </div>	      		
	      	</div>
			
			<fieldset>
				<legend>SKOS Play : introduction</legend>
				<h4>What is "SKOS Play" ?</h4>
				<p>
					SKOS Play is an application to render and visualise thesaurus, taxonomies or controlled vocabularies expressed in 
					<a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">SKOS</a>.
					With SKOS Play you can print Knowledge Organization Systems that use the SKOS data model in HTML or PDF documents, and
					visualize them in graphical representations.
					SKOS Play is intended to demonstrate some of the possibilities of enterprise vocabularies usage and web of data
					technologies.
				</p>
				<h4>What is it for ?</h4>
				<p>
					<ul>
						<li>Generating printable versions of thesaurus or knowledge organization systems;</li>
						<li>Bridge the gap between SKOS data and datviz provided by <a href="http://d3js.org" target="_blank">d3js</a>;</li>
						<li>Demonstrate and illustrate how some of the technologies of the web of data work;</li>
					</ul>
				</p>
				<h4>Is it free ?</h4>
				<p>
					Yes.
				</p>
				<h4>Is is open-source ?</h4>
				<p>
					Yes, the code is published <a href="http://bitbucket.org/tfrancart/sparna">on bitbucket</a>.
					SKOS Play has for the time being a
					<a href="http://creativecommons.org/licenses/by-sa/3.0/deed.fr" target="_blank">CC-BY-SA</a> licence :
					<ul>
						<li>You can freely use the online application, or download the code and use it at home, including for commercial use;
						</li>
						<li>If you reuse it, you have to cite the author ("Thomas Francart for Sparna");</li>
						<li>If you modify the code you have to publish your modifications in the same licence, the easiest
						being to contribute directly in <a href="http://bitbucket.org/tfrancart/sparna" target="_blank">the sources</a>;
						</li>
					</ul>					
					Anyway, <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">contact me</a> if in doubt
					(it is not very for me neither anyway).
					<br />These licensing terms may evolve in the future.
				</p>
				<h4>Does SKOS Play keep a copy of the submitted data ? </h4>
				<p>No.</p>
				<h4>Who build it ?</h4>
				<p>
					<a href="http://francart.fr" target="_blank">Thomas Francart</a> for <a href="http://sparna.fr" target="_blank">Sparna</a>.
				</p>
				<h4>What are the licences of included examples ?</h4>
				<p>
					<ul>
						<li>Thesaurus EUROVOC : © European Union, 2013, <a href="http://eurovoc.europa.eu/" target="_blank">http://eurovoc.europa.eu/</a>.</li>
						<li>UNESCO Thesaurus : <a href="http://www.unesco.org/new/fr/terms-of-use/terms-of-use/copyright" target="_blank">copyright UNESCO</a>, thanks to <a href="http://skos.um.es/unescothes/" target="_blank">University of Murcia</a>.</li>
						<li>W Thesaurus : property of archives de France, data downloaded <a href="http://www.archivesdefrance.culture.gouv.fr/gerer/classement/normes-outils/thesaurus/" target="_blank">here</a> in may 2013</li>
						<li>New-York Times Subject descriptors : Creative Commons Attribution 3.0 United States License, New York Times Company. Data downloaded <a href="http://data.nytimes.com">here</a> in may 2013.</li>
						<li><a href="http://data.reegle.info/thesaurus">Reegle clean energy thesaurus</a> : Data downloaded <a href="http://poolparty.reegle.info/PoolParty/sparql/glossary">here</a> in august 2013, with publisher's permission.</li>
					</ul>
					<a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank">Suggest more.</a>
				</p>
				<h4>I have a question, I need feature XYZ, I would like to contribute.</h4>
				<p>
					Nice !
					<ul>
						<li><a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank">vote and give feedbacks on Google Moderator</a>;</li>
						<li>or <a href="http://francart.fr/skos-play-generer-html-pdf-dataviz-thesaurus-skos">leave a word on the blog</a>;</li>
						<li>or <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">send me an email</a>;</li>
						<li>or look at <a href="http://bitbucket.org/tfrancart/sparna" target="_blank">the code</a>;</li>
					</ul>
				</p>
			</fieldset>

			<br />
			<br />
			
			<fieldset>
				<legend>SKOS</legend>
				<h4>What is SKOS ?</h4>
				<p>
					SKOS is a data model to share and link knowledge organisation systems on the Web. This model is defined by the
					W3C <a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">here</a>.
				</p>
				<h4>Where can I find SKOS data ?</h4>
				<p><a href="http://www.w3.org/2001/sw/wiki/SKOS/Datasets" target="_blank">Here</a></p>
				<h4>How can I write or generate a SKOS file ?</h4>
				<p>
					Try <a href="http://sourceforge.net/projects/tematres/" target="_blank">Tematres</a> (open-source), the <a href="http://code.google.com/p/skoseditor/" target="_blank">Protégé SKOS plugin</a> (open-source),
					<a href="http://thmanager.sourceforge.net/" target="_blank">ThManager</a> (open-source), <a href="https://github.com/culturecommunication/ginco" target="_blank">Ginco</a> (open-source). There are also
					commercial solutions of course.
					<br />
					You can also simply write or generate a file with this structure, save it with *.ttl extension, and make sure it is encoded in UTF-8 :
					<pre>
# declare this header in the top of the file
@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix skos: &lt;http://www.w3.org/2004/02/skos/core#&gt; .
# modify this prefix to suit your namespace
@prefix me: &lt;http://www.exemple.com/&gt; .

# declare a concept scheme that represents the thesaurus, one time at the top of the file
me:MyThesaurus a skos:ConceptScheme .
me:MyThesaurus skos:prefLabel "Name of the thesaurus here"@en .

# then declare concepts. Here is one
me:Vehicle a skos:Concept .
# for each concept you need to say it is part of the thesaurus
me:Vehicle skos:inScheme me:MyThesaurus .
# and then declare its preferred label (the one that will appear on-screen)
me:Vehicle skos:prefLabel "Vehicle"@en .

# here is a second concept
me:Car a skos:Concept .
me:Car skos:inScheme me:MyThesaurus .
me:Car skos:prefLabel "Car"@en .
# this is an alternate label, you can put multiple ones by repeating the line
me:Car skos:altLabel "Automobile"@en .
# and then say that me:Car is a more specific concept than me:Vehicle
me:Car skos:broader me:Vehicle .

# and here is a third concept
me:123456 a skos:Concept .
me:123456 skos:inScheme me:MyThesaurus .
me:123456 skos:prefLabel "Bicycle"@en .
me:123456 skos:altLabel "Bike"@en .
me:123456 skos:altLabel "Two-wheeler"@en .
me:123456 skos:broader me:Vehicle .
					</pre>
				</p>
			</fieldset>
			
			<br />
			<br />
			
			<fieldset>
				<legend>SKOS Play : how does it work ?</legend>
				<h4>How does it look like under the hood ?</h4>
				<p>
					SKOS Play is based on <a href="http://openrdf.org">OpenRDF Sesame</a>, the only sensible choice for enterprise semantic technologies
					applications, and also uses the <a href="http://tfrancart.bitbucket.org/sesame-toolkit/apidocs/fr/sparna/rdf/sesame/toolkit/Documentation.html" target="_blank">sesame-toolkit</a> component.
					Data visualisation are created with <a href="http://d3js.org" target="_blank">d3js</a>, PDF with <a href="http://xmlgraphics.apache.org/fop/" target="_blank">Apache FOP</a>.
					All of that uses SPARQL to query the data
					<br />
					Schematically, there are 4 layers in the application :
					<ol>
						<li>Loading/processing RDF with Sesame and <a href="http://tfrancart.bitbucket.org/sesame-toolkit/apidocs/fr/sparna/rdf/sesame/toolkit/Documentation.html" target="_blank">sesame-toolkit</a>;</li>
						<li>SKOS-specific queries to navigate the data (see algorithm below);</li>
						<li>Transformation of SKOS data into "printable" structure using JAXB, then from this structure to HTML or PDF using XSLT;</li>
						<li>The screens of the application itself, using servlets, JSP & JSPL, Jquery, Bootstrap.</li>
					</ol>
				</p>
				<h4>How is the tree data structure generated from SKOS ?</h4>
				<p>
					Good question. SKOS data model is flexible and SKOS data can come in multiple variations, choices has to made.
					Here is the algorithm that SKOS Play follows to try to accomodate diverse situations :
					<ul>
						<li>To determine the root :
							<ul>
								<li>If at least one concept scheme exists, it needs to be selected in the screen, and will be the root.</li>
								<li>Otherwise, if one and only one collection exists, it will be considered as the root.</li>
								<li>Otherwise, if one and only one concept without skos:broader (or not referenced by a skos:narrower) exists, it will be considered as the root.</li>
								<li>Otherwise, if multiple collections or multiple concepts without skos:broader (or not referenced by a skos:narrower) exist,
									a dummy root is created under which these first level elements will be inserted.
								</li>
							</ul>
						</li>
						<li>To navigate the tree :
							<ul>
								<li>If on a concept scheme :
									<ul>
										<li>If some collections are marked skos:inScheme of that scheme, without being referenced as skos:member
										from other collections (indicating a top-level collection), they are inserted as children of the scheme in the tree.
										</li>
										<li>Otherwise, if the concept scheme indicates skos:hasTopConcepts, or if concepts are marked skos:hasTopConcept
										of that scheme, they are inserted as children of the scheme in the tree.
										</li>
										<li>Otherwise, if concepts without skos:broader (or not referenced as skos:narrower) exists in that scheme,
										they are inserted as children of the scheme in the tree.									
										</li>
									</ul>
								</li>
								<li>If on a collection :
									<ul>
										<li>If that collection indicates sub-collections via skos:member, these collection are inserted as children in the tree.
										</li>
										<li>Otherwise, concepts without skos:broader (or not referenced as skos:narrower) belonging to that collection 
										via skos:member are inserted as children in the tree.								
										</li>
									</ul>
								</li>
								<li>If on a concept :
									<ul>
										<li>concepts indicating a skos:broader to that concept, or indicated by a skos:narrower from that concept
											are inserted as children in the tree.
										</li>
									</ul>
								</li>
							</ul>
						</li>
					</ul>
				</p>
				<h4>What are the language supported ?</h4>
				<p>
					It depends on the data : the list of languages is dynamically fetched from the data.
				</p>
				<h4>What are the supported RDF syntaxes ?</h4>
				<p>
					All of the syntaxes supported by Sesame : RDF/XML, Turtle, N3, N-triples, TriG, TriX...
				</p>
				<h4>How many concepts can SKOS Play handle ?</h4>
				<p>
					For the time being, <b>SKOS Play is limited to 5000 concepts</b>, to avoid overloading the mini-server
					in my living room that host the application.
					<br />
					Theoretically, there is no limit, but in practice, for alphabetical or hierarchical rendering, I would avoid
					more than 5000 concepts to limit the output file size. For dataviv, I would say that above 2000 concepts you
					can't see anything and you have big latencies when zooming.
				</p>
				<h4>Is there a web service, an API ? is SKOS-XL supported ? do you support inference ?
				</h4>
				<p>
					No. No. No. But <a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank">vote</a>
					and express your interest, and this may be added one day in the future.
				</p>
				<h4>It's slow !</h4>
				<p>
					Yes. the application is hosted by a mini fanless server running in my living-room, on my personnal Internet connection.
					I am looking for organizations willing to host freely the application to remove the 5000 concepts limit. If you are interested,
					<a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">contact-me</a>.
				</p>
			</fieldset>

      	</div>
      	<jsp:include page="footer.jsp" />
	</body>
</html>