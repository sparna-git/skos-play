package fr.sparna.commons.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * See http://www.javapractices.com/topic/TopicAction.do?Id=78
 * 
 * Simple utilities to return the stack trace of an exception as a String.
 */
public class StackTraceUtil {

	/**
	 * Returns a String containing the stack trace of the given throwable
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTraceAsString(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	/** Demonstrate output. */
	public static void main (String... aArguments){
		final Throwable throwable = new IllegalArgumentException("Blah");
		System.out.println( getStackTraceAsString(throwable) );
	}

}
