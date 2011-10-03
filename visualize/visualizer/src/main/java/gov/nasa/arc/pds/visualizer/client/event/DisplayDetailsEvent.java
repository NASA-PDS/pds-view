package gov.nasa.arc.pds.visualizer.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DisplayDetailsEvent extends GwtEvent<DisplayDetailsEventHandler> {
	public static Type<DisplayDetailsEventHandler> TYPE = new Type<DisplayDetailsEventHandler>();

	public DisplayDetailsEvent() {
	}

	@Override
	public Type<DisplayDetailsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DisplayDetailsEventHandler handler) {
		handler.onEvent(this);
	}
}
