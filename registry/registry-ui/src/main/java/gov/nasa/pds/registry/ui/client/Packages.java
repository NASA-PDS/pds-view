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
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackage;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackages;
import gov.nasa.pds.registry.ui.shared.ViewSlot;
import gov.nasa.pds.registry.ui.shared.InputContainer;
import gov.nasa.pds.registry.ui.shared.Constants;
import gov.nasa.pds.registry.ui.shared.StatusInformation;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.ListCellEditor;
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

import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.logging.client.DevelopmentModeLogHandler;
import com.google.gwt.logging.client.FirebugLogHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.logging.client.SystemLogHandler;

import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.Comparator;

/**
 * A tab to browse the registered packages. 
 * 
 * @author hyunlee
 * 
 */
public class Packages extends Tab {
	
	public static TabInfo init() {
		return new TabInfo("Packages", "") {
			public Tab createInstance() {
				return new Packages();
			}
			
			public String getColor() {
				return "#14386D";
			}
			
			public String getName() {
				return "Packages";
			}
		};
	}
	
	private VerticalPanel panel = new VerticalPanel();	
	private VerticalPanel scrollPanel = new VerticalPanel();
	private FlexTable layout = new FlexTable();
	private HorizontalPanel pagingPanel = new HorizontalPanel();

	/**
	 * The dialog box that displays product details.
	 */
	protected final DialogBox productDetailsBox = new DialogBox();

	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final VerticalPanel dialogVPanel = new VerticalPanel();

	protected final HTML loadingPlaceholder = new HTML("Loading packages...");

	/**
	 * The close button used by the dialogBox to close the dialog.
	 */
	protected Button closeButton;
	
	/**
	 * Components for filtering 
	 */
	private final TextBox guidInput = new TextBox();
	private final TextBox lidInput = new TextBox();
	private final TextBox nameInput = new TextBox();
	private final ListBox statusInput = new ListBox(false);
	private final Button updateButton = new Button("Update Status");
	private final Button clearButton = new Button("Clear");
    private final Button refreshButton = new Button("Refresh");
    private final Button deleteButton = new Button("Delete");

    //private int totalCount = 0;
	
	/**
     * The {@link PackageTableModel}.
     */
	private PackageTableModel tableModel = null;
	
	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewRegistryPackage> tableDefinition = null;
	
	protected HTML recordCountContainer = new HTML("");
		
	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewRegistryPackage> pagingScrollTable = null;
	
	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewRegistryPackage> cachedTableModel = null;
	
	private Packages instance = null;
	
