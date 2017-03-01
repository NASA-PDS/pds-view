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
package gov.nasa.pds.tools.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Utility class.
 *
 * @author mcayanan
 *
 */
public class Utility {

  // Implementation is needed since pds.nasa.gov currently uses SNI
  // which is not supported in Java 6, but is supported in Java 7.
  static {
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
    new javax.net.ssl.HostnameVerifier(){
        public boolean verify(String hostname,
                javax.net.ssl.SSLSession sslSession) {
          if (hostname.equals("pds.nasa.gov")
              && hostname.equals(sslSession.getPeerHost())) {
            return true;
          } else {
            return false;
          }
        }
    });
   }

  /**
   * Method that opens a connection. Supports redirects.
   *
   * @param conn URL Connection
   *
   * @return input stream.
   * @throws IOException If an error occurred while opening
   * the stream.
   */
  public static InputStream openConnection(URLConnection conn)
      throws IOException {
    boolean redir;
    int redirects = 0;
    InputStream in = null;
    do {
      if (conn instanceof HttpURLConnection) {
        ((HttpURLConnection) conn).setInstanceFollowRedirects(false);
      }
      // We want to open the input stream before getting headers
      // because getHeaderField() et al swallow IOExceptions.
      in = conn.getInputStream();
      redir = false;
      if (conn instanceof HttpURLConnection) {
        HttpURLConnection http = (HttpURLConnection) conn;
        int stat = http.getResponseCode();
        if (stat >= 300 && stat <= 307 && stat != 306 &&
            stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
          URL base = http.getURL();
          String loc = http.getHeaderField("Location");
          URL target = null;
          if (loc != null) {
            target = new URL(base, loc);
          }
          http.disconnect();
          // Redirection should be allowed only for HTTP and HTTPS
          // and should be limited to 5 redirections at most.
          if (target == null || !(target.getProtocol().equals("http") ||
              target.getProtocol().equals("https")) ||
              redirects >= 5) {
            throw new SecurityException("illegal URL redirect");
          }
          redir = true;
          conn = target.openConnection();
          redirects++;
        }
      }
    } while (redir);
    return in;
  }

  public static List<URL> toURL(List<String> targets)
      throws MalformedURLException {
    List<URL> results = new ArrayList<URL>();
    for (String t : targets) {
      results.add(toURL(t));
    }
    return results;
  }

  public static URL toURL (String target) throws MalformedURLException {
    URL url = null;
    try {
      url = new URL(target);
    } catch (MalformedURLException u) {
      File file = new File(target);
      url = file.toURI().normalize().toURL();
    }
    return url;
  }
  
  public static boolean isDir(String url) {
    try {
      return isDir(new URL(url));
    } catch (MalformedURLException e) {
      return false;
    }
  }
  
  public static boolean isDir(URL url) {
    File file = FileUtils.toFile(url);
    if (file != null) {
      if (file.isDirectory() || 
          (FilenameUtils.getExtension(file.toString()).length() == 0)) {
        return true;
      } else {
        return false;
      }
    } else {
      if (FilenameUtils.getExtension(url.toString()).length() == 3) {
        return false;
      } else {
        return true;
      }
    }
  }
  
  public static URL getParent(URL url) {
    try {
      return url.toURI().getPath().endsWith("/") ?
        url.toURI().resolve("..").toURL() :
          url.toURI().resolve(".").toURL();
    } catch (Exception e) {
      return null;
    }
  }
  
  public static boolean canRead(URL url) {
    try {
      url.openStream().close();
      return true;
    } catch (IOException io) {
      return false;
    }
  }
  
  public static boolean canRead(String url) {
    try {
      return canRead(new URL(url));
    } catch (MalformedURLException e) {
      return false;
    }
  }
  
  public static String removeLastSlash(String url) {
      if(url.endsWith("/")) {
          return url.substring(0, url.lastIndexOf("/"));
      } else {
          return url;
      }
  }
  
  /**
   * Convenience method for disabling xinclude support throughout
   * the core library.
   * 
   * @return
   */
  public static boolean supportXincludes() {
    return false;
  }
}
