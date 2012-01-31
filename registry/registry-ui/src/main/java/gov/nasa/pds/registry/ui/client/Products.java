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
import gov.nasa.pds.registry.ui.shared.ViewAssociation;
import gov.nasa.pds.registry.ui.shared.ViewProduct;
import gov.nasa.pds.registry.ui.shared.ViewSlot;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.google.gwt.user.client.Window;
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

/**
 * A tab to browse the registered products. 
 * 
 * @author jagander, hyunlee
 * 
 */
@SuppressWarnings("nls")
public class Products extends Tab {

	public static TabInfo init() {
		return new TabInfo("Products", "") {
			public Tab createInstance() {
				return new Products();
			}
			
			public String getColor() {
				return "#003366";
			}
			
			public String getName() {
				return "Products";
			}
		};
	}

	/**
	 * A mapping to <code>this</code> so that anonymous subclasses, handlers,
	 * may get access to the containing class.
	 */
	private Products instance = null;

	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public Products get() {
		return instance;
	}
	
	/*
	 * The main layout panel. All view components should be children of this.
	 */
	private VerticalPanel panel = new VerticalPanel();  // main panel
	
	private FlexTable layout = new FlexTable();

	private VerticalPanel scrollPanel = new VerticalPanel();

	/**
	 * The dialog box that displays product details.
	 */
	protected final DialogBox productDetailsBox = new DialogBox();

	protected final DialogBox associationDetailsBox = new DialogBox();

	protected RowSelectionHandler associationClickHander;

	protected ScrollTable associationScrollTable;

	protected List<ViewAssociation> tempAssociations;

	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final VerticalPanel dialogVPanel = new VerticalPanel();

	protected final VerticalPanel associationVPanel = new VerticalPanel();

	protected final HTML associationPlaceholder = new HTML(
			"Loading associations...");

	/**
	 * The close button used by the dialogBox to close the dialog.
	 */
	protected Button closeButton;

	protected Button associationCloseButton;

	protected HTML recordCountContainer = new HTML("");
	
	private int recordCount;

	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewProduct> cachedTableModel = null;

	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewProduct> pagingScrollTable = null;

	/**
	 * The {@link ProductTableModel}.
	 */
	private ProductTableModel tableModel = null;

	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewProduct> tableDefinition = null;

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
	public CachedTableModel<ViewProduct> getCachedTableModel() {
		return this.cachedTableModel;
	}

	/**
	 * @return the {@link PagingScrollTable}
	 */
	public PagingScrollTable<ViewProduct> getPagingScrollTable() {
		return this.pagingScrollTable;
	}

	/**
	 * @return the table definition of columns
	 */
	public DefaultTableDefinition<ViewProduct> getTableDefinition() {
		return this.tableDefinition;
	}

	/**
	 * @return the table model
	 */
	public ProductTableModel getTableModel() {
		return this.tableModel;
	}

	protected FlexTable getLayout() {
		return this.layout;
	}

	protected List<ViewAssociation> getTempAssociations() {
		return this.tempAssociations;
	}

	protected void setTempAssociations(List<ViewAssociation> tempAssociations) {
		this.tempAssociations = tempAssociations;
	}
	
	private void setRecordCount(int count) {
		this.recordCount = count;
	}
	
	private int getRecordCount() {
		return this.recordCount;
	}

	public Products() {
		// assign instance for exterior retrieval
		instance = this;	
		panel.setWidth("100%");
		initWidget(panel);	
		
		// Initialize and add the main layout to the page
		this.initLayout();

		// Initialize product detail popup
		this.initProductDetailPopup();

		// Initialize association detail popup
		this.initAssociationDetailPopup();

		// initialize filter and search options
		this.initFilterOptions();

		// initialize scroll table
		this.initTable();

		this.initAssociationTable();

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

		// add layout to root panel
		panel.add(this.layout);

		// add enough rows for all of the required widgets
		// filter options
		this.layout.insertRow(0);
		// scroll table wrapper
		this.layout.insertRow(0);
		// paging options
		this.layout.insertRow(0);

		// add the scroll panel
		this.layout.setWidget(1, 0, this.scrollPanel);

		// add title to scroll table
		this.scrollPanel.add(new HTML(
				"<div class=\"title\">Product Registry</div>"));

		// add record count container
		this.layout.setWidget(3, 0, this.recordCountContainer);		
		
		this.scrollPanel.addStyleName("scrollTableWrapper");
	}

