package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.Feedback;

/**
 * Defines an event that is triggered when a system error occurs.
 */
public class SystemFailureEvent extends GenericEvent<SystemFailureEvent.EventDetails, SystemFailureEvent.Handler> {

	/** The type of the event. */
	public static final Type<SystemFailureEvent.Handler> TYPE = new Type<SystemFailureEvent.Handler>();

	/**
	 * Defines the interface the event handlers must implement.
	 */
	public interface Handler extends GenericEventHandler<SystemFailureEvent.EventDetails> { }
	
	/**
	 * Implements an object that holds details about the service failure. 
	 */
	public static class EventDetails {
	
		private String message;
		private Feedback feedback;
		
		/**
		 * Creates a new instance of the event.
		 * 
		 * @param message the error message
		 * @param feedback the feedback object
		 */
		public EventDetails(String message, Feedback feedback) {
			this.message = message;
			this.feedback = feedback;
		}
		
		/**
		 * Gets the error message.
		 * 
		 * @return 
		 */
		public String getMessage() {
			return message;
		}
		
		/**
		 * Gets the feedback object.
		 * 
		 * @return a feedback object
		 */
		public Feedback getFeedback() {
			return feedback;
		}
	}
	
	/**
	 * Creates a new instance of the event.
	 * 
	 * @param message the error message
	 */
	public SystemFailureEvent(String message) {
		super(new EventDetails(message, null), TYPE);
	}
	
	/**
	 * Creates a new instance of the event.
	 * 
	 * @param message the error message
	 * @param feedback the feedback object
	 */
	public SystemFailureEvent(String message, Feedback feedback) {
		super(new EventDetails(message, feedback), TYPE);
	}
}
