package fr.sparna.assembly;


/**
 * An abstraction for objects capable of consuming the <code>Assembly</code> produced
 * by the <code>AssemblyLine</code>.
 * A consumer should be able to commit or rollback the given <code>Assembly</code>.
 * 
 * @author Thomas Francart
 */
public interface AssemblyConsumer<X> extends AssemblyLineComponent<X> {

	/**
	 * Consumes a single document generated through an <code>AssemblyLine</code>, typically
	 * sends it to a SolR server.
	 * 
	 * @param doc	The complete document to be consumed.
	 * @throws ConsumeException
	 */
	public void consume(Assembly<X> doc) throws ConsumeException;
	
	/**
	 * Commit the consumer, typically sends a commit command to a SolR server.
	 * This method is called by the <code>AssemblyLine</code> once the process is finished sucessfully.
	 * 
	 * @throws ConsumeException
	 */
	public void commit() throws ConsumeException;
	
	/**
	 * Rollback the consumer, typically send a rollback command to a SolR server.
	 * This method is called by the <code>AssemblyLine</code> if an error happen during documents processing.
	 * @throws ConsumeException
	 */
	public void rollback() throws ConsumeException;
	
}
