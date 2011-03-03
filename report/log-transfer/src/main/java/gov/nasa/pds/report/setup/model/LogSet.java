package gov.nasa.pds.report.setup.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LogSet {

	private int _logSetId;
	private int _profileId;
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
	}
	
	public LogSet(ResultSet rs)  throws SQLException {
		this._logSetId = rs.getInt("log_set_id");
		this._hostname = rs.getString("hostname");
		this._username = rs.getString("username");
		this._password = rs.getString("password");
		this._pathname = rs.getString("pathname");
		this._label = rs.getString("label");
		this._profileId = rs.getInt("profile_id");
		//this._activeFlag = rs.getString("active_flag");
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
	public JsonObject toJson() {
		JsonObject jObj = new JsonObject();
		jObj.add("hostname", new JsonPrimitive(this._hostname));
		jObj.add("label", new JsonPrimitive(this._label));
		jObj.add("password", new JsonPrimitive(this._password));
		jObj.add("pathname", new JsonPrimitive(this._pathname));
		jObj.add("username", new JsonPrimitive(this._username));
		jObj.add("logSetId", new JsonPrimitive(this._logSetId));
		jObj.add("profileId", new JsonPrimitive(this._profileId));
		//jObj.add("activeFlag", new JsonPrimitive(this._activeFlag));
		return jObj;
	}
}
