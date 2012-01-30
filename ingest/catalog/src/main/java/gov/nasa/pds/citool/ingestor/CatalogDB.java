// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations 
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
// is subject to U.S. export control laws and regulations, the recipient has 
// the responsibility to obtain export licenses or other export authority as 
// may be required before exporting such information to foreign countries or 
// providing access to foreign nationals.
//
// $Id$


package gov.nasa.pds.citool.ingestor;

import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.citool.report.IngestReport;
import gov.nasa.pds.citool.target.Target;
import gov.nasa.pds.citool.util.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.net.URI;

/**
 * Class to insert the specific catalog data into the corresponding table(s).
 *
 * @author hyunlee
 */
public class CatalogDB {
	public static int okCount = 0;
	public static int failCount = 0;
	public static int newStdValueCount = 0;

    Connection _conn;
    String _sql = null;
    String _dename = "rjoyner";
    String INGEST_MSG = "Inserted: ";
    final String SPACES = "   ";
    boolean isFailed = false;
    boolean debugFlag = false;
    boolean isConnected = false;
    Date _now = new Date();
    final float _unk = (float)100000000000000000000000000000000.0; 
    final float _na = (float)-100000000000000000000000000000000.0;

    private Map<String, String> standardValueMap;
    private IngestReport _report;
    private Label _label;
    
	/**
	 * Constructor
	 *
	 * @param user Name of the database user
	 * @param pass Password of the database connection
	 * @param server Server name of the database
	 * @param dbname Name of the database
	 */
	public CatalogDB(String user, String pass, String server, String dbname, Target target, IngestReport report) throws SQLException, Exception{
		standardValueMap = new HashMap<String, String>();
		URI targetUri = null;
        setReport(report);
        try {
        	if (target.isDirectory()) {
    			targetUri = Utility.toURL(target.toString()).toURI();   
            }
            else {
            	targetUri = target.toURL().toURI();
            }
        	
        	String url = "jdbc:sybase:Tds:" + server + ":4100";
            Class.forName("com.sybase.jdbc3.jdbc.SybDriver");
            _conn = DriverManager.getConnection(url, user, pass);
			if (_conn!=null) {
				_conn.setCatalog(dbname);
				isConnected = true;
			}
        }
        catch (SQLException e) {
        	//e.printStackTrace();
        	throw e;
        }
        catch (Exception e) {			
        	throw e;
        }
    }
	
	public void setReport(IngestReport report) {
		this._report = report;
	}
	
	public void setLabel(Label label) {
		this._label = label;
	}

	/**
	 * Method to close the database connection
	 */
    public void close() throws SQLException {
        try {
			if (_conn!=null) {
            	_conn.close();
			}
        } 
        catch (SQLException e) {
        	throw e;
        }
    }
    
    public boolean isConnected() {
    	return isConnected;
    }
    
    public boolean isFailed() {
    	return isFailed;
    }
    
    public void setIsFailed(boolean failed) {
    	isFailed = failed;
    }
    
	/**
	 * Method to ingest MISSION object
	 *
	 * @param lblMap Hashmap of the PDS label(s)
	 */
    protected void ingestMissionObject(Map<String,AttributeStatement> lblMap) {
        ingestMsnD(lblMap);
        ingestMsnObjsmy(lblMap);
        ingestMsnInfo(lblMap);
    }

	/** 
	 * Method to ingest the data into 'msnd' table
	 *
	 * @param lblMap Hashmap of the PDS label(s)
	 */
    protected void ingestMsnD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        int count = 0;
        int delete = 0;
        String missionName = null;
        if (lblMap.get("MISSION_NAME")!=null)  
            missionName = collapse(lblMap.get("MISSION_NAME").getValue().toString());
        else {
        	Object[] arguments = { "msnd", "MISSION_NAME" };
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	        _label.addProblem(lp);
			isFailed = true;
			failCount++;
			return;
        }

