package fr.sparna.gate.service;

import gate.CorpusController;
import gate.Factory;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GateServiceApplication {

	/**
	 * Nombre d'applications dans le pool = nombre maximal d'appels concurrents
	 */
	private static final int POOL_SIZE = 3;

	/**
	 * Chemin vers le fichier gapp relatif à gate.home
	 */
	private String applicationPath;

	/**
	 * Le pool d'applications gate
	 */
	private BlockingQueue<CorpusController> pool;

	public GateServiceApplication(String applicationPath) {
		super();
		this.applicationPath = applicationPath;
	}

	public void init() throws PersistenceException, ResourceInstantiationException, IOException {
		pool = new LinkedBlockingQueue<CorpusController>();
		for(int i = 0; i < POOL_SIZE; i++) {
			pool.add(createApplication());
		}
	}
	
	public void destroy() {
		for(CorpusController c : pool) {
			Factory.deleteResource(c);
		}
	}
	
	public CorpusController takeApplication() {
		try {
			// blocks if the pool is empty
			return pool.take();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public void returnApplication(CorpusController c) {
		this.pool.add(c);
	}

	private CorpusController createApplication() throws PersistenceException, ResourceInstantiationException, IOException {
		return (CorpusController)PersistenceManager.loadObjectFromFile(new File(Gate.getGateHome(),this.applicationPath));
	}
	
}
