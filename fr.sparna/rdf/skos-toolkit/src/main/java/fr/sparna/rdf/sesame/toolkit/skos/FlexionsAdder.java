package fr.sparna.rdf.sesame.toolkit.skos;

import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.ling.dictionary.DelaDictionary;
import fr.sparna.rdf.sesame.toolkit.query.SPARQLExecutionException;
import fr.sparna.rdf.sesame.toolkit.query.SesameSPARQLExecuter;
import fr.sparna.rdf.sesame.toolkit.util.RepositoryTransaction;

public class FlexionsAdder {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected java.net.URI flexionsProperty = java.net.URI.create(SKOS.HIDDEN_LABEL);
	
	public FlexionsAdder() {
		super();
	}

	public FlexionsAdder(java.net.URI flexionsProperty) {
		super();
		this.flexionsProperty = flexionsProperty;
	}
	
	public void addFlexions(Repository repository) throws RepositoryException, SPARQLExecutionException {
		DelaDictionary dico = new DelaDictionary(DelaDictionary.class.getClassLoader().getResourceAsStream("dela/dela_fr.lst"), "UTF-8", "fr");
		FlexionsAdderHelper helper = new FlexionsAdderHelper(dico, repository);
		SesameSPARQLExecuter.newExecuter(repository).executeSelect(helper);
	}

	class FlexionsAdderHelper extends GetLabelsHelper {

		protected DelaDictionary dico;
		protected Repository repository;
		protected RepositoryTransaction transaction;
		
		public FlexionsAdderHelper(
				DelaDictionary dico,
				Repository repository) 
		throws RepositoryException {
			super(null, true, true, true, null);
			this.dico = dico;
			this.repository = repository;
			
			this.transaction = new RepositoryTransaction(repository.getConnection());
		}

		@Override
		protected void handleLabel(
				Resource concept,
				URI labelType,
				String label,
				String lang)
		throws TupleQueryResultHandlerException {
			log.trace("Handling flexions of label "+label+"...");
			if(lang.equals("fr")) {
				Set<String> flexions = dico.getFlexion(label, false, false);
				log.trace("Found "+flexions.size()+" flexions for this label");
				if(flexions != null) {
					for (String aFlexion : flexions) {
						try {
							this.transaction.add(repository.getValueFactory().createStatement(
									concept,
									repository.getValueFactory().createURI(flexionsProperty.toString()),
									repository.getValueFactory().createLiteral(aFlexion, lang))
							);
						} catch (RepositoryException e) {
							throw new TupleQueryResultHandlerException(e);
						}
					}
				}
			}
		}

		@Override
		public void endQueryResult() throws TupleQueryResultHandlerException {
			super.endQueryResult();
			try {
				this.transaction.commit();
			} catch (RepositoryException e) {
				throw new TupleQueryResultHandlerException(e);
			}
		}
		
	}

}
