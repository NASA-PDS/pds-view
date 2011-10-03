package gov.nasa.arc.pds.visualizer.client;

import gov.nasa.pds.domain.PDSObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface DataServiceAsync
{

    void parseCollection(String archiveRoot, String readDataFromFile,
			AsyncCallback<PDSObject[]> callback);

    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static DataServiceAsync instance;

        public static final DataServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (DataServiceAsync) GWT.create( DataService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "DataService" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }


	void fetchToplevel(AsyncCallback<PDSObject[]> callback);

	void parseImageProduct(String archiveRoot, String readDataFromFile,
			AsyncCallback<PDSObject[]> callback);

	void parseDocumentProduct(String archiveRoot, String readDataFromFile,
			AsyncCallback<PDSObject[]> callback);

	void parseTableCharacterProduct(String archiveRoot, String readDataFromFile,
			AsyncCallback<PDSObject[]> callback);

}
