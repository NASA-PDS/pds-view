package gov.nasa.pds.web.applets;

import gov.nasa.arc.pds.tools.util.StrUtils;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Preview a given PDS archive file from a volume being validated.
 * 
 * @author jagander
 */
public class PreviewAction extends QueueAction {

	/**
	 * Path to file relative to the volume root
	 */
	private final String relPath;

	/**
	 * Path to volume root
	 */
	private final String volPath;

	/**
	 * Unique process id
	 */
	private final String procId;

	/**
	 * Line number to scroll to. Used to navigate to the line of a specific
	 * problem within a file.
	 */
	private String lineNumber;

	/**
	 * Constructor for preview action
	 * 
	 * @param applet
	 *            launching applet to interact with local file system
	 * @param relPath
	 *            relative path from volume root to target file
	 * @param volPath
	 *            path to volume root on local file system
	 * @param procId
	 *            unique process id
	 * @param lineNumber
	 *            line number to scroll to when displaying the preview
	 */
	public PreviewAction(final Applet applet, final String relPath,
			final String volPath, final String procId, String lineNumber) {
		super(applet);
		this.relPath = relPath;
		this.volPath = volPath;
		this.procId = procId;
		this.lineNumber = lineNumber;
	}

	/**
	 * Main method of the action. Finds file for preview, posts it to the server
	 * in a temporary cache, redirects to the page that displays the preview.
	 */
	@SuppressWarnings("nls")
	@Override
	public Void doInBackground() {
		// generate the post URL using the applet's location
		final URL docBase = getApplet().getDocumentBase();
		String docBaseString = docBase.toString();
		final String urlBase = StrUtils.getURLBase(docBaseString);
		String pathEncoded = null;
		try {
			pathEncoded = URLEncoder.encode(relPath, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// ignore - Java SE always has UTF-8
		}
		
		final String postAction = urlBase + "PostFilePreview.action?" //$NON-NLS-1$
				+ "node=" + pathEncoded //$NON-NLS-1$
				+ "&procId=" + this.procId; //$NON-NLS-1$
		System.out.println("Sending preview file to: " + postAction);

		// get the file for preview
		final File file = new File(new File(this.volPath), this.relPath);

		try {
			// post contents
			final FileInputStream is = new FileInputStream(file);

			final URL target = new URL(postAction);
			HttpURLConnection urlConnection = (HttpURLConnection) target
					.openConnection();
//			urlConnection.setRequestMethod("POST"); //$NON-NLS-1$
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setDefaultUseCaches(false);
			urlConnection.setRequestProperty("Content-Type", //$NON-NLS-1$
					"application/octet-stream"); //$NON-NLS-1$

			OutputStream out = urlConnection.getOutputStream();

			// TODO: is this the right size?
			byte[] buffer = new byte[4096];
			int len;
			while ((len = is.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
			is.close();
			out.close();

			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Did not get an OK from server: "
						+ urlConnection.getResponseCode());
			} else {

				// send to page to show results
				String resultsURLString = urlBase
						+ "PreviewFile.action?node=" + pathEncoded //$NON-NLS-1$
						+ "&procId=" + this.procId; //$NON-NLS-1$
				if (this.lineNumber != null) {
					resultsURLString += "#line" + this.lineNumber; //$NON-NLS-1$
				}
				final URL resultsURL = new URL(resultsURLString);
				System.out.println("Got OK from server, redirecting to "
						+ resultsURL.toString());

				setResultURL(resultsURL.toString());
			}
		} catch (final Exception e) {
			// TODO: split up catches and determine how to handle
		}
		
		return null;
	}

	/**
	 * Cancel is not implemented as this is not a cancelable action.
	 */
	public void cancel() {
		throw new RuntimeException("not implemented"); //$NON-NLS-1$
	}

	/**
	 * Get status for action. In this case, due to brevity of the process, OK is
	 * just hard coded in.
	 */
	public String status() {
		return "OK"; //$NON-NLS-1$
	}
}
