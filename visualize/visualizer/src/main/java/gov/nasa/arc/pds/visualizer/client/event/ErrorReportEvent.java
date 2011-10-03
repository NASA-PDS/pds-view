package gov.nasa.arc.pds.visualizer.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ErrorReportEvent extends GwtEvent<ErrorReportEventHandler> {
	public static Type<ErrorReportEventHandler> TYPE = new Type<ErrorReportEventHandler>();
	private String message = null;

	public ErrorReportEvent(String msg) {
		message = msg;
	}

	@Override
	public Type<ErrorReportEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ErrorReportEventHandler handler) {
		handler.onEvent(this);
	}

	public String getMessage() {
		return message;
	}
}
