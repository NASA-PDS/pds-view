package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularFileBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/*
 * Creates TabularFileBuilder and passes it to a thread for asynchronous
 * processing
 */
public class Download extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;
	protected final static Map<String, StatusContainer> statusCache = new HashMap<String, StatusContainer>();
	private final StatusContainer status = new StatusContainer(1);
	// for testing purposes
	protected boolean blocking = false;

	@Override
	protected String executeInner() throws Exception {
		TabularDataProcess tabularDataProcess = (TabularDataProcess) getProcess();

		// create builder to be passed to thread
		TabularFileBuilder builder = new TabularFileBuilder(tabularDataProcess
				.getSlice(), this.status);

		// put status in cache
		Download.statusCache.put(tabularDataProcess.getID(), this.status);
		final File sessionTempDir = HTTPUtils.getSessionTempDir();
		// start data loader in a new thread so it's not blocking
		FileBuilderThread thread = new FileBuilderThread(builder,
				tabularDataProcess, sessionTempDir);
		if (this.blocking) {
			thread.run();
		} else {
			thread.start();
		}

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		//
	}

	@Override
	protected void validateUserInput() {
		//
	}

	/*
	 * inner class for asynchronous building of tabularData output File
	 * 
	 * @param tabularFileBuilder a TabularFileBuilder object that will be used
	 * to create an output file from a SliceContainer
	 * 
	 * @param process the process the SliceContainer created by this thread
	 * should be assigned to
	 * 
	 * @param sessionTempDir the directory to write the file to
	 */
	class FileBuilderThread extends Thread {

		private final TabularFileBuilder tabularFileBuilder;
		private final TabularDataProcess process;
		private final File sessionTempDir;

		public FileBuilderThread(final TabularFileBuilder tabularFileBuilder,
				TabularDataProcess process, File sessionTempDir) {
			this.process = process;
			this.tabularFileBuilder = tabularFileBuilder;
			this.sessionTempDir = sessionTempDir;
		}

		@Override
		/*
		 * Calls tabularDataLoader.load, retrieves resulting sliceContainer
		 * object from loader and assigns it to the process passed into
		 * constructor.
		 */
		public void run() {
			try {
				this.tabularFileBuilder.buildFile();
				ByteArrayOutputStream outputStream = this.tabularFileBuilder
						.getOutput();
				// get id to be used as name for file
				String id = this.process.getSlice().getActiveTabularData()
						.getId();

				// write file
				final File targetFile = new File(this.sessionTempDir, id);
				targetFile.createNewFile();
				final FileOutputStream fileOut = new FileOutputStream(
						targetFile);
				fileOut.write(outputStream.toByteArray());
				fileOut.flush();
				fileOut.close();

				LogManager.logDownload(this.process.getSlice());

			} catch (final CancelledException e) {
				//
			} catch (final Exception e) {
				setException(e);
			}
		}
	}
}
