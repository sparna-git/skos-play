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
		<title>SKOS Play ! - Visualiser des thesaurus SKOS - A propos</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
		
		<link rel="alternate" hreflang="en" href="?lang=en" />
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
				<legend>SKOS Play : présentation</legend>
				<h4>Qu'est-ce que "SKOS Play" ?</h4>
				<p>
					SKOS Play est un service de visualisation de thesaurus, taxonomies ou vocabulaires au format 
					<a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">SKOS</a>.
					Plus généralement il permet de visualiser ou imprimer un système d'organisation de connaissances exprimé en SKOS.
				</p>
				<h4>A quoi ça sert ?</h4>
				<p>
					<ul>
						<li>A générer des versions imprimables ou enregistrables localement de thesaurus ou de systèmes d'organisation de connaissances;</li>
						<li>A faire le pont entre des données SKOS et des visualisation de données de <a href="http://d3js.org" target="_blank">d3js</a>;</li>
						<li>A démontrer et illustrer le fonctionnement de certaines technologies sémantiques;</li>
					</ul>
				</p>
				<h4>L'utilisation est gratuite ?</h4>
				<p>
					Oui.
				</p>
				<h4>C'est open-source ?</h4>
				<p>
					Oui, le code est récupérable <a href="http://bitbucket.org/tfrancart/sparna">ici</a>.
					SKOS-Play a pour le moment une licence
					<a href="http://creativecommons.org/licenses/by-sa/3.0/deed.fr" target="_blank">CC-BY-SA</a>, en d'autres termes :
					<ul>
						<li>Vous pouvez utiliser l'application en ligne, ou télécharger le code et l'installer chez vous, y compris
						pour une utilisation commerciale;
						</li>
						<li>Si vous réutiliser l'application, vous devez citez son auteur ("Thomas Francart pour Sparna");</li>
						<li>Si vous modifiez le code, vous devez publier vos modifications sous cette mếme licence, le mieux étant
						d'apporter vos modifications directement dans <a href="http://bitbucket.org/tfrancart/sparna" target="_blank">les sources</a>;
						</li>
					</ul>					
					Enfin, <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">contactez-moi</a> si tout
					ça n'est pas clair (ça ne l'est pas pour moi non plus de toutes façons).
					<br />Cette licence pourra évoluer à l'avenir.
				</p>
				<h4>SKOS Play garde-t-il une copie des données soumises ? </h4>
				<p>Non.</p>
				<h4>Qui a développé SKOS Play ?</h4>
				<p>
					<a href="http://francart.fr" target="_blank">Thomas Francart</a> pour <a href="http://sparna.fr" target="_blank">Sparna</a>.
				</p>
				<h4>Quelles sont les licences des données d'exemple incluses ?</h4>
				<p>
					<ul>
						<li>Thesaurus EUROVOC : © European Union, 2013, <a href="http://eurovoc.europa.eu/" target="_blank">http://eurovoc.europa.eu/</a>.</li>
						<li>Thesaurus UNESCO : <a href="http://www.unesco.org/new/fr/terms-of-use/terms-of-use/copyright" target="_blank">copyright UNESCO</a>, thanks to <a href="http://skos.um.es/unescothes/" target="_blank">University of Murcia</a>.</li>
						<li>Thesaurus W : propriété des archives de France, données téléchargées <a href="http://www.archivesdefrance.culture.gouv.fr/gerer/classement/normes-outils/thesaurus/" target="_blank">ici</a> en mai 2013</li>
						<li>Descripteurs New-York Times : Creative Commons Attribution 3.0 United States License, New York Times Company. Données téléchargées <a href="http://data.nytimes.com">ici</a> en mai 2013.</li>
						<li><a href="http://data.reegle.info/thesaurus">Thesaurus Reegle sur l'énergie</a> : Données téléchargées <a href="http://poolparty.reegle.info/PoolParty/sparql/glossary">ici</a> en aout 2013, avec l'accord de l'éditeur.</li>
					</ul>
					<a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank">Suggérez d'autres données.</a>
				</p>
				<h4>J'ai une question, j'aimerais avoir la fonctionalité XYZ, ou j'aimerais contribuer.</h4>
				<p>
					Chouette !
					<ul>
						<li><a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank">votez, donnez vos idées et feedback sur le Google Moderator</a>;</li>
						<li>ou <a href="http://francart.fr/skos-play-generer-html-pdf-dataviz-thesaurus-skos">laissez un mot sur le blog</a>;</li>
						<li>ou <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">prenez contact par e-mail</a>;</li>
						<li>ou regardez <a href="http://bitbucket.org/tfrancart/sparna" target="_blank">le code</a>;</li>
					</ul>
				</p>
			</fieldset>

			<br />
			<br />
			
			<fieldset>
				<legend>SKOS</legend>
				<h4>Qu'est-ce que SKOS ?</h4>
				<p>
					SKOS est un modèle de données partagé pour échanger et relier des systèmes d'organisation de connaissances sur le Web.
					Ce modèle est défini par le W3C <a href="http://www.w3.org/TR/2009/REC-skos-reference-20090818/" target="_blank">ici</a>.
				</p>
				<h4>Où trouver des données SKOS ?</h4>
				<p><a href="http://www.w3.org/2001/sw/wiki/SKOS/Datasets" target="_blank">Ici</a></p>
				<h4>Comment écrire un fichier SKOS ?</h4>
				<p>
					Essayez <a href="http://sourceforge.net/projects/tematres/" target="_blank">Tematres</a> (open-source), le <a href="http://code.google.com/p/skoseditor/" target="_blank">plugin SKOS de Protégé</a> (open-source),
					<a href="http://thmanager.sourceforge.net/" target="_blank">ThManager</a> (open-source), <a href="https://github.com/culturecommunication/ginco" target="_blank">Ginco</a> (open-source). Il existe également des
					outils commerciaux.
					<br />
					Sinon vous pouvez simplement écrire à la main ou générer un fichier de cette forme, l'enregistrer avec l'extension *.ttl, en faisant attention
					que l'encodage du fichier soit UTF-8, et voilà :
					<pre>
