package fr.sparna.rdf.skos.xls2skos.reconcile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class DummyReconcileService implements ReconcileServiceIfc {

	private final String rootUri;
	
	public DummyReconcileService(String rootUri) {
		super();
		this.rootUri = rootUri;
	}

	@Override
	public Map<String, ReconcileResultIfc> reconcile(Map<String, ReconcileQueryIfc> queries) {
		
		Map<String, ReconcileResultIfc> result = new HashMap<String, ReconcileResultIfc>();
		
		for (Map.Entry<String, ReconcileQueryIfc> anEntry : queries.entrySet()) {
			try {
				result.put(
						anEntry.getKey(),
						new SimpleReconcileResult(
								SimpleValueFactory.getInstance().createIRI(this.rootUri+"/"+URLEncoder.encode(anEntry.getValue().getQuery(), "UTF-8")).toString(),
								anEntry.getValue().getQuery(),
								(anEntry.getValue().getTypes() != null && anEntry.getValue().getTypes().size() > 0)?anEntry.getValue().getTypes().get(0):null
						)
				);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return result;		
	}

}
