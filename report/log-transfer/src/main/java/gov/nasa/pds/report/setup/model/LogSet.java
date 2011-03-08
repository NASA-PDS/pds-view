package gov.nasa.pds.report.setup.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LogSet {

	private int _logSetId;
	private int _profileId;
	private int _setNumber;
	private String _activeFlag;
	private String _label;
	private String _hostname;
	private String _username;
	private String _password;
	private String _pathname;
	
	public LogSet() {
		this._logSetId = 0;
		this._profileId = 0;
		this._label = "";
		this._hostname = "";
		this._username = "";
		this._password = "";
		this._pathname = "";
		this._activeFlag = "";
		this._setNumber = 0;
	}
	
	public LogSet(ResultSet rs)  throws SQLException {
		setLogSetId(rs.getInt("log_set_id"));
		setHostname(rs.getString("hostname"));
		setUsername(rs.getString("username"));
		setPassword(rs.getString("password"));
		setPathname(rs.getString("pathname"));
		setLabel(rs.getString("label"));
		setProfileId(rs.getInt("profile_id"));
		setSetNumber(rs.getInt("set_number"));
		setActiveFlag(rs.getString("active_flag"));
	}
	
	public LogSet(Map<String,String[]> paramMap, int i) {
		setLogSetId(Integer.parseInt(paramMap.get("log-set-id-"+i)[0]));
		setLabel(paramMap.get("label-"+i)[0]);
		setHostname(paramMap.get("hostname-"+i)[0]);
		setUsername(paramMap.get("username-"+i)[0]);
		setPassword(paramMap.get("password-"+i)[0]);
		setPathname(paramMap.get("pathname-"+i)[0]);
		setActiveFlag(paramMap.get("active-flag-"+i)[0]);
		setSetNumber(Integer.parseInt(paramMap.get("set-number-"+i)[0]));
	}

	public String getActiveFlag() {
		return _activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this._activeFlag = activeFlag;
	}
	public int getLogSetId() {
		return _logSetId;
	}
	public void setLogSetId(int infoId) {
		this._logSetId = infoId;
	}
	public int getProfileId() {
		return _profileId;
	}
	public void setProfileId(int profileId) {
		this._profileId = profileId;
	}
	public String getLabel() {
		return _label;
	}
	public void setLabel(String label) {
		this._label = label.replace(' ', '_');
	}
	public String getHostname() {
		return _hostname;
	}
	public void setHostname(String hostname) {
		this._hostname = hostname;
	}
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		this._username = username;
	}
	public String getPassword() {
		return _password;
	}
	public void setPassword(String password) {
		this._password = password;
	}
	public String getPathname() {
		return _pathname;
	}
	public void setPathname(String pathname) {
		this._pathname = pathname;
	}
	public int getSetNumber() {
		return _setNumber;
	}
	public void setSetNumber(int setNumber) {
		this._setNumber = setNumber;
	}
	public boolean isNewSet() {
		if (this._logSetId == 0)
			return true;
		
		return false;
	}
	public JsonObject toJson() {
		JsonObject jObj = new JsonObject();
		jObj.add("hostname", new JsonPrimitive(this._hostname));
		jObj.add("label", new JsonPrimitive(this._label));
		jObj.add("password", new JsonPrimitive(this._password));
		jObj.add("pathname", new JsonPrimitive(this._pathname));
		jObj.add("setNumber", new JsonPrimitive(this._setNumber));
		jObj.add("username", new JsonPrimitive(this._username));
		jObj.add("logSetId", new JsonPrimitive(this._logSetId));
		jObj.add("profileId", new JsonPrimitive(this._profileId));
		//jObj.add("activeFlag", new JsonPrimitive(this._activeFlag));
		return jObj;
	}
}
