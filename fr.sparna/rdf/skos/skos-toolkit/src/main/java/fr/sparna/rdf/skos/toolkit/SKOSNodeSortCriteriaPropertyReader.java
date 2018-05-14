package fr.sparna.rdf.skos.toolkit;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.reader.KeyValueReader;


public class SKOSNodeSortCriteriaPropertyReader implements SKOSNodeSortCriteriaReader {
	
	protected KeyValueReader<IRI, Literal> reader;
	protected String lang;
	protected RepositoryConnection connection;

	public SKOSNodeSortCriteriaPropertyReader(KeyValueReader<IRI, Literal> reader, String lang, RepositoryConnection connection) {
		super();
		this.reader = reader;
		this.connection = connection;
		this.lang = lang;
	}

	@Override
	public String getLang() {
		return lang;
	}

	@Override
	public String readSortCriteria(IRI node) {
		Literal sortCriteria = reader.readUnique(node, connection);
		if(sortCriteria == null) {
			return null;
		}
		return sortCriteria.stringValue();
	}

	
}