# declarer cette entete en haut du fichier
@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix skos: &lt;http://www.w3.org/2004/02/skos/core#&gt; .
# modifier ce prefixe avec votre espace de nom
@prefix chezmoi: &lt;http://www.exemple.fr/&gt; .

# declarer un concept scheme qui represente votre thesaurus, une seule fois en haut du fichier
chezmoi:MonThesaurus a skos:ConceptScheme .
chezmoi:MonThesaurus skos:prefLabel "Le nom de mon thesaurus"@fr .

# ensuite declarer des concepts. En voila un
chezmoi:Vehicule a skos:Concept .
# pour chaque concept il faut dire qu'il fait partie du thesaurus
chezmoi:Vehicule skos:inScheme chezmoi:MonThesaurus .
# et il faut declarer son libellés préférentiel (celui qui sera affiché)
chezmoi:Vehicule skos:prefLabel "Vehicules"@fr .

# voila un deuxieme concept
chezmoi:Voiture a skos:Concept .
chezmoi:Voiture skos:inScheme chezmoi:MonThesaurus .
chezmoi:Voiture skos:prefLabel "Voiture"@fr .
# ca c'est un libellé alternatif, on peut en mettre plusieurs en repetant cette ligne
chezmoi:Voiture skos:altLabel "Bagnole"@fr .
# et la on declare que "Voiture" est un concept plus specifique que chezmoi:Vehicule
chezmoi:Voiture skos:broader chezmoi:Vehicule .

