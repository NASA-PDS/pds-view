package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;
import gov.nasa.arc.pds.lace.client.presenter.PopupPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SimpleItemPresenter;
import gov.nasa.arc.pds.lace.client.util.SubstringSuggestOracle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements the view for the display of a simple element, with
 * no subcontent.
 */
public class SimpleItemView extends Composite implements SimpleItemPresenter.Display {

	private static final int DROPDOWN_IMAGE_WIDTH = 18;

	/** A style name used to indicate that the simple item has the focus. */
	private static final String FOCUS_STYLE = "focus";
	
	/** A style name used to hide the alert icon. */
	private static final String HIDDEN_STYLE = "hidden";

	private static SimpleItemViewUiBinder uiBinder = GWT.create(SimpleItemViewUiBinder.class);

	interface SimpleItemViewUiBinder extends UiBinder<Widget, SimpleItemView> { }
	
	private SimpleItemPresenter presenter;
	private SubstringSuggestOracle oracle;
	private boolean deleteEventsAttached = false;

	@UiField
	HTMLPanel alertIcon;
	
	@UiField
	InlineLabel label;
	
	@UiField
	HTMLPanel value;

	@UiField(provided=true)
	SuggestBox textBox;

	@UiField
	HorizontalPanel horizontalPanel;
	
	@UiField
	FocusPanel simpleItem;
	
	@UiField(provided=true)
	ModificationButtonPresenter modificationButton;

	/**
	 * Creates a new instance of the view.
	 * 
	 * @param an instance of the modification button presenter
	 */
	@Inject
	public SimpleItemView(ModificationButtonPresenter modButton) {
		modificationButton = modButton;
		oracle = new SubstringSuggestOracle();
		textBox = new SuggestBox(oracle, new TextBox(), new ScrollingSuggestionDisplay());
		textBox.setLimit(1000);
		textBox.setAutoSelectEnabled(false);
		
		initWidget(uiBinder.createAndBindUi(this));

		horizontalPanel.setCellWidth(label, "20%");
		horizontalPanel.setCellWidth(value, "100%");

		textBox.getValueBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//TODO: Do a better job of determining whether the mouse
				// click location should bring up the popup.
				Element inputElement = textBox.getValueBox().getElement();
				int x = event.getRelativeX(inputElement);
				if (x >= inputElement.getClientWidth() - DROPDOWN_IMAGE_WIDTH) {
					if (textBox.getValue().isEmpty()) {
						//TODO: Find a different way of showing all options.
						textBox.setValue(" ");
					}
					textBox.showSuggestionList();
				}
			}
		});

		textBox.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				value.addStyleName(FOCUS_STYLE);
			}
		});

		textBox.getValueBox().addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				value.removeStyleName(FOCUS_STYLE);
			}
		});
		
		textBox.getValueBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				presenter.saveValue(event.getValue());
			}
		});
	}

	@Override
	public void setPresenter(SimpleItemPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setLabel(String text, boolean required) {
		if (required) {
			label.setStyleName("required");
		}
		label.setText(text);
	}

	@Override
	public void setValue(String value) {
		textBox.setValue(value, true);
	}

	@Override
	public void setValidValues(String[] values) {
		oracle.clear();
		for (String suggestion : values) {
			oracle.add(suggestion);
		}

		if (values.length > 0) {
			textBox.addStyleName("hasSuggestions");
		}
	}
	
	@Override
	public void setState(boolean complete) {
		if (complete) {
			alertIcon.addStyleName(HIDDEN_STYLE);			
		} else {
			alertIcon.removeStyleName(HIDDEN_STYLE);
		}
	}

    /**
     * Implements a {@link SuggestBox.SuggestionDisplay} that supports
     * scrolling.
     */
    private static class ScrollingSuggestionDisplay extends SuggestBox.DefaultSuggestionDisplay {

		/** 
		 * The class name used by the default suggestion display to
		 * indicate the currently selected suggestion.
		 */
		private static final String ITEM_SELECTED = "item-selected";
		
		private ScrollPanel scrollPanel;
		
		@Override
		protected Widget decorateSuggestionList(Widget suggestionList) {
			scrollPanel = new ScrollPanel(suggestionList);
			scrollPanel.setStyleName("suggestScrollContent");
			return scrollPanel;
		}
		
		@Override
		protected void moveSelectionDown() {
	        super.moveSelectionDown();
	        scrollSelectedItemIntoView();
		}
		
		@Override
		protected void moveSelectionUp() {
	        super.moveSelectionUp();
	        scrollSelectedItemIntoView();
		}
		
		/**
		 * Finds the currently selected suggestion, if any, and ensures
		 * that it is visible by scrolling it into view.
		 */
		private void scrollSelectedItemIntoView() {
	        NodeList<Element> items = scrollPanel.getElement().getElementsByTagName("td");
	
	        for (int i=0; i < items.getLength(); ++i) {
                Element item = items.getItem(i);
                if (item.getClassName().contains(ITEM_SELECTED)) {
                    item.scrollIntoView();
                    break;
                }
	        }
		}

    }

	@Override
	public void addDeleteEventListeners() {
		if (!deleteEventsAttached) {
			simpleItem.addMouseMoveHandler(new MouseMoveHandler() {
				
				@Override
				public void onMouseMove(MouseMoveEvent event) {
					event.preventDefault();
					presenter.handleMouseEvent(true);
				}
			});
			
			simpleItem.addMouseOverHandler(new MouseOverHandler() {
				
				@Override
				public void onMouseOver(MouseOverEvent event) {
					event.preventDefault();
					presenter.handleMouseEvent(true);
					
				}
			});
			
			simpleItem.addMouseOutHandler(new MouseOutHandler() {
				
				@Override
				public void onMouseOut(MouseOutEvent event) {
					event.preventDefault();
					presenter.handleMouseEvent(false);				
				}
			});
		
			modificationButton.addEventHandler(new ClickHandler() {
	
				@Override
				public void onClick(ClickEvent event) {	
					event.stopPropagation();
					presenter.modifyElement();
				}
				
			});
		
			deleteEventsAttached = true;
		}	

	}

	@Override
	public void enableModification(boolean show) {
		modificationButton.setVisible(show);
	}
	
	@Override
	public void handleDeleteAction(String name, PopupPresenter popup) {
		String msg = "This action will permanently delete the '" + name 
				+ "' element. Do you want to continue?";
		enableModification(false);
		showConfirmationPopup("Delete", msg, popup);
	}
	
	private void showConfirmationPopup(String title, String msg, final PopupPresenter popup) {		
		ClickHandler yesBtnHandler = new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.deleteElement();
			}
		};
		
		ClickHandler noBtnHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				presenter.cancelDelete();
			}
		};
		
		popup.setText(title);
		popup.setContent(msg);
		popup.setConfirmation(yesBtnHandler, noBtnHandler);
		popup.display();	
	}
	
	@Override
	public boolean isDeleteEventsAttached() {
		return deleteEventsAttached;		
	}
}
