package gov.nasa.arc.pds.visualizer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ErrorReportEventHandler extends EventHandler {
  void onEvent(ErrorReportEvent event);
}
