/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id: DBUtil.java 9088 2011-05-24 20:44:53Z jpadams $ 
 * 
 */

package gov.nasa.pds.report.db;

import gov.nasa.pds.report.logs.pushpull.model.LogSet;
import gov.nasa.pds.report.logs.pushpull.model.Profile;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jpadams
 * 
 */
public class DBUtil extends DatabaseManager {

	/** Holds profileId for current profile being inserted/updated. **/
	private int profileId = -1;

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public DBUtil(final String path) throws FileNotFoundException {
		super(path);
	}

	/**
	 * Query the report service database for a specific profile by ID
	 * 
	 * @param profileId
	 * @return Profile object
	 * @throws SQLException
	 */
	public final Profile findByProfileId(final int profileId)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Profile prof = null;
			List<LogSet> liList = new ArrayList<LogSet>();

			connect();
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM profiles WHERE profile_id="
					+ profileId + " AND active_flag='y'");
			if (rs.next()) {
				prof = new Profile(rs);

				this.log.finer("PROFILE: "
						+ String.valueOf(prof.getProfileId()) + ", "
						+ prof.getNode() + ", " + prof.getIdentifier() + ", "
						+ prof.getName() + ", " + prof.getMethod());
			} else {
				return null;
			}

			LogSet li;
			rs = stmt.executeQuery("SELECT * FROM log_sets WHERE profile_id="
					+ profileId + " AND active_flag='y'");
			while (rs.next()) {
				li = new LogSet(rs);

				this.log.finer("LOG INFO: " + String.valueOf(li.getProfileId())
						+ ", " + li.getHostname() + ", " + li.getUsername()
						+ ", " + li.getPassword() + ", " + li.getPathname()
						+ ", " + li.getLogSetId());

				liList.add(li);
			}

			prof.setLogSetList(liList);
			return prof;
		} catch (NullPointerException e) {
			throw new SQLException("Profile not found.");
		} finally {
			stmt.close();
			rs.close();
			closeConnect();
		}
	}

	/**
	 * Query the report service database for a specific profile by ID
	 * 
	 * @param profileName
	 * @return Profile object
	 * @throws SQLException
	 */
	public final Profile findByProfileName(final String profileName)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Profile prof = null;
			List<LogSet> liList = new ArrayList<LogSet>();

			connect();
			stmt = this.conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM profiles WHERE name='"
					+ profileName + "' AND active_flag='y'");
			if (rs.next()) {
				prof = new Profile(rs);

				this.log.finer("PROFILE: "
						+ String.valueOf(prof.getProfileId()) + ", "
						+ prof.getNode() + ", " + prof.getIdentifier() + ", "
						+ prof.getName() + ", " + prof.getMethod());
			} else {
				return null;
			}

			LogSet li;
			rs = stmt.executeQuery("SELECT * FROM log_sets WHERE profile_id="
					+ prof.getProfileId() + " AND active_flag='y'");
			while (rs.next()) {
				li = new LogSet(rs);

				this.log.info("LOG INFO: " + String.valueOf(li.getProfileId())
						+ ", " + li.getHostname() + ", " + li.getUsername()
						+ ", " + li.getPassword() + ", " + li.getPathname()
						+ ", " + li.getLogSetId());

				liList.add(li);
			}

			prof.setLogSetList(liList);
			return prof;
		} catch (NullPointerException e) {
			throw new SQLException("Profile not found.");
		} finally {
			stmt.close();
			rs.close();
			closeConnect();
		}
	}

	/**
	 * Query the database for all active profiles
	 * 
	 * @return List&lt;Profile&gt;
	 * @throws SQLException
	 */
	public final List<Profile> findAllProfiles() throws SQLException {
		Statement stmt1 = null;
		Statement stmt2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		try {
			List<Profile> pList = new ArrayList<Profile>();
			Profile prof;
			List<LogSet> lsList;
			LogSet li;

			connect();
			stmt1 = this.conn.createStatement();
			rs1 = stmt1
					.executeQuery("SELECT * FROM profiles WHERE active_flag='y' ORDER BY name");
			while (rs1.next()) {
				prof = new Profile(rs1);
				this.log.finer("PROFILE: "
						+ String.valueOf(prof.getProfileId()) + ", "
						+ prof.getNode() + ", " + prof.getIdentifier() + ", "
						+ prof.getName() + ", " + prof.getMethod());

				lsList = new ArrayList<LogSet>();
				stmt2 = this.conn.createStatement();
				rs2 = stmt2
						.executeQuery("SELECT * FROM log_sets WHERE profile_id="
								+ rs1.getInt("profile_id")
								+ " AND active_flag='y'");
				while (rs2.next()) {
					li = new LogSet(rs2);
					this.log.finer("LOG INFO: "
							+ String.valueOf(li.getProfileId()) + ", "
							+ li.getHostname() + ", " + li.getUsername() + ", "
							+ li.getPassword() + ", " + li.getPathname());

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
			closeConnect();
		}
	}

	/**
	 * 
	 * @param profile
	 * @throws SQLException
	 */
	public final void createNew(final Profile profile) throws SQLException {
		insertProfile(profile);
		for (Iterator<LogSet> it = profile.getLogSetList().iterator(); it
				.hasNext();) {
			insertLogInfo(it.next());
		}
	}

	/**
	 * 
	 * @param lsList
	 * @param profileId
	 * @throws SQLException
	 */
	public final void update(final List<LogSet> lsList, final int profileId)
			throws SQLException {
		LogSet ls;
		this.profileId = profileId;
		// updateProfile(profile);
		for (Iterator<LogSet> it = lsList.iterator(); it.hasNext();) {
			ls = it.next();
			if (ls.getLogSetId() == 0) {
				insertLogInfo(ls);
			} else {
				updateLogInfo(ls);
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public final void removeLogInfo(final int id) throws SQLException {
		String sql = "UPDATE log_sets SET " + "active_flag='n' "
				+ "WHERE log_set_id=" + id;

		this.log.fine(sql);
		executeUpdate(sql);
	}

	/**
	 * 
	 * @param profile
	 * @throws SQLException
	 */
	public final void updateProfile(final Profile profile) throws SQLException {
		this.profileId = profile.getProfileId();
		String sql = "UPDATE profiles SET " + "node='" + profile.getNode()
				+ "'," + "identifier='" + profile.getIdentifier() + "',"
				+ "name='" + profile.getName() + "'," + "method='"
				+ profile.getMethod() + "'," + "created_by='UNK' " // TODO
																	// change
																	// from
																	// default
																	// UNK
				+ "WHERE profile_id=" + this.profileId;

		this.log.fine(sql);
		executeUpdate(sql);
	}

	/**
	 * 
	 * @param logSet
	 * @throws SQLException
	 */
	public final void updateLogInfo(final LogSet logSet) throws SQLException {
		String sql = "UPDATE log_sets SET " + "hostname='"
				+ logSet.getHostname() + "'," + "username='"
				+ logSet.getUsername() + "'," + "password='"
				+ logSet.getPassword() + "'," + "pathname='"
				+ logSet.getPathname() + "'," + "label='" + logSet.getLabel()
				+ "'," + "set_number=" + logSet.getSetNumber() + ","
				+ "created_by='UNK' " + // TODO change from default UNK
				"WHERE log_set_id=" + logSet.getLogSetId();

		this.log.fine(sql);
		executeUpdate(sql);
	}

	/**
	 * 
	 * @param profile
	 * @throws SQLException
	 */
	public final void insertProfile(final Profile profile) throws SQLException {
		String sql = "INSERT INTO profiles ( node, identifier, name, method, created_by)"
				+ "values ('"
				+ profile.getNode()
				+ "',"
				+ "'"
				+ profile.getIdentifier()
				+ "',"
				+ "'"
				+ profile.getName()
				+ "'," + "'" + profile.getMethod() + "'," + "'UNK')"; // TODO
																		// change
																		// from
																		// default
																		// UNK

		this.profileId = executeUpdate(sql);
	}

	/**
	 * 
	 * @param logSet
	 * @throws SQLException
	 */
	public final void insertLogInfo(final LogSet logSet) throws SQLException {
		String sql = "INSERT INTO log_sets ( profile_id, hostname, username, password, pathname, label, set_number, created_by"
				+ ") values ("
				+ this.profileId
				+ ","
				+ "'"
				+ logSet.getHostname()
				+ "',"
				+ "'"
				+ logSet.getUsername()
				+ "',"
				+ "'"
				+ logSet.getPassword()
				+ "',"
				+ "'"
				+ logSet.getPathname()
				+ "',"
				+ "'"
				+ logSet.getLabel()
				+ "',"
				+ "" + logSet.getSetNumber() + "," + "'UNK')"; // TODO change
																// from default
																// UNK

		executeUpdate(sql);
	}
}
