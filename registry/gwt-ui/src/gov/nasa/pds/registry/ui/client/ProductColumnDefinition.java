package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewProduct;

import com.google.gwt.gen2.table.client.AbstractColumnDefinition;

/**
 * Column definition specific to the products table display
 * 
 * @author jagander
 */
public abstract class ProductColumnDefinition<ColType> extends
		AbstractColumnDefinition<ViewProduct, ColType> {

	/**
	 * Construct a new {@link ProductColumnDefinition}.
	 * 
	 * @param name
	 *            the name of the column
	 */
	public ProductColumnDefinition(String name) {
		setHeader(0, name);
		setFooter(0, name);
	}

	public void setCellValue(ViewProduct rowValue, String cellValue) {
		// noop
	}
}
