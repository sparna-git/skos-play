<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

			<div class="page-header">
				<div class="row">
			    	<div class="span6"><h1>SKOS Play !</h1></div>
			    	<div class="span6">
			    		<ul class="nav nav-pills pull-right">
			    			<li ${param.active == 'home' ? 'class="active"' : ''}><a href="home"><i class="icon-home"></i>&nbsp;<fmt:message key="menu.home" /></a></li>
			    			<li ${param.active == 'upload' ? 'class="active"' : ''}><a href="upload"><i class="icon-play"></i>&nbsp;<fmt:message key="menu.start" /></a></li>
					    	<li ${param.active == 'about' ? 'class="active"' : ''}><a href="about"><i class="icon-info-sign"></i>&nbsp;<fmt:message key="menu.about" /></a></li>
					    	<li><a href="https://groups.google.com/d/forum/skos-play-discuss" target="_blank"><i class="icon-user"></i>&nbsp;<fmt:message key="menu.feedback" /></a></li>
							<li class="dropdown">
								<a class="dropdown-toggle" data-toggle="dropdown" href="#">
									<c:choose>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'fr'}">fr</c:when>
										<c:when test="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language == 'de'}">de</c:when>
										<c:otherwise>en</c:otherwise>
									</c:choose>
									<b class="caret"></b>
								</a>
								<ul class="dropdown-menu">									
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
							</li>
					    </ul>
					</div>
			    </div>	      		
	      	</div>