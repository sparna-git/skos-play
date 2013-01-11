package fr.sparna.rdf.sesame.toolkit.query.builder;

/**
 * Specifies an "ORDER BY" criteria in a SPARQL query : the String representing
 * the field to order on, and a boolean indicating if the ordering should be in
 * ascending or descending order.
 * 
 * <p>This is used by {@link PagingSPARQLQueryBuilder}.
 * 
 * @author Thomas Francart
 */
public class OrderBy {

	private String order;
	private boolean ascending;
	
	/**
	 * Constructs an OrderBy with the given order and a flag indiating if ordering
	 * should be in ascending order.
	 * 
	 * @param order		The field to order on
	 * @param ascending	True to specify ascending order, False to specify descending order
	 */
	public OrderBy(String order, boolean ascending) {
		super();
		this.order = order;
		this.ascending = ascending;
	}
	
	/**
	 * Constructs an OrderBy on the given field in ascending order.
	 * 
	 * @param order	The field to order on
	 */
	public OrderBy(String order) {
		this(order, true);
	}

	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public boolean isAscending() {
		return ascending;
	}
	
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
}
