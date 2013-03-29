package fr.sparna.commons.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Reads the content of a URL.
 */
public class URLReader {

	/**
	 * Reads the content of the given address and returns it in a String.
	 * This is the same as calling <code>readContents(new URL(address))</code>
	 * 
	 * @param address
	 * @return
	 * @throws IOException
	 */
    public String readContents(String address) throws IOException {
        return readContents(new URL(address));
    }
    
    /**
	 * Reads the content of the given URL and returns it in a String
	 * 
	 * @param url				URL to read from
	 * @return
	 * @throws IOException
	 */
    public String readContents(URL url) throws IOException {
        StringBuilder contents = new StringBuilder(2048);
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = "";
            while (line != null) {
                line = br.readLine();
                contents.append(line);
            }
        } finally {
            close(br);
        }

        return contents.toString();
    }

    private static void close(Reader br) {
        try {
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
    	URLReader urlReader = new URLReader();

        for (String url : args) {
            try {
                String contents = urlReader.readContents(url);
                System.out.printf("url: %s contents: %s\n", url, contents);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}