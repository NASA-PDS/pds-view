package gov.nasa.arc.pds.visualizer.client.event;


import gov.nasa.pds.domain.PDSObject;

import com.google.gwt.event.shared.GwtEvent;

public class PresentDataEvent extends GwtEvent<PresentDataEventHandler> {
	public static Type<PresentDataEventHandler> TYPE = new Type<PresentDataEventHandler>();
	private PDSObject dataItem = null;

	public PresentDataEvent(PDSObject dataItem) {
		super();
		this.dataItem = dataItem;
	}

	@Override
	public Type<PresentDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PresentDataEventHandler handler) {
		handler.onEvent(this);
	}

	public PDSObject getDataItem() {
		return dataItem;
	}
}
