package gov.nasa.arc.pds.lace.client.view;

import gov.nasa.arc.pds.lace.client.event.ModificationEvent;
import gov.nasa.arc.pds.lace.client.event.ModificationEvent.ModificationType;
import gov.nasa.arc.pds.lace.client.presenter.ModificationButtonPresenter;
import gov.nasa.arc.pds.lace.client.presenter.SimpleItemPresenter;
import gov.nasa.arc.pds.lace.client.util.SubstringSuggestOracle;
import gov.nasa.arc.pds.lace.client.util.SuggestionProvider;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements the view for the display of a simple element, with
 * no subcontent.
 */
public class SimpleItemView extends Composite implements SimpleItemPresenter.Display {

	interface SimpleItemViewUiBinder extends UiBinder<Widget, SimpleItemView> { /*empty*/ }

	/** A style name used to indicate that the simple item has the focus. */
	private static final String FOCUS_STYLE = "focus";

	/** A style name used to hide components. */
	private static final String HIDDEN_STYLE = "hidden";

	/** A style to indicate when the documentation is being shown. */
	private static final String OPEN_STYLE = "open";

	/** A place holder text used to display as a hint in fields with a drop down. */
	private static final String PLACEHOLDER_TEXT = "Make a selection";

	private static SimpleItemViewUiBinder uiBinder = GWT.create(SimpleItemViewUiBinder.class);

	private SimpleItemPresenter presenter;
	private SubstringSuggestOracle oracle;
	private HandlerRegistration modificationButtonHandler;
	private HandlerRegistration mouseOutHandler;
	private HandlerRegistration mouseMoveHandler;
	private HandlerRegistration mouseOverHandler;
	private boolean deleteEventsAttached = false;
	private boolean isMultiline = false;

	private boolean hasErrors = false;
	private boolean hasFocus = false;

	@UiField
	HTMLPanel alertIcon;

	@UiField
	HTMLPanel itemErrors;

	@UiField
	HTML itemDocumentation;

	@UiField
	InlineLabel label;

	@UiField
	HTMLPanel outerWrapper;

	@UiField
	HTMLPanel innerInputWrapper;

	@UiField
	HTMLPanel innerTextAreaWrapper;

	@UiField(provided=true)
	SuggestBox textBox;

	@UiField
	TextArea textArea;

	@UiField
	HorizontalPanel horizontalPanel;

	@UiField
	FocusPanel simpleItem;

	@UiField(provided=true)
	ModificationButtonPresenter modificationButton;

	@UiField
	HTMLPanel attributes;

	@UiField
	HTMLPanel buttonArrow;

	@UiField
	InlineLabel infoIcon;

	/**
	 * Creates a new instance of the view.
	 *
	 * @param an instance of the modification button presenter
	 */
	@Inject
	public SimpleItemView(ModificationButtonPresenter modButton) {
		modificationButton = modButton;
		oracle = new SubstringSuggestOracle();
		ScrollingSuggestionDisplay suggestionDisplay = new ScrollingSuggestionDisplay();
		textBox = new SuggestBox(oracle, new TextBox(), suggestionDisplay);
		textBox.setLimit(1000);
		textBox.setAutoSelectEnabled(false);

		initWidget(uiBinder.createAndBindUi(this));

		oracle.setSuggestionProvider(new SuggestionProvider() {
			@Override
			public Collection<String> getSuggestions() {
				if (presenter == null) {
					return Collections.emptyList();
				} else {
					return presenter.getSuggestions();
				}
			}
		});

		suggestionDisplay.setPositionRelativeTo(outerWrapper);
		horizontalPanel.setCellWidth(alertIcon, "18px");
		horizontalPanel.setCellWidth(label, "217px");
	}

