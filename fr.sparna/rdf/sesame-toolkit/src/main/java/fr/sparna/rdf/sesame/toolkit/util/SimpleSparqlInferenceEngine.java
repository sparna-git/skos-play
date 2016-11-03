package fr.sparna.rdf.sesame.toolkit.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.handler.CopyStatementRDFHandler;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSparqlHelper;
import fr.sparna.rdf.sesame.toolkit.query.ConstructSparqlHelperIfc;
import fr.sparna.rdf.sesame.toolkit.query.SparqlPerformException;
import fr.sparna.rdf.sesame.toolkit.query.SparqlQueryIfc;
import fr.sparna.rdf.sesame.toolkit.query.Perform;

/**
 * A simple SPARQL CONSTRUCT-based inference engine, that recursively applies a set of SPARQL CONSTRUCT
 * queries until it finds that no additionnal data have been added.
 * 
 * @author Thomas Francart
 */
public class SimpleSparqlInferenceEngine {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	// la liste de helper construct qui vont chacun executer une regle
	protected List<ConstructSparqlHelper> constructHelpers;
	// l'executer a travers lequel on va executer nos regles
	protected Perform executer;
	// le nombre d'it�rations que le moteur a effectu�
	protected int iterationCount = 0;
	// le nombre maximum d'iteration que peut faire le moteur avant de s'arreter. si <= 0, on boucle jusqu'a la fin
	protected int maxIterationCount = -1;
	
	public SimpleSparqlInferenceEngine(Repository repository, List<SparqlQueryIfc> rules) {
		this.constructHelpers = new ArrayList<ConstructSparqlHelper>();
		this.executer = new Perform(repository);
		// pour chaque regle pass�e en param�tre...
		for (SparqlQueryIfc aRule : rules) {
			this.constructHelpers.add(
					// on construit un Helper
					new ConstructSparqlHelper(
							// avec la regle
							aRule,
							// en lui demandant de copier le resultat dans le meme repository que celui de d�part
							new CopyStatementRDFHandler(repository) 
					)	
			);
		}
	}
	
	public void run() throws SparqlPerformException {
		boolean saturated = false;
		do {
			// keep track of number of iterations
			iterationCount++;
			log.debug("Iteration number "+iterationCount);
			// we imagine we are done
			saturated = true;
			for (ConstructSparqlHelperIfc aHelper : this.constructHelpers) {
				// get the rule index once for debug messages
				int ruleIndex = this.constructHelpers.indexOf(aHelper);
				// execute a rule
				log.debug("Executing rule "+ruleIndex);
				long start = System.currentTimeMillis();
				executer.construct(aHelper);
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
