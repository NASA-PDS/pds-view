package gov.nasa.arc.pds.lace.client.event;


public class ClearEvent extends GenericEvent<String, ClearEventHandler> {

	/** The GWT type of the event. */
	public static final Type<ClearEventHandler> TYPE = new Type<ClearEventHandler>();
	
	public ClearEvent(String state) {
		super(state, TYPE);
	}

}
