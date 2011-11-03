// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations 
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
// is subject to U.S. export control laws and regulations, the recipient has 
// the responsibility to obtain export licenses or other export authority as 
// may be required before exporting such information to foreign countries or 
// providing access to foreign nationals.
//
// $Id$ 
package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.client.Tab.TabInfo;
import gov.nasa.pds.registry.ui.shared.InputContainer;
import gov.nasa.pds.registry.ui.shared.ViewAuditableEvent;
import gov.nasa.pds.registry.ui.shared.ViewSlot;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import java.text.SimpleDateFormat;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable;
import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.DefaultRowRenderer;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.PagingOptions;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.TableDefinition;
import com.google.gwt.gen2.table.client.AbstractScrollTable.SortPolicy;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.gen2.table.event.client.PageCountChangeEvent;
import com.google.gwt.gen2.table.event.client.PageCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowCountChangeEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;
import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * A tab to browse the registered events. 
 * 
 * @author hyunlee
 * 
 */
public class Events extends Tab {
	
	public static TabInfo init() {
		return new TabInfo("Events", "") {
			public Tab createInstance() {
				return new Events();
			}
			
			public String getColor() {
				return "#19488D";
			}
			
			public String getName() {
				return "Events";
			}
		};
	}
	
	/**
	 * A mapping to <code>this</code> so that anonymous subclasses, handlers,
	 * may get access to the containing class.
	 */
	private Events instance = null;

	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public Events get() {
		return instance;
	}

	/**
	 * The main layout panel. All view components should be children of this.
	 */
	
	private VerticalPanel panel = new VerticalPanel();  // main panel
	private VerticalPanel scrollPanel = new VerticalPanel();
	private FlexTable layout = new FlexTable();

	//private final SimpleDateFormat dateFormat = new SimpleDateFormat(
    //	"yyyy-MM-dd'T'HH:mm:ss");
	private final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	/**
	 * The dialog box that displays product details.
	 */
	protected final DialogBox productDetailsBox = new DialogBox();

	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final VerticalPanel dialogVPanel = new VerticalPanel();

	protected final HTML loadingPlaceholder = new HTML(
			"Loading events...");

	/**
	 * The close button used by the dialogBox to close the dialog.
	 */
	protected Button closeButton;

	protected HTML recordCountContainer = new HTML("");
	
	private int recordCount;

	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewAuditableEvent> cachedTableModel = null;

	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewAuditableEvent> pagingScrollTable = null;

	/**
	 * The {@link EventTableModel}.
	 */
	private EventTableModel tableModel = null;

	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewAuditableEvent> tableDefinition = null;

	/**
	 * @return the data table. This is the underlying grid for displaying data,
	 *         not just a data model.
	 */
	public FixedWidthGrid getDataTable() {
		return getScrollTable().getDataTable();
	}

	/**
	 * @return the footer table. In this case, it is a repeat of the header row
	 *         but is not sortable.
	 */
	public FixedWidthFlexTable getFooterTable() {
		return getScrollTable().getFooterTable();
	}

	/**
	 * @return the header table. This is the header row of the data table. It is
	 *         separate from the data table itself so that it does not scroll
	 *         with the data. It also supports the table sorting.
	 */
	public FixedWidthFlexTable getHeaderTable() {
		return getScrollTable().getHeaderTable();
	}

	/**
	 * @return the scroll table.
	 */
	// TODO: unclear why scrollTable is needed separately from pagingScrollTable
	// but is based on example and will be investigated for necessity
	public AbstractScrollTable getScrollTable() {
		return this.pagingScrollTable;
	}

	/**
	 * @return the cached table model
	 */
	public CachedTableModel<ViewAuditableEvent> getCachedTableModel() {
		return this.cachedTableModel;
	}

	/**
	 * @return the {@link PagingScrollTable}
	 */
	public PagingScrollTable<ViewAuditableEvent> getPagingScrollTable() {
		return this.pagingScrollTable;
	}

	/**
	 * @return the table definition of columns
	 */
	public DefaultTableDefinition<ViewAuditableEvent> getTableDefinition() {
		return this.tableDefinition;
	}

	/**
	 * @return the table model
	 */
	public EventTableModel getTableModel() {
		return this.tableModel;
	}

