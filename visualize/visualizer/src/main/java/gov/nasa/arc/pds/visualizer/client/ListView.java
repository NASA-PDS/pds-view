/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.nasa.arc.pds.visualizer.client;


import gov.nasa.arc.pds.visualizer.shared.Constants;
import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.FieldInfo;
import gov.nasa.pds.domain.HasURL;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TokenizedLabel;
import gov.nasa.pds.domain.TokenizedLabelSection;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListView extends ResizeComposite implements ListPresenter.Display {

	// An image bundle is a gwt construct used to improve application performance by reducing the number 
	// of round trip HTTP requests to the server to fetch images.
	public interface Images extends ClientBundle {
		public static final Images INSTANCE = GWT.create(Images.class);
		@Source("images/pdfIcon.jpg") ImageResource pdfIcon();
		@Source("images/htmlIcon.png") ImageResource htmlIcon();
		@Source("images/txtDocIcon60.png")ImageResource txtDocIcon60();
		@Source("images/unknownDoc60.png")ImageResource unknownDoc60();
		@Source("images/spreadsheetDoc60.png")ImageResource spreadsheetDoc60();
	}  
	
	private final String COL_WIDTH =  "333px";
	private final int ROW0 =  0;
	private final String ROW0_STYLE_NAME= "row0";
	interface Binder extends UiBinder<Widget, ListView> { }
	interface SelectionStyle extends CssResource {
		String selectedRow();
	}
	private static final Binder binder = GWT.create(Binder.class);
	static final int VISIBLE_DATAITEM_COUNT = 60;
	@UiField FlexTable header;
	@UiField FlexTable table;
	@UiField SelectionStyle selectionStyle;
	private int selectedRowNumber = -1;
	
	public ListView() {
		initWidget(binder.createAndBindUi(this));
		header.setText(0, 0, Constants.listViewMsg1);
	}
	
	@Override
	protected void onLoad() {

		// Select the first row if none is selected.
		if (selectedRowNumber == -1) {
			selectRow(0);
		}
	}

	@UiHandler("table")
	void onTableClicked(ClickEvent event) {
		// Select the row that was clicked (-1 to account for header row).
		Cell cell = table.getCellForEvent(event);
		selectRow(cell.getRowIndex());
	}

	/** Selects the given row (relative to the current page). */
	private void selectRow(int row) {
		
		styleRow(selectedRowNumber, false);
		styleRow(row, true);		
		selectedRowNumber = row;
	}

	private void styleDimensions(Image i) {
		// todo for consistency get image dimensions from popup label's dimensions
		double parentWidth  = 600;
		double parentHeight = 600;
		double imageHeight = i.getHeight();
		double imageWidth = i.getWidth();
		double percentOverHeight = (imageHeight - parentHeight) / parentHeight;
		double percentOverWidth = (imageWidth - parentWidth) / parentWidth;
    	double adjustBy = 1;

    	if (percentOverHeight > 0 || percentOverWidth > 0) {
    		adjustBy = Math.max(percentOverHeight, percentOverWidth);
    		System.err.println("adjustBy"+ adjustBy);
    		if (adjustBy < 1) {
    			adjustBy = (1 - adjustBy);
    		} else {
    			adjustBy = 1 / adjustBy;
    		}
    		String newHeight = String.valueOf(imageHeight * adjustBy);
    		String newWidth = String.valueOf(imageWidth * adjustBy);
    		i.setHeight(newHeight + "px");
    		i.setWidth(newWidth + "px");
    	}      
	}

	private void styleRow(int row, boolean selected) {
		if (row != -1) {
			String style = selectionStyle.selectedRow();

			if (selected) {
				table.getRowFormatter().addStyleName(row, style);
			} else {
				table.getRowFormatter().removeStyleName(row, style);
			}
		}
	}

	@Override
	public HasClickHandlers getList() {
		return table;
	}

	@Override
	public int getClickedRow(ClickEvent event) {
		int selectedRow = -1;
		HTMLTable.Cell cell = table.getCellForEvent(event);
		if (cell != null) {			
			selectedRow = cell.getRowIndex();			
		}
		return selectedRow;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void updatePending() { 
		final String s = "Please wait... fetching data";
		table.removeAllRows();	
        table.setWidth("600px");

		table.setText(0, 0, s); 
		table.getCellFormatter().setWidth(0, 0, "333px"/*;String.valueOf(s.length() + 1) + "em"*/);
	}

	// todo presenter should have domain, view just strings
	@Override
	public void updateChildlistView(PDSObject[] diList, int totalRows) { 
		
		table.removeAllRows();
        table.setWidth("600px");
		table.getColumnFormatter().setWidth(0, COL_WIDTH);//col0
		for (int row = 1; row < VISIBLE_DATAITEM_COUNT; ++row) {
			// Don't read past the end of real data contents
			int diIndex = row - 1;
			if (diIndex >= totalRows) {
				break;
			}
			PDSObject item = this.getDataItem(diList,  diIndex);		
			assert (item.getThis_file_name() != null);
			table.setText(row, 0, ellipse(item.getThis_file_name(), 48)); 
			table.setText(row, 1, item.getArchiveRoot()); 
		}	
	}

	@Override
	public void updateImageView(PDSObject[] diList) {
        table.setWidth("600px");
		table.removeAllRows();
		int count = diList.length;
		int max = VISIBLE_DATAITEM_COUNT;
		if (max > count) {
			max = count;
		}

		// header populates first ROW0
		table.setText(ROW0, 0, "Images");
		table.getColumnFormatter().setWidth(0, COL_WIDTH); //for col0
		table.setText(ROW0, 1, "Attributes");
		table.getColumnFormatter().setWidth(1, COL_WIDTH); // for col1

		table.getRowFormatter().addStyleName(ROW0, ROW0_STYLE_NAME);

		for (int row = 1; row < VISIBLE_DATAITEM_COUNT; ++row) {
			// Don't read past the end of real data contents
			int diIndex = row - 1;
			if (diIndex >= diList.length) {
				break;
			}
			final PDSObject item = this.getDataItem(diList, diIndex);	

			Image image = new Image(((HasURL)item).getUrl());
			image.setStyleName("listViewImage");
			image.setTitle(((HasURL)item).getUrl());

			table.setWidget(row, 0, image);
			table.getColumnFormatter().setWidth(row, COL_WIDTH);

			VerticalPanel vp = new VerticalPanel();
			vp.add(new HTML(getMinimalImageText((Array2DImageProduct)item)));
			Label imageDisplay = new InlineLabel(Constants.linkImage);
			imageDisplay.setStyleName("onclickLabel");
			imageDisplay.addClickHandler(new ClickHandler() {

	            @Override
	            public void onClick(ClickEvent event) {
					final DialogBox popup = new DialogBox();
				    popup.getCaption().setText(item.getThis_file_name());
					popup.setAnimationEnabled(true);
					popup.setAutoHideEnabled(true);					
	            	String url = ((HasURL)item).getUrl();
					Image i = new Image(url);
								
					styleDimensions(i);
					Button close = new Button("Close");
					close.addClickHandler(new ClickHandler() {		
						@Override
						public void onClick(ClickEvent event) {
						    popup.hide();
						}
					});
					Panel panel = new VerticalPanel();
					panel.add(i);
					panel.add(close);
					popup.setWidget(panel);
					popup.center();
					popup.show();
	            }
	        });

			Label fullLabelDisplay = new InlineLabel(Constants.linkLabel);
			fullLabelDisplay.setStyleName("onclickLabel");
			fullLabelDisplay.addClickHandler(new ClickHandler() {
	            @Override
	            public void onClick(ClickEvent event) {
					createAndShowLabel(item.getTokenizedLabel());
	            }
	        });

			Panel controls = new FlowPanel();
			controls.add(fullLabelDisplay);
			controls.add(imageDisplay);
			vp.add(controls);
			
			table.setWidget(row, 1, vp);
		}
	}

	
	@Override
	public void updateDocumentView(PDSObject[] diList) {

		table.removeAllRows();

		// header populates first ROW0
		table.setText(ROW0, 0, "Documents");
		table.getColumnFormatter().setWidth(0, COL_WIDTH); //for col0
		table.setText(ROW0, 1, "Attributes");
		table.getColumnFormatter().setWidth(1, COL_WIDTH); //for col1

		table.getRowFormatter().addStyleName(ROW0, ROW0_STYLE_NAME);

		for (int row = 1; row < VISIBLE_DATAITEM_COUNT; ++row) {
			int diIndex = row - 1;
			if (diIndex >= diList.length) {
				break;
			}
			final PDSObject item = this.getDataItem(diList, diIndex);	

			table.setHTML(row, 0, imageItemHTML(((DocumentProduct)item).getDocumentType(), item.getThis_file_name()));
			table.getColumnFormatter().setWidth(row, COL_WIDTH);

			VerticalPanel vp = new VerticalPanel();
			vp.add(new HTML(getMinimalDocumentText((DocumentProduct)item)));
			
			// label control			
			Label fullLabelDisplay = new InlineLabel(Constants.linkLabel);			
			fullLabelDisplay.setStyleName("onclickLabel");
			fullLabelDisplay.addClickHandler(new ClickHandler() {
	            @Override
	            public void onClick(ClickEvent event) {
					createAndShowLabel(item.getTokenizedLabel());
	            }
	        });
			
			Label docButton = new InlineLabel(Constants.linkDoc);
			docButton.setStyleName("onclickLabel");
			docButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					String url = ((HasURL)item).getUrl();				    
					com.google.gwt.user.client.Window.open(url, "_blank", "");
				}
			});
			
			Panel controls = new FlowPanel();
			controls.add(fullLabelDisplay);
			controls.add(docButton);
			vp.add(controls);
			table.setWidget(row, 1, vp);
		}
	}
	
	
	/**
	 * Update the view for a table.  
	 * @param item A display item containing a list of all the fields.  
	 *             Each field also has all the row values.
	 */
	@Override
	public void updateTableView(List<FieldInfo> fieldInfos, int totalRows, final TokenizedLabel tokenizedLabel) {
		int currentRow = -1;

		table.removeAllRows();	
		assert ((fieldInfos != null) && (fieldInfos.size() > 0)) : "invalid fieldInfos parameter";

		// label control
		currentRow++;
		Label fullLabelDisplay = new InlineLabel(Constants.linkLabel);
		table.setWidget(currentRow, 0, fullLabelDisplay);
		table.getFlexCellFormatter().setColSpan(currentRow, 0, 2); 
		fullLabelDisplay.setStyleName("onclickLabel");
		fullLabelDisplay.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
				createAndShowLabel(tokenizedLabel);
            }
        });
		
		// init column headers and width of each kth column
		currentRow++;
		String colwidth =  "100px";
		int k=0;
		while (k < fieldInfos.size()) {
			String columnName = fieldInfos.get(k).getField_name();
			table.setText(currentRow, k, columnName);
			table.getColumnFormatter().setWidth(k, colwidth);
			k++;			
		}
		table.getRowFormatter().addStyleName(currentRow, ROW0_STYLE_NAME);
			
		// populate table contents
		currentRow++;
		for (int row=currentRow; row < totalRows; row++) {
			for (int col=0; col<fieldInfos.size(); col++) {
				FieldInfo fi = fieldInfos.get(col);
				table.setText(row, col, fi.getValues().get(row));
			}
		}	
	}


	private String getMinimalImageText(Array2DImageProduct item) {
		return "Filename: "+item.getThis_file_name() 
		+ " <br>Fast Axis Name: "+item.getArray_axis_1_name()
		+ " <br>Fast Axis Size: "+item.getArray_axis_1_elements()
		+ " <br>Fast Axis Unit: "+item.getArray_axis_1_unit()

		+ " <br>Axis2 Name: "+item.getArray_axis_2_name()
		+ " <br>Axis2 Size: "+item.getArray_axis_2_elements()
		+ " <br>Axis2 Unit: "+item.getArray_axis_2_unit()


		+ " <br>Records: "+item.getRecords()
		+ " <br>File size: "+item.getFile_size()
		+ " <br>Creation date: "+item.getCreation_date_time()
		;
	}
	
	private String getMinimalDocumentText(DocumentProduct item) {
		return "Local Identifier: "+item.getDocumentLocalIdentifier() 
		+ " <br>Type: "+item.getDocumentType()
		+ " <br>Creation date: "+item.getCreation_date_time()
		;
	}


	private PDSObject getDataItem(PDSObject[] diList, int index) {
		if (index >= diList.length) {
			return null;
		}
		return diList[index];
	}

	/**
	 * Truncate a string and add an ellipsis ('...') to the end if it exceeds the
	 * specified length.
	 * 
	 * @param value the string to truncate
	 * @param len the maximum length to allow before truncating
	 * @return the converted text
	 */
	private static String ellipse(String value, int len) {
		if (value != null && value.length() > len) {
			return value.substring(0, len - 3) + "...";
		}
		return value;
	}

	private String imageItemHTML(String docType, String title) {
		ImageResource ir = null;
		if (docType.equals("HTML")) 
			ir = Images.INSTANCE.htmlIcon();		
		else 
			if (docType.equals("PDF")) 
				ir = Images.INSTANCE.pdfIcon();
			else 
				if (docType.equals("TEXT")) 
					ir = Images.INSTANCE.txtDocIcon60();
				else if (docType.equals("SPREADSHEET")) 
					ir = Images.INSTANCE.spreadsheetDoc60();
				else
					ir = Images.INSTANCE.unknownDoc60();

		return AbstractImagePrototype.create(ir).getHTML() + " " + title;
	}
	
	private void createAndShowLabel(final TokenizedLabel tokenizedLabel) {
		final DialogBox popup = new DialogBox();
	    popup.getCaption().setText(tokenizedLabel.getTitle());
	   
		popup.setAnimationEnabled(true);
		popup.setAutoHideEnabled(true);   
		//popup.addStyleName("popupDialog");

		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
			    popup.hide();
			}
		});
		Panel scrollPanel = new ScrollPanel();
		FlexTable labTab = new FlexTable();
		labTab.setStyleName("labTab");
		Map<String, TokenizedLabelSection> sections = tokenizedLabel.getAllSections();
		int row = 0;
		for (String sectionTitle : sections.keySet()) {  
			labTab.setText(row, 0, sectionTitle);
			labTab.getRowFormatter().addStyleName(row, "boldRow");
			labTab.getFlexCellFormatter().setColSpan(row, 0, 2); 
			row++; // section Title spans two cols
			
			TokenizedLabelSection sc = sections.get(sectionTitle);
			List<String[]> nv = sc.getAllNameValuePairs();
			for (String[] pair : nv) {
				labTab.setText(row, 0, ellipse(pair[0], 50));
				labTab.getCellFormatter().setWidth(row, 0, "50px");// todo 

				labTab.setText(row, 1, pair[1]);
				row++;  //name value pair in next row
			}						
		}

		
		// HTML htmlLabel = new HTML(item.getTokenizedLabel().toString());  simple layout
		scrollPanel.add(labTab/*htmlLabel*/);
		scrollPanel.setStyleName("popupLabel");

		Panel outerPanel = new VerticalPanel();
		outerPanel.add(scrollPanel);
		outerPanel.add(close); 

		popup.setWidget(outerPanel);

		popup.center();
		popup.show();
	}
	

}
