package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.WildcardElementSelectedEvent;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.view.WildcardView;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays a list of namespaces
 * and allows the user to insert an element from the list of available 
 * elements for the selected namespace.
 */
public class WildcardPresenter extends Presenter<WildcardPresenter.Display> {
	
	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(WildcardView.class)
	public interface Display extends  Presenter.Display<WildcardPresenter> {
		
		/**
		 * Sets the list of namespaces.
		 * 
		 * @param namespaces a list of namespaces
		 */
		void setNamespaces(List<String> namespaces);
		
		/**
		 * Sets the list of element names.		 
		 * 
		 * @param elementNames a list of element names
		 */
		void setElementNames(Collection<String> elementNames);
		
		/**
		 * Gets the default value of the element names dropdown.
		 * 
		 * @return the default value of the element names dropdown
		 */
		String getDefaultValue();
	}
	
	private PopupPresenter popup;
	private LabelContentsServiceAsync service;
	private InsertionPointPresenter insPointPresenter;
	
	/**
	 * Creates an instance of the wildcard presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param popup the popup to use for displaying error messages
	 * @param service
	 */
	@Inject
	public WildcardPresenter(
			Display view,
			PopupPresenter popup,
			LabelContentsServiceAsync service
	) {
		super(view);
		view.setPresenter(this);
		this.service = service;
		this.popup = popup;
	}
	
	/**
	 * Displays the widget. 
	 * 
	 * @param namespaces the list of namespaces to populate the view with
	 * @param insPointPresenter the presenter of the insertion point that this widget is created from.
	 */
	public void display(List<String> namespaces, InsertionPointPresenter insPointPresenter) {
		Display view = getView();
		view.setNamespaces(namespaces);		
		this.insPointPresenter = insPointPresenter;
	}
	
	/**
	 * Gets the list of top-level elements for the given namespace and loads them in the view.
	 * 
	 * @param namespace
	 */
	public void loadTopLevelElementNames(final String namespace) {		
		service.getElementNamesByNamespace(namespace, new AsyncCallback<Collection<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				String msg = "The system was not able to get the top-level element names for namespace='" + namespace + "'.";
				GWT.log(msg + " " + caught.getMessage());
				fireEvent(new SystemFailureEvent(msg));
			}

			@Override
			public void onSuccess(Collection<String> elementNames) {
				getView().setElementNames(elementNames);
			}
			
		});	
	}
	
	/**
	 * Cancels the element insertion by firing ClearEvent event.
	 */
	public void cancel() {
		fireEvent(new ClearEvent());
	}
	
	/**
	 * Inserts the given element by firing WildcardElementSelectedEvent event.
	 * 
	 * @param event the submit event
	 * @param namespace the selected namespace
	 * @param elementName the selected element name
	 */
	public void insertElement(SubmitEvent event, String namespace, String elementName) {		
		if (elementName.equals(getView().getDefaultValue())) {
			popup.showErrorBox("Please select an element name.");
			event.cancel();
			return;
		}

		fireEvent(new WildcardElementSelectedEvent(insPointPresenter, namespace, elementName));
	}
}
