/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */
package gov.nasa.pds.report.setup.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author jpadams
 *
 */
public class Profile {
	//private String _activeFlag;
	private String _node;
	private String _identifier;
	private String _name;
	private String _method;
	private int _profileId;
	private ArrayList<LogSet> _lsList;
	
	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	public Profile() {
		//this._activeFlag = "";
		this._profileId = 0;
		this._node = "";
		this._identifier = "";
		this._name = "";
		this._method = "";
		this._lsList = new ArrayList<LogSet>();
	}
	
	public Profile(ResultSet rs) throws SQLException {
		this._profileId = rs.getInt("profile_id");
		this._node = rs.getString("node");
		this._identifier = rs.getString("identifier");
		this._name = rs.getString("name");
		this._method = rs.getString("method");
	}
	
	public void setProfile(Map<String,String[]> paramMap) {
		this._profileId = Integer.parseInt((paramMap.get("profile-id")[0]));
		this._identifier = paramMap.get("identifier")[0];
		this._method = paramMap.get("method")[0];
		this._name = paramMap.get("name")[0];
		this._node = paramMap.get("node")[0];
		
		log.info("profileId: "+this._profileId);
		log.info("identifier: "+this._identifier);
		log.info("method: "+this._method);
		log.info("name: "+this._name);
		log.info("node: "+this._node);
		
		ArrayList<LogSet> lsList = new ArrayList<LogSet>();
		//int logCount = Integer.parseInt(paramMap.get("log-set-count")[0]);
		LogSet ls;
		for (int i=1; i<=Integer.parseInt(paramMap.get("log-set-count")[0]); i++) {
			ls = new LogSet();
			ls.setProfileId(getProfileId());
			ls.setLogSetId(Integer.parseInt(paramMap.get("log-set-id-"+i)[0]));
			// Remote machine host name or IP
			ls.setLabel(paramMap.get("label-"+i)[0]);
			ls.setHostname(paramMap.get("hostname-"+i)[0]);
			// User name to connect the remote machine
			ls.setUsername(paramMap.get("username-"+i)[0]);
			// Password for remote machine authentication
			ls.setPassword(paramMap.get("password-"+i)[0]);
			// Destination directory location on remote machine
			ls.setPathname(paramMap.get("pathname-"+i)[0]);
			ls.setActiveFlag(paramMap.get("active-flag-"+i)[0]);

			log.info("profile-"+i+": "+ls.getProfileId());
			log.info("log-set-id-"+i+": "+ls.getLogSetId());
			log.info("label-"+i+": "+ls.getLabel());
			log.info("hostname-"+i+": "+ls.getHostname());
			log.info("username-"+i+": "+ls.getUsername());
			log.info("pathname-"+i+": "+ls.getPathname());
			
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
		return _profileId;
	}

	public void setProfileId(int profileId) {
		this._profileId = profileId;
	}	
	
	public String getNode() {
		return _node;
	}

	public void setNode(String node) {
		this._node = node;
	}

	public String getIdentifier() {
		return _identifier;
	}

	public void setIdentifier(String identifier) {
		this._identifier = identifier;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getMethod() {
		return _method;
	}

	public void setMethod(String method) {
		this._method = method;
	}

	public ArrayList<LogSet> getLogSetList() {
		return _lsList;
	}
	
	public void setLogSetList(ArrayList<LogSet> logSetList) {
		this._lsList = logSetList;
	}
	
	public JsonObject toJson() {
		JsonObject root=  new JsonObject();
		JsonArray jArray = new JsonArray();
		
		root.add("identifier",new JsonPrimitive(this._identifier));
		root.add("method", new JsonPrimitive(this._method));
		root.add("name", new JsonPrimitive(this._name));
		root.add("node", new JsonPrimitive(this._node));
		root.add("profileId", new JsonPrimitive(this._profileId));

		for (Iterator<LogSet> it = this._lsList.iterator(); it.hasNext();) {
			jArray.add(it.next().toJson());
		}
		root.add("logSets", jArray);
		
		return root;
	}
	
}
