package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.MultiPartEmail;

/**
 * Action for sending an error report to the appropriate dev parties.
 * 
 * THIS IS NOT WIRED TO ANYTHING. Due to issues with getting mail sending
 * working on production, this is not currently being used. Should it be
 * re-connected, the from and to addresses will need to be modified below.
 */
// TODO: get more info about error like action, submit params if any, browser
// info, IP, etc
public class SendError extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	private String email = ""; //$NON-NLS-1$

	private String name = ""; //$NON-NLS-1$

	private String circumstances = ""; //$NON-NLS-1$

	private String stack = ""; //$NON-NLS-1$

	private MultiPartEmail emailObj = new MultiPartEmail();

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setCircumstances(final String circumstances) {
		this.circumstances = circumstances;
	}

	public String getCircumstances() {
		return this.circumstances;
	}

	public void setStack(final String stack) {
		this.stack = stack;
	}

	protected MultiPartEmail getEmailObj() {
		return this.emailObj;
	}

	@SuppressWarnings("nls")
	@Override
	public String executeInner() throws Exception {

		// assemble message
		String messageBody = ""; //$NON-NLS-1$
		messageBody += "Reported By: " + this.name + "\n";
		messageBody += "Reply To: " + this.email + "\n\n";
		messageBody += this.circumstances;

		// Setup mail server
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.jpl.nasa.gov");

		// Get a mail session
		@SuppressWarnings("hiding")
		Session session = Session.getDefaultInstance(props, null);

		// Define a new mail message
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("pds_operator@jpl.nasa.gov"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				"pds_operator@jpl.nasa.gov"));
		message.setSubject("UCD Tools Bug Report");

		// Create a message part to represent the body text
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(messageBody);

		// use a MimeMultipart as we need to handle the file attachments
		Multipart multipart = new MimeMultipart();

		// add the message body to the mime message
		multipart.addBodyPart(messageBodyPart);

		// add any file attachments to the message
		addAtachments(new String[] { this.stack }, multipart);

		// Put all message parts in the message
		message.setContent(multipart);

		// Send the message
		Transport.send(message);

		// add notice that email sent
		addNotice("submitError.notice.messageSent"); //$NON-NLS-1$

		return SUCCESS;
	}

	protected void addAtachments(String[] attachments, Multipart multipart)
			throws MessagingException, AddressException {
		for (int i = 0; i <= attachments.length - 1; i++) {
			String filename = attachments[i];
			MimeBodyPart attachmentBodyPart = new MimeBodyPart();

			// use a JAF FileDataSource as it does MIME type detection
			DataSource source = new FileDataSource(filename);
			attachmentBodyPart.setDataHandler(new DataHandler(source));

			// assume that the filename you want to send is the same as the
			// actual file name - could alter this to remove the file path
			attachmentBodyPart.setFileName(filename);

			// add the attachment
			multipart.addBodyPart(attachmentBodyPart);
		}
	}

	@Override
	protected void pushBackUserInput() {
		// TODO: push back the values entered IF forwarding to new page

	}

	@Override
	protected void validateUserInput() {
		if (StrUtils.nullOrEmpty(this.circumstances)) {
			addError("error.requiredstring", //$NON-NLS-1$
					getUIManager().getTxt("error.label.circumstances")); //$NON-NLS-1$
		}

		if (StrUtils.nullOrEmpty(this.email)) {
			addError(
					"error.requiredstring", getUIManager().getTxt("error.label.email")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// TODO: verify that is a valid email

		if (StrUtils.nullOrEmpty(this.name)) {
			addError(
					"error.requiredstring", getUIManager().getTxt("error.label.name")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public String getExceptionStackString() {
		return this.stack;
	}
}