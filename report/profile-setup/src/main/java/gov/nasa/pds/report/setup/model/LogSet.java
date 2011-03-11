package gov.nasa.pds.report.setup.model;

import gov.nasa.pds.report.setup.Globals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LogSet extends Globals {

	private int logSetId;
	private int profileId;
	private int setNumber;
	private String activeFlag;
	private String label;
	private String hostname;
	private String username;
	private String password;
	private String pathname;
	
	private StandardPBEStringEncryptor encryptor;

	public LogSet() {
		setEncryptor();
		
		this.logSetId = 0;
		this.profileId = 0;
		this.label = "";
		this.hostname = "";
		this.username = "";
		this.password = "";
		this.pathname = "";
		this.activeFlag = "";
		this.setNumber = 0;
	}

	public LogSet(ResultSet rs)  throws SQLException {
		setEncryptor();
		
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

	public LogSet( Map<String, String[]> paramMap, int i) {
		setEncryptor();
		
		setLogSetId(Integer.parseInt(paramMap.get("log-set-id-" + i)[0]));
		setLabel(paramMap.get("label-" + i)[0]);
		setHostname(paramMap.get("hostname-" + i)[0]);
		setUsername(paramMap.get("username-" + i)[0]);
		setPassword(paramMap.get("password-" + i)[0]);
		setPathname(paramMap.get("pathname-" + i)[0]);
		setActiveFlag(paramMap.get("active-flag-"+i)[0]);
		setSetNumber(Integer.parseInt(paramMap.get("set-number-" + i)[0]));
	}
	
	private void setEncryptor() {
		this.encryptor = new StandardPBEStringEncryptor();
		this.encryptor.setPassword(CRYPT_PASSWORD);
	}

	public String getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	public int getLogSetId() {
		return logSetId;
	}
	public void setLogSetId(int infoId) {
		this.logSetId = infoId;
	}
	public int getProfileId() {
		return profileId;
	}
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label.replace(' ', '_');
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public String getDecryptedPassword() {
		return this.encryptor.decrypt(password);
	}
	/**
	 * Set the local password variable.
	 * If new log set, encrypt.
	 * Else leave because password is already encrypted.
	 * @param password
	 */
	public void setPassword(String password) {
		if (this.logSetId == 0) {
			this.password = this.encryptor.encrypt(password);
		} else {
			this.password = password;
		}
	}
	public String getPathname() {
		return pathname;
	}
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}
	public int getSetNumber() {
		return setNumber;
	}
	public void setSetNumber(int setNumber) {
		this.setNumber = setNumber;
	}
	public boolean isNewSet() {
		if (this.logSetId == 0) {
			return true;
		}
		return false;
	}
	public JsonObject toJson() {
		JsonObject jObj = new JsonObject();
		jObj.add("hostname", new JsonPrimitive(this.hostname));
		jObj.add("label", new JsonPrimitive(this.label));
		jObj.add("password", new JsonPrimitive(this.password));
		jObj.add("pathname", new JsonPrimitive(this.pathname));
		jObj.add("setNumber", new JsonPrimitive(this.setNumber));
		jObj.add("username", new JsonPrimitive(this.username));
		jObj.add("logSetId", new JsonPrimitive(this.logSetId));
		jObj.add("profileId", new JsonPrimitive(this.profileId));
		//jObj.add("activeFlag", new JsonPrimitive(this._activeFlag));
		return jObj;
	}
}
