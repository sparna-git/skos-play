package fr.sparna.commons.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Si on a configuré une Datasource dans le serveur, cette classe permet d'obtenir
 * une Connection à partir de la Datasource
 * 
 * @see http://keithrieck.wordpress.com/2010/01/28/h2-database-on-tomcat/
 * @see http://www.javapractices.com/topic/TopicAction.do?Id=183
 * 
 * @author thomas
 *
 */
public class JNDIConnectionSource implements ConnectionSource {

	/**
	 * Le nom JNDI de la datasource
	 */
	protected String datasourceJndi;
	
	public JNDIConnectionSource(String datasourceJndi) {
		super();
		this.datasourceJndi = datasourceJndi;
	}

	/**
	 *  Return a {@link Connection} to the database
	 */
	@Override
	public final Connection getConnection() throws ConnectionSourceException {
		Connection result = null;

		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup(datasourceJndi);
			if ( datasource == null ){
				System.err.println("Datasource is null for : " + datasourceJndi);
			}
			
			result = datasource.getConnection();
		}
		catch (NamingException ex){
			throw new ConnectionSourceException(
					"Config error with JNDI and datasource, for db " + datasourceJndi, ex
					);
		}
		catch (SQLException ex ){
			throw new ConnectionSourceException(
					"Cannot get JNDI connection from datasource, for db " + datasourceJndi, 
					ex
					);
		}
		return result;
	}

}
