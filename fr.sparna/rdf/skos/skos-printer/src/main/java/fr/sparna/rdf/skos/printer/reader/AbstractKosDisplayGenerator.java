package fr.sparna.rdf.skos.printer.reader;

import java.util.UUID;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.skos.printer.schema.KosDisplay;

/**
 * Responsible for generating the body of a Display.
 * 
 * @author Thomas Francart.
 */
public abstract class AbstractKosDisplayGenerator {
	
	protected RepositoryConnection connection;
	
	protected String displayId;
	
	public AbstractKosDisplayGenerator(RepositoryConnection connection, String displayId) {
		super();
		this.connection = connection;
		this.displayId = displayId;
	}
	
	/**
	 * Creates a generator with a randomly-created displayId
	 * @param repository
	 */
	public AbstractKosDisplayGenerator(RepositoryConnection connection) {
		this(connection, UUID.randomUUID().toString());
	}

	public KosDisplay generateDisplay(String mainLang, final IRI conceptScheme) {
		// prevent null language
		if(mainLang == null) {
			mainLang = "";
		}

		// sets the ID on the generated display and return
		KosDisplay display = this.doGenerate(mainLang, conceptScheme);
		display.setDisplayId(this.displayId);
		return display;
	}
	
	protected abstract KosDisplay doGenerate(String mainLang, final IRI conceptScheme);

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}	
	
}
