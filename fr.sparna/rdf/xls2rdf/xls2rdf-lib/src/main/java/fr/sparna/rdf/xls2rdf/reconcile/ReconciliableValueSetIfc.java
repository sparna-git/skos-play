package fr.sparna.rdf.xls2rdf.reconcile;

import org.eclipse.rdf4j.model.IRI;

public interface ReconciliableValueSetIfc {

	IRI getReconciledValue(String value);

}