/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id: Profile.java 9088 2011-05-24 20:44:53Z jpadams $ 
 * 
 */
package gov.nasa.pds.report.logs.pushpull.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Profile object
 * 
 * @author jpadams
 * 
 */
public class Profile {
	private String node;
	private String identifier;
	private String name;
	private String method;
	private int profileId;
	private List<LogSet> lsList;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Base constructor class with no parameters
	 */
	public Profile() {
		this.profileId = 0;
		this.node = "";
		this.identifier = "";
		this.name = "";
		this.method = "";
		this.lsList = new ArrayList<LogSet>();
	}

	/*
	 * public String get_activeFlag() { return _activeFlag; } public void
	 * set_activeFlag(String activeFlag) { this._activeFlag = activeFlag; }
	 */
	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<LogSet> getLogSetList() {
		return lsList;
	}

	public List<LogSet> getNewLogSets() {
		List<LogSet> newSetList = new ArrayList<LogSet>();
		LogSet ls;
		for (Iterator<LogSet> it = this.lsList.iterator(); it.hasNext();) {
			ls = it.next();
			if (ls.isNewSet())
				newSetList.add(ls);
		}
		return newSetList;
	}

	public void setLogSetList(List<LogSet> logSetList) {
		this.lsList = logSetList;
	}
}