        try {
        	INGEST_MSG = "Inserted: ";
            // select msnd.* from msnd where msnd.msnname = get("MISSION_NAME")
            // if there is a record then delete it before inserting
            // delete from msnd where msnd.msnname = get("MISSION_NAME")
            _sql = "SELECT msnd.* FROM msnd WHERE msnd.msnname = '" + missionName + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM msnd WHERE msnd.msnname = '" + missionName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            AttributeStatement asmt = lblMap.get("MISSION_DESC");
            if (asmt!=null) {
                if (delete!=0 || count==0) {
					// insert a record for each line of mission description
                    _sql = "INSERT INTO msnd (msnname, msnd, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] descStr = rtrim(asmt.getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        // add each line as one record .....increment tupseqnum for each line
                        pstmt.setString(1, missionName);
                        pstmt.setString(2, descStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "msnd - " + missionName + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "msnd", "MISSION_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
				isFailed = true;
				failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestMsnD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'msnobjsmy' table
	 *
	 */
    protected void ingestMsnObjsmy(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        int count = 0;
        int delete = 0;
        String missionName = null;
        if (lblMap.get("MISSION_NAME")!=null)
            missionName = collapse(lblMap.get("MISSION_NAME").getValue().toString());
        else {
        	Object[] arguments = { "msnobjsmy", "MISSION_NAME" };	
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
                    null, "ingest.error.missingKeyword",
                    ProblemType.MISSING_MEMBER, arguments);
            _label.addProblem(lp);
        	isFailed = true;
    		failCount++;
    		return;
        }

        try {
        	INGEST_MSG = "Inserted: ";
            // select msnobjsmy.* from msnobjsmy where msnobjsmy.msnname = get("MISSION_NAME")
            // delete from msnobjsmy where msnobjsmy.msnname = get("MISSION_NAME")
            _sql = "SELECT msnobjsmy.* FROM msnobjsmy WHERE msnobjsmy.msnname = '" + missionName + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM msnobjsmy WHERE msnobjsmy.msnname = '" + missionName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            AttributeStatement asmt = lblMap.get("MISSION_OBJECTIVES_SUMMARY");
            if (asmt!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO msnobjsmy (msnname, msnobjsmy, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] objsmy = rtrim(asmt.getValue().toString()).split("\n");
                    for (int i=0; i<objsmy.length; i++) {
                        pstmt.setString(1, missionName);
                        pstmt.setString(2, objsmy[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "msnobjsmy - " + missionName + " (" + objsmy.length + " rows)");
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "msnobjsmy", "MISSION_OBJECTIVES_SUMMARY" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        } 
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestMsnObjsmy");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'msnhost' table
	 */
    protected void ingestMsnHost(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        int count = 0;
        int delete = 0;
        String missionName = null;
        if (lblMap.get("MISSION_NAME")!=null)
            missionName = collapse(lblMap.get("MISSION_NAME").getValue().toString());
        else {
        	Object[] arguments = { "msnhost", "MISSION_NAME" };
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
                    null, "ingest.error.missingKeyword",
                    ProblemType.MISSING_MEMBER, arguments);
            _label.addProblem(lp);
            isFailed = true;
    		failCount++;
    		return;
        }
        try {
        	INGEST_MSG = "Inserted: ";
            // select msnhost.* from msnhost where msnhost.msnname = get("MISSION_NAME") and msnhost.insthostid = get("INSTRUMENT_HOST_ID")
            // delete from msnhost where msnhost.msnname = get("MISSION_NAME") and msnhost.insthostid = get("INSTRUMENT_HOST_ID")
            String instHostId = null;
            if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
                instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
			_sql = "SELECT msnhost.* FROM msnhost WHERE msnhost.msnname = '" + missionName + 
                   "' AND msnhost.insthostid = '" + instHostId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM msnhost WHERE msnhost.msnname = '" + missionName + 
					   "' AND msnhost.insthostid = '" + instHostId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            String targetName = null;
            if (lblMap.get("TARGET_NAME")!=null)
                targetName = lblMap.get("TARGET_NAME").getValue().toString();

            if (instHostId==null || targetName==null) {
            	Object[] arguments = { "msnhost", "INSTRUMENT_HOST_ID,TARGET_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO msnhost (msnname, insthostid, targname, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    pstmt.setString(1, missionName);
                    pstmt.setString(2, instHostId);
                    pstmt.setString(3, targetName);
                    pstmt.setString(4, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "msnhost - " + instHostId + " / " + targetName);
		            _label.addProblem(lp);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestMsnHost");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'msninfo' table
	 */
    protected void ingestMsnInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        int count = 0;
        int delete = 0;
        String missionName = null;
        if (lblMap.get("MISSION_NAME")!=null)
            missionName = collapse(lblMap.get("MISSION_NAME").getValue().toString());
        else {
        	Object[] arguments = { "msninfo", "MISSION_NAME" };
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
                    null, "ingest.error.missingKeyword",
                    ProblemType.MISSING_MEMBER, arguments);
            _label.addProblem(lp);
            isFailed = true;
    		failCount++;
    		return;
        }
        try {
        	INGEST_MSG = "Inserted: ";
            // select msninfo.* from msninfo where msninfo.msnname = get("MISSION_NAME")
            // delete from msninfo where msninfo.msnname = get("MISSION_NAME")
            _sql = "SELECT msninfo.* FROM msninfo WHERE msninfo.msnname = '" + missionName + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM msninfo WHERE msninfo.msnname = '" + missionName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            		
            String msnStartDate = null;
            String msnStopDate = null;
            String msnAliasName = null;
            String lblRevisionNote = null;
            if (lblMap.get("MISSION_START_DATE")!=null)
                msnStartDate = lblMap.get("MISSION_START_DATE").getValue().toString();
            if (lblMap.get("MISSION_STOP_DATE")!=null)
                msnStopDate = lblMap.get("MISSION_STOP_DATE").getValue().toString();
            if (lblMap.get("MISSION_ALIAS_NAME")!=null)
                msnAliasName = collapse(lblMap.get("MISSION_ALIAS_NAME").getValue().toString());
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();

            if (msnStartDate==null || msnStopDate==null || msnAliasName==null || lblRevisionNote==null) {
            	Object[] arguments = { "msninfo", "MISSION_START_DATE,MISSION_STOP_DATE,MISSION_ALIAS_NAME,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO msninfo (msnname, msnstrtdate, msnstopdate, msnaliasname, labelrevnote, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "mission name = " + missionName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "label revision date = " + lblRevisionNote));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "mission start date = " + msnStartDate));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "mission stop date = " + msnStopDate));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "mission alias name = " + msnAliasName));
                    */
                    pstmt.setString(1, missionName);
                    pstmt.setString(2, msnStartDate);
                    pstmt.setString(3, msnStopDate);
                    pstmt.setString(4, msnAliasName);
                    pstmt.setString(5, lblRevisionNote);
                    pstmt.setString(6, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(7, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "msninfo - " + missionName);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("msinfo.msnname", missionName);
                    standardValueMap.put("msninfo.msnaliasname", msnAliasName);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestMsnInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'msndoc' table
	 */
    protected void ingestMsnDoc(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        Statement stmt = null;
        int count = 0;
        int delete = 0;
        String missionName = null;
        if (lblMap.get("MISSION_NAME")!=null)
            missionName = collapse(lblMap.get("MISSION_NAME").getValue().toString());
        else {
        	Object[] arguments = { "msndoc", "MISSION_NAME" };
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
                    null, "ingest.error.missingKeyword",
                    ProblemType.MISSING_MEMBER, arguments);
            _label.addProblem(lp);
            isFailed = true;
    		failCount++;
    		return;
        }
        try {
        	INGEST_MSG = "Inserted: ";
            // optional
            // select msndoc.* from msndoc where msndoc.msnname = get("MISSION_NAME")
            // on the 1st pass, delete all of the records, then begin inserting new records
            // delete from msndoc where msndoc.msnname = get("MISSION_NAME")
            _sql = "SELECT msndoc.* FROM msndoc WHERE msndoc.msnname = '" + missionName  + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM msndoc WHERE msndoc.msnname = '" + missionName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            String refkeyid = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null) 
                refkeyid = lblMap.get("REFERENCE_KEY_ID").getValue().toString();
            if (refkeyid !=null) {
                if (delete!=0 || count==0) {
                    _sql = " INSERT INTO msndoc (msnname, refkeyid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "ref key id = " + refkeyid));
                    pstmt.setString(1, missionName);
                    pstmt.setString(2, refkeyid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "msndoc - " + refkeyid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "msndoc", "REFERENCE_KEY_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestMsnDoc");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest INSTRUMENT_HOST object
	 */
    protected  void ingestInstHostObject(Map<String,AttributeStatement> lblMap) {
        ingestHostD(lblMap);
        ingestHostInfo(lblMap);
    }

	/**
	 * Method to ingest the data into 'hostd' table
	 */
    protected void ingestHostD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int rowCount = 0;
        int delete = 0;
        String instHostId = null;
        if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
        	instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
        else {
        	Object[] arguments = { "hostd", "INSTRUMENT_HOST_ID" };
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
                    null, "ingest.error.missingKeyword",
                    ProblemType.MISSING_MEMBER, arguments);
            _label.addProblem(lp);
            isFailed = true;
    		failCount++;
    		return;
        }
        try {
        	INGEST_MSG = "Inserted: ";
            // select hostd.* from hostd where hostd.insthostid = get("INSTRUMENT_HOST_ID")
            // delete from hostd where hostd.insthostid = get("INSTRUMENT_HOST_ID")
            _sql = "SELECT hostd.* FROM hostd WHERE hostd.insthostid = '" + instHostId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM hostd WHERE hostd.insthostid = '" + instHostId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("INSTRUMENT_HOST_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO hostd (insthostid, insthostd, tupseqnum, userid, revdate) " +
                        "VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "instrument host id = " + instHostId));
                    String[] descStr = rtrim(lblMap.get("INSTRUMENT_HOST_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst host desc " + i + "  value = " + descStr[i]));
                        pstmt.setString(1, instHostId);
                        pstmt.setString(2, descStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "hostd - " + instHostId + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "hostd", "INSTRUMENT_HOST_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestHostD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'hostinfo' table
	 */
    protected void ingestHostInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int rowCount = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String instHostId = null;
        	if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
            	instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
            else {
            	Object[] arguments = { "hostinfo", "INSTRUMENT_HOST_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            }          
            // select hostinfo.* from hostinfo where hostinfo.insthostid = get("INSTRUMENT_HOST_ID")
            // delete from hostinfo where hostinfo.insthostid = get("INSTRUMENT_HOST_ID")
            _sql = "SELECT hostinfo.* FROM hostinfo WHERE hostinfo.insthostid = '" + instHostId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM hostinfo WHERE hostinfo.insthostid = '" + instHostId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }

            String instHostName = null;
            String instHostType = null;
            String lblRevisionNote = null;
            if (lblMap.get("INSTRUMENT_HOST_NAME")!=null)
                instHostName = collapse(lblMap.get("INSTRUMENT_HOST_NAME").getValue().toString());            
            if (lblMap.get("INSTRUMENT_HOST_TYPE")!=null)
                instHostType = lblMap.get("INSTRUMENT_HOST_TYPE").getValue().toString();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();
            if (instHostName==null || instHostType==null || lblRevisionNote==null) {
            	/*
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "ABORT: HOSTINFO - one or more of the required keywords was not found."));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - INSTRUMENT_HOST_ID: " + instHostId));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - INSTRUMENT_HOST_NAME: " + instHostName));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - INSTRUMENT_HOST_TYPE: " + instHostType));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - LABEL_REVISION_NOTE: " + lblRevisionNote));
                */
            	Object[] arguments = { "hostinfo", "INSTRUMENT_HOST_NAME,INSTRUMENT_HOST_TYPE,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO hostinfo (insthostid, insthostname, insthosttype, labelrevnote, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, instHostId);
                    pstmt.setString(2, instHostName);
                    pstmt.setString(3, instHostType);
                    pstmt.setString(4, lblRevisionNote);
                    pstmt.setString(5, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(6, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "hostinfo - " + instHostId);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("hostinfo.insthostid", instHostId);
                    standardValueMap.put("hostinfo.insthostname", instHostName);
                    standardValueMap.put("hostinfo.insthosttype", instHostType);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestHostInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'hostdoc' table
	 */
    protected void ingestInstHostRefInfoObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String instHostId = null;
        	if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
            	instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
            String refkeyid = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null)
                refkeyid = lblMap.get("REFERENCE_KEY_ID").getValue().toString();
            
            if (instHostId==null) {
            	Object[] arguments = { "hostdoc", "INSTRUMENT_HOST_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
	            isFailed = true;
                failCount++;
            }
            else if (refkeyid==null) {
            	Object[] arguments = { "hostdoc", "REFERENCE_KEY_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// optional
                // select hostdoc.* from hostdoc where hostdoc.insthostid = get("INSTRUMENT_HOST_ID")
                // delete from hostdoc where hostdoc.insthostid = get("INSTRUMENT_HOST_ID")
                _sql = "SELECT hostdoc.* FROM hostdoc WHERE hostdoc.insthostid = '" + instHostId + 
                    "' AND hostdoc.refkeyid = '" + refkeyid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM hostdoc WHERE hostdoc.insthostid = '" + instHostId + 
                        "' AND hostdoc.refkeyid = '" + refkeyid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO hostdoc (insthostid, refkeyid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "reference key id = " + refkeyid));
                    pstmt.setString(1, instHostId);
                    pstmt.setString(2, refkeyid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "hostdoc - " + refkeyid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        } 
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestHostDoc");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/** 
	 * Method to ingest INSTRUMENT object
	 */
    protected void ingestInstObject(Map<String,AttributeStatement> lblMap) {
        ingestInstD(lblMap);
        ingestInstInfo(lblMap); 
    }

	/**
	 * Method to ingest the data into 'instd' table
	 */
    protected void ingestInstD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String instId = null;
            String instHostId = null;
        	if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
            	instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
            else {
            	Object[] arguments = { "instd", "INSTRUMENT_HOST_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 
            if (lblMap.get("INSTRUMENT_ID")!=null)
                instId = lblMap.get("INSTRUMENT_ID").getValue().toString();
            else {
            	Object[] arguments = { "instd", "INSTRUMENT_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            }
            // select instd.* from instd where instd.insthostid = get("INSTRUMENT_HOST_ID") and instd.instid = get("INSTRUMENT_ID")
            // delete from instd where instd.insthostid = get("INSTRUMENT_HOST_ID") and instd.instid = get("INSTRUMENT_ID")
            _sql = "SELECT instd.* FROM instd WHERE instd.insthostid = '" + instHostId + "' AND instd.instid = '" +
                instId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM instd WHERE instd.insthostid = '" + instHostId + "' AND instd.instid = '" + instId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("INSTRUMENT_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO instd (insthostid, instid, instd, tupseqnum, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst host id = " + instHostId));
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst id = " + instId));
                    String[] descStr = rtrim(lblMap.get("INSTRUMENT_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst desc " + i + "   " + descStr[i]));
                        pstmt.setString(1, instHostId);
                        pstmt.setString(2, instId);
                        pstmt.setString(3, descStr[i]);
                        pstmt.setShort(4, (short) (i+1));
                        pstmt.setString(5, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(6, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded", ProblemType.SUCCEED, 
		                    INGEST_MSG + "instd - " + instHostId + " / " + instId + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "instd", "INSTRUMENT_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestInstD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'instinfo' table
	 */
    protected void ingestInstInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;        
        try {
        	INGEST_MSG = "Inserted: ";
        	String instId = null;
            String instHostId = null;
        	if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
            	instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
            else {
            	Object[] arguments = { "instinfo", "INSTRUMENT_HOST_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 
            if (lblMap.get("INSTRUMENT_ID")!=null)
                instId = lblMap.get("INSTRUMENT_ID").getValue().toString();
            else {
            	Object[] arguments = { "instinfo", "INSTRUMENT_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            }
            // select instinfo.* from instinfo where instinfo.insthostid = get("INSTRUMENT_HOST_ID") and instinfo.instid = get("INSTRUMENT_ID")
            // delete from instinfo where instinfo.insthostid = get("INSTRUMENT_HOST_ID") and instinfo.instid = get("INSTRUMENT_ID")
            _sql = "SELECT instinfo.* FROM instinfo WHERE instinfo.insthostid = '" + instHostId + 
                "' AND instinfo.instid = '" + instId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM instinfo WHERE instinfo.insthostid = '" + instHostId + "' AND instinfo.instid = '" + instId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }

            String instName = null;
            String instType = null;
            String lblRevisionNote = null;
            // need to capture \r\n and remove beginning spaces from second line
            if (lblMap.get("INSTRUMENT_NAME")!=null)
                instName = collapse(lblMap.get("INSTRUMENT_NAME").getValue().toString());
            if (lblMap.get("INSTRUMENT_TYPE")!=null)
                instType = lblMap.get("INSTRUMENT_TYPE").getValue().toString();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();

            if (instName==null || instType==null || lblRevisionNote==null) {
            	/*
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "ABORT: INSTINFO - one or more of the required keywords was not found."));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - INSTRUMENT_HOST_ID: " + instHostId));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - INSTRUMENT_NAME: " + instName));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - INSTRUMENT_TYPE: " + instType));
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE, SPACES + "                - LABEL_REVISION_NOTE: " + lblRevisionNote));
                */
            	Object[] arguments = { "instinfo", "INSTRUMENT_NAME,INSTRUMENT_TYPE,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO instinfo (insthostid, instid, instname, insttype, labelrevnote, userid, revdate) " +
                        "VALUES(?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, instHostId);
                    pstmt.setString(2, instId);
                    pstmt.setString(3, instName);
                    pstmt.setString(4, instType);
                    pstmt.setString(5, lblRevisionNote);
                    pstmt.setString(6, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(7, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "instinfo - " + instHostId + " / " + instId);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("instinfo.instid", instId);
                    standardValueMap.put("instinfo.instname", instName);
                    standardValueMap.put("instinfo.insttype", instType);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestInstInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'instdoc' table
	 */
    protected void ingestInstRefInfoObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String instId = null;
            String instHostId = null;
        	if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
            	instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString(); 
            if (lblMap.get("INSTRUMENT_ID")!=null)
                instId = lblMap.get("INSTRUMENT_ID").getValue().toString();   
            String refkeyId = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null)
            	refkeyId = lblMap.get("REFERENCE_KEY_ID").getValue().toString();
            
            if (instHostId==null || instId==null || refkeyId==null) {
            	Object[] arguments = { "instdoc", "INSTRUMENT_HOST_ID,INSTRUMENT_ID,REFERENCE_KEY_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// optional
                // select instdoc.* from instdoc where instdoc.insthostid = get("INSTRUMENT_HOST_ID") and 
                // instdoc.instid = get("INSTRUMENT_ID") and instdoc.refkeyid = get("REFERENCE_KEY_ID")
                // delete from instdoc where instdoc.insthostid = get("INSTRUMENT_HOST_ID") and instdoc.instid = get("INSTRUMENT_ID")
            	_sql = "SELECT instdoc.* FROM instdoc WHERE instdoc.insthostid = '" + instHostId +
                	"' AND instdoc.instid = '" + instId + "' AND instdoc.refkeyid = '" + refkeyId + "'";
            	count = getRowCount(_sql);
            	if (count!=0) {
            		_sql = "DELETE FROM instdoc WHERE instdoc.insthostid = '" + instHostId + "' AND instdoc.instid = '" + 
                    	instId + "' AND instdoc.refkeyid = '" + refkeyId + "'";
            		delete = deleteRecords(_sql);
            		INGEST_MSG = "Updated: ";
            	}
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO instdoc (insthostid, instid, refkeyid, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst host id = " + instHostId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst id = " + instId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "ref key id = " + refkeyId));
                    */
                    pstmt.setString(1, instHostId);
                    pstmt.setString(2, instId);
                    pstmt.setString(3, refkeyId);
                    pstmt.setString(4, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "instdoc - " + refkeyId);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }    
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestInstDoc");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest DATA_SET object
	 */
    protected void ingestDataSetObject(Map<String,AttributeStatement> attrSmts) {  
    	ingestDSD(attrSmts);
        ingestDSConf(attrSmts);
        ingestDSTarg(attrSmts);
        ingestDSMsn(attrSmts);
        ingestDSInfo(attrSmts);
    }

	/**
	 * Method to ingest the data into 'dsmsn' table
	 */
    protected void ingestDSMsn(Map<String,AttributeStatement> attrSmts) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (attrSmts.get("DATA_SET_ID")!=null)
                dsId = collapse(attrSmts.get("DATA_SET_ID").getValue().toString());
            String missionName = null;
            if (attrSmts.get("MISSION_NAME")!=null) 
                missionName = (attrSmts.get("MISSION_NAME").getValue().toString());
 
            if (dsId==null || missionName==null) {
            	Object[] arguments = { "dsmsn", "DATA_SET_ID,MISSION_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select dsmsn.* from dsmsn where dsmsn.dsid = get("DATA_SET_ID") and dsmsn.msnname = get("MISSION_NAME")
                // delete from dsmsn where dsmsn.dsid = get("DATA_SET_ID") and dsmsn.msnname = get("MISSION_NAME")
                _sql = "SELECT dsmsn.* FROM dsmsn WHERE dsmsn.dsid = '" + dsId + "' AND dsmsn.msnname = '" + missionName + "'";
                count = getRowCount(_sql);

                if (count!=0) {
                    _sql = "DELETE FROM dsmsn WHERE dsmsn.dsid = '" + dsId + "' AND dsmsn.msnname = '" + missionName + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";             
                }
                if (delete!=0 || count==0) { 
                    _sql = "INSERT INTO dsmsn (dsid, msnname, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, dsId);
                    pstmt.setString(2, missionName);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsmsn - " + dsId + " / " + missionName);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSMsn");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
			e.printStackTrace();
        }
    }

	/**
	 * Method to ingest the data into 'dsd' table
	 */
    protected void ingestDSD(Map<String,AttributeStatement> attrSmts) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (attrSmts.get("DATA_SET_ID")!=null) {
            	dsId = collapse(attrSmts.get("DATA_SET_ID").getValue().toString());
            }
            else {
            	Object[] arguments = { "dsd", "DATA_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 

            // select dsd.* from dsd where dsd.dsid = get("DATA_SET_ID")
            // delete from dsd where dsd.dsid = get("DATA_SET_ID")
            _sql = "SELECT dsd.* FROM dsd WHERE dsd.dsid = '" + dsId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dsd WHERE dsd.dsid = '" + dsId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            AttributeStatement asmt = attrSmts.get("DATA_SET_DESC");
            if (asmt!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dsd (dsid, dsd, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] descStr = rtrim(asmt.getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "data set desc " + i + "    value = " + descStr[i]));
                        pstmt.setString(1, dsId);
                        if (rtrim(descStr[i]).length()>80) {                  	
                        	Object[] arguments = { (asmt.getLineNumber()+i), "DATA_SET_DESC", rtrim(descStr[i]).length(), 80 };
                        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
                        			"parser.error.lineTooLong2", ProblemType.EXCESSIVE_LINE_LENGTH, arguments);
            	            _label.addProblem(lp);
                        }
                        pstmt.setString(2, descStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsd - " + dsId + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "dsd", "DATA_SET_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'dsconf' table
	 */
    protected void ingestDSConf(Map<String,AttributeStatement> attrSmts) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (attrSmts.get("DATA_SET_ID")!=null)
                dsId = collapse(attrSmts.get("DATA_SET_ID").getValue().toString());
            else {
            	Object[] arguments = { "dsconf", "DATA_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 

            // select dsconf.* from dsconf where dsconf.dsid = get("DATA_SET_ID")
            // delete  from dsconf where dsconf.dsid = get("DATA_SET_ID")
            _sql = "SELECT dsconf.* FROM dsconf WHERE dsconf.dsid = '" + dsId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dsconf WHERE dsconf.dsid = '" + dsId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            AttributeStatement asmt = null;
            if (attrSmts.get("CONFIDENCE_LEVEL_NOTE")!=null) {
            	asmt = attrSmts.get("CONFIDENCE_LEVEL_NOTE");
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dsconf (dsid, conflvlnote, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] conflvnote = rtrim(asmt.getValue().toString()).split("\n");
                    for (int i=0; i<conflvnote.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "conf lv note " + i + "  value = " + conflvnote[i]));
                        pstmt.setString(1, dsId);
                        if (conflvnote[i].length()>80) {
                        	Object[] arguments = { (asmt.getLineNumber()+i), "CONFIDENCE_LEVEL_NOTE", rtrim(conflvnote[i]).length(), 80 };
                        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
                        			"parser.error.lineTooLong2", ProblemType.EXCESSIVE_LINE_LENGTH, arguments);
            	            _label.addProblem(lp);
                        }
                        pstmt.setString(2, conflvnote[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsconf - " + dsId + " (" + conflvnote.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "dsconf", "CONFIDENCE_LEVEL_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSConf");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'dstarg' table
	 */
    protected void ingestDSTarg(Map<String,AttributeStatement> attrSmts) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (attrSmts.get("DATA_SET_ID")!=null) {
            	dsId = collapse(attrSmts.get("DATA_SET_ID").getValue().toString());
            }
            else {
            	Object[] arguments = { "dstarg", "DATA_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 

            // select dstarg.* from dstarg where dstarg.sdid = get("DATA_SET_ID")
            // delete from dstarg where dstarg.dsid = get("DATA_SET_ID")
            _sql = "SELECT dstarg.* FROM dstarg WHERE dstarg.dsid = '" + dsId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dstarg WHERE dstarg.dsid = '" + dsId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String targetName = null;
            if (attrSmts.get("TARGET_NAME")!=null) {
                targetName = attrSmts.get("TARGET_NAME").getValue().toString();
                
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dstarg (dsid, targname, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, dsId);
                    pstmt.setString(2, targetName);
                    pstmt.setString(3, _dename); 
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dstarg - " + targetName);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "dstarg", "TARGET_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSTarg");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'dshost' table
	 */
    protected void ingestDSHost(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (lblMap.get("DATA_SET_ID")!=null) 
            	dsId = collapse(lblMap.get("DATA_SET_ID").getValue().toString());
            String instHostId = null;
            String instId = null;
            if (lblMap.get("INSTRUMENT_HOST_ID")!=null)
                instHostId = lblMap.get("INSTRUMENT_HOST_ID").getValue().toString();
            if (lblMap.get("INSTRUMENT_ID")!=null)
                instId = lblMap.get("INSTRUMENT_ID").getValue().toString();

            if (dsId==null || instHostId==null || instId==null) {
            	Object[] arguments = { "dshot", "DATA_SET_ID,INSTRUMENT_HOST_ID,INSTRUMENT_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select dshost.* from dshost where dshost.dsid = get("DATA_SET_ID")
                // delete from dshost where dshost.dsid = get("DATA_SET_ID")
                _sql = "SELECT dshost.* FROM dshost WHERE dshost.dsid = '" + dsId + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM dshost WHERE dshost.dsid = '" + dsId + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dshost (dsid, insthostid, instid, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, dsId);
                    pstmt.setString(2, instHostId);
                    pstmt.setString(3, instId);
                    pstmt.setString(4, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dshost - " + instHostId + " / " + instId);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSHost");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/** 
	 * Method to ingest the data into 'dsinfo' table
	 */
    protected void ingestDSInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (lblMap.get("DATA_SET_ID")!=null) {
            	dsId = collapse(lblMap.get("DATA_SET_ID").getValue().toString());
            }
            else {
            	Object[] arguments = { "dsinfo", "DATA_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            }

            // select dsinfo.* from dsinfo where dsinfo.dsid = get("DATA_SET_ID")
            // delete from dsinfo where dsinfo.dsid = get("DATA_SET_ID")
            _sql = "SELECT dsinfo.* FROM dsinfo WHERE dsinfo.dsid = '" + dsId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dsinfo WHERE dsinfo.dsid = '" + dsId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String dsName = null;
            String startTime = null;
            String stopTime = null;
            String dataObjType = null;
            String dsReleaseDate = null;
            String archiveStatus = null;
            String curatingNodeId = null;
            String producerFullname = null;
            String detailedCatalogFlag = null;
            String dsCollMemberFlag = null;
            String dsTerseDesc = null;
            String lblRevisionNote = null;
            String citationDesc = null;
            String abstractDesc = null;
            String missionName = null;
            if (lblMap.get("DATA_SET_NAME")!=null) 
                dsName = collapse(lblMap.get("DATA_SET_NAME").getValue().toString());
            if (lblMap.get("START_TIME")!=null)
                startTime = lblMap.get("START_TIME").getValue().toString();
            if (lblMap.get("STOP_TIME")!=null)
                stopTime = lblMap.get("STOP_TIME").getValue().toString();
            if (lblMap.get("DATA_OBJECT_TYPE")!=null)
                dataObjType = lblMap.get("DATA_OBJECT_TYPE").getValue().toString();
            if (lblMap.get("DATA_SET_RELEASE_DATE")!=null)
                dsReleaseDate = lblMap.get("DATA_SET_RELEASE_DATE").getValue().toString();
            if (lblMap.get("ARCHIVE_STATUS")!=null)
                archiveStatus = lblMap.get("ARCHIVE_STATUS").getValue().toString();
            if (lblMap.get("CURATING_NODE_ID")!=null)
                curatingNodeId = lblMap.get("CURATING_NODE_ID").getValue().toString();
            if (lblMap.get("PRODUCER_FULL_NAME")!=null)
                producerFullname = lblMap.get("PRODUCER_FULL_NAME").getValue().toString();
            if (lblMap.get("DETAILED_CATALOG_FLAG")!=null)
                detailedCatalogFlag = lblMap.get("DETAILED_CATALOG_FLAG").getValue().toString();
            if (lblMap.get("DATA_SET_COLLECTION_MEMBER_FLG")!=null)
                dsCollMemberFlag = lblMap.get("DATA_SET_COLLECTION_MEMBER_FLG").getValue().toString();
            if (lblMap.get("DATA_SET_TERSE_DESC")!=null) {	
                dsTerseDesc = lblMap.get("DATA_SET_TERSE_DESC").getValue().toString().trim();
                if (dsTerseDesc.length()>256) {
                	AttributeStatement asmt = null;
                    asmt = lblMap.get("DATA_SET_TERSE_DESC");
                    Object[] arguments = { asmt.getLineNumber(), "DATA_SET_TERSE_DESC", dsTerseDesc.length(), 255 };
                	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
                			"parser.error.lineTooLong2", ProblemType.EXCESSIVE_LINE_LENGTH, arguments);
                	_label.addProblem(lp);
                }
            }
            if (lblMap.get("LABEL_REVISION_NOTE")!=null) 
                lblRevisionNote = ltrim(lblMap.get("LABEL_REVISION_NOTE").getValue().toString());
            if (lblMap.get("CITATION_DESC")!=null)
                citationDesc = ltrim(lblMap.get("CITATION_DESC").getValue().toString());
            if (lblMap.get("ABSTRACT_DESC")!=null)
                abstractDesc = ltrim(lblMap.get("ABSTRACT_DESC").getValue().toString());          
            if (lblMap.get("MISSION_NAME")!=null)
                 missionName = collapse(lblMap.get("MISSION_NAME").getValue().toString());

            if (archiveStatus == null) {
            	//log.log(new ToolsLogRecord(CIToolLevel.WARNING, SPACES + "WARNING: DSINFO - ARCHIVE_STATUS: " + archiveStatus));
            }

            if (dsName==null || dsId==null || startTime==null || stopTime==null || dsTerseDesc==null || 
                dsCollMemberFlag==null || dataObjType==null || dsReleaseDate==null || producerFullname==null ||
                detailedCatalogFlag==null || missionName==null || lblRevisionNote==null ||
                citationDesc==null || abstractDesc==null) {
            	Object[] arguments = { "dsinfo", "DATA_SET_NAME,DATA_SET_ID,START_TIME,STOP_TIME,DATA_SET_COLLECTION_MEMBER_FLG," +
            			"DATA_OBJECT_TYPE,DATA_SET_RELEASE_DATE,PRODUCER_FULL_NAME,DETAILED_CATALOG_FLAG,MISSION_NAME," +
            			"LABEL_REVISION_NOTE,DATA_SET_TERSE_DESC,CITATION_DESC,ABSTRACT_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dsinfo (dsid, dsname, strttime, stoptime, dataobjtype, dsreleasedt, archivestat, curatingndid," + 
                        "      prodfullname, detailcatflg, dscollmemflg, dstersedesc, labelrevnote,citdesc, abstract, userid, revdate) " +
                        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, dsId);
                    pstmt.setString(2, dsName);
                    pstmt.setString(3, startTime);
                    pstmt.setString(4, stopTime);
                    pstmt.setString(5, dataObjType);
                    pstmt.setString(6, dsReleaseDate);
                    pstmt.setString(7, archiveStatus);
                    pstmt.setString(8, curatingNodeId);
                    pstmt.setString(9, producerFullname);
                    pstmt.setString(10, detailedCatalogFlag);
                    pstmt.setString(11, dsCollMemberFlag);
                    // need to remove extra spaces - hhl
                    pstmt.setString(12, dsTerseDesc);
                    pstmt.setString(13, lblRevisionNote);
                    pstmt.setString(14, citationDesc);
                    // remove extra spaces - hhl
                    pstmt.setString(15, abstractDesc);
                    pstmt.setString(16, _dename);   						                        // userid
                    _now = new Date();
                    pstmt.setTimestamp(17, new Timestamp(_now.getTime()));							// revdate

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsinfo - " + dsId);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("dsninfo.dscollmemflg", dsCollMemberFlag);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
   
	/** 
	 * Method to ingest the data into 'dsdoc' table
	 */
    protected void ingestDSRefInfoObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsId = null;
            if (lblMap.get("DATA_SET_ID")!=null)
                dsId = collapse(lblMap.get("DATA_SET_ID").getValue().toString());
            String refkeyid = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null)
                refkeyid = lblMap.get("REFERENCE_KEY_ID").getValue().toString();
            
            if (dsId==null || refkeyid==null) {
            	Object[] arguments = { "dsdoc", "DATA_SET_ID,REFERENCE_KEY_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// optional
                // select dsdoc.* from dsdoc where dsdoc.dsid = get("DATA_SET_ID")
                // delete from dsdoc where dsdoc.dsid = get("DATA_SET_ID")
                // there are more than one reference key....
                _sql = "SELECT dsdoc.* FROM dsdoc WHERE dsdoc.dsid = '" + dsId + "' AND dsdoc.refkeyid = '" + refkeyid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM dsdoc WHERE dsdoc.dsid = '" + dsId + "' AND dsdoc.refkeyid = '" + refkeyid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                
                if (delete!=0 || count==0) { 
                    _sql = "INSERT INTO dsdoc (dsid, refkeyid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, dsId);
                    pstmt.setString(2, refkeyid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));
    
                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsdoc - " + refkeyid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSRefInfoObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'refd' table
	 */
    public void ingestReferenceObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String refKeyId = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null)
            	refKeyId = lblMap.get("REFERENCE_KEY_ID").getValue().toString();        
            else {
            	Object[] arguments = { "refd", "REFERENCE_KEY_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 
            // select refd.* from refd where refd.refkeyid = get("REFERENCE_KEY_ID")
            // delete from refd where refd.refkeyid = get("REFERENCE_KEY_ID")
            _sql = "SELECT refd.* FROM refd WHERE refd.refkeyid = '" + refKeyId + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM refd WHERE refd.refkeyid = '" + refKeyId + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("REFERENCE_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO refd (refkeyid, refd, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    String[] descStr = rtrim(lblMap.get("REFERENCE_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        pstmt.setString(1, refKeyId);
                        pstmt.setString(2, descStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "refd - " + refKeyId);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "refd", "REFERENCE_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestReferenceObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest PERSONNEL object
	 */
    protected void ingestPersonnelObject(Map<String,AttributeStatement> lblMap) {
        ingestPersMailAddr(lblMap);
        ingestPersInfo(lblMap);
    }

	/**
	 * Method to ingest the data into 'persmailaddr' table
	 */
    protected void ingestPersMailAddr(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String pdsuserid = null;
            if (lblMap.get("PDS_USER_ID")!=null) {
            	pdsuserid = lblMap.get("PDS_USER_ID").getValue().toString();
            }
            else {
            	Object[] arguments = { "persmailaddr", "PDS_USER_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 

            // select persmailaddr.* from persmailaddr where persmailaddr.pdsuserid = get("PDS_USER_ID")
            // delete from permailaddr where persmailaddr.pdsuserid = get("PDS_USER_ID")
            _sql = "SELECT persmailaddr.* FROM persmailaddr WHERE persmailaddr.pdsuserid = '" + pdsuserid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM persmailaddr WHERE persmailaddr.pdsuserid = '" + pdsuserid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("ADDRESS_TEXT")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO persmailaddr (pdsuserid, addresstext, tupseqnum, userid, revdate) " + 
                        "VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    String[] addrText = (lblMap.get("ADDRESS_TEXT").getValue().toString()).trim().split("\n");
                    for (int i=0; i<addrText.length; i++) {
                        pstmt.setString(1, pdsuserid);
                        pstmt.setString(2, addrText[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "persmailaddr - " + pdsuserid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "persmailaddr", "ADDRESS_TEXT" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestPersMailAddr");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/** 
	 * Method to ingest the data into 'persinfo' table
	 */
    protected void ingestPersInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String pdsuserid = null;
        	if (lblMap.get("PDS_USER_ID")!=null)
        		pdsuserid = lblMap.get("PDS_USER_ID").getValue().toString();  
        	else {
        		Object[] arguments = { "persinfo", "PDS_USER_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
         		failCount++;
         		return;
        	}
            // select persinfo.* from persinfo where persinfo.pdsuserid = get("PDS_USER_ID")
            // delete from persinfo where persinfo.pdsuserid = get("PDS_USER_ID")
            _sql = "SELECT persinfo.* FROM persinfo WHERE persinfo.pdsuserid = '" + pdsuserid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM persinfo WHERE persinfo.pdsuserid = '" + pdsuserid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String lastName = null;
            String fullName = null;
            String phoneNum = null;
            String altPhoneNum = null;
            String faxNum = null;
            String institutionName = null;
            String nodeId = null;
            String registrationDate = null;
            // 0: PDS User    1: Data Provider    2: CN DE     3: Rls Mgr    4: Rls Admin
            String bookFlag = null;
            String pdsAffiliation = null;
            String lblRevisionNote = null;
            if (lblMap.get("LAST_NAME")!=null)
                lastName = lblMap.get("LAST_NAME").getValue().toString();
            if (lblMap.get("FULL_NAME")!=null)
                fullName = lblMap.get("FULL_NAME").getValue().toString();
            if (lblMap.get("TELEPHONE_NUMBER")!=null) 
                phoneNum = lblMap.get("TELEPHONE_NUMBER").getValue().toString();
            if (lblMap.get("ALTERNATE_TELEPHONE_NUMBER")!=null)
                altPhoneNum = lblMap.get("ALTERNATE_TELEPHONE_NUMBER").getValue().toString();
            if (lblMap.get("FAX_NUMBER")!=null)
                faxNum = lblMap.get("FAX_NUMBER").getValue().toString();
            if (lblMap.get("INSTITUTION_NAME")!=null) 
                institutionName = collapse(lblMap.get("INSTITUTION_NAME").getValue().toString());
            if (lblMap.get("NODE_ID")!=null)
                nodeId = collapse(lblMap.get("NODE_ID").getValue().toString());
            if (lblMap.get("REGISTRATION_DATE")!=null)
                registrationDate = collapse(lblMap.get("REGISTRATION_DATE").getValue().toString());
            if (lblMap.get("PDS_ADDRESS_BOOK_FLAG")!=null)
                bookFlag = lblMap.get("PDS_ADDRESS_BOOK_FLAG").getValue().toString();
            if (lblMap.get("PDS_AFFILIATION")!=null)
                pdsAffiliation = collapse(lblMap.get("PDS_AFFILIATION").getValue().toString());
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();

            _sql = "SELECT nodeinfo.* FROM nodeinfo WHERE nodeinfo.nodeid = '" + pdsAffiliation + "'";
            count = getRowCount(_sql);
            if (count==0) {
            	//log.log(new ToolsLogRecord(CIToolLevel.WARNING, SPACES + "WARNING: PERSINFO - " + pdsAffiliation + 
            	//		" is not a valid PDS_AFFILIATION (not found in nodeinfo table)."));
            }
            	
            if (lastName==null || fullName==null || phoneNum==null || altPhoneNum==null || faxNum==null || 
            	institutionName==null || nodeId==null || registrationDate==null || bookFlag==null || 
            	pdsAffiliation==null || lblRevisionNote==null) {
            	Object[] arguments = { "persinfo", "LAST_NAME,FULL_NAME,TELEPHONE_NUMBER,ALTERNATE_TELEPHONE_NUMBER,FAX_NUMBER," +
            			"INSTITUTION_NAME,NODE_ID,REGISTRATION_DATE,PDS_ADDRESS_BOOK_FLAG,PDS_AFFILIATION,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO persinfo (pdsuserid, lastname, fullname, telephonenum, altphonenum, faxnumber, instnname, " + 
                        "nodeid, regdate, addrbookflg, pdsaffil, labelrevnote, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "last name = " + lastName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "full name = " + fullName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "phone = " + phoneNum));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "alternate phone = " + altPhoneNum));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "fax = " + faxNum));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "inst name = " + institutionName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "node id = " + nodeId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "registration date = " + registrationDate));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "pds address book flag = " + bookFlag));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "pds affiliation = " + pdsAffiliation));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "label revision date = " + lblRevisionNote));
                    */
                    pstmt.setString(1, pdsuserid);
                    pstmt.setString(2, lastName);
                    pstmt.setString(3, fullName);
                    pstmt.setString(4, phoneNum);
                    pstmt.setString(5, altPhoneNum);
                    pstmt.setString(6, faxNum);
                    pstmt.setString(7, institutionName);
                    pstmt.setString(8, nodeId);
                    pstmt.setString(9, registrationDate);
                    pstmt.setString(10, bookFlag);
                    pstmt.setString(11, pdsAffiliation);
                    pstmt.setString(12, lblRevisionNote);
                    pstmt.setString(13, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(14, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "persinfo - " + pdsuserid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestPersInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'perselecmail' table
	 */
    protected void ingestPersElecmail(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String pdsuserid = null;
        	if (lblMap.get("PDS_USER_ID")!=null)
        		pdsuserid = lblMap.get("PDS_USER_ID").getValue().toString();  
        	else {
        		Object[] arguments = { "perselecmail", "PDS_USER_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
         		failCount++;
         		return;
        	}
        	String email = null;
        	if (lblMap.get("ELECTRONIC_MAIL_ID")!=null)
        		email = collapse(lblMap.get("ELECTRONIC_MAIL_ID").getValue().toString());
        	else {
        		Object[] arguments = { "perselecmail", "ELECTRONIC_MAIL_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
         		failCount++;
         		return;
        	}
            // select perselecmail.* from perselecmail where perselecmail.pdsuserid = get("PDS_USER_ID")
            // delete from perselecmail where perselecmail.pdsuserid = get("PDS_USER_ID")
            _sql = "SELECT perselecmail.* FROM perselecmail WHERE perselecmail.pdsuserid = '" + pdsuserid +
                "' AND perselecmail.elecmailid = '" + email + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM perselecmail WHERE perselecmail.pdsuserid = '" + pdsuserid + 
                    "' AND perselecmail.elecmailid = '" + email + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String emailType = null;
            if (lblMap.get("ELECTRONIC_MAIL_TYPE")!=null)
                emailType = collapse(lblMap.get("ELECTRONIC_MAIL_TYPE").getValue().toString());
            String prefId = null;
            if (lblMap.get("PREFERENCE_ID")!=null)
                prefId = collapse(lblMap.get("PREFERENCE_ID").getValue().toString());
            if (emailType==null || prefId==null) {
            	Object[] arguments = { "perselecmail", "ELECTRONIC_MAIL_TYPE,PREFERENCE_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO perselecmail (pdsuserid, elecmailid, elecmailtype, preferenceid, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "email id = " + email));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "email type = " + emailType));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "preference id = " + prefId));
                    */
                    pstmt.setString(1, pdsuserid);
                    pstmt.setString(2, email);
                    pstmt.setString(3, emailType);
                    pstmt.setShort(4, (short)(Short.parseShort(prefId)));
                    pstmt.setString(5, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(6, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "perselecmail - " + pdsuserid + " / " + email);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("perselecmail.elecmailtype", emailType);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestPersElecmail");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'dscollds' table
	 */
    protected void ingestDSCollAssocDSObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dscollid = null;
            if (lblMap.get("DATA_SET_COLLECTION_ID")!=null)
                dscollid = collapse(lblMap.get("DATA_SET_COLLECTION_ID").getValue().toString());
            String dsid = null;
            if (lblMap.get("DATA_SET_ID")!=null)
                dsid = lblMap.get("DATA_SET_ID").getValue().toString();
            
            if (dscollid==null || dsid==null) {
            	Object[] arguments = { "dscollds", "DATA_SET_COLLECTION_ID,DATA_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select dscollds.* from dscollds where dscollds.dscollid = get("DATA_SET_COLLECTION_ID")
                // delete from dscollds where dscollds.dscollid = get("DATA_SET_COLLECTION_ID")
                _sql = "SELECT dscollds.* FROM dscollds WHERE dscollds.dscollid = '" + dscollid + 
                    "' AND dscollds.dsid = '" + dsid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM dscollds WHERE dscollds.dscollid = '" + dscollid + "' AND dscollds.dsid = '" + dsid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) { 
                    _sql = "INSERT INTO dscollds (dscollid, dsid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "dscoll id = " + dscollid));
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "ds id = " + dsid));
                    pstmt.setString(1, dscollid);
                    pstmt.setString(2, dsid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dscollds - " + dsid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSCollAssocDSObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest DATA_SET_COLLECTION object
	 */
    protected void ingestDataSetColObject(Map<String,AttributeStatement> lblMap) {
        ingestDSCollUsgd(lblMap);   
        ingestDSCollD(lblMap);
        ingestDSCollInfo(lblMap);
    }

	/**
	 * Method to ingest the data into 'dscollusgd' table
	 */
    protected void ingestDSCollUsgd(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dscollid = null;
            if (lblMap.get("DATA_SET_COLLECTION_ID")!=null) {
            	dscollid = collapse(lblMap.get("DATA_SET_COLLECTION_ID").getValue().toString());
            }
            else {
            	Object[] arguments = { "dscollusgd", "DATA_SET_COLLECTION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 
            // select dscollusgd.* from dscollusgd where dscollusgd.dscollid = get("DATA_SET_COLLECTION_ID")
            // delete from dscollusgd where dscollusgd.dscollid = get("DATA_SET_COLLECTION_ID")
            _sql = "SELECT dscollusgd.* FROM dscollusgd WHERE dscollusgd.dscollid = '" + dscollid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dscollusgd WHERE dscollusgd.dscollid = '" + dscollid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("DATA_SET_COLLECTION_USAGE_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dscollusgd (dscollid, dscollusgd, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] usgDescStr = rtrim(lblMap.get("DATA_SET_COLLECTION_USAGE_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<usgDescStr.length; i++) {
                        pstmt.setString(1, dscollid);
                        pstmt.setString(2, usgDescStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dscollusgd - " + dscollid + " (" + usgDescStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "dscollusgd", "DATA_SET_COLLECTION_USAGE_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSCollUsgd");
            _label.addProblem(lp);
            e.printStackTrace();
			isFailed = true;
			failCount++;
        }
    }

	/** 
	 * Method to ingest the data into 'dscollinfo' table
	 */
    protected void ingestDSCollInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String dscollid = null;
            if (lblMap.get("DATA_SET_COLLECTION_ID")!=null) {
            	dscollid = collapse(lblMap.get("DATA_SET_COLLECTION_ID").getValue().toString());
            }
            else {
            	Object[] arguments = { "dscollinfo", "DATA_SET_COLLECTION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 

            // select dscollinfo.* from dscollinfo where dscollinfo.dscollid = get("DATA_SET_COLLECTION_ID")
            // delete from dscollinfo where dscollinfo.dscollid = get("DATA_SET_COLLECTION_ID")
            _sql = "SELECT dscollinfo.* FROM dscollinfo WHERE dscollinfo.dscollid = '" + dscollid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dscollinfo WHERE dscollinfo.dscollid = '" + dscollid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }

            String dscollname = null;
            String dataSets = null;
            String startTime = null;
            String stopTime = null;
            String dscollReleaseDt = null;
            String producerFullname = null;
            String lblRevisionNote = null;

            if (lblMap.get("DATA_SET_COLLECTION_NAME")!=null)
                dscollname = lblMap.get("DATA_SET_COLLECTION_NAME").getValue().toString();
            if (lblMap.get("DATA_SETS")!=null)
                dataSets = collapse(lblMap.get("DATA_SETS").getValue().toString());
            if (lblMap.get("START_TIME")!=null)
                startTime = lblMap.get("START_TIME").getValue().toString();
            if (lblMap.get("STOP_TIME")!=null)
                stopTime = lblMap.get("STOP_TIME").getValue().toString();
            if (lblMap.get("DATA_SET_COLLECTION_RELEASE_DT")!=null)
                dscollReleaseDt = lblMap.get("DATA_SET_COLLECTION_RELEASE_DT").getValue().toString();
            if (lblMap.get("PRODUCER_FULL_NAME")!=null)
                producerFullname = lblMap.get("PRODUCER_FULL_NAME").getValue().toString();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();

            if (dscollname==null || dataSets==null || startTime==null || stopTime==null ||
                dscollReleaseDt==null || producerFullname==null || lblRevisionNote==null) {
            	Object[] arguments = { "dscollinfo", "DATA_SET_COLLECTION_NAME,DATA_SETS,START_TIME,STOP_TIME," +
            			"DATA_SET_COLEECTION_RELEASE_DT,PRODUCER_FULL_NAME,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dscollinfo (dscollid, dscollname, datasets, strttime, stoptime, dscollreldt, prodfullname, " + 
                        "labelrevnote, userid, revdate) VALUES(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "ds coll name = " + dscollname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "data sets = " + dataSets));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "start time = " + startTime));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "stop time = " + stopTime));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "ds coll release date = " + dscollReleaseDt));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "producer name = " + producerFullname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "label rev note = " + lblRevisionNote));
                    */
                    pstmt.setString(1, dscollid);
                    pstmt.setString(2, dscollname);
                    pstmt.setInt(3, Integer.parseInt(dataSets));
                    pstmt.setString(4, startTime);
                    pstmt.setString(5, stopTime);
                    pstmt.setString(6, dscollReleaseDt);
                    pstmt.setString(7, producerFullname);
                    pstmt.setString(8, lblRevisionNote);
                    pstmt.setString(9, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(10, new Timestamp(_now.getTime()));
            
                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dscollinfo - " + dscollid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSCollInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/** 
	 * Method to ingest the data into 'dscolld' table
	 */
    protected void ingestDSCollD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String dscollid = null;
            if (lblMap.get("DATA_SET_COLLECTION_ID")!=null) {
            	dscollid = collapse(lblMap.get("DATA_SET_COLLECTION_ID").getValue().toString());
            }
            else {
            	Object[] arguments = { "dscolld", "DATA_SET_COLLECTION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            } 

            // select dscolld.* from dscolld where dscolld.dscollid = get("DATA_SET_COLLECTION_ID")
            // delete from dscolld where dscolld.dscollid = get("DATA_SET_COLLECTION_ID")
            _sql = "SELECT dscolld.* FROM dscolld WHERE dscolld.dscollid = '" + dscollid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM dscolld WHERE dscolld.dscollid = '" + dscollid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("DATA_SET_COLLECTION_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dscolld (dscollid, dscolld, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] descStr = rtrim(lblMap.get("DATA_SET_COLLECTION_DESC").getValue().toString()).split("\n"); 
                    for (int i=0; i<descStr.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "ds coll desc " + i + "   value = " + descStr[i]));
                        pstmt.setString(1, dscollid);
                        pstmt.setString(2, descStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dscolld - " + dscollid + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "dscolld", "DATA_SET_COLLECTION_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSCollD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
   
	/**
	 * Method to ingest the data into 'dscolldoc' table
	 */
    protected void ingestDSCollDoc(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dscollid = null;
            if (lblMap.get("DATA_SET_COLLECTION_ID")!=null)
            	dscollid = collapse(lblMap.get("DATA_SET_COLLECTION_ID").getValue().toString());
            else {
            	Object[] arguments = { "dscolldoc", "DATA_SET_COLLECTION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
        		failCount++;
        		return;
            }

            // optional
            // select dscolldoc.* from dscolldoc where dscolldoc.dscollid = get("DATA_SET_COLLECTION_ID")
            // delete from dscolldoc where dscolldoc.dscollid = get("DATA_SET_COLLECTION_ID")
            _sql = "SELECT dscolldoc.* FROM dscolldoc WHERE dscolldoc.dscollid = '" + dscollid + "'";
            count = getRowCount(_sql); 
            if (count!=0) {
                _sql = "DELETE FROM dscolldoc WHERE dscolldoc.dscollid = '" + dscollid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            String refkeyid = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null) {
                refkeyid = lblMap.get("REFERENCE_KEY_ID").getValue().toString();
          
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dscolldoc (dscollid, refkeyid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, dscollid);
                    pstmt.setString(2, refkeyid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));
    
                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dscolldoc - " + refkeyid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "dscolldoc", "REFERENCE_KEY_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSCollDoc");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest INVENTORY object
	 */
    public void ingestInventoryObject(Map<String,AttributeStatement> lblMap) {
        ingestInvSpcOrdNt(lblMap);
        ingestInvNodeMedia(lblMap);
    }

	/**
	 * Method to ingest the data into 'invspcordnt' table
	 */
    protected void ingestInvSpcOrdNt(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dscolldsid = null;
            if (lblMap.get("PRODUCT_DATA_SET_ID")!=null)
                dscolldsid = collapse(lblMap.get("PRODUCT_DATA_SET_ID").getValue().toString());
            String nodeId = null;
            if (lblMap.get("NODE_ID")!=null)
                nodeId = lblMap.get("NODE_ID").getValue().toString();
            String mediumType = null;
            if (lblMap.get("MEDIUM_TYPE")!=null)
                mediumType = lblMap.get("MEDIUM_TYPE").getValue().toString();
            /*
            log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "dscolldsid = " + dscolldsid));
            log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "node id = " + nodeId));
            log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "medium type = " + mediumType));
            */
            if (dscolldsid==null || nodeId==null || mediumType==null) {
            	Object[] arguments = { "invspcordnt", "PRODUCT_DATA_SET_ID,NODE_ID,MEDIUM_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
                return;
            }
            	
            // select invspcordnt.* from invspcordnt where invspcordnt.dscolldsid = get("PRODUCT_DATA_SET_ID") 
            //      and invspcordnt.nodeid = get("NODE_ID") and invspcordnt.mediumtype = get("MEDIUM_TYPE")
            // delete from invspcordnt where invspcordnt.dscolldsid = get("PRODUCT_DATA_SET_ID") 
			//      and invspcordnt.nodeid = get("NODE_ID") and invspcordnt.mediumtype = get("MEDIUM_TYPE")
            _sql = "SELECT invspcordnt.* FROM invspcordnt WHERE invspcordnt.dscolldsid = '" + dscolldsid + 
                "' AND invspcordnt.nodeid = '" + nodeId + "' AND invspcordnt.mediumtype = '" + mediumType + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM invspcordnt WHERE invspcordnt.dscolldsid = '" + dscolldsid + 
                    "' AND invspcordnt.nodeid = '" + nodeId + "' AND invspcordnt.mediumtype = '" + mediumType + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }

            if (lblMap.get("INVENTORY_SPECIAL_ORDER_NOTE")!=null) {
                if (delete!=0 || count==0) { 
                    _sql = "INSERT INTO invspcordnt (dscolldsid, nodeid, mediumtype, invspcordnt, tupseqnum, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    String[] noteStr = rtrim(lblMap.get("INVENTORY_SPECIAL_ORDER_NOTE").getValue().toString()).split("\n");
                    for (int i=0; i<noteStr.length; i++) {
                        pstmt.setString(1, dscolldsid);
                        pstmt.setString(2, nodeId);
                        pstmt.setString(3, mediumType);
                        pstmt.setString(4, noteStr[i].trim());
                        pstmt.setShort(5, (short) (i+1));
                        pstmt.setString(6, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(7, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "invspcordnt - " + nodeId + " / " + dscolldsid + " / " + mediumType);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "invspcordnt", "PRODUCT_DATA_SET_ID,NODE_ID,MEDIUM_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestInvSpcOrdNt");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'invnodemedia' table
	 */
    protected void ingestInvNodeMedia(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {  
        	INGEST_MSG = "Inserted: ";
            String dscolldsid = null;
            if (lblMap.get("PRODUCT_DATA_SET_ID")!=null)
                dscolldsid = collapse(lblMap.get("PRODUCT_DATA_SET_ID").getValue().toString());
            String nodeId = null;
            if (lblMap.get("NODE_ID")!=null)
                nodeId = lblMap.get("NODE_ID").getValue().toString();
            String mediumType = null;
            if (lblMap.get("MEDIUM_TYPE")!=null)
                mediumType = lblMap.get("MEDIUM_TYPE").getValue().toString();
            if (dscolldsid==null || nodeId==null || mediumType==null) {
            	Object[] arguments = { "invnodemedia", "PRODUCT_DATA_SET_ID,NODE_ID,MEDIUM_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
                return;
            }
            // select invnodemedia.* from invnodemedia where invnodemedia.nodeid = get("NODE_ID") 
            //      and invnodemedia.dscolldsid = get("PRODUCT_DATA_SET_ID") and invnodemedia.mediumtype = get("MEDIUM_TYPE")
            // delete from invnodemedia where invnodemedia.nodeid = get("NODE_ID") and invnodemedia.dscolldsid = get("PRODUCT_DATA_SET_ID") 
            //      and invnodemedia.mediumtype = get("MEDIUM_TYPE")
            _sql = "SELECT invnodemedia.* FROM invnodemedia WHERE invnodemedia.nodeid = '" + nodeId + 
                "' AND invnodemedia.dscolldsid = '" + dscolldsid + "' AND invnodemedia.mediumtype = '" + mediumType + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM invnodemedia WHERE invnodemedia.nodeid = '" + nodeId + "' AND invnodemedia.dscolldsid = '" +
                    dscolldsid + "' AND invnodemedia.mediumtype = '" + mediumType + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String mediumDesc = null;
            String copies = null;
            if (lblMap.get("MEDIUM_DESC")!=null)
                mediumDesc = lblMap.get("MEDIUM_DESC").getValue().toString();
            if (lblMap.get("COPIES")!=null)
                copies = collapse(lblMap.get("COPIES").getValue().toString());
            if (mediumDesc==null || copies==null) {
            	Object[] arguments = { "invnodemedia", "MEDIUM_DESC,COPIES" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO invnodemedia (nodeid, dscolldsid, mediumtype, mediumd, copies, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    pstmt.setString(1, nodeId);
                    pstmt.setString(2, dscolldsid);
                    pstmt.setString(3, mediumType);
                    pstmt.setString(4, mediumDesc);
                    pstmt.setInt(5, Integer.parseInt(copies));
                    pstmt.setString(6, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(7, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "invnodemedia - " + nodeId + " / " + dscolldsid + " / " + mediumType);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestInvNodeMedia");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest SOFTWARE object
	 */
    protected void ingestSoftwareObject(Map<String,AttributeStatement> lblMap) {
        ingestSWD(lblMap);
        ingestSWInfo(lblMap);
        ingestSWPur(lblMap);
    }

	/**
	 * Method to ingest the data into 'swd' table
	 */
    protected void ingestSWD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String swid = null;
            if (lblMap.get("SOFTWARE_ID")!=null)
            	swid = lblMap.get("SOFTWARE_ID").getValue().toString();
            String swverid = null;
            if (lblMap.get("SOFTWARE_VERSION_ID")!=null)
            	swverid = lblMap.get("SOFTWARE_VERSION_ID").getValue().toString();
            if (swid==null || swverid==null) {
            	Object[] arguments = { "swd", "SOFTWARE_ID,SOFTWARE_VERSION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            	return;
            }
            _sql = "SELECT swd.* FROM swd WHERE swd.swid = '" + swid + "' AND swd.swverid = '" + swverid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM swd WHERE swd.swid = '" + swid + "' AND swd.swverid = '" + swverid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("SOFTWARE_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO swd (swid, swverid, swd, tupseqnum, userid, revdate) VALUES(?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    if (debugFlag) {
                        System.out.println("sw id = " + swid);
                        System.out.println("sw ver id = " + swverid);
                    }

                    String[] descStr = rtrim(lblMap.get("SOFTWARE_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        if (debugFlag) 
                            System.out.println("desc " + i + "    value = " + descStr[i]);
                        pstmt.setString(1, swid);
                        pstmt.setString(2, swverid);
                        pstmt.setString(3, descStr[i]);
                        pstmt.setShort(4, (short) (i+1));
                        pstmt.setString(5, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(6, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "swd - " + swid + " / " + swverid + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "swd", "SOFTWARE_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestSWD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'swinfo' table
	 */
    protected void ingestSWInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String swid = null;
            if (lblMap.get("SOFTWARE_ID")!=null)
            	swid = lblMap.get("SOFTWARE_ID").getValue().toString();
            String swverid = null;
            if (lblMap.get("SOFTWARE_VERSION_ID")!=null)
            	swverid = lblMap.get("SOFTWARE_VERSION_ID").getValue().toString();
            
            String swname = null;
            String dataFormat = null;
            String swLicenseType = null;
            String techSupportType = null;
            String reqStorageBytes = null;
            String pdsUserId = null;
            String nodeId = null;
            String lblRevisionNote = null;
            if (lblMap.get("SOFTWARE_NAME")!=null)
                swname = collapse(lblMap.get("SOFTWARE_NAME").getValue().toString());
            if (lblMap.get("DATA_FORMAT")!=null)
                dataFormat = collapse(lblMap.get("DATA_FORMAT").getValue().toString());
            if (lblMap.get("SOFTWARE_LICENSE_TYPE")!=null)
                swLicenseType = collapse(lblMap.get("SOFTWARE_LICENSE_TYPE").getValue().toString());
            if (lblMap.get("TECHNICAL_SUPPORT_TYPE")!=null)
                techSupportType = collapse(lblMap.get("TECHNICAL_SUPPORT_TYPE").getValue().toString());
            if (lblMap.get("REQUIRED_STORAGE_BYTES")!=null)
                reqStorageBytes = collapse(lblMap.get("REQUIRED_STORAGE_BYTES").getValue().toString());
            if (lblMap.get("PDS_USER_ID")!=null) 
                pdsUserId = collapse(lblMap.get("PDS_USER_ID").getValue().toString());
            if (lblMap.get("NODE_ID")!=null)
                nodeId = collapse(lblMap.get("NODE_ID").getValue().toString());
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString();
            
            if (swid==null || swverid==null || swname==null || dataFormat==null || swLicenseType==null || techSupportType==null ||
                reqStorageBytes==null || pdsUserId==null || nodeId==null || lblRevisionNote==null) {
            	Object[] arguments = { "swinfo", "SOFTWARE_ID,SOFTWARE_VERSION_ID,SOFTWARE_NAME,DATA_FORMAT," +
            			"SOFTWARE_LICENSE_TYPE,TECHNICAL_SUPPORT_TYPE,REQUIRED_STORAGE_BYTES,PDS_USER_ID,NODE_ID,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select swinfo.* from swinfo where swinfo.swid = swid and swinfo.swverid = swverid
                // delte from swinfo where swinfo.swid = swid and swinfo.swverid = swverid
                _sql = "SELECT swinfo.* FROM swinfo WHERE swinfo.swid = '" + swid + "' AND swinfo.swverid = '" + swverid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM swinfo WHERE swinfo.swid = '" + swid + "' AND swinfo.swverid = '" + swverid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO swinfo (swid, swverid, swname, dataformat, swlicensetyp, techsupport, reqstorbytes, pdsuserid, " +
                        "nodeid, labelrevnote, userid, revdate) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    if (debugFlag) {
                        System.out.println("sw name = " + swname);
                        System.out.println("data format = " + dataFormat);
                        System.out.println("sw license type = " + swLicenseType);
                        System.out.println("tech support = " + techSupportType);
                        System.out.println("req storage bytes = " + reqStorageBytes);
                        System.out.println("pds user id = " + pdsUserId);
                        System.out.println("node id = " + nodeId);
                        System.out.println("label rev note = " + lblRevisionNote);
                    }
                    pstmt.setString(1, swid);
                    pstmt.setString(2, swverid);
                    pstmt.setString(3, swname);
                    pstmt.setString(4, dataFormat);
                    pstmt.setString(5, swLicenseType);
                    pstmt.setString(6, techSupportType);
                    pstmt.setString(7, reqStorageBytes);
                    pstmt.setString(8, pdsUserId);
                    pstmt.setString(9, nodeId);
                    pstmt.setString(10, lblRevisionNote);
                    pstmt.setString(11, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(12, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "swinfo - " + swid + " / " + swverid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestSWInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'swpur' table
	 */
    protected void ingestSWPur(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String swid = null;
            if (lblMap.get("SOFTWARE_ID")!=null)
            	swid = lblMap.get("SOFTWARE_ID").getValue().toString();
            String swverid = null;
            if (lblMap.get("SOFTWARE_VERSION_ID")!=null)
            	swverid = lblMap.get("SOFTWARE_VERSION_ID").getValue().toString();
            if (swid==null || swverid==null) {
            	Object[] arguments = { "swpur", "SOFTWARE_ID,SOFTWARE_VERSION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            	return;
            }
            // select swpur.* from swpur where swpur.swid = swid and swpur.swverid = swverid order by swpur.swpurpose
            // delete from swpur where swpur.swid = swid and swpur.swverid = swverid
            _sql = "SELECT swpur.* FROM swpur WHERE swpur.swid = '" + swid + "' AND swpur.swverid = '" +
                swverid + "' order by swpur.swpurpose";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM swpur WHERE swpur.swid = '" + swid + "' AND swpur.swverid = '" + swverid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("SOFTWARE_PURPOSE")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO swpur (swid, swverid, swpurpose, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "sw purpose = " + lblMap.get("SOFTWARE_PURPOSE")));
                    pstmt.setString(1, swid);
                    pstmt.setString(2, swverid);
                    pstmt.setString(3, lblMap.get("SOFTWARE_PURPOSE").getValue().toString());
                    pstmt.setString(4, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "swpur - " + swid + " / " + swverid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "swpur", "SOFTWARE_PURPOSE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestSWPur");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/** 
	 * Method to ingest the data into 'swonline' table
	 */
    public void ingestSoftwareOnlineObject(Map<String,AttributeStatement> lblMap) {   
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String swid = null;
            if (lblMap.get("SOFTWARE_ID")!=null)
            	swid = lblMap.get("SOFTWARE_ID").getValue().toString();
            String swverid = null;
            if (lblMap.get("SOFTWARE_VERSION_ID")!=null)
            	swverid = lblMap.get("SOFTWARE_VERSION_ID").getValue().toString();
            String platform = null;
            if (lblMap.get("PLATFORM")!=null)
            	platform = lblMap.get("PLATFORM").getValue().toString();
                 
            String nodeId = null;
            String onlineName = null;
            String onlineId = null;
            String protocolType = null;
            if (lblMap.get("NODE_ID")!=null)
                nodeId = collapse(lblMap.get("NODE_ID").getValue().toString());
            if (lblMap.get("ON_LINE_NAME")!=null)
                onlineName = collapse(lblMap.get("ON_LINE_NAME").getValue().toString());
            if (lblMap.get("ON_LINE_IDENTIFICATION")!=null)
                onlineId = collapse(lblMap.get("ON_LINE_IDENTIFICATION").getValue().toString());
            if (lblMap.get("PROTOCOL_TYPE")!=null)
                protocolType = lblMap.get("PROTOCOL_TYPE").getValue().toString();

            if (swid==null || swverid==null || platform==null || nodeId==null || 
            	onlineName==null || onlineId==null || protocolType==null) {
            	Object[] arguments = { "swonline", "SOFTWARE_ID,SOFTWARE_VERSION_ID,NODE_ID,ON_LINE_NAME," +
            			"ON_LINE_IDENTIFICATION,PROTOCOL_TYPE,PLATFORM" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
            else {
            	// select swonline.* from swonline where swonline.swid = swid and swonline.swverid = swverid order by swonline.onlinenm
                // delete from swonline where swonline.swid = swid and swonline.swverid = swverid
                _sql = "SELECT swonline.* FROM swonline WHERE swonline.swid = '" + swid + "' AND swonline.swverid = '" + swverid +
                    "' AND swonline.platform = '" + platform + "' order by swonline.onlinenm";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM swonline WHERE swonline.swid = '" + swid + "'AND swonline.swverid = '" + swverid + 
                        "' AND swonline.platform = '" + platform + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }

                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO swonline (swid, swverid, nodeid, onlinenm, onlineid, protocoltype, platform, userid, revdate) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "sw id = " + swid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "sw verid = " + swverid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "node id = " + nodeId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "online name = " + onlineName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "online id = " + onlineId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "protocol type = " + protocolType));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "platform = " + platform));
                    */
                    pstmt.setString(1, swid);
                    pstmt.setString(2, swverid);
                    pstmt.setString(3, nodeId);
                    pstmt.setString(4, onlineName);
                    pstmt.setString(5, onlineId);
                    pstmt.setString(6, protocolType);
                    pstmt.setString(7, platform);
                    pstmt.setString(8, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(9, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "swonline - " + swid + " / " + swverid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestSoftwareOnlineObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest TARGET object
	 */
    public void ingestTargetObject(Map<String,AttributeStatement> lblMap) {
        ingestTargetD(lblMap);
        ingestTargetDoc(lblMap);
        ingestTargetInfo(lblMap);
    }

	/**
	 * Method to ingest the data into 'targetd' table
	 */
    protected void ingestTargetD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	String targetName = null;
        	INGEST_MSG = "Inserted: ";
        	if (lblMap.get("TARGET_NAME")!=null) 
            	targetName = collapse(lblMap.get("TARGET_NAME").getValue().toString());
        	else {
        		Object[] arguments = { "targetd", "TARGET_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
        		isFailed = true;
        		failCount++;
        		return;
        	}
        	
            // select targetd.* from targetd where targetd.targname = get("TARGET_NAME")
            // delete from targetd where targetd.targname = get("TARGET_NAME")
            _sql = "SELECT targetd.* FROM targetd WHERE targetd.targname = '" + targetName + "'";
            count = getRowCount(_sql); 
            if (count!=0) {
                _sql = "DELETE FROM targetd WHERE targetd.targname = '" + targetName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("TARGET_DESC")!=null) {
                if (delete!=0 ||count==0) {
                    // Session.DENAME from Application.cfm (currently set to "rjoyner"
                    // TARGET_NAME, TARGET_DESCRIPTION, rowcount, session user, now()
                    _sql = "INSERT INTO targetd (targname, targetd, tupseqnum, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    String[] descStr = rtrim(lblMap.get("TARGET_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "target desc " + i + "   value = " + descStr[i]));
                        pstmt.setString(1, targetName);
                        pstmt.setString(2, descStr[i]);
                        pstmt.setShort(3, (short) (i+1));
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));
        
                        pstmt.executeUpdate(); 
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "targetd - " + targetName + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "targetd", "TARGET_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestTargetD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'targetinfo' table
	 */
    protected void ingestTargetInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	String targetName = null;
        	INGEST_MSG = "Inserted: ";
        	if (lblMap.get("TARGET_NAME")!=null) 
        		targetName = collapse(lblMap.get("TARGET_NAME").getValue().toString());
        	else {
        		Object[] arguments = { "targetinfo", "TARGET_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
        		isFailed = true;
        		failCount++;
        		return;
        	}
        	
            // select targetinfo.* from targetinfo where targetinfo.targname = lblMap.get("TARGET_NAME")
            // delete from targetinfo where targetinfo.targname = get("TARGET_NAME")
            _sql = "SELECT targetinfo.* FROM targetinfo WHERE targetinfo.targname = '" + targetName + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM targetinfo WHERE targetinfo.targname = '" + targetName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String primBodyname = null;
            String orbDirection = null;
            String rotDirection = null;
            String targetType = null;
            String lblRevisionNote = null;
            if (lblMap.get("PRIMARY_BODY_NAME")!=null)
                primBodyname = lblMap.get("PRIMARY_BODY_NAME").getValue().toString();
            if (lblMap.get("ORBIT_DIRECTION")!=null)
                orbDirection = lblMap.get("ORBIT_DIRECTION").getValue().toString();
            if (lblMap.get("ROTATION_DIRECTION")!=null)
                rotDirection = lblMap.get("ROTATION_DIRECTION").getValue().toString();
            if (lblMap.get("TARGET_TYPE")!=null)
                targetType = lblMap.get("TARGET_TYPE").getValue().toString();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();
            
            if (primBodyname==null || orbDirection==null || rotDirection==null || 
                targetType==null || lblRevisionNote==null) {
            	Object[] arguments = { "targetinfo", "PRIMARY_BODY_NAME,ORBIT_DIRECTION,ROTATION_DIRECTION," +
            			"TARGET_TYPE,LABEL_REVISION_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    // TARGET_NAME, PRIMARY_BODY_NAME, ORBIT_DIRECTION, ROTATION_DIRECTION, TARGET_TYPE, UNK, UNK, LABEL_REVISION_NOTE, Session.DENAME, now()
                    _sql = "INSERT INTO targetinfo (targname, primbodyname, orbdir, rotdir, targtype, targtersedesc, sbntarglocator, " + 
                        "labelrevnote, userid, revdate) VALUES(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "target_name = "+ targetName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "target type = " + targetType));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "primary body name = " + primBodyname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "orbit_direction = " + orbDirection));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "rotation direction = " + rotDirection));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "label revision note = " + lblRevisionNote));
					*/
                    pstmt.setString(1, targetName);
                    pstmt.setString(2, primBodyname);
                    pstmt.setString(3, orbDirection);
                    pstmt.setString(4, rotDirection);
                    pstmt.setString(5, targetType);
                    pstmt.setString(6, "UNK");
                    pstmt.setString(7, "UNK");
                    pstmt.setString(8, lblRevisionNote);
                    pstmt.setString(9, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(10, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "targetinfo - " + targetName);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("targetinfo.targname", targetName);
                    standardValueMap.put("targetinfo.orbdir", orbDirection);
                    standardValueMap.put("targetinfo.primbodyname", primBodyname);
                    standardValueMap.put("targetinfo.rotdir", rotDirection);
                    standardValueMap.put("targetinfo.targtype", targetType);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestTargetInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'targetdoc' table
	 */
    protected void ingestTargetDoc(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	String targetName = null;
        	INGEST_MSG = "Inserted: ";
        	if (lblMap.get("TARGET_NAME")!=null) 
        		targetName = collapse(lblMap.get("TARGET_NAME").getValue().toString());
        	else {
        		Object[] arguments = { "targetdoc", "TARGET_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
        		isFailed = true;
        		failCount++;
        		return;
        	}
        	
            // optional
            // select targetdoc.* from targetdoc where targetdoc.targname = get("TARGET_NAME")
            // delete from targetdoc where targetdoc.targname = get("TARGET_NAME")
            _sql = "SELECT targetdoc.* FROM targetdoc WHERE targetdoc.targname = '" + targetName + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM targetdoc WHERE targetdoc.targname = '" + targetName + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String refkeyid = null;
            if (lblMap.get("REFERENCE_KEY_ID")!=null) {
                refkeyid = lblMap.get("REFERENCE_KEY_ID").getValue().toString();
        
                if (delete!=0 || count==0) {
                    // TARGET_NAME, REFERENCE_KEY_ID, Session.DENAME, now()
                    _sql = "INSERT INTO targetdoc (targname, refkeyid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "reference key id = " + lblMap.get("REFERENCE_KEY_ID")));
                    pstmt.setString(1, targetName);
                    pstmt.setString(2, refkeyid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "targetdoc - " + refkeyid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestTargetDoc");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest VOLUME object
	 */
    public void ingestVolumeObject(Map<String,AttributeStatement> lblMap) {
        ingestVolDS(lblMap);
        ingestVolDesc(lblMap);
        ingestVolInfo(lblMap);
        ingestVolSetInfo(lblMap);
        ingestVolSerSet(lblMap);
        ingestVolSerInfo(lblMap);
    }

	/**
	 * Method to ingest the data into 'volds' table
	 */
    protected void ingestVolDS(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String volsetid = null;
            if (lblMap.get("VOLUME_SET_ID")!=null)
            	volsetid = lblMap.get("VOLUME_SET_ID").getValue().toString();
            String volid = null;
            if (lblMap.get("VOLUME_ID")!=null)
            	volid = lblMap.get("VOLUME_ID").getValue().toString();
            if (volsetid==null || volid==null) {
            	Object[] arguments = { "volds", "VOLUME_SET_ID,VOLUME_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
        		isFailed = true;
        		failCount++;
        		return;
            }
            // select volds.* from volds where volds.volumesetid = get("VOLUME_SET_ID") and volds.volumeid = get("VOLUME_ID")
            // delete from volds where volds.volumesetid = get("VOLUME_SET_ID") and volds.volumeid = get("VOLUME_ID")
            _sql = "SELECT volds.* FROM volds WHERE volds.volumesetid = '" + volsetid + "' AND volds.volumeid = '" + volid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM volds WHERE volds.volumesetid = '" + volsetid + "' AND volds.volumeid = '" + volid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String dsid = null;
            if (lblMap.get("DATA_SET_COLL_OR_DATA_SET_ID")!=null)
                dsid = lblMap.get("DATA_SET_COLL_OR_DATA_SET_ID").getValue().toString();
            if (lblMap.get("DATA_SET_ID")!=null)
                dsid = lblMap.get("DATA_SET_ID").getValue().toString();

            if (dsid!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO volds (volumesetid, volumeid, dscolldsid, dscatflag, userid, revdate) VALUES(?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "volume set id = " + volsetid));
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "volume id = " + volid));
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "ds coll dsid = " + dsid));
                    pstmt.setString(1, volsetid);
                    pstmt.setString(2, volid);
                    pstmt.setString(3, dsid);
                    pstmt.setString(4, "Y");
                    pstmt.setString(5, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(6, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "volds - " + volid + " / " + dsid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "volds", "DATA_SET_ID,DATA_SET_COLL_OR_DATA_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolDS");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'voldesc' table
	 */
    protected void ingestVolDesc(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String volsetid = null;
            if (lblMap.get("VOLUME_SET_ID")!=null)
            	volsetid = lblMap.get("VOLUME_SET_ID").getValue().toString();
            String volid = null;
            if (lblMap.get("VOLUME_ID")!=null)
            	volid = lblMap.get("VOLUME_ID").getValue().toString();
            if (volsetid==null || volid==null) {
            	Object[] arguments = { "voldesc", "VOLUME_SET_ID,VOLUME_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
        		failCount++;
        		return;
            }
            // select voldesc.* from voldesc where voldesc.volumesetid = get("VOLUME_SET_ID") and 
			//       voldesc.volumeid = get("VOLUME_ID")
            // delete from voldesc where voldesc.volumesetid = get("VOLUME_SET_ID") and voldesc.volumeid = get("VOLUME_ID")
            _sql = "SELECT voldesc.* FROM voldesc WHERE voldesc.volumesetid = '" + volsetid + "' AND voldesc.volumeid = '" +
			       volid + "'";
            count = getRowCount(_sql); 
            if (count!=0) {
                _sql = "DELETE FROM voldesc WHERE voldesc.volumesetid = '" + volsetid + "' AND voldesc.volumeid = '" + 
				       volid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("DESCRIPTION")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO voldesc (volumesetid, volumeid, volumedesc, tupseqnum, userid, revdate) VALUES(?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] descStr = rtrim(lblMap.get("DESCRIPTION").getValue().toString()).split("\n");
                    for (int i=0; i<descStr.length; i++) {
                        //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "volume desc " + i + "  value = " + descStr[i]));
                        pstmt.setString(1, volsetid);
                        pstmt.setString(2, volid);
                        pstmt.setString(3, descStr[i]);
                        pstmt.setShort(4, (short) (i+1));
                        pstmt.setString(5, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(6, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "voldesc - " + volid + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "voldesc", "DESCRIPTION" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolDesc");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'volinfo' table
	 */
    protected void ingestVolInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
        	String volsetid = null;
            if (lblMap.get("VOLUME_SET_ID")!=null)
            	volsetid = lblMap.get("VOLUME_SET_ID").getValue().toString();
            String volid = null;
            if (lblMap.get("VOLUME_ID")!=null)
            	volid = lblMap.get("VOLUME_ID").getValue().toString();
            if (volsetid==null || volid==null) {
            	Object[] arguments = { "volinfo", "VOLUME_SET_ID,VOLUME_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
        		failCount++;
        		return;
            }
            // select volinfo.* from volinfo where volinfo.volumesetid = volsetid and volinfo.volumeid = volid
            // delete from volinfo where volinfo.volumesetid = volsetid and volinfo.volumeid = volid
            _sql = "SELECT volinfo.* FROM volinfo WHERE volinfo.volumesetid = '" + volsetid + 
                "' AND volinfo.volumeid = '" + volid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM volinfo WHERE volinfo.volumesetid = '" + volsetid + "' AND volinfo.volumeid = '" + 
					   volid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            String volname = null;
            String volverid = null;
            String volformat = null;
            String pubDate = null;
            String mediumType = null;
            String lblRevisionNote = null;
            if (lblMap.get("VOLUME_NAME")!=null)
                volname = collapse(lblMap.get("VOLUME_NAME").getValue().toString());
            if (lblMap.get("VOLUME_VERSION_ID")!=null)
                volverid = lblMap.get("VOLUME_VERSION_ID").getValue().toString();
            if (lblMap.get("VOLUME_FORMAT")!=null)
                volformat = lblMap.get("VOLUME_FORMAT").getValue().toString();
            if (lblMap.get("PUBLICATION_DATE")!=null) 
                pubDate = lblMap.get("PUBLICATION_DATE").getValue().toString();
            if (lblMap.get("MEDIUM_TYPE")!=null)
                mediumType = lblMap.get("MEDIUM_TYPE").getValue().toString();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblRevisionNote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();
            else
            	lblRevisionNote = "NULL";

            if (volname==null || volverid==null || volformat==null || 
                pubDate==null || mediumType==null) {
            	//|| lblRevisionNote==null) { // Betty this keyword is no longer required (email from betty on 12/2/10)
            	Object[] arguments = { "volinfo", "VOLUME_NAME,VOLUME_VERSION_ID,VOLUME_FORAMT,PUBLICATION_DATE," +
            			"MEDIUM_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO volinfo (volumesetid, volumeid, volumename, volumeverid, volumeformat, publdate, mediumtype, " + 
                        "labelrevnote, userid, revdate) VALUES(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "volume name = " + volname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "volume ver id = " + volverid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "volume format = " + volformat));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "publication date = " + pubDate));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "medium type = " + mediumType));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "label rev note = " + lblRevisionNote));
                    */
                    pstmt.setString(1, volsetid);
                    pstmt.setString(2, volid);
                    pstmt.setString(3, volname);
                    pstmt.setString(4, volverid);
                    pstmt.setString(5, volformat);
                    pstmt.setString(6, pubDate);
                    pstmt.setString(7, mediumType);
                    pstmt.setString(8, lblRevisionNote);
                    pstmt.setString(9, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(10, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "volinfo - " + volsetid + " / " + volid);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("volinfo.volumesetid", volsetid);
                    standardValueMap.put("volinfo.mediumtype", mediumType);
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'volsetinfo' table
	 */
    protected void ingestVolSetInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";    
            String volsetid = null;
            if (lblMap.get("VOLUME_SET_ID")!=null)
            	volsetid = lblMap.get("VOLUME_SET_ID").getValue().toString();
            String volsetnm = null;
            if (lblMap.get("VOLUME_SET_NAME")!=null)
                volsetnm = collapse(lblMap.get("VOLUME_SET_NAME").getValue().toString());
            if (volsetid==null || volsetnm==null) {
            	Object[] arguments = { "volsetinfo", "VOLUME_SET_ID,VOLUME_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
        		isFailed = true;
        		failCount++;
        		return;
            }
            
            // select volsetinfo.* from volsetinfo where volsetinfo.volumesetid = volsetid and volsetinfo.volumesetnm = volsetname
            // delete from volsetinfo where volsetinfo.volumesetid = volsetid and volsetinfo.volsetnm = volsetnm
            _sql = "SELECT volsetinfo.* FROM volsetinfo WHERE volsetinfo.volumesetid = '" + volsetid + 
                "' AND volsetinfo.volumesetnm = '" + volsetnm + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM volsetinfo WHERE volsetinfo.volumesetid = '" + volsetid + "' AND volsetinfo.volumesetnm = '" +
                    volsetnm + "'";
                delete = deleteRecords(_sql); 
                INGEST_MSG = "Updated: ";
            }
            String volumes = null;
            if (lblMap.get("VOLUMES")!=null) {
                volumes = collapse(lblMap.get("VOLUMES").getValue().toString()); 
                if (!Character.isDigit(volumes.charAt(0))) {
                	Object[] arguments = { "VOLUMES", "NUMBER" };
                	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
                			"ingest.error.invalidType", ProblemType.EXECUTE_FAIL, arguments);
                    _label.addProblem(lp);
        			isFailed = true;
        			failCount++;
        			return;
                }
                	
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO volsetinfo (volumesetid, volumesetnm, volumes, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "vol set name = " + volsetnm));
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "volumes = " + volumes));
                    pstmt.setString(1, volsetid);
                    pstmt.setString(2, volsetnm);
                    pstmt.setShort(3, (short)(Short.parseShort(volumes)));
                    pstmt.setString(4, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "volsetinfo - " + volsetid);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("volsetinfo.volumesetnm", volsetnm);
                }
            }
            else {
            	Object[] arguments = { "volsetinfo", "VOLUMES" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolSetInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

	/**
	 * Method to ingest the data into 'volserset' table
	 */
    protected void ingestVolSerSet(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String volsetid = null;
            if (lblMap.get("VOLUME_SET_ID")!=null)
            	volsetid = lblMap.get("VOLUME_SET_ID").getValue().toString();
            else {
            	Object[] arguments = { "volserset", "VOLUME_SET_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
            	failCount++;
            	return;
            }
            String volsernm = collapse(lblMap.get("VOLUME_SERIES_NAME").getValue().toString());
            // select volserset.* from volserset where volserset.volumesernm = volsernm and volserset.volumesetid = volsetid
            // delete from volserset where volserset.volumesernm = volsernm and volserset.volumesetid = volsetid
            _sql = "SELECT volserset.* FROM volserset WHERE volserset.volumesernm = '" + volsernm + 
                "' AND volserset.volumesetid = '" + volsetid + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM volserset WHERE volserset.volumesernm = '" + volsernm + "' AND volserset.volumesetid = '" + 
                    volsetid + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (volsernm!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO volserset (volumesernm, volumesetid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "vol series name = " + volsernm));
                    pstmt.setString(1, volsernm);
                    pstmt.setString(2, volsetid);
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "volserset - " + volsetid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "volserset", "VOLUME_SERIES_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolSerSet");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'volserinfo' table
     */
    protected void ingestVolSerInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String volsernm = null;
            if (lblMap.get("VOLUME_SERIES_NAME")!=null) {
            	volsernm = collapse(lblMap.get("VOLUME_SERIES_NAME").getValue().toString());
            
            	// select volserinfo.* from volserinfo where volserinfo.volumesernm = volsernm
                // delete from volserinfo where volserinfo.volumesernm = volsernm
                _sql = "SELECT volserinfo.* FROM volserinfo WHERE volserinfo.volumesernm = '" + volsernm + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM volserinfo WHERE volserinfo.volumesernm = '" + volsernm + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO volserinfo (volumesernm, volumesets, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    pstmt.setString(1, volsernm);
                    pstmt.setShort(2, (short) (count+1));
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "volserinfo - " + volsernm);
		            _label.addProblem(lp);
					okCount++;
					standardValueMap.put("volserinfo.volumesernm", volsernm);
                }
            }
            else {
            	Object[] arguments = { "volserinfo", "VOLUME_SERIES_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolSerInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
  
    /**
     * Method to ingest the data into 'volonline' table
     */
    protected void ingestVolOnline(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String volsetid = null;
            if (lblMap.get("VOLUME_SET_ID")!=null)
            	volsetid = lblMap.get("VOLUME_SET_ID").getValue().toString();
            String volid = null;
            if (lblMap.get("VOLUME_ID")!=null)
            	volid = lblMap.get("VOLUME_ID").getValue().toString();
    
            String nodeId = null;
            String onlineName = null;
            String onlineId = null;
            String protocolType = null;
            if (lblMap.get("NODE_ID")!=null)
                nodeId = lblMap.get("NODE_ID").getValue().toString();
            if (lblMap.get("ON_LINE_NAME")!=null)
                onlineName = collapse(lblMap.get("ON_LINE_NAME").getValue().toString());
            if (lblMap.get("ON_LINE_IDENTIFICATION")!=null)
                onlineId = lblMap.get("ON_LINE_IDENTIFICATION").getValue().toString();
            if (lblMap.get("PROTOCOL_TYPE")!=null)
                protocolType = lblMap.get("PROTOCOL_TYPE").getValue().toString();

            if (volsetid==null || volid==null || nodeId==null || 
            	onlineName==null || onlineId==null || protocolType==null) {
            	Object[] arguments = { "volonline", "VOLUME_SET_ID,VOLUME_ID,NODE_ID,ON_LINE_NAME,ON_LINE_IDENTIFICATION,PROTOCOL_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
	            /* TODO: check again...*/
            	//isFailed = true;
                //failCount++;
            }
            else {
            	// optional
                // select volonline.* from volonline where volonline.volumesetid = volsetid and volonline.volumeid = volid
                // delete from volonline where volonline.volumesetid = volsetid and volonline.volumeid = volid
                _sql = "SELECT volonline.* FROM volonline WHERE volonline.volumesetid = '" + volsetid +
                    "' AND volonline.volumeid = '" + volid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM volonline WHERE volonline.volumesetid = '" + volsetid + "' AND volonline.volumeid = '" + 
                        volid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO volonline (volumesetid, volumeid, nodeid, onlinenm, onlineid, protocoltype, userid, revdate) " +
                        "VALUES(?,?,?,?,?,?,?,?)"; 
                    pstmt = _conn.prepareStatement(_sql);  
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "VOLUME_SET_ID = " + volsetid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "VOLUME_ID = " + volid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "NODE_ID = " + nodeId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "ON_LINE_NAME = " + onlineName));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "ON_LINE_IDENTIFICATION = " + onlineId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "PROTOCOL_TYPE = " + protocolType));
                    */
                    pstmt.setString(1, volsetid);
                    pstmt.setString(2, volid);
                    pstmt.setString(3, nodeId);
                    pstmt.setString(4, onlineName);
                    pstmt.setString(5, onlineId);
                    pstmt.setString(6, protocolType);
                    pstmt.setString(7, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(8, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "volonline - " + volid + " / " + onlineId);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestVolOnline");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest NSSDCDSID object
     */
    protected void ingestNssdcdsidObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dscolldsid = null;
            if (lblMap.get("DATA_SET_COLL_OR_DATA_SET_ID")!=null)
            	dscolldsid = collapse(lblMap.get("DATA_SET_COLL_OR_DATA_SET_ID").getValue().toString());
            String nssdcid = null;
            if (lblMap.get("NSSDC_DATA_SET_ID")!=null)
            	nssdcid = collapse(lblMap.get("NSSDC_DATA_SET_ID").getValue().toString());
            String mediumType = null;
            if (lblMap.get("MEDIUM_TYPE")!=null)
            	mediumType = lblMap.get("MEDIUM_TYPE").getValue().toString();
            
            if (dscolldsid==null || nssdcid==null || mediumType==null) {
            	Object[] arguments = { "dsnssdc", "DATA_SET_COLL_OR_DATA_SET_ID,NSSDC_DATA_SET_ID,MEDIUM_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select dsnssdc.* from dsnssdc where dsnssdc.dscolldsid = dscolldsid and dsnssdc.nssdcdsid = nssdcdsid 
                //         and dsnssdc.mediumtype = mediumtype
                // delete from dsnssdc where dsnssdc.dscolldsid = dscolldsid and dsnssdc.nssdcdsid = nssdcid 
                //         and dsnssdc.mediumtype = mediumtype
                _sql = "SELECT dsnssdc.* FROM dsnssdc WHERE dsnssdc.dscolldsid = '" + dscolldsid + "' AND dsnssdc.nssdcdsid = '" +
                    nssdcid + "' AND dsnssdc.mediumtype = '" + mediumType + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM dsnssdc WHERE dsnssdc.dscolldsid = '" + dscolldsid + "' AND dsnssdc.nssdcdsid = '" + 
                        nssdcid + "' AND dsnssdc.mediumtype = '" + mediumType + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dsnssdc (dscolldsid, nssdcdsid, mediumtype, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "dscoll ds id = " + dscolldsid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "nssdc ds id = " + nssdcid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "medium type = " + mediumType));
                    */
                    pstmt.setString(1, dscolldsid);
                    pstmt.setString(2, nssdcid);
                    pstmt.setString(3, mediumType);
                    pstmt.setString(4, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsnssdc - " + dscolldsid + " / " + nssdcid + " / " + mediumType);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestNssdcdsidObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest DATA_SET_RELEASE object
     */
    protected void ingestDSReleaseObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsid = null;
            if (lblMap.get("DATA_SET_ID")!=null)
            	dsid = collapse(lblMap.get("DATA_SET_ID").getValue().toString());
            String releaseid = null;
            if (lblMap.get("RELEASE_ID")!=null)
            	releaseid = collapse(lblMap.get("RELEASE_ID").getValue().toString());

            String releaseDate = null;
            String releaseMedium = null;
            String prodType = null;
            String archiveStatus = null;
            String releaseParamText = null;
            String dataProvidername = null;
            String distType = null;
            String desc = null;
            if (lblMap.get("RELEASE_DATE")!=null)
                releaseDate = lblMap.get("RELEASE_DATE").getValue().toString();
            if (lblMap.get("RELEASE_MEDIUM")!=null)
                releaseMedium = lblMap.get("RELEASE_MEDIUM").getValue().toString();
            if (lblMap.get("PRODUCT_TYPE")!=null)
                prodType = lblMap.get("PRODUCT_TYPE").getValue().toString();
            if (lblMap.get("ARCHIVE_STATUS")!=null)
                archiveStatus = lblMap.get("ARCHIVE_STATUS").getValue().toString();
            if (lblMap.get("RELEASE_PARAMETER_TEXT")!=null)
                releaseParamText = lblMap.get("RELEASE_PARAMETER_TEXT").getValue().toString();
            if (lblMap.get("DATA_PROVIDER_NAME")!=null) 
                dataProvidername = lblMap.get("DATA_PROVIDER_NAME").getValue().toString();
            if (lblMap.get("DISTRIBUTION_TYPE")!=null)
                distType = lblMap.get("DISTRIBUTION_TYPE").getValue().toString();
            if (lblMap.get("DESCRIPTION")!=null)
                desc = lblMap.get("DESCRIPTION").getValue().toString().trim();

            if (dsid==null || releaseid==null || releaseDate==null || releaseMedium==null || prodType==null || archiveStatus==null ||
                releaseParamText==null || dataProvidername==null || distType==null || desc==null) {
            	Object[] arguments = { "dsrelease", "DATA_SET_ID,RELEASE_ID,RELEASE_DATE,RELEASE_MEDIUM,PRODUCT_TYPE," +
            			"ARCHIVE_STATUS,RELEASE_PARAMETER_TEXT,DATA_PROVIDER_NAME,DISTRIBUTION_TYPE,DESCRIPTION" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select dsrelease.* from dsrelease where dsrelease.dsid = dsid and dsrelease.releaseid = releaseid
                // delete from dsrelease where dsrelease.dsid = dsid and dsrelease.releaseid = releaseid
                _sql = "SELECT dsrelease.* FROM dsrelease WHERE dsrelease.dsid = '" + dsid + "' AND dsrelease.releaseid = '" + 
                    releaseid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM dsrelease WHERE dsrelease.dsid = '" + dsid + "' AND dsrelease.releaseid = '" + 
                        releaseid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO dsrelease (dsid, releaseid, reldate, relmedium, producttype, " +
                        "relarchstat, relparatext, reldisplayflag, dpname, disttype, reldesc, userid, revdate) " + 
                        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);

                    pstmt.setString(1, dsid);
                    pstmt.setString(2, releaseid);
                    pstmt.setString(3, releaseDate);
                    pstmt.setString(4, releaseMedium);
                    pstmt.setString(5, prodType);
                    pstmt.setString(6, archiveStatus);
                    pstmt.setString(7, releaseParamText);
                    pstmt.setString(8, "N");                              // reldisplayflag
                    pstmt.setString(9, dataProvidername);
                    pstmt.setString(10, distType);
                    pstmt.setString(11, desc);
                    pstmt.setString(12, "dbo");                            // userid
                    _now = new Date();
                    pstmt.setTimestamp(13, new Timestamp(_now.getTime())); // revdate

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "dsrelease - " + dsid + " / " + releaseid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDSReleaseObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
     * Method to ingest DATA_SET_HOUSEKEEPING & RESOURCE objectsccc
     */
    protected void ingestResourceObject(Map<String,AttributeStatement> lblMap) {
        ingestResDS(lblMap);
        ingestResInfo(lblMap);
    }

    /**
     * Method to ingest the data into 'resds' table
     */
    protected void ingestResDS(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int rowCount = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsid = null;
            if (lblMap.get("DATA_SET_ID")!=null)
            	dsid = collapse(lblMap.get("DATA_SET_ID").getValue().toString());
            String resourceid = null;
            if (lblMap.get("RESOURCE_ID")!=null)
            	resourceid = collapse(lblMap.get("RESOURCE_ID").getValue().toString());
            
            if (dsid==null || resourceid==null) {
            	Object[] arguments = { "resds", "DATA_SET_ID,RESOURCE_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select resds.* from resds where resds.dsid = dsid and resds.resourceid = resourceid
                // delete from resds where resds.dsid = dsid and resds.resourceid = resourceid
                _sql = "SELECT resds.* FROM resds WHERE resds.dsid = '" + dsid + "' AND resds.resourceid = '" + resourceid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM resds WHERE resds.dsid = '" + dsid + "' AND resds.resourceid = '" + resourceid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO resds (resourceid, dsid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "dsid = " + dsid));
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "resource id = " + resourceid));
                    pstmt.setString(1, resourceid);
                    pstmt.setString(2, dsid);
                    pstmt.setString(3, "dbo");
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    rowCount = pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "resds - " + dsid + " / " + resourceid);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestResDS");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'resinfo' table
     */
    protected void ingestResInfo(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int rowCount = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String dsid = null;
            if (lblMap.get("DATA_SET_ID")!=null)
            	dsid = collapse(lblMap.get("DATA_SET_ID").getValue().toString());
            String resourceid = null;
            if (lblMap.get("RESOURCE_ID")!=null)
            	resourceid = collapse(lblMap.get("RESOURCE_ID").getValue().toString());
            String resname = null;
            String resdesc = null;
            String resclass = null;
            String resstatus = null;
            String reslink = null;
            String labelnote = null;
            String curatingnodeid = null;
            if (lblMap.get("RESOURCE_NAME")!=null)
                resname = lblMap.get("RESOURCE_NAME").getValue().toString();
            if (lblMap.get("DESCRIPTION")!=null)
            	resdesc = lblMap.get("DESCRIPTION").getValue().toString().trim();
            if (lblMap.get("RESOURCE_CLASS")!=null)
                resclass = lblMap.get("RESOURCE_CLASS").getValue().toString();
            if (lblMap.get("RESOURCE_STATUS")!=null)
                resstatus = lblMap.get("RESOURCE_STATUS").getValue().toString();
            if (lblMap.get("RESOURCE_LINK")!=null)
                reslink = lblMap.get("RESOURCE_LINK").getValue().toString().trim();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                labelnote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();
            if (lblMap.get("CURATING_NODE_ID")!=null)
            	curatingnodeid = lblMap.get("CURATING_NODE_ID").getValue().toString();

            if (dsid==null || resourceid==null || resname==null || resdesc==null || resclass==null || resstatus==null || reslink==null) {
            	Object[] arguments = { "resinfo", "DATA_SET_ID,CURATING_NODE_ID,RESOURCE_ID,RESOURCE_NAME,RESOURCE_CLASS," +
            			"RESOURCE_STATUS,RESOURCE_LINK,DESCRIPTION" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
            else {
            	// select resinfo.* from resinfo where resinfo.resourceid = resourceid
                // delete from resinfo where resinfo.resourceid = resourceid
                _sql = "SELECT resinfo.* FROM resinfo WHERE resinfo.resourceid = '" + resourceid + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM resinfo WHERE resinfo.resourceid = '" + resourceid + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO resinfo (resourceid, resname, resdesc, resclass, resstatus, reslink, labelrevnote, userid, revdate) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "resname = " + resname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "resdesc = " + resdesc));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "resclass = " + resclass));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "res status = " + resstatus));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "res link = " + reslink));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "label rev note = " + labelnote));
                    */
                    pstmt.setString(1, resourceid);
                    pstmt.setString(2, resname);
                    pstmt.setString(3, resdesc);
                    pstmt.setString(4, resclass);
                    pstmt.setString(5, resstatus);
                    pstmt.setString(6, reslink);
                    pstmt.setString(7, labelnote);
                    pstmt.setString(8, "dbo");
                    _now = new Date();
                    pstmt.setTimestamp(9, new Timestamp(_now.getTime()));

                    rowCount = pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "resinfo - " + dsid + " / " + resourceid);
		            _label.addProblem(lp);
					okCount++;
                
					// update "curating_node_id" in dsinfo 
					/*_sql = "UPDATE dsinfo SET curatingndid=?, revdate=? WHERE dsinfo.dsid = ?";
                	pstmt = _conn.prepareStatement(_sql);
                	pstmt.setString(1, curatingnodeid);
                	pstmt.setTimestamp(2, new Timestamp(_now.getTime()));
                	pstmt.setString(3, dsid);
                	rowCount = pstmt.executeUpdate();
                	pstmt.close();
					 */
            	}
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestResInfo");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest ELEMENT_DEFINITION object
     */
    protected void ingestElemDefObject(Map<String,AttributeStatement> lblMap) {     
        ingestDDColD(lblMap);
        ingestDDFormRule(lblMap);
        ingestDDSysClass(lblMap);
        ingestDDGenClass(lblMap);
        ingestDDCol(lblMap);
    }

    /**
     * Method to ingest the data into 'ddcol' table
     */
    protected void ingestDDCol(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String colname = null;
            if (lblMap.get("ELEMENT_NAME")!=null)
            	colname = lblMap.get("ELEMENT_NAME").getValue().toString().trim();
            String blname = null;
            if (lblMap.get("BL_NAME")!=null)
            	blname = lblMap.get("BL_NAME").getValue().toString().trim();
           
            String tersename = null;
            String genclasstype = null;
            String unitId = null;
            String stdvaltype = null;
            String max = null;
            String min = null;
            String maxLen = null;
            String minLen = null;
            String changedate = null;
            String statusType = null;
            String sourcename = null;
            String sqlfmt = null;
            String blsqlfmt = null;
            String dispfmt = null;
            String stdvaloutflag = null;
            String txtflag = null;
            String availableValtype = null;
            String keywordDefvalue = null;
            String lblnote = null;
            if (lblMap.get("TERSE_NAME")!=null)
                tersename = collapse(lblMap.get("TERSE_NAME").getValue().toString());
            if (lblMap.get("GENERAL_CLASSIFICATION_TYPE")!=null)
                genclasstype = lblMap.get("GENERAL_CLASSIFICATION_TYPE").getValue().toString();
            if (lblMap.get("UNIT_ID")!=null)
                unitId = lblMap.get("UNIT_ID").getValue().toString();
            if (lblMap.get("STANDARD_VALUE_TYPE")!=null)
                stdvaltype = lblMap.get("STANDARD_VALUE_TYPE").getValue().toString();
            if (lblMap.get("MAXIMUM")!=null)
                max = collapse(lblMap.get("MAXIMUM").getValue().toString());
            if (lblMap.get("MINIMUM")!=null)
                min = collapse(lblMap.get("MINIMUM").getValue().toString());
            if (lblMap.get("MAXIMUM_LENGTH")!=null)
                maxLen = collapse(lblMap.get("MAXIMUM_LENGTH").getValue().toString());
            if (lblMap.get("MINIMUM_LENGTH")!=null)
                minLen = collapse(lblMap.get("MINIMUM_LENGTH").getValue().toString());
            if (lblMap.get("CHANGE_DATE")!=null)
                changedate = lblMap.get("CHANGE_DATE").getValue().toString();
            if (lblMap.get("STATUS_TYPE")!=null)
                statusType = lblMap.get("STATUS_TYPE").getValue().toString();
            if (lblMap.get("SOURCE_NAME")!=null)
                sourcename = collapse(lblMap.get("SOURCE_NAME").getValue().toString());
            if (lblMap.get("SQL_FORMAT")!=null)
                sqlfmt = lblMap.get("SQL_FORMAT").getValue().toString();
            if (lblMap.get("BL_SQL_FORMAT")!=null)
                blsqlfmt = lblMap.get("BL_SQL_FORMAT").getValue().toString();
            if (lblMap.get("DISPLAY_FORMAT")!=null)
                dispfmt = lblMap.get("DISPLAY_FORMAT").getValue().toString();
            if (lblMap.get("STANDARD_VALUE_OUTPUT_FLAG")!=null)
                stdvaloutflag = lblMap.get("STANDARD_VALUE_OUTPUT_FLAG").getValue().toString();
            if (lblMap.get("TEXT_FLAG")!=null)
                txtflag = lblMap.get("TEXT_FLAG").getValue().toString();
            if (lblMap.get("AVAILABLE_VALUE_TYPE")!=null)
                availableValtype = lblMap.get("AVAILABLE_VALUE_TYPE").getValue().toString();
            if (lblMap.get("KEYWORD_DEFAULT_VALUE")!=null)
                keywordDefvalue = lblMap.get("KEYWORD_DEFAULT_VALUE").getValue().toString();
            if (lblMap.get("LABEL_REVISION_NOTE")!=null)
                lblnote = lblMap.get("LABEL_REVISION_NOTE").getValue().toString().trim();

            if (colname==null || blname==null || tersename==null || genclasstype==null || unitId==null || 
                stdvaltype==null || max==null || min==null || maxLen==null || minLen==null ||
                changedate==null || statusType==null || sourcename==null || sqlfmt==null || blsqlfmt==null ||
                dispfmt==null || stdvaloutflag==null || txtflag==null || availableValtype==null || lblnote==null) {
            	Object[] arguments = { "ddocl", "ELEMENT_NAME,BL_NAME,GENERAL_CLASSIFICATION_TYPE,UNIT_ID,STATUS_TYPE," +
            			"CHANGE_DATE,SOURCE_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
            else {
            	// select ddcol.* from ddcol where ddcol.colname = colname and ddcol.blname = blname
                // delete from ddcol where ddcol.colname = colname and ddcol.blname = blname
                _sql = "SELECT ddcol.* FROM ddcol WHERE ddcol.colname = '" + colname + "' AND ddcol.blname = '" + blname + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM ddcol WHERE ddcol.colname = '" + colname + "' AND ddcol.blname = '" + blname + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddcol (colname, blname, tersename, gendatatype, unitid, stdvaltype, maxcolval, mincolval, maxlength, " + 
                        "minlength, changedate, statustype, sourcename, sqlfmt, blsqlfmt, dspfmt, stdvaloutflg, txtflag, " + 
                        "avlvaltype, stddefault, labelrevnote, userid, revdate) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";               
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col name = " + colname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "blname = " + blname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "tersename = " + tersename));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "gendatatype = " + genclasstype));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "unitid = " + unitId));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "std val type = " + stdvaltype));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "max colval = " + max));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "min colval = " + min));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "maxlength = " + maxLen));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "minlength = " + minLen));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "changedate = " + changedate));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "status type = " + statusType));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "source name = " + sourcename));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "sql format = " + sqlfmt));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "bl sql format = " + blsqlfmt));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "dsp format = " + dispfmt));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "std valout flag = " + stdvaloutflag));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "text flag = " + txtflag));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "avlvaltype = " + availableValtype));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "std default = " + keywordDefvalue));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "label rev note = " + lblnote));
                    */
                    pstmt.setString(1, colname);  // colname
                    pstmt.setString(2, blname);   // blname
                    pstmt.setString(3, tersename);
                    pstmt.setString(4, genclasstype);
                    pstmt.setString(5, unitId);
                    pstmt.setString(6, stdvaltype);

                    if (max.equalsIgnoreCase("N/A"))
                        pstmt.setFloat(7, _na);
                    else if (max.equalsIgnoreCase("UNK"))
                        pstmt.setFloat(7, _unk);
                    else
                        pstmt.setFloat(7, Float.parseFloat(max));                                    // maxcolval

                    if (min.equalsIgnoreCase("N/A"))
                        pstmt.setFloat(8, _na);
                    else if (min.equalsIgnoreCase("UNK"))
                        pstmt.setFloat(8, _unk);
                    else
                        pstmt.setFloat(8, Float.parseFloat(min));                                    // mincolval

                    if (maxLen.equalsIgnoreCase("N/A"))
                        pstmt.setInt(9, (int)_na);
                    else if (maxLen.equalsIgnoreCase("UNK"))
                        pstmt.setInt(9, (int)_unk);
                    else
                        pstmt.setInt(9, Integer.parseInt(maxLen));                                      // maxlength

                    if (minLen.equalsIgnoreCase("N/A"))
                        pstmt.setInt(10, (int)_na);
                    else if (minLen.equalsIgnoreCase("UNK"))
                        pstmt.setInt(10, (int)_unk);
                    else
                        pstmt.setInt(10, Integer.parseInt(minLen));                                     // minlength

                    pstmt.setString(11, changedate);                 // changedate
                    pstmt.setString(12, statusType);                 // statustype
                    pstmt.setString(13, sourcename);                 // sourcename
                    pstmt.setString(14, sqlfmt);                     // sqlfmt
                    pstmt.setString(15, blsqlfmt);                   // blsqlfmt
                    pstmt.setString(16, dispfmt);                    // dspfmt
                    pstmt.setString(17, stdvaloutflag);              // stdvaloutflg
                    pstmt.setString(18, txtflag);                    // txtflag
                    pstmt.setString(19, availableValtype);           // avlvaltype
                    pstmt.setString(20, keywordDefvalue);            // stddefault
                    pstmt.setString(21, lblnote);                    // labelrevnote
                    pstmt.setString(22, _dename);                                   // userid
                    _now = new Date();
                    pstmt.setTimestamp(23, new Timestamp(_now.getTime()));          // revdate

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddcol - " + colname + " / " + blname);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDCol");
            _label.addProblem(lp);
            e.printStackTrace();
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddcold' table
     */
    protected void ingestDDColD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String blname = null;
            if (lblMap.get("BL_NAME")!=null)
            	blname = collapse(lblMap.get("BL_NAME").getValue().toString());  
            else  {
            	Object[] arguments = { "ddcold", "BL_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            
            // select ddcold.* from ddcold where ddcold.blname = blname
            // delete from ddcold where ddcold.blname = blname
            _sql = "SELECT ddcold.* FROM ddcold WHERE ddcold.blname = '" + blname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddcold WHERE ddcold.blname = '" + blname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("DESCRIPTION")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddcold (blname, tupseqnum, cold, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] descStr = rtrim(lblMap.get("DESCRIPTION").getValue().toString()).split("\n");
                    _now = new Date();
                    for (int i=0; i<descStr.length; i++) {
                        pstmt.setString(1,blname);
                        pstmt.setShort(2, (short) (i+1));
                        pstmt.setString(3, descStr[i]);
                        pstmt.setString(4, _dename);
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddcold - " + blname + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "ddcold", "DESCRIPTION" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDColD");
            _label.addProblem(lp);
            e.printStackTrace();
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddstdvald' table
     */
    protected void ingestDDStdValD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String colname = null;
            if (lblMap.get("ELEMENT_NAME")!=null)
            	colname = collapse(lblMap.get("ELEMENT_NAME").getValue().toString());
            else {
            	Object[] arguments = { "ddstdvald", "ELEMENT_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            // select ddstdvald.* from ddstdvald where ddstdvald.colname = colname
            // delete from ddstdvald where ddstdvald.colname = colname
            _sql = "SELECT ddstdvald.* FROM ddstdvald WHERE ddstdvald.colname = '" + colname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddstdvald WHERE ddstdvald.colname = '" + colname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("STANDARD_VALUE_SET_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddstdvald (colname, tupseqnum, description, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] stdDescStr = rtrim(lblMap.get("STANDARD_VALUE_SET_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<stdDescStr.length; i++) {
                    	//log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "std val desc " + i + "    value = " + stdDescStr[i]));
                        pstmt.setString(1, colname);
                        pstmt.setShort(2, (short) (i+1));
                        pstmt.setString(3, stdDescStr[i]);
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddstdvald - " + colname + " (" + stdDescStr.length + " rows)");
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "ddstdvald", "STANDARD_VALUE_SET_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDStdValD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
       
    /**
     * Method to ingest the data into 'ddformrule' table
     */
    protected void ingestDDFormRule(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String colname = null;
            if (lblMap.get("ELEMENT_NAME")!=null)
            	colname = collapse(lblMap.get("ELEMENT_NAME").getValue().toString());
            else {
            	Object[] arguments = { "ddformrule", "ELEMENT_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            // optional
            // select ddformrule.* from ddformrule where ddformrule.colname = colname
            // delete from ddformrule where ddformrule.colname = colname
            _sql = "SELECT ddformrule.* FROM ddformrule WHERE ddformrule.colname = '" + colname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddformrule WHERE ddformrule.colname = '" + colname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("FORMATION_RULE_DESC")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddformrule (colname, tupseqnum, description, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] formDescStr = rtrim(lblMap.get("FORMATION_RULE_DESC").getValue().toString()).split("\n");
                    for (int i=0; i<formDescStr.length; i++) {
                    	//log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "formation rule desc " + i + "   value = " + formDescStr[i]));
                        pstmt.setString(1, colname);
                        pstmt.setShort(2, (short) (i+1));
                        pstmt.setString(3, formDescStr[i]);
                        pstmt.setString(4, _dename);
                        _now = new Date();
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
					okCount++;
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddformrule - " + colname);
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "ddformrule", "FORMATION_RULE_DESC" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDFormRule");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddsysclass' table
     */
    protected void ingestDDSysClass(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String colname = null;
            if (lblMap.get("ELEMENT_NAME")!=null)
            	colname = lblMap.get("ELEMENT_NAME").getValue().toString();
            else {
            	Object[] arguments = { "ddsysclass", "ELEMENT_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            // select ddsysclass.* from ddsysclass where ddsysclass.colname = colname
            // delete from ddsysclass where ddsysclass.colname = colname
            _sql = "SELECT ddsysclass.* FROM ddsysclass WHERE ddsysclass.colname = '" + colname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddsysclass WHERE ddsysclass.colname = '" + colname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("SYSTEM_CLASSIFICATION_ID")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddsysclass (colname, sysclassid, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, "sysclass id = " + lblMap.get("SYSTEM_CLASSIFICATION_ID")));
                    pstmt.setString(1, colname);
                    pstmt.setString(2, lblMap.get("SYSTEM_CLASSIFICATION_ID").getValue().toString());
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));
                    
                    pstmt.executeUpdate();
                    pstmt.close();
					okCount++;	
					LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddsysclass - " + colname);
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "ddsysclass", "SYSTEM_CLASSIFICATION_ID" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDSysClass");
            _label.addProblem(lp);
            e.printStackTrace();
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddgenclass' table
     */
    protected void ingestDDGenClass(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String colname = null;
            if (lblMap.get("ELEMENT_NAME")!=null)
            	colname = lblMap.get("ELEMENT_NAME").getValue().toString();
            else {
            	Object[] arguments = { "ddgenclass", "ELEMENT_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            // select ddgenclass.* from ddgenclass where ddgenclass.colname = colname
            // delete from ddgenclass where ddgenclass.colname = colname
            _sql = "SELECT ddgenclass.* FROM ddgenclass WHERE ddgenclass.colname = '" + colname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddgenclass WHERE ddgenclass.colname = '" + colname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("GENERAL_CLASSIFICATION_TYPE")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddgenclass (colname, genclasstype, userid, revdate) VALUES(?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    //log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "gen class type = " + lblMap.get("GENERAL_CLASSIFICATION_TYPE")));
                    pstmt.setString(1, colname);
                    pstmt.setString(2, lblMap.get("GENERAL_CLASSIFICATION_TYPE").getValue().toString());
                    pstmt.setString(3, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(4, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
					okCount++;					
	            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddgenclass - " + colname);
		            _label.addProblem(lp);
                }
            }
            else {
            	Object[] arguments = { "ddgenclass", "GENERAL_CLASSIFICATION_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDGenClass");
            _label.addProblem(lp);
            e.printStackTrace();
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddcolstdval' table
     */
    protected void ingestStdValObject(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String blname = null;
            if (lblMap.get("BL_NAME")!=null)
            	blname = lblMap.get("BL_NAME").getValue().toString();
            String colval = null;
            if (lblMap.get("COLUMN_VALUE")!=null)
            	colval = collapse(lblMap.get("COLUMN_VALUE").getValue().toString());
            
            String colvalnodeid = null;
            String colvaltype = null;
            String outflag = null;
            if (lblMap.get("COLUMN_VALUE_NODE_ID")!=null)
                colvalnodeid = lblMap.get("COLUMN_VALUE_NODE_ID").getValue().toString();
            if (lblMap.get("COLUMN_VALUE_TYPE")!=null)
                colvaltype = lblMap.get("COLUMN_VALUE_TYPE").getValue().toString();
            if (lblMap.get("OUTPUT_FLAG")!=null)
                outflag = lblMap.get("OUTPUT_FLAG").getValue().toString();
            if (blname==null || colval==null || colvalnodeid==null || colvaltype==null || outflag==null) {
            	Object[] arguments = { "ddcolstdval", "BL_NAME,COLUMN_VALUE,COLUME_VALUE_NODE_ID,COLUMN_VALUE_TYPE,OUTPUT_FLAG" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// optional
                // select ddcolstdval.* from ddcolstdval where ddcolstdval.blname = blname and ddcolstdval.colval = colval
                // delete from ddcolstdval where ddcolstdval.blname = blname and ddcolstdval.colval = colval
                _sql = "SELECT ddcolstdval.* FROM ddcolstdval WHERE ddcolstdval.blname = '" + blname + 
                    "' AND ddcolstdval.colval = '" + colval + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM ddcolstdval WHERE ddcolstdval.blname = '" + blname + "' AND ddcolstdval.colval = '" + colval + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }                
                
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddcolstdval (blname, colval, colvalnodeid, colvaltype, outputflag, userid, revdate) " +
                        "VALUES(?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "blname = " + blname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col val = " + colval));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col val node id = " + colvalnodeid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col val type = " + colvaltype));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "output flag = " + outflag));
                    */
                    pstmt.setString(1, blname);
                    pstmt.setString(2, colval);
                    pstmt.setString(3, colvalnodeid);
                    pstmt.setString(4, colvaltype);
                    pstmt.setString(5, outflag);
                    pstmt.setString(6, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(7, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddcolstdval - " + blname + " / " + colval);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestStdValObject");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Returns a map of standard values found from the most recent
     * ingestion run.
     * 
     * @return A map containing standard values and its associated
     *  table/column name. Example: instinfo.instid - VG
     */
    public Map<String, String> getIngestedStdValues() {
        return standardValueMap;
    }

    /**
     * Method to ingest new standard values into the ddcolstdval table. The
     * method first checks to see if the standard value in the given map
     * already exists in the database.
     * 
     * @param stdValues A map containing a list of standard values and its
     * associated table/column name. Example: instinfo.instid - VG
     */
    protected void ingestNewStdValues(Map <String, String> stdValues) {
        String colvalnodeid = "U";
        String colvaltype = "A";
        String outflag = "Y";

        for(Map.Entry<String, String> entry : stdValues.entrySet()) {
            String []tableFields = entry.getKey().split("\\.");
            String blname = tableFields[1];
            String colval = entry.getValue();

            try {
                _sql = "SELECT ddcolstdval.* FROM ddcolstdval WHERE ddcolstdval.blname = '" + blname +
                   "' AND ddcolstdval.colval = '" + colval + "'";
                int count = getRowCount(_sql);
                
                if(count == 0) {
                    _sql = "INSERT INTO ddcolstdval (blname, colval, colvalnodeid, colvaltype, outputflag, userid, revdate) " +
                    "VALUES(?,?,?,?,?,?,?)";
                    PreparedStatement pstmt = _conn.prepareStatement(_sql);
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "blname = " + blname));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col val = " + colval));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col val node id = " + colvalnodeid));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "col val type = " + colvaltype));
                    log.log(new ToolsLogRecord(CIToolLevel.DEBUG, SPACES + "output flag = " + outflag));
                    */
                    pstmt.setString(1, blname);
                    pstmt.setString(2, colval);
                    pstmt.setString(3, colvalnodeid);
                    pstmt.setString(4, colvaltype);
                    pstmt.setString(5, outflag);
                    pstmt.setString(6, _dename);
                    _now = new Date();
                    pstmt.setTimestamp(7, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddcolstdval - " + blname + " / " + colval);
		            _label.addProblem(lp);
                    okCount++;
                    newStdValueCount++;
                }
            } catch (SQLException e) {
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
            			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestNewStdValues");
                _label.addProblem(lp);
                failCount++;
            }
        }
    }
    
    /**
     * Method to ingest OBJECT_DEFINTION object
     */
    protected void ingestObjDefObject(Map<String, AttributeStatement> lblMap) {	
    	ingestDDObjAlias(lblMap);
    	ingestDDObjD(lblMap);  	
    	ingestDDObjects(lblMap);
    	ingestDDObjElm(lblMap);
    	ingestDDObjStsNote(lblMap);
    }
    
    /**
     * Method to ingest the data into 'ddobjhier' table
     */
    protected void ingestDDObjAlias(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String objname = null;
            if (lblMap.get("NAME")!=null)
            	objname = collapse(lblMap.get("NAME").getValue().toString());       
            String aliasname = null;
            String usagenote = null;
            if (lblMap.get("ALIAS_NAME")!=null)
                aliasname = lblMap.get("ALIAS_NAME").getValue().toString();
            if (lblMap.get("USAGE_NOTE")!=null)
            	usagenote = collapse(lblMap.get("USAGE_NOTE").getValue().toString());
            
            if (objname==null || aliasname==null || usagenote==null) {
            	Object[] arguments = { "ddobjalias", "NAME,ALIAS_NAME,USAGE_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
            else {
            	// select ddobjalias.* from ddobjalias where ddobjalias.objname = objname
                // delete from ddobjalias where ddobjalias.objname = objname
                _sql = "SELECT ddobjalias.* FROM ddobjalias WHERE ddobjalias.objname = '" + objname + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM ddobjalias WHERE ddobjalias.objname = '" + objname + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddobjalias (objname, aliasname, usagenote, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    _now = new Date();                 
                    pstmt.setString(1, objname);
                    pstmt.setString(2, aliasname);
                    pstmt.setString(3, usagenote);
                    pstmt.setString(4, _dename);
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddobjalias - " + objname);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDObjAlias");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
    
    /**
     * Method to ingest the data into 'ddobjd' table
     */
    protected void ingestDDObjD(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String objname = null;
            if (lblMap.get("NAME")!=null)
            	objname = collapse(lblMap.get("NAME").getValue().toString());
            else {
            	Object[] arguments = { "ddobjd", "NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            // select ddobjd.* from ddobjd where ddobjd.objname = objname
            // delete from ddobjd where ddobjd.objname = objname
            _sql = "SELECT ddobjd.* FROM ddobjd WHERE ddobjd.objname = '" + objname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddobjd WHERE ddobjd.objname = '" + objname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            if (lblMap.get("DESCRIPTION")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddobjd (objname, tupseqnum, description, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] descStr = lblMap.get("DESCRIPTION").getValue().toString().trim().split("\n");
                    _now = new Date();
                    for (int i=0; i<descStr.length; i++) {
                        pstmt.setString(1, objname);
                        pstmt.setShort(2, (short) (i+1));
                        pstmt.setString(3, descStr[i]);
                        pstmt.setString(4, _dename);
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddobjd - " + objname + " (" + descStr.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "ddobjd", "DESCRIPTION" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDObjD");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddobjects' table
     */
    protected void ingestDDObjects(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String objname = null;
            if (lblMap.get("NAME")!=null)
            	objname = collapse(lblMap.get("NAME").getValue().toString());
            String tersename = null;
            String statustype = null;
            String sourcename = null;
            String objtype = null;
            String objclasstype = null;           
            if (lblMap.get("TERSE_NAME")!=null)
                tersename = lblMap.get("TERSE_NAME").getValue().toString();
            if (lblMap.get("STATUS_TYPE")!=null) 
            	statustype = lblMap.get("STATUS_TYPE").getValue().toString();
            if (lblMap.get("SOURCE_NAME")!=null)
            	sourcename = lblMap.get("SOURCE_NAME").getValue().toString();
            if (lblMap.get("OBJECT_TYPE")!=null)
            	objtype = lblMap.get("OBJECT_TYPE").getValue().toString();
            if (lblMap.get("OBJECT_CLASSIFICATION_TYPE")!=null)
            	objclasstype = lblMap.get("OBJECT_CLASSIFICATION_TYPE").getValue().toString();   
            
            if (objname==null || tersename==null || statustype==null || sourcename==null || objtype==null || objclasstype==null) {
            	Object[] arguments = { "ddobjects", "NAME,TERSE_NAME,STATUS_TYPE,SOURCE_NAME,OBJECT_TYPE,OBJECT_CLASSIFICATION_TYPE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeywords",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select ddobjects.* from ddobjects where ddobjects.objname = objname
                // delete from ddobjects where ddobjects.objname = objname
                _sql = "SELECT ddobjects.* FROM ddobjects WHERE ddobjects.objname = '" + objname + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM ddobjects WHERE ddobjects.objname = '" + objname + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddobjects (objname, tersename, statustype, sourcename, objtype, objclass, objclasstype, userid, revdate) " + 
                    	"VALUES(?,?,?,?,?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    _now = new Date();                 
                    pstmt.setString(1, objname);
                    pstmt.setString(2, tersename);
                    pstmt.setString(3, statustype);
                    pstmt.setString(4, sourcename);
                    pstmt.setString(5, objtype);
                    pstmt.setString(6, "OBJECT");
                    pstmt.setString(7, objclasstype);
                    pstmt.setString(8, _dename);
                    pstmt.setTimestamp(9, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddobjects - " + objname);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDObjects");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddobjelm' table
     */
    protected void ingestDDObjElm(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String objname = null;
            if (lblMap.get("NAME")!=null)
            	objname = collapse(lblMap.get("NAME").getValue().toString());  
                   
            String reqflag = null;
            String colname = null;
            if (lblMap.get("REQUIRED_FLAG")!=null)
            	reqflag = lblMap.get("REQUIRED_FLAG").getValue().toString();   
            if (lblMap.get("ELEMENT_NAME")!=null)
            	colname = lblMap.get("ELEMENT_NAME").getValue().toString();
            
            if (objname==null || reqflag==null || colname==null) {
            	Object[] arguments = { "ddobjelm", "NAME,REQUIRED_FLAG,ELEMENT_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
            else {
            	// select ddobjelm.* from ddobjelm where ddobjelm.objname = objname
                // delete from ddobjelm where ddobjelm.objname = objname
                _sql = "SELECT ddobjelm.* FROM ddobjelm WHERE ddobjelm.objname = '" + objname + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM ddobjelm WHERE ddobjelm.objname = '" + objname + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddobjelm (objname, requiredflag, colname, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    _now = new Date();                 
                    pstmt.setString(1, objname);
                    pstmt.setString(2, reqflag);
                    pstmt.setString(3, colname);
                    pstmt.setString(4, _dename);
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddobjelm - " + objname);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDObjElm");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }

    /**
     * Method to ingest the data into 'ddobjhier' table
     */
    protected void ingestDDObjHier(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String objname = null;
            if (lblMap.get("NAME")!=null)
            	objname = collapse(lblMap.get("NAME").getValue().toString());  
            String reqflag = null;
            String subobjname = null;
            if (lblMap.get("REQUIRED_FLAG")!=null)
            	reqflag = lblMap.get("REQUIRED_FLAG").getValue().toString();   
            if (lblMap.get("SUB_OBJECT_NAME")!=null)
                subobjname = lblMap.get("SUB_OBJECT_NAME").getValue().toString();   
                                  
            if (objname==null || reqflag==null || subobjname==null) {
            	Object[] arguments = { "ddobjhier", "NAME,REQUIRED_FLAG,SUB_OBJECT_NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
            	isFailed = true;
                failCount++;
            }
            else {
            	// select ddobjhier.* from ddobjhier where ddobjhier.objname = objname and ddobjhier.subobjname = subobjname
                // delete from ddobjhier where ddobjhier.objname = objname and 
                _sql = "SELECT ddobjhier.* FROM ddobjhier WHERE ddobjhier.objname = '" + objname + "' AND ddobjhier.subobjname = '" + subobjname + "'";
                count = getRowCount(_sql);
                if (count!=0) {
                    _sql = "DELETE FROM ddobjhier WHERE ddobjhier.objname = '" + objname + "' AND ddobjhier.subobjname = '" + subobjname + "'";
                    delete = deleteRecords(_sql);
                    INGEST_MSG = "Updated: ";
                }
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddobjhier (objname, requiredflag, subobjname, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    _now = new Date();                 
                    pstmt.setString(1, objname);
                    pstmt.setString(2, reqflag);
                    pstmt.setString(3, subobjname);
                    pstmt.setString(4, _dename);
                    pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                    pstmt.executeUpdate();
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddobjhier - " + objname + " / " + subobjname);
		            _label.addProblem(lp);
					okCount++;
                }
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDObjHier");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
 
    /**
     * Method to ingest the data into 'ddobjstsnote' table
     */
    protected void ingestDDObjStsNote(Map<String,AttributeStatement> lblMap) {
        PreparedStatement pstmt = null;
        int count = 0;
        int delete = 0;
        try {
        	INGEST_MSG = "Inserted: ";
            String objname = null;
            if (lblMap.get("NAME")!=null)
            	objname = collapse(lblMap.get("NAME").getValue().toString());  
            else {
            	Object[] arguments = { "ddobjstsnote", "NAME" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
               	isFailed = true;
               	failCount++;
               	return;
            }
            // select ddobjstsnote.* from ddobjstsnote where ddobjstsnote.objname = objname
            // delete from ddobjstsnote where ddobjstsnote.objname = objname
            _sql = "SELECT ddobjstsnote.* FROM ddobjstsnote WHERE ddobjstsnote.objname = '" + objname + "'";
            count = getRowCount(_sql);
            if (count!=0) {
                _sql = "DELETE FROM ddobjstsnote WHERE ddobjstsnote.objname = '" + objname + "'";
                delete = deleteRecords(_sql);
                INGEST_MSG = "Updated: ";
            }
            
            if (lblMap.get("STATUS_NOTE")!=null) {
                if (delete!=0 || count==0) {
                    _sql = "INSERT INTO ddobjstsnote (objname, tupseqnum, statusnote, userid, revdate) VALUES(?,?,?,?,?)";
                    pstmt = _conn.prepareStatement(_sql);
                    String[] statusnote = lblMap.get("STATUS_NOTE").getValue().toString().trim().split("\n");
                    _now = new Date();
                    for (int i=0; i<statusnote.length; i++) {
                        pstmt.setString(1, objname);
                        pstmt.setShort(2, (short) (i+1));
                        pstmt.setString(3, statusnote[i]);
                        pstmt.setString(4, _dename);
                        pstmt.setTimestamp(5, new Timestamp(_now.getTime()));

                        pstmt.executeUpdate();
                    }
                    pstmt.close();
                    LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
		                    null, "ingest.text.recordAdded",
		                    ProblemType.SUCCEED, INGEST_MSG + "ddobjstsnote - " + objname + " (" + statusnote.length + " rows)");
		            _label.addProblem(lp);
					okCount++;
                }
            }
            else {
            	Object[] arguments = { "ddobjstsnote", "STATUS_NOTE" };
            	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null,
	                    null, "ingest.error.missingKeyword",
	                    ProblemType.MISSING_MEMBER, arguments);
	            _label.addProblem(lp);
                isFailed = true;
                failCount++;
            }
        }
        catch (SQLException e) {
        	LabelParserException lp = new LabelParserException(_label.getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestDDObjStsNote");
            _label.addProblem(lp);
			isFailed = true;
			failCount++;
        }
    }
    
    /**
     * Method to remove extra spaces from the string specified by the string attributes.  
     * It returns the collapsed string without any additional spaces at the beginning
     * of the string and '\r\n' characters.
     * 
     * Example (orignal line): OBJECT = "THIS LINE       IS BAD"
     * 	          (collapsed): OBJECT = "THIS LINE IS BAD"
     */
    protected String collapse(String source) {
    	// remove '\r', leading whitespaces, and replace multiple whitespaces with single blank	
    	String outStr = itrim(ltrim(source));
    	outStr = outStr.replaceAll("\r\n", " ");
    	outStr = rtrim(outStr);
    	return outStr;
    }
    
    /**
     * Replace multiple whitespaces between words with single blank
     */
    protected String itrim(String source) {
    	return source.replaceAll("\\b\\s{2,}\\b", " ");		
    }
    
    /**
     * Trim the string of any spaces on the left end (ie., leading spaces removed)
     * Remove leading spaces for each line when there is multiple lines of string
     */
    protected String ltrim(String source) {
    	String tmpStr = source.trim();
    	
    	String outStr = "";
    	if (tmpStr.contains("\r\n")) {
    		// remove leading whitespace from each line
    		String[] inStr = tmpStr.split("\n");
    		for (int i=0; i<inStr.length; i++) {
    			if (inStr[i].length()==1)
    				outStr += inStr[i];    
    			else
    				outStr += inStr[i].replaceAll("^\\s+", "");
    			
    			if (i!=(inStr.length-1))
    				outStr += "\n";
    		}
    	} 
    	else 
    		outStr = tmpStr;
    	return outStr;
    }
    	
    /**
     * Trim the string of any spaces on the right end (ie., trailing spaces removed)
     */
    protected String rtrim(String source) {
    	return source.replaceAll("\\s+$", "");
    }

    /**
     * Method to get the row count from the database
     * 
     * @param query SQL query string
     * 
     * @return Number of rows with given SQL query
     */
    protected int getRowCount(String query) {
        int count = 0;
        try {
            Statement stmt = _conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            rs.last();
            count = rs.getRow();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Method to delete records in the database
     * 
     * @param query SQL query sting
     * 
     * @return number of deleted records
     */
    protected int deleteRecords(String query) {
        int delete = 0;
        try {
            Statement stmt = _conn.createStatement();
            delete = stmt.executeUpdate(query);
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return delete;
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
    	/*
        CatalogDB catDb = new CatalogDB("hlee", "new4hlee", "starsyb", "lee");
        //catDb.getDsStdValue("facilityname");
        CatalogObject catObj = new CatalogObject();
        catObj.setCatalogDB(catDb);
        Label lbl = catObj.parse("/Users/hyunlee/dev/pds_en/citool/src/main/java/gov/nasa/pds/citool/catfiles/test/data_set.CAT");

        catObj.ingest(lbl);
        catDb.close();
        */
    }
}
