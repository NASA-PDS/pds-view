package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;

/**
 * Implements the presenter for a widget that displays an insertion point as
 * a plus button, or an optional panel for inserting an optional element or a
 * required panel for inserting an element from a list of choices. 
 */
public class InsertionPointPresenter extends Presenter<InsertionPointPresenter.Display> {
	
	/**
	 * Defines an interface that the view must implement.	
	 */
	public interface Display extends Presenter.Display<InsertionPointPresenter> {
		
		/**
		 * Sets the position of the plus button in the view.
		 * 
		 * @param top the top position in pixel
		 * @param right the right position in pixel
		 */
		void setPlusButtonPosition(int top, int right);
		
		/**
		 * Shows/hides the plus button panel.
		 * 
		 * @param show true, shows the panel, false, hides it.
		 */
		void showButtonPanel(boolean show);
		
		/**
		 * Shows/hides the required panel which is
		 * used as a place holder for a choice.
		 * 
		 * @param show true, shows the panel, false, hides it.
		 */
		void showRequiredPanel(boolean show);
		
		/**
		 * Shows/hides the optional panel which is used
		 * as a place holder for an optional element.
		 * 
		 * @param show true, shows the panel, false, hides it.
		 */
		void showOptionalPanel(boolean show);				
		
		/**
		 * Sets the title of the optional element insertion point.
		 * 
		 * @param title the optional element name
		 */
		void setOptionalTitle(String title);
	}
	
	private EventBus bus;
	private List<LabelItemType> alternatives;
	private PopupPresenter popup;
	private InsertionPoint insPoint;
	private Container parentContainer;
	
	/**
	 * Creates a new instance of the insertion point presenter. 
	 * 
	 * @param view the view to use for user interaction and display
	 * @param popup
	 * @param bus	 
	 */	
	@Inject
	public InsertionPointPresenter(Display view, PopupPresenter popup, EventBus bus) {
		super(view);
		this.bus = bus;
		this.popup = popup;
		view.setPresenter(this);
	}

	/**
	 * Displays an insertion point.
	 * 
	 * @param insPoint
	 * @param container the container in which this insertion point belongs to.
	 * @param top
	 * @param right
	 */
	public void display(InsertionPoint insPoint, Container container, int top, int right) {
		assert insPoint != null;

		this.insPoint = insPoint;
		this.alternatives = insPoint.getAlternatives();	
		this.parentContainer = container;
		togglePanels(top, right);
	}
	
	/**
	 * Handles a click on the link inside the optional  panel.
	 * @param event
	 */
	public void handleOptionalPanelClickEvent(ClickEvent event) {	
		bus.fireEvent(new ElementSelectionEvent(0, insPoint, parentContainer, null));				
	}
	
	/**
	 * Handles a click on the plus button.
	 * 
	 * @param event
	 */
	public void handlePlusButtonClickEvent(ClickEvent event) {
		showPopup(this.alternatives, "Add an element");		
	}
	
	/**
	 * Handles a click on the link inside the required choice panel.
	 * 
	 * @param event
	 */
	public void handleRequiredPanelClickEvent(ClickEvent event) {			
		showPopup(this.alternatives, "Choose an element");		
	}
	
	/**
	 * 
	 * @param alternatives
	 * @param text
	 */
	private void showPopup(List<LabelItemType> alternatives, String text) {
		final ListBox listbox = new ListBox();
		listbox.setVisibleItemCount(alternatives.size() + 1);
		listbox.setSelectedIndex(-1);
		
		Iterator<LabelItemType> iterator = alternatives.iterator();
		while (iterator.hasNext()) {
			listbox.addItem(iterator.next().getElementName());
		}	
				
		listbox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {				
				// Fire an event to registered handlers to notify them 
				// that an element is selected from an insertion point.				
				bus.fireEvent(new ElementSelectionEvent(listbox.getSelectedIndex(), insPoint, parentContainer, popup));
			}
		});
		
		popup.setText(text);
		popup.setContent(listbox);
		popup.display();				
	}
	
	private void togglePanels(int top, int right) {
		Display view = getView();
		String displayType = insPoint.getDisplayType();
		
		if (displayType.equals(InsertionPoint.DisplayType.CHOICE.getDisplayType())) {
			view.showButtonPanel(false);
			view.showOptionalPanel(false);
			view.showRequiredPanel(true);
		} else {
			view.showRequiredPanel(false);			
			if (displayType.equals(InsertionPoint.DisplayType.OPTIONAL.getDisplayType())) {
				// Assuming the alternatives list has only one element.
				// TODO: need to do error checking
				view.setOptionalTitle(insPoint.getAlternatives().get(0).getElementName());				
				view.showOptionalPanel(true);
				view.showButtonPanel(false);
			} else {
				view.showButtonPanel(true);
				view.setPlusButtonPosition(top, right);
				view.showOptionalPanel(false);
			}			
		}
	}
}
