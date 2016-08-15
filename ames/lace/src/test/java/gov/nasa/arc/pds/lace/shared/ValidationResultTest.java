package gov.nasa.arc.pds.lace.shared;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.testng.annotations.Test;

public class ValidationResultTest {

	private static final String PATTERN = ".*\\$\\{.*\\}.*";

	private static final String ID_WITH_MSG = "id1";

	private static final String ID_WITHOUT_MSG = "id2";

	@Test
	public void testVariablePattern() {
		ValidationResult result = new ValidationResult();
		assertNull(result.getVariablePattern());

		result.setVariablePattern(PATTERN);
		assertEquals(result.getVariablePattern(), PATTERN);
	}

	@Test
	public void testValidationMessages() {
		ValidationResult result = new ValidationResult();

		assertNull(result.getMessages(ID_WITH_MSG));
		assertNull(result.getMessages(ID_WITHOUT_MSG));

		result.addMessage(ID_WITH_MSG, "the message", "attrName", "the value");
		assertNull(result.getMessages(ID_WITHOUT_MSG));
		List<ValidationMessage> messages = result.getMessages(ID_WITH_MSG);

		assertNotNull(messages);
		assertEquals(messages.size(), 1);
		ValidationMessage msg = messages.get(0);

		assertEquals(msg.getMessage(), "the message");
		assertEquals(msg.getAttributeName(), "attrName");
		assertEquals(msg.getValue(), "the value");
	}

}
