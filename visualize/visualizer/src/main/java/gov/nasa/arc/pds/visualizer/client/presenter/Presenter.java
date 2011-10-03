package gov.nasa.arc.pds.visualizer.client.presenter;

import gov.nasa.pds.domain.PDSObject;


public abstract interface Presenter {
  public abstract void go(PDSObject dataItem);
}
