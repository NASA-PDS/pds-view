package gov.nasa.pds.report.logs.pushpull.model;

import gov.nasa.pds.report.constants.Constants;

import org.jasypt.util.text.StrongTextEncryptor;

// TODO: This class (and perhaps others in this package) are out-dated since we
// no longer use a database to store profiles.
public class LogSet {

	private int logSetId;
	private int profileId;
	private int setNumber;
	private String activeFlag;
	private String label;
	private String hostname;
	private String username;
	private String password;
	private String pathname;
	private String xferType;
	private boolean encrypted;

	private StrongTextEncryptor encryptor;

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
		this.encrypted = false;
	}

	private void setEncryptor() {
		this.encryptor = new StrongTextEncryptor();
		this.encryptor.setPassword(Constants.CRYPT_PASSWORD);
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

	/*
	 * public String getDecryptedPassword() { return
	 * this.encryptor.decrypt(password); }
	 */
	/**
	 * Set the local password variable. If new log set, encrypt. Else leave
	 * because password is already encrypted.
	 * 
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
	
	public String getXferType(){
		return xferType;
	}
	
	public void setXferType(String xferType){
		this.xferType = xferType;
	}
	
	public boolean getEncrypted(){
		return encrypted;
	}
	
	public void setEncrypted(boolean encrypted){
		this.encrypted = encrypted;
	}
	
	public String toString(){
		String str = "name:" + this.label;
		str += "\nhost:" + this.hostname;
		str += "\nuser:" + this.username;
		str += "\npassword:" + this.password;
		str += "\ntransfer type:" + this.xferType;
		str += "\nencrypted:" + this.encrypted;
		return str;
	}
	
}
