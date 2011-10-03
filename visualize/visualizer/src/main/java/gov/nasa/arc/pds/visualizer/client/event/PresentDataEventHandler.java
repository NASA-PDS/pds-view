package gov.nasa.arc.pds.visualizer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PresentDataEventHandler extends EventHandler {
  void onEvent(PresentDataEvent event);
}
