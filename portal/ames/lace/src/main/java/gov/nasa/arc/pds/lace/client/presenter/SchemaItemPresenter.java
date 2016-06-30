package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.view.SchemaItemView;

import javax.inject.Inject;

import com.google.inject.ImplementedBy;

/**
 * Implements a component that represents a single schema file in the
 * local schema file list.
 */
public class SchemaItemPresenter extends Presenter<SchemaItemPresenter.Display> {

	/**
	 * Defines the interface the view must implement.
	 */
	@ImplementedBy(SchemaItemView.class)
	public interface Display extends Presenter.Display<SchemaItemPresenter> {

		/**
		 * Sets the name of the schema item.
		 * 
		 * @param name the new name
		 */
		void setItemName(String name);
		
	}
	
	/**
	 * Defines an interface a caller may implement to get
	 * events.
	 */
	public interface Handler {

		/**
		 * The user has requested to delete the schema item.
		 * 
		 * @param name the item name
		 */
		void onRequestDelete(String name);
		
	}

	private String itemName;
	private Handler handler;

	/**
	 * Creates a new instance.
	 * 
	 * @param view the view to use for user interaction and display.
	 */
	@Inject
	public SchemaItemPresenter(Display view) {
		super(view);
		view.setPresenter(this);
	}

	/**
	 * Sets the name of the schema item.
	 * 
	 * @param name the new name
	 */
	public void setItemName(String name) {
		this.itemName = name;
		getView().setItemName(name);
	}
	
	/**
	 * Sets the callback handler for item deletion requests.
	 * 
	 * @param handler the handler
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * Handles a request to delete the schema item.
	 */
	public void onRequestDelete() {
		if (handler != null) {
			handler.onRequestDelete(itemName);
		}
	}
	
}