	private void addTextAreaHandlers() {
		textArea.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				handleFocusEvent();
			}
		});

		textArea.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				handleBlurEvent();
			}
		});

		textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				presenter.saveValue(event.getValue());
			}
		});
	}

	private void addTextBoxHandlers() {
		textBox.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				handleFocusEvent();
			}
		});

		textBox.getValueBox().addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				handleBlurEvent();
			}
		});

		textBox.getValueBox().addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				presenter.saveValue(event.getValue());
			}
		});

		textBox.getValueBox().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				presenter.onValueChange(textBox.getValue());
			}
		});

		textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				 if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					 textBox.setFocus(false);
				 }
			}
		});

		textBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				presenter.saveValue(event.getSelectedItem().getDisplayString());
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
	public String getValue() {
		if (isMultiline) {
			return textArea.getValue();
		} else {
			return textBox.getValue();
		}
	}

	@Override
	public void setValue(String value) {
		if (isMultiline) {
			textArea.setValue(value);
		} else {
			textBox.setValue(value, true);
		}
	}

	@Override
	public void setEnumeration(boolean flag) {
		if (!isMultiline && flag) {
			outerWrapper.addStyleName("hasSuggestions");
			textBox.getElement().setAttribute("placeholder", PLACEHOLDER_TEXT);

			buttonArrow.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					oracle.setIgnoreQuery(true);
					textBox.showSuggestionList();
				}
			}, ClickEvent.getType());
		}
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		// If the field is empty and there is only a single possible value,
		// fill in the field and update the model.
		if ((textBox.getValue() == null || textBox.getValue().length() == 0) && defaultValue != null) {
			setValue(defaultValue);
			presenter.saveValue(defaultValue);
		}
	}

	@Override
	public void setState(boolean complete) {
		if (complete) {
			alertIcon.addStyleName(HIDDEN_STYLE);
			itemErrors.addStyleName(HIDDEN_STYLE);
		} else {
			alertIcon.removeStyleName(HIDDEN_STYLE);
			itemErrors.removeStyleName(HIDDEN_STYLE);
		}
	}

	@Override
	public void addAttribute(IsWidget attrPresenter) {
		attributes.add(attrPresenter.asWidget());
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
			mouseMoveHandler = simpleItem.addMouseMoveHandler(new MouseMoveHandler() {

				@Override
				public void onMouseMove(MouseMoveEvent event) {
					event.preventDefault();
					presenter.handleMouseEvent(true);
				}
			});

			mouseOverHandler = simpleItem.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					event.preventDefault();
					presenter.handleMouseEvent(true);

				}
			});

			mouseOutHandler = simpleItem.addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					event.preventDefault();
					presenter.handleMouseEvent(false);
				}
			});

			modificationButtonHandler = modificationButton.addEventHandler(new ModificationEvent.Handler() {

				@Override
				public void onEvent(ModificationType eventType) {
					switch (eventType) {
					case CUT:
						presenter.onCut();
						break;
					case COPY:
						presenter.onCopy();
						break;
					default: //DELETE
						presenter.onDelete();
						break;
					}
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
	public boolean isDeleteEventsAttached() {
		return deleteEventsAttached;
	}

	@Override
	public void removeDeleteEventListeners() {
		if (deleteEventsAttached) {
			mouseOverHandler.removeHandler();
			mouseOutHandler.removeHandler();
			mouseMoveHandler.removeHandler();
			modificationButtonHandler.removeHandler();
			deleteEventsAttached = false;
			enableModification(false);
		}
	}

	@Override
	public void setErrors(String[] messages) {
		itemErrors.getElement().setInnerHTML("");
		hasErrors = (messages.length > 0);

		if (!hasErrors) {
			outerWrapper.removeStyleName("error");
		} else {
			Document d = Document.get();
			UListElement list = d.createULElement();

			for (String msg : messages) {
				LIElement li = d.createLIElement();
				li.setInnerText(msg);
				list.appendChild(li);
			}

			itemErrors.getElement().appendChild(list);
			outerWrapper.addStyleName("error");
		}
	}

	@UiHandler("simpleItem")
	public void onClick(ClickEvent event) {
		event.stopPropagation();
		if (!hasFocus) {
			if (isMultiline) {
				textArea.setFocus(true);
			} else {
				textBox.getValueBox().setFocus(true);
			}
		}
	}

	@Override
	public void setMultiline(boolean multiline) {
		this.isMultiline = multiline;

		if (isMultiline) {
			// Show a text area
			addTextAreaHandlers();
			outerWrapper.addStyleName("textArea");
			innerTextAreaWrapper.setVisible(true);
		} else {
			// Show a text box
			addTextBoxHandlers();
			innerInputWrapper.setVisible(true);
		}
	}

	private void handleBlurEvent() {
		outerWrapper.removeStyleName(FOCUS_STYLE);
		hasFocus = false;
	}

	private void handleFocusEvent() {
		outerWrapper.addStyleName(FOCUS_STYLE);
		hasFocus = true;
	}

	@Override
	public void setDocumentation(String documentation) {
		itemDocumentation.setHTML(SafeHtmlUtils.fromTrustedString(documentation));
		infoIcon.removeStyleName(HIDDEN_STYLE);
	}

	@UiHandler("infoIcon")
	void onInfoIconClick(ClickEvent event) {
		presenter.onRequestDocumentation();
	}

	@Override
	public void showDocumentation(boolean show) {
		if (show) {
			infoIcon.addStyleName(OPEN_STYLE);
			itemDocumentation.removeStyleName(HIDDEN_STYLE);
		} else {
			infoIcon.removeStyleName(OPEN_STYLE);
			itemDocumentation.addStyleName(HIDDEN_STYLE);
		}
	}

}
