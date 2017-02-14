<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<c:set var="data" value="${sessionScope['fr.sparna.rdf.skosplay.SessionData']}" />

<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

			<div class="page-header">
				<div class="container-fluid">
 			    	<div class="col-md-3" id="header-logo"><h1>SKOS Play !</h1></div>
			    	<div class="col-md-9" id="header-menu">
			    		<ul class="nav nav-pills navbar-right">
			    			<li id="header-pill-home" ${param.active == 'home' ? 'class="active"' : ''}><a href="home"><i class="glyphicon glyphicon-home"></i>&nbsp;<fmt:message key="menu.home" /></a></li>
			    			<li id="header-pill-upload" ${param.active == 'upload' ? 'class="active"' : ''}><a href="upload"><i class="glyphicon glyphicon-play"></i>&nbsp;<fmt:message key="menu.start" /></a></li>
			    			<li id="header-pill-convert" ${param.active == 'convert' ? 'class="active"' : ''}><a href="convert"><i class="glyphicon glyphicon-list-alt"></i>&nbsp;<fmt:message key="menu.convert" /></a></li>
					    	<li id="header-pill-about" ${param.active == 'about' ? 'class="active"' : ''}><a href="about"><i class="glyphicon glyphicon-info-sign"></i>&nbsp;<fmt:message key="menu.about" /></a></li>
					    	<li id="header-pill-forum"><a href="https://groups.google.com/d/forum/skos-play-discuss" target="_blank"><i class="glyphicon glyphicon-comment"></i>&nbsp;<fmt:message key="menu.feedback" /></a></li>
					    	<li id="header-pill-connexion">
						    	
						    	<c:if test="${data.user!= null}">
										<a><i class="glyphicon glyphicon-user"></i>&nbsp; ${data.user.name}</a>	
								</c:if>
								<c:if test="${data.user== null}">
								<!-- https://www.googleapis.com/auth/spreadsheets.readonly -->
									<a 
							    	href="${data.googleConnector.generateLoginUrl()}">
							    	<i class="glyphicon glyphicon-user"></i>&nbsp;login
							    	</a>
								</c:if>
					    	</li>
					    	<li><a href="log">log</a></li>
							<li id="header-pill-lang" >
								
						  		<a class="dropdown-toggle" data-toggle="dropdown" href="#">
								    <c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}">fr</c:when>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'de'}">de</c:when>
										<c:otherwise>en</c:otherwise>
									</c:choose>
								    <b class="caret"></b>
								  </a>
								  <ul class="dropdown-menu" >
								    <c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}">
											<li><a href="?lang=en">en</a></li>
											<li><a href="?lang=de">de</a></li>
										</c:when>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'de'}">
											<li><a href="?lang=en">en</a></li>
											<li><a href="?lang=fr">fr</a></li>
										</c:when>
										<c:otherwise>
											<li><a href="?lang=fr">fr</a></li>
											<li><a href="?lang=de">de</a></li>
										</c:otherwise>
									</c:choose>	
								  </ul>
								  </ul>
								</div>
						
						  		
								</div>
							
					</div>
    	