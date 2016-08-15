package gov.nasa.pds.web.ui.containers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class GoogleVisTable {

	protected JSONObject baseObj = new JSONObject();

	protected JSONObject tableObj = new JSONObject();

	protected JSONArray rowsObj = new JSONArray();

	protected JSONArray colsObj = new JSONArray();

	private final List<GoogleVisColumn> columns = new ArrayList<GoogleVisColumn>();

	@SuppressWarnings("unused")
	private final List<GoogleVisRow> rows = new ArrayList<GoogleVisRow>();

	@SuppressWarnings("nls")
	protected GoogleVisTable(final String reqId) {
		try {
			this.baseObj.put("status", "ok");
			this.baseObj.put("reqId", reqId);
			this.baseObj.put("table", this.tableObj);
			this.tableObj.put("rows", this.rowsObj);
			this.tableObj.put("cols", this.colsObj);
			// can't recall what this does but doesn't seem needed for this use
			this.tableObj.put("p", JSONObject.NULL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void addColumn(final String id, final String label,
			final String pattern, final String type) {
		this.columns.add(new GoogleVisColumn(id, label, pattern, type));
	}

	protected void addRow() {
		// not supported
	}

	public abstract JSONObject toJSONObj();

}
