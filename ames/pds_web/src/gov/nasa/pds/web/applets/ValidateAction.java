package gov.nasa.pds.web.applets;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.containers.StatusContainer;
import gov.nasa.pds.web.ui.containers.dataSet.Bucket;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.utils.DataSetValidator;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Action that does the validation of a PDS volume. This is the primary entry
 * point to validation, though volumes hosted on the same machine have a more
 * direct entry point (primarily used during development or rare cases of user
 * installations).
 */
@SuppressWarnings("nls")
public class ValidateAction extends QueueAction implements Observer {

	/**
	 * Status container to hold current state of the validation process
	 */
	protected final StatusContainer status = new StatusContainer();

	/** The last directory navigated to by the user. */
	protected static File lastDirectory = null;

	/**
	 * Volume to validate
	 */
	protected File vol;

	/**
	 * Process id
	 */
	protected String procId;

	/**
	 * Base location of the web app
	 */
	protected String urlBase;
	
	/**
	 * The count of total problems encountered.
	 */
	private int problemCount = 0;

	/**
	 * Constructor for revalidation process where volume path and proc id are
	 * already set
	 */
	protected ValidateAction(final Applet applet, final String volPath) {
		super(applet);

		// set url base
		final URL docBase = applet.getDocumentBase();
		String docBaseString = docBase.toString();
		this.urlBase = StrUtils.getURLBase(docBaseString);

		// assign a new proc id if none explicitly set
		this.procId = UUID.randomUUID().toString();

		// only set vol if override provided, normal path set using file chooser
		if (volPath != null) {
			this.vol = new File(volPath);
		}
	}

	/**
	 * Main method of the action, encompasses the validation process
	 */
	@Override
	protected Void doInBackground() {
		problemCount = 0;
		
		System.out.println("In ValidateAction.run().");
		try {
			// TODO: make sure you can find a volume here, fail and re-ask
			// if not a volume

			// get location of the master dictionary
			URL dictionaryURL = getClass().getResource("/masterdd.full"); //$NON-NLS-1$

			// manually init dictionary since retrieval is different for
			// applet
			System.out.println("About to initialize PDS data dictionary.");
			DataSetValidator.initMasterDictionary(dictionaryURL);
			System.out.println("Finished initializing PDS data dictionary.");

			// do validation and post results to db and cache directory
			validateAndPost();
		} catch (Exception e) {
			setMessage("Exception: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Post a postable object to server. Currently this is either a bucket of
	 * problems or a results container
	 * 
	 * @param location
	 *            the location to post to
	 * @param postable
	 *            an object able to be posted to the server
	 */
	private static HttpURLConnection post(final String location,
			final Object postable) throws IOException {
		final URL target = new URL(location);
		HttpURLConnection urlConnection = (HttpURLConnection) target
				.openConnection();
		urlConnection.setRequestMethod("POST"); //$NON-NLS-1$
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setDefaultUseCaches(false);
		urlConnection.setRequestProperty("Content-Type", //$NON-NLS-1$
				"application/octet-stream"); //$NON-NLS-1$

		OutputStream out = urlConnection.getOutputStream();

		// create an output stream
		ObjectOutputStream oos = new ObjectOutputStream(out);

		// write the serialized data object to the output stream
		oos.writeObject(postable);

		// possible to write multiple times and stuff other objects in
		oos.flush();
		oos.close();

		return urlConnection;
	}

	/**
	 * Do validation and post data
	 */
	public void validateAndPost() {
		System.out.println("In validateAndPost().");

		try {

			// tell the page what you're validating
			setVolume();

			// create a validator instance
			DataSetValidator validator = new DataSetValidator(this.procId,
					this.vol, this.status);
			validator.addObserver(this);

			// do validation
			System.out.println("About to validate.");
			validator.validate();
			System.out.println("Finished validation.");

			// get results from validator
			ValidationResults results = validator.getResults();

			final String postAction = this.urlBase
					+ "PostValidation.action?procId=" + this.procId; //$NON-NLS-1$

			System.out.println("Posting results.");
			HttpURLConnection urlConnection = post(postAction, results);
			System.out.println("Finished posting results.");

			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Got error response:" + urlConnection.getResponseCode() + ".");
				setMessage("Did not get an OK from server when attempting to post results: "
						+ urlConnection.getResponseCode());
			} else {
				System.out.println("Got OK from server, redirect now");

				// send to page to show results
				final URL resultsURL = new URL(this.urlBase
						+ "ShowResults.action?procId=" + this.procId); //$NON-NLS-1$
				setMessage("Validation complete, attempting to show results. If not forwarded, make sure popups are enabled. Click <b><a href=\""
						+ resultsURL.toString()
						+ "\">here</a></b> to manually show results.");
				redirect(resultsURL);
			}
		} catch (final CancelledException e) {
			// update the page to clear the message and re-enable button
			setMessage("Validation Cancelled!");
		} catch (final Exception e) {
			String message = "Failed to validate and send results. Exception message: "
					+ e.getMessage();
			setMessage(message);
			System.out.println(message);
			e.printStackTrace();
		}
	}

	/**
	 * Post a bucket of problems to server (to db), chunked due to the large
	 * number of problems that may be generated.
	 */
	private void postProblems(final Bucket bucket) {

		// NOTE: only get here when bucket up in queue

		// get url for post location
		final String postAction = this.urlBase
				+ "PostValidationBucket.action?procId=" + this.procId; //$NON-NLS-1$
		try {
			// do the actual post
			final HttpURLConnection urlConnection = post(postAction, bucket);

			// check result of post
			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				setMessage("Did not get an OK from server when attempting to post problems: "
						+ urlConnection.getResponseCode());
			} else {
				setMessage("Sending problems to server.");
			}
		} catch (final Exception e) {
			String message = "Failed to post problems. Exception message: "
					+ e.getMessage();
			setMessage(message);
			System.out.println(message);
			e.printStackTrace();
		}
	}

	/**
	 * Generic update to validation process, either updates message or posts a
	 * bucket
	 * 
	 * @param validator
	 *            validator action
	 * @param object
	 *            object to use in update
	 */
	@Override
	public void update(Observable validator, Object object) {
		if (object instanceof Bucket) {
			Bucket bucket = (Bucket) object;
			postProblems(bucket);
			if (bucket.getProblemCount() > 0) {
				int oldProblemCount = problemCount;
				problemCount += ((Bucket) object).getProblemCount();
				firePropertyChange(ActionProperty.PROBLEM_COUNT.toString(), oldProblemCount, problemCount);				
			}
		} else {
			String message = this.status.getMessageKey() + " (step "
					+ this.status.getStep() + ")";
//			if (this.status.isMajor()) {
				setMessage(message);
				this.status.seen();
//			}
		}
	}

	/**
	 * Set a message to user.
	 * 
	 * @param message
	 *            message to send to user
	 */
	public void setMessage(final String message) {
		firePropertyChange(ActionProperty.STATUS.toString(), null, message);
	}

	/**
	 * Set the volume name for display in the page
	 */
	public void setVolume() {
		firePropertyChange(ActionProperty.VOLUME_TO_VALIDATE.toString(), null, this.vol.toString());
	}

	/**
	 * Redirect to a given location. Depends on a javascript function to do the
	 * redirect. Using this instead of redirect initiated directly due to issues
	 * on some red hat systems.
	 */
	public void redirect(final URL location) {
		System.out.println("Redirecting to '" + location.toString() + "'.");
		setResultURL(location.toString());
	}
	
}
