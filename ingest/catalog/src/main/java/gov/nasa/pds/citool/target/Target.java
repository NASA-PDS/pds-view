package gov.nasa.pds.citool.target;

import gov.nasa.pds.citool.file.FileList;
import gov.nasa.pds.citool.file.FileListGenerator;
import gov.nasa.pds.citool.util.Utility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.FilenameUtils;

/**
 * Class to support target URLs, files, and directories.
 *
 * @author mcayanan
 *
 */
public class Target {
    private boolean isDirectory;
    private String target;

    /**
     * Constructor
     *
     * @param target A file, URL, or directory.
     */
    public Target(String target) {
        this.target = target;
        try {
            URL url = new URL(target);
            if (!url.getProtocol().equals("file")) {
                String extension = FilenameUtils.getExtension(target);
                if (extension.length() == 0) {
                    isDirectory = true;
                } else {
                    isDirectory = false;
                }
            }
            else {
                throw new MalformedURLException("Target is a file");
            }
        } catch (MalformedURLException uEx) {
            //Must be a file.
            File t = new File(target);
            if (t.isDirectory()) {
                isDirectory = true;
            } else {
                isDirectory = false;
            }
        }
    }

    /**
     * Determine if the target is a directory
     *
     * @return 'true' if the target is a directory, 'false' otherwise
     */
    public boolean isDirectory() {
        return isDirectory;
    }


    /**
     * Get the target name.
     *
     * @return A string representation of the target.
     */
    public String toString() {
        return target;
    }

    /**
     * Convert the target to a URL.
     *
     * @return a URL representation of the target
     * @throws MalformedURLException
     */
    public URL toURL() throws MalformedURLException {
        return Utility.toURL(target);
    }

    /**
     * Traverse the target directory.
     *
     * @param recurse Set to 'true' to recursively traverse the target,
     * 'false' otherwise
     *
     * @return A list of URLs or an empty list if the target is
     * not a directory
     *
     * @throws BadLocationException
     * @throws IOException
     */
    public List<URL> traverse(boolean recurse) throws IOException, BadLocationException {
        List<URL> urls = new ArrayList<URL>();
        String regexp[] = {"*.CAT", "*.cat"};

        //Just return the empty list if the target is a directory
        if(!isDirectory)
            return urls;

        FileListGenerator generator = new FileListGenerator();
        generator.setFilters(Arrays.asList(regexp), null, null);

        FileList fileList = generator.visitTarget(target.toString(), recurse);
        for(Iterator i = fileList.getFiles().iterator(); i.hasNext();) {
            URL file = Utility.toURL(i.next().toString());
            urls.add(file);
        }
        for(Iterator i = fileList.getDirs().iterator(); i.hasNext();) {
            urls.addAll(traverse(i.next().toString(), recurse));
        }

        return urls;
    }

    private List<URL> traverse(String dir, boolean recurse) throws IOException, BadLocationException {
        List<URL> urls = new ArrayList<URL>();
        String regexp[] = {"*.CAT", "*.cat"};

        FileListGenerator generator = new FileListGenerator();
        generator.setFilters(Arrays.asList(regexp), null, null);

        FileList fileList = generator.visitTarget(dir.toString(), recurse);
        for(Iterator i = fileList.getFiles().iterator(); i.hasNext();) {
            URL file = Utility.toURL(i.next().toString());
            urls.add(file);
        }
        for(Iterator i = fileList.getDirs().iterator(); i.hasNext();) {
            urls.addAll(traverse(i.next().toString(), recurse));
        }
        return urls;
    }
}
