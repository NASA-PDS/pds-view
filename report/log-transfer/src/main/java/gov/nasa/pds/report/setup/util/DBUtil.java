/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */

package gov.nasa.pds.report.setup.util;


import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.properties.DBProperties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author jpadams
 * 
 */
public class DBUtil {

	private Connection conn;
	//private String qString;
	private DBProperties dbProps;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private int profileId = -1;

	/**
	 * For testing purposes only.
	 */
	public DBUtil() throws FileNotFoundException {
		try {
			this.dbProps = new DBProperties("/Users/jpadams/dev/workspace/2010-workspace/report/transfer-logs/src/main/resources/");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public DBUtil(final String path) throws FileNotFoundException {
		try {
			this.dbProps = new DBProperties(path);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public DBUtil(final String path, final String qs) throws FileNotFoundException {
		try {
			this.dbProps = new DBProperties(path);
			//this.qString = qs;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public final void closeConn() {
		try {
			this.conn.close();
			//DriverManager.deregisterDriver(DriverManager.getDriver(this._dbProps.getDriver()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void connect() throws SQLException {
		try {
			Class.forName(this.dbProps.getDriver());
			this.conn = DriverManager.getConnection(this.dbProps.getUrl(),
					this.dbProps.getProperties());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public final Profile findByProfileId(final int profileId) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Profile prof = null;
			ArrayList<LogSet> liList = new ArrayList<LogSet>();
			
			connect();			
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM profiles WHERE profile_id="+profileId+" AND active_flag='y'");
			if (rs.next()) {
				prof = new Profile(rs);
				
				this.log.finer("PROFILE: "+String.valueOf(prof.getProfileId())+", "+prof.getNode()+", "+
						prof.getIdentifier()+", "+prof.getName()+", "+prof.getMethod());
			}
			
			LogSet li;
			rs = stmt.executeQuery("SELECT * FROM log_sets WHERE profile_id="+profileId+" AND active_flag='y'");
			while (rs.next()) {
				li = new LogSet(rs);
				
				this.log.finer("LOG INFO: "+String.valueOf(li.getProfileId())+", "+li.getHostname()+", "+
						li.getUsername()+", "+li.getPassword()+", "+li.getPathname()+", "+li.getLogSetId());
				
				liList.add(li);
			}
			
			prof.setLogSetList(liList);
			return prof;
		} catch (NullPointerException e) {
			throw new SQLException("Profile not found.");
		} finally { 
			stmt.close();
			rs.close();
			closeConn();
		}
	}
	
	public final ArrayList<Profile> findAllProfiles() throws SQLException {
		Statement stmt1 = null;
		Statement stmt2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		
		try {
			ArrayList<Profile> pList = new ArrayList<Profile>();
			Profile prof;
			ArrayList<LogSet> lsList;
			LogSet li;
			
			connect();
			stmt1 = this.conn.createStatement();
			rs1 = stmt1.executeQuery("SELECT * FROM profiles WHERE active_flag='y' ORDER BY name");
			while (rs1.next()) {
				prof = new Profile(rs1);
				this.log.finer("PROFILE: "+String.valueOf(prof.getProfileId())+", "+prof.getNode()+", "+
						prof.getIdentifier()+", "+prof.getName()+", "+prof.getMethod());
				
				lsList=  new ArrayList<LogSet>();
				stmt2 = this.conn.createStatement();
				rs2 = stmt2.executeQuery("SELECT * FROM log_sets WHERE profile_id="+rs1.getInt("profile_id")+" AND active_flag='y'");
				while (rs2.next()) {
					li = new LogSet(rs2);
					this.log.finer("LOG INFO: "+String.valueOf(li.getProfileId())+", "+li.getHostname()+", "+
							li.getUsername()+", "+li.getPassword()+", "+li.getPathname());
					
					lsList.add(li);
				}
				prof.setLogSetList(lsList);
				pList.add(prof);
			}

			return pList;
		} finally {
			rs1.close();
			rs2.close();
			stmt1.close();
			stmt2.close();
			closeConn();
		}
	}

	public final void createNew(final Profile profile) throws SQLException {
		insertProfile(profile);
		for(Iterator<LogSet> it = profile.getLogSetList().iterator(); it.hasNext();) {
			insertLogInfo(it.next());
		}
	}
	
	public final void update(final ArrayList<LogSet> lsList, final int profileId) throws SQLException {
		LogSet ls;
		this.profileId = profileId;
		//updateProfile(profile);
		for(Iterator<LogSet> it = lsList.iterator(); it.hasNext();) {
			ls = it.next();
			if (ls.getLogSetId() == 0) {
				insertLogInfo(ls);
			} else {
				updateLogInfo(ls);
			}
		}
	}
	
	public final void removeLogInfo(final int id) throws SQLException {
		String sql = "UPDATE log_sets SET " +
			"active_flag='n' "+
			"WHERE log_set_id="+id;
		
		this.log.fine(sql);
		executeUpdate(sql);
	}
	
	public final void updateProfile(final Profile profile) throws SQLException {
		this.profileId = profile.getProfileId();
		String sql = "UPDATE profiles SET " +
				"node='"+profile.getNode()+"'," +
				"identifier='"+profile.getIdentifier()+"'," +
				"name='"+profile.getName()+"'," +
				"method='"+profile.getMethod()+"'," +
				"created_by='UNK' " + // TODO change from default UNK
				"WHERE profile_id="+this.profileId;
		
		this.log.fine(sql);
		executeUpdate(sql);
	}
	
	public final void updateLogInfo(final LogSet logSet) throws SQLException {
		String sql = "UPDATE log_sets SET " +
				"hostname='"+logSet.getHostname()+"'," +
				"username='"+logSet.getUsername()+"'," +
				"password='"+logSet.getPassword()+"'," +
				"pathname='"+logSet.getPathname()+"'," +
				"label='"+logSet.getLabel()+"'," +
				//"set_number="+logSet.getSetNumber()+"," +
				"created_by='UNK' " + // TODO change from default UNK
				"WHERE log_set_id="+logSet.getLogSetId();

		this.log.fine(sql);
		executeUpdate(sql);
	}
	
	public final void insertProfile(final Profile profile) throws SQLException {
		String sql = "INSERT INTO profiles ( node, identifier, name, method, created_by)"+
					"values ('"+profile.getNode()+"',"+
					"'"+profile.getIdentifier()+"',"+
					"'"+profile.getName()+"',"+
					"'"+profile.getMethod()+"',"+
					"'UNK')"; // TODO change from default UNK

		this.profileId = executeUpdate(sql);
	}
	
	public final void insertLogInfo(final LogSet logSet) throws SQLException {
		String sql = "INSERT INTO log_sets ( profile_id, hostname, username, password, pathname, label, created_by"+
		") values ("+this.profileId+","+
			"'"+logSet.getHostname()+"',"+
			"'"+logSet.getUsername()+"',"+
			"'"+logSet.getPassword()+"',"+
			"'"+logSet.getPathname()+"',"+
			"'"+logSet.getLabel()+"',"+
			//""+logSet.getSetNumber()+","+
			"'UNK')"; // TODO change from default UNK

		executeUpdate(sql);
	}
	
	public final int executeUpdate(final String sql) throws SQLException {
		this.log.fine(sql);
		connect();
		Statement stmt1 = null;
		try {
			stmt1 = this.conn.createStatement();
			stmt1.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt1.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} finally {
			try {
				stmt1.close();
				closeConn();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args){
		try {			
			DBUtil util = new DBUtil();
			//util.connect();
			
			Profile profile = new Profile();
			profile.setIdentifier("ident10");
			//profile.setMethod("pull1");
			profile.setName("name10");
			profile.setNode("eng");

			ArrayList<LogSet> logInfoList = new ArrayList<LogSet>(); 
			LogSet logSet = new LogSet();
			logSet.setHostname("hostname10");
			logSet.setUsername("username10");
			logSet.setPassword("password10");    
			logSet.setPathname("srcPath10");
			logInfoList.add(logSet);
			
			profile.setLogSetList(logInfoList);
			//util.createNew(profile);
			
			profile = util.findByProfileId(8);
			
			ArrayList<Profile> profileList = util.findAllProfiles();
			//log.info("Profile Output: "profile.getProfileId())
		}catch(Exception e) {
			e.printStackTrace();
		}//end catch
	}//end main


}
