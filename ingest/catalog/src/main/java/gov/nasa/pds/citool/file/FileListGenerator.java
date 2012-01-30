//Copyright 2006-2007, by the California Institute of Technology.
//ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//Any commercial use must be negotiated with the Office of Technology Transfer
//at the California Institute of Technology.
//
//This software is subject to U. S. export control laws and regulations
//(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
//is subject to U.S. export control laws and regulations, the recipient has
//the responsibility to obtain export licenses or other export authority as
//may be required before exporting such information to foreign countries or
//providing access to foreign nationals.

// $Id: FileListGenerator.java 5853 2010-02-06 22:54:00Z shardman $

package gov.nasa.pds.citool.file;

import gov.nasa.pds.citool.file.filefilter.WildcardOSFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

/**
 * Class that can generate a list of files from a supplied directory and optionally, a specified
 * filter. The resulting files and directories are stored in a FileList.
 *
 * @author mcayanan
 * @version $Revision $
 */
public class FileListGenerator {

	private NotFileFilter noFileFilter;
	private IOFileFilter noDirFilter;
	private IOFileFilter fileFilter;
	private IOFileFilter effFileFilter;
	private FileFilter effDirFilter;
	private final int fileExt = 3;

	/**
	 * Default constructor
	 *
	 */
	public FileListGenerator() {
		fileFilter = new WildcardOSFilter("*");
		noFileFilter = null;
		noDirFilter = null;
	}

	/**
	 * Sets the filter to be used when searching for files in a directory
	 * @param wildcards a list of files and/or file patterns to match
	 */
	private void setFileFilter(List wildcards) {
		fileFilter = new WildcardOSFilter(wildcards);
	}

	/**
	 * Sets the filter to be used when searching for files to ignore in a directory
	 * @param wildcards a list of files and/or file patterns to ignore
	 */
	private void setNoFileFilter(List wildcards) {
		noFileFilter = new NotFileFilter(new WildcardOSFilter(wildcards));
	}

	/**
	 * Sets the filter to be used when searching for directories to ignore
	 * @param patterns a list of directory/directory patterns to ignore
	 */
	private void setNoDirFilter(List patterns) {
		noDirFilter = new NotFileFilter(new WildcardOSFilter(patterns));
	}

	/**
	 * Combines filters to include and exclude files using the AND file filter
	 *
	 */
	private void setEffFileFilter() {
		if(noFileFilter != null)
			effFileFilter = FileFilterUtils.andFileFilter(fileFilter, noFileFilter);
		else
			effFileFilter = fileFilter;
	}

	/**
	 * Combines filters to seek out directories to exclude using the AND file filter
	 *
	 */
	private void setEffDirFilter() {
		if(noDirFilter != null)
			effDirFilter = FileFilterUtils.andFileFilter(noDirFilter, FileFilterUtils.directoryFileFilter());
		else
			effDirFilter = FileFilterUtils.directoryFileFilter();
	}

	/**
	 * Sets all possible filters when looking in a directory.
	 *
	 * @param regexp File patterns to include when finding files in a directory
	 * @param noFiles File patterns to ignore when finding files in a directory
	 * @param noDirs Directory patterns to ignore when finding sub-directories
	 */
	public void setFilters(List regexp, List noFiles, List noDirs) {
		if(regexp != null)
			setFileFilter(regexp);
		if(noFiles != null)
			setNoFileFilter(noFiles);
		if(noDirs != null)
			setNoDirFilter(noDirs);

		setEffFileFilter();
		setEffDirFilter();
	}

	/**
	 * Allows one to pass in a file or URL. Directories will be visited if the
	 * target is a directory. The resulting list is stored in a FileList object.
	 *
	 * @param getSubDirs 'true' to look for sub-directories, 'false' to just search for files when
	 *                   given a directory as input
	 * @return A FileList object that contains the files and sub-directories
	 * @throws BadLocationException
	 * @throws IOException
	 */
	public FileList visitTarget(String target, boolean getSubDirs) throws IOException, BadLocationException {
		File file = null;
		FileList fileList = new FileList();

		try {
			URL url = new URL(target.toString());
			if((file = FileUtils.toFile(url)) != null) {
				return (visitFileTarget(file, getSubDirs));
			}
			if(!isLinkFile(target.toString()))
				fileList = crawl(new URL(target.toString()), getSubDirs);
			else
				fileList.addToFileList(url);
		}
		catch(MalformedURLException uEx) {
			return (visitFileTarget(new File(target), getSubDirs));
		}
		return fileList;
	}

	/**
	 * Visits the file being supplied. If a directory is being passed in, then it
	 * will look for files and sub-directories (if it is turned ON). Otherwise, it
	 * simply gets stored in the FileList.
	 *
	 * @param file A file or a directory. If it is a directory, then the visitDir method
	 *      will be called and the list of files can be retrieved via the getFiles and
	 *      getSubDirs methods.
	 * @param getSubDirs Tells the method whether to look for sub-directories
	 * @return a FileList object
	 * @throws IOException
	 */
	private FileList visitFileTarget(File file, boolean getSubDirs) throws IOException {
		FileList fileList = new FileList();
		if(file.isDirectory())
			fileList = visitDir(file, getSubDirs);
		else
			fileList.addToFileList(file);

		return fileList;
	}

