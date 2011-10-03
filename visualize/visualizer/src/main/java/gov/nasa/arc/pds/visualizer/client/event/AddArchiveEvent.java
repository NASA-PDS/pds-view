package gov.nasa.arc.pds.visualizer.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddArchiveEvent extends GwtEvent<AddArchiveEventHandler> {
  public static Type<AddArchiveEventHandler> TYPE = new Type<AddArchiveEventHandler>();
  
  @Override
  public Type<AddArchiveEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AddArchiveEventHandler handler) {
    handler.onAddContact(this);
  }
}
