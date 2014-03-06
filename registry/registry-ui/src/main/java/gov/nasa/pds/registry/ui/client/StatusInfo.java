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

import gov.nasa.pds.registry.ui.shared.StatusInformation;

//import java.util.HashMap;
//import java.util.Map;

import com.google.gwt.core.client.GWT;
/*
import com.google.gwt.user.client.Window;
import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
*/

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author hyunlee
 */
public class StatusInfo {

	/**
	 * The RPC service used to generate data.
	 */
	private StatusServiceAsync dataService = (StatusServiceAsync)GWT.create(StatusService.class);

	public StatusInfo() {		
	}
	
	public void getStatus(AsyncCallback callback) {
		this.dataService.getStatus(callback);
	}
	
	public void getRegistryServices(AsyncCallback callback) {
		this.dataService.getRegistryServices(callback);
	}
}