	/**
	 * Gets a list of files under a given directory.
	 *
	 * Filters must be set via setFileFilters prior to calling this method in order to look
	 * for specific files and filter out un-wanted files and sub-drirectories.
	 *
	 * @param dir the name of the directory
	 * @param getSubDirs 'true' to get a list of sub-directories
	 * @return A FileList object containing the files and sub-directories found
	 * @throws IOException
	 */
	public FileList visitDir(File dir, boolean getSubDirs) throws IOException {
		FileList fileList = new FileList();

		if( !dir.isDirectory() )
			throw new IllegalArgumentException("parameter 'dir' is not a directory: " + dir);

		//Find files only first
		fileList.addToFileList(FileUtils.listFiles(dir, effFileFilter, null));

		//Visit sub-directories if the recurse flag is set
		if(getSubDirs)
			fileList.addToDirList(Arrays.asList(dir.listFiles(effDirFilter)));

		return fileList;
	}

	/**
	 * Crawls a directory URL, looking for files and sub-directories. Files found in a URL
	 * are assumed to end with a ".xxx".
	 *
	 * Filters must be set via the setFileFilters method prior to crawling in order to look for
	 * files and filter out un-wanted files and directories.
	 *
	 * @param url The URL to crawl
	 * @param getSubDirURLs Set to 'true' to retrieve sub-directory URLs, 'false' otherwise
	 * @return A FileList object containing the files and sub-directories that were found.
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public FileList crawl(URL url, boolean getSubDirURLs) throws IOException, BadLocationException {
		Set links = new LinkedHashSet();
		FileList fileList = new FileList();

		links.addAll(getHyperLinks(url));
		fileList.addToFileList(getFileURLNames(url, links));

		if(getSubDirURLs) {
			fileList.addToDirList(getSubDirURLNames(url, links));
		}

		return fileList;
	}

	/**
	 * Gets hyperlinks found in an HTML document of a URL. No duplicate links will be returned.
	 *
	 * @param url location
	 * @return A Set of hyperlinks
	 *
	 * @throws IOException
	 * @throws BadLocationException
	 * @throws NullPointerException
	 */
	public Set getHyperLinks(URL url) throws IOException, BadLocationException, NullPointerException {
		InputStreamReader stream = null;
		HTMLDocument doc = null;
		EditorKit kit = null;
		Set links = new LinkedHashSet();

		try {
			stream = new InputStreamReader(url.openStream());
			kit = new HTMLEditorKit();
			doc = (HTMLDocument) kit.createDefaultDocument();
			kit.read(stream, doc, 0);
		}
		finally {
			stream.close();
		}

		for(HTMLDocument.Iterator i = doc.getIterator(HTML.Tag.A); i.isValid(); i.next()) {
			SimpleAttributeSet s = (SimpleAttributeSet) i.getAttributes();
			links.add(s.getAttribute(HTML.Attribute.HREF));
		}
		return links;
	}

	/**
	 * Finds links to files. This assumes that a file must end in a ".xxx", otherwise
	 * it will not be retrieved.
	 *
	 * @param url The location
	 * @param links The Set of files and directories found inside the URL
	 * @return a list of file URLs
	 * @throws MalformedURLException
	 */
	public List getFileURLNames(URL url, Set links) throws MalformedURLException {
		List fileURLs = new ArrayList();
		String parent = url.toString();

		if(parent.endsWith("/") == false)
			parent = parent.concat("/");

		for(Iterator i = links.iterator(); i.hasNext();) {
			String link = i.next().toString();
			if(isLinkFile(link)) {
				if(effFileFilter.accept(new File(link)) == true)
					fileURLs.add(new URL(parent.concat(link)));
			}
		}
		return fileURLs;
	}

	/**
	 * Finds links to sub-directory URLs
	 *
	 * @param url The location
	 * @param links The Set of files and directories found inside the URL
	 * @return a list of sub directory URLs
	 * @throws MalformedURLException
	 */
	public List getSubDirURLNames(URL url, Set links) throws MalformedURLException {
		List dirURLs = new ArrayList();
		String parent = url.toString();

		if(parent.endsWith("/") == false)
			parent = parent.concat("/");

		for(Iterator i = links.iterator(); i.hasNext();) {
			String link = i.next().toString();
			if(isLinkSubDir(url, link)) {
				if(noDirFilter == null)
					dirURLs.add(new URL(parent.concat(link)));
				else if( (noDirFilter != null) && (noDirFilter.accept(new File(link)) == true) )
					dirURLs.add(new URL(parent.concat(link)));
			}
		}
		return dirURLs;
	}

	/**
	 * Determines if a hyperlink is a file. The rule is that if the name ends with a ".xxx",
	 * then it is a file. Otherwise, false is returned.
	 *
	 * @param link The hyperlink name to examine
	 * @return 'true' if hyperlink contains a 3 character file extension, 'false' otherwise
	 */
	public boolean isLinkFile(String link) {
		String ext = FilenameUtils.getExtension(link);
		if(ext.length() == fileExt) {
			return true;
		}
		else
			return false;

	}

	/**
	 * Determines if a hyperlink is a sub-directory.
	 *
	 * @param url The location
	 * @param link The hyperlink name to examine
	 * @return 'true' if hyperlink is a sub-directory, 'false' otherwise
	 */
	public boolean isLinkSubDir(URL url, String link) {
		if( !isLinkFile(link) && link.indexOf('#') == -1 && link.indexOf('?') == -1) {
			//Check to see if the directory link is a hyperlink to the parent
			String parent = new File(url.getFile()).getParent();
			if( parent.equalsIgnoreCase(new File(link).toString()) )
				return false;
			else
				return true;
		}
		else
			return false;

	}

}
