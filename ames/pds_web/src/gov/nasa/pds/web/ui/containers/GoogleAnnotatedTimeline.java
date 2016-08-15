package gov.nasa.pds.web.ui.containers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleAnnotatedTimeline extends GoogleVisTable {

	public GoogleAnnotatedTimeline(String reqId) {
		super(reqId);
	}

	private GoogleVisColumn XAxis;

	private GoogleVisColumn YAxis;

	private Map<String, List<GoogleVisRow>> lines = new HashMap<String, List<GoogleVisRow>>();

	public void setXAxis(final String label, final String type) {
		this.XAxis = new GoogleVisColumn(null, label, null, type);
	}

	public void setYAxis(final String label, final String type) {
		this.YAxis = new GoogleVisColumn(null, label, null, type);
	}

	public void addLine(final String label, final List<GoogleVisRow> data) {
		this.lines.put(label, data);
	}

	@SuppressWarnings("nls")
	@Override
	public JSONObject toJSONObj() {
		this.colsObj.put(getColumnObj(this.XAxis));
		this.colsObj.put(getColumnObj(this.YAxis));
		// add events column
		this.colsObj.put(getColumnObj(new GoogleVisColumn(null, "text1", null,
				"string")));
		for (Iterator<Entry<String, List<GoogleVisRow>>> it = this.lines
				.entrySet().iterator(); it.hasNext();) {
			final Entry<String, List<GoogleVisRow>> entry = it.next();
			// final String label = entry.getKey();
			final List<GoogleVisRow> data = entry.getValue();

			// add top indicator of current value
			// final GoogleVisColumn indicator = new GoogleVisColumn(null,
			// label, null, "number");//$NON-NLS-1$
			// this.colsObj.put(getColumnObj(indicator));
			for (final GoogleVisRow row : data) {
				final JSONArray dataArray = new JSONArray();
				final JSONObject dataObj = new JSONObject();

				try {
					dataObj.put("c", dataArray);//$NON-NLS-1$
					this.rowsObj.put(dataObj);

					dataArray.put(getValObj(row.getXVal()));
					dataArray.put(getValObj(row.getYVal()));
					if (row.getEvent() != null) {
						dataArray.put(getValObj(row.getEvent()));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
		return this.baseObj;
	}

	private JSONObject getValObj(final Object val) throws JSONException {
		final JSONObject returnObject = new JSONObject();

		returnObject.put("v", val); //$NON-NLS-1$
		returnObject.put("f", JSONObject.NULL); //$NON-NLS-1$

		return returnObject;
	}

	private JSONObject getColumnObj(final GoogleVisColumn column) {
		final JSONObject returnObject = new JSONObject();

		try {
			returnObject.put("id", column.getId());//$NON-NLS-1$
			returnObject.put("label", column.getLabel()); //$NON-NLS-1$
			returnObject.put("pattern", column.getPattern()); //$NON-NLS-1$
			returnObject.put("type", column.getType()); //$NON-NLS-1$
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnObject;
	}
}
