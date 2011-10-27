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

import gov.nasa.pds.registry.ui.shared.ViewRegistryPackage;

import com.google.gwt.gen2.table.client.AbstractColumnDefinition;

/**
 * Column definition specific to the packages table display
 * 
 * @author hyunlee
 */
public abstract class PackageColumnDefinition<ColType> extends
		AbstractColumnDefinition<ViewRegistryPackage, ColType> {
	
	/**
	 * Construct a new {@link PackageColumnDefinition}.
	 * 
	 * @param name
	 *            the name of the column
	 */
	public PackageColumnDefinition(String name) {
		setHeader(0, name);
		setFooter(0, name);
	}

	public void setCellValue(ViewRegistryPackage rowValue, String cellValue) {
		// noop
	}
}
