package gov.nasa.arc.pds.lace.client.view;

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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of a simple element, with
 * no subcontent.
 */
public class SimpleItemView extends Composite implements SimpleItemPresenter.Display {

	private static final int DROPDOWN_IMAGE_WIDTH = 18;

	/** A style name used to indicate that the simple item has the focus. */
	private static final String FOCUS_STYLE = "focus";

	private static SimpleItemViewUiBinder uiBinder = GWT.create(SimpleItemViewUiBinder.class);

	interface SimpleItemViewUiBinder extends UiBinder<Widget, SimpleItemView> { }

	private SimpleItemPresenter presenter;
	private SubstringSuggestOracle oracle;

	@UiField
	FlowPanel form;

	@UiField
	InlineLabel label;

	@UiField
	HTMLPanel value;

	@UiField(provided=true)
	SuggestBox textBox;

	@UiField
	HorizontalPanel horizontalPanel;

	/**
	 * Creates a new instance of the view.
	 */
	public SimpleItemView() {
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
				presenter.saveValue(textBox.getValue());
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

    /**
     * Implements a {@link SuggestBox.SuggestionDisplay} that supports
     * scrolling.
     */
    private static class ScrollingSuggestionDisplay extends SuggestBox.DefaultSuggestionDisplay {

            /** The class name used by the default suggestion display to
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

}
