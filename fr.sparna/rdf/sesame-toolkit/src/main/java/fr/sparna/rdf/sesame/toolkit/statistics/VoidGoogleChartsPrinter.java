package fr.sparna.rdf.sesame.toolkit.statistics;

import org.eclipse.rdf4j.repository.Repository;

public class VoidGoogleChartsPrinter {

	protected String outputFilePath;
	
	public VoidGoogleChartsPrinter(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public void render(Repository repository) {

		// init writer
		// PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath)));

		
		/*
		 
		 <html>
		  <head>
		    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
		    <script type="text/javascript">
		      google.load("visualization", "1", {packages:["corechart"]});
		      google.setOnLoadCallback(drawChart);
		      function drawChart() {
		        var data = google.visualization.arrayToDataTable([
		          ['Task', 'Hours per Day'],
		          ['Work',     11],
		          ['Eat',      2],
		          ['Commute',  2],
		          ['Watch TV', 2],
		          ['Sleep',    7]
		        ]);
		
		        var options = {
		          title: 'My Daily Activities'
		        };
		
		        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
		        chart.draw(data, options);
		      }
		    </script>
		  </head>
		  <body>
		    <div id="chart_div" style="width: 900px; height: 500px;"></div>
		  </body>
		</html>
		 
		 
		 */
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
