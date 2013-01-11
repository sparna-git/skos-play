package fr.sparna.rdf.sesame.toolkit.statistics;


/**
 * Renders the statistics gathered by a {@link fr.sparna.rdf.sesame.toolkit.statistics.StatisticsHandler StatisticsHandler}
 * 
 * @author Thomas Francart
 *
 */
public interface StatisticsRenderer {

	/**
	 * Renders the statistics of the handler in an appropriate way.
	 * 
	 * @param handler
	 */
	public void render(StatisticsHandler handler);
	
}
