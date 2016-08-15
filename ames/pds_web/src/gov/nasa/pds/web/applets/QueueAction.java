package gov.nasa.pds.web.applets;

import java.applet.Applet;

import javax.swing.SwingWorker;

import netscape.javascript.JSObject;

public abstract class QueueAction extends SwingWorker<Void,Void> {

	/** The applet in which the action is running. */
	private Applet applet;
	
	/** The URL we should redirect to after the action completes. */
	private String resultURL = null;
	
	protected QueueAction(Applet applet) {
		this.applet = applet;
	}

	// This is always called on the event dispatch thread!
	@Override
	protected void done() {
		if (resultURL != null) {
			JSObject win = JSObject.getWindow(applet);
			win.call("redirect", (Object[]) new String[] { resultURL }); //$NON-NLS-1$
		}
	}

	/**
	 * Sets the URL which should be redirected to once the action is complete.
	 * 
	 * @param url the redirect URL
	 */
	protected void setResultURL(String url) {
		resultURL = url;
	}

	/**
	 * Gets the applet object in which the task is running.
	 * 
	 * @return the applet object
	 */
	protected Applet getApplet() {
		return applet;
	}
	
}
