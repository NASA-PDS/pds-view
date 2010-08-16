package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewProduct;

import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface to the products service
 * 
 * @author jagander
 */
@RemoteServiceRelativePath("products")
public interface ProductsService extends RemoteService {

	SerializableResponse<ViewProduct> requestRows(Request request,
			Map<String, String> filters);

}
