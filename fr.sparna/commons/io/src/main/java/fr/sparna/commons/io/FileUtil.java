package fr.sparna.commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		
		// sort output
		Collections.sort(result, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}			
		});
		
		return result;
	}
	
	/**
	 * Copies sourceFile to targetFile. If targetFile does not exists, create it before doing
	 * the copy, and create its parent directories with mkdirs.
	 * <p />See http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	 * 
	 * @param sourceFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.getParentFile().mkdirs();
	    	destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    } finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
}
