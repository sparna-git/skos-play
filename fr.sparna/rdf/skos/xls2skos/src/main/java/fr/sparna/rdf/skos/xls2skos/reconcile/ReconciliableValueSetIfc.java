package fr.sparna.rdf.skos.xls2skos.reconcile;

import org.eclipse.rdf4j.model.IRI;

public interface ReconciliableValueSetIfc {

	IRI getReconciledValue(String value);

}