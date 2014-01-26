package fr.sparna.commons.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

	/**
	 * Taille du tampon
	 */
	private static final int BUFFER_SIZE = 2048;

	public static void zip(OutputStream dest, Map<String, InputStream> inputs) throws IOException {
		// création d'un buffer d'écriture
		BufferedOutputStream buff = new BufferedOutputStream(dest);

		// création d'un flux d'écriture Zip
		ZipOutputStream out = new ZipOutputStream(buff);

		// spécification de la méthode de compression
		out.setMethod(ZipOutputStream.DEFLATED);

		// spécifier la qualité de la compression 0..9
		out.setLevel(Deflater.BEST_COMPRESSION);

		// buffer temporaire des données à écriture dans le flux de sortie
		byte data[] = new byte[BUFFER_SIZE];

		// pour chacun des fichiers de la liste
		for (Map.Entry<String, InputStream> anEntry : inputs.entrySet()) {

			// création d'un tampon de lecture sur ce flux
			BufferedInputStream buffi = new BufferedInputStream(anEntry.getValue(), BUFFER_SIZE);

			// création d'en entrée Zip pour ce fichier
			ZipEntry entry = new ZipEntry(unAccent(anEntry.getKey()));

			// ajout de cette entrée dans le flux d'écriture de l'archive Zip
			out.putNextEntry(entry);

			// écriture du fichier par paquet de BUFFER octets
			// dans le flux d'écriture
			int count;
			while((count = buffi.read(data, 0, BUFFER_SIZE)) != -1) {
				out.write(data, 0, count);
			}

			// Close the current entry
			out.closeEntry();

			// fermeture du flux de lecture
			buffi.close();
		}

		// fermeture du flux d'écriture
		out.close();
		buff.close();
		dest.close();
	}
	
	private static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		return temp.replaceAll("[^\\p{ASCII}]","");
	}
	
	public static void main(String... args) throws Exception {
		// create some data
		final String data = "This is some data in a String in memory and it does not come from a file and it contains an accented character : é and that's all.";
		final ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes("UTF-8"));
		FileOutputStream fos = new FileOutputStream("zip-test.zip");
		Zipper.zip(fos, new HashMap<String, InputStream>() {{ put("name-inside-zip.txt", bais); }});
		fos.close();
	}
	
}
