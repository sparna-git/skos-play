package fr.sparna.rdf.skos.xls2skos.reconcile;

import static fr.sparna.rdf.skos.xls2skos.ExcelHelper.getCellValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.skos.xls2skos.Xls2SkosException;

public class PreloadedReconciliableValueSet implements ReconciliableValueSetIfc {

	private static Logger log = LoggerFactory.getLogger(PreloadedReconciliableValueSet.class.getName());
	
	private static final int BATCH_SIZE = 20;
	
	private transient ReconcileServiceIfc reconcileService;
	private boolean failOnNoMatch = true;
	
	private Map<String, IRI> reconciledValues;
	
	
	public PreloadedReconciliableValueSet(
			ReconcileServiceIfc reconcileService,
			boolean failOnNoMatch
	) {
		this.reconcileService = reconcileService;
		this.failOnNoMatch = failOnNoMatch;
		this.reconciledValues = new HashMap<String, IRI>();	
	}
	
	/* (non-Javadoc)
	 * @see fr.sparna.rdf.skos.xls2skos.reconcile.ReconciliableValueSetIfc#getReconciledValue(java.lang.String)
	 */
	@Override
	public IRI getReconciledValue(String value) {
		return this.reconciledValues.get(value);
	}
	
	public static List<String> extractDistinctValues(Sheet sheet, int columnIndex, int headerRowIndex) {
		
		Set<String> result = new HashSet<String>();
		for (int rowIndex = (headerRowIndex + 1); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			Cell cell = row.getCell(columnIndex);			
			String value = getCellValue(cell);
			
			result.add(value.trim());
		}
		
		log.debug("Extracted "+result.size()+" distinct values from column "+CellReference.convertNumToColString(columnIndex));
		return new ArrayList<String>(result);
		
	}
	
	public void initReconciledValues(List<String> values, IRI reconcileType) {
		log.debug("Reconciling "+values.size()+" values against type "+ reconcileType +" ...");
		// iterate
		int currentOffset = 0;
		while((currentOffset + BATCH_SIZE) < values.size()) {
			List<String> batch = values.subList(currentOffset, currentOffset + BATCH_SIZE);
			this.reconciledValues.putAll(reconcileBatch(batch, reconcileType));
			currentOffset += BATCH_SIZE;
		}
		// process last part
		List<String> batch = values.subList(currentOffset, values.size());
		this.reconciledValues.putAll(reconcileBatch(batch, reconcileType));
	}
	
	private Map<String, IRI> reconcileBatch(List<String> values, IRI reconcileType) {
		
		// build the queries Map
		Map<String, ReconcileQueryIfc> queries = new HashMap<String, ReconcileQueryIfc>();
		for (String aValue : values) {
			queries.put("q"+values.indexOf(aValue), new SimpleReconcileQuery(
					aValue,
					(reconcileType != null)?Collections.singletonList(reconcileType.toString()):null
			));
		}
		
		// call ReconcileService
		Map<String, ReconcileResultIfc> reconcileResults = this.reconcileService.reconcile(queries);
		
		// parse results
		Map<String, IRI> result = new HashMap<String, IRI>();
		for (Map.Entry<String, ReconcileResultIfc> anEntry : reconcileResults.entrySet()) {
			
			String initialValue = queries.get(anEntry.getKey()).getQuery();
			
			if(anEntry.getValue().getMatches() == null || anEntry.getValue().getMatches().size() == 0) {
				// no reconciliation result for this value
				String message = "Unable to reconcile value '"+ initialValue +"' on type/scheme <"+ reconcileType +">";
				if(this.failOnNoMatch) {
					throw new Xls2SkosException(message);
				} else {
					log.error(message);
				}
			} else {
				// pick the first one, assuming only one result for now
				String matchResult = anEntry.getValue().getMatches().get(0).getId();
				log.debug("Value '"+initialValue+"' reconciled to <"+matchResult+">");
				result.put(initialValue, SimpleValueFactory.getInstance().createIRI(anEntry.getValue().getMatches().get(0).getId()));
			}
		}
		
		return result;
	}
	
	
}