	/**
	 * Initialize the popup that will display product details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initProductDetailPopup() {
		// set title
		this.productDetailsBox.setText("Product Details");
		//this.productDetailsBox.setWidth("100%");

		// make hide and show be animated
		//this.productDetailsBox.setAnimationEnabled(true);

		// add label for associations
		this.dialogVPanel
				.add(new HTML(
						"<div class=\"title\" style=\"margin-top: 10px;\">Associations</div>"));

		// TODO: be nice to add association box here but needs return values
		// during initalization, another way around?
		this.dialogVPanel.add(this.associationPlaceholder);

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

				// replace associations with placeholder for next load
				get().dialogVPanel.remove(2);
				get().dialogVPanel.insert(get().associationPlaceholder, 2);

				// remove data from popup
				get().dialogVPanel.remove(0);

				// remove data from table
				get().associationScrollTable.getDataTable().clear();
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
	 * Initialize the popup that will display association details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initAssociationDetailPopup() {
		// set title
		this.associationDetailsBox.setText("Association Details");

		// make hide and show be animated
		this.associationDetailsBox.setAnimationEnabled(true);

		// create a close button
		this.associationCloseButton = new Button("Close");

		// add the close button to the panel
		this.associationVPanel.add(this.associationCloseButton);

		// create a handler for the click of the close button to hide the detail
		// box
		ClickHandler closButtonHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// hide the detail box
				get().associationDetailsBox.hide();
			}
		};

		// create a handler for the detail box hide that de-selects the row that
		// was selected when the dialog box was shown. This is separate from the
		// close handler as there is a click-outside test that also triggers a
		// hide of the dialog box
		CloseHandler<PopupPanel> dialogCloseHandler = new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				get().associationScrollTable.getDataTable().deselectAllRows();

				// remove data from popup
				get().associationVPanel.remove(0);
			}
		};

		// add the handler to the button
		this.associationCloseButton.addClickHandler(closButtonHandler);

		// add a vertical panel as master layout panel to the dialog box
		// contents
		this.associationDetailsBox.setWidget(this.associationVPanel);

		// set a style name associated with the dialog so that it can be styled
		// through external css
		this.associationDetailsBox.addStyleName("detailBox");

		// add the close handler that de-selects the row that was used to
		// trigger the "show" of the dialog
		this.associationDetailsBox.addCloseHandler(dialogCloseHandler);
	}

	/**
	 * Initialize the filter and search options for the table data.
	 */
	public void initFilterOptions() {
		// create filter and search elements
		HorizontalPanel inputTable = new HorizontalPanel();
		//inputTable.setWidth("100%");
		// set alignment to bottom so that button is positioned correctly
		inputTable.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		inputTable.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// add input table to layout
		this.layout.setWidget(0, 0, inputTable);

		// create textual input for global unique identifier search
		final TextBox guidInput = new TextBox();
		guidInput.setName("guid");
		InputContainer guidInputWrap = new InputContainer("GUID", guidInput);
		inputTable.add(guidInputWrap);

		// create textual input for local id search
		final TextBox lidInput = new TextBox();
		lidInput.setName("lid");
		InputContainer lidInputWrap = new InputContainer("LID", lidInput);
		inputTable.add(lidInputWrap);

		// create textual input for name search
		final TextBox nameInput = new TextBox();
		nameInput.setName("name");
		InputContainer nameInputWrap = new InputContainer("Name", nameInput);
		inputTable.add(nameInputWrap);

		// create dropdown input for object type
		// TODO: list should be exhaustive, get from enum or build process?
		final ListBox objectsInput = new ListBox(false);
		objectsInput.setName("objectType");
		objectsInput.addItem("Any Object Type", "-1");
		objectsInput.addItem("Collection_Browse");
		objectsInput.addItem("Collection_Calibration");
		objectsInput.addItem("Collection_Context");
		objectsInput.addItem("Collection_Data");
		objectsInput.addItem("Collection_Document");
		objectsInput.addItem("Collection_Generic");
		objectsInput.addItem("Collection_Geometry");
		objectsInput.addItem("Collection_Miscellaneous");
		objectsInput.addItem("Collection_SPICE");
		objectsInput.addItem("Collection_Volume_PDS3");
		objectsInput.addItem("Collection_Volume_Set_PDS3");
		objectsInput.addItem("Collection_XML_Schema");
		objectsInput.addItem("Local_DD");
		objectsInput.addItem("Product");
		objectsInput.addItem("Product_Archive_Bundle");
		objectsInput.addItem("Product_Array_2D_Image");
		objectsInput.addItem("Product_Array_3D_Image");
		objectsInput.addItem("Product_Array_3D_Spectrum");
		objectsInput.addItem("Product_Attribute_Definition");
		objectsInput.addItem("Product_Browse");
		objectsInput.addItem("Product_Bundle");
		//objectsInput.addItem("Product_Collection");
		//objectsInput.addItem("Product_Collection_Data");		
		objectsInput.addItem("Product_Data_Set_PDS3");
		//objectsInput.addItem("Product_Delimited_Table");
		//objectsInput.addItem("Product_Delimited_Table_Grouped");
		objectsInput.addItem("Product_Delivery_Manifest");
		objectsInput.addItem("Product_Document");
		objectsInput.addItem("Product_File_Repository");
		objectsInput.addItem("Product_File_Text");
		objectsInput.addItem("Product_Instrument");
		objectsInput.addItem("Product_Instrument_PDS3");
		objectsInput.addItem("Product_Instrument_Host");
		objectsInput.addItem("Product_Instrument_Host_PDS3");
		objectsInput.addItem("Product_Investigation");
		objectsInput.addItem("Product_Mission");
		objectsInput.addItem("Product_Mission_PDS3");
		objectsInput.addItem("Product_Node");
		objectsInput.addItem("Product_Non_Specific");
		objectsInput.addItem("Product_PDS_Affiliate");
		objectsInput.addItem("Product_PDS_Guest");
		objectsInput.addItem("Product_Proxy_PDS3");
		objectsInput.addItem("Product_Resource");
		objectsInput.addItem("Product_SPICE_Kernel_Binary");
		objectsInput.addItem("Product_SPICE_Kernel_Text");
		objectsInput.addItem("Product_Service");
		objectsInput.addItem("Product_Software");
		objectsInput.addItem("Product_Stream_Delimited");
		objectsInput.addItem("Product_Table_Binary");
		objectsInput.addItem("Product_Table_Binary_Grouped");
		objectsInput.addItem("Product_Table_Character");
		objectsInput.addItem("Product_Table_Character_Grouped");
		objectsInput.addItem("Product_Target");
		objectsInput.addItem("Product_Target_PDS3");
		objectsInput.addItem("Product_Thumbnail");
		objectsInput.addItem("Product_Update");
		objectsInput.addItem("Product_XML_Schema");
		objectsInput.addItem("Product_Zipped");
		InputContainer objectsInputWrap = new InputContainer("Object Type",
				objectsInput);
		inputTable.add(objectsInputWrap);

		// create dropdown input for status type
		final ListBox statusInput = new ListBox(false);
		// TODO: comes from enum ObjectStatus so iterate over values? or is
		// order more important?
		statusInput.setName("statusType");
		statusInput.addItem("Any Status", "-1");
		statusInput.addItem("Submitted");
		statusInput.addItem("Approved");
		statusInput.addItem("Deprecated");
		InputContainer statusInputWrap = new InputContainer("Status",
				statusInput);
		inputTable.add(statusInputWrap);

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
				ProductTableModel tablemodel = get().getTableModel();

				// clear old filters
				tablemodel.clearFilters();

				// get values from input
				// guid
				String guid = guidInput.getValue();
				if (!guid.equals("")) {
					tablemodel.addFilter("guid", guid);
				}

				// lid
				String lid = lidInput.getValue();
				if (!lid.equals("")) {
					tablemodel.addFilter("lid", lid);
				}

				// name
				String name = nameInput.getValue();
				if (!name.equals("")) {
					tablemodel.addFilter("name", name);
				}

				// object type
				String objectType = objectsInput.getValue(objectsInput
						.getSelectedIndex());
				// set object type filter if set to something specific
				if (!objectType.equals("-1")) {
					tablemodel.addFilter("objectType", objectType);
				}

				// status
				String status = statusInput.getValue(statusInput
						.getSelectedIndex());
				// set status if set to something specific
				if (!status.equals("-1")) {
					tablemodel.addFilter("status", status);
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
				guidInput.setText("");
				lidInput.setText("");
				nameInput.setText("");
				objectsInput.setSelectedIndex(0);
				statusInput.setSelectedIndex(0);
				
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
		this.tableModel = new ProductTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewProduct>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(RegistryUI.PAGE_SIZE);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(RegistryUI.PAGE_SIZE);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewProduct> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewProduct>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(RegistryUI.PAGE_SIZE);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"<h2>There is no data to display</h2>"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewProduct> bulkRenderer = new FixedWidthGridBulkRenderer<ViewProduct>(
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
				ViewProduct product = get().getPagingScrollTable().getRowValue(
						rowIndex);
	
				// create grid for data, there are 7 fixed fields and fields for
				// each slot
				Grid detailTable = new Grid(7 + product.getSlots().size(), 2);

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

				// slots
				List<ViewSlot> slots = product.getSlots();
				for (int i = 0; i < slots.size(); i++) {
					ViewSlot slot = slots.get(i);
					int curRow = i + 7;
					
					detailTable.setText(curRow, 0, getNice(slot.getName(),
							true, true));
					
					// check for long string....need to display differently (another popup???)
					String valuesString = toSeparatedString(slot.getValues());
					if (valuesString.length()>200) 
						detailTable.setText(curRow, 1, valuesString.substring(0, 200) + "  .....");
					else
						detailTable.setText(curRow, 1, valuesString);					
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

				// do async on getting associations
				get().getTableModel()
						.getAssociations(
								product.getGuid(),
								new AsyncCallback<SerializableResponse<ViewAssociation>>() {

									public void onFailure(Throwable caught) {
										System.out.println("RPC Failure");
										Window.alert("Products.getAssociations() RPC Failure" + caught.getMessage());
									}

									public void onSuccess(
											SerializableResponse<ViewAssociation> result) {
										System.out.println("success called on getting associations");
										// get results
										SerializableProductResponse<ViewAssociation> spr = (SerializableProductResponse<ViewAssociation>) result;

										// cast values to ViewProducts to get
										// extended data
										List<ViewAssociation> associations = (List<ViewAssociation>) spr
												.getValues();
										get().setTempAssociations(associations);

										// TODO: factor this out
										// TODO: make a loading element
										// TODO: deal with situation where more
										// than the requested amount are found
										FixedWidthGrid grid = get().associationScrollTable
												.getDataTable();
										grid.resize(associations.size(), 3);

										// add elements to the table
										for (int i = 0; i < associations.size(); i++) {
											final ViewAssociation association = associations
													.get(i);
											grid.setText(i, 0, association.getSourceGuid());
											grid.setText(i, 1, association.getAssociationType());
											grid.setText(i, 2, association.getTargetGuid());
										}

										// remove placeholder
										get().dialogVPanel.remove(2);

										// add table
										get().dialogVPanel.insert(get().associationScrollTable,2);
									}
								});
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
	}

	private void initAssociationTable() {
		// Get the components of the ScrollTable
		FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
		FlexCellFormatter formatter = headerTable.getFlexCellFormatter();

		headerTable.setHTML(0, 0, "Source Object");
		formatter.setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 1, "Association Type");
		formatter.setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 2, "Target Object");
		formatter.setHorizontalAlignment(0, 2,
				HasHorizontalAlignment.ALIGN_CENTER);

		FixedWidthGrid dataTable = new FixedWidthGrid();

		// Set some options in the data table
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);

		// Combine the components into a ScrollTable
		this.associationScrollTable = new ScrollTable(dataTable, headerTable);

		// set display properties
		this.associationScrollTable.setHeight("150px");
		this.associationScrollTable.setWidth("100%");
		this.associationScrollTable.setCellPadding(0);
		this.associationScrollTable.setCellSpacing(0);

		// Set some options in the scroll table
		this.associationScrollTable
				.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);

