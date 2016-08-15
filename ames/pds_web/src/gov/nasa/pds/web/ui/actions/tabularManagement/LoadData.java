package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS3DataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS4DataLoader;

import java.util.HashMap;
import java.util.Map;

/*
 * Retrieves process. Creates TabularDataLoader and passes it to instance of
 * inner class TableLoaderThread for asynchronous processing
 * 
 * @author Laura Baalman
 */
public class LoadData extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	protected final static Map<String, StatusContainer> statusCache = new HashMap<String, StatusContainer>();

	private final StatusContainer status = new StatusContainer(3);

	// for testing purposes
	protected boolean blocking = false;

	private int activeId = -1;

	// method used by unit tests
	public StatusContainer getStatus() {
		return this.status;
	}

	public void setActiveId(int id) {
		this.activeId = id;
	}

	@Override
	protected String executeInner() throws Exception {
		
		TabularDataProcess tabularDataProcess = (TabularDataProcess) getProcess();
		SliceContainer slice = tabularDataProcess.getSlice();

		// if activeId is passed in, save into slice
		if (this.activeId != -1) {
			slice.setActiveTabDataIndex(this.activeId);
		}

		TabularDataLoader loader;
		// select and build loader object to pass to thread
		if (slice.getPDSLabelType().equalsIgnoreCase("pds3")) {

			// build loader object to pass to thread
			loader = new TabularPDS3DataLoader(slice,
					this.status, HTTPUtils.getSessionId());

		// pds4
		} else {
			
			loader = new TabularPDS4DataLoader(slice,
					this.status, HTTPUtils.getSessionId());
		}

		// get table populate status: 0-error, 1-table does not exist, so
		// table
		// created and partially filled,
		// 2-table exists and completely populated, 3-table exists and not
		// completely populated
		int tableStatus = loader.readPartialData(tabularDataProcess
				.getSlice().getActiveTabularData());
		tabularDataProcess.setSlice(slice);

		if (tableStatus == 1) {

			// put status in cache
			LoadData.statusCache.put(tabularDataProcess.getID(),
					this.status);

			// start data loader in a new thread so it's not blocking
			TabularDataLoaderThread thread = new TabularDataLoaderThread(
					loader, tabularDataProcess);

			if (this.blocking) {
				thread.run();
			} else {
				thread.start();
			}
			return SUCCESS;
		} else if (tableStatus == 2) {
			return SUCCESS;
		} else if (tableStatus == 3) {
			return INPUT;
		} else {
			return INPUT;
		}
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
	 * inner class for asynchronous processing of TabularDataLoader
	 * 
	 * @param loader a TabularDataLoader object that will be used to create a
	 * SliceContainer
	 * 
	 * @param process the process the SliceContainer created by this thread
	 * should be assigned to
	 */
	class TabularDataLoaderThread extends Thread {
		private final TabularDataLoader tabularDataLoader;
		private final TabularDataProcess process;

		public TabularDataLoaderThread(final TabularDataLoader loader,
				TabularDataProcess process) {
			this.process = process;
			this.tabularDataLoader = loader;
		}

		/*
		 * Calls tabularDataLoader.load, retrieves resulting sliceContainer
		 * object from loader and assigns it to the process passed into
		 * constructor.
		 */
		@Override
		public void run() {
			try {
				this.tabularDataLoader.readData(this.process.getSlice()
						.getActiveTabularData());
				final SliceContainer slice = this.tabularDataLoader.getSlice();
				this.process.setSlice(slice);

				LogManager.logTabulardata(slice);

			} catch (final CancelledException e) {
				//
			} catch (final Exception e) {
				setException(e);
			}

		}
	}
}
