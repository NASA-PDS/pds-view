// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.harvest.crawler;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

public class LocalCrawler implements Crawler<File> {
    private FileList<File> filelist;
    private IOFileFilter fileFilter;
    private FileFilter dirFilter;

    public LocalCrawler() {
        filelist = new FileList<File>();
        fileFilter = new WildcardOSFilter("*");
        dirFilter = FileFilterUtils.directoryFileFilter();

    }

    public FileList<File> crawl(File directory) {
        return crawl(directory, true, null);
    }

    public FileList<File> crawl(File directory, boolean getSubDirectories) {
        return crawl(directory, getSubDirectories, null);
    }

    public FileList<File> crawl(File directory, boolean getSubDirectories, List<String> filesToGet) {
        if(filesToGet != null)
            fileFilter = new WildcardOSFilter(filesToGet);

        //Code to filter out sub directories. Not sure if this is needed
/*
        if(subDirsToIgnore != null) {
            dirFilter = FileFilterUtils.andFileFilter(
                          new NotFileFilter(new WildcardOSFilter(subDirsToIgnore)),
                          FileFilterUtils.directoryFileFilter());
        }
*/
        filelist.addFiles((List<File>) FileUtils.listFiles(directory, fileFilter, null));

        if(getSubDirectories)
            filelist.addDirs(Arrays.asList(directory.listFiles(dirFilter)));

        return filelist;
    }
}