	protected FlexTable getLayout() {
		return this.layout;
	}
	
	private void setRecordCount(int count) {
		this.recordCount = count;
	}
	
	private int getRecordCount() {
		return this.recordCount;
	}
	
	public Events() {
		// assign instance for exterior retrieval
		instance = this;	
		panel.setSpacing(10);
		panel.setWidth("100%");
		initWidget(panel);	
		// Initialize and add the main layout to the page
		this.initLayout();

		// Initialize product detail popup
		this.initProductDetailPopup();

		// initialize filter and search options
		this.initFilterOptions();

		// initialize scroll table
		this.initTable();

		// initialize paging widget
		this.initPaging();

		// HACK: A bug in PagingScrollTable causes the paging widget to not
		// update when new data from an RPC call comes in. This forces the
		// paging options to update with the correct num pages by triggering a
		// recount of the pages based on the row count.
		this.tableModel.addRowCountChangeHandler(new RowCountChangeHandler() {

			@Override
			public void onRowCountChange(RowCountChangeEvent event) {
				get().getPagingScrollTable().setPageSize(RegistryUI.PAGE_SIZE);
			}
		});
		onModuleLoaded();
	}

	/**
	 * Initialize the master layout including inserting expected rows and title.
	 */
	private void initLayout() {
		// set basic formatting options
		this.layout.setWidth("100%");
		this.layout.setCellPadding(0);
		this.layout.setCellSpacing(0);

		// add layout to main panel
		panel.add(this.layout);

		// add enough rows for all of the required widgets
		// filter options
		this.layout.insertRow(0);
		// scroll table wrapper
		this.layout.insertRow(0);
		// paging options
		this.layout.insertRow(0);

		// add style name to scroll table container
		this.layout.getCellFormatter().addStyleName(1, 0, "scrollTableWrapper");

		// add the scroll panel
		this.layout.setWidget(1, 0, this.scrollPanel);

		// add title to scroll table
		this.scrollPanel.add(new HTML(
				"<div class=\"title\">Event Registry</div>"));

		// add record count container
		this.layout.setWidget(3, 0, this.recordCountContainer);
	}

