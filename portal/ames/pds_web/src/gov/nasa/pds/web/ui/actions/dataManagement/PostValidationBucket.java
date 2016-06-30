package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.containers.dataSet.Bucket;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults.SimpleProblem;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Post a collection of problems to the database
 * 
 * @author jagander
 */
public class PostValidationBucket extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Statement for creating a problem cache table for the validation, assuming
	 * it does not already exist
	 */
	@SuppressWarnings("nls")
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` ("
			+ "`errorid` int(11) NOT NULL auto_increment,"
			+ "`errortype` VARCHAR(255) NOT NULL,"
			+ "`path` VARCHAR(500) NOT NULL,"
			+ "`problem` BLOB NOT NULL,"
			+ "PRIMARY KEY (`errorid`),"
			+ "KEY `errortype` (`errortype`(64)),"
			+ "KEY `path` (`path`(255))"
			+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";

	/**
	 * Statement for inserting a problem into the database
	 */
	public static final String INSERT_PROBLEM = "INSERT INTO `%s` SET errortype = ?, path = ?, problem = ?;"; //$NON-NLS-1$

	/**
	 * Prefix for all validation problem cache tables in the DB. Useful to
	 * identify them in batch cleaning process.
	 */
	public static final String TEMP_ERROR_PREFIX = "temp_errors_"; //$NON-NLS-1$

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// get input stream containing problems
		final InputStream in = HTTPUtils.getRequest().getInputStream();

		// create results
		ObjectInputStream ois = new ObjectInputStream(in);

		// read the serialized data object
		Bucket bucket = (Bucket) ois.readObject();

		// store the problems in the database
		storeBucket(this.getProcId(), bucket);

		return JSON;
	}

	/**
	 * Store the bucket of problems
	 * 
	 * @param procId
	 *            the process id for the validation
	 * @param bucket
	 *            the collection of problems to store
	 */
	public static void storeBucket(final String procId, final Bucket bucket)
			throws SQLException {

		// get a connection to the database
		Connection connection = DBManager.getConnection();

		// create a statement for creating the table
		Statement statement = connection.createStatement();

		// use process id for unique table name
		final String tableName = TEMP_ERROR_PREFIX + procId;
		String sql = String.format(CREATE_TABLE, tableName);

		// create the table
		statement.execute(sql);

		// create the problem statement given the table name
		final String insertProbStmntString = String.format(INSERT_PROBLEM,
				tableName);

		// prepare the statement as it will be used multiple times
		final PreparedStatement insertProbStmnt = connection
				.prepareStatement(insertProbStmntString);

		// iterate over problems and insert in db
		// NOTE: error type and relative path are used in filtering and must be
		// separated out to other colums
		for (final SimpleProblem problem : bucket.getProblems()) {

			// get the error type and set in prepared statement
			final String errorType = problem.getType().name();
			insertProbStmnt.setString(1, errorType);

			// get the path to the problem and set in the prepared statement
			final String path = problem.getFile().getRelativePath();
			insertProbStmnt.setString(2, path);

			// set the problem as binary data to be de-serialized on retrieval
			insertProbStmnt.setObject(3, problem);

			// do the problem addition to the db
			insertProbStmnt.execute();
		}

		// close the connection
		connection.close();
	}

	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validateUserInput() {
		// TODO Auto-generated method stub

	}

}
