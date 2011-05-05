/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */
package gov.nasa.pds.report.update.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Profile object
 * 
 * @author jpadams
 *
 */
public class Profile {
	//private String activeFlag;
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
		//this._activeFlag = "";
		this.profileId = 0;
		this.node = "";
		this.identifier = "";
		this.name = "";
		this.method = "";
		this.lsList = new ArrayList<LogSet>();
	}
	
	/**
	 * Constructor class that gets values from a ResultSet
	 * @param rs
	 * @throws SQLException
	 */
	public Profile(ResultSet rs) throws SQLException {
		this.profileId = rs.getInt("profile_id");
		this.node = rs.getString("node");
		this.identifier = rs.getString("identifier");
		this.name = rs.getString("name");
		this.method = rs.getString("method");
	}
	
	/**
	 * Constructor class that gets values from a paramater map
	 * @param paramMap
	 */
	public void setProfile(Map<String,String[]> paramMap) {
		this.profileId = Integer.parseInt((paramMap.get("profile-id")[0]));
		this.identifier = paramMap.get("identifier")[0];
		this.method = paramMap.get("method")[0];
		this.name = paramMap.get("name")[0];
		this.node = paramMap.get("node")[0];
		
		log.fine("profileId: "+this.profileId);
		log.fine("identifier: "+this.identifier);
		log.fine("method: "+this.method);
		log.fine("name: "+this.name);
		log.fine("node: "+this.node);
		
		ArrayList<LogSet> lsList = new ArrayList<LogSet>();
		//int logCount = Integer.parseInt(paramMap.get("log-set-count")[0]);
		LogSet ls;
		for (int i=1; i<=Integer.parseInt(paramMap.get("log-set-count")[0]); i++) {
			ls = new LogSet(paramMap,i);
			ls.setProfileId(getProfileId());

			log.fine("profile-"+i+": "+ls.getProfileId());
			log.fine("log-set-id-"+i+": "+ls.getLogSetId());
			log.fine("label-"+i+": "+ls.getLabel());
			log.fine("hostname-"+i+": "+ls.getHostname());
			log.fine("username-"+i+": "+ls.getUsername());
			log.fine("pathname-"+i+": "+ls.getPathname());
			
			lsList.add(ls);
		}
		setLogSetList(lsList);
	}
	
	/*public String get_activeFlag() {
		return _activeFlag;
	}
	public void set_activeFlag(String activeFlag) {
		this._activeFlag = activeFlag;
	}*/
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
	
	public JsonObject toJson() {
		JsonObject root=  new JsonObject();
		JsonArray jArray = new JsonArray();
		
		root.add("identifier",new JsonPrimitive(this.identifier));
		root.add("method", new JsonPrimitive(this.method));
		root.add("name", new JsonPrimitive(this.name));
		root.add("node", new JsonPrimitive(this.node));
		root.add("profileId", new JsonPrimitive(this.profileId));

		for (Iterator<LogSet> it = this.lsList.iterator(); it.hasNext();) {
			jArray.add(it.next().toJson());
		}
		root.add("logSets", jArray);
		
		return root;
	}
	
}
