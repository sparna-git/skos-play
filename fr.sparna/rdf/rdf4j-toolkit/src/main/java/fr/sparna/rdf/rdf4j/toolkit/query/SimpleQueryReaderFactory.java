package fr.sparna.rdf.rdf4j.toolkit.query;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public final class SimpleQueryReaderFactory {

	
	/**
	 * Turns a List<String> containing SPARQL queries into a List<SimpleQueryReader>
	 * 
	 * @param strings
	 * 
	 * @return a list of SimpleQueryReader, each wrapping one of the Strings.
	 */
	public static List<SimpleQueryReader> fromStringList(List<String> strings) {
		if(strings == null) {
			return null;
		}
		
		ArrayList<SimpleQueryReader> result = new ArrayList<SimpleQueryReader>();
		for (String aString : strings) {
			result.add(new SimpleQueryReader(aString));
		}
		return result;
	}
	
	
	/**
	 * Turns a directory containing files expressing SPARQL queries into a List<SimpleQueryReader>
	 * 
	 * @param directory		The directory to read from, or a file for a single query
	 * 
	 * @return a list of SimpleQueryReader
	 */
	public static List<SimpleQueryReader> fromDirectory(File directory) {
		if(directory == null) {
			return null;
		}
		
		ArrayList<SimpleQueryReader> result = new ArrayList<SimpleQueryReader>();
		if(directory.exists()) {
			// iterate in each file of the dir
			if(directory.isDirectory()) {
				List<File> files = new ArrayList<File>(FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
				
				// sort to garantee ordering
				Collections.sort(files, new Comparator<File>() {
					@Override
					public int compare(File o1, File o2) {
						return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
					}			
				});
				
				
				for (File aFile : files) {
					result.add(new SimpleQueryReader(aFile));
				}
			} else {
				// or use a single file
				result.add(new SimpleQueryReader(directory));
			}
		}
		return result;
	}
	
	/**
	 * Reads the provided resources on the classpath and turn them into a List<SimpleQueryReader>
	 * 
	 * @param resources		The list of resources to read from
	 * 
	 * @return a list of SimpleQueryReader
	 */
	public static List<SimpleQueryReader> fromResources(Class<?> owner, List<String> resources) {
		if(owner == null) {
			return null;
		}
		
		ArrayList<SimpleQueryReader> result = new ArrayList<SimpleQueryReader>();
		for (String aString : resources) {
			result.add(new SimpleQueryReader(owner, aString));
		}
		return result;
	}
	
	/**
	 * Reads the provided URLs and turn them into a List<SimpleQueryReader>
	 * 
	 * @param urls		The list of URLs to read from
	 * 
	 * @return a list of SimpleQueryReader
	 */
	public static List<SimpleQueryReader> fromUrls(List<URL> urls) {
		ArrayList<SimpleQueryReader> result = new ArrayList<SimpleQueryReader>();
		for (URL url : urls) {
			try {
				InputStream is = url.openStream();
				result.add(new SimpleQueryReader(is));
				is.close();
			} catch (IOException e) {
				throw new Error(e);
			}
		}
		
		return result;
	}
	
}
