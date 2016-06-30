package gov.nasa.arc.pds.lace.client.presenter;

import gov.nasa.arc.pds.lace.client.event.ClearEvent;
import gov.nasa.arc.pds.lace.client.event.ElementPasteEvent;
import gov.nasa.arc.pds.lace.client.event.ElementSelectionEvent;
import gov.nasa.arc.pds.lace.client.event.SystemFailureEvent;
import gov.nasa.arc.pds.lace.client.event.WildcardElementSelectedEvent;
import gov.nasa.arc.pds.lace.client.event.WildcardElementSelectedEvent.EventDetails;
import gov.nasa.arc.pds.lace.client.service.LabelContentsServiceAsync;
import gov.nasa.arc.pds.lace.client.util.ClipboardManager;
import gov.nasa.arc.pds.lace.client.util.InsertOptionMap;
import gov.nasa.arc.pds.lace.client.view.InsertionPointView;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.WildcardType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;

/**
 * Implements the presenter for a widget that displays an insertion point as
 * a plus button, an optional panel for inserting optional complex elements,
 * a required panel for inserting an element from a list of choices or an any
 * panel for inserting any element from a list of allowed namespaces.
 */
public class InsertionPointPresenter extends Presenter<InsertionPointPresenter.Display> {

	/**
	 * Defines an interface that the view must implement.
	 */
	@ImplementedBy(InsertionPointView.class)
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

		/**
		 * Shows/hides the any panel which is
		 * used as a place holder for a wildcard element.
		 *
		 * @param show true, shows the panel, false, hides it.
		 */
		void showAnyPanel(boolean show);

