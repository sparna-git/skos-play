<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" 	prefix="fmt" 	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	prefix="c" 		%>

<html>
	<head>
		<title>Le pressoir à page web</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" />
		<link href="css/blog.css" rel="stylesheet" />
	</head>
	  <body>

    <div class="blog-masthead">
      <div class="container">
        <nav class="blog-nav">
          <a class="blog-nav-item active" href="acceuil">Accueil</a>
          <a class="blog-nav-item" href="presser">Presser</a>
        </nav>
      </div>
    </div>

    <div class="container">

      <div class="blog-header">
        <h1 class="blog-title">Pressoir à page web</h1>
        <p class="lead blog-description">Un extracteur de données structurées</p>
      </div>

      <div class="row">

        <div class="col-sm-8 blog-main">

          <div class="blog-post">
            <h2 class="blog-post-title">La métaphore</h2>
            <br />
            <h4>Le pressoir à page web</h4>
			<p>
			Tel un <b>pressoir</b> qui extrait le jus de raisins, le suc, le nectar, cet extracteur <b>"presse" les contenus</b> qu'on lui soumet pour en extraire
			les données structurées. Il laisse de côté le "moût", le rebus, les peaux et les pépins, tout ce qui n'est pas utilisable par une machine
			pour faire du bon vin, de la bonne <b>data</b> : la mise en forme, le texte pour l'humain, les menus de navigation, etc. Seules sont extraites les <b>informations
			dites "sémantiques"</b>, celles qui ont été explicitement structurées ou marquées comme telles.
			</p>
			<h4>La cuve à data</h4>
			<p>
			Filons la métaphore : après avoir été pressé, le jus est stocké dans une <b>cuve</b>. Ici, nous avons notre "cuve à data", c'est une base de données qui aggrège toutes les
			extractions juteuses de notre pressoir.
			</p>
			<h4>Décanter le jus</h4>
			<p>
			A venir...
			</p>
			<h4>Grappes et grains de data</h4>
			<p>
			A venir...
			</p>
          </div><!-- /.blog-post -->
          
          <div class="blog-post">
           <h2 class="blog-post-title">Pressons, pressons !</h2>
			<br />
			<h4>Presser le jus</h4>
			<ul>
				<li>/api/v1/extract?uri=&lt;mon uri&gt;</li>
				<li><a href="api/v1/extract?uri=http://sparna.fr">/api/v1/extract?uri=http://sparna.fr</a></li>
				<li><a href="api/v1/extract?uri=http://grify.coopaxis.fr">/api/v1/extract?uri=http://grify.coopaxis.fr</a></li>
				<li><a href="api/v1/extract?uri=https://www.legifrance.gouv.fr/eli/loi/2016/10/7/ECFI1524250L/jo/texte">/api/v1/extract?uri=https://www.legifrance.gouv.fr/eli/loi/2016/10/7/ECFI1524250L/jo/texte</a> </li>
			</ul>
			<h4>Presser et Stocker dans la cuve</h4>
			<ul>
				<li>/api/v1/store?uri=&lt;mon uri&gt;</li>
				<li><a href="api/v1/store?uri=http://sparna.fr">/api/v1/store?uri=http://sparna.fr</a></li>
				<li><a href="api/v1/store?uri=http://grify.coopaxis.fr">/api/v1/store?uri=http://grify.coopaxis.fr</a></li>
			</ul>
          </div><!-- /.blog-post -->


        </div><!-- /.blog-main -->

        <div class="col-sm-3 col-sm-offset-1 blog-sidebar">
          <div class="sidebar-module">
            <a href="https://fr.wikipedia.org/wiki/Pressoir#/media/File:Pressoir_texte.jpg"><img src="images/pressoir_texte.jpg" style="width:100%" /></a>
          </div>
        
          <div class="sidebar-module sidebar-module-inset">
            <h4>A propos</h4>
            <p>Le pressoir à data pour Grify</p>
          </div>

          <div class="sidebar-module">
            <h4>Liens</h4>
            <ol class="list-unstyled">
              <li><a href="http://167.114.233.33:8080/openrdf-workbench/repositories/grify/summary">La cuve à data</a></li>
              <li><a href="http://grify.coopaxis.fr">Grify</a></li>
              <li><a href="http://www.sparna.fr">Sparna</a></li>
              <li><a href="http://www.schema.org">Schema.org</a></li>
            </ol>
          </div>
        </div><!-- /.blog-sidebar -->

      </div><!-- /.row -->

    </div><!-- /.container -->

    <footer class="blog-footer">
      <p>
        <a href="#">Back to top</a>
      </p>
    </footer>

	
	    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	    <!-- Include all compiled plugins (below), or include individual files as needed -->
	    <script src="bootstrap/js/bootstrap.min.js"></script>
	  </body>
</html>