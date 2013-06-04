package fr.sparna.commons.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * List the resources available from the classpath
 */
public class ResourceList {	
	
    /**
     * for all elements of java.class.path get a Collection of resources URL
     * pattern = Pattern.compile(".*"); gets all resources.
     * Returned URLs can use the "file:" protocol or the "jar:" protocol in the form "jar:file://path/to/jar!/x/y/z"
     * 
     * @param pattern	the pattern to match
     * @return 			the resources URL in the order they are found
     */
    public static List<URL> listResources(
            final Pattern pattern
        ) {
            final List<URL> retval = new ArrayList<URL>();
            final String classPath = System.getProperty("java.class.path", ".");
            final String[] classPathElements = classPath.split(System.getProperty("path.separator", ":"));
            for(final String element : classPathElements){
                retval.addAll(listResources(element, pattern));
            }
            // sort result to garantee ordering
            Collections.sort(retval, new Comparator<URL>() {
				@Override
				public int compare(URL o1, URL o2) {
					return o1.toString().compareTo(o2.toString());
				}
            });
            return retval;
        }
    
    public static List<URL> listDirectoryResources(
            String directory
    ) {
		if(!directory.endsWith("/")) {
			directory = directory.concat("/");
		}
        return listResources(Pattern.compile(".*"+directory+".*"));
    }

    private static Collection<URL> listResources(
        final String element,
        final Pattern pattern
    ) {
        final Collection<URL> retval = new ArrayList<URL>();
        final File file = new File(element);
        if(file.isDirectory()){
            retval.addAll(listResourcesFromDirectory(file, pattern));
        } else{
            retval.addAll(listResourcesFromJarFile(file, pattern));
        }
        return retval;
    }
    
    private static Collection<URL> listResourcesFromJarFile(
            final File file,
            final Pattern pattern
        ) {
            final Collection<URL> retval = new ArrayList<URL>();
            ZipFile zf;
            try{
                zf = new ZipFile(file);
            } catch(final ZipException e){
                throw new Error(e);
            } catch(final IOException e){
                throw new Error(e);
            }
            
            final Enumeration<? extends ZipEntry> e = zf.entries();
            while(e.hasMoreElements()){
                final ZipEntry ze = e.nextElement();
                final String fileName = ze.getName();
                final boolean accept = pattern.matcher(fileName).matches();
                if(accept){
                    // retval.add(fileName);
                	try {
						retval.add(new URL("jar:file://"+file.getCanonicalPath()+"!/"+fileName));
					} catch (IOException e1) {
						throw new Error(e1);
					}
                }
            }
            try{
                zf.close();
            } catch(final IOException e1){
                throw new Error(e1);
            }
            return retval;
        }

    private static Collection<URL> listResourcesFromDirectory(
        final File directory,
        final Pattern pattern
    ) {
        final Collection<URL> retval = new ArrayList<URL>();
        final File[] fileList = directory.listFiles();
        for(final File file : fileList){
            if(file.isDirectory()){
                retval.addAll(listResourcesFromDirectory(file, pattern));
            } else{
                try{
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if(accept){
                        retval.add(new URL("file:"+fileName));
                    }
                } catch(final IOException e){
                    throw new Error(e);
                }
            }
        }
        return retval;
    }

    /**
     * list the resources that match args[0]
     * 
     * @param args
     *            args[0] is the pattern to match, or list all resources if
     *            there are no args
     */
    public static void main(final String[] args) throws Exception {
        Pattern pattern;
        if(args.length < 1){
            pattern = Pattern.compile(".*");
        } else{
            pattern = Pattern.compile(args[0]);
        }
        final Collection<URL> list = ResourceList.listResources(pattern);
        for(final URL aURL : list){
            System.out.println(aURL);
        }
    }
}  
