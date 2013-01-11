package fr.sparna.commons.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * See http://www.javapractices.com/Topic42.cjp
 * @author thomas
 */
public class ReadWriteTextFile {
	
	/**
	 *
	 * @param aFile is a file which already exists and can be read.
	 */
	static public String getContents(File aFile, String charset)
	throws FileNotFoundException, IOException {
		//...checks on aFile are elided
		StringBuffer contents = new StringBuffer();

		//declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			// use buffering, reading one line at a time
			input = new BufferedReader( new InputStreamReader(new FileInputStream(aFile),charset) );
			String line = null; //not declared within while loop
			/*
			 * readLine is a bit quirky :
			 * it returns the content of a line MINUS the newline.
			 * it returns null only for the END of the stream.
			 * it returns an empty String if two newlines appear in a row.
			 */
			while (( line = input.readLine()) != null){
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return contents.toString();
	}
	
	/**
	 * Same as getContents(aFile, System.getProperty("file.encoding"))
	 * 
	 * @param aFile
	 * @return
	 */
	static public String getContents(File aFile)
	throws FileNotFoundException, IOException {
		return getContents(aFile, System.getProperty("file.encoding"));
	}

	/**
	 * Change the contents of text file in its entirety, overwriting any
	 * existing text.
	 *
	 * This style of implementation throws all exceptions to the caller.
	 *
	 * @param aFile is an existing file which can be written to.
	 * @throws IllegalArgumentException if param does not comply.
	 * @throws IOException if problem encountered during write.
	 */
	static public void setContents(File aFile, String aContents, String charset)
	throws FileNotFoundException, IOException {
		if (aFile == null) {
			throw new IllegalArgumentException("File should not be null.");
		}
		// auto-create files
		if (!aFile.exists()) {
			aFile.createNewFile();
		}
		if (!aFile.isFile()) {
			throw new IllegalArgumentException("Should not be a directory: " + aFile);
		}
		if (!aFile.canWrite()) {
			throw new IllegalArgumentException("File cannot be written: " + aFile);
		}

		//declared here only to make visible to finally clause; generic reference
		Writer output = null;
		try {
			//use buffering
			output = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(aFile),charset) );
			output.write( aContents );
		} finally {
			//flush and close both "output" and its underlying FileWriter
			if (output != null) output.close();
		}
	}
	
	/**
	 * Same as setContents(aFile, content, System.getProperty("file.encoding"))
	 * 
	 * @param aFile
	 * @return
	 */
	static public void setContents(File aFile, String content)
	throws FileNotFoundException, IOException {
		setContents(aFile, content, System.getProperty("file.encoding"));
	}

	/**
	 * Simple test harness.
	 */
	/*
	public static void main (String... aArguments) throws IOException {
		File testFile = new File("C:\\Temp\\blah.txt");
		System.out.println("Original file contents: " + getContents(testFile, System.getProperty("file.encoding")));
		setContents(testFile, "J'ai d'autres chats � fouetter, moi...", System.getProperty("file.encoding"));
		System.out.println("New file contents: " + getContents(testFile, System.getProperty("file.encoding")));
	}
	*/
}