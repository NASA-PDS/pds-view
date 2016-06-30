package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseStreamAction;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants.FileType;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;
import java.io.FileInputStream;

/*
 * retrieves file from sessionTempDir and builds an input stream from it. Sets
 * contenttype and filename based on slice in process.
 * 
 * @author Laura Baalman
 */
public class ServeFile extends BaseStreamAction {

	private static final long serialVersionUID = 1L;
	private String contentType;
	private String fileName;

	public String getContentType() {
		return this.contentType;
	}

	public String getFileName() {
		return this.fileName;
	}

	@Override
	public String executeInner() {
		try {
			TabularDataProcess process = (TabularDataProcess) getProcess();
			SliceContainer slice = process.getSlice();
			TabularDataContainer table = slice.getActiveTabularData();

			// return an input stream based on file in tempdir
			final File sessionTempDir = HTTPUtils.getSessionTempDir();
			// file is named with table's id
			final File tabularFile = new File(sessionTempDir, table.getId());
			this.inputStream = new FileInputStream(tabularFile);

			// set default
			String extension = TabularManagementConstants.FileType.CSV
					.getExtension();
			// pull contentType from TabularManagementConstants based on
			// slice.fileType
			String fileType = slice.getFileType();
			for (FileType type : TabularManagementConstants.FileType.values()) {
				if (fileType.equalsIgnoreCase(type.name())) {
					this.contentType = type.getContentType();
					extension = type.getExtension();
				}
			}
			// create default if somehow no content type is found
			if (this.contentType.length() == 0) {
				this.contentType = TabularManagementConstants.FileType.CSV
						.getContentType();
			}

			String tabFileName = table.getTabFileName();
			this.fileName = tabFileName.substring(0, tabFileName.indexOf(".")) //$NON-NLS-1$
					.concat(".").concat(extension.toLowerCase()); //$NON-NLS-1$

		} catch (Exception e) {
			setException(e);
			return ERROR;
		}
		return SUCCESS;
	}
}
