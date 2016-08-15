package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.pds.tools.dict.Dictionary;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.Scalar;
import gov.nasa.pds.tools.label.Set;
import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.web.AppVersionInfo;
import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.constants.DataSetConstants;
import gov.nasa.pds.web.ui.constants.DataSetConstants.DataType;
import gov.nasa.pds.web.ui.containers.LabelContainer;
import gov.nasa.pds.web.ui.containers.VolumeContainer;
import gov.nasa.pds.web.ui.utils.Comparators;
import gov.nasa.pds.web.ui.utils.DataSetValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

/**
 * Entry point for validating volumes
 * 
 * @author jagander
 */
public class ManageDataSets extends BaseViewAction implements ServletRequestAware {

	private static final long serialVersionUID = 1L;

	/**
	 * List of volumes found on local server available for validation
	 */
	private final List<VolumeContainer> volumes = new ArrayList<VolumeContainer>();

	/**
	 * Location of volumes on local server
	 */
	private File dataSetDirectory;

	/**
	 * Get volumes found on local server
	 */
	public List<VolumeContainer> getVolumes() {
		return this.volumes;
	}

	/**
	 * Master dictionary used for reading volume meta data in the display of
	 * local volumes
	 */
	private Dictionary dictionary = DataSetValidator.getMasterDictionary();

	private HttpServletRequest request;

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// try to get data set root from properties file
		this.dataSetDirectory = getDataSetDirectory();

		// if there's a data set directory, get volumes for display and
		// validation
		// NOTE: This is for development purposes and the rare case where a user
		// has installed locally (NOT RECOMMENDED)
		if (FileUtils.exists(this.dataSetDirectory)) {
			final List<File> vols = Arrays.asList(this.dataSetDirectory
					.listFiles());
			for (File vol : vols) {
				// skip compressed folders
				// TODO: make this cleaner and move to FileUtils
				final String name = vol.getName();
				if (name.matches("^.*\\.(zip|ZIP|tar|TAR|gz|GZ|tgz)$")) { //$NON-NLS-1$
					continue;
				}

				// first find volume from readme if present, only go down a few
				// folders to find volume
				final File readme = FileUtils.getTopFileByPattern(vol,
						DataSetConstants.README_NAME, 3);

				if (readme != null) {

					String volName = null;
					String volId = ""; //$NON-NLS-1$
					String setId = ""; //$NON-NLS-1$

					final File volFile = readme.getParentFile();

					// try to get names from voldesc
					final File voldesc = FileUtils.getCaseUnknownFile(volFile,
							DataSetConstants.VOLUME_DESC_FILE_NAME);
					if (voldesc.exists()) {
						// get label for voldesc
						LabelContainer volDescLabel = new LabelContainer(
								voldesc, null, this.dictionary, true);
						if (volDescLabel.isValid()) {
							List<ObjectStatement> volumeStatements = volDescLabel
									.getLabelObj().getObjects("VOLUME"); //$NON-NLS-1$
							if (volumeStatements != null
									&& volumeStatements.size() > 0) {
								ObjectStatement volumeStatement = volumeStatements
										.get(0);
								// get name
								AttributeStatement volNameStatement = volumeStatement
										.getAttribute("VOLUME_NAME"); //$NON-NLS-1$
								if (volNameStatement != null) {
									volName = volNameStatement.getValue()
											.toString();
								}
								// get id
								AttributeStatement volIdStatement = volumeStatement
										.getAttribute("VOLUME_ID"); //$NON-NLS-1$
								if (volIdStatement != null) {
									volId = volIdStatement.getValue()
											.toString();
								}
								AttributeStatement setIdStatement = volumeStatement
										.getAttribute("DATA_SET_ID"); //$NON-NLS-1$
								if (setIdStatement != null) {
									Value value = setIdStatement.getValue();
									if (value instanceof Set) {
										Iterator<Scalar> it = ((Set) value)
												.iterator();
										while (it.hasNext()) {
											setId += it.next().toString();
											if (it.hasNext()) {
												setId += ", "; //$NON-NLS-1$
											}
										}
									} else {
										setId = value.toString();
									}
								}
							}
						}
					}

					// default to folder name surrounding readme for volname
					if (volName == null) {
						volName = volFile.getName();
					}

					addVolume(volName, volId, setId, volFile);

				} else {
					// assume the folder is the volume
					addVolume(vol.getName(), null, null, vol);
				}
			}
		}

		// sort the volumes alphabetically
		Collections.sort(this.volumes, Comparators.VOLUME_CONTAINER_COMPARATOR);

		return SUCCESS;
	}

	/**
	 * Add a volume to the volume listing
	 * 
	 * @param volName
	 *            name of the volume
	 * @param volId
	 *            volume id
	 * @param setId
	 *            data set id(s)
	 * @param volFile
	 *            location to volume
	 */
	private void addVolume(final String volName, final String volId,
			final String setId, final File volFile) {
		final String relativePath = FileUtils.getRelativePath(
				this.dataSetDirectory, volFile);
		this.volumes.add(new VolumeContainer(getUIManager().getLocaleUtils(),
				volName, volId, setId, relativePath,
				DataSetConstants.Status.INCOMPLETE, "Mark Johnson", //$NON-NLS-1$
				DataType.CATALOG, UUID.randomUUID()));
	}

	/**
	 * Get application version information
	 * 
	 * @return application version information
	 */
	public AppVersionInfo getAppVersionInfo() {
		return new AppVersionInfo();
	}

	/**
	 * Get dictionary version
	 * 
	 * @return dictionary version
	 */
	public String getDictionaryVersion() {
		return this.dictionary.getVersion();
	}
	
	/**
	 * Tests whether the client browser is Safari.
	 * 
	 * @return true, if the client is a Safari browser
	 */
	public boolean isSafari() {
		return request.getHeader("User-Agent").contains("Safari/");
	}

	@Override
	public void setServletRequest(HttpServletRequest req) {
		this.request = req;
	}

}
