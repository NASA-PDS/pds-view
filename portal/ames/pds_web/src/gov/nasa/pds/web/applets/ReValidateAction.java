package gov.nasa.pds.web.applets;

import gov.nasa.pds.web.ui.utils.DataSetValidator;

import java.applet.Applet;
import java.net.URL;

public class ReValidateAction extends ValidateAction {

	public ReValidateAction(final Applet applet, final String volPath) {
		super(applet, volPath);
	}

	@Override
	public Void doInBackground() {
		URL dictionaryURL = getClass().getResource(
				"/masterdd.full"); //$NON-NLS-1$
		// manually init dictionary since retrieval is different for applet
		DataSetValidator.initMasterDictionary(dictionaryURL);

		validateAndPost();
		return null;
	}

}