	private Logger logger = Logger.getLogger("registry-ui");
	
	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public Packages get() {
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
	public PagingScrollTable<ViewRegistryPackage> getPagingScrollTable() {
		return this.pagingScrollTable;
	}
	
	/**
	 * @return the table model
	 */
	public PackageTableModel getTableModel() {
		this.tableModel.setServerUrl(RegistryUI.serverUrl);
		return this.tableModel;
	}
	
	/**
	 * @return the cached table model
	 */
	public CachedTableModel<ViewRegistryPackage> getCachedTableModel() {
		return this.cachedTableModel;
	}
	
	public Packages() {
		panel.clear();
		instance = this;	
		panel.setSpacing(8);
		panel.setWidth("100%");
		
        initWidget(panel);           
        this.initLayout();
        
        // Initialize product detail popup
		this.initProductDetailPopup();
		
		// initialize filter options (update and delete buttons)
		this.initFilterOptions();
				
        this.initTable();
        
        this.initPaging();
        
        // HACK: A bug in PagingScrollTable causes the paging widget to not
        // update when new data from an RPC call comes in. This forces the
        // paging options to update with the correct num pages by triggering a
        // recount of the pages based on the row count.
        get().getTableModel().addRowCountChangeHandler(new RowCountChangeHandler() {
            @Override
            public void onRowCountChange(RowCountChangeEvent event) {
                get().getPagingScrollTable().setPageSize(RegistryUI.PAGE_SIZE1);
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
                "<div class=\"title\">Package Registry</div>"));
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
		
		// create textual input for global unique identifier search
		guidInput.setName("guid");
		InputContainer guidInputWrap = new InputContainer("GUID", guidInput);
		inputTable.add(guidInputWrap);

		// create textual input for local id search
		lidInput.setName("lid");
		InputContainer lidInputWrap = new InputContainer("LID", lidInput);
		inputTable.add(lidInputWrap);

		// create textual input for name search
		nameInput.setName("name");
		InputContainer nameInputWrap = new InputContainer("Name", nameInput);
		inputTable.add(nameInputWrap);

		statusInput.setName("statusType");
		statusInput.addItem("Any Status", "-1");
		for (int i=0; i<Constants.status.length; i++) {
			statusInput.addItem(Constants.status[i]);
		}
		InputContainer statusInputWrap = new InputContainer("Status",
				statusInput);
		inputTable.add(statusInputWrap);
			
		// create button for doing refresh the table
        refreshButton.setStyleName("buttonwrapper");
        InputContainer refreshButtonWrap = new InputContainer(null, refreshButton);
        inputTable.add(refreshButtonWrap);
        
        //clearButton.setWidth("50px");
        clearButton.setStyleName("buttonwrapper");
        InputContainer clearButtonWrap = new InputContainer(null, clearButton);
        inputTable.add(clearButtonWrap);

        // create button for doing an update
        updateButton.setStyleName("buttonwrapper");
        InputContainer updateButtonWrap = new InputContainer(null, updateButton);
        inputTable.add(updateButtonWrap);

        deleteButton.setWidth("57px");
        deleteButton.setStyleName("buttonwrapper");
        InputContainer deleteButtonWrap = new InputContainer(null, deleteButton);
        inputTable.add(deleteButtonWrap);

        // add handler to leverage the refresh button
        refreshButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //get().getPagingScrollTable().getDataTable().deselectAllRows();
                //get().reloadData();
                get().refreshTable();
            }
        });
        
        clearButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				get().initializeInputs();				
				get().getTableModel().clearFilters();
				
