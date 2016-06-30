package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularLabelContainer;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;

public abstract class TabularDataLoader extends Observable {

	protected StatusContainer status;
	protected String sessionId;
	protected TabularLabelContainer labelContainer = null;
	protected SliceContainer slice;

	// Maximum number of columns that can be processed and inserted in the
	// temporary table. Some labels tend to have 3K +- columns. However,
	// mysql table does not allow for such large number of columns. The
	// current system only allows for about 500 columns
	protected final int NUMBER_OF_COLUMNS_ALLOWED = 500;

	// Total number of rows to sample when building the temporary table
	// Used to determine DATES, DECIMALS, etc...
	protected static int TOTAL_ROWS_TO_SAMPLE = 100;

	// Specifies the number of rows to show while the rest is loading
	protected final int ROWS_DISPLAY_LIMIT = 100;

	public abstract void parseLabel();
	
	public TabularDataLoader(final String labelURLString) {
		
		this.slice = new SliceContainer();
		this.slice.setLabelURLString(labelURLString);
		this.status = new StatusContainer();
		// Save sessionId, used to mark db tables as belonging to this session
		// and removed upon session end
		this.sessionId = HTTPUtils.getSessionId();
		
	}
	
	public TabularDataLoader(final String labelURLString,
			final StatusContainer status, final String sessionId) {
		
		this.slice = new SliceContainer();
		this.slice.setLabelURLString(labelURLString);
		this.status = status;
		// Save sessionId, used to mark db tables as belonging to an
		// asynchronous threads' session and removed upon session end
		this.sessionId = sessionId;
	}

	public TabularDataLoader(SliceContainer slice,
			final StatusContainer status, final String sessionId) {
		this.slice = slice;
		this.status = status;
		this.sessionId = sessionId;
	}
	
	public abstract TabularDataContainer buildTabularDataContainer(
			ObjectStatement statement, PointerStatement pointer)
			throws Exception;

	protected abstract void insertColumns(
			TabularDataContainer tabularDataContainer, String labelURLString)
			throws Exception;
	
	public abstract void readData(TabularDataContainer tabularDataContainer)
			throws CancelledException;

	public abstract int readPartialData(
			TabularDataContainer tabularDataContainer)
			throws CancelledException;
	
	protected abstract void createDataTable(
			TabularDataContainer tabularDataContainer) throws Exception;

	protected abstract void fillDataTable(
			TabularDataContainer tabularDataContainer, URL tabFileUrl,
			long dataStartByte) throws Exception;
	
	protected abstract void fillDataTable(TabularDataContainer tabularDataContainer) 
			throws Exception;

	protected abstract void fillPartialDataTable(
			TabularDataContainer tabularDataContainer, URL tabFileUrl,
			long dataStartByte) throws Exception;
	
	protected abstract void fillPartialDataTable(
			TabularDataContainer tabularDataContainer) throws Exception;

	public SliceContainer getSlice() {
		return this.slice;
	}

	/**
	 * Check to see if the table selected already exists
	 *
	 * @param tabularDataContainer
	 * @return True if table exists. False otherwise
	 */
	public boolean tableExists(TabularDataContainer tabularDataContainer) {

		Connection connection = DBManager.getConnection();

		// Get selected table name
		String table = TabularDataUtils.getTabDataTableName(this.sessionId,
				tabularDataContainer.getId());

		// Create sql statement to see if table exists. Select 1 row and limit
		// to 1.
		// If the query returns something means that the table exists
		StringBuilder checkIfTableExisits = new StringBuilder();
		checkIfTableExisits.append("SELECT 1 FROM `" + table + "` LIMIT 1;");
		String SQLExists = checkIfTableExisits.toString();

		try {

			Statement createStmtExists = connection.createStatement();
			createStmtExists.execute(SQLExists);
			createStmtExists.close();
			connection.close();

			// Table was accessed so table exists
			return true;

		} catch (SQLException sqlException) {

			// Reaching this point means that table does not exist
			return false;
		}
	}

	
	/*
	 * updates status of statusContainer
	 */
	protected void updateStatus(final String key) throws CancelledException {
		updateStatus(key, true);
	}

	/*
	 * updates status of statusContainer object and increment steps
	 */
	protected void updateStatus(final String key, Boolean incrementStep)
			throws CancelledException {
		//TODO: check if there is actual need to throw an exception
		//if (this.status.isCancelled()) {
		//	throw new CancelledException();
		//}
		
		if (incrementStep != null && incrementStep.equals(Boolean.TRUE)) {
			this.status.incrementStep();
		}
		this.status.setStatus(key);
		setChanged();
		notifyObservers(this.status);
	}
}