# voila un troisieme concept
chezmoi:123456 a skos:Concept .
chezmoi:123456 skos:inScheme chezmoi:MonThesaurus .
chezmoi:123456 skos:prefLabel "Vélo"@fr .
chezmoi:123456 skos:altLabel "Bicyclette"@fr .
chezmoi:123456 skos:altLabel "Biclou"@fr .
chezmoi:123456 skos:broader chezmoi:Vehicule .
					</pre>
				</p>
			</fieldset>
			
			<br />
			<br />
			
			<fieldset>
				<legend>SKOS Play : fonctionnement</legend>
				<h4>Comment fonctionne SKOS Play ?</h4>
				<p>
					SKOS Play est basé sur <a href="http://openrdf.org">OpenRDF Sesame</a> et utilise notamment le composant <a href="http://tfrancart.bitbucket.org/sesame-toolkit/apidocs/fr/sparna/rdf/sesame/toolkit/Documentation.html" target="_blank">sesame-toolkit</a>.
					Les visualisations de données sont faites avec <a href="http://d3js.org" target="_blank">d3js</a>, les PDF avec <a href="http://xmlgraphics.apache.org/fop/" target="_blank">Apache FOP</a>.
					La génération des rendus est faite avec des requêtes SPARQL.
					<br />
					Schématiquement, il y a 4 niveaux logiques dans l'application :
					<ol>
						<li>Le chargement/traitement du RDF avec Sesame et <a href="http://tfrancart.bitbucket.org/sesame-toolkit/apidocs/fr/sparna/rdf/sesame/toolkit/Documentation.html" target="_blank">sesame-toolkit</a>;</li>
						<li>Les requêtes spécifiques au modèle de données SKOS (voir l'algo de parcours d'arbre plus bas);</li>
						<li>La transformation des données SKOS en structure "imprimable" en utilisant JAXB, puis de cette structure en HTML ou PDF avec des XSLT;</li>
						<li>Le code des écrans de l'application, servlets, JSP & JSPL, Jquery, Bootstrap.</li>
					</ol>
				</p>
				<h4>Comment est générée la structure d'arbre à partir du SKOS ?</h4>
				<p>
					Bonne question. Le modèle SKOS est lâche et des données SKOS peuvent se structurer de plusieurs façons, il faut donc faire des choix.
					Voici l'algorithme qui est suivi par SKOS Play :
					<ul>
						<li>Pour déterminer la racine :
							<ul>
								<li>Si au moins un schema de concepts existe, on doit en choisir un dans l'interface, ce sera la racine.</li>
								<li>Sinon, si une et une seule collection existe, elle sera prise comme racine.</li>
								<li>Sinon, si un et un seul concept sans broader (ou pas référencé par un narrower) existe, il sera pris comme racine.</li>
								<li>Sinon, si de multiples collections ou de multiples concepts sans broader (ou pas référencé par un narrower) existent,
									une racine fictive est créée sous laquelle ces éléments de premier niveau sont insérés.
								</li>
							</ul>
						</li>
						<li>Pour parcourir l'arbre :
							<ul>
								<li>Si on est sur un schéma de concept :
									<ul>
										<li>Si des collections marquées skos:inScheme de ce schéma de concepts existent, et qui ne sont pas référencées
											comme skos:member d'une autres collection, elles sont insérées comme fils du schéma de concepts dans l'arbre.
										</li>
										<li>Sinon, si le schéma de concepts indique des skos:hasTopConcepts, ou bien si des concepts sont indiqués
										skos:isTopConceptOf de ce schéma, ces concepts sont insérés comme fils du schéma de concepts dans l'arbre.
										</li>
										<li>Sinon, si des concepts sans skos:broader (ou pas référencés comme skos:narrower) existent dans ce schéma,
										ils sont insérés comme files du schéma de concepts dans l'arbre.									
										</li>
									</ul>
								</li>
								<li>Si on est sur une collection :
									<ul>
										<li>Si cette collection indique des sous-collections via un skos:member, ces collections sont insérées comme
										fils dans l'arbre.
										</li>
										<li>Sinon, les concepts sans skos:broader (ou pas référencés comme skos:narrower) appartenant à cette collection
										via des skos:member sont insérés comme fils.								
										</li>
									</ul>
								</li>
								<li>Si on est sur un concept :
									<ul>
										<li>Les concepts qui indiquent un skos:broader vers ce concept, ou qui sont indiqués par un skos:narrower par
											ce concept, sont insérés comme fils.
										</li>
									</ul>
								</li>
							</ul>
						</li>
					</ul>
				</p>
				<h4>Quelles sont les langues supportées ?</h4>
				<p>
					Cela dépend des données : la liste des langues possibles est récupérée dans les données.
				</p>
				<h4>Quels sont les syntaxes RDF supportées ?</h4>
				<p>
					Toutes celles supportées par Sesame, donc RDF/XML, Turtle, N3, N-triples, TriG, TriX...
				</p>
				<h4>Combien de concepts SKOS Play peut-il traiter ?</h4>
				<p>
					Pour le moment, <b>SKOS Play accepte un maximum de 5000 concepts</b>, pour ne pas surcharger
					le mini-serveur qui héberge l'application.
					<br />
					En théorie, il n'y a pas de limite, mais de façon pratique, pour les rendus liste de concepts, alphabétique et hiérarchique,
					j'éviterais d'envoyer plus de 5000 concepts pour ne pas avoir des fichiers de sortie énormes.
					Pour les visualisations, je dirai pas plus de 2000 concepts, sinon on ne voit plus rien.
				</p>
				<h4>Est-ce qu'il y a un service web, une API ? est-ce que SKOS-XL est supporté ? est-ce qu'il y a de l'inférence ?
				</h4>
				<p>
					Non. Non. Non. Mais <a href="http://www.google.com/moderator/#15/e=209fff&t=209fff.40" target="_blank">votez</a> pour exprimer votre intérêt et cela existera peut-être un jour.
				</p>
				<h4>C'est lent !</h4>
				<p>
					Oui. L'application est hébergée sur un mini-serveur dans mon salon, à côté de ma Freebox. Je recherche des personnes susceptibles
					de founir gracieusement un hébergement pour l'application pour enlever la limite des 5000 concepts.
					Si vous êtes intéressés, <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">contactez-moi</a>.
				</p>
			</fieldset>

      	</div>
      	<jsp:include page="footer.jsp" />
	</body>
</html>