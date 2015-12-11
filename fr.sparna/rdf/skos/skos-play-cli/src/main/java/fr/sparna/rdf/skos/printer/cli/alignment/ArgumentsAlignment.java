package fr.sparna.rdf.skos.printer.cli.alignment;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.cli.ArgumentsSkosPlayCli;

@Parameters(commandDescription = "Generates a complete report.")
public class ArgumentsAlignment extends ArgumentsSkosPlayCli {
	
	@Parameter(
			names = { "-c", "--cache" },
			description = "Cache directory when fetching alignments. Defaults to 'alignment-cache'.",
			converter = FileConverter.class
	)
	private File cacheDir = new File("alignment-cache");
	
	@Parameter(
			names = { "-bsc", "--bySourceConcept" },
			description = "Generates the output by listing source concepts instead of grouping by target scheme."
	)
	private boolean bySourceConcept = false;

	public File getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}

	public boolean isBySourceConcept() {
		return bySourceConcept;
	}

	public void setBySourceConcept(boolean bySourceConcept) {
		this.bySourceConcept = bySourceConcept;
	}
	
}
