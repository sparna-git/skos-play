package fr.sparna.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class InputStreamUtil {

	/**
	 * Turns the given stream into String using the given charset
	 * 
	 * @param is
	 * @param charsetName
	 * @return
	 */
	public static String readToString(InputStream is, String charsetName) {
		try {
			return new java.util.Scanner(is, charsetName).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	/**
	 * Turns the given stream into a String using the default platform charset
	 * 
	 * @param is
	 * @return
	 */
	public static String readToString(InputStream is) {
		return readToString(is, Charset.defaultCharset().name());
	}

	/**
	 * Turns the given stream into a byte array
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] readToBytes(InputStream is) 
	throws IOException {
		byte[] data = new byte[16384];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		buffer.close();

		return buffer.toByteArray();
	}

}
