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
		<title><c:out value="${applicationData.skosPlayConfig.applicationTitle}" /></title>
		<link rel="canonical" href="https://skos-play.sparna.fr/play/about?lang=fr" />

		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="bootstrap-fileupload/bootstrap-fileupload.min.css" rel="stylesheet" />
		<link href="css/skos-play.css" rel="stylesheet" />
		<link href="style/custom.css" rel="stylesheet" />
		<script src="js/jquery.min.js"></script>
		<script src="bootstrap/js/bootstrap.min.js"></script>
		<script src="bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
		
		<link rel="alternate" hreflang="en" href="?lang=en" />
	</head>
	<body>
		<div class="container">
			<%-- see http://stackoverflow.com/questions/19150683/passing-parameters-to-another-jsp-file-in-jspinclude --%>
			<jsp:include page="header.jsp">
				<jsp:param name="active" value="about"/>
			</jsp:include>
			
			<fieldset>
				<legend>SKOS Play : présentation</legend>
				<h4>Qu'est-ce que "SKOS Play" ?</h4>
				<p>
					SKOS Play est un service de visualisation de thesaurus, taxonomies ou vocabulaires au format 
					<a href="http://www.sparna.fr/skos/SKOS-traduction-francais.html" target="_blank">SKOS</a>.
					<p />Plus généralement il permet de visualiser ou imprimer un système d'organisation de connaissances exprimé en SKOS, et de démontrer
					certains principes du web de données</p>
					<p/>SKOS Play permet également de <a href="convert">générer des fichiers SKOS à partir de tableaux Excel</a>.
				</p>
				<h4>A quoi ça sert ?</h4>
				<p>
					<ul>
						<li>A générer des versions imprimables ou enregistrables localement de thesaurus ou de systèmes d'organisation de connaissances;</li>
						<li>A tester un vocabulaire quand on est en train de le mettre au point, à le valider avec des experts du domaine;</li>
						<li>A publier des versions imprimables des thesaurus sur le web;</li>
						<li>A faire le pont entre des données SKOS et des visualisation de données de <a href="http://d3js.org" target="_blank">d3js</a>;</li>
						<li>A démontrer et illustrer le fonctionnement de certaines technologies du web de données;</li>
					</ul>
				</p>
				<h4>L'utilisation est gratuite ?</h4>
				<p>
					Oui.
				</p>
				<h4>C'est open-source ?</h4>
				<p>
					Oui, le code est récupérable <a href="https://github.com/sparna-git/skos-play">ici</a>.
					SKOS-Play a pour le moment une licence
					<a href="http://creativecommons.org/licenses/by-sa/3.0/deed.fr" target="_blank">CC-BY-SA</a>, en d'autres termes :
					<ul>
						<li>Vous pouvez utiliser l'application en ligne, ou télécharger le code et l'installer chez vous, y compris
						pour une utilisation commerciale;
						</li>
						<li>Si vous réutilisez l'application, vous devez citez son auteur ("Thomas Francart pour Sparna");</li>
						<li>Si vous modifiez le code, vous devez publier vos modifications sous cette mếme licence, le mieux étant
						d'apporter vos modifications directement dans <a href="https://github.com/sparna-git/skos-play" target="_blank">les sources</a>;
						</li>
					</ul>					
					Enfin, <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">contactez-moi</a> si tout
					ça n'est pas clair.
					<br />Cette licence pourra évoluer à l'avenir.
				</p>
				<h4>SKOS Play garde-t-il une copie des données soumises ? </h4>
				<p>Non.</p>
				<h4>Qui a développé SKOS Play ?</h4>
				<p>
					<a href="http://blog.sparna.fr" target="_blank">Thomas Francart</a> pour <a href="http://sparna.fr" target="_blank">Sparna</a>.
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
					<a href="https://groups.google.com/d/forum/skos-play-discuss" target="_blank">Suggérez d'autres données.</a>
				</p>
				<h4>J'ai une question, j'aimerais avoir la fonctionalité XYZ, ou j'aimerais contribuer.</h4>
				<p>
					Chouette !
					<ul>
						<li><a href="https://groups.google.com/d/forum/skos-play-discuss" target="_blank">discutez-en sur le forum</a>;</li>
						<li>ou <a href="http://blog.sparna.fr">laissez un mot sur le blog</a>;</li>
						<li>ou <a href="m&#x61;ilto:t&#x68;om&#x61;s.fr&#x61;nc&#x61;&#x72;t@sp&#x61;&#x72;na&#46;fr">prenez contact par e-mail</a>;</li>
						<li>ou laissez un ticket sur <a href="https://github.com/sparna-git/skos-play" target="_blank">le Github</a>;</li>
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
					Ce modèle est défini par le W3C <a href="http://www.sparna.fr/skos/SKOS-traduction-francais.html" target="_blank">ici</a>.
				</p>
				<h4>Où trouver des données SKOS ?</h4>
				<p><a href="http://www.w3.org/2001/sw/wiki/SKOS/Datasets" target="_blank">Ici</a></p>
				<h4>Comment écrire un fichier SKOS ?</h4>
				<p>Le plus direct est d'utiliser le <a href="convert">générateur de fichiers SKOS à partir de Excel fourni par SKOS Play</a>.</p>
				<p>
					Essayez <a href="http://sourceforge.net/projects/tematres/" target="_blank">Tematres</a> (open-source),  <a href="https://github.com/culturecommunication/ginco" target="_blank">Ginco</a> (open-source).
					Il existe également des	outils commerciaux (mais c'est plus char !).
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
					SKOS Play est basé sur <a href="http://rdf4j.org">Eclipse RDF4J</a>.
					Les visualisations de données sont faites avec <a href="http://d3js.org" target="_blank">d3js</a>, les PDF avec <a href="http://xmlgraphics.apache.org/fop/" target="_blank">Apache FOP</a>.
					La génération des rendus est faite avec des requêtes SPARQL.
					<br />
					Schématiquement, il y a 4 niveaux logiques dans l'application :
					<ol>
						<li>Le chargement/traitement du RDF avec RDF4J;</li>
						<li>Les requêtes spécifiques au modèle de données SKOS (voir l'algo de parcours d'arbre plus bas);</li>
						<li>La transformation des données SKOS en structure "imprimable" en utilisant JAXB, puis de cette structure en HTML ou PDF avec des XSLT;</li>
						<li>Le code des écrans de l'application, servlets, JSP & JSPL, Jquery, Bootstrap.</li>
					</ol>
				</p>
				<h4>Comment est générée la structure d'arbre à partir du SKOS ?</h4>
				<p>Bonne question. Le modèle SKOS est lâche et des données SKOS peuvent se structurer de plusieurs façons, il faut donc faire des choix.</p>
				<p>Une partie de l'algorithme suivi par SKOS Play repose sur l'interprétation de Collections comme des <a href="http://purl.org/iso25964/skos-thes#ThesaurusArray">ThesaurusArray</a>.
					Un ThesaurusArray est une Collection qui a explicitement ce type, ou dont on trouve qu'elle ne contient que des Concepts qui ont le même parent, ou que des Concepts
					qui n'ont pas de parent.
				</p>
				<p>
					Voici l'algorithme qui est suivi par SKOS Play :
						<ul>
							<li>On commence par l'URI du ConceptScheme sélectionné comme racine (s'il y en a une - c'est le cas la plupart du temps. Sinon SKOS Play essaiera de déterminer une racine "au mieux").</li>
							<li>Si on est sur un schéma de concept :
								<ul>
									<li>Si des collections marquées skos:inScheme de ce schéma de concepts existent, et qui ne sont pas référencées
										comme skos:member d'une autres collection, et qui ne sont pas des ThesaurusArray, elles sont insérées comme fils du schéma de concepts dans l'arbre.
									</li>
									<li>Sinon, si 1/ aucun concept ayant des broaders ou narrowers n'est trouvé (cad le ConceptScheme est une liste à plat) et 2/ tous
									les concepts de ce ConceptScheme appartiennent à un ThesaurusArray (cad le ConceptScheme est entièrement partitionné), alors seuls ces
									ThesaurusArray sont mis comme fils du ConceptScheme.</li>
									<li>Sinon, si aucune Collection "de premier niveau" n'a été trouvée, et qu'il y une hiérarchie ou que le partitionnement du ConceptScheme
									en ThesaurusArray est incomplet, alors on cherche les Concepts :
										<ul>
											<li>si le schéma de concepts indique des skos:hasTopConcepts, ou bien si des concepts sont indiqués
											skos:isTopConceptOf de ce schéma, ces concepts sont insérés comme fils du schéma de concepts dans l'arbre.
											</li>
											<li>Sinon, si des concepts sans skos:broader (ou pas référencés comme skos:narrower) existent dans ce schéma,
											ils sont insérés comme files du schéma de concepts dans l'arbre.									
											</li>
											<li>En plus de cela, on essaie de ramener sous le ConceptScheme les Collections qui sont considérées comme des ThesaurusArray
											contenant seulement des Concepts racines. C'est-à-dire les Collections contenant uniquement des Concepts sans parent.
											</li>
										</ul>
									</li>
								</ul>
							</li>
							<li>Si on est sur une Collection "normale" qui n'est pas un ThesaurusArray :
								<ul>
									<li>On cherche toutes les Collections référencés par un skos:member depuis cette Collection, et
									les concepts sans skos:broader (ou pas référencés comme skos:narrower) référencés par un skos:inScheme.								
									</li>
								</ul>
							</li>
							<li>Si on est sur une Collection qui est un ThesaurusArray :
								<ul>
									<li>On cherche tous les Concepts référencés par un skos:member depuis cette Collection.
									Par construction, ce sont des Concepts qui ont le même parent ou pas de parent du tout.</li>
								</ul>
							</li>
							<li>Si on est sur un concept :
								<ul>
									<li>On insère comme fils les ThesaurusArray qui sont des tableaux de Concepts fils de ce Concept</li>
									<li>Les concepts qui indiquent un skos:broader vers ce concept, ou qui sont indiqués par un skos:narrower par
										ce concept, sont insérés comme fils, seulement s'ils ne font pas partie d'un ThesaurusArray de Concepts fils de ce Concept.
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
					Toutes celles supportées par RDF4J, donc RDF/XML, Turtle, N3, N-triples, TriG, TriX...
				</p>
				<h4>Combien de concepts SKOS Play peut-il traiter ?</h4>
				<p>
					Pour le moment, <b>SKOS Play accepte un maximum de 5000 concepts</b>.
					<br />
					En théorie, il n'y a pas de limite, mais de façon pratique, pour les rendus liste de concepts, alphabétique et hiérarchique,
					j'éviterais d'envoyer plus de 5000 concepts pour ne pas avoir des fichiers de sortie énormes.
					Pour les visualisations, je dirais pas plus de 2000 concepts, sinon on ne voit plus rien.
				</p>
				<h4>Est-ce que SKOS-XL est supporté ?</h4>
				<p>Oui ! il y a une option lors de la génération pour lire les <a href="http://www.sparna.fr/skos/SKOS-traduction-francais.html#xl" target="_blank">libellés en SKOS-XL</a>. Décochez l'option si vous voulez gagner un peu de temps.</p>
				<h4>Est-ce qu'il y a un service web, une API ? est-ce ça fait le café ?
				</h4>
				<p>
					Non. Non. Mais <a href="https://groups.google.com/d/forum/skos-play-discuss" target="_blank">parlez-en sur le forum</a> pour exprimer votre intérêt et cela existera peut-être un jour.
				</p>
			</fieldset>

      	</div>
      	<jsp:include page="footer.jsp" />
	</body>
</html>