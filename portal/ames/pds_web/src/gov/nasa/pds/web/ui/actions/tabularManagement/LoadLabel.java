package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.arc.pds.tools.util.URLUtils;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.dict.DictIdentifier;
import gov.nasa.pds.tools.dict.parser.DictIDFactory;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularLabelContainer;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.DataSetValidator;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataLoader;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;
import gov.nasa.pds.web.ui.utils.TabularPDS3DataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS4DataLoader;
import gov.nasa.pds.web.ui.utils.TabularPDS4DataUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;

/*
 * Validates labelURLString (passed in by struts as specified in struts.xml) is
 * a valid PDS label and checks for the existence of supported data objects in
 * the label. Reports errors on unsupported structures. Creates and stores
 * process for retrieval by next action.
 */

public class LoadLabel extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;
	private String labelURLString;
	private TabularDataLoader loader;
	
	private String pdsLabelType;
	private File xmlFile = null;
	private URL dataTableURL = null;
	private URL xmlFileURL = null;

	public void setLabelURLString(final String labelURLString) {
		this.labelURLString = labelURLString;
	}

	/*
	 * Creates a TabularDataProcess and stores the process id in order to pass
	 * it to the next action. Creates a TabularDataLoader (which creates a
	 * SliceContainer, calls TabularDataLoader.readSupportedObjects() and
	 * assigns it to the process. Stores the process for retrieval by next
	 * action.
	 * 
	 * @return string representing outcome of action (ie SUCCESS) for directing
	 * processing in struts.xml
	 */
	@Override
	protected String executeInner() throws Exception {
		// create process object to hold serialized data
		TabularDataProcess process = new TabularDataProcess();
		// set tabularDataProcess id to current process id
		setProcId(process.getID());

		String pdsLabelType;

		// call appropriate PDS3 or PDS4 loader
		if (this.pdsLabelType.equalsIgnoreCase("pds3")) {
			this.loader = new TabularPDS3DataLoader(this.labelURLString);
			pdsLabelType = "pds3";
		} else {
			this.loader = new TabularPDS4DataLoader(this.labelURLString,
					xmlFile, dataTableURL, xmlFileURL);
			pdsLabelType = "pds4";
		}

		SliceContainer slice = (this.loader.getSlice());

		// set label type to call appropriate loader
		slice.setPDSLabelType(pdsLabelType);

		// store slice in process
		process.setSlice(slice);
		// store process
		HTTPUtils.storeProcess(process);

		LogManager.logLabel(slice);

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// 
	}

	/*
	 * Validates labelFileUrl (passed in via struts.xml) exists, is well formed,
	 * is reachable, and points to a valid PDS label file. If label can be read,
	 * it reports error if no supported objects (TABLE, SERIES, SPECTRUM,
	 * SPREADSHEET) are found. Reads and saves object statements for insertion
	 * into sliceContainer in executeInner()
	 * 
	 * @see gov.nasa.pds.web.ui.actions.BaseSubmitAction#validateUserInput()
	 */
	@SuppressWarnings("nls")
	@Override
	protected void validateUserInput() {
		TabularLabelContainer labelContainer;
		Label label = null;
		URL labelUrl = null;

		// validate user has entered something in the label field
				if (this.labelURLString.length() == 0) {
					addError("loadLabel.error.missingLabelUrl");
				}
				// validate the URL is reachable, well formed and for a label file
				if (!this.hasErrors()) {
					try {
						// validate URL to PDS3 or PDS4 label is reachable and valid
						labelUrl = new URL(this.labelURLString);
						if (!URLUtils.exists(labelUrl)) {
							addError("loadLabel.error.urlUnreachable");
						}
					} catch (MalformedURLException URLException) {
						// URL not in correct format
						// TODO: nice-to-have smart handling if http:// not present
						addError("loadLabel.error.malformedLabelUrl");
					}

					// At this point it has been checked that the label
					// exists and that it is a valid, reachable URL
					if (!this.hasErrors()) {
						
						// check to see if it is a PDS4 label
						if (labelUrl.toString().toLowerCase().endsWith(".xml")) {

							try {
								this.pdsLabelType = "pds4";
								xmlFileURL = labelUrl;

								// creates temporary file in which to store the XML
								// set to null to use application's temporary directory
								// deletes file when the virtual machine terminates
								// Per meeting on 9-3-14, it is OK to save this in
								// temporary folder using File
								xmlFile = File.createTempFile("xml", null, null);
								FileUtils.copyURLToFile(labelUrl, xmlFile);

								// get FileAreaObservational in order to check to see if
								// the data file is reachable
								ObjectProvider objectAccess = new ObjectAccess();
								ProductObservational product = objectAccess.getProduct(
										xmlFile, ProductObservational.class);

								// build URL of data table file
								FileAreaObservational fileArea = product
										.getFileAreaObservationals().get(0);
								String fileName = fileArea.getFile().getFileName();
								File dataFile = new File(fileName);
								dataTableURL = TabularPDS4DataUtils
										.getURLofSameLevelFile(labelUrl, dataFile);
								
								// delete all the files created and ensure they are
								// deleted
								//dataFile.delete();
								dataFile.deleteOnExit();
								//xmlFile.delete();
								xmlFile.deleteOnExit();
								
								// If null, file does not exist or could not be found
								// within the same volume directory
								if (dataTableURL == null) {
									addError("loadLabel.error.structureNotFound",
											fileName);
								}

								//caused by File.delete
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// PDS3 label
						} else if (labelUrl.toString().toLowerCase().endsWith(".lbl")) {

							this.pdsLabelType = "pds3";

							labelContainer = new TabularLabelContainer(labelUrl, null,
									DataSetValidator.getMasterDictionary());
							if (labelContainer.isValid()) {
								label = labelContainer.getLabelObj();
							} else {
								addError(labelContainer.getProblems().get(0)
										.getMessage());
							}

							if (!this.hasErrors() && label != null) {

								// Get all pointers from the label or any detached files
								List<PointerStatement> allPointers = labelContainer
										.getPointers();

								// if no tabular pointers found or any other embedded
								// pointers, then
								// no supported objects could be found
								if (allPointers.size() == 0) {
									addError("loadLabel.error.noSupportedObjectsFound");
								}

								// tabular pointers or other embedded pointers exist
								if (!this.hasErrors()) {

									for (PointerStatement pointer : allPointers) {

										// Check to see if the file exists (URLs are
										// reachable)
										try {
											// read label fragment
											URL fileURL = labelContainer.getFirstURI(
													pointer).toURL();
											URL fileFoundURI = TabularDataUtils
													.findStructureFile(fileURL);

											// If null, file does not exist or could not
											// be found within the same volume directory
											if (fileFoundURI == null) {
												addError(
														"loadLabel.error.structureNotFound",
														fileURL.toString());
											}

										} catch (Exception e) {
											e.printStackTrace();
											throw new RuntimeException(
													"unable to parse STRUCTURE pointer" //$NON-NLS-1$
															+ e.getMessage());
										}

										// indicate that you're looking for object with
										// same name
										DictIdentifier objId = DictIDFactory
												.createObjectDefId(pointer
														.getIdentifier().toString());

										// Get top level object statements (e.g., TABLE
										// pointers)
										for (ObjectStatement objectStatement : labelContainer
												.getAllObjects(objId)) {

											if (testForSamplingParameterNA(objectStatement) == true) {
												addError("loadLabel.error.samplingNotSupported");
											}
											// get pointers for this object statement
											final List<PointerStatement> innerPointers = objectStatement
													.getPointers();

											// loop through
											for (final PointerStatement secondLevelPointer : innerPointers) {
												// find structure pointers
												if (secondLevelPointer.getIdentifier()
														.toString()
														.equalsIgnoreCase("STRUCTURE")) { //$NON-NLS-1$

													// assuming one file
													// FileReference file =
													// secondLevelPointer.getFileRefs().get(0);
													DefaultLabelParser parser = new DefaultLabelParser(
															new ManualPathResolver());

													try {
														// read label fragment
														URL fragmentURL2 = labelContainer
																.getFirstURI(
																		secondLevelPointer)
																.toURL();
														fragmentURL2 = TabularDataUtils
																.findStructureFile(fragmentURL2);

														if (fragmentURL2 == null) {
															addError(
																	"loadLabel.error.structureNotFound",
																	secondLevelPointer
																			.getValue()
																			.toString());
														} else {
															// Parse and check for label
															// parsing exceptions
															parser.parsePartial(
																	fragmentURL2, label);
														}

													} catch (LabelParserException e) {
														// ?
														e.printStackTrace();
														throw new RuntimeException(
																"unable to parse STRUCTURE pointer" //$NON-NLS-1$
																		+ e.getMessage());

													} catch (IOException e) {
														addError(
																"loadLabel.error.structureNotFound",
																this.labelURLString);
														// e.printStackTrace();
														// throw new RuntimeException(
														// "IO Exception while retrieving STRUCTURE pointer" //$NON-NLS-1$
														// + e.getMessage());

													}// end catch
												}// end if pointer id = structure
											}// end for loop on inner pointers
										}// end for loop on all objects named same as
											// tabular pointer
									}// end for loop (PointerStatement pointer :
										// allPointers)
								}// end if (!this.hasErrors())
							}// end if (!this.hasErrors() && label != null)
						} else {
							this.pdsLabelType = "";
							addError("loadLabel.error.urlUnreachable");
						}
					}// if (!this.hasErrors()) {

				}// if (!this.hasErrors()) {
	}

	@SuppressWarnings("nls")
	private boolean testForSamplingParameterNA(ObjectStatement objectStatement) {

		if (objectStatement.getAttribute("SAMPLING_PARAMETER_INTERVAL") != null
				&& objectStatement.getAttribute("SAMPLING_PARAMETER_INTERVAL")
						.getValue().toString().equalsIgnoreCase("N/A"))
			return true;
		return false;

	}

}
