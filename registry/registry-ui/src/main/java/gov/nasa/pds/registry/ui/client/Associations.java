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
import gov.nasa.pds.registry.ui.shared.ViewProducts;
import gov.nasa.pds.registry.ui.shared.ViewSlot;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;

import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.DefaultRowRenderer;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.gen2.table.client.PagingOptions;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.TableDefinition;
import com.google.gwt.gen2.table.client.AbstractScrollTable.SortPolicy;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;

import com.google.gwt.gen2.table.event.client.PageCountChangeEvent;
import com.google.gwt.gen2.table.event.client.PageCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowCountChangeEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;

import java.util.List;
import java.util.Set;

/**
 * A tab to browse the registered associations. 
 * 
 * @author hyunlee
 * 
 */
public class Associations extends Tab {
	
	public static TabInfo init() {
		return new TabInfo("Associations", "") {
			public Tab createInstance() {
				return new Associations();
			}
			
			public String getColor() {
				return "#123261";
			}
			
			public String getName() {
				return "Associations";
			}
		};
	}
	
	/**
	 * Default number of records to fetch 
	 */
	public final int FETCH_ROW_SIZE = 100;
	
	private VerticalPanel panel = new VerticalPanel();	
	private VerticalPanel scrollPanel = new VerticalPanel();
	private VerticalPanel associationVPanel = new VerticalPanel();
	protected final VerticalPanel productVPanel = new VerticalPanel();
	
	private FlexTable layout = new FlexTable();
	
	protected ScrollTable srcScrollTable, targScrollTable;

	protected ViewProducts srcProducts, targetProducts;
	
	protected HTML recordCountContainer = new HTML("");
	protected final HTML srcPlaceholder = new HTML("Loading a source product...");
	protected final HTML targetPlaceholder = new HTML("Loading a target product...");
	
	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final DialogBox associationDetailsBox = new DialogBox();
	protected final DialogBox productDetailsBox = new DialogBox();
	protected Button associationCloseButton;
	protected Button productCloseButton;
	
	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewAssociation> cachedTableModel = null;

	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewAssociation> pagingScrollTable = null;
	
	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewAssociation> tableDefinition = null;

	/**
	 * The {@link ProductTableModel}.
	 */
	private AssociationTableModel tableModel = null;
	
	private Associations instance = null;
	
	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public Associations get() {
		return instance;
	}
	
	/**
	 * @return the table model
	 */
	public AssociationTableModel getTableModel() {
		return this.tableModel;
	}
	
	/**
	 * @return the data table. This is the underlying grid for displaying data,
	 *         not just a data model.
	 */
	public FixedWidthGrid getDataTable() {
		return getPagingScrollTable().getDataTable();
	}
	
	/**
	 * @return the {@link PagingScrollTable}
	 */
	public PagingScrollTable<ViewAssociation> getPagingScrollTable() {
		return this.pagingScrollTable;
	}
	
