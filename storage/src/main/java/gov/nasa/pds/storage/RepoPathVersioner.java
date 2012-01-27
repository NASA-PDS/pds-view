// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
package gov.nasa.pds.storage;

import org.apache.oodt.cas.filemgr.structs.Product;
import org.apache.oodt.cas.filemgr.structs.Reference;
import org.apache.oodt.cas.filemgr.structs.exceptions.VersioningException;
import org.apache.oodt.cas.filemgr.versioning.Versioner;
import org.apache.oodt.cas.filemgr.versioning.VersioningUtils;
import org.apache.oodt.cas.metadata.Metadata;
import org.apache.oodt.cas.metadata.util.PathUtils;
import org.apache.oodt.commons.exec.EnvUtilities;

import java.io.File;
import java.util.logging.Logger;
import java.util.List;

public class RepoPathVersioner implements Versioner {
	public static String DELIMITER = ",";
	
  protected String originalFilenameKey = "OriginalFilename";
  private static Logger LOG = Logger.getLogger(RepoPathVersioner.class
      .getName());

  public RepoPathVersioner() {
    super();
  }

  public void createDataStoreReferences(Product product, Metadata metadata)
      throws VersioningException {
    for (Reference reference : product.getProductReferences()) {
      String originalFilename = new File(VersioningUtils
          .getAbsolutePathFromUri(reference.getOrigReference())).getName();
 
      metadata.removeMetadata(originalFilenameKey);
      metadata.addMetadata(originalFilenameKey, originalFilename);
     
      String dataStoreRef = replaceEnvVariables(product
          .getProductType().getProductRepositoryPath(), metadata, false);
      reference.setDataStoreReference(dataStoreRef);     
    }
  }
  
  public static String replaceEnvVariables(String origPath,
          Metadata metadata, boolean expand) {
      StringBuffer finalPath = new StringBuffer();
      for (int i = 0; i < origPath.length(); i++) {
          if (origPath.charAt(i) == '[') {
              VarData data = readEnvVarName(origPath, i);
              String var = null;
              if (metadata != null
                      && metadata.getMetadata(data.getFieldName()) != null) {
                  List valList = metadata.getAllMetadata(data.getFieldName());
                  var = (String) valList.get(0);
                  if (expand)
                      for (int j = 1; j < valList.size(); j++)
                          var += DELIMITER + (String) valList.get(j);
              } else {
                  var = EnvUtilities.getEnv(data.getFieldName());
              }
              finalPath.append(var);
              i = data.getEndIdx();
          } else {
              finalPath.append(origPath.charAt(i));
          }
      }

      return finalPath.toString();
  }
  private static VarData readEnvVarName(String origPathStr, int startIdx) {
      StringBuffer varName = new StringBuffer();
      int idx = startIdx + 1;

      do {
          varName.append(origPathStr.charAt(idx));
          idx++;
      } while (origPathStr.charAt(idx) != ']');

      VarData data = new RepoPathVersioner().new VarData();
      data.setFieldName(varName.toString());
      data.setEndIdx(idx);
      return data;

  }

  class VarData {

      private String fieldName = null;

      private int endIdx = -1;

      public VarData() {
      }

      /**
       * @return the endIdx
       */
      public int getEndIdx() {
          return endIdx;
      }

      /**
       * @param endIdx
       *            the endIdx to set
       */
      public void setEndIdx(int endIdx) {
          this.endIdx = endIdx;
      }

      /**
       * @return the fieldName
       */
      public String getFieldName() {
          return fieldName;
      }

      /**
       * @param fieldName
       *            the fieldName to set
       */
      public void setFieldName(String fieldName) {
          this.fieldName = fieldName;
      }

  }
}