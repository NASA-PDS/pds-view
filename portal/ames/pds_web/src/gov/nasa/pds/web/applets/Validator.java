package gov.nasa.pds.web.applets;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;

import netscape.javascript.JSObject;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.factories.Forms;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * The validation applet. This encompases all validation actions that occur
 * through an applet. Currently this includes validation, re-validation, preview
 * and associated sub actions such as cancel and status checking.
 * 
 * @author jagander
 */
// TODO: when implement queuing, have cancel pull from this.actions if there or
// do action's cancel
// TODO: send back an action ID to use with the cancel function
// TODO: disable the validate local button until canceled
// TODO: embed logging props file and change all system outs to logging
@SuppressWarnings("nls")
public class Validator extends Applet implements PropertyChangeListener {

	private static final long serialVersionUID = -344318964099526638L;

	private JButton validateButton;
	
	private JButton cancelButton;

	private JLabel volumeName;
	private JLabel problemCount;
	private JLabel message;
	
	private Component statusPane;
	
	private QueueAction currentAction = null;
	
	/**
	 * Method that initializes the applet. Currently this includes starting the
	 * user event thread and getting the window instance.
	 */
	@Override
	public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeLater(new Runnable() {
        		@Override
                public void run() {
        			createGUI(Validator.this);
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
	}
	
	private void createGUI(Container parent) {
		volumeName = new JLabel();
		problemCount = new JLabel("0");
    	message = new JLabel();
    	
    	if (getParameter("volumePath") == null) {
	    	validateButton = new JButton("Validate Volume...");
	    	validateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateLocal();
				}
			});
    	} else {
    		validateButton = new JButton("Revalidate");
    		validateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					reValidate(getParameter("volumePath"));
				}
    		});
    	}
    	
    	cancelButton = new JButton("Cancel");
    	cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
    	
		FormLayout layout = new FormLayout(
				"right:90px, 10px, 300px",
				""
		);
		DefaultFormBuilder statusBuilder = new DefaultFormBuilder(layout);

		statusBuilder.append("Volume:", volumeName);
		statusBuilder.nextLine();
		statusBuilder.append("Error count:", problemCount);
		statusBuilder.nextLine();
		statusBuilder.append("Status:", message);
		statusBuilder.nextLine();

		statusPane = statusBuilder.getContainer();
		parent.add(statusPane, BorderLayout.CENTER);
		
		FormLayout mainLayout = new FormLayout("left:pref", "pref, pref");
		DefaultFormBuilder mainBuilder = new DefaultFormBuilder(mainLayout);
		
		mainBuilder.add(Forms.buttonBar(validateButton, cancelButton), CC.rc(1, 1, CellConstraints.DEFAULT, CellConstraints.CENTER));
		mainBuilder.add(statusPane, CC.rc(2, 1));
		
		parent.add(mainBuilder.getPanel());

    	cancelButton.setEnabled(false);
    	statusPane.setVisible(false);
	}

	/**
	 * Validate a local (on the user's machine) volume.
	 */
	public void validateLocal() {
		
		// debug info to be found in a java console dump
		System.out.println("Doing local validation.");

		// generate the file chooser
		JFileChooser fileChooser = getJFileChooser();

		// open the file chooser
		final int returnVal = fileChooser.showOpenDialog(this);

		// if user does not cancel file chooser, do validation
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			doValidateAction(new ValidateAction(this, fileChooser.getSelectedFile().getAbsolutePath()));
		}
	}

	/**
	 * Preview a file from a given validation
	 * 
	 * @param relPath
	 *            path to file relative to the volume root
	 * @param volPath
	 *            path to volume on the user's file system
	 * @param procId
	 *            process id for the validation
	 */
	public void previewFile(final String relPath, final String volPath,
			final String procId) {
		this.previewFile(relPath, volPath, procId, null);
	}

	/**
	 * Preview a file from a given validation and scroll to a given line number.
	 * 
	 * @param relPath
	 *            path to file relative to the volume root
	 * @param volPath
	 *            path to volume on the user's file system
	 * @param procId
	 *            process id for the validation
	 * @param lineNumber
	 *            the line number to scroll to
	 */
	public void previewFile(final String relPath, final String volPath,
			final String procId, final String lineNumber) {

		// debug info to appear in the java console
		System.out.println("Doing file preview of file '" + volPath
				+ File.separator + relPath + "'. Proc ID is " + procId + ".");

		// create the preview action
		final PreviewAction action = new PreviewAction(this, relPath, volPath,
				procId, lineNumber);

		// add action to action queue
		addAction(action);
	}

	/**
	 * Re-validate a given action
	 * 
	 * @param volPath
	 *            the path to the volume on the file system
	 */
	public void reValidate(final String volPath) {
		// debug info to appear in the java console
		System.out.println("Doing local re-validation");

		// create the re-validation action
		doValidateAction(new ReValidateAction(this, volPath));
	}
	
	private void doValidateAction(QueueAction action) {
		validateButton.setEnabled(false);
		cancelButton.setEnabled(true);
		statusPane.setVisible(true);
		
		action.addPropertyChangeListener(this);

		// add action to action queue
		addAction(action);
	}

	/**
	 * Add a given action to the action queue
	 * 
	 * @param action
	 *            the action to add to the queue
	 */
	private void addAction(final QueueAction action) {
		currentAction = action;
		action.execute();
	}

	/**
	 * Send action id to page. This is used for all interaction between the page
	 * and applet. For instance, user initiated cancellation or status polling.
	 * This method must be called on the event dispatch thread.
	 * 
	 * @param actionId
	 *            the id to associate with the page
	 */
	public void sendActionId(final String actionId) {
		JSObject window = JSObject.getWindow(Validator.this);
		window.call("setAppletActionId", (Object[]) new String[] { actionId });			
	}

	/**
	 * Cancel the action. This is always invoked on the event dispatch thread.
	 */
	public void cancel() {
		System.out.println("Cancelling current action");
		currentAction.cancel(true);
		currentAction = null;
		validateButton.setEnabled(true);
		cancelButton.setEnabled(false);
		
		// clear the status on the page
		System.out.println("updating page to reflect cancel");
		JSObject window = JSObject.getWindow(Validator.this);
		window.call("clearStatus", (Object[]) null);
	}

	/**
	 * Gets a file chooser dialog.
	 * 
	 * @return a new file chooser dialog
	 */
	private JFileChooser getJFileChooser() {

		final JFileChooser fileChooser = new JFileChooser() {
			private static final long serialVersionUID = 1541813407103968847L;

			@Override
			public void updateUI() {
				// fix for windows issue reading into zip folders
				putClientProperty("FileChooser.useShellFolder", Boolean.FALSE); //$NON-NLS-1$
				// remove new folder icon
				putClientProperty("FileChooser.readOnly", Boolean.TRUE); //$NON-NLS-1$

				super.updateUI();
			}
		};

		// do not accept all
		fileChooser.setAcceptAllFileFilterUsed(false);

		// only allow choosing directories, only folders can be volumes
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// disallow multiple select, only one volume can be validated at a time
		fileChooser.setMultiSelectionEnabled(false);

		// set approve button text to appropriate label for validation
		fileChooser.setApproveButtonText("Validate Volume");

		return fileChooser;
	}

	// Note: Always called on the event dispatch thread!
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
//		JSObject win = JSObject.getWindow(Validator.this);

		if (event.getPropertyName().equals(ActionProperty.STATUS.toString())) {
//			win.call("updateStatus", new Object[] { event.getNewValue() }); //$NON-NLS-1$
			message.setText(event.getNewValue().toString());
		} else if (event.getPropertyName().equals(ActionProperty.VOLUME_TO_VALIDATE.toString())) {
//			win.call("setVolume", new Object[] { event.getNewValue() }); //$NON-NLS-1$
			volumeName.setText(event.getNewValue().toString());
		} else if (event.getPropertyName().equals(ActionProperty.PROBLEM_COUNT.toString())) {
			problemCount.setText(event.getNewValue().toString());
		} else if (event.getPropertyName().equals("state")) {
			switch ((StateValue) event.getNewValue()) {
			case PENDING:
//				win.call("updateStatus", new Object[] { "Starting validation..." }); //$NON-NLS-1$
				message.setText("Starting validation...");
				break;
			case STARTED:
//				win.call("updateStatus", new Object[] { "Validation in progress..." }); //$NON-NLS-1$
				message.setText("Starting validation...");
				break;
			default: // DONE
				if (currentAction!=null && currentAction.isCancelled()) {
//					win.call("updateStatus", new Object[] { "Validation canceled." }); //$NON-NLS-1$
					message.setText("Validation canceled.");
				} else {
//					win.call("updateStatus", new Object[] { "Validation complete." }); //$NON-NLS-1$
					message.setText("Validation complete.");
				}
				break;
			}
		} else {
			System.err.println("Unknown property changed: " + event.getPropertyName()
					+ " old=" + event.getOldValue() + " new=" + event.getNewValue());
		}
	}
	
}
