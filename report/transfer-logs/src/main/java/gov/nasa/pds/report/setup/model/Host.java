package gov.nasa.pds.report.setup.model;

public class Host {

	private String _profile;
	private String _hostname;
	private String _username;
	private String _password;
	private String _srcPath;
	private String _regex;
	
	public Host() {
		this._profile = null;
		this._hostname = "";
		this._username = "";
		this._password = "";
		this._srcPath = "";
		this._regex = "";
	}
	
	public String getProfile() {
		return _profile;
	}
	public void setProfile(String profile) {
		this._profile = profile;
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
	public String getSrcPath() {
		return _srcPath;
	}
	public void setSrcPath(String srcPath) {
		this._srcPath = srcPath;
	}
	public String getRegex() {
		return _regex;
	}
	public void setRegex(String regex) {
		this._regex = regex;
	}
	
}
