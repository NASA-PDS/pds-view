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

import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to the packages service
 * 
 * @author hyunlee
 */
@RemoteServiceRelativePath("packages")
public interface PackageService extends RemoteService {

	SerializableResponse<ViewRegistryPackage> requestRows(Request request);
	
	boolean updatePackage(final ViewRegistryPackage registryPackage);
	
	boolean deletePackage(ViewRegistryPackage registryPackage);
	
	String getRemoteUser();
	
	String getRemotePassword();
	
	String getSessionId();
}
