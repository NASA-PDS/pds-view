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
package gov.nasa.pds.harvest.crawler.stats;

/**
 * Class that captures statistics on how associations were handled during
 * the crawling process.
 *
 * @author mcayanan
 *
 */
public class AssociationStats {
  /** The number of associations skipped. */
  private int numSkipped;

  /** The number of associations registered. */
  private int numRegistered;

  /** The number of associations not registered. */
  private int numNotRegistered;

  /**
   * Constructor.
   *
   */
  public AssociationStats() {
    numSkipped = 0;
    numRegistered = 0;
    numNotRegistered = 0;
  }

  /**
   * Clears the statistics
   *
   */
  public void clear() {
    numSkipped = 0;
    numRegistered = 0;
    numNotRegistered = 0;
  }

  /**
   * @return the number of associations skipped.
   *
   */
  public int getNumSkipped() {
    return numSkipped;
  }

  /**
   * Adds the given number to the number of associations skipped.
   *
   * @param num An integer value to add.
   */
  public void addNumSkipped(int num) {
    numSkipped += num;
  }

  /**
   * @return the number of associations registered.
   */
  public int getNumRegistered() {
    return numRegistered;
  }

  /**
   * Adds the given number to the number of associations registered.
   *
   * @param num An integer value to add.
   *
   */
  public void addNumRegistered(int num) {
    numRegistered += num;
  }

  /**
   * @return the number of associations not registered.
   */
  public int getNumNotRegistered() {
    return numNotRegistered;
  }

  /**
   * Adds the given number to the number of associations not registered.
   *
   * @param num An integer value to add.
   */
  public void addNumNotRegistered(int num) {
    numNotRegistered += num;
  }
}
