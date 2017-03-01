// Copyright 2006-2017, by the California Institute of Technology.
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
package gov.nasa.pds.tools.validate.rule.pds4;

import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.tools.validate.Target;
import gov.nasa.pds.tools.validate.crawler.Crawler;
import gov.nasa.pds.tools.validate.crawler.CrawlerFactory;
import gov.nasa.pds.tools.validate.rule.AbstractValidationChain;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

/**
 * Implements a validation chain that validates PDS4 bundles. It is applicable
 * if there is a bundle label in the root directory.
 */
public class BundleValidationRule extends AbstractValidationChain {

	private static final Pattern BUNDLE_LABEL_PATTERN = Pattern.compile("bundle(_.*)\\.xml", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean isApplicable(String location) {
		URL url;
    try {
      url = new URL(location);
    } catch (MalformedURLException e) {
      return false;
    }

		if (!Utility.isDir(url)) {
			return false;
		}
		Crawler crawler = CrawlerFactory.newInstance(url);
		try {
  		List<Target> children = crawler.crawl(url);
  		// Check for bundle(_.*)?\.xml file.
  		for (Target child : children) {
  			Matcher matcher = BUNDLE_LABEL_PATTERN.matcher(FilenameUtils.getName(child.toString()));
  			if (matcher.matches()) {
  				return true;
  			}
  		}
		} catch(IOException io) {
		  //Ignore. We'll return false anyways.
		}
		return false;
	}

}
