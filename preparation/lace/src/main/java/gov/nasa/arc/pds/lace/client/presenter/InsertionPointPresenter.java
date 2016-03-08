package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
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
 * a plus button, or an optional panel for inserting an optional complex 
 * element or a required panel for inserting an element from a list of choices. 
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
		void showPlusButton(boolean show);
		
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
	private PopupPresenter popup;
	private InsertionPoint insPoint;
	private ContainerPresenter parentPresenter;
	
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
	public void display(InsertionPoint insPoint, ContainerPresenter parentPresenter, int top, int right) {
		assert insPoint != null;

		this.insPoint = insPoint;		
		this.parentPresenter = parentPresenter;
		togglePanels(top, right);
	}
	
	/**
	 * Handles a click on the link inside the optional  panel.
	 * @param event
	 */
	public void handleOptionalPanelClickEvent(ClickEvent event) {			
		bus.fireEvent(new ElementSelectionEvent(0, insPoint, parentPresenter, null));				
	}
	
	/**
	 * Handles a click on the plus button.
	 * 
	 * @param event
	 */
	public void handlePlusButtonClickEvent(ClickEvent event) {
		showPopup("Add an element");		
	}
	
	/**
	 * Handles a click on the link inside the required choice panel.
	 * 
	 * @param event
	 */
	public void handleRequiredPanelClickEvent(ClickEvent event) {
		showPopup("Choose an element");		
	}
	
	private void showPopup(String text) {
		int count = 0;
		final ListBox listbox = new ListBox();
		List<LabelItemType> alternatives = insPoint.getAlternatives();
		
		// Add items within the insertable range to the list box
		for (int i = insPoint.getInsertFirst(); i <= insPoint.getInsertLast(); ++i) {
			listbox.addItem(alternatives.get(i).getElementName());
			count++;
		}
		
		listbox.setVisibleItemCount(count + 1);
		listbox.setSelectedIndex(-1);		
		listbox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				event.stopPropagation();
				// Notify the registered handlers that an element is selected. 			
				bus.fireEvent(new ElementSelectionEvent(
						listbox.getSelectedIndex(), insPoint, parentPresenter, popup));
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
			view.showPlusButton(false);
			view.showOptionalPanel(false);
			view.showRequiredPanel(true);
		} else {
			view.showRequiredPanel(false);			
			if (displayType.equals(InsertionPoint.DisplayType.OPTIONAL.getDisplayType())) {
				// Assuming the alternatives list has only one element.
				// TODO: need to do error checking
				view.setOptionalTitle(insPoint.getAlternatives().get(0).getElementName());				
				view.showOptionalPanel(true);
				view.showPlusButton(false);
			} else {
				view.showPlusButton(true);
				view.setPlusButtonPosition(top, right);
				view.showOptionalPanel(false);
			}			
		}
	}
}
