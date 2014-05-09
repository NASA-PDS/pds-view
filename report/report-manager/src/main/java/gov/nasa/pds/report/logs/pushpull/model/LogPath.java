package gov.nasa.pds.report.logs.pushpull.model;

public class LogPath {

	private String profileNode;
	private String profileName;
	private String logSetLabel;
	private String logHome;

	public LogPath() {
		this.profileName = null;
		this.profileNode = null;
		this.logSetLabel = null;
		this.logHome = null;
	}

	public LogPath(String logHome) {
		this.profileName = null;
		this.profileNode = null;
		this.logSetLabel = null;
		this.logHome = logHome;
	}

	public LogPath(String node, String name) {
		this.profileName = name;
		this.profileNode = node;
		this.logSetLabel = null;
		this.logHome = null;
	}

	public LogPath(String logHome, String node, String name, String label) {
		this.profileName = name;
		this.profileNode = node;
		this.logSetLabel = label;
		this.logHome = logHome;
	}

	public String getProfileNode() {
		return profileNode;
	}

	public void setProfileNode(String profileNode) {
		this.profileNode = profileNode;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getLogSetLabel() {
		return logSetLabel;
	}

	public void setLogSetLabel(String logSetLabel) {
		this.logSetLabel = logSetLabel;
	}

	public String getLogHome() {
		return logHome;
	}

	public void setLogHome(String logHome) {
		this.logHome = logHome;
	}

	public String getPath() {
		return this.logHome + "/" + this.profileNode + "/" + this.profileName
				+ "/" + this.logSetLabel;
	}

}
