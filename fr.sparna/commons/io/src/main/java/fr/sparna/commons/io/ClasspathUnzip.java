package fr.sparna.commons.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Pattern;

public class ClasspathUnzip {

	public static void unzipDirectoryFromClassPath(
			String classpathDir,
			String outputDir,
			boolean eraseBefore
	) throws IOException {
		File outputDirectory = new File(outputDir);

		// erase output dir of not exists
		if(eraseBefore) {
			FileUtil.deleteFileRecursive(outputDirectory);
		}

		// create outputDir if not exists		
		if(!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		// list all resources from classpath matching given directory
		Collection<URL> resources = ResourceList.listResources(Pattern.compile(classpathDir+"/.*"));

		// for each resource, extract it into the given output dir
		for (URL aResourceURL : resources) {
			String aResource = aResourceURL.toString();
			// log.trace("Extracting resource\t\t'"+aResource+"'");

			// ca c'est le chemin de la resource depuis la racine du classpath
			String resourcePathInClassPath = aResource.substring(aResource.indexOf(classpathDir));
			// log.trace("Resource Path in classpath\t'"+resourcePathInClassPath+"'");

			// ca c'est le chemin de la resource depuis le répertoire du classpath donné
			// bien faire attention à ce qu'on prenne 1 de plus car on a un "/" en plus dans le Pattern recherché
			String resourcePath = aResource.substring(aResource.indexOf(classpathDir)+classpathDir.length()+(aResource.endsWith("/")?0:1));
			// log.trace("Resource Path\t\t\t'"+resourcePath+"'");

			// ca c'est le chemin des repertoires pour accéder au fichier
			String resourcePathDirectory = resourcePath.substring(0, (resourcePath.lastIndexOf('/') > 0)?resourcePath.lastIndexOf('/'):0);
			// log.trace("Resource Path directory\t\t'"+resourcePathDirectory+"'");

			// on créé la hiérarchie de répertoires
			if(!"".equals(resourcePathDirectory)) {
				File dirToCreate = new File(outputDirectory,resourcePathDirectory);
				if(!dirToCreate.exists()) {
					dirToCreate.mkdirs();
				}
			}

			// on créé le fichier une fois qu'on a créé les répertoires
			File fileToCreate = new File(outputDirectory, resourcePath);
			if(!fileToCreate.exists()) {
				fileToCreate.createNewFile();
			}

			// enregistrer le fichier
			if(!fileToCreate.isDirectory()) {
				setFile(ClasspathUnzip.class.getClassLoader().getResourceAsStream(resourcePathInClassPath), fileToCreate.getAbsolutePath());
			}
		}

	}

	public static void unzipFileFromClassPath(
			String classpathFile,
			String outputDir,
			boolean keepDirectoryStructure
	) throws IOException {
		File outputDirectory = new File(outputDir);

		// create outputDir if not exists		
		if(!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}
		
		File fileToCreate = new File(outputDirectory, classpathFile);
		if(!keepDirectoryStructure) {
			fileToCreate = new File(outputDirectory, fileToCreate.getName());
		}
		
		// create super directories if needed
		File parentFile = fileToCreate.getParentFile();
		if(!parentFile.exists()) {
			parentFile.mkdirs();
		}
		setFile(ClasspathUnzip.class.getClassLoader().getResourceAsStream(classpathFile), fileToCreate.getAbsolutePath());

	}

	public static void unzipFileFromClassPath(
			String classpathFile,
			String outputDir
	) throws IOException {
		unzipFileFromClassPath(classpathFile, outputDir, true);
	}

	/**
	 *Writes a file given the inputstream and filename.
	 *Used for unpacking the JAR.
	 *
	 *@param io       Source InputStream to be written to a file
	 *@param fileName Name of file to be written 
	 */
	public static void setFile(InputStream io, String fileName)
	throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		try {
			byte[] buf = new byte[256];
			int read = 0;
			while ((read = io.read(buf)) > 0) {
				fos.write(buf, 0, read);
			}
		} finally {
			fos.flush();
			fos.close();
		}
	}


	public static void main(String... args) throws Exception {
		ClasspathUnzip.unzipDirectoryFromClassPath("com/mondeca/rdfindexer/solr", "/home/mondeca/test", false);
	}

}
