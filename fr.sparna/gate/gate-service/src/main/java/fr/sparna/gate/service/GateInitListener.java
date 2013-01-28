package fr.sparna.gate.service;

import gate.Gate;
import gate.util.GateException;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener qui est appelé une seule fois au moment du déploiement de l'application
 * @author thomas
 *
 */
public class GateInitListener implements ServletContextListener {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * Nom du chemin local vers le répertoire des ressources GATE (passé comme "gate home".
	 */
	private static final String DEFAULT_GATE_HOME = "gate";
	
	/**
	 * GATE User Config File Name
	 */
	private static final String GATE_USER_CONFIG_FILE_NAME = "user-gate.xml";
	
	/**
	 * Flag qui indique si on a correctement initialisé Gate
	 */
	public static boolean gateInitialized = false;
	
	@Override
	public void contextInitialized(ServletContextEvent e) {
		try {
			this.initGate();
			gateInitialized = true;
		} catch (GateServletException ex) {
			ex.printStackTrace();
			gateInitialized = false;
			log.error("Gate not correctly initialized.");
		}		
	}
	
	/**
	 * Appelé au moment du _dé_chargement de l'application (arrêt du serveur ou
	 * mise à jour)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		// nothing		
	}

	private void initGate() throws GateServletException {
		// TODO : setup alternative gate.home
		File gateHome = new File(DEFAULT_GATE_HOME);
		
		log.info("Initializing GATE...");
		
		if (gateHome!=null && gateHome.exists() && gateHome.isDirectory()) {
			log.info("GATE Home absolute path "+gateHome.getAbsolutePath());

			if (Gate.getGateHome()==null) {
				// we set gateHome !
				Gate.setGateHome(gateHome);
			} else {
				log.trace("get home already set");
			}

			// <gate.home>/plugins is the plugins directory
			// <gate.home>/gate.xml is the site config file.
			// Use <gate.home>/user-gate.xml as the user config file, to avoid confusion with your own user config.

			if (Gate.getUserConfigFile()==null) {
				Gate.setUserConfigFile(new File(gateHome, GATE_USER_CONFIG_FILE_NAME));
			} //else do nothing.

			try {
				// initialise GATE - this must be done before calling any GATE APIs
				Gate.init();				
				log.info("Successfully initialized GATE !");
			} catch (GateException e) {
				throw new GateServletException("unexpected GATE error !",e);
			}
		} else {
			// le repertoire GATE_HOME n'existe pas - on renvoie une exception
			log.error("error searching GateResources");
			throw new GateServletException("error searching GateResources at : "+DEFAULT_GATE_HOME);
		}
	}

}