		/**
		 * Sets the type of insertion that can occur. This affects the styling
		 * of the UI elements.
		 *
		 * @param insertionType a string indicating the insertion type
		 */
		void setInsertionType(String insertionType);

	}

	private PopupPresenter popup;
	private InsertionPoint insPoint;
	private ContainerPresenter parentPresenter;
	private InsertOptionMap insertOptionMap;
	private Provider<WildcardPresenter> wildcardProvider;
	private LabelContentsServiceAsync service;
	private InsertionPointPresenter presenter;
	private ClipboardManager clipboard;

	/**
	 * Creates a new instance of the insertion point presenter.
	 *
	 * @param view the view to use for user interaction and display
	 * @param popup the popup for showing the insert options
	 * @param insertOptionMap the map of insert options
	 * @param service the label RPC service
	 * @param wildcardProvider a provider of the wildcard insertion dialog
	 * @param clipboard the clipboard service
	 */
	@Inject
	public InsertionPointPresenter(
			Display view,
			PopupPresenter popup,
			InsertOptionMap insertOptionMap,
			LabelContentsServiceAsync service,
			Provider<WildcardPresenter> wildcardProvider,
			ClipboardManager clipboard
	) {
		super(view);
		this.popup = popup;
		this.insertOptionMap = insertOptionMap;
		this.wildcardProvider = wildcardProvider;
		this.service = service;
		this.presenter = this;
		this.clipboard = clipboard;
		view.setPresenter(this);
	}

	@Override
	protected void addEventHandlers() {
		if (insPoint.getDisplayType().equals(InsertionPoint.DisplayType.ANY.getDisplayType())) {
			addWildcardHandlers();
		}
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
		for (InsertOption alternative : insPoint.getAlternatives()) {
			int id = alternative.getId();
			if (insertOptionMap.get(id) == null) {
				insertOptionMap.put(id, alternative);
			}
		}
		togglePanels(top, right);

		if (insPoint.getDisplayType().equals(DisplayType.PLUS_BUTTON.getDisplayType())) {
			getView().setInsertionType("between");
		} else {
			getView().setInsertionType("replace");
		}
	}

	/**
	 * Handles a click on the optional panel.
	 *
	 * @param event the click event
	 */
	public void handleOptionalPanel(ClickEvent event) {
		handleElementSelection(0, insPoint.getAlternatives().get(0).getTypes().get(0), false);
	}

	/**
	 * Handles a click on the plus button.
	 *
	 * @param event
	 */
	public void handlePlusButtonClickEvent(ClickEvent event) {
		showPopup("Add an element", getListbox());
	}

	/**
	 * Handles a click on the any panel.
	 *
	 * @param event
	 */
	public void handleAnyPanel(ClickEvent event) {
		showPopup("Insert an element", getWildcard());
	}

	/**
	 * Handles a click on the required choice panel.
	 *
	 * @param event
	 */
	public void handleRequiredPanel(ClickEvent event) {
		showPopup("Choose an element", getListbox());
	}

	private void showPopup(String text, Widget widget) {
		popup.setText(text);
		popup.setContent(widget);
		popup.display();
	}

	private Widget getWildcard() {
		InsertOption insOption = insPoint.getAlternatives().get(0);
		WildcardType type = (WildcardType) insOption.getTypes().get(0);

		WildcardPresenter wildcardPresenter = wildcardProvider.get();
		wildcardPresenter.display(type.getNamespaces(), this);

		return wildcardPresenter.asWidget();
	}

	private ListBox getListbox() {
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
		listbox.addStyleName("insPointListBox");

		listbox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				event.stopPropagation();
				LabelItemType type = listboxTypes.get(listbox.getSelectedIndex());
				handleElementSelection(findInsertOption(type, alternatives), type, true);
			}

		});

		return listbox;
	}

	/**
	 * Notifies the registered handlers that an element is selected.
	 *
	 * @param alternativeIndex
	 * @param type the selected item's type
	 * @param isPopup true, if a popup was involved, false, otherwise
	 */
	private void handleElementSelection(int alternativeIndex, LabelItemType type, boolean isPopup) {
		fireEvent(new ElementSelectionEvent(
				alternativeIndex,
				type,
				insPoint,
				parentPresenter,
				(isPopup) ? popup : null
		));
	}

	private void addWildcardHandlers() {
		addEventHandler(WildcardElementSelectedEvent.TYPE, new WildcardElementSelectedEvent.Handler() {

			@Override
			public void onEvent(EventDetails data) {
				if (data.getPresenter().equals(presenter)) {
					service.getTypeForElement(data.getElementName(), data.getNamespace(), new AsyncCallback<LabelItemType>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("The service failed to get the label item type for the specified element: " + caught.getMessage());
							fireEvent(new SystemFailureEvent("The system failed to process the selected element."));
						}

						@Override
						public void onSuccess(LabelItemType type) {
							handleElementSelection(0, type, true);
						}
					});
				}
			}
		});

		addEventHandler(ClearEvent.TYPE, new ClearEvent.Handler() {
			@Override
			public void onEvent(String data) {
				popup.hide();
			}
		});
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
			view.showAnyPanel(false);
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
				view.showAnyPanel(false);
				view.showOptionalPanel(true);
			} else if (displayType.equals(InsertionPoint.DisplayType.ANY.getDisplayType())) {
					view.showPlusButton(false);
					view.showOptionalPanel(false);
					view.showAnyPanel(true);
			} else {
				view.showOptionalPanel(false);
				view.showAnyPanel(false);

				if (isMaxExhausted()) {
					view.showPlusButton(false);
				} else {
					view.showPlusButton(true);
					view.setPlusButtonPosition(top, right);
				}
			}
		}
	}

	private boolean isMaxExhausted() {
		List<InsertOption> alternatives = insPoint.getAlternatives();
		for (InsertOption alternative : alternatives) {
			if (alternative.getMaxOccurrences() != alternative.getUsedOccurrences()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Handle a request to paste from the clipboard.
	 */
	public void onRequestPaste() {
		if (!clipboard.hasItem()) {
			GWT.log("Should not get here - no clipboard item to paste");
		}

		LabelItem item = clipboard.getItem();
		if (!(item instanceof LabelElement)) {
			GWT.log("Should not happen - clipboard item is not a label element");
		}

		fireEvent(new ElementPasteEvent(
				(LabelElement) item,
				insPoint,
				parentPresenter
		));
	}

	public boolean canDropOnto() {
		return !insPoint.getDisplayType().equals(DisplayType.PLUS_BUTTON.getDisplayType());
	}

	public boolean canPaste() {
		if (!clipboard.hasItem()) {
			GWT.log("No clipboard item, cannot paste");
			return false;
		}

		LabelItem item = clipboard.getItem();
		if (!(item instanceof LabelElement)) {
			GWT.log("Clipboard item is not a label element, cannot paste");
			return false;
		}

		LabelElement element = (LabelElement) item;
		LabelItemType type = element.getType();

		for (InsertOption option : insPoint.getAlternatives()) {
			if (option.getMaxOccurrences() > 0 && option.getUsedOccurrences() >= option.getMaxOccurrences()) {
				GWT.log("Reached max occurrences, cannot paste");
			} else {
				for (LabelItemType optionType : option.getTypes()) {
					GWT.log("Checking drop target: "
							+ "{" + type.getElementName() + ":" + type.getElementNamespace() + "}"
							+ " ==? "
							+ "{" + optionType.getElementName() + ":" + optionType.getElementNamespace() + "}");
					if (type.getElementName().equals(optionType.getElementName())
							&& type.getElementNamespace().equals(optionType.getElementNamespace())) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
