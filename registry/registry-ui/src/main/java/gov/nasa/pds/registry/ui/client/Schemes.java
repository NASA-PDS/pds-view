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
import gov.nasa.pds.registry.ui.shared.ViewClassificationNode;
import gov.nasa.pds.registry.ui.shared.ViewClassificationNodes;
import gov.nasa.pds.registry.ui.shared.ViewScheme;
import gov.nasa.pds.registry.ui.shared.ViewSlot;
import gov.nasa.pds.registry.ui.shared.InputContainer;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;

import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.PagingOptions;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.TableDefinition;
import com.google.gwt.gen2.table.client.DefaultRowRenderer;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;

import com.google.gwt.gen2.table.event.client.RowCountChangeEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;
import com.google.gwt.gen2.table.event.client.PageCountChangeEvent;
import com.google.gwt.gen2.table.event.client.PageCountChangeHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable.SortPolicy;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;

import java.util.List;
import java.util.Set;

/**
 * A tab to browse the registered schemes. 
 * 
 * @author hyunlee
 * 
 */
public class Schemes extends Tab {
	
	public static TabInfo init() {
		return new TabInfo("Schemes", "") {
			public Tab createInstance() {
				return new Schemes();
			}
			
			public String getColor() {
				return "#1C509B";
			}
			
			public String getName() {
				return "Schemes";
			}
		};
	}
	
	private VerticalPanel panel = new VerticalPanel();	
	private VerticalPanel scrollPanel = new VerticalPanel();	
	private FlexTable layout = new FlexTable();
	
	/**
	 * The dialog box that displays product details.
	 */
	protected final DialogBox productDetailsBox = new DialogBox();

	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final VerticalPanel dialogVPanel = new VerticalPanel();

	protected final HTML servicePlaceholder = new HTML("Loading Schemes...");
	protected final HTML nodePlaceholder = new HTML("Loading classification nodes...");

	/**
	 * The close button used by the dialogBox to close the dialog.
	 */
	protected Button closeButton;
	
	/**
     * The {@link SchemeTableModel}.
     */
	private SchemeTableModel tableModel = null;
	
	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewScheme> tableDefinition = null;
	
	protected HTML recordCountContainer = new HTML("");
	
	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewScheme> pagingScrollTable = null;
	
	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewScheme> cachedTableModel = null;
	
	private Schemes instance = null;
	
	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public Schemes get() {
		return instance;
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
	public PagingScrollTable<ViewScheme> getPagingScrollTable() {
		return this.pagingScrollTable;
	}
	
	/**
	 * @return the table model
	 */
	public SchemeTableModel getTableModel() {
		return this.tableModel;
	}
	
	protected ViewClassificationNodes tempNodes;
	protected ScrollTable nodeScrollTable;
	
	protected void setTempClassificationNodes(ViewClassificationNodes tempNodes) {
		this.tempNodes = tempNodes;
	}
	
	public Schemes() {
		panel.clear();
		instance = this;	
		panel.setSpacing(8);
		panel.setWidth("100%");	
        initWidget(panel);     
        
        this.initLayout();
        
        // Initialize product detail popup
		this.initProductDetailPopup();
		
        this.initTable();
        
        this.initNodeTable();
        
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
                "<div class=\"title\">Schemes Registry</div>"));

