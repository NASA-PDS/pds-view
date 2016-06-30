package gov.nasa.arc.pds.lace.client.event;

import gov.nasa.arc.pds.lace.client.presenter.ContainerPresenter;
import gov.nasa.arc.pds.lace.shared.Container;

/**
 * Defines an event that is triggered when the value of
 * a simple field is changed.
 */
public class FieldValueChangedEvent extends GenericEvent<FieldValueChangedEvent.EventDetails, FieldValueChangedEvent.Handler> {

	/** The type of the event. */
	public static final Type<FieldValueChangedEvent.Handler> TYPE = new Type<FieldValueChangedEvent.Handler>();

	/**
	 * Defines the interface the event handlers must implement.
	 */
	public interface Handler extends GenericEventHandler<FieldValueChangedEvent.EventDetails> { }

	/**
	 * Implements an object that holds details about
	 * the changed 'name' field.
	 */
	public static class EventDetails {

		private ContainerPresenter containerPresenter;
		private Container container;
		private String value;

		/**
		 * Creates a new instance of the event details.
		 *
		 * @param container the container that the name field is a child of
		 * @param value the new value of the name field
		 * @param containerPresenter the presenter of the container that the name field is a child of
		 */
		public EventDetails(Container container, String value, ContainerPresenter containerPresenter) {
			this.container = container;
			this.value = value;
			this.containerPresenter = containerPresenter;
		}

		/**
		 * Gets the container that the name field is a child of.
		 *
		 * @return a container
		 */
		public Container getContainer() {
			return container;
		}

		/**
		 * Gets the new value of the name field.
		 *
		 * @return the new value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Gets the presenter of a container that the name field is a child of.
		 *
		 * @return a container presenter
		 */
		public ContainerPresenter getContainerPresenter() {
			return containerPresenter;
		}
	}

	/**
	 * Creates a new instance of the event.
	 *
	 * @param container the container that the name field is a child of
	 * @param value the new value of the name field
	 * @param containerPresenter the presenter of the container that the name field is a child of
	 */
	public FieldValueChangedEvent(Container container, String value, ContainerPresenter containerPresenter) {
		super(new EventDetails(container, value, containerPresenter), TYPE);
	}
}
