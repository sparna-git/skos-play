package fr.sparna.rdf.toolkit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListFilesRecursive {

	/**
	 * Returns a list of all the files, recursively, excluding directories.
	 * 
	 * @param fileOrDirectory
	 * @return
	 */
	public static List<File> listFilesRecursive(File fileOrDirectory) {
		// null input = null output
		if(fileOrDirectory == null) {
			return null;
		}
		
		List<File> result = new ArrayList<File>();
		if(!fileOrDirectory.isHidden()) {
			if(fileOrDirectory.isDirectory()) {
				for (File aFile : fileOrDirectory.listFiles()) {
					result.addAll(listFilesRecursive(aFile));
				}
			} else {
				result.add(fileOrDirectory);
			}			
		}
		
		// sort output
		Collections.sort(result, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}			
		});
		
		return result;
	}
	
}
