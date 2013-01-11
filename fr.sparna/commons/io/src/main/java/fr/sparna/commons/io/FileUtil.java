package fr.sparna.commons.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	/**
	 * Recursively deletes directories and files.
	 * 
	 * @param path File or Directory to be deleted
	 * @return true indicates success.
	 */
	public static boolean deleteFileRecursive(File path) {
	    if( path.exists() ) {
	    	if (path.isDirectory()) {
		        File[] files = path.listFiles();
		        for(int i=0; i<files.length; i++) {
		           if(files[i].isDirectory()) {
		        	   deleteFileRecursive(files[i]);
		           } else {
		             files[i].delete();
		           }
		        }
	    	}
	    }
        return(path.delete());
	}
	
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
		
		return result;
	}
	
}
