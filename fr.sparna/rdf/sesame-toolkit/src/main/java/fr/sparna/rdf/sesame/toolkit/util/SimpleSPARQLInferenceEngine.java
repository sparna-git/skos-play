package fr.sparna.rdf.sesame.toolkit.util;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelper;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSPARQLHelperIfc;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLQueryIfc;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;

/**
 * A simple SPARQL-based inference engine.
 * @author mondeca
 *
 */
public class SimpleSPARQLInferenceEngine {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// la liste de helper construct qui vont chacun executer une regle
	protected List<ConstructSPARQLHelper> constructHelpers;
	// l'executer a travers lequel on va executer nos regles
	protected SesameSPARQLExecuter executer;
	// le nombre d'it�rations que le moteur a effectu�
	protected int iterationCount = 0;
	// le nombre maximum d'iteration que peut faire le moteur avant de s'arreter. si <= 0, on boucle jusqu'a la fin
	protected int maxIterationCount = -1;
	
	public SimpleSPARQLInferenceEngine(Repository repository, List<SPARQLQueryIfc> rules) {
		this.constructHelpers = new ArrayList<ConstructSPARQLHelper>();
		this.executer = new SesameSPARQLExecuter(repository);
		// pour chaque regle pass�e en param�tre...
		for (SPARQLQueryIfc aRule : rules) {
			this.constructHelpers.add(
					// on construit un DelegatingConstructSPARQLHelper
					new ConstructSPARQLHelper(
							// avec la r�gle
							aRule,
							// en lui demandant de copier le r�sultat dans le m�me repository que celui de d�part
							new CopyStatementRDFHandler(repository) 
					)	
			);
		}
	}
	
	public void run() throws SPARQLExecutionException {
		boolean saturated = false;
		do {
			// keep track of number of iterations
			iterationCount++;
			log.debug("Iteration number "+iterationCount);
			// we imagine we are done
			saturated = true;
			for (ConstructSPARQLHelperIfc aHelper : this.constructHelpers) {
				// get the rule index once for debug messages
				int ruleIndex = this.constructHelpers.indexOf(aHelper);
				// execute a rule
				log.debug("Executing rule "+ruleIndex);
				long start = System.currentTimeMillis();
				executer.executeConstruct(aHelper);
				log.debug("Done in "+((System.currentTimeMillis() - start))+" ms");
				// if the rule had more results than before...
				if(((CopyStatementRDFHandler)aHelper.getHandler()).getResultStatementsCount() > ((CopyStatementRDFHandler)aHelper.getHandler()).getPreviousResultStatementsCount()) {
					// ...then we are not done, and we will iterate again
					saturated = false;
					log.debug("Rule "+ruleIndex+" had "+((CopyStatementRDFHandler)aHelper.getHandler()).getResultStatementsCount()+" results, while it had previously "+((CopyStatementRDFHandler)aHelper.getHandler()).getPreviousResultStatementsCount());
				} else {
					log.debug("Rule "+ruleIndex+" has same result than before ("+((CopyStatementRDFHandler)aHelper.getHandler()).getResultStatementsCount()+")");
				}
			}
		// on s'arrete tant que ce n'est pas sature ou si on a atteint le nombre max d'iterations
		} while (!saturated && (this.maxIterationCount <= 0 || this.iterationCount < this.maxIterationCount));
	}

	public int getMaxIterationCount() {
		return maxIterationCount;
	}

	public void setMaxIterationCount(int maxIterationCount) {
		this.maxIterationCount = maxIterationCount;
	}
	
}