	/**
	 * Initialize the popup that will display event details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initProductDetailPopup() {
		// set title
		this.productDetailsBox.setText("Event Details");

		// make hide and show be animated
		this.productDetailsBox.setAnimationEnabled(true);

		// create a close button
		this.closeButton = new Button("Close");

		// add the close button to the panel
		this.dialogVPanel.add(this.closeButton);

		// create a handler for the click of the close button to hide the detail
		// box
		ClickHandler closButtonHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// hide the detail box
				get().productDetailsBox.hide();
			}
		};

		// create a handler for the detail box hide that de-selects the row that
		// was selected when the dialog box was shown. This is separate from the
		// close handler as there is a click-outside test that also triggers a
		// hide of the dialog box
		CloseHandler<PopupPanel> dialogCloseHandler = new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// clear selection and allow row to be clicked again
				get().getPagingScrollTable().getDataTable().deselectAllRows();

				// remove data from popup
				get().dialogVPanel.remove(0);
			}
		};

		// add the handler to the button
		this.closeButton.addClickHandler(closButtonHandler);

		// add a vertical panel as master layout panel to the dialog box
		// contents
		this.productDetailsBox.setWidget(this.dialogVPanel);

		// set a style name associated with the dialog so that it can be styled
		// through external css
		this.productDetailsBox.addStyleName("detailBox");
		
		// add the close handler that de-selects the row that was used to
		// trigger the "show" of the dialog
		this.productDetailsBox.addCloseHandler(dialogCloseHandler);
	}
	
	/**
	 * Initialize the filter and search options for the table data.
	 */
	public void initFilterOptions() {
		// create filter and search elements
		HorizontalPanel inputTable = new HorizontalPanel();
		// set alignment to bottom so that button is positioned correctly
		inputTable.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		inputTable.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// add input table to layout
		this.layout.setWidget(0, 0, inputTable);

		// create textual input for event start search
		final TextBox evtStartInput = new TextBox();
		evtStartInput.setName("eventStart");
		InputContainer evtStartInputWrap = new InputContainer("Event Start", evtStartInput);
		inputTable.add(evtStartInputWrap);

		// create textual input for event end search
		final TextBox evtEndInput = new TextBox();
		evtEndInput.setName("eventEnd");
		InputContainer evtEndInputWrap = new InputContainer("Event End", evtEndInput);
		inputTable.add(evtEndInputWrap);

		// create dropdown input for requestId
		final TextBox requestIdInput = new TextBox();
		requestIdInput.setWidth("250px");
		requestIdInput.setName("requestId");
		InputContainer requestIdInputWrap = new InputContainer("Request ID",
				requestIdInput);
		inputTable.add(requestIdInputWrap);
		
		// create dropdown input for event type search
		//Created, Approved, Deleted, Updated, Deprecated, Versioned, Undeprecated, Replicated
		final ListBox evtTypeInput = new ListBox(false);
		// TODO: comes from enum ObjectStatus so iterate over values? or is
		// order more important?
		evtTypeInput.setName("eventType");
		evtTypeInput.addItem("Any Event Type", "-1");
		evtTypeInput.addItem("Created");
		evtTypeInput.addItem("Approved");
		evtTypeInput.addItem("Deleted");
		evtTypeInput.addItem("Updated");
		evtTypeInput.addItem("Deprecated");
		evtTypeInput.addItem("Versioned");
		evtTypeInput.addItem("Undeprecated");
		evtTypeInput.addItem("Replicated");
		InputContainer evtTypeInputWrap = new InputContainer("Event Type",
				evtTypeInput);
		inputTable.add(evtTypeInputWrap);

		// create button for doing an update
		final Button updateButton = new Button("Update");
		updateButton.setWidth("55px");
		updateButton.setStyleName("buttonwrapper");
		InputContainer updateButtonWrap = new InputContainer(null, updateButton);
		inputTable.add(updateButtonWrap);

		final Button clearButton = new Button("Clear");
		clearButton.setWidth("50px");
		clearButton.setStyleName("buttonwrapper");
		InputContainer clearButtonWrap = new InputContainer(null, clearButton);
		inputTable.add(clearButtonWrap);
		
		// add handler to leverage the update button
		// TODO: determine if there is a form widget that's better to use here,
		// not sure this is reasonable as we're not doing a real submit, it's
		// triggering a javascript event that is integral to the behavior of the
		// scroll table
		updateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// clear cache as data will be invalid after filter
				get().getCachedTableModel().clearCache();

				// get table model that holds filters
				EventTableModel tablemodel = get().getTableModel();

				// clear old filters
				tablemodel.clearFilters();

				// get values from input
				// eventStart
				String evtStart = evtStartInput.getValue();
				if (!evtStart.equals("")) {
					tablemodel.addFilter("eventStart", evtStart);
				}

				// eventEnd
				String evtEnd = evtEndInput.getValue();
				if (!evtEnd.equals("")) {
					tablemodel.addFilter("eventEnd", evtEnd);
				}
				
				String evtType = evtTypeInput.getValue(evtTypeInput
						.getSelectedIndex());
				// set eventType if set to something specific
				if (!evtType.equals("-1")) {
					tablemodel.addFilter("eventType", evtType);
				}

				// requestId
				String requestId = requestIdInput.getValue();
				if (!requestId.equals("")) {
					tablemodel.addFilter("requestId", requestId);
				}

				// HACK: row count of zero causes cache check to fail and table
				// not be updated
				get().getTableModel().setRowCount(RegistryUI.FETCH_ROW_SIZE);

				// go back to first page and force update
				get().getPagingScrollTable().gotoPage(0, true);
			}
		});
		
		clearButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				evtStartInput.setText("");
				evtEndInput.setText("");
				evtTypeInput.setSelectedIndex(0);
				requestIdInput.setText("");
				
				get().getTableModel().clearFilters();
				get().getTableModel().setRowCount(getRecordCount());

				// go back to first page and force update
				get().getPagingScrollTable().gotoPage(0, true);
			}
		});
	}

	/**
	 * Initialize scroll table and behaviors.
	 */
	protected void initTable() {
		// create table model that knows to make rest call for rows and caches
		// filter settings for calls
		this.tableModel = new EventTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewAuditableEvent>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(RegistryUI.PAGE_SIZE);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(RegistryUI.PAGE_SIZE);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewAuditableEvent> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewAuditableEvent>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(RegistryUI.PAGE_SIZE);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"<h2>There is no data to display</h2>"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewAuditableEvent> bulkRenderer = new FixedWidthGridBulkRenderer<ViewAuditableEvent>(
				this.pagingScrollTable.getDataTable(), this.pagingScrollTable);
		this.pagingScrollTable.setBulkRenderer(bulkRenderer);

		// Setup the formatting
		this.pagingScrollTable.setCellPadding(0);
		this.pagingScrollTable.setCellSpacing(0);
		this.pagingScrollTable
				.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
		this.pagingScrollTable.setHeight(RegistryUI.TABLE_HEIGHT);

		// allow multiple cell resizing
		this.pagingScrollTable
				.setColumnResizePolicy(ScrollTable.ColumnResizePolicy.MULTI_CELL);

		// only allow a single column sort
		// TODO: implement multi-column sort? appears supported on the REST side
		this.pagingScrollTable.setSortPolicy(SortPolicy.SINGLE_CELL);

		// create row handler to fill in and display popup
		RowSelectionHandler handler = new RowSelectionHandler() {
			public void onRowSelection(RowSelectionEvent event) {
				// get selected rows
				Set<Row> selected = event.getSelectedRows();
				if (selected.size() == 0) {
					// TODO: out is for debugging purposes, fix for
					// erroneously fired events is pending
					System.out.println("Row selection event fired but no selected rows found.");
					return;
				}

				// since only one row can be selected, just get the first one
				int rowIndex = selected.iterator().next().getRowIndex();

				// get the product instance associated with that row
				ViewAuditableEvent product = get().getPagingScrollTable().getRowValue(
						rowIndex);
				
				// create grid for data, there are 7 fixed fields and fields for
				// each slot
				Grid detailTable = new Grid(7 + product.getSlots().size() + 8, 2);

				// set each field label and the values
				// name
				detailTable.setText(0, 0, "Name");
				detailTable.setText(0, 1, product.getName());

				// type
				detailTable.setText(1, 0, "Object Type");
				detailTable.setText(1, 1, product.getObjectType());

				// status
				detailTable.setText(2, 0, "Status");
				detailTable.setText(2, 1, product.getStatus());

				// version
				detailTable.setText(3, 0, "Version Name");
				detailTable.setText(3, 1, product.getVersionName());

				// guid
				detailTable.setText(4, 0, "GUID");
				detailTable.setText(4, 1, product.getGuid());

				// lid
				detailTable.setText(5, 0, "LID");
				detailTable.setText(5, 1, product.getLid());

				// home
				detailTable.setText(6, 0, "Home");
				detailTable.setText(6, 1, product.getHome());

				int row = 7;
				// slots
				List<ViewSlot> slots = product.getSlots();
				for (int i = 0; i < slots.size(); i++) {
					ViewSlot slot = slots.get(i);
					int curRow = i + row;
					
					detailTable.setText(curRow, 0, Products.getNice(slot.getName(),
							true, true));
					
					// check for long string....need to display differently (another popup???)
					String valuesString = Products.toSeparatedString(slot.getValues());
					if (valuesString.length()>200) 
						detailTable.setText(curRow, 1, valuesString.substring(0, 200) + "  ...");
					else
						detailTable.setText(curRow, 1, valuesString);		
					row = curRow;
				}
				
				row += 2;
				detailTable.setText(row, 0, "Event Type");
				detailTable.setText(row++, 1, product.getEventType());
					
				detailTable.setText(row, 0, "Request ID");
				detailTable.setText(row++, 1, product.getRequestId());
					
				detailTable.setText(row, 0, "User");
				detailTable.setText(row++, 1, product.getUser());
				
				detailTable.setText(row, 0, "Timestamp");
				detailTable.setText(row++, 1, dateFormat.format(product.getTimestamp()));
				//detailTable.setText(row++, 1, product.getTimestamp().toString());
				
				List<String> affectedObjs = product.getAffectedObjects();
				for (String affectedObj: affectedObjs) {
					detailTable.setText(row, 0, "Affected Object");
					detailTable.setText(row++, 1, affectedObj);
				}
				
				// add styles to cells
				for (int i = 0; i < detailTable.getRowCount(); i++) {
					detailTable.getCellFormatter().setStyleName(i, 0,
							"detailLabel");
					detailTable.getCellFormatter().setStyleName(i, 1,
							"detailValue");
				}
				get().dialogVPanel.insert(detailTable, 0);

				// display, center and focus popup
				get().productDetailsBox.center();
				get().closeButton.setFocus(true);
			}
		};

		// add detail popup handler to rows
		this.pagingScrollTable.getDataTable().addRowSelectionHandler(handler);

		// add handler to update found element count
		this.pagingScrollTable
				.addPageCountChangeHandler(new PageCountChangeHandler() {

					@Override
					public void onPageCountChange(PageCountChangeEvent event) {
						int count = get().pagingScrollTable.getTableModel()
								.getRowCount();

						// display count in page
						// TODO: do number formatting and message formatting and
						// externalize
						get().recordCountContainer
								.setHTML("<div class=\"recordCount\">Num Records: "
										+ count + "</div>");
					}
				});

		// set selection policy
		SelectionGrid grid = get().getDataTable();
		grid.setSelectionPolicy(SelectionPolicy.ONE_ROW);

		// Add the scroll table to the layout
		this.scrollPanel.add(this.pagingScrollTable);
		this.scrollPanel.setWidth("100%");

		// add a formatter
		final FlexCellFormatter formatter = this.layout.getFlexCellFormatter();
		formatter.setWidth(1, 0, "100%");
		formatter.setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
	}
	
	/**
	 * Initialize the paging widget.
	 */
	public void initPaging() {
		PagingOptions pagingOptions = new PagingOptions(getPagingScrollTable());

		// add the paging widget to the last row of the layout
		this.layout.setWidget(2, 0, pagingOptions);
	}

	/**
	 * Action to take once the module has loaded.
	 */
	protected void onModuleLoaded() {
		// set page to first page, triggering call for data
		this.pagingScrollTable.gotoFirstPage();
		setRecordCount(get().pagingScrollTable.getTableModel().getRowCount());
	}

	/**
	 * @return the {@link TableDefinition} with all ColumnDefinitions defined.
	 */
	private TableDefinition<ViewAuditableEvent> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewAuditableEvent>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewAuditableEvent>(rowColors));
		
		// request id
		{
			EventColumnDefinition<String> columnDef = new EventColumnDefinition<String>(
					"Request ID") {
				@Override
				public String getCellValue(ViewAuditableEvent rowValue) {
					return rowValue.getRequestId();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(150);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// Timestamp
		{
			EventColumnDefinition<String> columnDef = new EventColumnDefinition<String>(
					"Timestamp") {
				@Override
				public String getCellValue(ViewAuditableEvent rowValue) {
					return dateFormat.format(rowValue.getTimestamp());
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// event type
		{
			EventColumnDefinition<String> columnDef = new EventColumnDefinition<String>(
					"Event Type") {
				@Override
				public String getCellValue(ViewAuditableEvent rowValue) {
					return rowValue.getEventType();
				}

			};
			columnDef.setMinimumColumnWidth(35);
			columnDef.setPreferredColumnWidth(35);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// User
		{
			EventColumnDefinition<String> columnDef = new EventColumnDefinition<String>(
					"User") {
				@Override
				public String getCellValue(ViewAuditableEvent rowValue) {
					return rowValue.getUser();
				}

			};
			columnDef.setMinimumColumnWidth(30);
			columnDef.setPreferredColumnWidth(30);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// OBJECT TYPE
		{
			EventColumnDefinition<String> columnDef = new EventColumnDefinition<String>(
					"Object Type") {
				@Override
				public String getCellValue(ViewAuditableEvent rowValue) {
					return rowValue.getObjectType();
				}
			};

			columnDef.setMinimumColumnWidth(40);
			columnDef.setPreferredColumnWidth(40);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			EventColumnDefinition<String> columnDef = new EventColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewAuditableEvent rowValue) {
					return rowValue.getStatus();
				}

			};

			columnDef.setMinimumColumnWidth(35);
			columnDef.setPreferredColumnWidth(35);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		return this.tableDefinition;
	}

	public void onShow() {
	}
}