				// go back to first page and force update
				get().getPagingScrollTable().gotoPage(0, true);
			}
		});
		
		updateButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Set<Integer> selectedRows = get().getDataTable().getSelectedRows();
				logger.log(Level.FINEST, "size of selected rows = " + selectedRows.size());
				
				if (statusInput.getSelectedIndex()==0 || selectedRows.size()==0) {
					Window.alert("Please choose the Status and/or select package(s) to update.");
				}
				else {
					for (Integer rowIndex: selectedRows) {
						ViewRegistryPackage aPackage = get().getPagingScrollTable().getRowValue(rowIndex);
						final String tmpLid = aPackage.getLid();
						aPackage.setStatus(statusInput.getItemText(statusInput.getSelectedIndex()));
						logger.log(Level.FINEST, "row = " + rowIndex + "   aPackage.getStatus = " + aPackage.getStatus());
						logger.log(Level.FINEST, "start time = " + (new Date()).toString());
						get().getTableModel().updatePackage(aPackage,
								new AsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								//System.out.println("RPC Failure");
								Window.alert("Packages.updatePackage() RPC Failure" + caught.getMessage());
							}

							@Override
							public void onSuccess(Boolean result) {
								logger.log(Level.FINEST, "result of updatePackage() = " + result);								
								get().reloadData();
								// should refresh product tab too????
								Products.get().reloadData();
								logger.log(Level.FINEST, "stop time = " + (new Date()).toString() + " for lid = " + tmpLid);
								Window.alert("The update request is completed for lid = " + tmpLid);
							}
						});
					}
					Window.alert("The update request is being processed by the Registry Service "+
							"and will take some time to complete based on the number of members in the package.");
				}
				statusInput.setSelectedIndex(0);
			}
		});
		
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Set<Integer> selectedRows = get().getDataTable().getSelectedRows();
				List<Integer> selectedLists = new java.util.ArrayList<Integer>(selectedRows);
				
				logger.log(Level.FINEST, "size of selected rows = " + selectedRows.size());
				logger.log(Level.FINEST, "selectedLists = " + selectedLists.toString());
				
				// call connectionmanager delete method
				if (selectedRows.size()==0) {
					Window.alert("Please select package(s) to delete.");
				}
				else {	
					boolean okToDelete = Window.confirm("Are you sure you want to delete the selected package(s)?");					
					logger.log(Level.FINEST, "ok to delete = " + okToDelete);
					
					if (okToDelete) {					
						// also add progress popup window while deleting
						for (int i=selectedLists.size()-1; i>=0;i--) {
							final int rowIndex = selectedLists.get(i);
							logger.log(Level.FINEST, "selected rowIndex = " + rowIndex);
							ViewRegistryPackage aPackage = get().getPagingScrollTable().getRowValue(rowIndex);
							logger.log(Level.FINEST, "row = " + rowIndex + "   aPackage.name = " + aPackage.getName());
							final String tmpLid = aPackage.getLid();
							get().getTableModel().deletePackage(aPackage,
									new AsyncCallback<Boolean>() {
								@Override
								public void onFailure(Throwable caught) {
									Window.alert("Package.deletePackage() RPC Failure" + caught.getMessage());
								}
		
								public void onSuccess(Boolean result) {
									logger.log(Level.FINEST, "result of deletePackage() = " + result);
									logger.log(Level.FINEST, "refreshing the package display.....");
									get().reloadData();
									// need to refresh product tabs???
									Products.get().reloadData();
									//Products.get().refreshTable();
									Window.alert("The delete request is completed for lid = " + tmpLid);
								}
							});
							get().getDataTable().removeRow(rowIndex);
						}
						Window.alert("The delete request is being processed by the Registry Service "+
								"and will take some time to complete based on the number of members in the package.");
					}
				}				
			}
		});	
	}

	protected void initializeInputs() {
		guidInput.setText("");
		lidInput.setText("");
		nameInput.setText("");
		statusInput.setSelectedIndex(0);
	}
	
	protected void reloadData() {
        get().getCachedTableModel().clearCache();	
		// to reload a page after returning zero results from a previous search call (PDS-133)
		get().getTableModel().setRowCount(RegistryUI.PAGE_SIZE1);
		get().getPagingScrollTable().redraw();
		
		RegistryUI.statusInfo.getStatus(RegistryUI.serverUrl, new AsyncCallback<StatusInformation>() {
        	@Override
			public void onFailure(Throwable caught) {
				Window.alert("Status.getStatus() RPC Failure" + caught.getMessage());
			}

			@Override
			public void onSuccess(StatusInformation result) {
				long count = result.getPackages();
				logger.log(Level.FINEST, "reloadData   num of packages " + count);		
				get().recordCountContainer.setHTML("<div class=\"recordCount\">Total Records: " + count + "</div>");
			}
		});
    }

    private void refreshTable() {
    	// clear cache as data will be invalid after filter
    	get().getCachedTableModel().clearCache();

    	// get table model that holds filters
    	PackageTableModel tablemodel = get().getTableModel();

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
    	
    	// status
    	String status = statusInput.getValue(statusInput
    			.getSelectedIndex());
    	logger.log(Level.FINEST, "status = " + status);
    	// set status if set to something specific
    	if (!status.equals("-1")) {
    		tablemodel.addFilter("status", status);
    	} 

        get().reloadData();
    }
	
    /**
	 * Initialize scroll table and behaviors.
	 */
	protected void initTable() {
		// create table model that knows to make rest call for rows and caches
		// filter settings for calls
		this.tableModel = new PackageTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewRegistryPackage>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(RegistryUI.PAGE_SIZE1);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(RegistryUI.PAGE_SIZE1);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewRegistryPackage> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewRegistryPackage>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(RegistryUI.PAGE_SIZE1);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"<h2>There is no data to display</h2>"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewRegistryPackage> bulkRenderer = new FixedWidthGridBulkRenderer<ViewRegistryPackage>(
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
		
		get().getDataTable().addTableListener(new TableListener() {
			public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
				System.out.println("row = " + row + "    cell = " + cell);
				logger.log(Level.FINEST, "row = " + row + "    cell = " + cell + 
						"     value = " + get().getPagingScrollTable().getRowValue(row).getStatus());
				get().getPackageDetail(row);
			}
		});
				
		// add handler to update found element count
		this.pagingScrollTable
				.addPageCountChangeHandler(new PageCountChangeHandler() {
					@Override
					public void onPageCountChange(PageCountChangeEvent event) {
						int count = get().getPagingScrollTable().getTableModel().getRowCount();

						// display count in page
						get().recordCountContainer
								.setHTML("<div class=\"recordCount\">Total Records: " + count + "</div>");
					}
				});

		// set selection policy
		SelectionGrid grid = get().getDataTable();
		grid.setSelectionPolicy(SelectionPolicy.CHECKBOX);

		// Add the scroll table to the layout
		this.scrollPanel.add(this.pagingScrollTable);
		this.scrollPanel.setWidth("100%");

		// add a formatter
		final FlexCellFormatter formatter = this.layout.getFlexCellFormatter();
		formatter.setWidth(1, 0, "100%");
		formatter.setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
	}
	
	private void getPackageDetail(int rowIndex) {	
		// get the product instance associated with that row
		ViewRegistryPackage product = get().getPagingScrollTable().getRowValue(
				rowIndex);

		// create grid for data, there are 7 fixed fields and fields for
		// each slot
		Grid detailTable = new Grid(7 + product.getSlots().size() + 2, 2);

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
		// Alphabetize the slot listing in the product details view (PDS-279)
		if (slots.size() > 0) {
			Collections.sort(slots, new Comparator<ViewSlot>() {
				@Override
				public int compare(final ViewSlot object1, final ViewSlot object2) {
					return object1.getName().compareTo(object2.getName());
				}
			} );
		}
		for (int i = 0; i < slots.size(); i++) {
			ViewSlot slot = slots.get(i);
			int curRow = i + row;
			
			detailTable.setText(curRow, 0, Products.getNice(slot.getName(),
					true, true));
						
			if (slot.getValues()==null) {
				detailTable.setText(curRow, 1, "");
			} 
			else {
				String valuesString = Products.toSeparatedString(slot.getValues());
				if (valuesString.length()>Constants.MAX_STR_SIZE) {
					DisclosurePanel dp = new DisclosurePanel("Click to see the value.");
					dp.setContent(new Label(valuesString));
					dp.setWidth("96%");
					detailTable.setWidget(curRow, 1, dp);
				}
				else {
					// add unit if there is one 
					if (slot.getSlotType()!=null) 
						valuesString += " <" + slot.getSlotType() + ">";
					
					detailTable.setText(curRow, 1, valuesString);	
				}
			}
			row = curRow;
		}
		
		// add styles to cells
		for (int i = 0; i < detailTable.getRowCount(); i++) {
			detailTable.getCellFormatter().setStyleName(i, 0,
					"detailLabel");
			detailTable.getCellFormatter().setStyleName(i, 1,
					"detailValue");
		}
		FocusPanel fp = new FocusPanel(detailTable);
		get().dialogVPanel.insert(fp, 0);
		
		get().productDetailsBox.center();
	}

	/**
	 * Initialize the popup that will display package details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initProductDetailPopup() {
		// set title
		this.productDetailsBox.setText("Package Details");

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
	 * Initialize the paging widget.
	 */
	public void initPaging() {
		PagingOptions pagingOptions = new PagingOptions(getPagingScrollTable());

		this.pagingPanel.add(pagingOptions);
		this.pagingPanel.add(this.recordCountContainer);
		this.layout.setWidget(2, 0, this.pagingPanel);
	}
		
	private TableDefinition<ViewRegistryPackage> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewRegistryPackage>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewRegistryPackage>(rowColors));

		// service name
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Name") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(false);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// LID
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"LID") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(150);
			columnDef.setColumnSortable(false);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// System version name
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Version Name") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getVersionName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(false);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// OBJECT TYPE
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Object Type") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getObjectType();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getStatus();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// DESCRIPTION
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Description") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getDescription();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(false);
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
		get().refreshTable();
	}

	public void onShow() {
		logger.log(Level.FINEST, "Packages..onShow method... serverUrl = " + RegistryUI.serverUrl);
		//logger.log(Level.FINEST, "Products..onShow method... serverUrl = " + RegistryUI.serverUrl);
		onModuleLoaded();
	}
}
