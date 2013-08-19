<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.skosplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.skosplay.i18n.Bundle"/>

      	<footer>
      	SKOS Play! par <a href="http://francart.fr" target="_blank">Thomas Francart</a> pour <a href="http://sparna.fr" target="_blank">Sparna</a>
      	&nbsp;|&nbsp;
      	<a rel="license" href="http://creativecommons.org/licenses/by-sa/3.0/deed.fr"><img alt="Licence Creative Commons" style="border-width:0" src="http://i.creativecommons.org/l/by-sa/3.0/88x31.png" /></a>
      	&nbsp;|&nbsp;
      	version : ${applicationScope.applicationData.buildTimestamp}
      	<br />
      	<br />
      	</footer>