	public Associations() {
		panel.clear();
		instance = this;		
		panel.setSpacing(10);
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

		this.initAssociationTable();
		
		this.initProductTables();

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
				"<div class=\"title\">Association Registry</div>"));

		// add record count container
		this.layout.setWidget(3, 0, this.recordCountContainer);
	}

	private void initProductTables() {
		// Get the components of the ScrollTable
		FixedWidthFlexTable headerTable1 = new FixedWidthFlexTable();
		headerTable1.setHTML(0, 0, "Name");
		headerTable1.setHTML(0, 1, "LID");
		headerTable1.setHTML(0, 2, "Version ID");
		headerTable1.setHTML(0, 3, "Object Type");
		headerTable1.setHTML(0, 4, "Status");
		
		FixedWidthFlexTable headerTable2 = new FixedWidthFlexTable();
		headerTable2.setHTML(0, 0, "Name");
		headerTable2.setHTML(0, 1, "LID");
		headerTable2.setHTML(0, 2, "Version ID");
		headerTable2.setHTML(0, 3, "Object Type");
		headerTable2.setHTML(0, 4, "Status");
				
		FixedWidthGrid srcDataTable = new FixedWidthGrid();
		FixedWidthGrid targDataTable = new FixedWidthGrid();

		// Set some options in the data table
		srcDataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		targDataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);

		// Combine the components into a ScrollTable
		this.srcScrollTable = new ScrollTable(srcDataTable, headerTable1);
		this.targScrollTable = new ScrollTable(targDataTable, headerTable2);

		// set display properties
		this.srcScrollTable.setHeight("85px");
		this.srcScrollTable.setWidth("100%");
		this.srcScrollTable.setCellPadding(0);
		this.srcScrollTable.setCellSpacing(0);
		
		this.targScrollTable.setHeight("85px");
		this.targScrollTable.setWidth("100%");
		this.targScrollTable.setCellPadding(0);
		this.targScrollTable.setCellSpacing(0);

		// Set some options in the scroll table
		this.srcScrollTable.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
		this.targScrollTable.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);

		// for SOURCE OBJECT
		this.srcScrollTable.getDataTable().addRowSelectionHandler(
				new RowSelectionHandler() {

					@Override
					public void onRowSelection(RowSelectionEvent event) {
						// get selected rows
						Set<Row> selected = event.getSelectedRows();
						if (selected.size() == 0) {
							// TODO: out is for debugging purposes, fix for
							// erroneously fired events is pending
							System.out
									.println("Row selection event fired but no selected rows found.");
							return;
						}

						// since only one row can be selected, just get the
						// first one
						int rowIndex = selected.iterator().next().getRowIndex();

						// get the association instance associated with that row
						ViewProduct aProduct = srcProducts.get(rowIndex);

						// create grid for data, there are 8 fixed fields and
						// fields for each slot
						Grid detailTable = new Grid(8 + aProduct.getSlots().size(), 2);

						// set each field label and the values
						// name
		                detailTable.setText(0, 0, "Name");
		                detailTable.setText(0, 1, aProduct.getName());

		                // type
		                detailTable.setText(1, 0, "Object Type");
		                detailTable.setText(1, 1, aProduct.getObjectType());

		                // status
		                detailTable.setText(2, 0, "Status");
		                detailTable.setText(2, 1, aProduct.getStatus());

		                // version
		                detailTable.setText(3, 0, "Version Name");
		                detailTable.setText(3, 1, aProduct.getVersionName());

		                // guid
		                detailTable.setText(4, 0, "GUID");
		                detailTable.setText(4, 1, aProduct.getGuid());

		                // lid
		                detailTable.setText(5, 0, "LID");
		                detailTable.setText(5, 1, aProduct.getLid());

		                // home
		                detailTable.setText(6, 0, "Home");
		                detailTable.setText(6, 1, aProduct.getHome());

		                // slots
		                List<ViewSlot> slots = aProduct.getSlots();
		                for (int i = 0; i < slots.size(); i++) {
		                    ViewSlot slot = slots.get(i);
		                    int curRow = i + 7;

		                    detailTable.setText(curRow, 0, Products.getNice(slot.getName(),
		                            true, true));

		                    // check for long string....need to display differently (another popup???)
		                    String valuesString = Products.toSeparatedString(slot.getValues());
		                    if (valuesString.length()>200)
		                        detailTable.setText(curRow, 1, valuesString.substring(0, 200) + "  ...");
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

						// add new data as first element in vertical stack,
						// which should be the close button since last data table was removed
						get().productVPanel.insert(detailTable, 0);

						// display, center and focus popup
						get().productDetailsBox.center();
						get().productCloseButton.setFocus(true);

					}

				});
		
		// for TARGET OBJECT
		this.targScrollTable.getDataTable().addRowSelectionHandler(
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

						// since only one row can be selected, just get the
						// first one
						int rowIndex = selected.iterator().next().getRowIndex();

						// get the association instance associated with that row
						ViewProduct aProduct = targetProducts.get(rowIndex);

						// create grid for data, there are 7 fixed fields and
						// fields for each slot
						Grid detailTable = new Grid(7 + aProduct.getSlots().size(), 2);

						// set each field label and the values
						// name
		                detailTable.setText(0, 0, "Name");
		                detailTable.setText(0, 1, aProduct.getName());

		                // type
		                detailTable.setText(1, 0, "Object Type");
		                detailTable.setText(1, 1, aProduct.getObjectType());

		                // status
		                detailTable.setText(2, 0, "Status");
		                detailTable.setText(2, 1, aProduct.getStatus());

		                // version
		                detailTable.setText(3, 0, "Version Name");
		                detailTable.setText(3, 1, aProduct.getVersionName());

		                // guid
		                detailTable.setText(4, 0, "GUID");
		                detailTable.setText(4, 1, aProduct.getGuid());

		                // lid
		                detailTable.setText(5, 0, "LID");
		                detailTable.setText(5, 1, aProduct.getLid());

		                // home
		                detailTable.setText(6, 0, "Home");
		                detailTable.setText(6, 1, aProduct.getHome());

		                // slots
		                List<ViewSlot> slots = aProduct.getSlots();
		                for (int i = 0; i < slots.size(); i++) {
		                    ViewSlot slot = slots.get(i);
		                    int curRow = i + 7;

		                    detailTable.setText(curRow, 0, Products.getNice(slot.getName(),
		                            true, true));

		                    // check for long string....need to display differently (another popup???)
		                    String valuesString = Products.toSeparatedString(slot.getValues());
		                    if (valuesString.length()>200)
		                        detailTable.setText(curRow, 1, valuesString.substring(0, 200) + "  ...");
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

						// add new data as first element in vertical stack,
						// which should be the close button since last data table was removed
						get().productVPanel.insert(detailTable, 0);

						// display, center and focus popup
						get().productDetailsBox.center();
						get().productCloseButton.setFocus(true);
					}
				});
	}
	
	protected void initAssociationTable() {
		// create table model that knows to make rest call for rows and caches
		// filter settings for calls
		this.tableModel = new AssociationTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewAssociation>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(RegistryUI.PAGE_SIZE);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(RegistryUI.PAGE_SIZE);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewAssociation> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewAssociation>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(RegistryUI.PAGE_SIZE);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"<h2>There is no data to display</h2>"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewAssociation> bulkRenderer = new FixedWidthGridBulkRenderer<ViewAssociation>(
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

				// get the association instance associated with that row
				ViewAssociation association = get().getPagingScrollTable().getRowValue(
						rowIndex);
				// NOTE: if need to look up object specifically, use lid. This
				// will be necessary if the get products call gets less data,
				// reducing the response size and the cost of generating row
				// data.

				// create grid for data, there are 8 fixed fields and fields for
				// each slot
				Grid detailTable = new Grid(16, 2);

				// set each field label and the values
				int row = 0;
                detailTable.setText(row, 0, "Association Type");
                detailTable.setText(row, 1, association.getAssociationType());
                row++;

                detailTable.setText(row, 0, "Description");
                detailTable.setText(row, 1, association.getDescription());
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
                
                List<ViewSlot> slots = association.getSlots();
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
                }

				// add styles to cells
				for (int i = 0; i < detailTable.getRowCount(); i++) {
					detailTable.getCellFormatter().setStyleName(i, 0,
							"detailLabel");
					detailTable.getCellFormatter().setStyleName(i, 1,
							"detailValue");
				}

				get().associationVPanel.insert(detailTable, 0);

				// display, center and focus popup
				get().associationDetailsBox.center();
				get().associationCloseButton.setFocus(true);

				get().getProducts(association.getSourceGuid(), true);
				get().getProducts(association.getTargetGuid(), false);
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
				}

				);

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
	 * Initialize the popup that will display product details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initProductDetailPopup() {
		// set title
		this.productDetailsBox.setText("Product Details");

		// make hide and show be animated
		this.productDetailsBox.setAnimationEnabled(true);

		// create a close button
		this.productCloseButton = new Button("Close");

		// add the close button to the panel
		this.productVPanel.add(this.productCloseButton);

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
				get().srcScrollTable.getDataTable().deselectAllRows();
				
				// remove data from popup
				get().productVPanel.remove(0);
			}
		};

		// add the handler to the button
		this.productCloseButton.addClickHandler(closButtonHandler);

		// add a vertical panel as master layout panel to the dialog box
		// contents
		this.productDetailsBox.setWidget(this.productVPanel);

		// set a style name associated with the dialog so that it can be styled
		// through external css
		this.productDetailsBox.addStyleName("detailBox");

		// enable hiding the dialog when clicking outside of its constraints
		//this.productDetailsBox.setAutoHideEnabled(true);

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
		//this.associationDetailsBox.set

		// make hide and show be animated
		this.associationDetailsBox.setAnimationEnabled(true);
		
		this.associationVPanel.setWidth("700px");
		
		// row 1 of associationVPanel
		this.associationVPanel
			.add(new HTML("<div class=\"title\" style=\"margin-top: 10px;\">Source Product</div>"));
		
		// row 2
		this.associationVPanel.add(this.srcPlaceholder);
		
		// row 3
		this.associationVPanel
			.add(new HTML("<div class=\"title\" style=\"margin-top: 10px;\">Target Product</div>"));
		
		// row 4
		this.associationVPanel.add(this.targetPlaceholder);

		// create a close button
		this.associationCloseButton = new Button("Close");

		// add the close button to the panel (row 5)
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
				get().getPagingScrollTable().getDataTable().deselectAllRows();
				
				// remove data from popup
				get().associationVPanel.remove(2);
				get().associationVPanel.insert(get().srcPlaceholder, 2);
				
				get().associationVPanel.remove(4);
				get().associationVPanel.insert(get().targetPlaceholder, 4);
				
				get().associationVPanel.remove(0);
				get().srcScrollTable.getDataTable().clear();
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
		// set alignment to bottom so that button is positioned correctly
		inputTable.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		inputTable.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// add input table to layout
		this.layout.setWidget(0, 0, inputTable);

		// create textual input for global unique identifier search of source object
		final TextBox srcGuidInput = new TextBox();
		srcGuidInput.setName("sourceObject");
		InputContainer srcGuidInputWrap = new InputContainer("Source GUID", srcGuidInput);
		inputTable.add(srcGuidInputWrap);

		// create textual input for global unique identifier search of target object
		final TextBox targetGuidInput = new TextBox();
		targetGuidInput.setName("targetObject");
		InputContainer targetGuidInputWrap = new InputContainer("Target GUID", targetGuidInput);
		inputTable.add(targetGuidInputWrap);

		// create dropdown input for association type
		// TODO: list should be exhaustive, get from enum or build process?
		final ListBox assocTypeInput = new ListBox(false);
		assocTypeInput.setName("associationType");
		assocTypeInput.addItem("Any Association Type", "-1");
		assocTypeInput.addItem("has_target");
		assocTypeInput.addItem("has_investigation");
		assocTypeInput.addItem("has_data_set");
		assocTypeInput.addItem("has_instrument_host");
		assocTypeInput.addItem("has_instrument");
		assocTypeInput.addItem("has_resource");
		assocTypeInput.addItem("urn:registry:AssociationType:HasMember");
				
		InputContainer assocTypeInputWrap = new InputContainer("Association Type",
				assocTypeInput);
		inputTable.add(assocTypeInputWrap);

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
				get().cachedTableModel.clearCache();

				// get table model that holds filters
				AssociationTableModel tablemodel = get().tableModel;

				// clear old filters
				tablemodel.clearFilters();

				// get values from input
				// source guid
				String srcGuid = srcGuidInput.getValue();
				if (!srcGuid.equals("")) {
					tablemodel.addFilter("sourceObject", srcGuid);
				}

				// target guid
				String targetGuid = targetGuidInput.getValue();
				if (!targetGuid.equals("")) {
					tablemodel.addFilter("targetObject", targetGuid);
				}

				// association type
				String associationType = assocTypeInput.getValue(assocTypeInput
						.getSelectedIndex());
				// set association type filter if set to something specific
				if (!associationType.equals("-1")) {
					tablemodel.addFilter("associationType", associationType);
				}

				// HACK: row count of zero causes cache check to fail and table
				// not be updated
				get().tableModel.setRowCount(FETCH_ROW_SIZE);

				// go back to first page and force update
				get().getPagingScrollTable().gotoPage(0, true);
			}
		});
		
		clearButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				srcGuidInput.setText("");
				targetGuidInput.setText("");
				assocTypeInput.setSelectedIndex(0);
				
				get().getTableModel().clearFilters();
				//get().getTableModel().setRowCount(RegistryUI.FETCH_ROW_SIZE);

				// go back to first page and force update
				get().getPagingScrollTable().gotoPage(0, true);
			}
		});
	}

	/**
	 * @return the {@link TableDefinition} with all ColumnDefinitions defined.
	 */
	private TableDefinition<ViewAssociation> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewAssociation>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewAssociation>(rowColors));
		//Source GUID, Source LID, Association Type, Version Name, Status, Target GUID, Target LID

		// Source GUID
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Source GUID") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getSourceGuid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// Source LID
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Source LID") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getSourceLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// Association Type
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Association Type") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getAssociationType();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// Version Name 
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Version Name") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getVersionName();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getStatus();
				}

			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// Target GUID
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Target GUID") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getTargetGuid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// Target LID
		{
			AssociationColumnDefinition<String> columnDef = new AssociationColumnDefinition<String>(
					"Target LID") {
				@Override
				public String getCellValue(ViewAssociation rowValue) {
					return rowValue.getTargetLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		return this.tableDefinition;
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
	}
	
	protected void getProducts(String guid, final boolean srcFlag) {
		get().tableModel.getProduct(guid, 
				new AsyncCallback<SerializableResponse<ViewProduct>>() {
					public void onFailure(Throwable caught) {
						System.out.println("RPC Failure for getProduct with guid...");
						Window.alert("Associations.getProducts() RPC Failure" + caught.getMessage());
					}
				
					public void onSuccess(SerializableResponse<ViewProduct> result) {
						SerializableProductResponse<ViewProduct> spr = (SerializableProductResponse<ViewProduct>) result;
					
						ViewProducts products = (ViewProducts) spr.getValues();
						if (srcFlag) {
							get().srcProducts = products;
												
							FixedWidthGrid grid = get().srcScrollTable.getDataTable();
							grid.resize(products.size(), 5);
						
							for (int i=0; i<products.size(); i++) {
								final ViewProduct aProduct = products.get(i);
								grid.setText(i, 0, aProduct.getName());
								grid.setText(i, 1, aProduct.getLid());
								grid.setText(i, 2, aProduct.getVersionName());
								grid.setText(i, 3, aProduct.getObjectType());
								grid.setText(i, 4, aProduct.getStatus());
							}
						
							get().associationVPanel.remove(2);
							get().associationVPanel.insert(get().srcScrollTable, 2);
						}
						else {
							get().targetProducts = products;
							
							FixedWidthGrid grid = get().targScrollTable.getDataTable();
							grid.resize(products.size(), 5);
						
							for (int i=0; i<products.size(); i++) {
								final ViewProduct aProduct = products.get(i);
								grid.setText(i, 0, aProduct.getName());
								grid.setText(i, 1, aProduct.getLid());
								grid.setText(i, 2, aProduct.getVersionName());
								grid.setText(i, 3, aProduct.getObjectType());
								grid.setText(i, 4, aProduct.getStatus());
							}
						
							get().associationVPanel.remove(4);
							get().associationVPanel.insert(get().targScrollTable, 4);
						}
					}
				});
	}
	
	public void onShow() {	
	}
}