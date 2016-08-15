package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.tools.dict.DictIdentifier;
import gov.nasa.pds.tools.dict.parser.DictIDFactory;
import gov.nasa.pds.web.applets.CancelledException;
import gov.nasa.pds.web.ui.actions.BaseTestAction;

import java.io.File;

@SuppressWarnings("nls")
public class DataDictionarySupportTest extends BaseTestAction {

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub

	}

	private File getDataSetVolumePath() {
		return new File(getTestDataDirectory(), "volumes");//$NON-NLS-1$ 
	}

	public void testMultipleDDSupported() throws CancelledException {
		DataSetValidator validator = new DataSetValidator("1", new File(
				getDataSetVolumePath(), "multipleDD"));
		validator.validate();
		DictIdentifier valid1 = DictIDFactory.createElementDefId("FOO");
		DictIdentifier valid2 = DictIDFactory.createElementDefId("BAR");
		DictIdentifier invalid = DictIDFactory.createElementDefId("BANG");
		assertTrue(validator.dictionary.containsDefinition(valid1));
		assertTrue(validator.dictionary.containsDefinition(valid2));
		assertFalse(validator.dictionary.containsDefinition(invalid));
	}

	public void testBadDDName() throws CancelledException {
		DataSetValidator validator = new DataSetValidator("1", new File(
				getDataSetVolumePath(), "multipleDD"));
		validator.validate();
		DictIdentifier invalid = DictIDFactory.createElementDefId("BANG");
		assertFalse(validator.dictionary.containsDefinition(invalid));

	}

}
