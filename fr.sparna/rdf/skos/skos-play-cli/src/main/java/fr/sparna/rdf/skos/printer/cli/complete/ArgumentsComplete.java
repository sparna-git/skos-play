package fr.sparna.rdf.skos.printer.cli.complete;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import fr.sparna.rdf.skos.printer.cli.ArgumentsSkosPlayCli;

@Parameters(commandDescription = "Generates an alphabetical report")
public class ArgumentsComplete extends ArgumentsSkosPlayCli {
	
	@Parameter(
			names = { "-m", "--multilingual" },
			description = "Also includes translations in other languages. Defaults to false."
	)
	private boolean multilingual = false;
	
	@Parameter(
			names = { "-c", "--cache" },
			description = "Cache directory when fetching alignments. Defaults to 'alignment-cache'.",
			converter = FileConverter.class
	)
	private File cacheDir = new File("alignment-cache");

	public boolean isMultilingual() {
		return multilingual;
	}

	public void setMultilingual(boolean multilingual) {
		this.multilingual = multilingual;
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}
	
}
