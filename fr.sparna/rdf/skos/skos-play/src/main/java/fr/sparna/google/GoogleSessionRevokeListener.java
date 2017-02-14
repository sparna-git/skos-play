package fr.sparna.google;

import java.io.IOException;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.skosplay.SessionData;

public class GoogleSessionRevokeListener implements HttpSessionListener {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// log.debug(this.getClass().getName()+" - "+"sessionCreated called.");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		// log.debug(this.getClass().getName()+" - "+"sessionDestroyed called.");
		if(SessionData.get(event.getSession()) != null) {
			if(SessionData.get(event.getSession()).getGoogleConnector() != null) {
				try {
					// see http://stackoverflow.com/questions/21405274/this-app-would-like-to-have-offline-access-when-access-type-online
					SessionData.get(event.getSession()).getGoogleConnector().revokeToken();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
