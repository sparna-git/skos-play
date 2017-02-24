     
     
      
      function drawChartjour() {
    	  var data = new google.visualization.DataTable();
    	  data.addColumn('string', 'Jour');
    	  data.addColumn('number', 'Print/Visualize');
    	  data.addColumn('number', 'Convert');  
    	  data.addRows([
		    	         <c:forEach items="${data.nbreConvertPrintParJour}" var="stat">
		    	        		
		    	         	["${stat.day}",${stat.nbrePrint},${stat.nbreConvert}],
		    	         
		    	        </c:forEach>
    	        ]);
    	  
    	  var view = new google.visualization.DataView(data);
    	  view.setColumns([0, 1, 2]);  
    	  var options = {
  	            title: 'Nombre de print et convert par jour',
  	            colors: ['#FF7F50','#008B8B'],
  	            is3D: true,
  	            isStacked : true,
  	            legend : 'bottom',

  	          };
        var chart = new google.visualization.ColumnChart(
            document.getElementById('divjour'));
        chart.draw(view, options);
       
      }
 /*---------------------------satistique par mois-----------------------------------*/     
      function drawChartmois() {
    	  var data = new google.visualization.DataTable();
    	 
    	  data.addColumn('number', 'Mois');
    	  data.addColumn('number', 'Print/Visualize');
    	  data.addColumn('number', 'Convert');
    	  data.addRows([
		    	         <c:forEach items="${data.nbreConvertPrintParMois}" var="stat">
		    	        	
		    	         	[${stat.mois},${stat.nbrePrint},${stat.nbreConvert}],
		    	         
		    	        </c:forEach>
		    	      ]);
    	  
    	  var view = new google.visualization.DataView(data);
    	  view.setColumns([0, 1, 2]);  
    	  var options = {
    	            title: 'Nombre de print et convert par mois',
    	            colors: ['#FF7F50','#008B8B'],
    	            is3D: true,
    	            isStacked : true,
    	            legend : 'bottom',

    	          };
    	  
        var chart = new google.visualization.ColumnChart(
            document.getElementById('divmois'));
        chart.draw(view,options);
       
      }
      /*---------------------------satistique par annee-----------------------------------*/     
      function drawChartannee() {
    	  var data = new google.visualization.DataTable();
    	  data.addColumn('string', 'Annee');
    	  data.addColumn('number', 'Print/Visualize');
    	  data.addColumn('number', 'Convert');
    	  
    	  data.addRows([
		    	         <c:forEach items="${data.nbreConvertPrintParAnnee}" var="stat">
		    	        	
		    	         	["${stat.annee}",${stat.nbrePrint},${stat.nbreConvert}],
		    	         
		    	        </c:forEach>
		    	      ]);
    	  
    	  var view = new google.visualization.DataView(data);
    	  view.setColumns([0, 1, 2]);  
    	  var options = {
  	            title: 'Nombre de print et convert par annee',
  	            colors: ['#FF7F50','#008B8B'],
  	            is3D: true,
  	            isStacked : true,
  	            legend : 'bottom',

  	          };
        var chart = new google.visualization.ColumnChart(
            document.getElementById('divannee'));
        chart.draw(view,options);
       
      }