        // add record count container
        this.layout.setWidget(3, 0, this.recordCountContainer);
    }
    
    /**
	 * Initialize scroll table and behaviors.
	 */
	protected void initTable() {
		// create table model that knows to make rest call for rows and caches
		// filter settings for calls
		this.tableModel = new SchemeTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewScheme>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(RegistryUI.PAGE_SIZE);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(RegistryUI.PAGE_SIZE);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewScheme> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewScheme>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(RegistryUI.PAGE_SIZE);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"<h2>There is no data to display</h2>"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewScheme> bulkRenderer = new FixedWidthGridBulkRenderer<ViewScheme>(
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
				ViewScheme product = get().getPagingScrollTable().getRowValue(
						rowIndex);

				// create grid for data, there are 7 fixed fields and fields for
				// each slot
				Grid detailTable = new Grid(7 + product.getSlots().size() + 4, 2);

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
				int row = 7;
				
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
				row++;
				
				detailTable.setText(row, 0, "Node Type");
				detailTable.setText(row++, 1, product.getNodeType());
				
				detailTable.setText(row, 0, "IsInternal");
				detailTable.setText(row++, 1, product.getIsInternal().toString());

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
				
				// do async on getting classification nodes
	/*
				get().getTableModel()
						.getClassificationNodes(
								product.getGuid(),
								new AsyncCallback<SerializableResponse<ViewClassificationNode>>() {

									public void onFailure(Throwable caught) {
										System.out.println("RPC Failure");
										Window.alert("Schemes.getClassificationNodes() RPC Failure" + caught.getMessage() + "<br>"+
												caught.toString());
									}

									public void onSuccess(
											SerializableResponse<ViewClassificationNode> result) {
										System.out.println("success called on getting classification nodes");
										// get results
										SerializableProductResponse<ViewClassificationNode> spr = 
												(SerializableProductResponse<ViewClassificationNode>) result;

										// cast values to ViewProducts to get
										// extended data
										ViewClassificationNodes nodes = (ViewClassificationNodes) spr
												.getValues();
										//get().setTempClassificationNodes(nodes);

										// TODO: factor this out
										// TODO: make a loading element
										// TODO: deal with situation where more
										// than the requested amount are found
										FixedWidthGrid grid = get().nodeScrollTable.getDataTable();
										grid.resize(nodes.size(), 7);

										//int i=0;
										// add elements to the table
										for (int i = 0; i < nodes.size(); i++) {
										//{
											
											final ViewClassificationNode aNode = nodes.get(i);
											grid.setText(i, 0, aNode.getParent());
											grid.setText(i, 1, aNode.getCode());
											grid.setText(i, 2, aNode.getPath());
											grid.setText(i, 3, aNode.getGuid());
											grid.setText(i, 4, aNode.getLid());
											grid.setText(i, 5, aNode.getVersionName());
											grid.setText(i, 6, aNode.getStatus());
											
											//grid.setText(i, 0, "test");
											//grid.setText(i, 1, "Test");
											//grid.setText(i, 2, "test");										
										}

										// remove placeholder
										get().dialogVPanel.remove(2);

										// add table
										get().dialogVPanel.insert(get().nodeScrollTable,2);
									}
								});
								*/
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
	
	private void initNodeTable() {
		// Get the components of the ScrollTable
		FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
		FlexCellFormatter formatter = headerTable.getFlexCellFormatter();

		headerTable.setHTML(0, 0, "Parent");
		formatter.setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 1, "Code");
		formatter.setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 2, "Path");
		formatter.setHorizontalAlignment(0, 2,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 3, "Guid");
		formatter.setHorizontalAlignment(0, 3,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 4, "Lid");
		formatter.setHorizontalAlignment(0, 4,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 5, "Version Name");
		formatter.setHorizontalAlignment(0, 5,
				HasHorizontalAlignment.ALIGN_CENTER);
		headerTable.setHTML(0, 6, "Status");
		formatter.setHorizontalAlignment(0, 6,
				HasHorizontalAlignment.ALIGN_CENTER);	

		FixedWidthGrid dataTable = new FixedWidthGrid();

		// Set some options in the data table
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);

		// Combine the components into a ScrollTable
		this.nodeScrollTable = new ScrollTable(dataTable, headerTable);

		// set display properties
		this.nodeScrollTable.setHeight("400px");
		this.nodeScrollTable.setWidth("100%");
		this.nodeScrollTable.setCellPadding(0);
		this.nodeScrollTable.setCellSpacing(0);

		// Set some options in the scroll table
		this.nodeScrollTable.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
	}
	
	/**
	 * Initialize the popup that will display product details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initProductDetailPopup() {
		// set title
		this.productDetailsBox.setText("Scheme Details");

		// make hide and show be animated
		this.productDetailsBox.setAnimationEnabled(true);
		
		// add label for associations
		//this.dialogVPanel
		//		.add(new HTML(
		//				"<div class=\"title\" style=\"margin-top: 10px;\">Classification Nodes</div>"));

		// TODO: be nice to add association box here but needs return values
		// during initalization, another way around?
		//this.dialogVPanel.add(this.nodePlaceholder);

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

				// replace nodes with placeholder for next load
				//get().dialogVPanel.remove(2);
				//get().dialogVPanel.insert(get().nodePlaceholder, 2);

				// remove data from popup
				get().dialogVPanel.remove(0);

				// remove data from table
				get().nodeScrollTable.getDataTable().clear();
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
	 * Initialize the paging widget.
	 */
	public void initPaging() {
		PagingOptions pagingOptions = new PagingOptions(getPagingScrollTable());

		// add the paging widget to the last row of the layout
		this.layout.setWidget(2, 0, pagingOptions);
	}
	
	/**
	 * @return the {@link TableDefinition} with all ColumnDefinitions defined.
	 */
	private TableDefinition<ViewScheme> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewScheme>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewScheme>(rowColors));

		// nodeType 
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"Node Type") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getNodeType();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		

		// isInternal
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"IsInternal") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getIsInternal().toString();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// LID
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"LID") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(150);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// HOME
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"Home") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getHome();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// System version name
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"Version Name") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getVersionName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// OBJECT TYPE
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"Object Type") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getObjectType();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			SchemeColumnDefinition<String> columnDef = new SchemeColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewScheme rowValue) {
					return rowValue.getStatus();
				}

			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		return this.tableDefinition;
	}

	/**
	 * Action to take once the module has loaded.
	 */
	protected void onModuleLoaded() {
		// set page to first page, triggering call for data
		this.pagingScrollTable.gotoFirstPage();
	}

	public void onShow() {
	}
}