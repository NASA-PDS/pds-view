package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewProduct;

import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface to the products service
 * 
 * @author jagander
 */
public interface ProductsServiceAsync {

	void requestRows(Request request, Map<String, String> filters,
			AsyncCallback<SerializableResponse<ViewProduct>> callback);

}