		this.associationScrollTable.getDataTable().addRowSelectionHandler(
				new RowSelectionHandler() {
					@Override
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
						ViewAssociation association = get()
								.getTempAssociations().get(rowIndex);

						// create grid for data
						Grid detailTable = new Grid(16, 2);

						// set each field label and the values
						int row = 0;

						// name
						detailTable.setText(row, 0, "Association Type");
						detailTable.setText(row, 1, association
								.getAssociationType());
						row++;

						detailTable.setText(row, 0, "Description");
						detailTable.setText(row, 1, association
								.getDescription());
						row++;

						detailTable.setText(row, 0, "Logical ID");
						detailTable.setText(row, 1, association.getLid());
						row++;

						detailTable.setText(row, 0, "Name");
						detailTable.setText(row, 1, association.getName());
						row++;

						detailTable.setText(row, 0, "Object Type");
						detailTable
								.setText(row, 1, association.getObjectType());
						row++;

						detailTable.setText(row, 0, "Source GUID");
						detailTable
								.setText(row, 1, association.getSourceGuid());
						row++;

						detailTable.setText(row, 0, "Status");
						detailTable.setText(row, 1, association.getStatus());
						row++;

						detailTable.setText(row, 0, "Target GUID");
						detailTable
								.setText(row, 1, association.getTargetGuid());
						row++;

						detailTable.setText(row, 0, "Version Name");
						detailTable.setText(row, 1, association.getVersionName());
						row++;

						// add styles to cells
						for (int i = 0; i < detailTable.getRowCount(); i++) {
							detailTable.getCellFormatter().setStyleName(i, 0,
									"detailLabel");
							detailTable.getCellFormatter().setStyleName(i, 1,
									"detailValue");
						}

						// add new data as first element in vertical stack,
						// which should
						// be the close button since last data table was removed
						get().associationVPanel.insert(detailTable, 0);

						// display, center and focus popup
						get().associationDetailsBox.center();
						get().associationCloseButton.setFocus(true);
					}
				});
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
		//this.cachedTableModel.clearCache();
		this.pagingScrollTable.gotoFirstPage();
		setRecordCount(get().pagingScrollTable.getTableModel().getRowCount());
	}

	/**
	 * @return the {@link TableDefinition} with all ColumnDefinitions defined.
	 */
	private TableDefinition<ViewProduct> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewProduct>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewProduct>(rowColors));
		// Name, LID, userVersion, ObjectType, Status
		// product name
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Name") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(150);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// LID
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"LID") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(150);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// User Version
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Version ID") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getVersionName();
				}

			};
			columnDef.setMinimumColumnWidth(35);
			columnDef.setPreferredColumnWidth(35);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// OBJECT TYPE
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Object Type") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getObjectType();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(80);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
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

	/**
	 * Get a display version of an underscore separated string. This is useful
	 * for the slot display names.
	 * 
	 * Note that this is copied from the pds-utils project, StrUtils. It was
	 * copied wholesale due to dependency issues in that class and the normal
	 * functioning of GWT client classes.
	 */
	public static String getNice(final String string, final Boolean capitalize,
			final Boolean replaceUnderscore) {
		String out = string.trim();
		if (replaceUnderscore == null || replaceUnderscore) {
			out = out.replaceAll("_", " ");
		}
		if (capitalize == null || capitalize) {
			out = toUpper(out);
		}
		return out;
	}

	/**
	 * Uppercase each word of a string.
	 * 
	 * Note that this is copied from the pds-utils project, StrUtils. It was
	 * copied wholesale due to dependency issues in that class and the normal
	 * functioning of GWT client classes.
	 */
	public static String toUpper(final String string) {
		String out = "";
		final String source = string.toLowerCase();
		String[] parts = source.split(" ");
		for (int i = 0; i < parts.length; i++) {
			final String part = parts[i];
			out += part.substring(0, 1).toUpperCase() + part.substring(1);
			if (i < parts.length - 1) {
				out += " ";
			}
		}
		return out;
	}

	/**
	 * Convert a list to a comma separated string.
	 * 
	 * Note that this is copied from the pds-utils project, StrUtils. It was
	 * copied wholesale due to dependency issues in that class and the normal
	 * functioning of GWT client classes.
	 */
	public static String toSeparatedString(final List<?> list) {
		String returnString = "";

		Iterator<?> it = list.iterator();
		while (it.hasNext()) {
			Object element = it.next();
			String val = null;
			val = element.toString();
			returnString += val;
			if (it.hasNext()) {
				returnString += ", ";
			}
		}
		return returnString;
	}
	
	public void onShow() {	
	}
}
