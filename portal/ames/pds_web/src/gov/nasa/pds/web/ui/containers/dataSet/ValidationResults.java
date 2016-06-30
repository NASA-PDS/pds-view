package gov.nasa.pds.web.ui.containers.dataSet;

import gov.nasa.arc.pds.tools.container.FileMirror;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.containers.SimpleDictionaryChange;
import gov.nasa.pds.web.ui.actions.dataManagement.PostValidationBucket;
import gov.nasa.pds.web.ui.containers.BaseContainerInterface;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ValidationResults implements Serializable, BaseContainerInterface {

	// update this if you make non-compatible change to class
	public static final long serialVersionUID = -3224942935855761610L;

	public static final transient String GET_PROBLEMS = "SELECT problem FROM `%s` WHERE errortype = '%s' LIMIT %s OFFSET %s"; //$NON-NLS-1$

	public static final transient String GET_FILE_PROBLEMS = "SELECT problem FROM `%s` WHERE path = '%s' LIMIT %s"; //$NON-NLS-1$

	public static transient PreparedStatement GET_FILE_PROBLEMS_STMNT;

	public static final transient int BUCKET_LIMIT = 1000;

	private static final transient int PREVIEW_LIMIT = 200;

	private static final transient int PREVIEW_FILE_LIMIT = 2000;

	private String id;

	private String name;

	// path on filesystem - useful to return to remote filesystem for preview
	private String path;

	private transient File volume;

	private transient List<Bucket> problemBuckets = new ArrayList<Bucket>();

	private transient Bucket currentBucket = new Bucket();

	private final List<NewValue> newValues = new ArrayList<NewValue>();

	private List<SimpleDictionaryChange> dictionaryChanges = new ArrayList<SimpleDictionaryChange>();

	private final Map<ProblemType, Integer> groupCount = new HashMap<ProblemType, Integer>();

	private String volumeId;

	private long numFiles = 0;

	private long numFolders = 0;

	private long volumeSpace = 0;

	// offset for retrieving problems from the db, used when getting large
	// volume of problems and need to chunk return
	private int offset = 0;

	// ms it took to validate
	private long duration;

	private String originalSeparator;

	public ValidationResults(final String procId) {
		this.id = procId;
		this.originalSeparator = File.separator;
	}

	public String getOriginalSeparator() {
		return this.originalSeparator;
	}

	public String getId() {
		return this.id;
	}

	public Bucket getBucket() {
		if (this.problemBuckets.size() > 0) {
			return this.problemBuckets.remove(0);
		}
		return this.currentBucket;
	}

	public List<NewValue> getNewValues() {
		return this.newValues;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public Map<ProblemType, Integer> getGroupCount() {
		return this.groupCount;
	}

	public void setRootNode(final File root) {
		this.volume = root;
		this.path = root.getPath();
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setVolumeId(final String volumeId) {
		this.volumeId = volumeId;
	}

	public String getVolumeId() {
		return this.volumeId;
	}

	public void setNumFiles(final long size) {
		this.numFiles = size;
	}

	public long getNumFiles() {
		return this.numFiles;
	}

	public void setNumFolders(final long size) {
		this.numFolders = size;
	}

	public long getNumFolders() {
		return this.numFolders;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public long getVolumeSpace() {
		return this.volumeSpace;
	}

	public void setVolumeSpace(final long volumeSpace) {
		this.volumeSpace = volumeSpace;
	}

	public boolean addProblem(final File file, final String key,
			final ProblemType type, final Integer lineNumber,
			final Object... arguments) {

		// update count
		if (this.groupCount.containsKey(type)) {
			Integer count = this.groupCount.get(type);
			this.groupCount.put(type, count + 1);
		} else {
			this.groupCount.put(type, 1);
		}

		final SimpleProblem problem = new SimpleProblem(file, this.volume, key,
				type, lineNumber, arguments);

		this.currentBucket.addProblem(problem);

		boolean full = this.currentBucket.isFull();
		if (full) {
			this.problemBuckets.add(this.currentBucket);
			this.currentBucket = new Bucket();
		}

		return full;
	}

	public String getTableName() {
		return PostValidationBucket.TEMP_ERROR_PREFIX + this.id;
	}

	public List<SimpleProblem> getProblems(final String filePath) {
		final String tableName = getTableName();
		final String sql = String.format(GET_FILE_PROBLEMS, tableName, filePath
				.replace("\\", "\\\\"), PREVIEW_FILE_LIMIT, this.offset); //$NON-NLS-1$//$NON-NLS-2$
		return getProbsFromSQL(sql);
	}

	// this could potentially get too many results, be careful how used
	public List<SimpleProblem> getProblems(final ProblemType type,
			final int limit) {

		final String tableName = getTableName();

		final String sql = String.format(GET_PROBLEMS, tableName, type, limit,
				this.offset);

		return getProbsFromSQL(sql);
	}

	private List<SimpleProblem> getProbsFromSQL(final String sql) {
		final List<SimpleProblem> probs = new ArrayList<SimpleProblem>();
		try {
			Connection connection = DBManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);

			if (rs != null) {
				while (rs.next()) {
					//Object object = rs.getObject("problem");//$NON-NLS-1$
					byte[] buf = rs.getBytes("problem"); //$NON-NLS-1$
					ObjectInputStream objectIn = null;
					if (buf != null) {
						objectIn = new ObjectInputStream(
								new ByteArrayInputStream(buf));
					}
					if (objectIn != null) {
						Object deSerializedObject = objectIn.readObject();
						probs.add((SimpleProblem) deSerializedObject);
						objectIn.close();
					}
				}
			}
			statement.close();
			connection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return probs;
	}

	// get the next group of problems from the db based on the current offset
	// this is only used for report writing so that large amounts of data don't
	// overflow memory
	public List<SimpleProblem> getNextBucket(final ProblemType type) {
		final List<SimpleProblem> probs = getProblems(type, BUCKET_LIMIT);
		this.offset += BUCKET_LIMIT;
		return probs;
	}

	// reset the problem offset so that getting problems doesn't continue to get
	// 0 results
	public void resetProblemPosition() {
		this.offset = 0;
	}

	public List<SimpleProblem> getPreviewProblems() {
		final List<SimpleProblem> probs = new ArrayList<SimpleProblem>();
		// for each of the groups, get preview limit worth of problems
		for (Iterator<Entry<ProblemType, Integer>> it = this.groupCount
				.entrySet().iterator(); it.hasNext();) {
			Entry<ProblemType, Integer> entry = it.next();
			ProblemType type = entry.getKey();
			final List<SimpleProblem> groupProbs = getProblems(type,
					PREVIEW_LIMIT);
			if (groupProbs != null) {
				probs.addAll(groupProbs);
			}
		}
		return probs;
	}

	public void addNewValue(final File file, final LabelParserException lpe) {

		NewValue newVal = new NewValue(file, lpe, this.volume);
		this.newValues.add(newVal);
	}

	public void setDictionaryChanges(final List<SimpleDictionaryChange> changes) {
		this.dictionaryChanges = changes;
	}

	public List<SimpleDictionaryChange> getDictionaryChganges() {
		return this.dictionaryChanges;
	}

	public static class SimpleProblem implements Serializable {

		private static final long serialVersionUID = 6136947673318570107L;

		private final transient File fileObj;

		private final FileMirror file;

		private final String key;

		private final ProblemType type;

		private final Integer lineNumber;

		private final Object[] arguments;

		@SuppressWarnings("nls")
		public SimpleProblem(final File file, final File root,
				final String key, final ProblemType type,
				final Integer lineNumber, final Object... arguments) {
			for (Object obj : arguments) {
				if (obj != null && !(obj instanceof java.io.Serializable)) {
					throw new RuntimeException(
							"Unable to serialize object of type "
									+ obj.getClass().getName() + " in file "
									+ file.toString() + " on line "
									+ lineNumber);
				}
			}
			this.fileObj = file;
			this.file = new FileMirror(file, root);
			this.key = key;
			this.type = type;
			this.lineNumber = lineNumber;
			this.arguments = arguments;
		}

		public FileMirror getFile() {
			return this.file;
		}

		public File getFileObj() {
			return this.fileObj;
		}

		public String getKey() {
			return this.key;
		}

		public ProblemType getType() {
			return this.type;
		}

		public Integer getLineNumber() {
			return this.lineNumber;
		}

		public Object[] getArguments() {
			return this.arguments;
		}
	}

	public static class NewValue implements Serializable {

		private static final long serialVersionUID = 5361475879414619107L;

		private final Integer location; // line number in file

		// consistent across instances
		private final String key;

		private final String value;

		private final FileMirror file;

		private final transient File fileObj;

		public NewValue(final File file, LabelParserException problem,
				final File root) {
			this.fileObj = file;
			this.file = new FileMirror(file, root);
			this.location = problem.getLineNumber();
			this.key = getKeyFromLPE(problem);
			this.value = getValueFromLPE(problem);
		}

		private String getKeyFromLPE(LabelParserException lpe) {
			return lpe.getArguments()[1].toString();
		}

		private String getValueFromLPE(LabelParserException lpe) {
			return lpe.getArguments()[2].toString();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (obj.getClass() != this.getClass())) {
				return false;
			}
			NewValue newVal = (NewValue) obj;
			return newVal.getKey().equals(this.key)
					&& newVal.getValue().equals(this.value);
		}

		@Override
		public int hashCode() {
			return (this.key + this.value).hashCode();
		}

		public boolean is(final LabelParserException lpe) {
			final String testKey = getKeyFromLPE(lpe);
			final String testValue = getValueFromLPE(lpe);
			return this.key.equals(testKey) && this.value.equals(testValue);
		}

		public FileMirror getFile() {
			return this.file;
		}

		public File getFileObj() {
			return this.fileObj;
		}

		public Integer getLineNumber() {
			return this.location;
		}

		public String getKey() {
			return this.key;
		}

		public String getValue() {
			return this.value;
		}
	}

	// TODO: this is a hack, get rid of it for something else
	public static class Counter {

		private long size = 0;

		public Counter() {
			// noop
		}

		public void increment() {
			this.size++;
		}

		public long size() {
			return this.size;
		}
	}
}
