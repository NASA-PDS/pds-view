package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItemType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;

/**
 * Implements the presenter for a widget that displays an insertion point as
 * a plus button, an optional panel for inserting optional complex elements 
 * or a required panel for inserting an element from a list of choices. 
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
		 * Sets the title for an optional element.
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
	public void display(
			InsertionPoint insPoint,
			ContainerPresenter parentPresenter,
			int top,
			int right
	) {
		assert insPoint != null;

		this.insPoint = insPoint;		
		this.parentPresenter = parentPresenter;
		togglePanels(top, right);
	}
	
	/**
	 * Handles a click on the link inside the optional  panel.
	 * 
	 * @param event
	 */
	public void handleOptionalPanelClickEvent(ClickEvent event) {			
		bus.fireEvent(new ElementSelectionEvent(0, 0, insPoint, parentPresenter, null));				
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
		popup.setText(text);
		popup.setContent(setupListbox());
		popup.display();
	}
	
	private ListBox setupListbox() {
		int count = 0;
		final ListBox listbox = new ListBox();
		final List<LabelItemType> listboxTypes = new ArrayList<LabelItemType>();
		final List<InsertOption> alternatives = insPoint.getAlternatives();
		
		// Add items to the list box
		for (InsertOption alternative : alternatives) {
			if (alternative.getMaxOccurrences() != alternative.getUsedOccurrences()) {						
				for (LabelItemType type : alternative.getTypes()) {
					listbox.addItem(type.getElementName());
					listboxTypes.add(type);
					count++;
				}
			}
		}
		
		listbox.setVisibleItemCount(count + 1);
		listbox.setSelectedIndex(-1);
		
		listbox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				event.stopPropagation();
				handleElementSelection(listboxTypes.get(listbox.getSelectedIndex()), alternatives);
			}
			
		});
		
		return listbox;
	}
	
	/**
	 * Notifies the registered handlers that an element is selected.
	 * 
	 * @param type the selected item's type
	 * @param alternatives the list of alternatives for this insertion point
	 */
	private void handleElementSelection(LabelItemType type, List<InsertOption> alternatives) {
		int alternativeIndex = findInsertOption(type, alternatives);
		
		bus.fireEvent(new ElementSelectionEvent(
				alternativeIndex,
				alternatives.get(alternativeIndex).getTypes().indexOf(type),
				insPoint,
				parentPresenter,
				popup
		));
	}
	
	/**
	 * Finds the position of the insert option instance that
	 * holds the specified label item type.
	 * 
	 * @param type the selected element's label item type
	 * @param alternatives the list of alternatives for this insertion point
	 * @return the index of an insert option that contains the type
	 */
	private int findInsertOption(LabelItemType type, List<InsertOption> alternatives) {
		for (InsertOption alternative : alternatives) {
			if (alternative.getMaxOccurrences() != alternative.getUsedOccurrences()) {						
				if (alternative.getTypes().contains(type)) {
					return alternatives.indexOf(alternative);
				}
			}
		}
		return -1;
	}
	
	// TODO: clean up the code inside this method
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
				// The alternatives list should only have one insert option 
				// with one type.
				List<InsertOption> alternatives = insPoint.getAlternatives();
				assert alternatives.size() == 1;
				
				List<LabelItemType> types = alternatives.get(0).getTypes();
				assert types.size() == 1;
				
				view.setOptionalTitle(types.get(0).getElementName());
				view.showPlusButton(false);
				view.showOptionalPanel(true);
			} else {
				view.showOptionalPanel(false);
				
				if (isMaxExhaused()) {
					view.showPlusButton(false);
				} else {
					view.showPlusButton(true);
					view.setPlusButtonPosition(top, right);
				}				
			}			
		}
	}
	// TODO: name correct?
	private boolean isMaxExhaused() {
		List<InsertOption> alternatives = insPoint.getAlternatives();
		for (InsertOption alternative : alternatives) {
			if (alternative.getMaxOccurrences() != alternative.getUsedOccurrences()) {
				return false;
			}	
		}
		return true;
	}
}
