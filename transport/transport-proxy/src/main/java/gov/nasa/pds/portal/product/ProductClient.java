// Copyright 2002-2015, by the California Institute of Technology.
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
// This class was copied from version 3.0.11 of the grid-product package and 
// modified to remove dependencies from the early OODT infrastructure like 
// the RMI Registry.
//
// $Id$

package gov.nasa.pds.portal.product;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import jpl.eda.product.ProductException;
import jpl.eda.product.ProductService;
import jpl.eda.product.Retriever;
import jpl.eda.product.Server;
import jpl.eda.product.test.TestRunner;
import jpl.eda.product.test.TestConfig;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;

/**
 * Client access to a product service.
 *
 * @author Kelly
 * @version $Revision$
 */
public class ProductClient implements Retriever {

  /** Product service we're using. */
  private ProductService productService;

  /**
   * Creates a new <code>ProductClient</code> instance.
   *
   * @param serviceID Name of the product server to contact.
   * @throws ProductException if an error occurs.
   */
  public ProductClient(String serviceID) throws ProductException {
    try {
      if (serviceID.startsWith("https")) {
        productService = new HTTPSAdaptor(new URL(serviceID));
      } else {
        productService = new HTTPAdaptor(new URL(serviceID));
      }
    } catch (MalformedURLException ex) {
      throw new ProductException(ex);
    }
  }

  /**
   * Query the product server.
   *
   * @param q Query
   * @return Response.
   * @throws ProductException if an error occurs.
   */
  public XMLQuery query(XMLQuery q) throws ProductException {
    try {
      Server server = productService.createServer();
      XMLQuery response = server.query(q);
      response.setRetriever(this);
      return response;
    } catch (RemoteException ex) {
      throw new ProductException(ex);
    }
  }

  /**
   * Retrieve a chunk from a large product.
   *
   * @param productID Product ID.
   * @param offset Where in the product to retrieve the data.
   * @param length How much data to get.
   * @return The data.
   * @throws ProductException if an error occurs.
   */
  public byte[] retrieveChunk(String productID, long offset, int length) throws ProductException {
    try {
      Server server = productService.createServer();
      return server.retrieveChunk(productID, offset, length);
    } catch (RemoteException ex) {
      throw new ProductException(ex);
    }
  }

  /**
   * Close off a large product.
   *
   * @param productID Product ID.
   * @throws ProductException if an error occurs.
   */
  public void close(String productID) throws ProductException {
    try {
      Server server = productService.createServer();
      server.close(productID);
    } catch (RemoteException ex) {
      throw new ProductException(ex);
    }
  }
}
